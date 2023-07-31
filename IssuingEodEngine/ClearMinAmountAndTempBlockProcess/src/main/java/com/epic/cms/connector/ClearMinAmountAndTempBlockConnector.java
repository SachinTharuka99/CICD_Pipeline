package com.epic.cms.connector;

import com.epic.cms.common.ProcessBuilder;
import com.epic.cms.model.bean.LastStatementSummeryBean;
import com.epic.cms.repository.LastStatementSummaryRepo;
import com.epic.cms.service.ClearMinAmountAndTempBlockService;
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
public class ClearMinAmountAndTempBlockConnector extends ProcessBuilder {

    @Autowired
    LogManager logManager;

    @Autowired
    @Qualifier("ThreadPool_100")
    ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    StatusVarList statusVarList;

    @Autowired
    LastStatementSummaryRepo lastStatementSummaryRepo;

    @Autowired
    ClearMinAmountAndTempBlockService clearMinAmountAndTempBlockService;
<<<<<<< Updated upstream
=======
    int capacity = 200000;
    BlockingQueue<Integer> successCount = new ArrayBlockingQueue<Integer>(capacity);
    BlockingQueue<Integer> failCount = new ArrayBlockingQueue<Integer>(capacity);
>>>>>>> Stashed changes
    List<LastStatementSummeryBean> cardList = new ArrayList<>();

    private static final Logger logInfo = LoggerFactory.getLogger("logInfo");
    private static final Logger logError = LoggerFactory.getLogger("logError");
    @Override
    public void concreteProcess() throws Exception {
        StringBuffer cardNo = null;
        try {
            Configurations.RUNNING_PROCESS_ID = Configurations.PROCESS_CLEAR_MINPAYMENTS_AND_TEMPBLOCK;
            CommonMethods.eodDashboardProgressParametersReset();
            cardList = lastStatementSummaryRepo.getStatementCardList();
            Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS  = cardList.size();

            summery.put("Checking cards for min payment", cardList.size() + "");

//            for (LastStatementSummeryBean lastStatement : cardList) {
//                clearMinAmountAndTempBlockService.processClearMinAmountAndTempBlock(lastStatement);
//            }
            cardList.forEach(lastStatement-> {
<<<<<<< Updated upstream
                clearMinAmountAndTempBlockService.processClearMinAmountAndTempBlock(lastStatement,Configurations.successCount,Configurations.failCount);
=======
                clearMinAmountAndTempBlockService.processClearMinAmountAndTempBlock(lastStatement,successCount,failCount);
>>>>>>> Stashed changes
            });
            while (!(taskExecutor.getActiveCount() == 0)) {
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            Configurations.IS_PROCESS_COMPLETELY_FAILED = true;
            logError.error("Failed Clear Min Amount & Temp Block Process ", e);

        } finally {
            try {
                if (cardList != null && cardList.size() != 0) {
                    for (LastStatementSummeryBean lastStatement : cardList) {
                        CommonMethods.clearStringBuffer(new StringBuffer(lastStatement.getCardno()));
                    }
                    cardList = null;
                }
            } catch (Exception e) {
                logError.error("Exception Occurred for Clear Min Amount & Temp Block Process ", e);
            }
        }
    }

    @Override
    public void addSummaries() {
            summery.put("Started Date", Configurations.EOD_DATE.toString());
            summery.put("Number of transaction Cards Count", Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS);
<<<<<<< Updated upstream
            summery.put("Number of success Cards Count", Configurations.successCount.size());
            summery.put("Number of failure Cards Count", Configurations.failCount.size());
=======
            summery.put("Number of success Cards Count", successCount.size());
            summery.put("Number of failure Cards Count", failCount.size());
>>>>>>> Stashed changes
    }
}
