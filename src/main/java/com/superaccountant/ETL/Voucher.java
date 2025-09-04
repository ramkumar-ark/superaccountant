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
@ToString(exclude = { "ledgerEntries", "company" }) // Exclude to avoid circular dependency issues in logs
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
    private String partyLedgerName;
    private String voucherTypeName;
    private String voucherNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

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
