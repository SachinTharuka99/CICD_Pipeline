/**
 * Author : rasintha_j
 * Date : 11/22/2022
 * Time : 6:18 AM
 * Project Name : ecms_eod_engine
 */

package com.epic.cms.connector;

import com.epic.cms.common.ProcessBuilder;
import com.epic.cms.model.bean.CardBean;
import com.epic.cms.model.bean.OtbBean;
import com.epic.cms.repository.CommonRepo;
import com.epic.cms.repository.KnockOffRepo;
import com.epic.cms.service.KnockOffService;
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
public class KnockOffConnector extends ProcessBuilder {
    int capacity = 200000;
    BlockingQueue<Integer> successCount = new ArrayBlockingQueue<Integer>(capacity);
    BlockingQueue<Integer> failCount = new ArrayBlockingQueue<Integer>(capacity);
    private static final Logger logInfo = LoggerFactory.getLogger("logInfo");
    private static final Logger logError = LoggerFactory.getLogger("logError");
    @Autowired
    LogManager logManager;
    @Autowired
    CommonRepo commonRepo;
    @Autowired
    @Qualifier("ThreadPool_100")
    ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    KnockOffService knockOffService;
    @Autowired
    KnockOffRepo knockOffRepo;
    @Autowired
    StatusVarList statusVarList;
    ArrayList<OtbBean> custAccList = new ArrayList<OtbBean>();
    ArrayList<OtbBean> cardList = new ArrayList<OtbBean>();
    ArrayList<OtbBean> paymentList = new ArrayList<OtbBean>();
    int failedCount = 0;

    @Override
    public void concreteProcess() throws Exception {
        try {
            if (Configurations.STARTING_EOD_STATUS.equals(statusVarList.getINITIAL_STATUS())) {
                custAccList = knockOffRepo.getInitKnockOffCustAcc();
            } else if (Configurations.STARTING_EOD_STATUS.equals(statusVarList.getERROR_STATUS())) {
                custAccList = knockOffRepo.getErrorKnockOffCustAcc();
            }

            if (custAccList != null && custAccList.size() > 0) {
                Configurations.RUNNING_PROCESS_ID = Configurations.PROCESS_ID_KNOCK_OFF;
                CommonMethods.eodDashboardProgressParametersReset();
                summery.put("Accounts eligible for knock off process: ", custAccList.size());

//                for (OtbBean custAccBean : custAccList) {
//                    knockOffService.knockOff(custAccBean, cardList, paymentList);
//                }

                custAccList.forEach(custAccBean -> {
                    knockOffService.knockOff(custAccBean, cardList, paymentList,Configurations.successCount,Configurations.failCount);
                    knockOffService.knockOff(custAccBean, cardList, paymentList,successCount,failCount);
                });
                //wait till all the threads are completed
                while (!(taskExecutor.getActiveCount() == 0)) {
                    Thread.sleep(1000);
                }

//                failedCount = Configurations.PROCESS_FAILD_COUNT;
                Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS = (custAccList.size());
//                Configurations.PROCESS_SUCCESS_COUNT = (custAccList.size() - failedCount);
//                Configurations.PROCESS_FAILD_COUNT = (failedCount);

            } else {
                summery.put("Accounts eligible for fee posting process ", 0 + "");
            }
        } catch (Exception e) {
            Configurations.IS_PROCESS_COMPLETELY_FAILED = true;
            logError.error("Knock Off process Error", e);
        } finally {
            //logInfo.info(logManager.logSummery(summery));
            try {
                if (custAccList != null && custAccList.size() != 0) {
                    for (OtbBean custAccBean : custAccList) {
                        CommonMethods.clearStringBuffer(custAccBean.getCardnumber());
                        CommonMethods.clearStringBuffer(custAccBean.getMaincardno());
                    }
                    custAccList = null;
                }
                if (cardList != null && cardList.size() != 0) {
                    for (OtbBean supCardBean : cardList) {
                        CommonMethods.clearStringBuffer(supCardBean.getCardnumber());
                        CommonMethods.clearStringBuffer(supCardBean.getMaincardno());
                    }
                    cardList = null;
                }
                if (paymentList != null && paymentList.size() != 0) {
                    for (OtbBean paymentBean : paymentList) {
                        CommonMethods.clearStringBuffer(paymentBean.getCardnumber());
                        CommonMethods.clearStringBuffer(paymentBean.getMaincardno());
                    }
                    paymentList = null;
                }
            } catch (Exception e) {
                logError.error("Knock Off process Error", e);
            }
        }
    }

    @Override
    public void addSummaries() {
        summery.put("Number of transaction to sync", Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS);

        summery.put("Number of success transaction", Configurations.successCount.size());
        summery.put("Number of failure transaction",Configurations.failCount.size())

        summery.put("Number of success transaction", successCount.size());
        summery.put("Number of failure transaction",failCount.size());

    }
}
