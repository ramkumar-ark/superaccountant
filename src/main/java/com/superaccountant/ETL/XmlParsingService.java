package com.superaccountant.ETL;

import com.superaccountant.ETL.model.Body;
import com.superaccountant.ETL.model.ImportData;
import com.superaccountant.ETL.model.RequestData;
import com.superaccountant.ETL.model.Envelope;
import com.superaccountant.ETL.model.TallyMessage;
import com.superaccountant.ETL.model.GroupMasterXml;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class XmlParsingService {

    private static final Logger log = LoggerFactory.getLogger(XmlParsingService.class);

    private final VoucherRepository voucherRepository;
    private final VoucherMapper voucherMapper;
    private final LedgerMasterRepository ledgerMasterRepository;
    private final LedgerMasterMapper ledgerMasterMapper;
    private final GroupMasterRepository groupMasterRepository;
    private final VoucherTypeMasterRepository voucherTypeMasterRepository;

    private final TallyXmlDataRepository tallyXmlDataRepository;
    private final JAXBContext jaxbContext;
    private final CompanyRepository companyRepository;
    private final XmlSanitizer xmlSanitizer;

    @Autowired
    public XmlParsingService(VoucherRepository voucherRepository,
            VoucherMapper voucherMapper,
            LedgerMasterRepository ledgerMasterRepository,
            LedgerMasterMapper ledgerMasterMapper,
            GroupMasterRepository groupMasterRepository,
            VoucherTypeMasterRepository voucherTypeMasterRepository,
            TallyXmlDataRepository tallyXmlDataRepository,
            JAXBContext jaxbContext,
            XmlSanitizer xmlSanitizer,
            CompanyRepository companyRepository) {
        this.voucherRepository = voucherRepository;
        this.voucherMapper = voucherMapper;
        this.ledgerMasterRepository = ledgerMasterRepository;
        this.ledgerMasterMapper = ledgerMasterMapper;
        this.groupMasterRepository = groupMasterRepository;
        this.voucherTypeMasterRepository = voucherTypeMasterRepository;
        this.tallyXmlDataRepository = tallyXmlDataRepository;
        this.jaxbContext = jaxbContext;
        this.xmlSanitizer = xmlSanitizer;
        this.companyRepository = companyRepository;
    }

    @Transactional
    public void parseAndSave(MultipartFile mastersFile, MultipartFile transactionsFile, String companyName)
            throws JAXBException, IOException {
        Company company = companyRepository.findByName(companyName)
                .orElseGet(() -> {
                    Company newCompany = new Company();
                    newCompany.setName(companyName);
                    return companyRepository.save(newCompany);
                });

        // Delete existing data for this company before processing the new files.
        voucherRepository.deleteAllByCompany(company);
        ledgerMasterRepository.deleteAllByCompany(company);
        groupMasterRepository.deleteAllByCompany(company);
        voucherTypeMasterRepository.deleteAllByCompany(company);

        // 1. Process and save Masters
        String mastersContent = xmlSanitizer.sanitize(mastersFile.getBytes(), mastersFile.getOriginalFilename());
        tallyXmlDataRepository.save(
                new TallyXmlData(mastersFile.getOriginalFilename(), mastersContent, company, XmlFileType.MASTERS));
        processMastersFromXml(mastersContent, company);
        log.info("Successfully processed masters file: {}", mastersFile.getOriginalFilename());

        // 2. Process and save Transactions
        String transactionsContent = xmlSanitizer.sanitize(transactionsFile.getBytes(),
                transactionsFile.getOriginalFilename());
        tallyXmlDataRepository.save(new TallyXmlData(transactionsFile.getOriginalFilename(), transactionsContent,
                company, XmlFileType.TRANSACTIONS));
        processVouchersFromXml(transactionsContent, company);
        log.info("Successfully processed transactions file: {}", transactionsFile.getOriginalFilename());
    }

    @Transactional
    public void reprocessXml(Long xmlDataId) throws JAXBException {
        TallyXmlData stagedData = tallyXmlDataRepository.findById(xmlDataId)
                .orElseThrow(() -> new ResourceNotFoundException("Staged XML data not found with id: " + xmlDataId));

        log.info("Reprocessing staged XML data with id: {}", xmlDataId);
        Company company = Optional.ofNullable(stagedData.getCompany())
                .orElseThrow(() -> new IllegalStateException("Staged XML data " + xmlDataId + " has no company."));

        voucherRepository.deleteAllByCompany(company);
        processVouchersFromXml(stagedData.getXmlContent(), company);
    }

    private void processMastersFromXml(String content, Company company) throws JAXBException {
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Envelope envelope = (Envelope) jaxbUnmarshaller.unmarshal(new StringReader(content));

        List<TallyMessage> tallyMessages = Optional.ofNullable(envelope)
                .map(Envelope::getBody)
                .map(Body::getImportData)
                .map(ImportData::getRequestData)
                .map(RequestData::getTallyMessages)
                .orElse(Collections.emptyList());

        // --- Group Processing (2 passes to build hierarchy) ---
        log.info("Processing Group masters...");
        List<GroupMasterXml> groupXmls = tallyMessages.stream()
                .map(TallyMessage::getGroup)
                .filter(java.util.Objects::nonNull)
                .toList();

        // Pass 1: Create all groups without parent links and store in a map
        java.util.Map<String, GroupMaster> groupMap = new HashMap<>();
        for (GroupMasterXml groupXml : groupXmls) {
            GroupMaster group = new GroupMaster();
            group.setName(groupXml.getName());
            group.setCompany(company);
            groupMap.put(group.getName(), group);
        }

        // Pass 2: Set parent links using the map
        for (GroupMasterXml groupXml : groupXmls) {
            if (groupXml.getParent() != null && !groupXml.getParent().isBlank()) {
                GroupMaster child = groupMap.get(groupXml.getName());
                GroupMaster parent = groupMap.get(groupXml.getParent());
                if (child != null && parent != null) {
                    child.setParent(parent);
                }
            }
        }
        groupMasterRepository.saveAll(groupMap.values());
        log.info("Saved {} Group masters.", groupMap.size());

        // --- Voucher Type Processing ---
        log.info("Processing Voucher Type masters...");
        List<VoucherTypeMaster> voucherTypes = tallyMessages.stream()
                .map(TallyMessage::getVoucherType)
                .filter(java.util.Objects::nonNull)
                .map(vtXml -> {
                    VoucherTypeMaster vt = new VoucherTypeMaster();
                    vt.setName(vtXml.getName());
                    vt.setCompany(company);
                    return vt;
                })
                .toList();
        voucherTypeMasterRepository.saveAll(voucherTypes);
        log.info("Saved {} Voucher Type masters.", voucherTypes.size());

        // --- Ledger Processing ---
        log.info("Processing Ledger masters...");
        List<LedgerMaster> ledgers = tallyMessages.stream()
                .map(TallyMessage::getLedger)
                .filter(java.util.Objects::nonNull)
                .map(xml -> ledgerMasterMapper.toLedgerMaster(xml, groupMap))
                .peek(master -> master.setCompany(company))
                .toList();
        ledgerMasterRepository.saveAll(ledgers);
        log.info("Saved {} Ledger masters.", ledgers.size());
    }

    private void processVouchersFromXml(String content, Company company) throws JAXBException {
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Envelope envelope = (Envelope) jaxbUnmarshaller.unmarshal(new StringReader(content));

        List<TallyMessage> tallyMessages = Optional.ofNullable(envelope)
                .map(Envelope::getBody)
                .map(Body::getImportData)
                .map(ImportData::getRequestData)
                .map(RequestData::getTallyMessages)
                .orElse(Collections.emptyList());

        tallyMessages.stream()
                .map(TallyMessage::getVoucher)
                .filter(java.util.Objects::nonNull)
                .map(voucherXml -> voucherMapper.toVoucher(voucherXml, company))
                .filter(java.util.Objects::nonNull)
                .peek(voucher -> voucher.setCompany(company))
                .forEach(voucherRepository::save);
    }
}