package com.superaccountant.ETL.model;

import lombok.Data;

import java.util.List;

@Data
public class VoucherInput {

    private String remoteId;
    private String voucherKey;
    private String voucherType;
    private String action;
    private String date;
    private String guid;
    private String narration;
    private String partyLedgerName;
    private String voucherTypeName;
    private String voucherNumber;

    private List<LedgerEntryInput> ledgerEntries;
}
