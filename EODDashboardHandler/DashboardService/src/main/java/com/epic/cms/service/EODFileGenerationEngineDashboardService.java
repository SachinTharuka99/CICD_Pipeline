/**
 * Author : rasintha_j
 * Date : 3/18/2023
 * Time : 6:41 AM
 * Project Name : ecms_eod_product
 */

package com.epic.cms.service;

import com.epic.cms.model.bean.EodOutputFileBean;
import com.epic.cms.model.bean.StatementGenSummeryBean;
import com.epic.cms.model.entity.EODOUTPUTFILES;
import com.epic.cms.repository.EodOutputFileRepo;
import com.epic.cms.repository.StatementGenSummeryListRepo;
import com.epic.cms.util.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class EODFileGenerationEngineDashboardService {

    @Autowired
    EodOutputFileRepo eodOutputFileRepo;

    @Autowired
    StatementGenSummeryListRepo genSummeryListRepo;

    @Autowired
    LogManager logManager;

    public List<EodOutputFileBean> getEodOutputFIleList(Long eodId) {
        List<EodOutputFileBean> outputFileBeanList = new ArrayList<>();

        try {
            List<EODOUTPUTFILES> eodOutputFilesList = eodOutputFileRepo.findEODOUTPUTFILESByEODID(eodId);

            eodOutputFilesList.forEach(eod -> {
                EodOutputFileBean eodBean = new EodOutputFileBean();
                eodBean.setEodId(eodId);
                eodBean.setCreatedTime(eod.getCREATEDTIME());
                eodBean.setFileType(eod.getFILETYPE());
                eodBean.setNoOfRecords(eod.getNOOFRECORDS());
                eodBean.setFileName(eod.getFILENAME());
                eodBean.setSubFolder(eod.getSUBFOLDER());

                outputFileBeanList.add(eodBean);
            });
        } catch (Exception e) {
            throw e;
        }
        return outputFileBeanList;
    }

    public List<StatementGenSummeryBean> getStatementGenSummeryList(Long eodId) {
        List<StatementGenSummeryBean> stmtGenSummeryList = new ArrayList<>();

        try {
            stmtGenSummeryList = genSummeryListRepo.findStmtGenSummeryListByEodId(eodId);
        } catch (Exception e) {
            throw e;
        }
        return stmtGenSummeryList;
    }
}