package com.epic.cms.connector;

import com.epic.cms.common.ProcessBuilder;
import com.epic.cms.model.bean.ReturnChequePaymentDetailBean;
import com.epic.cms.repository.ChequePaymentRepo;
import com.epic.cms.service.ChequePaymentService;
import com.epic.cms.util.CommonMethods;
import com.epic.cms.util.Configurations;
import com.epic.cms.util.LogManager;
import com.epic.cms.util.Statusts;
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
public class ChequePaymentConnector extends ProcessBuilder {
    int capacity = 200000;
    BlockingQueue<Integer> successCount = new ArrayBlockingQueue<Integer>(capacity);
    BlockingQueue<Integer> failCount = new ArrayBlockingQueue<Integer>(capacity);
    private static final Logger logInfo = LoggerFactory.getLogger("logInfo");
    private static final Logger logError = LoggerFactory.getLogger("logError");

    /**
     * This process syncs the cheque payments and backs up all the current EOD sales,
     * charges and payments.
     */

    @Autowired
    LogManager logManager;
    @Autowired
    @Qualifier("ThreadPool_100")
    ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    ChequePaymentRepo chequePaymentRepo;
    @Autowired
    ChequePaymentService chequePaymentService;

    @Override
    public void concreteProcess() throws Exception {
        //Get a backup of cheque payments made and insert into Cheque payment Table.
        List<ReturnChequePaymentDetailBean> chqList = new ArrayList<>();
        try {
            Configurations.RUNNING_PROCESS_ID = Configurations.PROCESS_ID_CHEQUEPAYMENT;
            CommonMethods.eodDashboardProgressParametersReset();
            chqList = chequePaymentRepo.getChequePaymentsBackup();
            Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS = chqList.size();

            try {
                if (CommonMethods.getFailedCardsFromDB().size() > 0) {
                    /**
                     * If any card failed while in looping the db tables, get those
                     * cards from the backend and add it to the errorList.
                     */
                    Configurations.errorCardList.addAll(CommonMethods.getFailedCardsFromDB());
                    //Finally remove the cards in the common Failed Card List.
                    CommonMethods.resetFailedCardList();
                }

                if (chqList.size() > 0) {
//                    for (ReturnChequePaymentDetailBean bean : chqList) {
//                        chequePaymentService.processChequePayment(bean);
//                    }
                    chqList.forEach(bean->  {
<<<<<<< Updated upstream
                        chequePaymentService.processChequePayment(bean,Configurations.successCount,Configurations.failCount);
=======
                        chequePaymentService.processChequePayment(bean,successCount,failCount);
>>>>>>> Stashed changes
                    });

                }
            } catch (Exception e) {
                logError.error("Failed Cheque Payment Process ", e);
                throw e;
            }
        } catch (Exception e) {
            Configurations.IS_PROCESS_COMPLETELY_FAILED = true;
            logError.error("Failed Cheque Payment Process Exception ", e);
        } finally {
            //logInfo.info(logManager.logSummery(summery));
            try {
                if (chqList != null && chqList.size() != 0) {
                    /** PADSS Change -
                     variables handling card data should be nullified by replacing the value of variable with zero and call NULL function */
                    for (ReturnChequePaymentDetailBean bean : chqList) {
                        CommonMethods.clearStringBuffer(bean.getCardnumber());
                        CommonMethods.clearStringBuffer(bean.getMaincardno());
                        CommonMethods.clearStringBuffer(bean.getOldcardnumber());
                    }
                    chqList = null;
                }
            } catch (Exception e) {
                logError.error("Cheque Payment Process Exception ", e);
            }
        }
    }

    @Override
    public void addSummaries() {
//        summery.put("Cheque Payments Processed", Statusts.SUMMARY_FOR_CHEQUE_PAYMENTS + "");
//        summery.put("No of fail Cheque Payments ", Configurations.PROCESS_FAILD_COUNT);

        summery.put("Cheque Payments Processed",  Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS + "");
<<<<<<< Updated upstream
        summery.put("No of Success Cheque Payments",  Configurations.successCount.size() + "");
        summery.put("No of fail Cheque Payments ", Configurations.failCount.size());
=======
        summery.put("No of Success Cheque Payments",  successCount.size() + "");
        summery.put("No of fail Cheque Payments ", failCount.size());
>>>>>>> Stashed changes
    }
}
