package com.epic.cms.repository;

import com.epic.cms.model.entity.RECPAYMENTFILEINVALID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecPaymentFileInvalidRepo extends JpaRepository<RECPAYMENTFILEINVALID, Integer>, JpaSpecificationExecutor<RECPAYMENTFILEINVALID> {
    List<RECPAYMENTFILEINVALID> findRECPAYMENTFILEINVALIDByEODID(Long eodId);
}