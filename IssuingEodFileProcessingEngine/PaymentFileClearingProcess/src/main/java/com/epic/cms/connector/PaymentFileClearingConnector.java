/**
 * Author :
 * Date : 2/2/2023
 * Time : 4:19 PM
 * Project Name : ecms_eod_file_processing_engine
 */

package com.epic.cms.connector;

import com.epic.cms.common.FileProcessingProcessBuilder;
import com.epic.cms.model.bean.FileBean;
import com.epic.cms.model.bean.RecPaymentFileIptRowDataBean;
import com.epic.cms.repository.CommonRepo;
import com.epic.cms.repository.PaymentFileClearingRepo;
import com.epic.cms.service.PaymentFileClearingService;
import com.epic.cms.util.*;
import com.epic.cms.validation.PaymentValidations;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;

@Service
public class PaymentFileClearingConnector extends FileProcessingProcessBuilder {
    private static final Logger logInfo = LoggerFactory.getLogger("logInfo");
    private static final Logger logError = LoggerFactory.getLogger("logError");
    @Autowired
    PaymentFileClearingService paymentFileClearingService;
    @Autowired
    PaymentFileClearingRepo paymentFileClearingRepo;
    @Autowired
    CommonRepo commonRepo;
    @Autowired
    LogManager logManager;
    @Autowired
    StatusVarList status;
    @Autowired
    QueryParametersList queryParametersList;
    @Autowired
    @Qualifier("ThreadPool_PaymentFileValidator")
    ThreadPoolTaskExecutor taskExecutor;

    @Override
    public void concreteProcess(String fileId) {
        //initialize variables
        ArrayList<String> nameFieldList = new ArrayList<String>();
        FileBean fileBean;
        String filepath = "";
        String isFileNameValid = "";
        int noOfRecords = 0;
        boolean fileReadStatus = false;
        ArrayList<RecPaymentFileIptRowDataBean> fileContent = new ArrayList<RecPaymentFileIptRowDataBean>();
        try {
            Configurations.RUNNING_PROCESS_ID = Configurations.PROCESS_PAYMENT_FILE_VALIDATE;
            CommonMethods.eodDashboardProgressParametersReset();

            if ("LINUX".equals(Configurations.SERVER_RUN_PLATFORM)) {
                filepath = commonRepo.getLinuxFilePath(Configurations.FILE_CODE_PAYMENT);
            } else if ("WINDOWS".equals(Configurations.SERVER_RUN_PLATFORM)) {
                filepath = commonRepo.getWindowsFilePath(Configurations.FILE_CODE_PAYMENT);
            }
            //get payment file info
            fileBean = this.getPaymentFileInfo(fileId);
            if (fileBean != null) {
                //get name field list
                nameFieldList = commonRepo.getNameFields(Configurations.FILE_CODE_PAYMENT);
                //validate file name
                isFileNameValid = PaymentValidations.fileNameValidation(nameFieldList, fileBean.getFileName());
                if (isFileNameValid.isEmpty()) {
                    File file = new File(filepath + File.separator + fileBean.getFileName());
                    // Make sure file exist
                    if (file.isFile() && file.exists() && file.canRead()) {
                        fileBean.setFilePath(file.getAbsolutePath());
                        //file reading
                        fileReadStatus = paymentFileClearingService.readFile(fileBean);
                        if (fileReadStatus) {
                            // if file reading completed then start validation part

                            Configurations.PAYMENT_VALIDATION_HASH_TABLE = paymentFileClearingRepo.getPaymentFieldsValidation();
                            fileContent = paymentFileClearingRepo.getPaymentFileContents(fileId);
                            Configurations.PROCESS_PAYMENT_FILE_CLEARING_TOTAL_NOOF_TRABSACTIONS = fileContent.size();

                            for (RecPaymentFileIptRowDataBean paymentFileBean : fileContent) {
                                paymentFileClearingService.validateFile(fileId, paymentFileBean);
                            }

                            //wait till all the threads are completed
                            while (!(taskExecutor.getActiveCount() == 0)) {
                                Thread.sleep(1000);
                            }

                            //update file status to COMP
                            paymentFileClearingRepo.updatePaymentFileStatus(status.getCOMMON_COMPLETED(), fileId);

                            summery.put("Started Date ", Configurations.EOD_DATE.toString());
                            summery.put("File ID ", fileId);
                            summery.put("Number of payments to process ", Configurations.PROCESS_PAYMENT_FILE_CLEARING_TOTAL_NOOF_TRABSACTIONS);
                            summery.put("Number of success payments ", Configurations.PROCESS_PAYMENT_FILE_CLEARING_SUCCESS_COUNT);
                            summery.put("Number of invalid payments ", Configurations.PROCESS_PAYMENT_FILE_CLEARING_INVALID_COUNT);
                            summery.put("Number of failure payments ", Configurations.PROCESS_PAYMENT_FILE_CLEARING_FAILD_COUNT);

                            logInfo.info(logManager.logSummery(summery));
                        } else {
                            logError.error("Payment file reading failed for file " + fileId);
                            //update file read status to FAIL
                            paymentFileClearingRepo.updatePaymentFileStatus(Configurations.FAIL_STATUS, fileId);
                        }
                    } else {
                        logInfo.info("Payment file not found..."
                                + "\nFile Name : " + fileBean.getFileName()
                                + "\nFile ID   : " + fileBean.getFileId());
                        //update file read status to fail
                        paymentFileClearingRepo.updatePaymentFileStatus(Configurations.FAIL_STATUS, fileId);
                    }
                } else {
                    logInfo.info("Payment file clearing process failed for file " + fileId + " , " + isFileNameValid);
                    //update file read status to fail
                    paymentFileClearingRepo.updatePaymentFileStatus(Configurations.FAIL_STATUS, fileId);
                }
            } else {
                //file cannot proceed due to invalid status
                logError.error("Cannot read, Payment file " + fileId + " is not in the initial status");
            }
        } catch (Exception ex) {
            logError.error("Payment file clearing process failed for file " + fileId, ex);
            //update file status to FAIL
            try {
                paymentFileClearingRepo.updatePaymentFileStatus(Configurations.FAIL_STATUS, fileId);
            } catch (Exception e) {
                logError.error("", e);
            }
        }
    }

    @Override
    public void addSummaries() {

    }

    private synchronized FileBean getPaymentFileInfo(String fileId) throws Exception {
        FileBean fileBean;
        try {
            fileBean = paymentFileClearingRepo.getPaymentFileInfo(fileId);
            if (fileBean.getFileId() != null) {
                //file status is INIT,so it can proceed.
                //update file status to intermediate status FPROS
                //paymentFileClearingRepo.updatePaymentFileStatus(DatabaseStatus.STATUS_FILE_PROS, fileId);
            } else {
                fileBean = null;
            }
        } catch (Exception ex) {
            throw ex;
        }
        return fileBean;
    }
}
