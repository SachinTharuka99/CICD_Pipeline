package com.epic.cms.connector;

import com.epic.cms.common.ProcessBuilder;
import com.epic.cms.model.bean.DropRequestBean;
import com.epic.cms.model.bean.ProcessBean;
import com.epic.cms.repository.CommonRepo;
import com.epic.cms.repository.TxnDropRequestRepo;
import com.epic.cms.service.TxnDropRequestService;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TxnDropRequestConnector extends ProcessBuilder {

    private static final Logger logInfo = LoggerFactory.getLogger("logInfo");
    private static final Logger logError = LoggerFactory.getLogger("logError");
    @Autowired
    @Qualifier("ThreadPool_100")
    ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    LogManager logManager;
    @Autowired
    CommonRepo commonRepo;
    @Autowired
    TxnDropRequestRepo txnDropRequestRepo;
    @Autowired
    StatusVarList statusList;
    @Autowired
    TxnDropRequestService txnDropRequestService;
<<<<<<< Updated upstream
=======
    int capacity = 200000;
    BlockingQueue<Integer> successCount = new ArrayBlockingQueue<Integer>(capacity);
    BlockingQueue<Integer> failCount = new ArrayBlockingQueue <Integer>(capacity);
>>>>>>> Stashed changes

    @Override
    public void concreteProcess() throws Exception {

        int successTxn = 0;
        int failedCards = 0;
        int failedTxn = 0;
        Set<StringBuffer> failedCardList = new HashSet<>();
        List<DropRequestBean> dropTransactionList = null;

        try {

            Configurations.RUNNING_PROCESS_ID = Configurations.PROCESS_TRANSACTION_DROP_REQUEST;
            CommonMethods.eodDashboardProgressParametersReset();
            processBean = new ProcessBean();
            processBean = commonRepo.getProcessDetails(Configurations.PROCESS_TRANSACTION_DROP_REQUEST);

            if (processBean != null) {

                /**get transaction validity period*/
                int txnValidityPeriod = txnDropRequestRepo.getTransactionValidityPeriod();

                /**get eligible drop transaction list*/
                dropTransactionList = txnDropRequestRepo.getDropTransactionList(txnValidityPeriod);
                Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS = dropTransactionList.size();

                if (dropTransactionList.size() > 0) {
//                    for (DropRequestBean bean : dropTransactionList) {
//                        txnDropRequestService.processTxnDropRequest(bean, processBean,faileCardCount);
//                    }

                    dropTransactionList.forEach(bean -> {
<<<<<<< Updated upstream
                        txnDropRequestService.processTxnDropRequest(bean, processBean,Configurations.successCount,Configurations.failCount);
=======
                        txnDropRequestService.processTxnDropRequest(bean, processBean,successCount,failCount);
>>>>>>> Stashed changes
                    });

                }

                while (!(taskExecutor.getActiveCount() == 0)) {
                    Thread.sleep(1000);
                }

                successTxn = Configurations.SuccessCount_TxnDropRequest;
                failedTxn = Configurations.FailedCount_TxnDropRequest;
                failedCards = Configurations.FailedCards_TxnDropRequest;

                Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS = (failedTxn + successTxn);
                Configurations.PROCESS_SUCCESS_COUNT = successTxn;
                Configurations.PROCESS_FAILD_COUNT = failedTxn;
            }
        } catch (Exception e) {
            Configurations.IS_PROCESS_COMPLETELY_FAILED = true;
            try {
                logError.error("Transaction Drop Request process failed", e);
                if (processBean.getCriticalStatus() == 1) {
                    Configurations.COMMIT_STATUS = false;
                    Configurations.FLOW_STEP_COMPLETE_STATUS = false;
                    Configurations.PROCESS_FLOW_STEP_COMPLETE_STATUS = false;
                    Configurations.MAIN_EOD_STATUS = false;
                }
            } catch (Exception e2) {
                logError.error("Exception in Transaction Drop Request", e2);
            }
        } finally {
            //logInfo.info(logManager.logSummery(summery));
            try {
                /** PADSS Change -
                 variables handling card data should be nullified by replacing the value of variable with zero and call NULL function */
                if (dropTransactionList != null && dropTransactionList.size() != 0) {
                    for (DropRequestBean bean : dropTransactionList) {
                        CommonMethods.clearStringBuffer(bean.getCardNumber());
                    }
                    dropTransactionList = null;
                }
                failedCardList = null;

            } catch (Exception e3) {
                logError.error("Exception in Transaction Drop Request", e3);
            }
        }
    }

    @Override
    public void addSummaries() {
        summery.put("Started Date ", Configurations.EOD_DATE.toString());
        summery.put("Total Number of Total Request ", Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS);
<<<<<<< Updated upstream
        summery.put("No of Drop Requests ", Configurations.successCount.size());
        summery.put("No of Fail Requests ", Configurations.failCount.size());
=======
        summery.put("No of Drop Requests ", successCount.size());
        summery.put("No of Fail Requests ", failCount.size());

//        summery.put("No of Drop Requests ", Configurations.PROCESS_SUCCESS_COUNT);
//        summery.put("No of Fail Requests ", Configurations.PROCESS_FAILD_COUNT);
>>>>>>> Stashed changes
        summery.put("No of Fail Cards ", Configurations.FailedCards_TxnDropRequest);


    }
}
