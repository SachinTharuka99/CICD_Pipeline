/**
 * Author : sharuka_j
 * Date : 11/22/2022
 * Time : 3:28 PM
 * Project Name : ecms_eod_engine
 */

package com.epic.cms.connector;

import com.epic.cms.common.ProcessBuilder;
import com.epic.cms.dao.RiskCalculationDao;
import com.epic.cms.model.bean.DelinquentAccountBean;
import com.epic.cms.model.bean.ProcessBean;
import com.epic.cms.model.bean.RiskCalculationBean;
import com.epic.cms.repository.CommonRepo;
import com.epic.cms.service.RiskCalculationService;
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
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RiskCalculationConnector extends ProcessBuilder {
    int capacity = 200000;
    BlockingQueue<Integer> successCount = new ArrayBlockingQueue<Integer>(capacity);
    BlockingQueue<Integer> failCount = new ArrayBlockingQueue <Integer>(capacity);
    private static final Logger logInfo = LoggerFactory.getLogger("logInfo");
    private static final Logger logError = LoggerFactory.getLogger("logError");
    @Autowired
    CommonRepo commonRepo;
    @Autowired
    StatusVarList statusVarList;
    @Autowired
    LogManager logManager;
    @Autowired
    RiskCalculationService riskCalculationService;
    @Autowired
    RiskCalculationDao riskCalculationDao;
    @Autowired
    @Qualifier("ThreadPool_100")
    ThreadPoolTaskExecutor taskExecutor;

    @Override
    public void concreteProcess() throws Exception {

        CommonMethods.eodDashboardProgressParametersReset();
        int configProcess = Configurations.PROCESS_ID_RISK_CALCULATION_PROCESS;
        String processHeader = "RISK CALCULATION PROCESS";
        LinkedHashMap summery = new LinkedHashMap();
        int count = 0;
        int noOfExistingCards = 0;
        int failedExistingCards = 0;
        int noOfNewCards = 0;
        int failedNewCards = 0;
        ArrayList<DelinquentAccountBean> delinquentCardList = null;
        ArrayList<RiskCalculationBean> cardList = new ArrayList<RiskCalculationBean>();

        Configurations.RUNNING_PROCESS_ID = Configurations.PROCESS_ID_RISK_CALCULATION_PROCESS;
        processBean = new ProcessBean();
        processBean = commonRepo.getProcessDetails(Configurations.PROCESS_ID_RISK_CALCULATION_PROCESS);

        try {
            if (processBean != null) {
                /**Get the already exist account list from delinquent table
                 //Np date will be either due date or normal date. (changed by bthe NP CR. prviously only due date account will NP.)
                 //This NP Method has used in easypayment process for accelerate the NP account easypayments. */
                delinquentCardList = riskCalculationDao.getDelinquentAccounts();
                Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS += delinquentCardList.size();

//                for (DelinquentAccountBean delinquentAccountBean : delinquentCardList) {
//                    riskCalculationService.riskCalculationProcess(delinquentAccountBean, configProcess, processBean,faileCardCount);
//                }

                delinquentCardList.forEach(delinquentAccountBean -> {
<<<<<<< Updated upstream
                    riskCalculationService.riskCalculationProcess(delinquentAccountBean, configProcess, processBean,Configurations.successCount,Configurations.failCount);
=======
                    riskCalculationService.riskCalculationProcess(delinquentAccountBean, configProcess, processBean,successCount,failCount);
>>>>>>> Stashed changes
                });

                //wait till all the threads are completed
                while (!(taskExecutor.getActiveCount() == 0)) {
                    Thread.sleep(1000);
                }

                //------------------End Change risk class,NDIA,MIA,Status...... in delinquent table-----------------------------------------------------//
//----------------------------------Insert fresh cards to delinquent table-----------------------------------------------------//

                //                if (day == 01) {//Cards will be absorb for risk process in a month beginning after a mdue date
                logInfo.info(logManager.logStartEnd("RISK_CALCULATION_PROCESS Process Started for new cards"));
                cardList = riskCalculationDao.getRiskCalculationCardList();
                Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS += cardList.size();
                if (cardList.size() > 0) {
//                    for (RiskCalculationBean riskCalculationBean : cardList) {
//                        riskCalculationService.freshCardToTable(riskCalculationBean, processBean,faileCardCount);
//                    }
                    cardList.forEach(riskCalculationBean -> {
<<<<<<< Updated upstream
                        riskCalculationService.freshCardToTable(riskCalculationBean, processBean,Configurations.successCount,Configurations.failCount);
=======
                        riskCalculationService.freshCardToTable(riskCalculationBean, processBean,successCount,failCount);
>>>>>>> Stashed changes
                    });



                    //wait till all the threads are completed
                    while (!(taskExecutor.getActiveCount() == 0)) {
                        Thread.sleep(1000);
                    }
                } else {
                    logInfo.info("No new cards for add to risk class");
                }

            }

        } catch (Exception e) {
            Configurations.IS_PROCESS_COMPLETELY_FAILED = true;
            logError.error("RISK_CALCULATION_PROCESS ended with", e);
        } finally {
            //logInfo.info(logManager.logSummery(summery));
        }
    }

    @Override
    public void addSummaries() {
        summery.put("Started Date", Configurations.EOD_DATE.toString());
        summery.put("No of Card effected", Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS);
<<<<<<< Updated upstream
        summery.put("No of Success Card ", Configurations.successCount.size());
        summery.put("No of fail Card ", Configurations.failCount.size());
=======
        summery.put("No of Success Card ", successCount.size());
        summery.put("No of fail Card ", failCount.size());
>>>>>>> Stashed changes
    }
}
