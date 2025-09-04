package com.superaccountant.ETL;

import com.superaccountant.ETL.model.Body;
import com.superaccountant.ETL.model.ImportData;
import com.superaccountant.ETL.model.RequestData;
import com.superaccountant.ETL.model.Envelope;
import com.superaccountant.ETL.model.TallyMessage;
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
import java.util.List;
import java.util.Optional;

@Service
public class XmlParsingService {

    private static final Logger log = LoggerFactory.getLogger(XmlParsingService.class);

    private final VoucherRepository voucherRepository;
    private final VoucherMapper voucherMapper;
    private final TallyXmlDataRepository tallyXmlDataRepository;
    private final JAXBContext jaxbContext;
    private final CompanyRepository companyRepository;
    private final XmlSanitizer xmlSanitizer;

    @Autowired
    public XmlParsingService(VoucherRepository voucherRepository, VoucherMapper voucherMapper,
            TallyXmlDataRepository tallyXmlDataRepository, JAXBContext jaxbContext, XmlSanitizer xmlSanitizer,
            CompanyRepository companyRepository) {
        this.voucherRepository = voucherRepository;
        this.voucherMapper = voucherMapper;
        this.tallyXmlDataRepository = tallyXmlDataRepository;
        this.jaxbContext = jaxbContext;
        this.xmlSanitizer = xmlSanitizer;
        this.companyRepository = companyRepository;
    }

    @Transactional
    public void parseAndSave(MultipartFile file, String companyName) throws JAXBException, IOException {
        Company company = companyRepository.findByName(companyName)
                .orElseGet(() -> {
                    Company newCompany = new Company();
                    newCompany.setName(companyName);
                    return companyRepository.save(newCompany);
                });

        // Delete existing vouchers for this company before processing the new file.
        voucherRepository.deleteAllByCompany(company);

        String content = xmlSanitizer.sanitize(file.getBytes(), file.getOriginalFilename());

        // Stage the raw XML data for auditing and reprocessing
        tallyXmlDataRepository.save(new TallyXmlData(file.getOriginalFilename(), content, company));

        processVouchersFromXml(content, company);
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
                .map(voucherMapper::toVoucher)
                .filter(java.util.Objects::nonNull).peek(voucher -> voucher.setCompany(company))
                .forEach(voucherRepository::save);
    }
}