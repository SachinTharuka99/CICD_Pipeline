package com.epic.cms.connector;

import com.epic.cms.common.ProcessBuilder;
import com.epic.cms.model.bean.BalanceComponentBean;
import com.epic.cms.model.bean.OtbBean;
import com.epic.cms.repository.CardLimitEnhancementRepo;
import com.epic.cms.repository.CommonRepo;
import com.epic.cms.service.CardLimitEnhancementService;
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
public class CardLimitEnhancementConnector extends ProcessBuilder {

    private static final Logger logInfo = LoggerFactory.getLogger("logInfo");
    private static final Logger logError = LoggerFactory.getLogger("logError");
    @Autowired
    @Qualifier("taskExecutor2")
    ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    StatusVarList statusList;
    @Autowired
    CardLimitEnhancementService cardLimitEnhancementService;
    @Autowired
    CardLimitEnhancementRepo cardLimitEnhancementRepo;
    @Autowired
    CommonRepo commonRepo;
    @Autowired
    LogManager logManager;
    ArrayList<BalanceComponentBean> enhancementList;
    private ArrayList<OtbBean> custAccList = new ArrayList<OtbBean>();
<<<<<<< Updated upstream
=======
    private int failedCount = 0;
    int capacity = 200000;
    BlockingQueue<Integer> successCount = new ArrayBlockingQueue<Integer>(capacity);
    BlockingQueue<Integer> failCount = new ArrayBlockingQueue<Integer>(capacity);

>>>>>>> Stashed changes

    @Override
    public void concreteProcess() throws Exception {

        try {
            if (Configurations.STARTING_EOD_STATUS.equals(statusList.getINITIAL_STATUS())) {
                custAccList = cardLimitEnhancementRepo.getInitLimitEnhanceCustAcc();
            } else if (Configurations.STARTING_EOD_STATUS.equals(statusList.getERROR_STATUS())) {
                custAccList = cardLimitEnhancementRepo.getErrorLimitEnhanceCustAcc();
            }
            Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS = custAccList.size();

            if (custAccList != null && custAccList.size() > 0) {
                Configurations.RUNNING_PROCESS_ID = Configurations.PROCESS_LIMIT_ENHANCEMENT;
                CommonMethods.eodDashboardProgressParametersReset();

                summery.put("Accounts eligible for limit enhance process: ", custAccList.size() + "");
                enhancementList = new ArrayList<>();

                custAccList.forEach(bean -> {
                    enhancementList = cardLimitEnhancementRepo.getLimitEnhanceReqConCardList(bean.getCustomerid(), bean.getAccountnumber());
<<<<<<< Updated upstream
                    cardLimitEnhancementService.processCardLimitEnhancement(enhancementList, bean,Configurations.successCount,Configurations.failCount);
=======
                    cardLimitEnhancementService.processCardLimitEnhancement(enhancementList, bean,successCount,failCount);
>>>>>>> Stashed changes
                });

                //wait till all the threads are completed
                while (!(taskExecutor.getActiveCount() == 0)) {
                    Thread.sleep(1000);
                }

            } else {
                summery.put("Accounts eligible for fee posting process ", 0 + "");
            }
        } catch (Exception ex) {
            Configurations.IS_PROCESS_COMPLETELY_FAILED = true;
            throw ex;
        } finally {
            try {
                if (custAccList != null && custAccList.size() != 0) {
                    for (OtbBean bean : custAccList) {
                        CommonMethods.clearStringBuffer(bean.getCardnumber());
                        CommonMethods.clearStringBuffer(bean.getMaincardno());
                    }
                    custAccList = null;
                }

                if (enhancementList != null && enhancementList.size() != 0) {
                    for (BalanceComponentBean componentBean : enhancementList) {
                        CommonMethods.clearStringBuffer(componentBean.getCardNumber());
                    }
                    enhancementList = null;
                }
            } catch (Exception e) {
                logError.error("Exception Occurred for Card Limit Enhancement ", e);
            }
        }
    }

    @Override
    public void addSummaries() {

        summery.put("Number of transaction to sync", Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS);
<<<<<<< Updated upstream
        summery.put("Number of success transaction", Configurations.successCount.size());
        summery.put("Number of failure transaction", Configurations.failCount.size());
=======
        summery.put("Number of success transaction", successCount.size());
        summery.put("Number of failure transaction", failCount.size());
>>>>>>> Stashed changes

    }
}
