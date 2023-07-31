package com.epic.cms.util;

import com.epic.cms.model.bean.ErrorCardBean;
import com.epic.cms.model.bean.ProcessBean;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Component
public class CommonMethods {

    @Autowired
    StatusVarList status;


    private static final Logger logInfo = LoggerFactory.getLogger("logInfo");
    private static final Logger logError = LoggerFactory.getLogger("logError");

    public static void eodDashboardProgressParametersReset() {

        Configurations.PROCESS_SUCCESS_COUNT = 0;
        Configurations.PROCESS_FAILD_COUNT = 0;
        Configurations.PROCESS_PROGRESS = "N/A";
        Configurations.PROCESS_TOTAL_NOOF_TRABSACTIONS = 0;
        Configurations.IS_PROCESS_ERROR = false;
        Configurations.successCount.remove();
        Configurations.failCount.remove();
    }

    public static String cardNumberMask(StringBuffer cardNo) {
        String maskedCardNo = null;

        try {
            //System.out.println("masking card--" + cardNo);
            if (cardNo == null) {
                return "--";
            }
            int startIndex = Configurations.START_INDEX;
            int endIndex = Configurations.END_INDEX;
            char maskCharacter = Configurations.PATTERN_CHAR.charAt(0);

            try {

                String prefix = cardNo.substring(0, startIndex);
                String postfix = cardNo.substring(endIndex, cardNo.length());
                String pattern = new String(new char[endIndex - startIndex]).replace('\0', maskCharacter);
                maskedCardNo = prefix + pattern + postfix;

            } catch (Exception ex) {
                if (endIndex < startIndex) {
                    logError.error("Error Occurd in method cardNumberMask" + System.lineSeparator() + "startIndex should be less than endIndex");
                    return cardNo.substring(0, 6) + new String(new char[12 - 6]).replace('\0', '*') + cardNo.substring(12, cardNo.length());
                } else if (endIndex > cardNo.length()) {
                    logError.error("Error Occurd in method cardNumberMask" + System.lineSeparator() + "endIndex Too Larger");
                    return cardNo.substring(0, 6) + new String(new char[12 - 6]).replace('\0', '*') + cardNo.substring(12, cardNo.length());
                } else {
                    logError.error("Error in Card Number MAsking Method", ex);
                    return cardNo.substring(0, 6) + new String(new char[12 - 6]).replace('\0', '*') + cardNo.substring(12, cardNo.length());
                }
            }
        } catch (Exception ex) {
            logError.error("Error in Card Number Masking Method", ex);
        }
        return maskedCardNo;
    }

    public static String validate(String groupNumber, int i, char c) throws Exception {
        String output = null;
        String output1 = null;

        int length = groupNumber.length();
        int padding = i - length;
        char[] test = new char[padding];
        String s = new String(test);
        test.toString();

        output1 = s.replace('\0', c);
        if (c == ' ') {
            output = groupNumber + output1;
        } else if (c == '0') {
            output = output1 + groupNumber;
        } else {
            throw new Exception("Invalid character to replaced");
        }
        return output;
    }

    public static String cardInfo(String cardNumber, ProcessBean processBean) {
        String info = "";
        info = cardNumber + " |" + processBean.getProcessId() + " |" + processBean.getProcessDes() + " |" + processBean.getSheduleTime();
        return info;
    }

    public static String checkForErrorCards(String sqlForCardNumber) {
        String sqlWithErrorCards = "";
        if (Configurations.STARTING_EOD_STATUS.equals("INIT")) {
//            System.out.println("status.getEOD_PENDING_STATUS()"+status.getEOD_PENDING_STATUS() );
            sqlWithErrorCards += " and " + sqlForCardNumber + " not in (select ec.cardno from eoderrorcards ec where ec.status='EPEN')";
        } else if (Configurations.STARTING_EOD_STATUS.equals("ERROR")) {
            sqlWithErrorCards += " and " + sqlForCardNumber + " in (select ec.cardno from eoderrorcards ec where ec.status='EPEN' and ec.EODID < " + Configurations.ERROR_EOD_ID + " and ec.PROCESSSTEPID <=" + Configurations.PROCESS_STEP_ID + ")";
        }
        return sqlWithErrorCards;
    }

    public static synchronized double getAmountFromCombination(double perc, double fee, String comb) {
        double _fee = 0f;
        if (comb.equals("MAX")) {
            if (perc >= fee) {
                _fee = perc;
            } else {
                _fee = fee;
            }

        } else if (comb.equals("MIN")) {
            if (perc <= fee) {
                _fee = perc;
            } else {
                _fee = fee;
            }

        } else if (comb.equals("CMB")) {
            _fee = fee + perc;
        } else {
            _fee = fee;
        }
        return _fee;
    }

    public static String eodDashboardProcessProgress(int successCount, int totalCount) {
        String progressStr;
        int progress = 0;
        if (successCount != 0 && totalCount != 0) {
            progress = (successCount * 100 / totalCount);
            progressStr = String.valueOf(progress) + "%";
            return progressStr;
        } else if (successCount == 0 && totalCount != 0) {
            return "0%";
        } else {
            return "100%";
        }
    }

    public static String ValuesRoundup(double x) {
        double zero = 0.0;
        String value = "";

        if (x * 1 % 1 == zero) {
            DecimalFormat df = new DecimalFormat("#");
            df.setRoundingMode(RoundingMode.FLOOR);
            value = df.format(x);

        } else if (x * 10 % 1 == zero) {
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.FLOOR);
            value = df.format(x);

        } else if (x * 100 % 1 == zero) {
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.FLOOR);
            value = df.format(x);

        } else {
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.FLOOR);
            value = df.format(x);

        }
        return value;
    }

    public static synchronized double getAmountfromCombination(double perc, double fee, String comb) {
        double _fee = 0f;
        if (comb.equals("MAX")) {
            if (perc >= fee) {
                _fee = perc;
            } else {
                _fee = fee;
            }

        } else if (comb.equals("MIN")) {
            if (perc <= fee) {
                _fee = perc;
            } else {
                _fee = fee;
            }

        } else if (comb.equals("CMB")) {
            _fee = fee + perc;
        } else {
            _fee = fee;
        }
        return _fee;
    }

    public static java.sql.Date getSqldate(Date date) {
        return new java.sql.Date(date.getTime());
    }

    public static void clearStringBuffer(StringBuffer cardNo) {
        try {
            if (cardNo == null || cardNo.toString().equals("")) {
            } else {
                cardNo.setLength(0);
            }
        } catch (Exception e) {
            logError.error(String.valueOf(e));
        }
    }

    public static String getInClauseString(ArrayList<StringBuffer> list) {
        StringBuilder queryBuilder = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {
//            if (i == 0) {
//                queryBuilder.append("(");
//            }
//            queryBuilder.append("'");
            queryBuilder.append(list.get(i));
//            queryBuilder.append("'");
            if (i != list.size() - 1) {
                queryBuilder.append(",");
            }
//            else {
//                queryBuilder.append(")");
//            }
        }

        return queryBuilder.toString();
    }

    public static double calcStampDutyFee(double totalForeignTxns, double persentage) {
        double totalFee = 0;
        int iterate = 1;
        persentage = (persentage / 100) * 1000;

        while (totalForeignTxns > 0) {
            totalFee = totalFee + persentage;
            totalForeignTxns = totalForeignTxns - 1000;
        }

        return totalFee;
    }


    /**
     * Add failed cards that may have occurred in a DB Connection class.
     */
    public static List<ErrorCardBean> failedCardList = new ArrayList<ErrorCardBean>();

    public static List<ErrorCardBean> getFailedCardsFromDB() {
        return failedCardList;
    }

    public static synchronized void addFailedCardsFromDB(ErrorCardBean fBean) {
        failedCardList.add(fBean);
    }

    public static synchronized void resetFailedCardList() {
        failedCardList.clear();
    }

    public static String getFormattedCurrency(String currencyAmount) {
        try {
            double amount = Double.parseDouble(currencyAmount);
            DecimalFormat formatter = new DecimalFormat("#,###.00"); // comma separated currency with two decimal places

            return formatter.format(amount);
        } catch (NumberFormatException ex) {
            return currencyAmount;
        }
    }

    public static Date getDateFromEODID(int eodID) throws Exception {
        Date parsedDate = null;
        String streodID = "";
        try {
            if (eodID > 10000000) {
                streodID = eodID + "";
                SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
                String eodIDsubs = streodID.substring(0, streodID.length() - 2);
                parsedDate = sdf.parse(eodIDsubs);
            }

        } catch (Exception e) {
            //e.printStackTrace();
            logError.error("--error--" + e);
            throw e;
        } finally {
            return parsedDate;
        }
    }

    // get date as yymmdd formate
    public static String getDate(Date date) throws Exception {
        try {
            String dateString = "";
            /**
             * @author bilal_a get given date to produce eod id
             */
            SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
            dateString = sdf.format(date);
            return dateString;
        } catch (Exception e) {
            throw e;
        }
    }

    public static int getNoOfDaysDifference(Date date1, Date date2) throws Exception {
        int days = 0;
        try {

            long date1Long = 0;
            long date2Long = 0;

            if (date1 == null) {
                date1Long = 0;
            } else {
                date1Long = date1.getTime();
            }

            if (date2 == null) {
                date2Long = 0;
            } else {
                date2Long = date2.getTime();
            }

            long diff = date2Long - date1Long;
            days = (int) ((diff / 3600000) / 24);

        } catch (Exception err) {
            //LogFileCreator.writeErrorToLog(err);
            throw err;
        }
        return days;
    }

    public static Date getNextDateForFreq(Date currDate, int numOfDays) {
        Calendar nextDate = Calendar.getInstance();
        nextDate.setTime(currDate);
        nextDate.add(Calendar.DATE, numOfDays);

        return nextDate.getTime();
    }

    public static String checkForErrorMerchants(String sqlForCardnumber) {
        String sqlWithErrorMerchants = "";
        if (Configurations.STARTING_EOD_STATUS.equals("INIT")) {
            sqlWithErrorMerchants += " and " + sqlForCardnumber + " not in (select nvl(em.MID,'000') as mid from EODERRORMERCHANT em where em.status='" + "EPEN" + "')";
        } else if (Configurations.STARTING_EOD_STATUS.equals("ERROR")) {
            sqlWithErrorMerchants += " and " + sqlForCardnumber + " in (select em.MID from EODERRORMERCHANT em where em.status='" + "EPEN" + "' and em.EODID < " + Configurations.ERROR_EOD_ID + " and em.PROCESSSTEPID <=" + Configurations.PROCESS_STEP_ID + ")";
        }
        return sqlWithErrorMerchants;
    }

    public static String validateLength(String groupNumber, int i) throws Exception {
        int length = groupNumber.length();
        if (length <= i) {
            return groupNumber;
        } else {
            throw new Exception("Maximum length exceed.");
        }
    }

    public static String validateCurrencyLength(String amount, int i) throws Exception {

        String decimal = null;
        String nonDecimal = null;
        int size = i;
        int padding;
        String s = amount;
        // System.out.println(s.length());
        String[] s1 = s.split("\\.");

        if (s.length() <= 30) {
            if (s1.length > 1) {
                // System.out.println(s1[0].length());
                if (s1[1].length() == 2) {
                    decimal = s1[1];
                } else if (s1[1].length() == 1) {
                    decimal = s1[1] + '0';
                } else {
                    decimal = "00";
                }

            } else {

                decimal = "00";
            }
        } else {
            throw new Exception("Invalid length for amount");
        }

        nonDecimal = s1[0];
        String finalS = nonDecimal + '.' + decimal;

        int length = finalS.length();
        if (length <= i) {
            return finalS;
        } else {
            throw new Exception("Maximum length exceed.");
        }
    }
}
