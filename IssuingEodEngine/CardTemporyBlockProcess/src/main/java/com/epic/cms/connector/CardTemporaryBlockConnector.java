package com.epic.cms.connector;

import com.epic.cms.common.ProcessBuilder;
import com.epic.cms.model.bean.BlockCardBean;
import com.epic.cms.model.bean.ProcessBean;
import com.epic.cms.repository.CardBlockRepo;
import com.epic.cms.repository.CommonRepo;
import com.epic.cms.service.CardTemporaryBlockService;
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
public class CardTemporaryBlockConnector extends ProcessBuilder {

    private static final Logger logInfo = LoggerFactory.getLogger("logInfo");
    private static final Logger logError = LoggerFactory.getLogger("logError");
    @Autowired
    @Qualifier("taskExecutor2")
    ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    CommonRepo commonRepo;
    @Autowired
    StatusVarList statusList;
    @Autowired
    CardBlockRepo cardTemporaryBlockRepo;
    @Autowired
    CardTemporaryBlockService cardTemporaryBlockService;
    @Autowired
    LogManager logManager;
    ArrayList<BlockCardBean> cardList = null;
    ProcessBean processBean = new ProcessBean();
    private int failedCount = 0;

    @Override
    public void concreteProcess() throws Exception {
        try {
            Configurations.RUNNING_PROCESS_ID = Configurations.PROCESS_ID_CARD_TEMPORARY_BLOCK;
            CommonMethods.eodDashboardProgressParametersReset();
            processBean = commonRepo.getProcessDetails(Configurations.PROCESS_ID_CARD_TEMPORARY_BLOCK);

            if (processBean != null) {
                Configurations.NO_OF_MONTHS_FOR_PERMENANT_BLOCK = cardTemporaryBlockRepo.getBlockTheshholdPeriod("TEMPORARYBLKTHRESHOLD");
                cardList = cardTemporaryBlockRepo.getCardListFromMinPayment(Configurations.EOD_PENDING_STATUS, Configurations.NO_OF_MONTHS_FOR_TEMPORARY_BLOCK);

                if (cardList != null && cardList.size() > 0) {
                    for (BlockCardBean blockCardBean : cardList) {
                        cardTemporaryBlockService.processCardTemporaryBlock(blockCardBean, processBean);
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
        } catch (Exception ex) {
            Configurations.IS_PROCESS_COMPLETELY_FAILED = true;
            throw ex;
        } finally {
            logInfo.info(logManager.logSummery(summery));
            try {
                /** PADSS Change -
                 variables handling card data should be nullified by replacing the value of variable with zero and call NULL function */
                if (cardList != null && cardList.size() != 0) {
                    for (BlockCardBean temporyBlockCardBean : cardList) {
                        CommonMethods.clearStringBuffer(temporyBlockCardBean.getCardNo());
                    }
                    cardList = null;
                }
            } catch (Exception e2) {
                logError.error("Card Temporary Block process Error ", e2);
            }
        }
    }

    public void addSummaries() {

        summery.put("Started Date", Configurations.EOD_DATE.toString());
        summery.put("No of Card effected", Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS);
        summery.put("No of Success Card", Configurations.PROCESS_SUCCESS_COUNT);
        summery.put("No of fail Card", Configurations.PROCESS_FAILD_COUNT);

    }
}
