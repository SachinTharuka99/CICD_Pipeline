package com.epic.cms.connector;

import com.epic.cms.common.ProcessBuilder;
import com.epic.cms.model.bean.LimitIncrementBean;
import com.epic.cms.model.bean.ProcessBean;
import com.epic.cms.repository.CommonRepo;
import com.epic.cms.repository.IncrementLimitExpireRepo;
import com.epic.cms.service.IncrementLimitExpireService;
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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class IncrementLimitExpireConnector extends ProcessBuilder {
<<<<<<< Updated upstream
=======
    int capacity = 200000;
    BlockingQueue<Integer> successCount = new ArrayBlockingQueue<Integer>(capacity);
    BlockingQueue<Integer> failCount = new ArrayBlockingQueue<Integer>(capacity);
>>>>>>> Stashed changes
    private static final Logger logInfo = LoggerFactory.getLogger("logInfo");
    private static final Logger logError = LoggerFactory.getLogger("logError");
    public int configProcess = Configurations.PROCESS_ID_INCREMENT_LIMIT_EXPIRE;
    public String processHeader = "LIMIT INCEREMENT EXPIRE PROCESS";
    @Autowired
    IncrementLimitExpireService incrementLimitExpireService;
    @Autowired
    LogManager logManager;
    @Autowired
    @Qualifier("taskExecutor2")
    ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    StatusVarList status;
    @Autowired
    IncrementLimitExpireRepo incrementLimitExpireRepo;
    @Autowired
    CommonRepo commonRepo;

    @Override
    public void concreteProcess() throws Exception {
        ArrayList<LimitIncrementBean> cardList = new ArrayList<LimitIncrementBean>();
        try {
            Configurations.RUNNING_PROCESS_ID = Configurations.PROCESS_ID_INCREMENT_LIMIT_EXPIRE;
            CommonMethods.eodDashboardProgressParametersReset();
            processBean = new ProcessBean();
            processBean = commonRepo.getProcessDetails(Configurations.PROCESS_ID_INCREMENT_LIMIT_EXPIRE);

            if (processBean != null) {
                /** Expire the Increment*/
                cardList = incrementLimitExpireRepo.getLimitExpiredCardList();
                Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS = cardList.size();

                /** Limit Expiring card one by one*/
//                for (LimitIncrementBean limitIncrementBean : cardList) {
//                    incrementLimitExpireService.processCreditLimitExpire(limitIncrementBean, processBean, configProcess, processHeader);
//                }

                cardList.forEach(limitIncrementBean -> {
<<<<<<< Updated upstream
                    incrementLimitExpireService.processCreditLimitExpire(limitIncrementBean, processBean, configProcess, processHeader, Configurations.successCount, Configurations.failCount);
=======
                    incrementLimitExpireService.processCreditLimitExpire(limitIncrementBean, processBean, configProcess, processHeader, successCount, failCount);
>>>>>>> Stashed changes
                });


                /**wait till all the threads are completed*/
                while (!(taskExecutor.getActiveCount() == 0)) {
                    Thread.sleep(1000);
                }
            }

        } catch (Exception e) {
            try {
                Configurations.IS_PROCESS_COMPLETELY_FAILED = true;
                logError.error("Increment Limit Expire Process Completely failed", e);

                if (processBean.getCriticalStatus() == 1) {
                    Configurations.COMMIT_STATUS = false;
                    Configurations.FLOW_STEP_COMPLETE_STATUS = false;
                    Configurations.PROCESS_FLOW_STEP_COMPLETE_STATUS = false;
                    Configurations.MAIN_EOD_STATUS = false;
                }
            } catch (Exception e2) {
                logError.error("Increment Limit Expire process ended with", e2);
            }
        } finally {
            logInfo.info(logManager.logSummery(summery));
            try {
                if (cardList != null && cardList.size() != 0) {
                    /* PADSS Change -
                    variables handling card data should be nullified by replacing the value of variable with zero and call NULL function */
                    for (LimitIncrementBean limitIncrementBean : cardList) {
                        CommonMethods.clearStringBuffer(limitIncrementBean.getCardNumber());
                    }
                    cardList = null;
                }
            } catch (Exception e2) {
                logError.error("Exception", e2);
            }
        }
    }

    @Override
    public void addSummaries() {
        summery.put("Started Date", Configurations.EOD_DATE.toString());
        summery.put("No of Card effected", Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS);
<<<<<<< Updated upstream
        summery.put("No of Success Card ", Configurations.successCount.size());
        summery.put("No of fail Card ",Configurations.failCount.size());
=======
        summery.put("No of Success Card ", successCount.size());
        summery.put("No of fail Card ",failCount.size());
>>>>>>> Stashed changes
    }
}
