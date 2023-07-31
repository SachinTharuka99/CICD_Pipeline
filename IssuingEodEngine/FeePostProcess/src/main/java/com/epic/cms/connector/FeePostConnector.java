/**
 * Author :
 * Date : 10/30/2022
 * Time : 8:28 AM
 * Project Name : ecms_eod_engine
 */

package com.epic.cms.connector;

import com.epic.cms.common.ProcessBuilder;
import com.epic.cms.model.bean.OtbBean;
import com.epic.cms.repository.FeePostRepo;
import com.epic.cms.service.FeePostService;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class FeePostConnector extends ProcessBuilder {

    int capacity = 200000;
    BlockingQueue<Integer> successCount = new ArrayBlockingQueue<Integer>(capacity);
    BlockingQueue<Integer> failCount = new ArrayBlockingQueue<Integer>(capacity);
    private static final Logger logInfo = LoggerFactory.getLogger("logInfo");
    private static final Logger logError = LoggerFactory.getLogger("logError");
    @Autowired
    LogManager logManager;
    @Autowired
    @Qualifier("ThreadPool_100")
    ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    StatusVarList status;
    @Autowired
    FeePostRepo feePostRepo;
    @Autowired
    FeePostService feePostService;
    List<OtbBean> custAccList = new ArrayList<>();

    @Override
    public void concreteProcess() throws Exception {
        LinkedHashMap details = new LinkedHashMap();
        LinkedHashMap summery = new LinkedHashMap();

        try {
            Configurations.RUNNING_PROCESS_ID = Configurations.PROCESS_ID_FEE_POST;
            Configurations.PROCESS_STEP_ID = 50;
            CommonMethods.eodDashboardProgressParametersReset();

            //get card account list
            if (Configurations.STARTING_EOD_STATUS.equals(status.getINITIAL_STATUS())) {
                custAccList = feePostRepo.getInitEodFeePostCustAcc();
            } else if (Configurations.STARTING_EOD_STATUS.equals(status.getERROR_STATUS())) {
                custAccList = feePostRepo.getErrorEodFeePostCustAcc();
            }
            Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS = custAccList.size();
            if (custAccList != null && custAccList.size() > 0) {
                System.out.println("Accounts eligible for fee posting process: " + custAccList.size());
                summery.put("Accounts eligible for fee posting process: ", custAccList.size() + "");
//                for (OtbBean bean : custAccList) {
//                    feePostService.proceedFeePost(bean);
//                }
                custAccList.forEach(bean -> {
<<<<<<< Updated upstream
                    feePostService.proceedFeePost(bean,Configurations.successCount,Configurations.failCount);
=======
                    feePostService.proceedFeePost(bean,successCount,failCount);
>>>>>>> Stashed changes
                });

            } else {
                summery.put("Accounts eligible for fee posting process ", 0 + "");
            }

            //wait till all the threads are completed
            while (!(taskExecutor.getActiveCount() == 0)) {
                Thread.sleep(1000);
            }
            Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS = (custAccList.size());

            //expire the fee promotion profile based on the end date mentioned in the profile.
            try {
                details.put("Fee promotion profile expire ", "Started");
                int success = feePostRepo.expireFeePromotionProfile();
                details.put("Fee promotion profile expire success for : " + success, "Finished");
            } catch (Exception ex) {
                logError.error("Fee post process failed when expiring the fee promotion profile ", ex);
                details.put("Fee promotion profile expire ", "Failed");
            }
        } catch (Exception ex) {
            Configurations.IS_PROCESS_COMPLETELY_FAILED = true;
            logError.error("--Error occurred--", ex);
        } finally {
            logInfo.info(logManager.logDetails(details));
            //logInfo.info(logManager.logSummery(summery));
             /* PADSS Change -
            variables handling card data should be nullified by replacing the value of variable with zero and call NULL function */
            for (OtbBean bean : custAccList) {
                CommonMethods.clearStringBuffer(bean.getCardnumber());
                CommonMethods.clearStringBuffer(bean.getMaincardno());
            }
        }
    }

    @Override
    public void addSummaries() {
        summery.put("Number of accounts to fee post ", Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS);
<<<<<<< Updated upstream
        summery.put("Number of success fee post ", Configurations.successCount.size());
        summery.put("Number of failure fee post ", Configurations.failCount.size());
=======
        summery.put("Number of success fee post ", successCount.size());
        summery.put("Number of failure fee post ", failCount.size());
>>>>>>> Stashed changes
    }
}
