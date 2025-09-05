package com.superaccountant.ETL.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

import java.util.List;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class InventoryEntryXml {

    // This captures the list of accounting details (ledger name, amount) for the
    // stock item. Tally often nests multiple ledger postings (e.g., for purchases
    // and additional costs) within the inventory entry structure.
    @XmlElement(name = "ACCOUNTINGALLOCATIONS.LIST")
    private List<LedgerEntryXml> accountingAllocations;
}