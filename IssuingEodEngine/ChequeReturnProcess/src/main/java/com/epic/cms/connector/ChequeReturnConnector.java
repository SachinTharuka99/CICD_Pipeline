package com.epic.cms.connector;

import com.epic.cms.common.ProcessBuilder;
import com.epic.cms.model.bean.ReturnChequePaymentDetailsBean;
import com.epic.cms.repository.ChequeReturnRepo;
import com.epic.cms.repository.CommonRepo;
import com.epic.cms.service.ChequeReturnService;
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class ChequeReturnConnector extends ProcessBuilder {

    private static final Logger logInfo = LoggerFactory.getLogger("logInfo");
    private static final Logger logError = LoggerFactory.getLogger("logError");
<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
    @Autowired
    LogManager logManager;
    @Autowired
    CommonRepo commonRepo;
    @Autowired
    @Qualifier("taskExecutor2")
    ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    ChequeReturnService chequeReturnService;
    @Autowired
    ChequeReturnRepo chequeReturnRepo;
    Map<StringBuffer, List<ReturnChequePaymentDetailsBean>> totalChequeReturnsList = new HashMap<StringBuffer, List<ReturnChequePaymentDetailsBean>>();
    private int failedCount = 0;

    @Override
    public void concreteProcess() {
        try {
            StringBuffer cardNo = null;
            Configurations.RUNNING_PROCESS_ID = Configurations.PROCESS_CHEQUERETURN;
            CommonMethods.eodDashboardProgressParametersReset();
            //update cheque returns
            chequeReturnService.updateChequeReturns();

            //Get all the entries in the chequepayment table for status as returned. Need cheque returned status cycle.
            totalChequeReturnsList = chequeReturnRepo.returnChequePaymentDetails();

            //Iterate over the entry set over a card.
            Iterator it = totalChequeReturnsList.entrySet().iterator();
            if (!totalChequeReturnsList.isEmpty()) {
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    chequeReturnService.proceedChequeReturn(pair);
                }

                //wait till all the threads are completed
                while (!(taskExecutor.getActiveCount() == 0)) {
                    Thread.sleep(1000);
                }

                failedCount = Configurations.PROCESS_FAILD_COUNT;
                Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS = totalChequeReturnsList.size();
                Configurations.PROCESS_SUCCESS_COUNT = (totalChequeReturnsList.size() - failedCount);
                Configurations.PROCESS_FAILD_COUNT = failedCount;

            } else {
                summery.put("cheque Returns not found", 0);
            }
        } catch (Exception ex) {
            Configurations.IS_PROCESS_COMPLETELY_FAILED = true;
            logError.error("Cheque Return process Error", ex);
        } finally {
            logInfo.info(logManager.logSummery(summery));
             /* PADSS Change -
               variables handling card data should be nullified by replacing the value of variable with zero and call NULL function */
            totalChequeReturnsList.clear();
        }
    }

    @Override
    public void addSummaries() {

        summery.put("Started Date", Configurations.EOD_DATE.toString());
        summery.put("Number of transaction to sync", Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS);
        summery.put("Number of success transaction", Configurations.PROCESS_SUCCESS_COUNT);
        summery.put("Number of failure transaction", Configurations.PROCESS_FAILD_COUNT);

    }
}
