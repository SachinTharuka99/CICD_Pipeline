package com.epic.cms.service;

import com.epic.cms.dao.TransactionPostDao;
import com.epic.cms.model.bean.OtbBean;
import com.epic.cms.repository.CommonRepo;
import com.epic.cms.util.Configurations;
import com.epic.cms.util.LogManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TransactionPostServiceTest {

    private TransactionPostService transactionPostServiceUnderTest;
    int capacity = 200000;
    BlockingQueue<Integer> successCount = new ArrayBlockingQueue<Integer>(capacity);
    BlockingQueue<Integer> failCount = new ArrayBlockingQueue <Integer>(capacity);

    @BeforeEach
    void setUp() {
        transactionPostServiceUnderTest = new TransactionPostService();
        transactionPostServiceUnderTest.transactionPostDao = mock(TransactionPostDao.class);
        transactionPostServiceUnderTest.logManager = mock(LogManager.class);
        transactionPostServiceUnderTest.commonRepo = mock(CommonRepo.class);
    }

    @Test
    void testTransactionList() throws Exception {
        // Setup
        final OtbBean bean = new OtbBean();
        bean.setAccountnumber("accountnumber");
        bean.setCardnumber(new StringBuffer("4890118864436725"));
        bean.setCustomerid("customerid");
        bean.setOtbcredit(0.0);
        bean.setOtbcash(0.0);
        bean.setTmpcredit(0.0);
        bean.setTmpcash(0.0);
        bean.setPayment(20.0);
        bean.setSale(0.0);
        bean.setCashadavance(0.0);
        bean.setEasypayrev(0.0);
        bean.setEasypay(0.0);
        bean.setEasypayfee(0.0);
        bean.setMvisaRefund(20.0);
        bean.setRefund(0.0);
        bean.setReversal(0.0);
        bean.setMoneysend(0.0);
        bean.setMoneysendreversal(0.0);
        bean.setAft(20.0);

        final ArrayList<OtbBean> txnList = new ArrayList<OtbBean>();
        txnList.add(bean);

        Configurations.START_INDEX = 6;
        Configurations.END_INDEX =12;
        Configurations.PATTERN_CHAR = "*";
        Configurations.EOD_ID = 12112;
        Configurations.EOD_USER = "user";


        when(transactionPostServiceUnderTest.transactionPostDao.getTxnAmount(any())).thenReturn(txnList);
        when(transactionPostServiceUnderTest.commonRepo.getNewCardNumber(any())).thenReturn(new StringBuffer("4890118864436725"));
        when(transactionPostServiceUnderTest.transactionPostDao.updateCardTemp(new StringBuffer("4890118864436725"),20.0)).thenReturn(1);
        when(transactionPostServiceUnderTest.transactionPostDao.updateCardOtbCredit(any(OtbBean.class))).thenReturn(1);
        when(transactionPostServiceUnderTest.transactionPostDao.updateAccountOtbCredit(any(OtbBean.class))).thenReturn(1);
        when(transactionPostServiceUnderTest.transactionPostDao.updateCustomerOtbCredit(any(OtbBean.class))).thenReturn(1);
        when(transactionPostServiceUnderTest.transactionPostDao.updateCardByPostedTransactions(any(OtbBean.class))).thenReturn(1);
        when(transactionPostServiceUnderTest.transactionPostDao.updateEODCARDBALANCEByTxn(any(OtbBean.class))).thenReturn(1);
        when(transactionPostServiceUnderTest.transactionPostDao.updateAccountOtb(any(OtbBean.class))).thenReturn(1);
        when(transactionPostServiceUnderTest.transactionPostDao.updateCustomerOtb(any(OtbBean.class))).thenReturn(1);
        when(transactionPostServiceUnderTest.transactionPostDao.updateEODTRANSACTION(any(String.class))).thenReturn(1);

        // Run the test
<<<<<<< Updated upstream
        transactionPostServiceUnderTest.transactionList(bean, Configurations.successCount, Configurations.failCount);
=======
        transactionPostServiceUnderTest.transactionList(bean, successCount, failCount);
>>>>>>> Stashed changes

        // Verify the results
        verify(transactionPostServiceUnderTest.transactionPostDao,times(1)).getTxnAmount(any());
        verify(transactionPostServiceUnderTest.transactionPostDao,times(2)).updateCardTemp(any(StringBuffer.class),any(Double.class));
        verify(transactionPostServiceUnderTest.transactionPostDao,times(1)).updateCardOtbCredit(any(OtbBean.class));
        verify(transactionPostServiceUnderTest.transactionPostDao,times(1)).updateAccountOtbCredit(any(OtbBean.class));
        verify(transactionPostServiceUnderTest.transactionPostDao,times(1)).updateCustomerOtbCredit(any(OtbBean.class));
        verify(transactionPostServiceUnderTest.transactionPostDao,times(1)).updateCardByPostedTransactions(any(OtbBean.class));
        verify(transactionPostServiceUnderTest.transactionPostDao,times(1)).updateEODCARDBALANCEByTxn(any(OtbBean.class));
        verify(transactionPostServiceUnderTest.transactionPostDao,times(1)).updateAccountOtb(any(OtbBean.class));
        verify(transactionPostServiceUnderTest.transactionPostDao,times(1)).updateCustomerOtb(any(OtbBean.class));
        verify(transactionPostServiceUnderTest.transactionPostDao,times(1)).updateEODTRANSACTION(any(String.class));

    }

}
