package com.epic.cms.service;

import com.epic.cms.dao.RiskCalculationDao;
import com.epic.cms.model.bean.DelinquentAccountBean;
import com.epic.cms.model.bean.ProcessBean;
import com.epic.cms.model.bean.RiskCalculationBean;
import com.epic.cms.repository.CommonRepo;
import com.epic.cms.util.Configurations;
import com.epic.cms.util.LogManager;
import com.epic.cms.util.StatusVarList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class RiskCalculationServiceTest {

    private RiskCalculationService riskCalculationServiceUnderTest;
    int capacity = 200000;
    BlockingQueue<Integer> successCount = new ArrayBlockingQueue<Integer>(capacity);
    BlockingQueue<Integer> failCount = new ArrayBlockingQueue <Integer>(capacity);

    @BeforeEach
    void setUp() {
        riskCalculationServiceUnderTest = new RiskCalculationService();
        riskCalculationServiceUnderTest.commonRepo = mock(CommonRepo.class);
        riskCalculationServiceUnderTest.logManager = mock(LogManager.class);
        riskCalculationServiceUnderTest.riskCalculationDao = mock(RiskCalculationDao.class);
        riskCalculationServiceUnderTest.statusVarList = mock(StatusVarList.class);
    }

    @Test
    void testRiskCalculationProcess() throws Exception {
        // Setup
        final DelinquentAccountBean delinquentAccountBean = new DelinquentAccountBean();
        delinquentAccountBean.setAccNo("123456789123");
        delinquentAccountBean.setAccStatus("backendStatus");
        delinquentAccountBean.setNDIA(2);
        delinquentAccountBean.setMIA(0);
        delinquentAccountBean.setLastStatementDate(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        delinquentAccountBean.setDelinqstatus("delinqstatus");
        delinquentAccountBean.setCardNumber(new StringBuffer("4890118864436725"));
        delinquentAccountBean.setDueDate(new GregorianCalendar(2023, Calendar.JULY, 14).getTime());
        delinquentAccountBean.setAssignStatus("assignStatus");
        delinquentAccountBean.setSupervisor("assignee");
        delinquentAccountBean.setAssignee("assignee");
        delinquentAccountBean.setRiskClass("RISK_CLASS_ZERO");
        delinquentAccountBean.setDueAmount("0.00");
        delinquentAccountBean.setIsdueDate(0);
        delinquentAccountBean.setNpInterest(0.0);
        delinquentAccountBean.setNpOutstanding(0.0);
        delinquentAccountBean.setProvisionAmount(0.0);
        delinquentAccountBean.setNpDate(new GregorianCalendar(2023, Calendar.JULY, 14).getTime());
        delinquentAccountBean.setRemainDue(0.0);

        final ProcessBean processBean = new ProcessBean();
        processBean.setProcessId(0);
        processBean.setStepId(0);
        processBean.setProcessDes("processDes");
        processBean.setCriticalStatus(0);
        processBean.setSheduleTime("sheduleTime");



        Configurations.EOD_DATE = new Date();
        Configurations.TXN_TYPE_REFUND = "123456";
        Configurations.TXN_TYPE_PAYMENT = "123456";
        Configurations.TXN_TYPE_REVERSAL = "123456";
        Configurations.TXN_TYPE_MVISA_REFUND = "123456";
        Configurations.START_INDEX = 6;
        Configurations.END_INDEX =12;
        Configurations.PATTERN_CHAR = "*";
        Configurations.EOD_ID = 12112;
        Configurations.EOD_USER = "user";


        final HashMap<String, Double> stringDoubleHashMap = new HashMap<>(Map.ofEntries(Map.entry("value", 0.0)));
        final HashMap<String, Date> stringDateHashMap = new HashMap<>(Map.ofEntries(Map.entry("value", new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())));
        final Date date = new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime();

        LinkedHashMap details = new LinkedHashMap();
        ArrayList<Object> lastStmtDetails = new ArrayList<>();
        lastStmtDetails.add(delinquentAccountBean.getLastStatementDate());
        lastStmtDetails.add(delinquentAccountBean.getMIA());
        lastStmtDetails.add(delinquentAccountBean.getDueDate());

        String[] newriskClass = new String[3];
        newriskClass[0] = String.valueOf(delinquentAccountBean.getNDIA());
        newriskClass[1] = String.valueOf(3);
        newriskClass[2] = "MINNDIA";

        String[] bucketId = new String[3];
        bucketId[0] = String.valueOf(delinquentAccountBean.getNDIA());
        bucketId[1] = String.valueOf(3);
        bucketId[2] = "MINNDIA";

        boolean isDueDate = true;

        when(riskCalculationServiceUnderTest.statusVarList.getRISK_CLASS_ZERO()).thenReturn("0");
        when(riskCalculationServiceUnderTest.statusVarList.getACCOUNT_NON_PERFORMING_STATUS()).thenReturn("NP");
        when(riskCalculationServiceUnderTest.statusVarList.getRISK_CLASS_ONE()).thenReturn("1");
        when(riskCalculationServiceUnderTest.statusVarList.getRISK_CLASS_TWO()).thenReturn("2");
        when(riskCalculationServiceUnderTest.statusVarList.getRISK_CLASS_THREE()).thenReturn("3");
        when(riskCalculationServiceUnderTest.statusVarList.getRISK_CLASS_FOUR()).thenReturn("4");
        when(riskCalculationServiceUnderTest.statusVarList.getRISK_CLASS_FIVE()).thenReturn("5");
        when(riskCalculationServiceUnderTest.statusVarList.getRISK_CLASS_SIX()).thenReturn("6");
        when(riskCalculationServiceUnderTest.statusVarList.getRISK_CLASS_SEVEN()).thenReturn("7");
        when(riskCalculationServiceUnderTest.statusVarList.getRISK_CLASS_EIGHT()).thenReturn("8");
        when(riskCalculationServiceUnderTest.statusVarList.getRISK_CLASS_NINE()).thenReturn("9");
        when(riskCalculationServiceUnderTest.statusVarList.getACCOUNT_ACTIVE_STATUS()).thenReturn("ACT");
        when(riskCalculationServiceUnderTest.statusVarList.getONLINE_CARD_ACTIVE_STATUS()).thenReturn(1);
        when(riskCalculationServiceUnderTest.statusVarList.getCHEQUE_RETURN_STATUS()).thenReturn("CQRT");
        when(riskCalculationServiceUnderTest.statusVarList.getTO_RESOLVE_STATUS()).thenReturn("RESL");
        when(riskCalculationServiceUnderTest.statusVarList.getONLY_MANUAL_NP_STATUS()).thenReturn("MNNP");
        when(riskCalculationServiceUnderTest.statusVarList.getSTATUS_NO()).thenReturn("NO");
        when(riskCalculationServiceUnderTest.statusVarList.getTO_ACTIVE_STATUS()).thenReturn("ACT");
        when(riskCalculationServiceUnderTest.statusVarList.getTO_NON_PERFORMING_TO_NON_PERFORMING_STATUS()).thenReturn("NTN");
        when(riskCalculationServiceUnderTest.statusVarList.getTO_NON_PERFORMING_TO_PERFORMING_STATUS()).thenReturn("NPTP");
        when(riskCalculationServiceUnderTest.statusVarList.getTO_PERFORMING_TO_NON_PERFORMING_STATUS()).thenReturn("PTNP");
        when(riskCalculationServiceUnderTest.statusVarList.getTO_PERFORMING_TO_PERFORMING_STATUS()).thenReturn("PTP");

        when(riskCalculationServiceUnderTest.riskCalculationDao.isManualNp(any())).thenReturn(true);
        when(riskCalculationServiceUnderTest.riskCalculationDao.getLastStatementDate(any(StringBuffer.class))).thenReturn(lastStmtDetails);
        when(riskCalculationServiceUnderTest.riskCalculationDao.checkForPayment("123456789123",new Date())).thenReturn(3.0);
        when(riskCalculationServiceUnderTest.riskCalculationDao.updateAllDELINQUENTACCOUNTnpdetails(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "123456789123")).thenReturn(1);
        when(riskCalculationServiceUnderTest.riskCalculationDao.getRiskclassOnNdia(any(Integer.class))).thenReturn(newriskClass);
        when(riskCalculationServiceUnderTest.riskCalculationDao.getNPRiskClass()).thenReturn("riskClass");
        when(riskCalculationServiceUnderTest.riskCalculationDao.getNDIAOnRiskClass(any(String.class))).thenReturn(bucketId);
        when(riskCalculationServiceUnderTest.riskCalculationDao.getTotalPaymentSinceLastDue(any(String.class), any(Date.class), any(Date.class))).thenReturn(1.0);
        when(riskCalculationServiceUnderTest.riskCalculationDao.addDetailsToDelinquentAccountTable(any(DelinquentAccountBean.class))).thenReturn(1);


        // Run the test
<<<<<<< Updated upstream
        riskCalculationServiceUnderTest.riskCalculationProcess(delinquentAccountBean, 0, processBean, Configurations.successCount,Configurations.failCount);
=======
        riskCalculationServiceUnderTest.riskCalculationProcess(delinquentAccountBean, 0, processBean, successCount,failCount);
>>>>>>> Stashed changes

        // Verify the results
        verify(riskCalculationServiceUnderTest.riskCalculationDao,times(1)).isManualNp(any());
        verify(riskCalculationServiceUnderTest.riskCalculationDao,times(1)).getLastStatementDate(any(StringBuffer.class));
        verify(riskCalculationServiceUnderTest.riskCalculationDao,times(2)).checkForPayment(any(String.class),any(Date.class));
        verify(riskCalculationServiceUnderTest.riskCalculationDao,times(1)).getRiskclassOnNdia(any(Integer.class));
        verify(riskCalculationServiceUnderTest.riskCalculationDao,times(1)).getNPRiskClass();
        verify(riskCalculationServiceUnderTest.riskCalculationDao,times(1)).getNDIAOnRiskClass(any(String.class));
        verify(riskCalculationServiceUnderTest.riskCalculationDao,times(1)).getTotalPaymentSinceLastDue(any(String.class),any(Date.class),any(Date.class));
        verify(riskCalculationServiceUnderTest.riskCalculationDao,times(1)).addDetailsToDelinquentAccountTable(any(DelinquentAccountBean.class));
        verify(riskCalculationServiceUnderTest.commonRepo,times(1)).insertIntoDelinquentHistory(any(StringBuffer.class),any(String.class),any(String.class));
    }
}







//        when(riskCalculationServiceUnderTest.commonRepo.getDueAmountList(any(StringBuffer.class))).thenReturn(stringDoubleHashMap);
//        when(riskCalculationServiceUnderTest.riskCalculationDao.getMinimumPaymentExistStatementDate(any(StringBuffer.class), eq(1))).thenReturn(new ArrayList<>(List.of("value")));
//        when(riskCalculationServiceUnderTest.riskCalculationDao.getTotalPaymentSinceLastDue("accNo", new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime(), new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())).thenReturn(0.0);
//        when(riskCalculationServiceUnderTest.riskCalculationDao.checkLeastMinimumPayment("accNo")).thenReturn(0.0);
//        when(riskCalculationServiceUnderTest.riskCalculationDao.getDelinquentAccountDetailsAsList("accNo")).thenReturn(List.of(new BigDecimal("0.00")));
//        when(riskCalculationServiceUnderTest.riskCalculationDao.updateAccountStatus("accNo", "backendStatus")).thenReturn(0);
//        when(riskCalculationServiceUnderTest.riskCalculationDao.updateOnlineAccountStatus("accNo", 0)).thenReturn(0);

//        when(riskCalculationServiceUnderTest.riskCalculationDao.getDueDateList(any(StringBuffer.class))).thenReturn(stringDateHashMap);
//        when(riskCalculationServiceUnderTest.riskCalculationDao.updateMinimumPayment(any(StringBuffer.class), eq(new HashMap<>(Map.ofEntries(Map.entry("value", 0.0)))), eq(new HashMap<>(Map.ofEntries(Map.entry("value", new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())))), eq(0))).thenReturn(0);
//
//        // Configure RiskCalculationDao.getDueDateOnRiskClass(...).
//
//        when(riskCalculationServiceUnderTest.riskCalculationDao.getDueDateOnRiskClass(eq(1), any(StringBuffer.class))).thenReturn(date);
//        when(riskCalculationServiceUnderTest.riskCalculationDao.getRiskclassOnNdia(0)).thenReturn(new String[]{"result"});
//        when(riskCalculationServiceUnderTest.statusVarList.getTO_ACTIVE_STATUS()).thenReturn("result");
//        when(riskCalculationServiceUnderTest.statusVarList.getTO_NON_PERFORMING_TO_NON_PERFORMING_STATUS()).thenReturn("result");
//        when(riskCalculationServiceUnderTest.statusVarList.getTO_NON_PERFORMING_TO_PERFORMING_STATUS()).thenReturn("result");
//        when(riskCalculationServiceUnderTest.statusVarList.getTO_PERFORMING_TO_NON_PERFORMING_STATUS()).thenReturn("result");
//        when(riskCalculationServiceUnderTest.statusVarList.getTO_PERFORMING_TO_PERFORMING_STATUS()).thenReturn("result");
//        when(riskCalculationServiceUnderTest.statusVarList.getACCOUNT_NON_PERFORMING_STATUS()).thenReturn("result");
//        when(riskCalculationServiceUnderTest.riskCalculationDao.getNPRiskClass()).thenReturn("riskClass");
//        when(riskCalculationServiceUnderTest.riskCalculationDao.getNDIAOnRiskClass("riskClass")).thenReturn(new String[]{"result"});
//        when(riskCalculationServiceUnderTest.statusVarList.getONLINE_DEACTIVE_STATUS()).thenReturn(0);
//        when(riskCalculationServiceUnderTest.logManager.logStartEnd("RISK_CALCULATION_PROCESS Process completed for existing cards with errors")).thenReturn("result");

//verify(riskCalculationServiceUnderTest.riskCalculationDao,times(1)).updateAllDELINQUENTACCOUNTnpdetails(any(Double.class),any(Double.class),any(Double.class),any(Double.class),any(Double.class),any(Double.class),any(String.class));

//        verify(riskCalculationServiceUnderTest.riskCalculationDao).updateAllDELINQUENTACCOUNTnpdetails(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "accNo");
//        verify(riskCalculationServiceUnderTest.riskCalculationDao).insertIntoEodGLAccountBigDecimal(eq(0), eq(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime()), any(StringBuffer.class), eq("KNOCKOFF_NPINTEREST_GL"), eq(new BigDecimal("0.00")), eq("DEBIT"), eq("payType"));
//        verify(riskCalculationServiceUnderTest.riskCalculationDao).updateAllDELINQUENTACCOUNTnpdetails(new BigDecimal("0.00"), new BigDecimal("0.00"), new BigDecimal("0.00"), new BigDecimal("0.00"), new BigDecimal("0.00"), new BigDecimal("0.00"), "accNo");
//        verify(riskCalculationServiceUnderTest.riskCalculationDao).updateNpStatusCardAccount("accNo", 0);
//        verify(riskCalculationServiceUnderTest.riskCalculationDao).addDetailsToDelinquentAccountTable(any(DelinquentAccountBean.class));
//        verify(riskCalculationServiceUnderTest.commonRepo).insertIntoDelinquentHistory(any(StringBuffer.class), eq("accNo"), eq("remark"));
//        verify(riskCalculationServiceUnderTest.riskCalculationDao).getNPDetailsFromLastBillingStatement(any(DelinquentAccountBean.class), eq(false));
//        verify(riskCalculationServiceUnderTest.riskCalculationDao).insertIntoEodGLAccount(eq(0), eq(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime()), any(StringBuffer.class), eq("INTEREST_ON_THE_NP_GL"), eq(0.0), eq("DEBIT"), eq("payType"));
//        verify(riskCalculationServiceUnderTest.riskCalculationDao).updateProvisionInDELINQUENTACCOUNT(new BigDecimal("0.00"), "accNo");
