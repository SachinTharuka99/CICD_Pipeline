package com.epic.cms.connector;

import com.epic.cms.common.ProcessBuilder;
import com.epic.cms.model.bean.EomCardBean;
import com.epic.cms.model.bean.ProcessBean;
import com.epic.cms.repository.CommonRepo;
import com.epic.cms.repository.EOMInterestRepo;
import com.epic.cms.service.EOMInterestService;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class EOMInterestConnector extends ProcessBuilder {
<<<<<<< Updated upstream
=======
    int capacity = 200000;
    BlockingQueue<Integer> successCount = new ArrayBlockingQueue<Integer>(capacity);
    BlockingQueue<Integer> failCount = new ArrayBlockingQueue<Integer>(capacity);
    private static final Logger logInfo = LoggerFactory.getLogger("logInfo");
>>>>>>> Stashed changes
    private static final Logger logError = LoggerFactory.getLogger("logError");
    @Autowired
    StatusVarList statusList;
    @Autowired
    CommonRepo commonRepo;
    @Autowired
    LogManager logManager;
    @Autowired
    EOMInterestRepo eomInterestRepo;
    @Autowired
    EOMInterestService eomInterestService;
    @Autowired
    @Qualifier("taskExecutor2")
    ThreadPoolTaskExecutor taskExecutor;

    @Override
    public void concreteProcess() throws Exception {

        try {
            Configurations.RUNNING_PROCESS_ID = Configurations.PROCESS_ID_EOM_INTEREST_CALCULATION;
            CommonMethods.eodDashboardProgressParametersReset();
            ArrayList<EomCardBean> accountList;
            DateFormat dateFormatforRenew = new SimpleDateFormat("dd");
            String curDateforRenew = dateFormatforRenew.format(Configurations.EOD_DATE);
            int day = Integer.parseInt(curDateforRenew);
            accountList = eomInterestRepo.getEomCardList(day);
            Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS = accountList.size();
            processBean = new ProcessBean();
            processBean = commonRepo.getProcessDetails(Configurations.PROCESS_ID_EOM_INTEREST_CALCULATION);

//            for (int i = 0; i < accountList.size(); i++) {
//                eomInterestService.EOMInterestCalculation(processBean, accountList.get(i));
//            }
            accountList.forEach(account -> {
<<<<<<< Updated upstream
                eomInterestService.EOMInterestCalculation(processBean, account,Configurations.successCount,Configurations.failCount);
=======
                eomInterestService.EOMInterestCalculation(processBean, account,successCount,failCount);
>>>>>>> Stashed changes
            });


            while (!(taskExecutor.getActiveCount() == 0)) {
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            Configurations.IS_PROCESS_COMPLETELY_FAILED = true;
            logError.error("EOM Interest Calculation Process failed", e);
        }
    }

    @Override
    public void addSummaries() {
        summery.put("Process Name ", processBean.getProcessDes());
        summery.put("Started Date ", Configurations.EOD_DATE.toString());
        summery.put("No of Accounts effected ", Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS);
<<<<<<< Updated upstream
        summery.put("No of Success Accounts ",Configurations.successCount.size());
        summery.put("No of fail Accounts ", Configurations.failCount.size());
=======
        summery.put("No of Success Accounts ",successCount.size());
        summery.put("No of fail Accounts ", failCount.size());
>>>>>>> Stashed changes
    }
}
