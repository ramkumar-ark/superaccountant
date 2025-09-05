package com.superaccountant.ETL;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = { "ledgerEntries", "company", "partyLedgerMaster" }) // Exclude to avoid circular dependency issues
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String remoteId;
    private String voucherKey;
    private String voucherType;
    private String action;
    private String date;
    private String guid;
    private String narration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_ledger_master_id")
    private LedgerMaster partyLedgerMaster;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_type_master_id")
    private VoucherTypeMaster voucherTypeMaster;

    private String voucherNumber;

    @OneToMany(mappedBy = "voucher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LedgerEntry> ledgerEntries;

    // Helper method to ensure the bidirectional relationship is maintained
    // correctly.
    public void setLedgerEntries(List<LedgerEntry> ledgerEntries) {
        this.ledgerEntries = ledgerEntries;
        if (ledgerEntries != null) {
            for (LedgerEntry entry : ledgerEntries) {
                entry.setVoucher(this);
            }
        }
    }
}
