package com.epic.cms.service;

import com.epic.cms.model.bean.ErrorCardBean;
import com.epic.cms.model.bean.LastStatementSummeryBean;
import com.epic.cms.model.bean.ProcessBean;
import com.epic.cms.repository.CheckPaymentForMinimumAmountRepo;
import com.epic.cms.repository.CommonRepo;
import com.epic.cms.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;

import static com.epic.cms.util.LogManager.errorLogger;
import static com.epic.cms.util.LogManager.infoLogger;

@Service
public class CheckPaymentForMinimumAmountService {

    @Autowired
    LogManager logManager;

    @Autowired
    CheckPaymentForMinimumAmountRepo checkPaymentForMinimumAmountRepo;

    @Autowired
    StatusVarList status;

    @Autowired
    CommonRepo commonRepo;

    @Async("ThreadPool_100")
    @Transactional(value = "transactionManager", propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public void CheckPaymentForMinimumAmount(LastStatementSummeryBean lastStatement) {
        LinkedHashMap details = new LinkedHashMap();
        Date checkDueDate = null;
        ProcessBean processBean = null;

        try {
            // Check daily payments. try to clear from min payment table.
            details.put("Checking card", CommonMethods.cardNumberMask(lastStatement.getCardno()));
            double totalTransactions = 0;
            double minAmount = 0;
            double payments = 0;

            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
            if (lastStatement != null) {
                checkDueDate = lastStatement.getDueDate();
            }

            if (checkDueDate != null && checkDueDate.toString().equals(format1.format(Configurations.EOD_DATE))) {
                details.put("Card falling on Due Date", CommonMethods.cardNumberMask(lastStatement.getCardno()));
                Statusts.SUMMARY_FOR_CARDS_ON_DUEDATE++;
                CreateEodId convertToEOD = new CreateEodId();
                int statementDayEODID = 0;
                Date statementEnd = lastStatement.getStatementEndDate();
                statementDayEODID = Integer.parseInt(convertToEOD.getDate(statementEnd) + "00");
                minAmount = lastStatement.getMinAmount();
                totalTransactions = lastStatement.getClosingBalance();
                String accNo = checkPaymentForMinimumAmountRepo.getAccountNoOnCard(lastStatement.getCardno());
                payments = checkPaymentForMinimumAmountRepo.getPaymentAmount(accNo, statementDayEODID);//dbBackendCon.getEODTotalTransactionDetailsForCard(cardNo, statementDayEODID, Configurations.EOD_ID);

                if (payments < minAmount) {
                    double paymentsBeforeDueDate = checkPaymentForMinimumAmountRepo.getTotalPaymentExceptDueDate(accNo, statementDayEODID);
                    checkPaymentForMinimumAmountRepo.insertToMinPayTable(lastStatement.getCardno(), minAmount, totalTransactions,
                            DateUtil.getSqldate(checkDueDate), accNo, statementDayEODID, payments, paymentsBeforeDueDate);
                    details.put("card added to min pay table", CommonMethods.cardNumberMask(lastStatement.getCardno()));
                    details.put("min payment", minAmount);
                    infoLogger.info(LogManager.processDetailsStyles(details));
                    details.clear();
                    Statusts.SUMMARY_FOR_MINPAYMENT_RISK_ADDED++;
                }
            }
            Configurations.PROCESS_SUCCESS_COUNT++;
            infoLogger.info(LogManager.processDetailsStyles(details));
            details.clear();
        } catch (Exception e) {
            Configurations.errorCardList.add(new ErrorCardBean(Configurations.ERROR_EOD_ID, Configurations.EOD_DATE, new StringBuffer(lastStatement.getCardno()), e.getMessage(), Configurations.RUNNING_PROCESS_ID, Configurations.RUNNING_PROCESS_DESCRIPTION, 0, CardAccount.CARD));
            Configurations.PROCESS_FAILD_COUNT++;
            errorLogger.error("Error Occured for cardno :" + CommonMethods.cardInfo(String.valueOf(lastStatement.getCardno()), processBean), e);
        }
        infoLogger.info(LogManager.processDetailsStyles(details));
    }
}
