/**
 * Author : sharuka_j
 * Date : 11/22/2022
 * Time : 3:13 PM
 * Project Name : ecms_eod_engine
 */

package com.epic.cms.repository;

import com.epic.cms.dao.CardRenewDao;
import com.epic.cms.model.bean.CardRenewBean;
import com.epic.cms.util.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Objects;


@Repository
public class CardRenewRepo implements CardRenewDao {

    private static final Logger logInfo = LoggerFactory.getLogger("logInfo");
    private static final Logger logError = LoggerFactory.getLogger("logError");
    @Autowired
    StatusVarList statusList;
    @Autowired
    LogManager logManager;
    @Autowired
    private JdbcTemplate backendJdbcTemplate;
    @Autowired
    @Qualifier("onlineJdbcTemplate")
    private JdbcTemplate onlineJdbcTemplate;



    @Autowired
    QueryParametersList queryParametersList;

    @Override
    public int getCardValidityPeriod(StringBuffer cardNumber) throws Exception {
        int validityPeriod = 0;

        //String query = "SELECT VALIDITYPERIOD FROM CARDPRODUCT WHERE PRODUCTCODE = (SELECT CARDPRODUCT FROM CARD WHERE CARD.CARDNUMBER = ?)";

        try {
            validityPeriod = backendJdbcTemplate.queryForObject(queryParametersList.getCardRenew_getCardValidityPeriod(), Integer.class, cardNumber.toString());
        } catch (EmptyResultDataAccessException e) {
            return 0;
        } catch (Exception e) {
            throw e;
        }
        return validityPeriod;
    }

    @Override
    public void updateCardTable(StringBuffer cardNumber, String newExpireDate, String isProductChange) throws Exception {
        try {
            if (isProductChange.equalsIgnoreCase(Configurations.YES_STATUS)) {
                //String query = "UPDATE CARD SET NEWEXPIRYDATE = ?,EXPIERYDATEMONTH=?,EXPIERYDATEYEAR=? WHERE CARDNUMBER = ?";

                backendJdbcTemplate.update(queryParametersList.getCardRenew_updateCardTable()
                        , newExpireDate
                        , newExpireDate.substring(2, 4)
                        , newExpireDate.substring(0, 2)
                        , cardNumber.toString());
            } else {
                //String query = "UPDATE CARD SET NEWEXPIRYDATE = ?,EMBOSSSTATUS = ?,EXPIERYDATEMONTH=?,EXPIERYDATEYEAR=? WHERE CARDNUMBER = ?";

                backendJdbcTemplate.update(queryParametersList.getCardRenew_updateCardTable_2()
                        , newExpireDate
                        , statusList.getSTATUS_NO()
                        , newExpireDate.substring(2, 4)
                        , newExpireDate.substring(0, 2)
                        , cardNumber.toString());
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void updateCardRenewTable(StringBuffer cardNumber) throws Exception {
        //String query = "UPDATE CARDRENEW SET STATUS = ?,LASTEODUPDATEDDATE=? WHERE CARDNUMBER = ?";
        try {

            java.sql.Date eodDate = DateUtil.getSqldate(Configurations.EOD_DATE);
            backendJdbcTemplate.update(queryParametersList.getCardRenew_updateCardRenewTable(), statusList.getCARD_RENEWAL_COMPLETE(), eodDate, cardNumber.toString());
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void updateOnlineCardTable(StringBuffer cardNumber, String newExpireDate) throws Exception {

        //String query = "UPDATE ECMS_ONLINE_CARD SET NEWEXPDATE =? WHERE CARDNUMBER =?";
        try {

            onlineJdbcTemplate.update(queryParametersList.getCardRenew_updateOnlineCardTable(), newExpireDate, cardNumber.toString());

            if (Configurations.ONLINE_LOG_LEVEL == 1) {
                //Only for troubleshoot
                logInfo.info("================ updateOnlineCardTable ===================" + Configurations.EOD_ID);
                logInfo.info(queryParametersList.getCardRenew_updateOnlineCardTable());
                logInfo.info(newExpireDate);
                logInfo.info(CommonMethods.cardNumberMask(cardNumber));
                logInfo.info("================ updateOnlineCardTable END ===================");
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public ArrayList<CardRenewBean> getApprovedCardList(String curDate) throws Exception {
        ArrayList<CardRenewBean> cardList = new ArrayList<CardRenewBean>();

        //String query = "SELECT CR.CARDNUMBER AS CARDNUMBER ,CR.EARLYRENEW AS EARLYRENEW,CR.EXPIRYDATE AS EXPIRYDATE, CR.ISREPLACERENEWAL AS ISREPLACERENEWAL, CD.CARDSTATUS  AS CARDSTATUS FROM CARDRENEW CR, CARD CD WHERE CD.cardnumber=CR.cardnumber AND CR.STATUS   =?";

        try {
            cardList = (ArrayList<CardRenewBean>) backendJdbcTemplate.query(queryParametersList.getCardRenew_getApprovedCardList(), new RowMapperResultSetExtractor<>((result, rowNum) -> {
                        CardRenewBean CRBean = new CardRenewBean();
                        CRBean.setCardNumber(new StringBuffer(result.getString("CARDNUMBER")));
                        CRBean.setEarlyRenew(result.getString("EARLYRENEW"));
                        CRBean.setExpirydate(result.getString("EXPIRYDATE"));
                        CRBean.setCardStatus(result.getString("CARDSTATUS"));
                        CRBean.setIsProductChange(result.getString("ISREPLACERENEWAL"));
                        System.out.println(CommonMethods.cardNumberMask(new StringBuffer(result.getString("CARDNUMBER"))) + "- selected to renewal process");
                        return CRBean;
                    })
                    , statusList.getCARD_RENEWAL_ACCEPTED());

        } catch (Exception e) {
            throw e;
        }
        return cardList;
    }

    @Override
    public boolean isProcessCompletlyFail(int ProcessID) throws Exception {
        boolean isProcessCompletlyFail = false;
        try {
            //String query = "SELECT ISPROCESSFAIL FROM EODERRORCARDS WHERE  ERRORPROCESSID = ? and STATUS = ? ";

            isProcessCompletlyFail = Objects.requireNonNull(backendJdbcTemplate.query(queryParametersList.getCardRenew_isProcessCompletlyFail(),
                    (ResultSet rs) -> {
                        boolean tempIsProcessCompletlyFail = false;
                        while (rs.next()) {
                            if (rs.getInt("ISPROCESSFAIL") == 1) {
                                tempIsProcessCompletlyFail = true;
                            }
                        }
                        return tempIsProcessCompletlyFail;
                    },
                    ProcessID,
                    statusList.getEOD_PENDING_STATUS()
            ));
        } catch (Exception e) {
            throw e;
        }
        return isProcessCompletlyFail;
    }
}
