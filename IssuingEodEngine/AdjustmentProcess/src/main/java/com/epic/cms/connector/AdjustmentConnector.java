/**
 * Author :
 * Date : 10/29/2022
 * Time : 7:34 AM
 * Project Name : ecms_eod_engine
 */

package com.epic.cms.connector;

import com.epic.cms.common.ProcessBuilder;
import com.epic.cms.dao.AdjustmentDao;
import com.epic.cms.model.bean.AdjustmentBean;
import com.epic.cms.service.AdjustmentService;
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

import java.util.ArrayList;
import java.util.List;


@Service
public class AdjustmentConnector extends ProcessBuilder {

    @Autowired
    @Qualifier("ThreadPool_100")
    ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    AdjustmentService adjustmentService;

    @Autowired
    AdjustmentDao adjustmentDao;

    @Autowired
    LogManager logManager;

    private static final Logger logInfo = LoggerFactory.getLogger("logInfo");
    private static final Logger logError = LoggerFactory.getLogger("logError");

    @Override
    public void concreteProcess() throws Exception {
        Configurations.ADJUSTMENT_SEQUENCE_NO = 1;

        List<AdjustmentBean> adjustmentList = new ArrayList<>();
        try {
            Configurations.RUNNING_PROCESS_ID = Configurations.ADJUSTMENT_PROCESS;
            CommonMethods.eodDashboardProgressParametersReset();

            //get adjustment list
            adjustmentList = adjustmentDao.getAdjustmentList();
            Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS = adjustmentList.size();

            for (AdjustmentBean adjustmentBean : adjustmentList) {
                adjustmentService.proceedAdjustment(adjustmentBean);
            }

            //wait till all the threads are completed
            while (!(taskExecutor.getActiveCount() == 0)) {
                Thread.sleep(1000);
            }

        } catch (Exception ex) {
            Configurations.IS_PROCESS_COMPLETELY_FAILED = true;
            throw ex;
        } finally {
            logInfo.info(logManager.logSummery(summery));
            /** PADSS Change -
             variables handling card data should be nullified
             by replacing the value of variable with zero and call NULL function */
            for (AdjustmentBean adjustBean : adjustmentList) {
                CommonMethods.clearStringBuffer(adjustBean.getCardNumber());
            }
            adjustmentList = null;
        }

    }

    @Override
    public void addSummaries() {
        summery.put("Started Date", Configurations.EOD_DATE.toString());
        summery.put("No of Card effected", Integer.toString(Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS));
        summery.put("No of Success Card ", Integer.toString(Configurations.PROCESS_SUCCESS_COUNT));
        summery.put("No of fail Card ", Integer.toString(Configurations.PROCESS_FAILD_COUNT));
    }
}
