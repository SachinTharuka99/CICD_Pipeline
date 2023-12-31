/**
 * Author : yasiru_l
 * Date : 11/21/2022
 * Time : 10:08 AM
 * Project Name : ecms_eod_file_generation_engine
 */

package com.epic.cms.connector;

import com.epic.cms.common.FileGenProcessBuilder;
import com.epic.cms.repository.CardApplicationConfirmationLetterRepo;
import com.epic.cms.service.CardApplicationConfirmationLetterService;
import com.epic.cms.util.CommonMethods;
import com.epic.cms.util.Configurations;
import com.epic.cms.util.LogManager;
import com.epic.cms.util.StatusVarList;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static com.epic.cms.util.LogManager.*;

@Service
public class CardApplicationConfirmationLetterConnector extends FileGenProcessBuilder {
    @Autowired
    LogManager logManager;
    @Autowired
    StatusVarList statusVarList;
    @Autowired
    CardApplicationConfirmationLetterService cardApplicationConfirmationLetterService;
    @Autowired
    CardApplicationConfirmationLetterRepo cardApplicationConfirmationLetterRepo;

    private static final Logger logInfo = LoggerFactory.getLogger("logInfo");
    private static final Logger logError = LoggerFactory.getLogger("logError");

    String[] fileNameAndPath = null;

    @Override
    public void concreteProcess() throws Exception {

        String[] fileNameAndPath = null;
        ArrayList<StringBuffer> confirmCardlist = cardApplicationConfirmationLetterRepo.getConfirmedCardToGenerateLetters();
        try {

            Configurations.RUNNING_PROCESS_ID = Configurations.PROCESS_ID_CARDAPPLICATION_LETTER_APPROVE;
            CommonMethods.eodDashboardProgressParametersReset();

            if (confirmCardlist != null) {
                Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS = confirmCardlist.size();
            }

            int sequenceNo = 0;
            for (int i = 0; i < confirmCardlist.size(); i++) {
                fileNameAndPath = cardApplicationConfirmationLetterService.getConfirmationLetter(confirmCardlist.get(i),sequenceNo);
                sequenceNo++;
            }

        }catch (Exception e){
            Configurations.IS_PROCESS_COMPLETELY_FAILED = true;
            logError.error("Card Application Confirmation Letter Process Failed", e);

            if(fileNameAndPath!= null){
                fileGenerationService.deleteExistFile(fileNameAndPath[0]);
            }
        }finally {
            logInfo.info(logManager.logSummery(summery));
            try {
                if (!confirmCardlist.isEmpty()) {
                    for (int i = 0; i < confirmCardlist.size(); i++) {
                        CommonMethods.clearStringBuffer(confirmCardlist.get(i));
                    }
                }
            } catch (Exception e) {
                logError.error("Exception in Card Number Clearing ",e);
            }
        }
    }

    @Override
    public void addSummaries() {
        summery.put("Started Date ", Configurations.EOD_DATE.toString());
        summery.put("Total No of Effected Letters ", Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS);
        summery.put("Letter Success Count ", Configurations.PROCESS_SUCCESS_COUNT);
        summery.put("Letter Failed Count ", Configurations.PROCESS_FAILD_COUNT);
        //summery.put("File Name and Path ", fileNameAndPath);
    }
}
