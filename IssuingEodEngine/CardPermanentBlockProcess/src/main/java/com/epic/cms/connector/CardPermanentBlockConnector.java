package com.epic.cms.connector;

import com.epic.cms.common.ProcessBuilder;
import com.epic.cms.model.bean.BlockCardBean;
import com.epic.cms.model.bean.ProcessBean;
import com.epic.cms.repository.CardBlockRepo;
import com.epic.cms.repository.CommonRepo;
import com.epic.cms.service.CardPermanentBlockService;
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


@Service
public class CardPermanentBlockConnector extends ProcessBuilder {

    @Autowired
    LogManager logManager;

    @Autowired
    @Qualifier("taskExecutor2")
    ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    CommonRepo commonRepo;

    @Autowired
    StatusVarList statusList;

    @Autowired
    CardPermanentBlockService cardPermanentBlockService;

    @Autowired
    CardBlockRepo cardPermanentBlockRepo;

    private static final Logger logInfo = LoggerFactory.getLogger("logInfo");
    private static final Logger logError = LoggerFactory.getLogger("logError");

    ArrayList<BlockCardBean> cardList = null;
    ProcessBean processBean = new ProcessBean();
    private int failedCount = 0;

    @Override
    public void concreteProcess() throws Exception {
        try {
            Configurations.RUNNING_PROCESS_ID = Configurations.PROCESS_ID_CARD_PERMENANT_BLOCK;
            CommonMethods.eodDashboardProgressParametersReset();
            processBean = commonRepo.getProcessDetails(Configurations.PROCESS_ID_CARD_PERMENANT_BLOCK);

            if (processBean != null) {
                Configurations.NO_OF_MONTHS_FOR_PERMENANT_BLOCK = cardPermanentBlockRepo.getBlockTheshholdPeriod("PERMENANTBLKTHRESHOLD");
                cardList = cardPermanentBlockRepo.getCardListFromMinPayment(statusList.getCARD_TEMPORARY_BLOCK_Status(), Configurations.NO_OF_MONTHS_FOR_PERMENANT_BLOCK); //CATB

                if (cardList != null && cardList.size() > 0) {

                    for (BlockCardBean blockCardBean : cardList) {
                        cardPermanentBlockService.processCardPermanentBlock(blockCardBean, processBean);
                    }
                }

                while (!(taskExecutor.getActiveCount() == 0)) {
                    Thread.sleep(1000);
                }

                failedCount = Configurations.PROCESS_FAILD_COUNT;
                Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS = cardList.size();
                Configurations.PROCESS_SUCCESS_COUNT = (cardList.size() - failedCount);
                Configurations.PROCESS_FAILD_COUNT = failedCount;

            }
        } catch (Exception e) {
            Configurations.IS_PROCESS_COMPLETELY_FAILED = true;
            logError.error("Card Permanent Block process Error", e);
        } finally {
            logInfo.info(logManager.logSummery(summery));
            try {
                /* PADSS Change -
                variables handling card data should be nullified by replacing the value of variable with zero and call NULL function */
                if (cardList != null && cardList.size() != 0) {
                    for (BlockCardBean blockCardBean : cardList) {
                        CommonMethods.clearStringBuffer(blockCardBean.getCardNo());
                    }
                    cardList = null;
                }
            } catch (Exception e2) {
                logError.error("Card Permanent Block process Error ", e2);
            }
        }
    }

    @Override
    public void addSummaries() {

            summery.put("Started Date", Configurations.EOD_DATE.toString());
            summery.put("No of Card effected", Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS);
            summery.put("No of Success Card", Configurations.PROCESS_SUCCESS_COUNT);
            summery.put("No of fail Card", Configurations.PROCESS_FAILD_COUNT);
    }
}
