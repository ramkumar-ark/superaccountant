package com.superaccountant.ETL;

import com.superaccountant.ETL.model.LedgerEntryXml;
import com.superaccountant.ETL.model.VoucherXml;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class VoucherMapper {

    public Voucher toVoucher(VoucherXml voucherXml) {
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
        voucher.setPartyLedgerName(voucherXml.getPartyLedgerName());
        voucher.setVoucherTypeName(voucherXml.getVoucherTypeName());
        voucher.setVoucherNumber(voucherXml.getVoucherNumber());

        if (voucherXml.getLedgerEntries() != null) {
            List<LedgerEntry> ledgerEntries = voucherXml.getLedgerEntries().stream()
                    .map(this::toLedgerEntry)
                    .collect(Collectors.toList());
            ledgerEntries.forEach(le -> le.setVoucher(voucher));
            voucher.setLedgerEntries(ledgerEntries);
        }

        return voucher;
    }

    private LedgerEntry toLedgerEntry(LedgerEntryXml ledgerEntryXml) {
        LedgerEntry ledgerEntry = new LedgerEntry();
        ledgerEntry.setLedgerName(ledgerEntryXml.getLedgerName());
        ledgerEntry.setIsDeemedPositive("Yes".equalsIgnoreCase(ledgerEntryXml.getIsDeemedPositive()));
        ledgerEntry.setAmount(ledgerEntryXml.getAmount());
        return ledgerEntry;
    }
}