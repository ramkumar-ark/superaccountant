package com.superaccountant.ETL;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface LedgerMasterRepository extends JpaRepository<LedgerMaster, Long> {
    Optional<LedgerMaster> findByCompanyAndName(Company company, String name);

    @Transactional
    void deleteAllByCompany(Company company);
}