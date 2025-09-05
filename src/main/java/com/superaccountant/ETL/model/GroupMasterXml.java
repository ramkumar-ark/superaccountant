package com.superaccountant.ETL.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class GroupMasterXml {

    @XmlAttribute(name = "NAME")
    private String name;

    @XmlElement(name = "PARENT")
    private String parent;
}