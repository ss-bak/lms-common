package com.smoothstack.lms.common.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smoothstack.lms.common.model.Book;
import com.smoothstack.lms.common.model.Branch;
import com.smoothstack.lms.common.model.Copies;

public interface CopiesCommonRepository extends JpaRepository<Copies, Long> {
  
    boolean existsByBook(Book book);
    boolean existsByBranch(Branch branch);

    void deleteAllByBook(Book book);

    List<Copies> findAllByBranchAndCopiesAmountGreaterThanEqual(Branch branch, int minimumNoOfCopies);

    Optional<Copies> findAllByBookAndBranchAndCopiesAmountGreaterThanEqual(Book book, Branch branch, int minimumNoOfCopies);
}
