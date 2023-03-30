package com.epic.cms.service;

import com.epic.cms.connector.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@ComponentScan(basePackages = {"com.epic.cms.*"})
public class ConsumerService {
    @Autowired
    ATMFileClearingConnector atmFileClearingConnector;
    @Autowired
    PaymentFileClearingConnector paymentFileClearingConnector;
    @Autowired
    VisaBaseIIFileClearingConnector visaBaseIIFileClearingConnector;
    @Autowired
    MasterFileClearingConnector masterFileClearingConnector;

    @KafkaListener(topics = "ATMFileClearing", groupId = "group_ATMFileClearing", containerFactory = "kafkaListenerContainerFactory")
    public void ATMFileClearingConsumer(String fileId) throws Exception {
        System.out.println("Start ATM File Clearing Process");
        atmFileClearingConnector.startProcess(fileId);
        System.out.println("Complete ATM File Clearing Process");
    }

    @KafkaListener(topics = "PaymentFileClearing", groupId = "group_PaymentFileClearing")
    public void paymentFileClearingConsumer(String fileId) throws Exception {
        System.out.println("Start Payment File Clearing Process");
        paymentFileClearingConnector.startProcess(fileId);
        System.out.println("Complete Payment File Clearing Process");
    }

    @KafkaListener(topics = "VisaFileClearing", groupId = "group_VisaFileClearing")
    public void visaFileClearingConsumer(String fileId) throws Exception {
        System.out.println("Start Visa File Clearing Process");
        visaBaseIIFileClearingConnector.startProcess(fileId);
        System.out.println("Complete Visa File Clearing Process");
    }

    @KafkaListener(topics = "MasterFileClearing", groupId = "group_MasterFileClearing")
    public void masterFileClearingConsumer(String fileId) throws Exception {
        System.out.println("Start Visa File Clearing Process");
        masterFileClearingConnector.startProcess(fileId);
        System.out.println("Complete Visa File Clearing Process");
    }
}
