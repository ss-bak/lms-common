package com.smoothstack.lms.common.service;

import com.smoothstack.lms.common.model.Branch;
import com.smoothstack.lms.common.repository.BranchCommonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.Validator;

public class BranchCommonService implements CommonService<Branch, Long> {


    private BranchCommonRepository authorCommonRepository;

    private Validator validator;

    @Autowired
    public final void setBranchCommonRepository(BranchCommonRepository authorCommonRepository) {
        this.authorCommonRepository = authorCommonRepository;
    }

    @Autowired
    public void setValidator(@Qualifier("DefaultValidator") Validator validator) {
        this.validator = validator;
    }

    @Override
    public Validator getValidator() {
        return validator;
    }

    @Override
    public JpaRepository<Branch, Long> getJpaRepository() {
        return authorCommonRepository;
    }
}

