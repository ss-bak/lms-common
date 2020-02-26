package com.smoothstack.lms.common.repository;

import com.smoothstack.lms.common.model.Book;
import com.smoothstack.lms.common.model.Branch;
import com.smoothstack.lms.common.model.Copies;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CopiesCommonRepository extends JpaRepository<Copies, Long> {


    default Optional<Copies> findById(Long bookId, Long branchId) {
        return findByBook_BookIdAndBranch_BranchId(bookId, branchId);
    }

    Optional<Copies> findByBook_BookIdAndBranch_BranchId(Long bookId, Long branchId);

    boolean existsByBook(Book book);
    boolean existsByBranch(Branch branch);

    void deleteAllByBook(Book book);



    List<Copies> findAllByBranchAndCopiesAmountGreaterThanEqual(Branch branch, int minimumNoOfCopies);

    Optional<Copies> findAllByBookAndBranchAndCopiesAmountGreaterThanEqual(Book book, Branch branch, int minimumNoOfCopies);
}
