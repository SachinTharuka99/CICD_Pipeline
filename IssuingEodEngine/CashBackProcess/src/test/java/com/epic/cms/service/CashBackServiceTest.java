package com.epic.cms.service;

import com.epic.cms.model.bean.CashBackBean;
import com.epic.cms.repository.CashBackRepo;
import com.epic.cms.repository.CommonRepo;
import com.epic.cms.util.Configurations;
import com.epic.cms.util.LogManager;
import com.epic.cms.util.StatusVarList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CashBackServiceTest {
    int capacity = 200000;
    BlockingQueue<Integer> successCount = new ArrayBlockingQueue<Integer>(capacity);
    BlockingQueue<Integer> failCount = new ArrayBlockingQueue<Integer>(capacity);
    private CashBackService cashBackServiceUnderTest;


    @BeforeEach
    void setUp() {
        cashBackServiceUnderTest = new CashBackService();
        cashBackServiceUnderTest.logManager = mock(LogManager.class);
        cashBackServiceUnderTest.cashBackRepo = mock(CashBackRepo.class);
        cashBackServiceUnderTest.status = mock(StatusVarList.class);
        cashBackServiceUnderTest.commonRepo = mock(CommonRepo.class);
    }

    @Test
    void testCashBack() {
        // Setup
        final CashBackBean cashbackBean = new CashBackBean();
        cashbackBean.setAccountNumber("4862950000698568");
        cashbackBean.setAccountStatus("accountStatus");
        cashbackBean.setMainCardNumber(new StringBuffer("4862950000698568"));
        cashbackBean.setMainCardStatus("mainCardStatus");
        cashbackBean.setStatementDate(new Date(System.currentTimeMillis()));
        cashbackBean.setCashbackProfileCode("cashbackProfileCode");
        cashbackBean.setCashbackStartDate(new Date(System.currentTimeMillis()));
        cashbackBean.setNextCashbackStartDate(new Date(System.currentTimeMillis()));
        cashbackBean.setNextCBRedeemDate(new Date(System.currentTimeMillis()));
        cashbackBean.setAvailableCashbackAmount(new BigDecimal("0.00"));
        cashbackBean.setRedeemRatio(0.0);
        cashbackBean.setMinSpendPerMonth(0.0);
        cashbackBean.setMaxCashbackPerYear(0.0);
        cashbackBean.setCashbackRate(10.0);
        cashbackBean.setCreditOption("creditOption");

        Configurations.EOD_DATE = new java.util.Date(System.currentTimeMillis());

        when(cashBackServiceUnderTest.status.getDEACTIVE_STATUS()).thenReturn("DEA");
        when(cashBackServiceUnderTest.status.getACCOUNT_NON_PERFORMING_STATUS()).thenReturn("NP");
        when(cashBackServiceUnderTest.status.getCARD_CLOSED_STATUS()).thenReturn("CACL");
        when(cashBackServiceUnderTest.status.getCARD_EXPIRED_STATUS()).thenReturn("CAEX");
        when(cashBackServiceUnderTest.status.getCARD_REPLACED_STATUS()).thenReturn("CARP");
        when(cashBackServiceUnderTest.status.getCARD_PRODUCT_CHANGE_STATUS()).thenReturn("CAPC");
        when(cashBackServiceUnderTest.status.getBILLING_DONE_STATUS()).thenReturn("result");
        when(cashBackServiceUnderTest.cashBackRepo.getCashbackAmount(any(CashBackBean.class))).thenReturn(new BigDecimal("0.00"));
        when(cashBackServiceUnderTest.cashBackRepo.getCashbackAdjustmentAmount(any(CashBackBean.class))).thenReturn(new BigDecimal("0.00"));
        when(cashBackServiceUnderTest.cashBackRepo.addNewCashBack(any(CashBackBean.class), eq(new BigDecimal("0.00")), eq(new BigDecimal("0.00")), eq("txnVolume"))).thenReturn(0);
        when(cashBackServiceUnderTest.cashBackRepo.updateCashbackAdjustmentStatus(anyString(), anyString())).thenReturn(0);
        when(cashBackServiceUnderTest.cashBackRepo.getRedeemRequestAmount("4862950000698568")).thenReturn(new BigDecimal("10.00"));
        when(cashBackServiceUnderTest.cashBackRepo.redeemCashbacks(any(CashBackBean.class), eq(new BigDecimal("0.00")))).thenReturn(0);
        when(cashBackServiceUnderTest.cashBackRepo.updateEodStatusInCashbackRequest("accountNumber")).thenReturn(0);
        when(cashBackServiceUnderTest.cashBackRepo.getRedeemableAmount(any(CashBackBean.class))).thenReturn(new BigDecimal("10.00"));
        when(cashBackServiceUnderTest.cashBackRepo.updateNextCBRedeemDate("accountNumber", "creditOption")).thenReturn(0);
        when(cashBackServiceUnderTest.cashBackRepo.updateTotalCBAmount("accountNumber")).thenReturn(0);

        // Run the test
<<<<<<< Updated upstream
        cashBackServiceUnderTest.cashBack(cashbackBean,Configurations.successCount,Configurations.failCount);
=======
        cashBackServiceUnderTest.cashBack(cashbackBean,successCount,failCount);
>>>>>>> Stashed changes

        // Verify the results

        assertEquals(new BigDecimal("0.00"),cashBackServiceUnderTest.cashBackRepo.getCashbackAmount(cashbackBean));
        assertEquals(new BigDecimal("0.00"),cashBackServiceUnderTest.cashBackRepo.getCashbackAdjustmentAmount(cashbackBean));
        assertEquals(new BigDecimal("10.00"),cashBackServiceUnderTest.cashBackRepo.getRedeemRequestAmount("4862950000698568"));

        //verify(cashBackServiceUnderTest.cashBackRepo,times(1)).getCashbackAmount(any(CashBackBean.class));
        //verify(cashBackServiceUnderTest.cashBackRepo,times(1)).getCashbackAdjustmentAmount(any(CashBackBean.class));
        verify(cashBackServiceUnderTest.cashBackRepo,times(1)).addNewCashBack(any(CashBackBean.class), any(BigDecimal.class), any(BigDecimal.class), anyString());
        verify(cashBackServiceUnderTest.cashBackRepo,times(1)).updateCashbackAdjustmentStatus(anyString(), anyString());
        verify(cashBackServiceUnderTest.cashBackRepo,times(1)).updateNextCBRedeemDate(anyString(), anyString());
        verify(cashBackServiceUnderTest.cashBackRepo,times(1)).updateTotalCBAmount(anyString());
        verify(cashBackServiceUnderTest.cashBackRepo,times(1)).updateCashbackStartDate(anyString(), any(Date.class));
        //verify(cashBackServiceUnderTest.cashBackRepo,times(1)).getRedeemRequestAmount("4862950000698568");
        verify(cashBackServiceUnderTest.cashBackRepo,times(2)).redeemCashbacks(any(CashBackBean.class), any(BigDecimal.class));
        verify(cashBackServiceUnderTest.cashBackRepo,times(1)).updateEodStatusInCashbackRequest(anyString());
        verify(cashBackServiceUnderTest.cashBackRepo,times(1)).getRedeemableAmount(any(CashBackBean.class));

    }

    @Test
    void testAccountIsNonPerforming(){
        // Setup
        final CashBackBean cashbackBean = new CashBackBean();
        cashbackBean.setAccountNumber("4862950000698568");
        cashbackBean.setAccountStatus("ABC");
        cashbackBean.setMainCardNumber(new StringBuffer("4862950000698568"));
        cashbackBean.setMainCardStatus("CACL");
        cashbackBean.setStatementDate(null);
        cashbackBean.setCashbackProfileCode("cashbackProfileCode");
        cashbackBean.setCashbackStartDate(new Date(System.currentTimeMillis()));
        cashbackBean.setNextCashbackStartDate(new Date(System.currentTimeMillis()));
        cashbackBean.setNextCBRedeemDate(new Date(System.currentTimeMillis()));
        cashbackBean.setAvailableCashbackAmount(new BigDecimal("0.00"));
        cashbackBean.setRedeemRatio(0.0);
        cashbackBean.setMinSpendPerMonth(0.0);
        cashbackBean.setMaxCashbackPerYear(0.0);
        cashbackBean.setCashbackRate(0.0);
        cashbackBean.setCreditOption("creditOption");

        Configurations.EOD_DATE = new java.util.Date(System.currentTimeMillis());

        when(cashBackServiceUnderTest.status.getDEACTIVE_STATUS()).thenReturn("ABC");
        when(cashBackServiceUnderTest.status.getACCOUNT_NON_PERFORMING_STATUS()).thenReturn("NP");
        when(cashBackServiceUnderTest.status.getCARD_CLOSED_STATUS()).thenReturn("CACL");
        when(cashBackServiceUnderTest.cashBackRepo.expireNonPerformingCashbacks(any(CashBackBean.class), eq(new BigDecimal("0.00")))).thenReturn(0);
        when(cashBackServiceUnderTest.cashBackRepo.getCashbackAmountToBeExpireForAccount("4862950000698568")).thenReturn(new BigDecimal("10.00"));
        when(cashBackServiceUnderTest.cashBackRepo.expireCardCloseCashbacks(any(CashBackBean.class), eq(new BigDecimal("0.00")))).thenReturn(0);
        when(cashBackServiceUnderTest.cashBackRepo.expireCashbacks(any(CashBackBean.class))).thenReturn(0);

        // Run the test
        cashBackServiceUnderTest.cashBack(cashbackBean,successCount,failCount);

        verify(cashBackServiceUnderTest.cashBackRepo,times(1)).expireNonPerformingCashbacks(any(CashBackBean.class), any(BigDecimal.class));
        verify(cashBackServiceUnderTest.cashBackRepo,times(1)).getCashbackAmountToBeExpireForAccount("4862950000698568");
        verify(cashBackServiceUnderTest.cashBackRepo,times(1)).updateTotalCBAmount(anyString());

    }

    @Test
    void testMainCardIsClose(){
        // Setup
        final CashBackBean cashbackBean = new CashBackBean();
        cashbackBean.setAccountNumber("4862950000698568");
        cashbackBean.setAccountStatus("accountStatus");
        cashbackBean.setMainCardNumber(new StringBuffer("4862950000698568"));
        cashbackBean.setMainCardStatus("CACL");
        cashbackBean.setStatementDate(null);
        cashbackBean.setCashbackProfileCode("cashbackProfileCode");
        cashbackBean.setCashbackStartDate(new Date(System.currentTimeMillis()));
        cashbackBean.setNextCashbackStartDate(new Date(System.currentTimeMillis()));
        cashbackBean.setNextCBRedeemDate(new Date(System.currentTimeMillis()));
        cashbackBean.setAvailableCashbackAmount(new BigDecimal("0.00"));
        cashbackBean.setRedeemRatio(0.0);
        cashbackBean.setMinSpendPerMonth(0.0);
        cashbackBean.setMaxCashbackPerYear(0.0);
        cashbackBean.setCashbackRate(0.0);
        cashbackBean.setCreditOption("creditOption");

        Configurations.EOD_DATE = new java.util.Date(System.currentTimeMillis());


        when(cashBackServiceUnderTest.status.getDEACTIVE_STATUS()).thenReturn("ABC");
        when(cashBackServiceUnderTest.status.getACCOUNT_NON_PERFORMING_STATUS()).thenReturn("NP");
        when(cashBackServiceUnderTest.status.getCARD_CLOSED_STATUS()).thenReturn("CACL");
        when(cashBackServiceUnderTest.cashBackRepo.expireNonPerformingCashbacks(any(CashBackBean.class), eq(new BigDecimal("0.00")))).thenReturn(0);
        when(cashBackServiceUnderTest.cashBackRepo.getCashbackAmountToBeExpireForAccount("4862950000698568")).thenReturn(new BigDecimal("10.00"));
        when(cashBackServiceUnderTest.cashBackRepo.expireCardCloseCashbacks(any(CashBackBean.class), eq(new BigDecimal("0.00")))).thenReturn(0);
        when(cashBackServiceUnderTest.cashBackRepo.expireCashbacks(any(CashBackBean.class))).thenReturn(0);

        // Run the test
        cashBackServiceUnderTest.cashBack(cashbackBean,successCount,failCount);

        verify(cashBackServiceUnderTest.cashBackRepo,times(1)).expireCardCloseCashbacks(any(CashBackBean.class), any(BigDecimal.class));
        verify(cashBackServiceUnderTest.cashBackRepo,times(1)).getCashbackAmountToBeExpireForAccount("4862950000698568");
        verify(cashBackServiceUnderTest.cashBackRepo,times(1)).updateTotalCBAmount(anyString());

    }
}
