package com.epic.cms.connector;

import com.epic.cms.common.ProcessBuilder;
import com.epic.cms.model.bean.CardBean;
import com.epic.cms.repository.CardExpireRepo;
import com.epic.cms.service.CardExpireService;
import com.epic.cms.util.CommonMethods;
import com.epic.cms.util.Configurations;
import com.epic.cms.util.LogManager;
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
public class CardExpireConnector extends ProcessBuilder {

    @Autowired
    @Qualifier("ThreadPool_100")
    ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    CardExpireService cardExpireService;

    @Autowired
    CardExpireRepo cardExpireRepo;

    @Autowired
    LogManager logManager;

    private static final Logger logError = LoggerFactory.getLogger("logError");
<<<<<<< Updated upstream
=======
    int capacity = 200000;
    BlockingQueue<Integer> successCount = new ArrayBlockingQueue<Integer>(capacity);
    BlockingQueue<Integer> failCount = new ArrayBlockingQueue<Integer>(capacity);
>>>>>>> Stashed changes
    private ArrayList<CardBean> expiredCardList = new ArrayList<>();


    @Override
    public void concreteProcess() throws Exception {
        /**
         * Get the card list where EXPIRY DATE is less than the eod date and the
         * status are not in expired, closed as well as card replaced
         */
        try {
            Configurations.RUNNING_PROCESS_ID = Configurations.PROCESS_CARD_EXPIRE;
            CommonMethods.eodDashboardProgressParametersReset();

            expiredCardList = cardExpireRepo.getExpiredCardList();
            if (expiredCardList != null) {
                Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS = expiredCardList.size();

                expiredCardList.forEach(cardBean -> {
<<<<<<< Updated upstream
                    cardExpireService.processCardExpire(cardBean,Configurations.successCount,Configurations.failCount);
=======
                    cardExpireService.processCardExpire(cardBean,successCount,failCount);
>>>>>>> Stashed changes
                });
                //wait till all the threads are completed
                while (!(taskExecutor.getActiveCount() == 0)) {
                    Thread.sleep(1000);
                }
            }
            Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS = expiredCardList.size();
        } catch (Exception ex) {
            Configurations.IS_PROCESS_COMPLETELY_FAILED = true;
            throw ex;
        } finally {
            try {
                if (expiredCardList != null && expiredCardList.size() != 0) {
                    /** PADSS Change -
                     variables handling card data should be nullified by replacing the value of variable with zero and call NULL function */
                    for (CardBean cardBean : expiredCardList) {
                        CommonMethods.clearStringBuffer(cardBean.getCardnumber());
                        CommonMethods.clearStringBuffer(cardBean.getMainCardNo());
                    }
                }
            } catch (Exception e) {
                logError.error(String.valueOf(e));
            }
        }
    }

    @Override
    public void addSummaries() {
        summery.put("Number of cards to expired ", Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS);
<<<<<<< Updated upstream
        summery.put("Number of success expired ", Configurations.successCount.size());
        summery.put("Number of failure expired ", Configurations.failCount.size());
=======
        summery.put("Number of success expired ", successCount.size());
        summery.put("Number of failure expired ", failCount.size());
>>>>>>> Stashed changes
    }
}
