package com.epic.cms.connector;

import com.epic.cms.common.ProcessBuilder;
import com.epic.cms.model.bean.LoyaltyBean;
import com.epic.cms.repository.CommonRepo;
import com.epic.cms.repository.LoyaltyPointsCalculationRepo;
import com.epic.cms.service.LoyaltyPointsCalculationService;
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
public class LoyaltyPointsCalculationConnector extends ProcessBuilder {

    @Autowired
    @Qualifier("ThreadPool_100")
    ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    LogManager logManager;

    @Autowired
    CommonRepo commonRepo;

    @Autowired
    StatusVarList statusList;

    @Autowired
    LoyaltyPointsCalculationRepo loyaltyPointsCalculationRepo;

    @Autowired
    LoyaltyPointsCalculationService loyaltyPointsCalculationService;
<<<<<<< Updated upstream
=======

    int capacity = 200000;
    BlockingQueue<Integer> successCount = new ArrayBlockingQueue<Integer>(capacity);
    BlockingQueue<Integer> failCount = new ArrayBlockingQueue<Integer>(capacity);
>>>>>>> Stashed changes
    private static final Logger logInfo = LoggerFactory.getLogger("logInfo");
    private static final Logger logError = LoggerFactory.getLogger("logError");


    @Override
    public void concreteProcess() throws Exception {
        ArrayList<LoyaltyBean> cardList = null;
        int noOfAccounts = 0;
        int failedAccounts = 0;

        try {
            logInfo.info(logManager.logStartEnd("Loyalty Points Calculation Process Started"));

            Configurations.RUNNING_PROCESS_ID = Configurations.PROCESS_ID_LOYALTY_POINT_CALCULATION_PROCESS;
            CommonMethods.eodDashboardProgressParametersReset();

            cardList = loyaltyPointsCalculationRepo.getTodayBillingCardSet(Configurations.EOD_DATE);
            Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS = cardList.size();


            loyaltyPointsCalculationRepo.getLoyaltyConfigurations();

            if (cardList.size() > 0) {
//                for (LoyaltyBean loyaltyBean : cardList) {
//                    loyaltyPointsCalculationService.calculateLoyaltyPoints(loyaltyBean);
//                }

                cardList.forEach(loyaltyBean -> {
<<<<<<< Updated upstream
                    loyaltyPointsCalculationService.calculateLoyaltyPoints(loyaltyBean,Configurations.successCount,Configurations.failCount);
=======
                    loyaltyPointsCalculationService.calculateLoyaltyPoints(loyaltyBean,successCount,failCount);
>>>>>>> Stashed changes
                });

                //wait till all the threads are completed
                while (!(taskExecutor.getActiveCount() == 0)) {
                    Thread.sleep(1000);
                }

                Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS = (noOfAccounts);
                Configurations.PROCESS_SUCCESS_COUNT = (noOfAccounts - failedAccounts);
                Configurations.PROCESS_FAILD_COUNT = (failedAccounts);
            }

        } catch (Exception e) {
            throw e;
        }finally {
            /* PADSS Change -
            variables handling card data should be nullified by replacing the value of variable with zero and call NULL function */
            try {
                if (cardList != null && cardList.size() != 0) {
                    for (LoyaltyBean loyaltyBean : cardList) {
                        CommonMethods.clearStringBuffer(loyaltyBean.getCardNo());
                    }
                    cardList = null;
                }
            } catch (Exception e) {
                logError.error(String.valueOf(e));
            }
        }
    }

    @Override
    public void addSummaries() {

    }
}
