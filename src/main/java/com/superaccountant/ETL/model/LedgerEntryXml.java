package com.superaccountant.ETL.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class LedgerEntryXml {

    @XmlElement(name = "LEDGERNAME")
    private String ledgerName;

    @XmlElement(name = "ISDEEMEDPOSITIVE")
    private String isDeemedPositive;

    @XmlElement(name = "AMOUNT")
    private Double amount;
}
