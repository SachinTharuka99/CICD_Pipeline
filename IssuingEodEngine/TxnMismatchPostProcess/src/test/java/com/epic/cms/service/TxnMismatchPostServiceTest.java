package com.epic.cms.service;

import com.epic.cms.model.bean.OtbBean;
import com.epic.cms.repository.CommonRepo;
import com.epic.cms.util.Configurations;
import com.epic.cms.util.LogManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TxnMismatchPostServiceTest {

    private TxnMismatchPostService txnMismatchPostServiceUnderTest;

<<<<<<< Updated upstream
=======
  //  public AtomicInteger faileCardCount = new AtomicInteger(0);
>>>>>>> Stashed changes
  int capacity = 200000;
    BlockingQueue<Integer> successCount = new ArrayBlockingQueue<Integer>(capacity);
    BlockingQueue<Integer> failCount = new ArrayBlockingQueue <Integer>(capacity);

    @BeforeEach
    void setUp() {
        txnMismatchPostServiceUnderTest = new TxnMismatchPostService();
        txnMismatchPostServiceUnderTest.logManager = mock(LogManager.class);
        txnMismatchPostServiceUnderTest.commonRepo = mock(CommonRepo.class);
    }

    @Test
    @DisplayName("Process Txn Mismatch Process Test One")
    void testProcessTxnMismatchTestOne() throws Exception {
        // Setup
        final OtbBean otbBean = new OtbBean();
        otbBean.setAccountnumber("accountnumber");
        otbBean.setCardnumber(new StringBuffer("4380433458012"));
        otbBean.setCustomerid("customerid");
        otbBean.setOtbcredit(0.0);
        otbBean.setOtbcash(0.0);
        otbBean.setFinacialcharges(0.0);
        otbBean.setCumpayment(0.0);
        otbBean.setCumcashadvance(0.0);
        otbBean.setCumtransactions(0.0);
        otbBean.setTmpcredit(0.0);
        otbBean.setTmpcash(0.0);
        otbBean.setTxnAmount(0.0);
        otbBean.setTxntype("123456");
        otbBean.setTxntypedesc("txntypedesc");
        otbBean.setPayment(0.0);
        final ArrayList<OtbBean> txnList = new ArrayList<>(List.of(otbBean));
        final OtbBean bean = new OtbBean();
        bean.setAccountnumber("accountnumber");
        bean.setCardnumber(new StringBuffer("value"));
        bean.setCustomerid("customerid");
        bean.setOtbcredit(0.0);
        bean.setOtbcash(0.0);
        bean.setFinacialcharges(0.0);
        bean.setCumpayment(0.0);
        bean.setCumcashadvance(0.0);
        bean.setCumtransactions(0.0);
        bean.setTmpcredit(0.0);
        bean.setTmpcash(0.0);
        bean.setTxnAmount(0.0);
        bean.setTxntype("123456");
        bean.setTxntypedesc("txntypedesc");
        bean.setPayment(0.0);

        Configurations.TXN_TYPE_PAYMENT = "123456";

        when(txnMismatchPostServiceUnderTest.commonRepo.getNewCardNumber(any(StringBuffer.class)))
                .thenReturn(new StringBuffer("4380433458012"));

        // Run the test
<<<<<<< Updated upstream
        txnMismatchPostServiceUnderTest.processTxnMismatch(txnList, bean, 0, Configurations.successCount, Configurations.failCount);
=======
        txnMismatchPostServiceUnderTest.processTxnMismatch(txnList, bean, 0, successCount, failCount);
>>>>>>> Stashed changes

        // Verify the results
        verify(txnMismatchPostServiceUnderTest.commonRepo, times(1)).updateCardOtb(any(OtbBean.class));
        verify(txnMismatchPostServiceUnderTest.commonRepo, times(1)).updateAccountOtb(any(OtbBean.class));
        verify(txnMismatchPostServiceUnderTest.commonRepo, times(1)).updateCustomerOtb(any(OtbBean.class));
    }

    @Test
    @DisplayName("Process Txn Mismatch Process Test Two")
    void testProcessTxnMismatchTestTwo() throws Exception {
        // Setup
        final OtbBean otbBean = new OtbBean();
        otbBean.setAccountnumber("accountnumber");
        otbBean.setCardnumber(new StringBuffer("4380433458012"));
        otbBean.setCustomerid("customerid");
        otbBean.setOtbcredit(0.0);
        otbBean.setOtbcash(0.0);
        otbBean.setFinacialcharges(0.0);
        otbBean.setCumpayment(0.0);
        otbBean.setCumcashadvance(0.0);
        otbBean.setCumtransactions(0.0);
        otbBean.setTmpcredit(0.0);
        otbBean.setTmpcash(0.0);
        otbBean.setTxnAmount(0.0);
        otbBean.setTxntype("123456");
        otbBean.setTxntypedesc("txntypedesc");
        otbBean.setPayment(0.0);
        final ArrayList<OtbBean> txnList = new ArrayList<>(List.of(otbBean));
        final OtbBean bean = new OtbBean();
        bean.setAccountnumber("accountnumber");
        bean.setCardnumber(new StringBuffer("value"));
        bean.setCustomerid("customerid");
        bean.setOtbcredit(0.0);
        bean.setOtbcash(0.0);
        bean.setFinacialcharges(0.0);
        bean.setCumpayment(0.0);
        bean.setCumcashadvance(0.0);
        bean.setCumtransactions(0.0);
        bean.setTmpcredit(0.0);
        bean.setTmpcash(0.0);
        bean.setTxnAmount(0.0);
        bean.setTxntype("123456");
        bean.setTxntypedesc("txntypedesc");
        bean.setPayment(0.0);

        Configurations.TXN_TYPE_SALE = "123456";
        Configurations.TXN_TYPE_MVISA_ORIGINATOR = "123456";

        when(txnMismatchPostServiceUnderTest.commonRepo.getNewCardNumber(any(StringBuffer.class)))
                .thenReturn(new StringBuffer("4380433458012"));

        // Run the test
        txnMismatchPostServiceUnderTest.processTxnMismatch(txnList, bean, 0, successCount, failCount);

        // Verify the results
        verify(txnMismatchPostServiceUnderTest.commonRepo, times(1)).updateCardOtb(any(OtbBean.class));
        verify(txnMismatchPostServiceUnderTest.commonRepo, times(1)).updateAccountOtb(any(OtbBean.class));
        verify(txnMismatchPostServiceUnderTest.commonRepo, times(1)).updateCustomerOtb(any(OtbBean.class));
    }

    @Test
    @DisplayName("Process Txn Mismatch Process Test Three")
    void testProcessTxnMismatchTestThree() throws Exception {
        // Setup
        final OtbBean otbBean = new OtbBean();
        otbBean.setAccountnumber("accountnumber");
        otbBean.setCardnumber(new StringBuffer("4380433458012"));
        otbBean.setCustomerid("customerid");
        otbBean.setOtbcredit(0.0);
        otbBean.setOtbcash(0.0);
        otbBean.setFinacialcharges(0.0);
        otbBean.setCumpayment(0.0);
        otbBean.setCumcashadvance(0.0);
        otbBean.setCumtransactions(0.0);
        otbBean.setTmpcredit(0.0);
        otbBean.setTmpcash(0.0);
        otbBean.setTxnAmount(0.0);
        otbBean.setTxntype("123456");
        otbBean.setTxntypedesc("txntypedesc");
        otbBean.setPayment(0.0);
        final ArrayList<OtbBean> txnList = new ArrayList<>(List.of(otbBean));
        final OtbBean bean = new OtbBean();
        bean.setAccountnumber("accountnumber");
        bean.setCardnumber(new StringBuffer("value"));
        bean.setCustomerid("customerid");
        bean.setOtbcredit(0.0);
        bean.setOtbcash(0.0);
        bean.setFinacialcharges(0.0);
        bean.setCumpayment(0.0);
        bean.setCumcashadvance(0.0);
        bean.setCumtransactions(0.0);
        bean.setTmpcredit(0.0);
        bean.setTmpcash(0.0);
        bean.setTxnAmount(0.0);
        bean.setTxntype("123456");
        bean.setTxntypedesc("txntypedesc");
        bean.setPayment(0.0);

        Configurations.TXN_TYPE_REFUND = "123456";
        Configurations.TXN_TYPE_REVERSAL = "123456";
        Configurations.TXN_TYPE_MVISA_REFUND = "123456";
        Configurations.TXN_TYPE_MONEY_SEND = "123456";
        Configurations.TXN_TYPE_MONEY_SEND_REVERSAL = "123456";

        when(txnMismatchPostServiceUnderTest.commonRepo.getNewCardNumber(any(StringBuffer.class)))
                .thenReturn(new StringBuffer("4380433458012"));

        // Run the test
        txnMismatchPostServiceUnderTest.processTxnMismatch(txnList, bean, 0, successCount, failCount);

        // Verify the results
        verify(txnMismatchPostServiceUnderTest.commonRepo, times(1)).updateCardOtb(any(OtbBean.class));
        verify(txnMismatchPostServiceUnderTest.commonRepo, times(1)).updateAccountOtb(any(OtbBean.class));
        verify(txnMismatchPostServiceUnderTest.commonRepo, times(1)).updateCustomerOtb(any(OtbBean.class));
    }

    @Test
    @DisplayName("Process Txn Mismatch Process Test Four")
    void testProcessTxnMismatchTestFour() throws Exception {
        // Setup
        final OtbBean otbBean = new OtbBean();
        otbBean.setAccountnumber("accountnumber");
        otbBean.setCardnumber(new StringBuffer("4380433458012"));
        otbBean.setCustomerid("customerid");
        otbBean.setOtbcredit(0.0);
        otbBean.setOtbcash(0.0);
        otbBean.setFinacialcharges(0.0);
        otbBean.setCumpayment(0.0);
        otbBean.setCumcashadvance(0.0);
        otbBean.setCumtransactions(0.0);
        otbBean.setTmpcredit(0.0);
        otbBean.setTmpcash(0.0);
        otbBean.setTxnAmount(0.0);
        otbBean.setTxntype("123456");
        otbBean.setTxntypedesc("txntypedesc");
        otbBean.setPayment(0.0);
        final ArrayList<OtbBean> txnList = new ArrayList<>(List.of(otbBean));
        final OtbBean bean = new OtbBean();
        bean.setAccountnumber("accountnumber");
        bean.setCardnumber(new StringBuffer("value"));
        bean.setCustomerid("customerid");
        bean.setOtbcredit(0.0);
        bean.setOtbcash(0.0);
        bean.setFinacialcharges(0.0);
        bean.setCumpayment(0.0);
        bean.setCumcashadvance(0.0);
        bean.setCumtransactions(0.0);
        bean.setTmpcredit(0.0);
        bean.setTmpcash(0.0);
        bean.setTxnAmount(0.0);
        bean.setTxntype("123456");
        bean.setTxntypedesc("txntypedesc");
        bean.setPayment(0.0);

        Configurations.TXN_TYPE_CASH_ADVANCE = "123456";

        when(txnMismatchPostServiceUnderTest.commonRepo.getNewCardNumber(any(StringBuffer.class)))
                .thenReturn(new StringBuffer("4380433458012"));

        // Run the test
        txnMismatchPostServiceUnderTest.processTxnMismatch(txnList, bean, 0, successCount, failCount);

        // Verify the results
        verify(txnMismatchPostServiceUnderTest.commonRepo, times(1)).updateCardOtb(any(OtbBean.class));
        verify(txnMismatchPostServiceUnderTest.commonRepo, times(1)).updateAccountOtb(any(OtbBean.class));
        verify(txnMismatchPostServiceUnderTest.commonRepo, times(1)).updateCustomerOtb(any(OtbBean.class));
    }
}
