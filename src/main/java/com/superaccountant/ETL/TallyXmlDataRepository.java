package com.superaccountant.ETL;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TallyXmlDataRepository extends JpaRepository<TallyXmlData, Long> {
    Optional<TallyXmlData> findTopByFileNameOrderByIdDesc(String fileName);
}