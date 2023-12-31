/**
 * Author :
 * Date : 2/3/2023
 * Time : 11:35 PM
 * Project Name : ecms_eod_file_processing_engine
 */

package com.epic.cms.model.bean;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Getter
@Setter
public class MasterFieldsDataBean {
    private String sessionid;
    private String fileid;
    private String lineNumber;
    private String txnId;
    private String status;
    private String mti;
    private StringBuffer pan;
    private String processingCode;
    private String txnAmount;
    private String reconAmount;
    private String billingAmount;
    private String transmissionTime;
    private String billingFee;
    private String billingConversionRate;
    private String reconConversionRate;
    private String traceNumber;
    private String localTransactionTime;
    private String effectiveDate;
    private String expirationDate;
    private String settlementDate;
    private String conversionDate;
    private String captureDate;
    private String merchantType;
    private String pancountryCode;
    private String acqureCountryCode;
    private String forwardingCountryCode;
    private String posCode;
    private String cardSeqNumber;
    private String originalAmounts;
    private String acquirerInstituteId;
    private String forwadingInstituteId;
    private String acquirerRefData;
    private String reconDate;
    private String reconIndicator;
    private String functionCode;
    private String messageReasonCode;
    private String acceptorBusinessCode;
    private String approvalCodeLength;
    private String extendedPan;
    private String track2Data;
    private String track3Data;
    private String rrNumber;
    private String approvalCode;
    private String actionCode;
    private String serviceCode;
    private String acceptorTerminalId;
    private String acceptoerId;
    private String acceptorName;
    private String additionalResponseData;
    private String track1Data;
    private String fees;
    private String nationalAdditionalData;
    private String privateAdditionalData;
    private String txnCurrencyCode;
    private String reconCurrencyCode;
    private String billingCurrencyCode;
    private String pinData;
    private String securityInformation;
    private String additionalAmounts;
    private String iccData;
    private String originalDataElements;
    private String authCode;
    private String authInstituteId;
    private String transportData;
    private String reservedForNational1;
    private String reservedForNational2;
    private String additionalData2;
    private String txnLifeCycleId;
    private String macField1;
    private String reservedIso1;
    private String originalFees;
    private String extendedPayment;
    private String receivingCountryCode;
    private String settlementCountryCode;
    private String authCountryCode;
    private String messageNumber;
    private String dataRecord;
    private String actionDate;
    private String creditsNo;
    private String creditsReversalNo;
    private String debitsNo;
    private String debitsReversalNo;
    private String transferNo;
    private String transferReversalNo;
    private String inquiriesNo;
    private String authorizationsNo;
    private String inquiriesReversalNo;
    private String paymentsNo;
    private String paymentsReversalNo;
    private String feeCollectionNo;
    private String creditsAmount;
    private String creditsReversalAmount;
    private String debitsAmount;
    private String debitsReversalAmount;
    private String authReversalNo;
    private String txnDestinationCountryCode;
    private String txnSourceCountryCode;
    private String txnDestinationCode;
    private String txnSourceCode;
    private String issuerRefData;
    private String keyMgtData;
    private String netReconAmount;
    private String payee;
    private String settlementInstituteId;
    private String receivingInstituteId;
    private String fileName;
    private String accountIdentification1;
    private String accountIdentification2;
    private String txnDescription;
    private String creditchargebackamount;
    private String debitChargebackAmount;
    private String creditChargebackNo;
    private String debitChargebackNo;
    private String creditFeeAmounts;
    private String debitFeeAmounts;
    private String conversionAssessAmount;
    private String reservedIso2;
    private String reservedIso3;
    private String reservedIso4;
    private String reservedIso5;
    private String reservedForNational3;
    private String reservedForNational4;
    private String reservedForNational5;
    private String reservedForNational6;
    private String reservedForNational7;
    private String reservedForNational8;
    private String reservedForNational9;
    private String additionalData3;
    private String additionalData4;
    private String additionalData5;
    private String reservedPrivate;
    private String networkData;
    private String macField2;
    private String reversalPds;
    private String reversalData;
    private String txnTypeProcessingCode;
    private String txnDate;
    private String txnTime;
    private String merchantName;
    private String merchantCity;
    private String merchantCountryCode;

    private HashMap<String, String> currencyExponentList;
}
