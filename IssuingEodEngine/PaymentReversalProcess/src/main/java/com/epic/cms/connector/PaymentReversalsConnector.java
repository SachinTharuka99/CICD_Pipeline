package com.epic.cms.connector;

import com.epic.cms.common.ProcessBuilder;
import com.epic.cms.model.bean.PaymentBean;
import com.epic.cms.repository.CommonRepo;
import com.epic.cms.repository.PaymentReversalRepo;
import com.epic.cms.service.PaymentReversalService;
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

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PaymentReversalsConnector extends ProcessBuilder {
    @Autowired
    LogManager logManager;

    @Autowired
    @Qualifier("taskExecutor2")
    ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    StatusVarList status;

    @Autowired
    CommonRepo commonRepo;

    @Autowired
    PaymentReversalService paymentReversalService;

    @Autowired
    PaymentReversalRepo paymentReversalRepo;
<<<<<<< Updated upstream
=======
    int capacity = 200000;
    BlockingQueue<Integer> successCount = new ArrayBlockingQueue<Integer>(capacity);
    BlockingQueue<Integer> failCount = new ArrayBlockingQueue <Integer>(capacity);
>>>>>>> Stashed changes
    private static final Logger logInfo = LoggerFactory.getLogger("logInfo");
    private static final Logger logError = LoggerFactory.getLogger("logError");
    private List<PaymentBean> paymentReversals = null;

    @Override
    public void concreteProcess() throws Exception {

        /**
         * implement cash reversals credit/debit
         *
         * Get The cash reversal from the payment table for the status CDP#
         * transaction type, then update the status of the original payment as
         * PRVS
         */

        try {
            Configurations.RUNNING_PROCESS_ID = Configurations.PAYMENT_REVERSAL_PROCESS;
            CommonMethods.eodDashboardProgressParametersReset();
            paymentReversals = paymentReversalRepo.getPaymentReversals();

            Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS = paymentReversals.size();

            if (paymentReversals.size() != 0) {
//                for (PaymentBean bean : paymentReversals) {
//                    paymentReversalService.setPaymentReversals(bean);
//                }

                paymentReversals.forEach(bean -> {
<<<<<<< Updated upstream
                    paymentReversalService.setPaymentReversals(bean,Configurations.successCount,Configurations.failCount);
=======
                    paymentReversalService.setPaymentReversals(bean,successCount,failCount);
>>>>>>> Stashed changes

                });
            }
            while (!(taskExecutor.getActiveCount() == 0)) {
                Thread.sleep(1000);
            }
        }catch (Exception e){
            Configurations.IS_PROCESS_COMPLETELY_FAILED = true;
            logError.error("--Error occurred--", e);
        }finally {
            logInfo.info(logManager.logSummery(summery));
            /** PADSS Change -
             variables handling card data should be nullified
             by replacing the value of variable with zero and call NULL function */
            try {
                if (paymentReversals != null && paymentReversals.size() != 0) {
                    for (PaymentBean paymentBean : paymentReversals) {
                        CommonMethods.clearStringBuffer(paymentBean.getCardnumber());
                        CommonMethods.clearStringBuffer(paymentBean.getMaincardno());
                    }
                    paymentReversals = null;
                }
            } catch (Exception e) {
                logError.error("--Error occurred--", e);
            }
        }
    }

    @Override
    public void addSummaries() {
        summery.put("Process Name ", "Payment Reversal");
        summery.put("No Of Payment Reversals awaiting ", Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS);
<<<<<<< Updated upstream
        summery.put("No of Payments successfully reversed ", Configurations.failCount.size());
        summery.put("No of Payments not reversed ", Configurations.failCount.size());
=======
        summery.put("No of Payments successfully reversed ", failCount.size());
        summery.put("No of Payments not reversed ", failCount.size());
>>>>>>> Stashed changes
    }
}
