package com.superaccountant.ETL;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface VoucherTypeMasterRepository extends JpaRepository<VoucherTypeMaster, Long> {
    Optional<VoucherTypeMaster> findByCompanyAndName(Company company, String name);

    @Transactional
    void deleteAllByCompany(Company company);
}