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
import javax.xml.datatype.XMLGregorianCalendar;
import java.security.SecureRandom;

@Slf4j
public class Pacs008Message {

    private String[] creditorAccounts = {"6723847BB", "8934833092", "43487202984", "84349274229", "468924248622"};

    private MxPacs00800108 message;
    private BICRepository bicRepository;
    private LegalEntityRepository legalEntityRepository;
    private ConfigProperties configProperties;

    private String messageId, uuid;
    private XMLGregorianCalendar messageTimestamp;
    private BranchAndFinancialInstitutionIdentification6 instructingAgentFrom;
    private BranchAndFinancialInstitutionIdentification6 instructedAgentTo;

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
        headerParam.setFr(Party44ChoiceRandom.getParty44ChoiceRandom(instructingAgentFrom.getFinInstnId().getBICFI()));
        headerParam.setTo(Party44ChoiceRandom.getParty44ChoiceRandom(instructedAgentTo.getFinInstnId().getBICFI()));
        this.message.setAppHdr(headerParam);
    }

    public CreditTransferTransaction39 generateTransactionInfo() throws DatatypeConfigurationException {
        CreditTransferTransaction39 transactionInfo =  new CreditTransferTransaction39();
        instructingAgentFrom = BranchAndFinancialInstitutionIdentification6Random
            .getBranchAndFinancialInstitutionIdentification(bicRepository, configProperties.getSourceBicList());
        instructedAgentTo = BranchAndFinancialInstitutionIdentification6Random
            .getBranchAndFinancialInstitutionIdentification(bicRepository, configProperties.getDestinationBicList());
        String fromCountryCode = Helper.getCountryFromBIC(instructingAgentFrom.getFinInstnId().getBICFI());
        String toCountryCode = Helper.getCountryFromBIC(instructedAgentTo.getFinInstnId().getBICFI());

        // From and To countries must be different
        if (toCountryCode.equals(fromCountryCode)) {
            int maxRetries = configProperties.getInvalidMessageRetry();
            int count = 0;
            boolean found = false;
            while (count < maxRetries) {
                instructedAgentTo = BranchAndFinancialInstitutionIdentification6Random
                        .getBranchAndFinancialInstitutionIdentification(
                                bicRepository, configProperties.getDestinationBicList());
                toCountryCode = Helper.getCountryFromBIC(instructedAgentTo.getFinInstnId().getBICFI());
                if (!toCountryCode.equals(fromCountryCode)) {
                    found = true;
                    break;
                }
                else {
                    count++;
                }
            }

            if (!found) {
                StringBuffer errorMsg =
                    new StringBuffer("Unable to continue, cannot find different country BICs for To and From Finanancial Institutions. ");
                errorMsg.append(" Increase number of different country BICs to improve probability of finding different country BICs.");
                log.error(errorMsg.toString());
                throw new RuntimeException(errorMsg.toString());
            }
        }

        String fromCurrencyCode = Helper.getCountryCurrencyCode(configProperties, fromCountryCode);
        String toCurrencyCode = Helper.getCountryCurrencyCode(configProperties, toCountryCode);

        transactionInfo.setInstgAgt(instructingAgentFrom);
        transactionInfo.setInstdAgt(instructedAgentTo);

        PartyIdentification135 debtor = PartyIdentification135Random.getPartyIdentification135(legalEntityRepository, fromCountryCode);
        transactionInfo.setDbtr(debtor);
        PartyIdentification135 creditor = PartyIdentification135Random.getPartyIdentification135(legalEntityRepository, toCountryCode);
        transactionInfo.setCdtr(creditor);

        transactionInfo.setDbtrAgt(
                BranchAndFinancialInstitutionIdentification6Random
                        .getBranchAndFinancialInstitutionIdentification(bicRepository, fromCountryCode)
        );
        transactionInfo.setCdtrAgt(
                BranchAndFinancialInstitutionIdentification6Random
                        .getBranchAndFinancialInstitutionIdentification(bicRepository, toCountryCode)
        );
        transactionInfo.setPmtId(generatePaymentIdentification());

        transactionInfo.setChrgBr(generateChargeBearer());
        if (transactionInfo.getChrgBr().equals(ChargeBearerType1Code.CRED))
            transactionInfo.addChrgsInf(generateChargeInfCRED(instructingAgentFrom.getFinInstnId().getBICFI()));

        transactionInfo.setPmtTpInf(PaymentTypeInformation28Random.getPaymentTypeInformation28(configProperties));

        transactionInfo.setPrvsInstgAgt1(
                BranchAndFinancialInstitutionIdentification6Random
                        .getBranchAndFinancialInstitutionIdentification(bicRepository, fromCountryCode)
        );
        transactionInfo.setIntrmyAgt1(
                BranchAndFinancialInstitutionIdentification6Random
                        .getBranchAndFinancialInstitutionIdentification(bicRepository, toCountryCode)
        );

        ActiveCurrencyAndAmount randomSettleAmount =
                ActiveCurrencyAndAmountRandom.getActiveCurrencyAndAmount(configProperties, fromCountryCode);
        transactionInfo.setIntrBkSttlmAmt(randomSettleAmount);
        transactionInfo.setIntrBkSttlmDt(this.messageTimestamp);
        ActiveOrHistoricCurrencyAndAmount instdCurrencyAndAmount =
                ActiveOrHistoricCurrencyAndAmountRandom
                        .getActiveOrHistoricCurrencyAndAmount(randomSettleAmount.getValue(), randomSettleAmount.getCcy());
        transactionInfo.setInstdAmt(instdCurrencyAndAmount);

        // TODO make it dynamic
        // Statically rendered optional rich data elements - using largely static value
        int idx = (int)(Math.random() * creditorAccounts.length);
        transactionInfo.setCdtrAcct(
            new CashAccount38().setId(
                new AccountIdentification4Choice().setOthr(
                    new GenericAccountIdentification1()
                        .setId(creditorAccounts[idx])
                )
            )
            .setCcy(toCurrencyCode)
            .setNm(creditor.getNm())
            .setPrxy(new ProxyAccountIdentification1()
                .setTp(new ProxyAccountType1Choice().setCd("EMAL"))
                .setId("webmaster-services-peter-crazy-but-oh-so-ubber-cool-english-alphabet-loverer-abcdefghijklmnopqrstuvwxyz@please-try-to.send-me-an-email-if-you-can-possibly-begin-to-remember-this-coz.this-is-the-longest-email-address-known-to-man-but-to-be-honest.this-is-such-a-stupidly-long-sub-domain-it-could-go-on-forever.pacraig.com")
            )
        );
        transactionInfo.addInstrForCdtrAgt(new InstructionForCreditorAgent1().setCd(Instruction3Code.PHOB)
            .setInstrInf("Please call the creditor as soon as funds are credited to the account.The phone number is 4234421443 or 324979347. Leave a message.")
        );
        transactionInfo.addInstrForNxtAgt(
            new InstructionForNextAgent1()
                .setInstrInf("Good luck with this payment order.I love this free text field and really want to fill it with useless information")
        );

        // Fixed purpose code COMC as it is not used in payment processing, it is meant for end customers, debtor or creditor.
        transactionInfo.setPurp(new Purpose2Choice().setCd("COMC"));

        // Add regulatory reporting for country code IN
        if (fromCountryCode.equals("IN")) {
            transactionInfo.addRgltryRptg(RegulatoryReporting3Random.getRegulatoryReporting3(false,
                    fromCountryCode,
                    instdCurrencyAndAmount));
        }
        if (toCountryCode.equals("IN")) {
            transactionInfo.addRgltryRptg(RegulatoryReporting3Random.getRegulatoryReporting3(true,
                    toCountryCode,
                    instdCurrencyAndAmount));
        }
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

    public Charges7 generateChargeInfCRED(String bicFi) {
        Charges7 charge = new Charges7();
        SecureRandom random = new SecureRandom();
        float value = random.nextInt(1500) / 100;
        ActiveOrHistoricCurrencyAndAmount amount =
                ActiveOrHistoricCurrencyAndAmountRandom
                        .getActiveOrHistoricCurrencyAndAmount(configProperties, Helper.getCountryFromBIC(bicFi));
        charge.setAmt(amount);
        BranchAndFinancialInstitutionIdentification6 finInstnId = new BranchAndFinancialInstitutionIdentification6();
        finInstnId.setFinInstnId(new FinancialInstitutionIdentification18().setBICFI(bicFi));
        charge.setAgt(finInstnId);
        return charge;
    }
}
