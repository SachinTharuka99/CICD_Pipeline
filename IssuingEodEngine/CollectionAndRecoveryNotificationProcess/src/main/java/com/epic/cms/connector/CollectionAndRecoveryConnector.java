package com.epic.cms.connector;

import com.epic.cms.common.ProcessBuilder;
import com.epic.cms.model.bean.CollectionAndRecoveryBean;
import com.epic.cms.model.bean.ProcessBean;
import com.epic.cms.repository.CollectionAndRecoveryRepo;
import com.epic.cms.repository.CommonRepo;
import com.epic.cms.service.CollectionAndRecoveryService;
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
public class CollectionAndRecoveryConnector extends ProcessBuilder {
<<<<<<< Updated upstream
    private static final Logger logInfo = LoggerFactory.getLogger("logInfo");
    private static final Logger logError = LoggerFactory.getLogger("logError");
=======
    int capacity = 200000;
    BlockingQueue<Integer> successCount = new ArrayBlockingQueue<Integer>(capacity);
    BlockingQueue<Integer> failCount = new ArrayBlockingQueue<Integer>(capacity);
    private static final Logger logInfo = LoggerFactory.getLogger("logInfo");
    private static final Logger logError = LoggerFactory.getLogger("logError");


>>>>>>> Stashed changes
    @Autowired
    LogManager logManager;
    @Autowired
    @Qualifier("taskExecutor2")
    ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    CollectionAndRecoveryService collectionAndRecoveryService;
    @Autowired
    CollectionAndRecoveryRepo collectionAndRecoveryRepo;
    @Autowired
    StatusVarList status;
    @Autowired
    CommonRepo commonRepo;

    @Override
    public void concreteProcess() throws Exception {

        int noOfCards = 0;
        int failedCards = 0;
        int noOfDays;

        ArrayList<CollectionAndRecoveryBean> cardList = new ArrayList<CollectionAndRecoveryBean>();

        try {
            Configurations.RUNNING_PROCESS_ID = Configurations.COLLECTION_AND_RECOVERY_NOTIFICATION;
            CommonMethods.eodDashboardProgressParametersReset();
            //commonRepo.insertToEodProcessSumery(Configurations.COLLECTION_AND_RECOVERY_NOTIFICATION, processBean.getEodmodule());

            processBean = new ProcessBean();
            processBean = commonRepo.getProcessDetails(151);

            if (processBean != null) {
                logInfo.info(logManager.logHeader("Collection and recovery notification Process Started"));

                /**Select the X date before Due Date card set*/
                noOfDays = collectionAndRecoveryRepo.getNoOfDaysOnTriggerPoint(Configurations.TP_X_DATES_BEFORE_FIRST_DUE_DATE);
                cardList = collectionAndRecoveryRepo.getCardListForCollectionAndRecoveryOnDueDate(noOfDays, 1, null);
                Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS += cardList.size();

                if (cardList.size() > 0) {
                    logInfo.info("X_DATES_BEFORE_FIRST_DUE_DATE");
//                    for (CollectionAndRecoveryBean collectionAndRecoveryBean : cardList) {
//                        collectionAndRecoveryService.processX_DATES_BEFORE_FIRST_DUE_DATE(collectionAndRecoveryBean, processBean);
//                    }
                    cardList.forEach(collectionAndRecoveryBean->{
<<<<<<< Updated upstream
                     collectionAndRecoveryService.processX_DATES_BEFORE_FIRST_DUE_DATE(collectionAndRecoveryBean, processBean,Configurations.successCount,Configurations.failCount);
=======
                     collectionAndRecoveryService.processX_DATES_BEFORE_FIRST_DUE_DATE(collectionAndRecoveryBean, processBean,successCount,failCount);
>>>>>>> Stashed changes
                    });

                    while (!(taskExecutor.getActiveCount() == 0)) {
                        Thread.sleep(1000);
                    }
                    cardList.clear();
                }

                /**Select x days After first Due Date card set*/
                noOfDays = collectionAndRecoveryRepo.getNoOfDaysOnTriggerPoint(Configurations.TP_X_DATES_AFTER_FIRST_DUE_DATE);
                cardList = collectionAndRecoveryRepo.getCardListForCollectionAndRecoveryOnDueDate(noOfDays, 2, Configurations.TP_X_DATES_BEFORE_FIRST_DUE_DATE);
                Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS += cardList.size();
                if (cardList.size() > 0) {
                    logInfo.info("X_DATES_AFTER_FIRST_DUE_DATE");
//                    for (CollectionAndRecoveryBean collectionAndRecoveryBean : cardList) {
//                        collectionAndRecoveryService.processX_DATES_AFTER_FIRST_DUE_DATE(collectionAndRecoveryBean, processBean);
//                    }
<<<<<<< Updated upstream
                    cardList.forEach(collectionAndRecoveryBean-> collectionAndRecoveryService.processX_DATES_AFTER_FIRST_DUE_DATE(collectionAndRecoveryBean, processBean,Configurations.successCount,Configurations.failCount));
=======
                    cardList.forEach(collectionAndRecoveryBean-> collectionAndRecoveryService.processX_DATES_AFTER_FIRST_DUE_DATE(collectionAndRecoveryBean, processBean,successCount,failCount));
>>>>>>> Stashed changes

                    while (!(taskExecutor.getActiveCount() == 0)) {
                        Thread.sleep(1000);
                    }

                    cardList.clear();
                }

                /**Select on the second statement card set*/
                noOfDays = collectionAndRecoveryRepo.getNoOfDaysOnTriggerPoint(Configurations.TP_X_DATES_AFTER_FIRST_DUE_DATE);
                cardList = collectionAndRecoveryRepo.getCardListForCollectionAndRecoveryOnStatmentDate(noOfDays, 1, Configurations.TP_X_DATES_AFTER_FIRST_DUE_DATE);
                Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS += cardList.size();
                if (cardList.size() > 0) {
                    logInfo.info("ON_THE_2ND_STATEMENT_DATE");
//                    for (CollectionAndRecoveryBean collectionAndRecoveryBean : cardList) {
//                        collectionAndRecoveryService.processON_THE_2ND_STATEMENT_DATE(collectionAndRecoveryBean, processBean);
//                    }

<<<<<<< Updated upstream
                    cardList.forEach(collectionAndRecoveryBean-> collectionAndRecoveryService.processON_THE_2ND_STATEMENT_DATE(collectionAndRecoveryBean, processBean,Configurations.successCount,Configurations.failCount));
=======
                    cardList.forEach(collectionAndRecoveryBean-> collectionAndRecoveryService.processON_THE_2ND_STATEMENT_DATE(collectionAndRecoveryBean, processBean,successCount,failCount));
>>>>>>> Stashed changes

                    while (!(taskExecutor.getActiveCount() == 0)) {
                        Thread.sleep(1000);
                    }
                    cardList.clear();
                }

                /**Select x days after second statement card set*/
                noOfDays = collectionAndRecoveryRepo.getNoOfDaysOnTriggerPoint(Configurations.TP_X_DAYS_AFTER_THE_2ND_STATEMENT_DATE);
                cardList = collectionAndRecoveryRepo.getCardListForCollectionAndRecoveryOnStatmentDate(noOfDays, 2, Configurations.TP_ON_THE_2ND_STATEMENT_DATE);
                Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS += cardList.size();
                if (cardList.size() > 0) {
                    logInfo.info("X_DATES_AFTER_SECOND_STATEMENT");
//                    for (CollectionAndRecoveryBean collectionAndRecoveryBean : cardList) {
//                        collectionAndRecoveryService.processX_DATES_AFTER_SECOND_STATEMENT(collectionAndRecoveryBean, processBean);
//                    }
<<<<<<< Updated upstream
                    cardList.forEach(collectionAndRecoveryBean -> collectionAndRecoveryService.processX_DATES_AFTER_SECOND_STATEMENT(collectionAndRecoveryBean, processBean,Configurations.successCount,Configurations.failCount));
=======
                    cardList.forEach(collectionAndRecoveryBean -> collectionAndRecoveryService.processX_DATES_AFTER_SECOND_STATEMENT(collectionAndRecoveryBean, processBean,successCount,failCount));
>>>>>>> Stashed changes


                    while (!(taskExecutor.getActiveCount() == 0)) {
                        Thread.sleep(1000);
                    }
                    cardList.clear();
                }

                /**Select x days After second Due Date card set*/
                noOfDays = collectionAndRecoveryRepo.getNoOfDaysOnTriggerPoint(Configurations.TP_IMMEDIATELY_AFTER_THE_2ND_DUE_DATE);
                cardList = collectionAndRecoveryRepo.getCardListForCollectionAndRecoveryOnDueDate(noOfDays, 2, Configurations.TP_X_DAYS_AFTER_THE_2ND_STATEMENT_DATE);
                Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS += cardList.size();
                if (cardList.size() > 0) {
                    logInfo.info("IMMEDIATELY_AFTER_THE_2ND_DUE_DATE");
//                    for (CollectionAndRecoveryBean collectionAndRecoveryBean : cardList) {
//                        collectionAndRecoveryService.processIMMEDIATELY_AFTER_THE_2ND_DUE_DATE(collectionAndRecoveryBean, processBean);
//                    }
<<<<<<< Updated upstream
                    cardList.forEach(collectionAndRecoveryBean-> collectionAndRecoveryService.processIMMEDIATELY_AFTER_THE_2ND_DUE_DATE(collectionAndRecoveryBean, processBean,Configurations.successCount,Configurations.failCount));
=======
                    cardList.forEach(collectionAndRecoveryBean-> collectionAndRecoveryService.processIMMEDIATELY_AFTER_THE_2ND_DUE_DATE(collectionAndRecoveryBean, processBean,successCount,failCount));
>>>>>>> Stashed changes


                    while (!(taskExecutor.getActiveCount() == 0)) {
                        Thread.sleep(1000);
                    }
                    cardList.clear();
                }

                /**Select on the third statement card set*/
                noOfDays = collectionAndRecoveryRepo.getNoOfDaysOnTriggerPoint(Configurations.TP_ON_THE_3RD_STATEMENT_DATE);
                cardList = collectionAndRecoveryRepo.getCardListForCollectionAndRecoveryOnStatmentDate(noOfDays, 1, Configurations.TP_IMMEDIATELY_AFTER_THE_2ND_DUE_DATE);
                Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS += cardList.size();
                if (cardList.size() > 0) {
                    logInfo.info("ON_THE_3RD_STATEMENT_DATE");
//                    for (CollectionAndRecoveryBean collectionAndRecoveryBean : cardList) {
//                        collectionAndRecoveryService.processON_THE_3RD_STATEMENT_DATE(collectionAndRecoveryBean, processBean);
//                    }
<<<<<<< Updated upstream
                    cardList.forEach(collectionAndRecoveryBean-> collectionAndRecoveryService.processON_THE_3RD_STATEMENT_DATE(collectionAndRecoveryBean, processBean,Configurations.successCount,Configurations.failCount));
=======
                    cardList.forEach(collectionAndRecoveryBean-> collectionAndRecoveryService.processON_THE_3RD_STATEMENT_DATE(collectionAndRecoveryBean, processBean,successCount,failCount));
>>>>>>> Stashed changes


                    while (!(taskExecutor.getActiveCount() == 0)) {
                        Thread.sleep(1000);
                    }
                    cardList.clear();
                }

                /**Select x days After third Due Date card set*/
                noOfDays = collectionAndRecoveryRepo.getNoOfDaysOnTriggerPoint(Configurations.TP_IMMEDIATELY_AFTER_THE_3RD_DUE_DATE);
                cardList = collectionAndRecoveryRepo.getCardListForCollectionAndRecoveryOnDueDate(noOfDays, 2, Configurations.TP_ON_THE_3RD_STATEMENT_DATE);
                Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS += cardList.size();
                if (cardList.size() > 0) {
                    logInfo.info("IMMEDIATELY_AFTER_THE_3RD_DUE_DATE");
//                    for (CollectionAndRecoveryBean collectionAndRecoveryBean : cardList) {
//                        collectionAndRecoveryService.processIMMEDIATELY_AFTER_THE_3RD_DUE_DATE(collectionAndRecoveryBean, processBean);
//                    }
<<<<<<< Updated upstream
                    cardList.forEach(collectionAndRecoveryBean-> collectionAndRecoveryService.processIMMEDIATELY_AFTER_THE_3RD_DUE_DATE(collectionAndRecoveryBean, processBean,Configurations.successCount,Configurations.failCount));
=======
                    cardList.forEach(collectionAndRecoveryBean-> collectionAndRecoveryService.processIMMEDIATELY_AFTER_THE_3RD_DUE_DATE(collectionAndRecoveryBean, processBean,successCount,failCount));
>>>>>>> Stashed changes

                    while (!(taskExecutor.getActiveCount() == 0)) {
                        Thread.sleep(1000);
                    }
                    cardList.clear();
                }

                /**Select on the fourth statement card set*/
                noOfDays = collectionAndRecoveryRepo.getNoOfDaysOnTriggerPoint(Configurations.TP_ON_THE_4TH_STATEMENT_DATE);
                cardList = collectionAndRecoveryRepo.getCardListForCollectionAndRecoveryOnStatmentDate(noOfDays, 1, Configurations.TP_IMMEDIATELY_AFTER_THE_3RD_DUE_DATE);
                Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS += cardList.size();
                if (cardList.size() > 0) {
                    logInfo.info("ON_THE_4TH_STATEMENT_DATE");
//                    for (CollectionAndRecoveryBean collectionAndRecoveryBean : cardList) {
//                        collectionAndRecoveryService.processON_THE_4TH_STATEMENT_DATE(collectionAndRecoveryBean, processBean);
//                    }
<<<<<<< Updated upstream
                    cardList.forEach(collectionAndRecoveryBean-> collectionAndRecoveryService.processON_THE_4TH_STATEMENT_DATE(collectionAndRecoveryBean, processBean,Configurations.successCount,Configurations.failCount));
=======
                    cardList.forEach(collectionAndRecoveryBean-> collectionAndRecoveryService.processON_THE_4TH_STATEMENT_DATE(collectionAndRecoveryBean, processBean,successCount,failCount));
>>>>>>> Stashed changes


                    while (!(taskExecutor.getActiveCount() == 0)) {
                        Thread.sleep(1000);
                    }
                    cardList.clear();
                }

                /**Select x days after fourth statement card set*/
                noOfDays = collectionAndRecoveryRepo.getNoOfDaysOnTriggerPoint(Configurations.TP_X_DAYS_AFTER_THE_4TH_STATEMENT_DATE);
                cardList = collectionAndRecoveryRepo.getCardListForCollectionAndRecoveryOnStatmentDate(noOfDays, 2, Configurations.TP_ON_THE_4TH_STATEMENT_DATE);
                Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS += cardList.size();
                if (cardList.size() > 0) {
                    logInfo.info("X_DAYS_AFTER_THE_4TH_STATEMENT_DATE");
//                    for (CollectionAndRecoveryBean collectionAndRecoveryBean : cardList) {
//                        collectionAndRecoveryService.processX_DAYS_AFTER_THE_4TH_STATEMENT_DATE(collectionAndRecoveryBean, processBean);
//                    }
<<<<<<< Updated upstream
                    cardList.forEach(collectionAndRecoveryBean -> collectionAndRecoveryService.processX_DAYS_AFTER_THE_4TH_STATEMENT_DATE(collectionAndRecoveryBean, processBean,Configurations.successCount,Configurations.failCount));
=======
                    cardList.forEach(collectionAndRecoveryBean -> collectionAndRecoveryService.processX_DAYS_AFTER_THE_4TH_STATEMENT_DATE(collectionAndRecoveryBean, processBean,successCount,failCount));
>>>>>>> Stashed changes


                    while (!(taskExecutor.getActiveCount() == 0)) {
                        Thread.sleep(1000);
                    }
                    cardList.clear();
                }

                /**Select x days after and crib file sent fourth statement card set*/
                noOfDays = collectionAndRecoveryRepo.getNoOfDaysOnTriggerPoint(Configurations.TP_WITHIN_X_DAYS_OF_THE_CRIB_INFO_LETTER_REMINDER);
                cardList = collectionAndRecoveryRepo.getCardListForCollectionAndRecoveryOnStatmentDate(noOfDays, 2, Configurations.TP_X_DAYS_AFTER_THE_4TH_STATEMENT_DATE);
                Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS += cardList.size();
                if (cardList.size() > 0) {
                    logInfo.info("WITHIN_X_DAYS_OF_THE_CRIB_INFO_LETTER_REMINDER");
//                    for (CollectionAndRecoveryBean collectionAndRecoveryBean : cardList) {
//                        collectionAndRecoveryService.processWITHIN_X_DAYS_OF_THE_CRIB_INFO_LETTER_REMINDER(collectionAndRecoveryBean, processBean);
//                    }
<<<<<<< Updated upstream
                    cardList.forEach(collectionAndRecoveryBean -> collectionAndRecoveryService.processWITHIN_X_DAYS_OF_THE_CRIB_INFO_LETTER_REMINDER(collectionAndRecoveryBean, processBean,Configurations.successCount,Configurations.failCount));
=======
                    cardList.forEach(collectionAndRecoveryBean -> collectionAndRecoveryService.processWITHIN_X_DAYS_OF_THE_CRIB_INFO_LETTER_REMINDER(collectionAndRecoveryBean, processBean,successCount,failCount));
>>>>>>> Stashed changes

                    while (!(taskExecutor.getActiveCount() == 0)) {
                        Thread.sleep(1000);
                    }
                    cardList.clear();
                }

                /**Select x days After fourth Due Date card set*/
                noOfDays = collectionAndRecoveryRepo.getNoOfDaysOnTriggerPoint(Configurations.TP_IMMEDIATELY_AFTER_THE_4TH_DUE_DATE);
                cardList = collectionAndRecoveryRepo.getCardListForCollectionAndRecoveryOnDueDate(noOfDays, 2, Configurations.TP_WITHIN_X_DAYS_OF_THE_CRIB_INFO_LETTER_REMINDER);
                Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS += cardList.size();
                if (cardList.size() > 0) {
                    logInfo.info("IMMEDIATELY_AFTER_THE_4TH_DUE_DATE");
//                    for (CollectionAndRecoveryBean collectionAndRecoveryBean : cardList) {
//                        collectionAndRecoveryService.processIMMEDIATELY_AFTER_THE_4TH_DUE_DATE(collectionAndRecoveryBean, processBean);
//                    }
<<<<<<< Updated upstream
                    cardList.forEach(collectionAndRecoveryBean-> collectionAndRecoveryService.processIMMEDIATELY_AFTER_THE_4TH_DUE_DATE(collectionAndRecoveryBean, processBean,Configurations.successCount,Configurations.failCount));
=======
                    cardList.forEach(collectionAndRecoveryBean-> collectionAndRecoveryService.processIMMEDIATELY_AFTER_THE_4TH_DUE_DATE(collectionAndRecoveryBean, processBean,successCount,failCount));
>>>>>>> Stashed changes

                    while (!(taskExecutor.getActiveCount() == 0)) {
                        Thread.sleep(1000);
                    }
                    cardList.clear();
                }

                noOfCards = Configurations.noOfCardsForCollectionAndRecoveryNotification;
                failedCards = Configurations.failedCardsForCollectionAndRecoveryNotification;

                Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS = noOfCards;
                Configurations.PROCESS_SUCCESS_COUNT = (noOfCards - failedCards);
                Configurations.PROCESS_FAILD_COUNT = failedCards;
            }
        } catch (Exception e) {
            Configurations.IS_PROCESS_COMPLETELY_FAILED = true;
            logError.error("Collection and recovery process failed", e);
            try {
                assert processBean != null;
                if (processBean.getCriticalStatus() == 1) {
                    Configurations.COMMIT_STATUS = false;
                    Configurations.FLOW_STEP_COMPLETE_STATUS = false;
                    Configurations.PROCESS_FLOW_STEP_COMPLETE_STATUS = false;
                    Configurations.MAIN_EOD_STATUS = false;
                }
            } catch (Exception e2) {
                logError.error("Collection and recovery process ended with", e2);
            }
        } finally {
            try {
                if (cardList != null && cardList.size() != 0) {
                    /**PADSS Change -
                     variables handling card data should be nullified by replacing the value of variable with zero and call NULL function */
                    for (CollectionAndRecoveryBean collectionAndRecoveryBean : cardList) {
                        CommonMethods.clearStringBuffer(collectionAndRecoveryBean.getCardNo());
                    }
                    cardList = null;
                }
            } catch (Exception e2) {
                logError.error("Exception ", e2);
            }
        }
    }

    @Override
    public void addSummaries() {
//        summery.put("Started Date", Configurations.EOD_DATE.toString());
//        summery.put("No of Card effected", Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS);
//        summery.put("No of Success Card ", Configurations.PROCESS_SUCCESS_COUNT);
//        summery.put("No of fail Card ", Configurations.PROCESS_FAILD_COUNT);

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
