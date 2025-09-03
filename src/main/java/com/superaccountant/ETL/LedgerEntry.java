package com.superaccountant.ETL;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "voucher") // Exclude to avoid circular dependency issues in logs
public class LedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String ledgerName;
    private Boolean isDeemedPositive;
    private Double amount;

    @ManyToOne
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;
}
