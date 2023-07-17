package com.epic.cms.connector;

import com.epic.cms.common.ProcessBuilder;
import com.epic.cms.model.bean.EomCardBean;
import com.epic.cms.model.bean.ProcessBean;
import com.epic.cms.repository.CommonRepo;
import com.epic.cms.repository.EOMInterestRepo;
import com.epic.cms.service.EOMInterestService;
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
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class EOMInterestConnector extends ProcessBuilder {

    private static final Logger logInfo = LoggerFactory.getLogger("logInfo");
    private static final Logger logError = LoggerFactory.getLogger("logError");
    public AtomicInteger faileCardCount = new AtomicInteger(0);
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

        int noOfAccounts = 0;
        int failedAccounts = 0;
        try {
            Configurations.RUNNING_PROCESS_ID = Configurations.PROCESS_ID_EOM_INTEREST_CALCULATION;
            ArrayList<EomCardBean> accountList;
            DateFormat dateFormatforRenew = new SimpleDateFormat("dd");
            String curDateforRenew = dateFormatforRenew.format(Configurations.EOD_DATE);
            int day = Integer.parseInt(curDateforRenew);
            accountList = eomInterestRepo.getEomCardList(day);
            processBean = new ProcessBean();
            processBean = commonRepo.getProcessDetails(Configurations.PROCESS_ID_EOM_INTEREST_CALCULATION);

            noOfAccounts = accountList.size();
//            for (int i = 0; i < accountList.size(); i++) {
//                eomInterestService.EOMInterestCalculation(processBean, accountList.get(i));
//            }
            accountList.forEach(account -> {
                eomInterestService.EOMInterestCalculation(processBean, account,faileCardCount);
            });


            while (!(taskExecutor.getActiveCount() == 0)) {
                Thread.sleep(1000);
            }

            Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS = noOfAccounts;
            Configurations.PROCESS_SUCCESS_COUNT = (noOfAccounts - failedAccounts);
            Configurations.PROCESS_FAILD_COUNT = failedAccounts;

        } catch (Exception e) {
            Configurations.IS_PROCESS_COMPLETELY_FAILED = true;
            logError.error("EOM Interest Calculation Process failed", e);
        } finally {
            //logInfo.info(logManager.logSummery(summery));
        }
    }

    @Override
    public void addSummaries() {
        summery.put("Process Name ", processBean.getProcessDes());
        summery.put("Started Date ", Configurations.EOD_DATE.toString());
        summery.put("No of Accounts effected ", Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS);
        summery.put("No of Success Accounts ", Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS - faileCardCount.get());
        summery.put("No of fail Accounts ", faileCardCount.get());
    }
}
