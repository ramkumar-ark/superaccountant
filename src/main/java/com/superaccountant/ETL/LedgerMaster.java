package com.superaccountant.ETL;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class LedgerMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String guid;

    private String groupName;

    private Boolean isGstApplicable;

    private Boolean isTdsApplicable;

    private String pan;

    private String gstin;

    private String typeOfSupply;

    private String baseGroupName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}