package com.epic.cms.connector;

import com.epic.cms.common.ProcessBuilder;
import com.epic.cms.model.bean.CardBean;
import com.epic.cms.model.bean.ErrorCardBean;
import com.epic.cms.model.bean.ProcessBean;
import com.epic.cms.repository.CommonRepo;
import com.epic.cms.repository.ManualNpRepo;
import com.epic.cms.service.ManualNpService;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class ManualNpConnector extends ProcessBuilder {
    private static final Logger logInfo = LoggerFactory.getLogger("logInfo");
    private static final Logger logError = LoggerFactory.getLogger("logError");
    public List<ErrorCardBean> cardErrorList = new ArrayList<ErrorCardBean>();
<<<<<<< Updated upstream
=======
    int capacity = 200000;
    BlockingQueue<Integer> successCount = new ArrayBlockingQueue<Integer>(capacity);
    BlockingQueue<Integer> failCount = new ArrayBlockingQueue<Integer>(capacity);
    BlockingQueue<Integer> successCountDe = new ArrayBlockingQueue<Integer>(capacity);
    BlockingQueue<Integer> failCountDe = new ArrayBlockingQueue<Integer>(capacity);
>>>>>>> Stashed changes

    int manualNpTotalCount = 0;
    int manualNpSuccesssCount = 0;
    int manualNpFailedCount = 0;
    @Autowired
    ManualNpService manualNpService;
    @Autowired
    ManualNpRepo manualNpRepo;
    @Autowired
    StatusVarList statusList;
    @Autowired
    CommonRepo commonRepo;
    @Autowired
    LogManager logManager;
    @Autowired
    @Qualifier("taskExecutor2")
    ThreadPoolTaskExecutor taskExecutor;
    int selectedaccounts = 0;
    int successCounts = 0;
    int FailedCounts = 0;

    @Override
    public void concreteProcess() throws Exception {

        ProcessBean processBean = new ProcessBean();
        try {
            CommonMethods.eodDashboardProgressParametersReset();

            processBean = commonRepo.getProcessDetails(Configurations.PROCESS_ID_MANUAL_NP_PROCESS);
            if (processBean != null) {
                HashMap<String, String[]> accMap = new HashMap<String, String[]>();

                /**-----------------------------------------Manual NP claissifcation Started----------------------------------------- */
                accMap = manualNpRepo.getManualNpRequestDetails(statusList.getYES_STATUS_1(), statusList.getEOD_DONE_STATUS());

                Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS += accMap.size();

                for (Map.Entry<String, String[]> acc : accMap.entrySet()) {
                    ArrayList<StringBuffer> arrList = new ArrayList<>();
                    String[] temp = acc.getValue();
                    arrList.add(new StringBuffer(acc.getKey()));
                    arrList.add(new StringBuffer(temp[0]));
                    arrList.add(new StringBuffer(temp[1]));
                    arrList.add(new StringBuffer(temp[2]));
<<<<<<< Updated upstream
                    manualNpService.manualNpClassification(arrList,Configurations.successCount,Configurations.failCount);
=======
                    manualNpService.manualNpClassification(arrList,successCount,failCount);
>>>>>>> Stashed changes

                }

                while (!(taskExecutor.getActiveCount() == 0)) {
                    Thread.sleep(1000);
                }

                manualNpTotalCount = selectedaccounts;
                manualNpSuccesssCount = successCounts;
                manualNpFailedCount = FailedCounts;

                /**-------------------------------------------Manual NP claissifcation Ended------------------------------------------- */
                /**->->->->->->->->->->->->->->->->->->->->->->->-->->->->->->->-><-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-*/
                /**->->->->->->->->->->->->->->->->->->->->->->->-->->->->->->->-><-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-*/
                /**-----------------------------------------Manual NP Declaissifcation Started----------------------------------------- */

                accMap.clear();
                accMap = manualNpRepo.getManualNpRequestDetails(statusList.getNO_STATUS_0(), statusList.getCOMMON_REQUEST_ACCEPTED());
                selectedaccounts = 0;
                successCounts = 0;
                FailedCounts = 0;


                Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS += accMap.size();

//                for (Map.Entry<String, String[]> acc : accMap.entrySet()) {
//                    ArrayList<StringBuffer> arrList = new ArrayList<>();
//                    String[] temp = acc.getValue();
//                    arrList.add(new StringBuffer(acc.getKey()));
//                    arrList.add(new StringBuffer(temp[0]));
//                    arrList.add(new StringBuffer(temp[1]));
//                    arrList.add(new StringBuffer(temp[2]));
//                    manualNpService.manualNpDeClassification(arrList);
//                }

                accMap.forEach((key, value) -> {
                    ArrayList<StringBuffer> arrList = new ArrayList<>();
                    arrList.add(new StringBuffer(key));
                    arrList.add(new StringBuffer(value[0]));
                    arrList.add(new StringBuffer(value[1]));
                    arrList.add(new StringBuffer(value[2]));
<<<<<<< Updated upstream
                    manualNpService.manualNpDeClassification(arrList, Configurations.successCountDe,Configurations.failCountDe);
=======
                    manualNpService.manualNpDeClassification(arrList, successCountDe,failCountDe);
>>>>>>> Stashed changes
                });

                while (!(taskExecutor.getActiveCount() == 0)) {
                    Thread.sleep(1000);
                }

                Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS = (manualNpTotalCount + selectedaccounts);
                Configurations.PROCESS_SUCCESS_COUNT = (manualNpSuccesssCount + successCounts);
                Configurations.PROCESS_FAILD_COUNT = (manualNpFailedCount + FailedCounts);

            }
        } catch (Exception e) {
            Configurations.IS_PROCESS_COMPLETELY_FAILED = true;
            try {
                logError.error("Manual NP process failed", e);

                if (processBean.getCriticalStatus() == 1) {
                    Configurations.COMMIT_STATUS = false;
                    Configurations.FLOW_STEP_COMPLETE_STATUS = false;
                    Configurations.PROCESS_FLOW_STEP_COMPLETE_STATUS = false;
                    Configurations.MAIN_EOD_STATUS = false;
                }

            } catch (Exception e2) {
                logError.error("Exception", e2);
            }
        } finally {
            //logInfo.info(logManager.logSummery(summery));
        }
    }

    @Override
    public void addSummaries() {
        summery.put("Selected Account for manual NP", selectedaccounts);
<<<<<<< Updated upstream
        summery.put("No of Success Accounts", Configurations.successCount.size());
        summery.put("No of Failed Accounts", Configurations.failCount.size());
        summery.put("Process Status for Manual NP", "Passed");

        summery.put("Selected Acc for manual NP De-classified ", Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS);
        summery.put("No of Success Accounts ",Configurations.successCountDe.size());
        summery.put("No of Failed Accounts ", Configurations.failCountDe.size());
=======
        summery.put("No of Success Accounts", successCount.size());
        summery.put("No of Failed Accounts", failCount.size());
        summery.put("Process Status for Manual NP", "Passed");

        summery.put("Selected Acc for manual NP De-classified ", Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS);
        summery.put("No of Success Accounts ", successCountDe.size());
        summery.put("No of Failed Accounts ", failCountDe.size());
>>>>>>> Stashed changes
        summery.put("Process Status for Manual NP De-classified", "Passed");
    }
}
