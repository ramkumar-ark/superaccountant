package com.superaccountant.ETL;

import com.superaccountant.ETL.model.VoucherInput;
import org.springframework.stereotype.Component;

@Component
public class VoucherInputMapper {

    public void updateVoucherFromInput(VoucherInput source, Voucher target) {
        if (source == null || target == null) {
            return;
        }
        target.setRemoteId(source.getRemoteId());
        target.setVoucherKey(source.getVoucherKey());
        target.setVoucherType(source.getVoucherType());
        target.setAction(source.getAction());
        target.setDate(source.getDate());
        target.setGuid(source.getGuid());
        target.setNarration(source.getNarration());
        target.setPartyLedgerName(source.getPartyLedgerName());
        target.setVoucherTypeName(source.getVoucherTypeName());
        target.setVoucherNumber(source.getVoucherNumber());
    }
}