package com.epic.cms.connector;

import com.epic.cms.common.ProcessBuilder;
import com.epic.cms.model.bean.CashBackAlertBean;
import com.epic.cms.repository.CashBackAlertRepo;
import com.epic.cms.repository.CommonRepo;
import com.epic.cms.service.CashBackAlertService;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


@Service
public class CashBackAlertConnector extends ProcessBuilder {
<<<<<<< Updated upstream
=======
    int capacity = 200000;
    BlockingQueue<Integer> successCount = new ArrayBlockingQueue<Integer>(capacity);
    BlockingQueue<Integer> failCount = new ArrayBlockingQueue<Integer>(capacity);
>>>>>>> Stashed changes
    private static final Logger logInfo = LoggerFactory.getLogger("logInfo");
    private static final Logger logError = LoggerFactory.getLogger("logError");
    @Autowired
    @Qualifier("ThreadPool_100")
    ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    StatusVarList statusList;
    @Autowired
    CashBackAlertRepo cashBackAlertRepo;
    @Autowired
    CashBackAlertService cashBackAlertService;
    @Autowired
    CommonRepo commonRepo;
    @Autowired
    LogManager logManager;

    @Override
    public void concreteProcess() throws Exception {

        HashMap<String, ArrayList<CashBackAlertBean>> confirmAccountlist = null;
        try {

            Configurations.RUNNING_PROCESS_ID = Configurations.PROCESS_ID_CASH_BACK_ALERT_PROCESS;
            processBean = commonRepo.getProcessDetails(Configurations.PROCESS_ID_CASH_BACK_ALERT_PROCESS);
            CommonMethods.eodDashboardProgressParametersReset();

            confirmAccountlist = cashBackAlertRepo.getConfirmedAccountToAlert();

            if (confirmAccountlist != null && confirmAccountlist.size() > 0) {
                for (Map.Entry<String, ArrayList<CashBackAlertBean>> entry : confirmAccountlist.entrySet()) {
                    Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS += entry.getValue().size();

<<<<<<< Updated upstream
                    cashBackAlertService.processCashBackAlertService(entry.getKey(), entry.getValue(), processBean,Configurations.successCount,Configurations.failCount);
=======
                    cashBackAlertService.processCashBackAlertService(entry.getKey(), entry.getValue(), processBean,successCount,failCount);
>>>>>>> Stashed changes
                }

                while (!(taskExecutor.getActiveCount() == 0)) {
                    Thread.sleep(1000);
                }

                Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS = (Configurations.successCardNoCount_CashBackAlert + Configurations.failedCardNoCount_CashBackAlert);
                Configurations.PROCESS_SUCCESS_COUNT = (Configurations.successCardNoCount_CashBackAlert);
                Configurations.PROCESS_FAILD_COUNT = (Configurations.failedCardNoCount_CashBackAlert);

            }
        } catch (Exception ex) {
            Configurations.IS_PROCESS_COMPLETELY_FAILED = true;
            throw ex;
        } finally {
            logInfo.info(logManager.logSummery(summery));
        }
    }

    @Override
    public void addSummaries() {
        summery.put("Process Name", processBean.getProcessDes());
        summery.put("Started Date", Configurations.EOD_DATE.toString());
        summery.put("No of Account effected", Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS);
<<<<<<< Updated upstream
        summery.put("No of Success Account ", Configurations.successCount.size());
        summery.put("No of fail Account ",Configurations.failCount.size());
=======
        summery.put("No of Success Account ", successCount.size());
        summery.put("No of fail Account ",failCount.size());
>>>>>>> Stashed changes
    }
}
