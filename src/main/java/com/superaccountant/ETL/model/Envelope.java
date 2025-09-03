package com.superaccountant.ETL.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement(name = "ENVELOPE")
@XmlAccessorType(XmlAccessType.FIELD)
public class Envelope {

    @XmlElement(name = "BODY")
    private Body body;
}
