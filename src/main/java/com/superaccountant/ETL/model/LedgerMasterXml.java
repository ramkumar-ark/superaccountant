package com.superaccountant.ETL.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class LedgerMasterXml {

    @XmlAttribute(name = "NAME")
    private String name;

    @XmlElement(name = "GUID")
    private String guid;

    @XmlElement(name = "PARENT")
    private String groupName;

    @XmlElement(name = "ISGSTAPPLICABLE")
    private String isGstApplicable;

    @XmlElement(name = "ISTDSAPPLICABLE")
    private String isTdsApplicable;

    @XmlElement(name = "PANNO")
    private String pan;

    @XmlElement(name = "PARTYGSTIN")
    private String gstin;

    @XmlElement(name = "GSTTYPEOFSUPPLY")
    private String typeOfSupply;
}