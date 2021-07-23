/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */
package rapide.iso20022.message.pacs008;

import com.prowidesoftware.swift.model.mx.BusinessAppHdrV02;
import com.prowidesoftware.swift.model.mx.MxPacs00800108;
import com.prowidesoftware.swift.model.mx.dic.*;
import lombok.extern.slf4j.Slf4j;
import rapide.iso20022.cli.config.ConfigProperties;
import rapide.iso20022.data.bic.repository.BICRepository;
import rapide.iso20022.data.lei.repository.LegalEntityRepository;
import rapide.iso20022.message.generators.fieldrandomizer.*;
import rapide.iso20022.message.generators.fieldrandomizer.Helper;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;

@Slf4j
public class Pacs008Message {

    private MxPacs00800108 message;
    private BICRepository bicRepository;
    private LegalEntityRepository legalEntityRepository;
    private ConfigProperties configProperties;

    private String messageId, uuid;
    private XMLGregorianCalendar messageTimestamp;
    private BranchAndFinancialInstitutionIdentification6Random instAgentFrom;
    private BranchAndFinancialInstitutionIdentification6Random instAgentTo;

    public Pacs008Message() {
        message = new MxPacs00800108();
    }

    public MxPacs00800108 generateRandomMessage(LegalEntityRepository legalEntityRepository,
                                                BICRepository bicRepository, ConfigProperties configProperties) throws DatatypeConfigurationException {
        this.bicRepository = bicRepository;
        this.legalEntityRepository = legalEntityRepository;
        this.configProperties = configProperties;
        //Generate data that will be used for other messages/parts
        this.messageId = Helper.getRandomAlphaNumeric(16);
        this.uuid = Helper.getUUIDv4();
        this.messageTimestamp = Helper.getXMLGregorianCalendarNow();

        this.generateMessageDocument();
        this.generateMessageHeader();
        return message;
    }

    public void generateMessageDocument() throws DatatypeConfigurationException {
        FIToFICustomerCreditTransferV08 customerTransferParam = new FIToFICustomerCreditTransferV08();
        customerTransferParam.addCdtTrfTxInf(generateTransactionInfo());
        customerTransferParam.setGrpHdr(generateGroupHeader());
        this.message.setFIToFICstmrCdtTrf(customerTransferParam);
    }

    public void generateMessageHeader() {
        BusinessAppHdrV02 headerParam = new BusinessAppHdrV02();
        headerParam.setBizSvc("swift.cbprplus.01");
        headerParam.setMsgDefIdr("pacs.008.001.08");
        headerParam.setBizMsgIdr(this.messageId);
        headerParam.setCreDt(this.messageTimestamp);
        headerParam.setFr(new Party44ChoiceRandom(instAgentFrom.getFinInstnId().getBICFI()));
        headerParam.setTo(new Party44ChoiceRandom(instAgentTo.getFinInstnId().getBICFI()));
        this.message.setAppHdr(headerParam);
    }

    public CreditTransferTransaction39 generateTransactionInfo() throws DatatypeConfigurationException {
        CreditTransferTransaction39 transactionInfo =  new CreditTransferTransaction39();
        instAgentFrom = new BranchAndFinancialInstitutionIdentification6Random(bicRepository, configProperties.getSourceBicList());
        instAgentTo = new BranchAndFinancialInstitutionIdentification6Random(bicRepository, configProperties.getDestinationBicList());
        transactionInfo.setInstgAgt(instAgentFrom);
        transactionInfo.setInstdAgt(instAgentTo);

        transactionInfo.setDbtr(new PartyIdentification135Random(legalEntityRepository));
        transactionInfo.setCdtr(new PartyIdentification135Random(legalEntityRepository));

        // Statically rendered optional rich data elements - using largely static value
        // addresses issue https://jira.aws-prototyping.cloud/browse/PE20836-32
        transactionInfo.setCdtrAcct(
                new CashAccount38().setId(
                        new AccountIdentification4Choice().setOthr(
                                new GenericAccountIdentification1()
                                        .setId("6723847BB")))
                        .setCcy("USD")
                        .setNm("ABC Import Receivables Europe")
                        .setPrxy(new ProxyAccountIdentification1()
                                                .setTp(new ProxyAccountType1Choice().setCd("EMAL"))
                                                .setId("webmaster-services-peter-crazy-but-oh-so-ubber-cool-english-alphabet-loverer-abcdefghijklmnopqrstuvwxyz@please-try-to.send-me-an-email-if-you-can-possibly-begin-to-remember-this-coz.this-is-the-longest-email-address-known-to-man-but-to-be-honest.this-is-such-a-stupidly-long-sub-domain-it-could-go-on-forever.pacraig.com")))
                .addInstrForCdtrAgt(new InstructionForCreditorAgent1().setCd(Instruction3Code.PHOB)
                        .setInstrInf("Please call the creditor as soon as funds are credited to the account.The phone number is 4234421443 or 324979347. Leave a message."))
                .addInstrForNxtAgt(new InstructionForNextAgent1().setInstrInf("Good luck with this payment order.I love this free text field and really want to fill it with useless information"))
                .setPurp(new Purpose2Choice().setCd("COMC"))
                .addRgltryRptg(new RegulatoryReporting3().setDbtCdtRptgInd(RegulatoryReportingType1Code.CRED)
                        .setAuthrty(new RegulatoryAuthority2().setNm("Reserve Bank of India")
                                .setCtry("IN")).addDtls(new StructuredRegulatoryReporting3()
                                .setTp("Export Reporting")
                                .setDt(DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.of(2019, 1, 13).toString()))
                                .setCtry("IN")
                                .setCd("P0102")
                                .setAmt(new ActiveOrHistoricCurrencyAndAmount()
                                        .setCcy("USD")
                                        .setValue(new BigDecimal("6891234567.50"))
                                )
                        )
                );

        transactionInfo.setDbtrAgt(new BranchAndFinancialInstitutionIdentification6Random(bicRepository));
        transactionInfo.setCdtrAgt(new BranchAndFinancialInstitutionIdentification6Random(bicRepository));
        transactionInfo.setPmtId(generatePaymentIdentification());

        transactionInfo.setChrgBr(generateChargeBearer());
        if (transactionInfo.getChrgBr().equals(ChargeBearerType1Code.CRED))
            transactionInfo.addChrgsInf(generateChargeInfCRED(instAgentFrom.getFinInstnId().getBICFI()));

        transactionInfo.setPmtTpInf(new PaymentTypeInformation28Random(configProperties));

        transactionInfo.setPrvsInstgAgt1(new BranchAndFinancialInstitutionIdentification6Random(bicRepository));
        transactionInfo.setIntrmyAgt1(new BranchAndFinancialInstitutionIdentification6Random(bicRepository));

        ActiveCurrencyAndAmountRandom randomSettleAmount = new ActiveCurrencyAndAmountRandom(configProperties);
        transactionInfo.setIntrBkSttlmAmt(randomSettleAmount);
        transactionInfo.setIntrBkSttlmDt(this.messageTimestamp);
        transactionInfo.setInstdAmt(new ActiveOrHistoricCurrencyAndAmountRandom(randomSettleAmount.getValue(),randomSettleAmount.getCcy()));

        return transactionInfo;
    }

    public PaymentIdentification7 generatePaymentIdentification() {
        PaymentIdentification7 paymentID = new PaymentIdentification7();
        paymentID.setUETR(this.uuid);
        paymentID.setInstrId(Helper.getRandomAlphaNumeric(16));
        paymentID.setEndToEndId(Helper.getRandomAlphaNumeric(16));
        return paymentID;
    }

    public GroupHeader93 generateGroupHeader() {
        GroupHeader93 groupHeader = new GroupHeader93();
        groupHeader.setMsgId(this.messageId);
        groupHeader.setNbOfTxs("1");
        groupHeader.setCreDtTm(this.messageTimestamp);

        SettlementInstruction7 settlementMethod = new SettlementInstruction7();
        settlementMethod.setSttlmMtd(SettlementMethod1Code.INDA);
        groupHeader.setSttlmInf(settlementMethod);
        return groupHeader;
    }

    public ChargeBearerType1Code generateChargeBearer() {
        SecureRandom random = new SecureRandom();
        int index = random.nextInt(3);
        return ChargeBearerType1Code.values()[index];
    }

    public Charges7 generateChargeInfCRED(String BICFI) {
        Charges7 charge = new Charges7();
        SecureRandom random = new SecureRandom();
        float value = random.nextInt(1500) / 100;
        ActiveOrHistoricCurrencyAndAmountRandom amount = new ActiveOrHistoricCurrencyAndAmountRandom(configProperties);
        charge.setAmt(amount);
        BranchAndFinancialInstitutionIdentification6 finInstnId = new BranchAndFinancialInstitutionIdentification6();
        finInstnId.setFinInstnId(new FinancialInstitutionIdentification18().setBICFI(BICFI));
        charge.setAgt(finInstnId);
        return charge;
    }
}
