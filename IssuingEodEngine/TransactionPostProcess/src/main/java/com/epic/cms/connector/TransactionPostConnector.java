/**
 * Author : sharuka_j
 * Date : 11/22/2022
 * Time : 3:48 PM
 * Project Name : ecms_eod_engine
 */

package com.epic.cms.connector;

import com.epic.cms.common.ProcessBuilder;
import com.epic.cms.dao.TransactionPostDao;
import com.epic.cms.model.bean.OtbBean;
import com.epic.cms.service.TransactionPostService;
import com.epic.cms.util.CommonMethods;
import com.epic.cms.util.Configurations;
import com.epic.cms.util.LogManager;
import com.epic.cms.util.StatusVarList;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TransactionPostConnector extends ProcessBuilder {
    @Autowired
    StatusVarList statusList;

    @Autowired
    TransactionPostDao transactionPostDao;

    @Autowired
    TransactionPostService transactionPostService;

    @Autowired
    LogManager logManager;
<<<<<<< Updated upstream
=======
    int capacity = 200000;
    BlockingQueue<Integer> successCount = new ArrayBlockingQueue<Integer>(capacity);
    BlockingQueue<Integer> failCount = new ArrayBlockingQueue <Integer>(capacity);
>>>>>>> Stashed changes
    private static final Logger logInfo = LoggerFactory.getLogger("logInfo");
    private static final Logger logError = LoggerFactory.getLogger("logError");
    @Autowired
    @Qualifier("ThreadPool_100")
    ThreadPoolTaskExecutor taskExecutor;

    LinkedHashMap summery = new LinkedHashMap();
    private ArrayList<OtbBean> custAccList = new ArrayList<OtbBean>();
    private ArrayList<OtbBean> txnList;
    private int failedCount = 0;

    @Override
    public void concreteProcess() throws Exception {
        try {
            Configurations.RUNNING_PROCESS_ID = Configurations.PROCESS_ID_TXN_POST;
            CommonMethods.eodDashboardProgressParametersReset();
            if (Configurations.STARTING_EOD_STATUS.equals(statusList.getINITIAL_STATUS())) {
                custAccList = transactionPostDao.getInitEodTxnPostCustAcc();
            } else if (Configurations.STARTING_EOD_STATUS.equals(statusList.getERROR_STATUS())) {
                custAccList = transactionPostDao.getErrorEodTxnPostCustAcc();
            }
            Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS = custAccList.size();

            for (OtbBean bean : custAccList) {
                transactionPostService.transactionList(bean, Configurations.successCount,Configurations.failCount);
            }

<<<<<<< Updated upstream
           /* custAccList.forEach(bean -> {
                transactionPostService.transactionList(bean, Configurations.successCount,Configurations.failCount);
            });*/
=======
            custAccList.forEach(bean -> {
                transactionPostService.transactionList(bean, successCount,failCount);
            });
>>>>>>> Stashed changes


            //wait till all the threads are completed
            while (!(taskExecutor.getActiveCount() == 0)) {
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            Configurations.IS_PROCESS_COMPLETELY_FAILED = true;
            logError.error("Failed Transaction Post Process Completely ", e);
        } finally {
            //logInfo.info(logManager.logSummery(summery));
            try {
               /* PADSS Change -
            variables handling card data should be nullified by replacing the value of variable with zero and call NULL function */
                if (custAccList != null && custAccList.size() != 0) {
                    for (OtbBean bean : custAccList) {
                        CommonMethods.clearStringBuffer(bean.getCardnumber());
                        CommonMethods.clearStringBuffer(bean.getMaincardno());
                    }
                    custAccList = null;
                }

                if (txnList != null && txnList.size() != 0) {
                    for (OtbBean card : txnList) {
                        CommonMethods.clearStringBuffer(card.getCardnumber());
                        CommonMethods.clearStringBuffer(card.getMaincardno());
                    }
                    txnList = null;
                }
            } catch (Exception e) {
                logError.error("Transaction Post Process Clear StringBuffer Fail" + e);
            }
        }
    }

    @Override
    public void addSummaries() {
        summery.put("Number of accounts to fee post ", Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS);
<<<<<<< Updated upstream
        summery.put("Number of success fee post ", Configurations.successCount.size());
        summery.put("Number of failure fee post ", Configurations.failCount.size());
=======
        summery.put("Number of success fee post ", successCount.size());
        summery.put("Number of failure fee post ", failCount.size());
>>>>>>> Stashed changes
    }
}
