package com.superaccountant.ETL;

import com.superaccountant.ETL.model.VoucherInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VoucherInputMapper {

    private final LedgerMasterRepository ledgerMasterRepository;

    // The mapper now needs the repository to look up the party ledger master.
    @Autowired
    public VoucherInputMapper(LedgerMasterRepository ledgerMasterRepository) {
        this.ledgerMasterRepository = ledgerMasterRepository;
    }

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

        // The partyLedgerName is now a relationship, so we look up the master record.
        if (source.getPartyLedgerName() != null && !source.getPartyLedgerName().isBlank()) {
            LedgerMaster partyLedger = ledgerMasterRepository
                    .findByCompanyAndName(target.getCompany(), source.getPartyLedgerName())
                    .orElseThrow(() -> new ResourceNotFoundException("Party ledger master not found with name: "
                            + source.getPartyLedgerName()));
            target.setPartyLedgerMaster(partyLedger);
        }

        // voucherTypeName is now also a relationship and should be handled similarly if
        // updatable.
        // For now, we assume it's not part of the updateVoucher mutation.
        target.setVoucherNumber(source.getVoucherNumber());
    }
}