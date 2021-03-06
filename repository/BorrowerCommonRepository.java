package com.smoothstack.lms.common.repository;

import com.smoothstack.lms.common.model.Borrower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BorrowerCommonRepository extends JpaRepository<Borrower, Long> {
    boolean existsByBorrowerNameIgnoreCase(String borrowerName);
}
