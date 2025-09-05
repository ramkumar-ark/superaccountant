package com.superaccountant.ETL;

import com.superaccountant.ETL.model.LedgerEntryXml;
import com.superaccountant.ETL.model.VoucherXml;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class XmlModelMapper {

    public VoucherXml toVoucherXml(Voucher voucher) {
        if (voucher == null) {
            return null;
        }

        VoucherXml voucherXml = new VoucherXml();
        voucherXml.setRemoteId(voucher.getRemoteId());
        voucherXml.setVoucherKey(voucher.getVoucherKey());
        voucherXml.setVoucherType(voucher.getVoucherType());
        voucherXml.setAction(voucher.getAction());
        voucherXml.setDate(voucher.getDate());
        voucherXml.setGuid(voucher.getGuid());
        voucherXml.setNarration(voucher.getNarration());
        if (voucher.getPartyLedgerMaster() != null) {
            voucherXml.setPartyLedgerName(voucher.getPartyLedgerMaster().getName());
        }
        if (voucher.getVoucherTypeMaster() != null) {
            voucherXml.setVoucherTypeName(voucher.getVoucherTypeMaster().getName());
        }
        voucherXml.setVoucherNumber(voucher.getVoucherNumber());

        if (voucher.getLedgerEntries() != null) {
            voucherXml.setAllLedgerEntriesList(voucher.getLedgerEntries().stream()
                    .map(this::toLedgerEntryXml)
                    .collect(Collectors.toList()));
        }

        return voucherXml;
    }

    private LedgerEntryXml toLedgerEntryXml(LedgerEntry ledgerEntry) {
        LedgerEntryXml ledgerEntryXml = new LedgerEntryXml();
        ledgerEntryXml.setLedgerName(ledgerEntry.getLedgerMaster().getName());
        ledgerEntryXml.setIsDeemedPositive(ledgerEntry.getIsDeemedPositive() ? "Yes" : "No");
        ledgerEntryXml.setAmount(ledgerEntry.getAmount());
        return ledgerEntryXml;
    }
}