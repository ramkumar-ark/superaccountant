package com.superaccountant.ETL.model;

import lombok.Data;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class VoucherXml {

    @XmlAttribute(name = "REMOTEID")
    private String remoteId;
    @XmlAttribute(name = "VCHKEY")
    private String voucherKey;
    @XmlAttribute(name = "VCHTYPE")
    private String voucherType;
    @XmlAttribute(name = "ACTION")
    private String action;
    @XmlElement(name = "DATE")
    private String date;
    @XmlElement(name = "GUID")
    private String guid;
    @XmlElement(name = "NARRATION")
    private String narration;
    @XmlElement(name = "PARTYLEDGERNAME")
    private String partyLedgerName;
    @XmlElement(name = "VOUCHERTYPENAME")
    private String voucherTypeName;
    @XmlElement(name = "VOUCHERNUMBER")
    private String voucherNumber;
    @XmlElement(name = "ALLLEDGERENTRIES.LIST")
    private List<LedgerEntryXml> ledgerEntries;
}
