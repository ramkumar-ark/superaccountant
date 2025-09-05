package com.superaccountant.ETL.model;

import lombok.Data;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private List<LedgerEntryXml> allLedgerEntriesList;

    @XmlElement(name = "LEDGERENTRIES.LIST")
    private List<LedgerEntryXml> ledgerEntriesList;

    @XmlElement(name = "INVENTORYENTRIES.LIST")
    private List<InventoryEntryXml> inventoryEntriesList;

    @XmlElement(name = "ALLINVENTORYENTRIES.LIST")
    private List<InventoryEntryXml> allInventoryEntriesList;

    /**
     * Combines entries from both ALLLEDGERENTRIES.LIST and LEDGERENTRIES.LIST.
     * It also extracts accounting entries from within INVENTORYENTRIES.LIST, which
     * is
     * common for purchase/sales vouchers in Tally XML.
     * This custom getter ensures that the mapper, which likely calls
     * getLedgerEntries(),
     * receives a complete list of entries regardless of which tag was used in the
     * XML.
     * The @Data annotation will not generate a getter for 'ledgerEntries' as no
     * such field exists.
     *
     * @return A combined list of all ledger entries.
     */
    public List<LedgerEntryXml> getLedgerEntries() {
        Stream<LedgerEntryXml> regularEntries = Stream.of(allLedgerEntriesList, ledgerEntriesList)
                .filter(java.util.Objects::nonNull).flatMap(List::stream);

        // Combine all possible inventory entry lists before processing.
        Stream<InventoryEntryXml> combinedInventoryEntries = Stream.of(inventoryEntriesList, allInventoryEntriesList)
                .filter(java.util.Objects::nonNull)
                .flatMap(List::stream);

        // Extracts the lists of ledger entries from each inventory item and flattens
        // them into a single stream.
        Stream<LedgerEntryXml> inventoryBasedEntries = combinedInventoryEntries
                .map(InventoryEntryXml::getAccountingAllocations) // Stream<List<LedgerEntryXml>>
                .filter(java.util.Objects::nonNull).flatMap(List::stream); // Stream<LedgerEntryXml>

        return Stream.concat(regularEntries, inventoryBasedEntries).collect(Collectors.toList());
    }
}
