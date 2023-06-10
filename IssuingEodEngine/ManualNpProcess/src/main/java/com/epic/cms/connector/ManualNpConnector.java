package com.epic.cms.connector;

import com.epic.cms.common.ProcessBuilder;
import com.epic.cms.model.bean.ErrorCardBean;
import com.epic.cms.model.bean.ProcessBean;
import com.epic.cms.repository.CommonRepo;
import com.epic.cms.repository.ManualNpRepo;
import com.epic.cms.service.ManualNpService;
import com.epic.cms.util.CommonMethods;
import com.epic.cms.util.Configurations;
import com.epic.cms.util.LogManager;
import com.epic.cms.util.StatusVarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.epic.cms.util.LogManager.errorLogger;
import static com.epic.cms.util.LogManager.infoLogger;

@Service
public class ManualNpConnector extends ProcessBuilder {
    public List<ErrorCardBean> cardErrorList = new ArrayList<ErrorCardBean>();
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
                    manualNpService.manualNpClassification(arrList);

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

                for (Map.Entry<String, String[]> acc : accMap.entrySet()) {
                    ArrayList<StringBuffer> arrList = new ArrayList<>();
                    String[] temp = acc.getValue();
                    arrList.add(new StringBuffer(acc.getKey()));
                    arrList.add(new StringBuffer(temp[0]));
                    arrList.add(new StringBuffer(temp[1]));
                    arrList.add(new StringBuffer(temp[2]));
                    manualNpService.manualNpDeClassification(arrList);
                }

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
                logManager.logError("Manual NP process failed", e,errorLogger);

                if (processBean.getCriticalStatus() == 1) {
                    Configurations.COMMIT_STATUS = false;
                    Configurations.FLOW_STEP_COMPLETE_STATUS = false;
                    Configurations.PROCESS_FLOW_STEP_COMPLETE_STATUS = false;
                    Configurations.MAIN_EOD_STATUS = false;
                }

            } catch (Exception e2) {
                logManager.logError("Exception", e2,errorLogger);
            }
        } finally {
            logManager.logSummery(summery, infoLogger);
        }
    }

    @Override
    public void addSummaries() {
        summery.put("Selected Account for manual NP", selectedaccounts);
        summery.put("No of Success Accounts", successCounts);
        summery.put("No of Failed Accounts", FailedCounts);
        summery.put("Process Status for Manual NP", "Passed");
        summery.put("Selected Acc for manual NP De-classified ", Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS);
        summery.put("No of Success Accounts ", Configurations.PROCESS_SUCCESS_COUNT);
        summery.put("No of Failed Accounts ", Configurations.PROCESS_FAILD_COUNT);
        summery.put("Process Status for Manual NP De-classified", "Passed");
    }
}
