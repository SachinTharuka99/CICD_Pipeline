/**
 * Author : sharuka_j
 * Date : 11/22/2022
 * Time : 2:54 PM
 * Project Name : ecms_eod_engine
 */

package com.epic.cms.connector;

import com.epic.cms.common.ProcessBuilder;
import com.epic.cms.dao.CardRenewDao;
import com.epic.cms.model.bean.CardRenewBean;
import com.epic.cms.model.bean.ErrorCardBean;
import com.epic.cms.model.bean.ProcessBean;
import com.epic.cms.repository.CardRenewRepo;
import com.epic.cms.repository.CommonRepo;
import com.epic.cms.service.CardRenewService;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class CardRenewConnector extends ProcessBuilder {
    private static final Logger logInfo = LoggerFactory.getLogger("logInfo");
    private static final Logger logError = LoggerFactory.getLogger("logError");
    @Autowired
    CardRenewService cardRenewService;
    @Autowired
    CardRenewDao cardRenewDao;
    @Autowired
    CommonRepo commonRepo;
    @Autowired
    StatusVarList statusVarList;
    @Autowired
    CardRenewRepo cardRenewRepo;
    @Autowired
    LogManager logManager;
    @Autowired
    @Qualifier("ThreadPool_100")
    ThreadPoolTaskExecutor taskExecutor;
<<<<<<< Updated upstream
=======
    int capacity = 200000;
    BlockingQueue<Integer> successCount = new ArrayBlockingQueue<Integer>(capacity);
    BlockingQueue<Integer> failCount = new ArrayBlockingQueue<Integer>(capacity);
>>>>>>> Stashed changes
    ArrayList<CardRenewBean> approvedCardBeanList = new ArrayList<CardRenewBean>();
    int noOfEarlyRenewals = 0;
    int noOfNormalRenewals = 0;

    /**
     * @author Malinda_R
     * @since 2016-10-12
     * <p>
     * *******************************************************************************
     * <p>
     * This will renew all cards which are having approval for renewal process.
     * For this purpose, picked up the cards from backend database( card table) , which are
     * in renewal threshhold period.
     * <p>
     * If there are cards in renewal threshhold period,
     * 1.insert those cards into renewal table.(with the status - RNIN) - by eod
     * <p>
     * 2.Once early renewal request comes,that card also inserted into
     * the renewal table by the web users(with the status - RNAC) - by web users
     * *
     * 3.then backend users will give approval for those cards(Status with RNIN) in renewal table
     * (Status change as RNAC)- by web users
     * *
     * 4.In next eod it will pickup all card which have the approval(status - RNAC)
     * and calculate new expiry date and update only card table and back end card table
     * <p>
     * 4.exp date calculation -
     * -> early renew --> exp date=current date+validity period
     * -> normal renew --> exp date=current exp date+validity period
     * <p>
     * ----NOTE----
     * When insert to card renewal tble, it will only inserted which are not in the
     * renewal table with the status all status. So that this card renewal table
     * should flush atleast within 4 years.
     * *******************************************************************************
     */

    @Override
    public void concreteProcess() throws Exception {

        List<String> eligibleCardList = new ArrayList<String>();
        List<ErrorCardBean> cardErrorList = new ArrayList<ErrorCardBean>();

        try {
            String StartEodStatus = Configurations.STARTING_EOD_STATUS;
            boolean isErrorProcess = commonRepo.isErrorProcess(Configurations.PROCESS_CARD_RENEW);
            boolean isProcessCompletlyFail = cardRenewDao.isProcessCompletlyFail(Configurations.PROCESS_CARD_RENEW);

            processBean = new ProcessBean();
            processBean = commonRepo.getProcessDetails(Configurations.PROCESS_CARD_RENEW);

            if (processBean != null) {
                Configurations.RUNNING_PROCESS_ID = Configurations.PROCESS_CARD_RENEW;
                CommonMethods.eodDashboardProgressParametersReset();

                /**get current month and current year*/
                DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");
                Date eod_date = Configurations.EOD_DATE;// eod date
                String eod_date_str = dateFormat.format(eod_date).toUpperCase();//07-OCT-16
                DateFormat dateFormatforRenew = new SimpleDateFormat("yyMM");
                String curDateforRenew = dateFormatforRenew.format(eod_date); //1610

                /**
                 * selecting cards which eligible to renew from card table In
                 * here, should check whether selected cards already in the
                 * cardrenewal table or not. if there is any new cards, (not
                 * exist in renewal table)that should insert to cardrenewal
                 * table
                 */
//                int hasErrorEODandProcess = 0;// this is for test
//                try {
//                    cardErrorList = dbCon.getEligibleCardList(eod_date_str, hasErrorEODandProcess);
//                } catch (Exception e) {
//                    infoLogger.info(logManager.processStartEndStyle("Card Renewal Process Failed when selecting eligible cards"));
//
//                    errorLogger.error("Card Renewal Process Failed when selecting eligible cards" + e);
//                }

                /**selecting card list from cardrenew Table which have approval*/
                approvedCardBeanList = cardRenewDao.getApprovedCardList(eod_date_str);
                Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS = approvedCardBeanList.size();
                if (approvedCardBeanList.size() != 0) {
                    /**iterate card list one by one*/
//                    for (CardRenewBean CRBean : approvedCardBeanList) {
//                        cardRenewService.cardRenewProcess(CRBean, noOfEarlyRenewals, noOfNormalRenewals,faileCardCount);
//                    }

                    approvedCardBeanList.forEach(CRBean -> {
<<<<<<< Updated upstream
                        cardRenewService.cardRenewProcess(CRBean, noOfEarlyRenewals, noOfNormalRenewals,Configurations.successCount,Configurations.failCount);
=======
                        cardRenewService.cardRenewProcess(CRBean, noOfEarlyRenewals, noOfNormalRenewals,successCount,failCount);
>>>>>>> Stashed changes
                    });
                }
            }
        } catch (Exception e) {
            Configurations.IS_PROCESS_COMPLETELY_FAILED = true;
            logInfo.info(logManager.logStartEnd("Card Renew Process Fails"));

            try {
                if (processBean.getCriticalStatus() == 1) {
                    Configurations.COMMIT_STATUS = false;
                    Configurations.FLOW_STEP_COMPLETE_STATUS = false;
                    Configurations.PROCESS_FLOW_STEP_COMPLETE_STATUS = false;
                    Configurations.MAIN_EOD_STATUS = false;
                }
            } catch (Exception e2) {
                logError.error("", e2);
            }

        } finally {
            try {
                int NoOfFailCards = Configurations.PROCESS_FAILD_COUNT;
                if (approvedCardBeanList.size() == 0) {
                    logInfo.info("No Cards in Aprroved List");
                }

                if (approvedCardBeanList != null && approvedCardBeanList.size() != 0) {
                    /* PADSS Change -
            variables handling card data should be nullified by replacing the value of variable with zero and call NULL function */
                    for (CardRenewBean CRBean : approvedCardBeanList) {
                        CommonMethods.clearStringBuffer(CRBean.getCardNumber());
                    }
                    approvedCardBeanList = null;
                }
            } catch (Exception e2) {
                logError.error("", e2);
            }
        }
    }

    @Override
    public void addSummaries() {
        summery.put("Process Name", "Card Renewal");
        summery.put("Renewal Process Started with", Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS + " Cards");
        summery.put("No of Early Renwals", Integer.toString(noOfEarlyRenewals));
        summery.put("No of Normal Renwals", Integer.toString(noOfNormalRenewals));
<<<<<<< Updated upstream
        summery.put("No of Success Card", Configurations.successCount.size());
        summery.put("Total Fails", Configurations.failCount.size());
=======
        summery.put("No of Success Card", successCount.size());
        summery.put("Total Fails", failCount.size());
>>>>>>> Stashed changes
    }
}
