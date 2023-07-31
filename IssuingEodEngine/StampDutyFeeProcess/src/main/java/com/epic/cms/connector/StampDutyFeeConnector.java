package com.epic.cms.connector;

import com.epic.cms.common.ProcessBuilder;
import com.epic.cms.model.bean.StampDutyBean;
import com.epic.cms.repository.StampDutyFeeRepo;
import com.epic.cms.service.StampDutyFeeService;
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
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class StampDutyFeeConnector extends ProcessBuilder {

    private static final Logger logInfo = LoggerFactory.getLogger("logInfo");
    private static final Logger logError = LoggerFactory.getLogger("logError");
    @Autowired
    StampDutyFeeRepo stampDutyFeeRepo;
    @Autowired
    StampDutyFeeService stampDutyFeeService;
    @Autowired
    LogManager logManager;
    @Autowired
    StatusVarList statusVarList;
    @Autowired
    @Qualifier("ThreadPool_100")
    ThreadPoolTaskExecutor taskExecutor;
<<<<<<< Updated upstream
=======
    int capacity = 200000;
    BlockingQueue<Integer> successCount = new ArrayBlockingQueue<Integer>(capacity);
    BlockingQueue<Integer> failCount = new ArrayBlockingQueue <Integer>(capacity);
>>>>>>> Stashed changes

    @Override
    public void concreteProcess() throws Exception {
        ArrayList<StampDutyBean> statementAccountList = new ArrayList<>();
        int totalFailedCount = 0;
        int noOfCards = 0;


        try {
            Configurations.RUNNING_PROCESS_ID = Configurations.PROCESS_STAMP_DUTY_FEE;
            CommonMethods.eodDashboardProgressParametersReset();
            if (Configurations.STARTING_EOD_STATUS.equals(statusVarList.getINITIAL_STATUS())) {
                statementAccountList = stampDutyFeeRepo.getInitStatementAccountList();
            } else if (Configurations.STARTING_EOD_STATUS.equals(statusVarList.getERROR_STATUS())) {
                statementAccountList = stampDutyFeeRepo.getErrorStatementAccountList();
            }
            Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS = statementAccountList.size();
            noOfCards = statementAccountList.size();

//            for (StampDutyBean stampDutyAcoountBean : statementAccountList) {
//                stampDutyFeeService.StampDutyFee(stampDutyAcoountBean,faileCardCount);
//            }

            statementAccountList.forEach(stampDutyAcoountBean -> {
<<<<<<< Updated upstream
                stampDutyFeeService.StampDutyFee(stampDutyAcoountBean,Configurations.successCount,Configurations.failCount);
=======
                stampDutyFeeService.StampDutyFee(stampDutyAcoountBean,successCount,failCount);
>>>>>>> Stashed changes
            });




            //wait till all the threads are completed
            while (!(taskExecutor.getActiveCount() == 0)) {
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            Configurations.IS_PROCESS_COMPLETELY_FAILED = true;
            logError.error("Stan Duty Fee Process Fail", e);
        } finally {
            try {
                /* PADSS Change -variables handling card data should be nullified by replacing
                 the value of variable with zero and call NULL function */
                if (statementAccountList != null && statementAccountList.size() != 0) {
                    for (StampDutyBean stampDutyAcoountBean : statementAccountList) {
                        CommonMethods.clearStringBuffer(stampDutyAcoountBean.getCardNumber());
                    }
                    statementAccountList = null;
                }
            } catch (Exception e) {
                logError.error(String.valueOf(e));
            }
        }
    }

    @Override
    public void addSummaries() {
        summery.put("Started Date", Configurations.EOD_DATE.toString());
        summery.put("No of Card effected", Integer.toString(Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS));
<<<<<<< Updated upstream
        summery.put("No of Success Card ", Configurations.successCount.size());
        summery.put("No of fail Card ", Configurations.failCount.size());
=======
        summery.put("No of Success Card ", successCount.size());
        summery.put("No of fail Card ", failCount.size());
>>>>>>> Stashed changes
    }
}
