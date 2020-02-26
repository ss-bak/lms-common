package com.smoothstack.lms.common.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smoothstack.lms.common.model.Book;
import com.smoothstack.lms.common.model.Borrower;
import com.smoothstack.lms.common.model.Branch;
import com.smoothstack.lms.common.model.Loans;

public interface LoansCommonRepository extends JpaRepository<Loans, Long> {

    List<Loans> findAllByBorrower(Borrower borrower);

    boolean existsByBook(Book book);

    boolean existsByBorrower(Borrower borrower);

    boolean existsByBranch(Branch branch);


    List<Loans> findAllByBorrowerAndBook(Borrower borrower, Book book);

    Optional<Loans> findAllByBorrowerAndBranchAndBook(Borrower borrower, Branch branch, Book book);



}
