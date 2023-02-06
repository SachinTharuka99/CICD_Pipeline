package com.epic.cms.service;

import com.epic.cms.model.bean.ErrorCardBean;
import com.epic.cms.repository.OnlineToBackendTxnRepo;
import com.epic.cms.util.CardAccount;
import com.epic.cms.util.Configurations;
import com.epic.cms.util.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.util.LinkedHashMap;

import static com.epic.cms.util.LogManager.errorLogger;
import static com.epic.cms.util.LogManager.infoLogger;


@Service
@Transactional(value="transactionManager",propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
public class OnlineToBackendTxnService {

    @Autowired
    LogManager logManager;

    @Autowired
    OnlineToBackendTxnRepo onlineToBackendTxnRepo;

    public void OnlineToBackend(){
        int[] txnCounts = new int[3];
        LinkedHashMap summery = new LinkedHashMap<>();

        try {
            txnCounts = onlineToBackendTxnRepo.callStoredProcedureForTxnSync();

            Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS=txnCounts[2];
            Configurations.PROCESS_SUCCESS_COUNT=txnCounts[0];
            Configurations.PROCESS_FAILD_COUNT=txnCounts[1];

            summery.put("Total Transaction Count ", Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS);
            summery.put("Total Success Count ", Configurations.PROCESS_SUCCESS_COUNT);
            summery.put("Total Failed Count ", Configurations.PROCESS_FAILD_COUNT);

            infoLogger.info(logManager.processSummeryStyles(summery));

        } catch (Exception e) {
            errorLogger.error("Online to backend transaction sync process failed for transaction ", e);
        }
    }
}
