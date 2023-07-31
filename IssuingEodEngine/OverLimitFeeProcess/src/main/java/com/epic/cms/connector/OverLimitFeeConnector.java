package com.epic.cms.connector;

import com.epic.cms.common.ProcessBuilder;
import com.epic.cms.model.bean.ProcessBean;
import com.epic.cms.repository.CommonRepo;
import com.epic.cms.repository.OverLimitFeeRepo;
import com.epic.cms.service.OverLimitFeeService;
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class OverLimitFeeConnector extends ProcessBuilder {

    /********************************************************************************
     *  This is the process of OverLimit interest calculation and update fee count.  *
     *  update fee count is optional                                                *
     *                                                                              *
     * If there is existing card list which has over limited for particular day       *
     *                                                                              *
     *  1.update card fee count                                                     *
     *                                                                              *
     *  2.commit both backend and online databases.                                 *
     *                                                                              *
     *  3.after finished the over limit fee process update the                  *
     *  next scheduled date in eod process table.                                      *
     ********************************************************************************
     *
     */
    @Autowired
    LogManager logManager;

    @Autowired
    @Qualifier("ThreadPool_100")
    ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    StatusVarList statusList;

    @Autowired
    CommonRepo commonRepo;

    @Autowired
    OverLimitFeeRepo overLimitFeeRepo;

    @Autowired
    OverLimitFeeService overLimitFeeService;
<<<<<<< Updated upstream
=======
    int capacity = 200000;
    BlockingQueue<Integer> successCount = new ArrayBlockingQueue<Integer>(capacity);
    BlockingQueue<Integer> failCount = new ArrayBlockingQueue <Integer>(capacity);
>>>>>>> Stashed changes
    private static final Logger logInfo = LoggerFactory.getLogger("logInfo");
    private static final Logger logError = LoggerFactory.getLogger("logError");
    public String processHeader = "OVERLIMIT FEE PROCESS";

    @Override
    public void concreteProcess() throws Exception {
        HashMap<String, StringBuffer> accMap = null;
        try {
            Configurations.RUNNING_PROCESS_ID = Configurations.PROCESS_ID_OVER_LIMIT_FEE;
            CommonMethods.eodDashboardProgressParametersReset();
            processBean = new ProcessBean();
            processBean = commonRepo.getProcessDetails(Configurations.PROCESS_ID_OVER_LIMIT_FEE);

            if (processBean != null) {
                accMap = overLimitFeeRepo.getOverLimitAcc();
                Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS = accMap.size();

                if (accMap.size() > 0) {
                    for (Map.Entry<String, StringBuffer> entry : accMap.entrySet()) {
<<<<<<< Updated upstream
                        overLimitFeeService.addOverLimitFee(entry.getKey(), entry.getValue(), processBean, processHeader,Configurations.successCount,Configurations.failCount);
=======
                        overLimitFeeService.addOverLimitFee(entry.getKey(), entry.getValue(), processBean, processHeader,successCount,failCount);
>>>>>>> Stashed changes
                    }


                    //wait till all the threads are completed
                    while (!(taskExecutor.getActiveCount() == 0)) {
                        Thread.sleep(1000);
                    }
                }
            }
        } catch (Exception e) {
            Configurations.IS_PROCESS_COMPLETELY_FAILED = true;
            logError.error("OverLimit fee process failed", e);
        } finally {
            logInfo.info(logManager.logSummery(summery));
            try {
                if (accMap != null) {
                    /* PADSS Change -
                    variables handling card data should be nullified by replacing the value of variable with zero and call NULL function */
                    accMap.clear();
                }
            } catch (Exception e3) {
                logError.error("Exception in overlimit fee", e3);
            }
        }
    }

    @Override
    public void addSummaries() {
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
