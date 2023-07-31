/**
 * Author : sharuka_j
 * Date : 12/6/2022
 * Time : 7:09 AM
 * Project Name : ecms_eod_engine
 */

package com.epic.cms.connector;

import com.epic.cms.common.ProcessBuilder;
import com.epic.cms.dao.EOMSupplementaryCardResetDao;
import com.epic.cms.model.bean.ProcessBean;
import com.epic.cms.repository.CommonRepo;
import com.epic.cms.service.EOMSupplementaryCardResetService;
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
public class EOMSupplementaryCardResetConnector extends ProcessBuilder {
<<<<<<< Updated upstream
=======
    int capacity = 200000;
    BlockingQueue<Integer> successCount = new ArrayBlockingQueue<Integer>(capacity);
    BlockingQueue<Integer> failCount = new ArrayBlockingQueue<Integer>(capacity);
>>>>>>> Stashed changes
    private static final Logger logInfo = LoggerFactory.getLogger("logInfo");
    private static final Logger logError = LoggerFactory.getLogger("logError");
    @Autowired
    CommonRepo commonRepo;
    @Autowired
    LogManager logManager;
    @Autowired
    EOMSupplementaryCardResetDao eomSupplementaryCardResetDao;
    @Autowired
    EOMSupplementaryCardResetService eomSupplementaryCardResetService;
    @Autowired
    StatusVarList statusVarList;
    @Autowired
    @Qualifier("ThreadPool_100")
    ThreadPoolTaskExecutor taskExecutor;

    @Override
    public void concreteProcess() throws Exception {
        Configurations.RUNNING_PROCESS_ID = Configurations.PROCESS_EOM_SUP_CARD_RESET;
        CommonMethods.eodDashboardProgressParametersReset();
        processBean = new ProcessBean();
        processBean = commonRepo.getProcessDetails(Configurations.PROCESS_EOM_SUP_CARD_RESET);
        int configProcess = Configurations.PROCESS_EOM_SUP_CARD_RESET;

        try {
            logInfo.info(logManager.logHeader("Supplementary Reset Process"));
            processBean = new ProcessBean();
            processBean = commonRepo.getProcessDetails(Configurations.PROCESS_EOM_SUP_CARD_RESET);

            if (processBean != null) {
                logInfo.info(logManager.logStartEnd("Supplementary Reset Process started"));

                ArrayList accList = eomSupplementaryCardResetDao.getEligibleAccounts();
                Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS = accList.size();

//                for (int i = 0; i < accList.size(); i++) {
//                    eomSupplementaryCardResetService.SupplementryResetThread(accList.get(i));
//                }

                accList.forEach(acc -> {
<<<<<<< Updated upstream
                    eomSupplementaryCardResetService.SupplementryResetThread(acc,Configurations.successCount,Configurations.failCount);
=======
                    eomSupplementaryCardResetService.SupplementryResetThread(acc,successCount,failCount);
>>>>>>> Stashed changes
                });

                /**wait till all the threads are completed*/
                while (!(taskExecutor.getActiveCount() == 0)) {
                    Thread.sleep(1000);
                }
            }
        } catch (Exception e) {
            try {
                Configurations.IS_PROCESS_COMPLETELY_FAILED = true;
                logError.error("Supplementary Reset Process", e);

                if (processBean.getCriticalStatus() == 1) {
                    Configurations.COMMIT_STATUS = false;
                    Configurations.FLOW_STEP_COMPLETE_STATUS = false;
                    Configurations.PROCESS_FLOW_STEP_COMPLETE_STATUS = false;
                    Configurations.MAIN_EOD_STATUS = false;
                }
            } catch (Exception e2) {
                logError.error("Supplementary Reset Process", e2);
            }
        } finally {
            //logInfo.info(logManager.logSummery(summery));
        }
    }

    @Override
    public void addSummaries() {
        summery.put("Number of accounts to supplementry card reset ", Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS);
<<<<<<< Updated upstream
        summery.put("Number of success card reset ", Configurations.successCount.size());
        summery.put("Number of failure supplementry card ", Configurations.failCount.size());
=======
        summery.put("Number of success card reset ", successCount.size());
        summery.put("Number of failure supplementry card ", failCount.size());
>>>>>>> Stashed changes
    }
}
