package com.epic.cms.connector;

import com.epic.cms.common.ProcessBuilder;
import com.epic.cms.model.bean.LastStatementSummeryBean;
import com.epic.cms.repository.CheckPaymentForMinimumAmountRepo;
import com.epic.cms.repository.CommonRepo;
import com.epic.cms.service.CheckPaymentForMinimumAmountService;
import com.epic.cms.util.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class CheckPaymentForMinimumAmountConnector extends ProcessBuilder {
<<<<<<< Updated upstream
=======
    int capacity = 200000;
    BlockingQueue<Integer> successCount = new ArrayBlockingQueue<Integer>(capacity);
    BlockingQueue<Integer> failCount = new ArrayBlockingQueue<Integer>(capacity);
>>>>>>> Stashed changes
    private static final Logger logInfo = LoggerFactory.getLogger("logInfo");
    private static final Logger logError = LoggerFactory.getLogger("logError");
    @Autowired
    StatusVarList statusVarList;
    @Autowired
    LogManager logManager;
    @Autowired
    @Qualifier("ThreadPool_100")
    ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    CheckPaymentForMinimumAmountRepo checkPaymentForMinimumAmountRepo;
    @Autowired
    CheckPaymentForMinimumAmountService checkPaymentForMinimumAmountService;
    @Autowired
    CommonRepo commonRepo;
    List<LastStatementSummeryBean> cardList = new ArrayList<LastStatementSummeryBean>();
    int failedCount = 0;

    @Override
    public void concreteProcess() throws Exception {
        try {
            Configurations.RUNNING_PROCESS_ID = Configurations.PROCESS_CHECK_PAYMENTS_FOR_MIN_AMOUNT;
            CommonMethods.eodDashboardProgressParametersReset();
            cardList = checkPaymentForMinimumAmountRepo.getStatementCardList();
            Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS = cardList.size();
            summery.put("Checking cards for min payment", cardList.size() + "");

<<<<<<< Updated upstream
            for (LastStatementSummeryBean lastStatement : cardList) {
                checkPaymentForMinimumAmountService.CheckPaymentForMinimumAmount(lastStatement,Configurations.successCount, Configurations.failCount);
            }
//            cardList.forEach(lastStatement-> {
//                checkPaymentForMinimumAmountService.CheckPaymentForMinimumAmount(lastStatement,Configurations.successCount, Configurations.failCount);
//            });
=======
//            for (LastStatementSummeryBean lastStatement : cardList) {
//                checkPaymentForMinimumAmountService.CheckPaymentForMinimumAmount(lastStatement);
//            }
            cardList.forEach(lastStatement-> {
                checkPaymentForMinimumAmountService.CheckPaymentForMinimumAmount(lastStatement,successCount, failCount);
            });
>>>>>>> Stashed changes


            //wait till all the threads are completed
            while (!(taskExecutor.getActiveCount() == 0)) {
                Thread.sleep(1000);
            }
<<<<<<< Updated upstream
=======

            Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS = cardList.size();
            Configurations.PROCESS_SUCCESS_COUNT = successCount.size();

>>>>>>> Stashed changes
        } catch (Exception e) {
            Configurations.IS_PROCESS_COMPLETELY_FAILED = true;
            logError.error("Check Payment For Minimum Amount process ended with", e);
        } finally {
           // logInfo.info(logManager.logSummery(summery));
            try {
                if (cardList != null && cardList.size() != 0) {
                    for (LastStatementSummeryBean lastStatementSummeryBean : cardList) {
                        CommonMethods.clearStringBuffer(new StringBuffer(lastStatementSummeryBean.getCardno()));
                    }
                    cardList = null;
                }
            } catch (Exception e) {
                logError.error("Check Payment For Minimum Amount process Error ", e);
            }
        }
    }

    @Override
    public void addSummaries() {
        summery.put("Cards falling on Due date", Statusts.SUMMARY_FOR_CARDS_ON_DUEDATE + "");
        summery.put("Cards which have not payed the Min Amount and Risk profile added", Statusts.SUMMARY_FOR_MINPAYMENT_RISK_ADDED + "");
        summery.put("Cards which have payed the Min Amount", Statusts.SUMMARY_FOR_CARDS_MINAMOUNT_PAID + "");
    }
}
