package com.superaccountant.ETL;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TallyXmlDataRepository extends JpaRepository<TallyXmlData, Long> {
}
