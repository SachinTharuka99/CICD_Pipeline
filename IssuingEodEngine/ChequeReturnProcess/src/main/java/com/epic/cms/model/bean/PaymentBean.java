package com.epic.cms.model.bean;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
public class PaymentBean {
    private int transactionid;
    private StringBuffer cardnumber;
    private String accountNo;
    private StringBuffer maincardno;
    private String isprimary;
    private String paymenttype;
    private String traceid;
    private double mainfinchargeknockoff;
    private double maincashadvanceknockoff;
    private double maintransactionknockoff;
    private double supfinchargeknockoff;
    private double supcashadvanceknockoff;
    private double suptransactionknockoff;
    private double forwardamount;
    private Date paymentdate;
    private String chequenumber;
    private String currencytype;
    private double amount;
    private String status;
    private String reference;
    private String cardholdername;
    private String chequebank;
    private int eodid;
    private String chequestatus;
    private int returnorrealizedeodid;
    private String bank;
    private String branch;
    private String remarks;
    private String transactiondesc;
    private String transactiontype;
    private Date transactiondatetime;
    private String sourceType;
    private String crdrmaintind;
    private String sequencenumber;
    private int INTERNAL_KEY;

    public PaymentBean(StringBuffer cardnumber, int eodid, String traceid, String transactiontype, String sequencenumber) {
        this.cardnumber = cardnumber;
        this.traceid = traceid;
        this.eodid = eodid;
        this.transactiontype = transactiontype;
        this.sequencenumber = sequencenumber;
    }

    public PaymentBean(StringBuffer cardnumber, int eodid, String traceid, String transactiontype, String sequencenumber, String accNo) {
        this.cardnumber = cardnumber;
        this.traceid = traceid;
        this.eodid = eodid;
        this.transactiontype = transactiontype;
        this.sequencenumber = sequencenumber;
        this.accountNo = accNo;
    }
}
