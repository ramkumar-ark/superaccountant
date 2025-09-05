package com.superaccountant.ETL;

import com.superaccountant.ETL.model.LedgerEntryXml;
import com.superaccountant.ETL.model.VoucherXml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class VoucherMapper {

    private final LedgerMasterRepository ledgerMasterRepository;
    private final VoucherTypeMasterRepository voucherTypeMasterRepository;

    @Autowired
    public VoucherMapper(LedgerMasterRepository ledgerMasterRepository,
            VoucherTypeMasterRepository voucherTypeMasterRepository) {
        this.ledgerMasterRepository = ledgerMasterRepository;
        this.voucherTypeMasterRepository = voucherTypeMasterRepository;
    }

    public Voucher toVoucher(VoucherXml voucherXml, Company company) {
        if (voucherXml == null) {
            return null;
        }

        Voucher voucher = new Voucher();
        voucher.setRemoteId(voucherXml.getRemoteId());
        voucher.setVoucherKey(voucherXml.getVoucherKey());
        voucher.setVoucherType(voucherXml.getVoucherType());
        voucher.setAction(voucherXml.getAction());
        voucher.setDate(voucherXml.getDate());
        voucher.setGuid(voucherXml.getGuid());
        voucher.setNarration(voucherXml.getNarration());

        if (voucherXml.getPartyLedgerName() != null && !voucherXml.getPartyLedgerName().isBlank()) {
            LedgerMaster partyLedgerMaster = ledgerMasterRepository
                    .findByCompanyAndName(company, voucherXml.getPartyLedgerName())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Party ledger master not found for company '" + company.getName() + "' and ledger name '"
                                    + voucherXml.getPartyLedgerName() + "'. Please upload masters first."));
            voucher.setPartyLedgerMaster(partyLedgerMaster);
        }

        VoucherTypeMaster voucherTypeMaster = voucherTypeMasterRepository
                .findByCompanyAndName(company, voucherXml.getVoucherTypeName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Voucher Type master not found for company '" + company.getName() + "' and voucher type name '"
                                + voucherXml.getVoucherTypeName() + "'. Please upload masters first."));
        voucher.setVoucherTypeMaster(voucherTypeMaster);

        voucher.setVoucherNumber(voucherXml.getVoucherNumber());

        if (voucherXml.getLedgerEntries() != null) {
            List<LedgerEntry> ledgerEntries = voucherXml.getLedgerEntries().stream()
                    .map(xml -> toLedgerEntry(xml, company))
                    .collect(Collectors.toList());
            ledgerEntries.forEach(le -> le.setVoucher(voucher));
            voucher.setLedgerEntries(ledgerEntries);
        }
        return voucher;
    }

    private LedgerEntry toLedgerEntry(LedgerEntryXml ledgerEntryXml, Company company) {
        LedgerEntry ledgerEntry = new LedgerEntry();
        ledgerEntry.setIsDeemedPositive("Yes".equalsIgnoreCase(ledgerEntryXml.getIsDeemedPositive()));
        ledgerEntry.setAmount(ledgerEntryXml.getAmount());

        // Find the corresponding LedgerMaster
        LedgerMaster ledgerMaster = ledgerMasterRepository.findByCompanyAndName(company, ledgerEntryXml.getLedgerName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ledger master not found for company '" + company.getName() + "' and ledger name '"
                                + ledgerEntryXml.getLedgerName() + "'. Please upload masters first."));
        ledgerEntry.setLedgerMaster(ledgerMaster);
        return ledgerEntry;
    }
}