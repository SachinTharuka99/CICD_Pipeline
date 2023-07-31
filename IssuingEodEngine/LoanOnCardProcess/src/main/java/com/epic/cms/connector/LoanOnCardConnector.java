package com.epic.cms.connector;

import com.epic.cms.common.ProcessBuilder;
import com.epic.cms.model.bean.InstallmentBean;
import com.epic.cms.model.bean.ProcessBean;
import com.epic.cms.repository.CommonRepo;
import com.epic.cms.repository.InstallmentPaymentRepo;
import com.epic.cms.service.LoanOnCardService;
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
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LoanOnCardConnector extends ProcessBuilder {
<<<<<<< Updated upstream
=======

    int capacity = 200000;
    BlockingQueue<Integer> successCount = new ArrayBlockingQueue<Integer>(capacity);
    BlockingQueue<Integer> failCount = new ArrayBlockingQueue<Integer>(capacity);
>>>>>>> Stashed changes
    private static final Logger logInfo = LoggerFactory.getLogger("logInfo");
    private static final Logger logError = LoggerFactory.getLogger("logError");
    @Autowired
    LogManager logManager;
    @Autowired
    CommonRepo commonRepo;
    @Autowired
    @Qualifier("ThreadPool_100")
    ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    InstallmentPaymentRepo installmentPaymentRepo;
    @Autowired
    LoanOnCardService loanOnCardService;
    @Autowired
    StatusVarList status;

    @Override
    public void concreteProcess() throws Exception {
        LinkedHashMap summery = new LinkedHashMap();
        List<InstallmentBean> txnList = new ArrayList<>();
        try {
            processBean = new ProcessBean();
            processBean = commonRepo.getProcessDetails(Configurations.PROCESS_ID_LOAN_ON_CARD);
            if (processBean != null) {
                Configurations.RUNNING_PROCESS_ID = Configurations.PROCESS_ID_LOAN_ON_CARD;
                //reset dashboard parameters
                CommonMethods.eodDashboardProgressParametersReset();
                /**
                 * Accelerate the LOC requests of NP accounts
                 */
                loanOnCardService.accelerateLOCRequestForNpAccount();
                /**
                 * LOC process
                 */
                txnList = installmentPaymentRepo.getBTOrLOCDetails("LOANONCARDREQUEST", "LOANONCARDPLAN");
                Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS += txnList.size();
//                for (InstallmentBean installmentBean : txnList) {
//                    loanOnCardService.startLOCProcess(installmentBean, processBean);
//                }

                txnList.forEach(installmentBean -> {
<<<<<<< Updated upstream
                    loanOnCardService.startLOCProcess(installmentBean, processBean, Configurations.successCount,Configurations.failCount);
=======
                    loanOnCardService.startLOCProcess(installmentBean, processBean, successCount,failCount);
>>>>>>> Stashed changes
                });

                //wait till all the threads are completed
                while (!(taskExecutor.getActiveCount() == 0)) {
                    Thread.sleep(1000);
                }
            }
        } catch (Exception e) {
            Configurations.IS_PROCESS_COMPLETELY_FAILED = true;
            logError.error("Loan On Card process failed", e);
        } finally {
            //logInfo.info(logManager.logSummery(summery));
            /** PADSS Change -
             variables handling card data should be nullified by replacing the value of variable with zero and call NULL function */
            if (txnList != null && txnList.size() != 0) {
                for (InstallmentBean installmentBean : txnList) {
                    CommonMethods.clearStringBuffer(installmentBean.getCardNumber());
                }
                txnList = null;
            }
        }
    }

    @Override
    public void addSummaries() {
        summery.put("Started Date", Configurations.EOD_DATE.toString());
        summery.put("No of Card effected", Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS);
<<<<<<< Updated upstream
        summery.put("No of Success Card ",Configurations.successCount.size());
        summery.put("No of fail Card ",Configurations.failCount.size());
=======
        summery.put("No of Success Card ",successCount.size());
        summery.put("No of fail Card ",failCount.size());
>>>>>>> Stashed changes
    }
}
