package com.superaccountant.ETL.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class TallyMessage {

    @XmlElement(name = "VOUCHER")
    private VoucherXml voucher;

    @XmlElement(name = "LEDGER")
    private LedgerMasterXml ledger;

    @XmlElement(name = "GROUP")
    private GroupMasterXml group;

    @XmlElement(name = "VOUCHERTYPE")
    private VoucherTypeMasterXml voucherType;
}
