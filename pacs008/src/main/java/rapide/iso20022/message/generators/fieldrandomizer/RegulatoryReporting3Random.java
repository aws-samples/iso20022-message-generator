/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */
package rapide.iso20022.message.generators.fieldrandomizer;

import com.prowidesoftware.swift.model.mx.dic.*;
import rapide.iso20022.util.StringUtils;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RegulatoryReporting3Random {
    private static Map<String, String> regulatoryAuthorities = Map.of(
            "US", "Federal Reserve",
            "CA", "Bank of Canada",
            "IN", "Reserve Bank of India",
            "GB", "Bank of England",
            "TH", "Bank of Thailand",
            "MX", "Bank of Mexico",
            "IE", "Central Bank of Ireland"
    );

    private static String[] regulatoryCodes = {"00.X0001", "00.X0002", "01.Y1300", "03.Z1000", "04.A001", "05.B1001"};
    private static String[] regulatoryTps = {"Import Reporting", "Export Reporting", "FDI Reporting", "Misc Reporting"};

    private static LocalDate startDate = LocalDate.of(2019, 1, 01);
    private static LocalDate endDate = LocalDate.now();

    public static RegulatoryReporting3 getRegulatoryReporting3(final boolean isToCountry, final String country,
                                                               final ActiveOrHistoricCurrencyAndAmount currencyAndAmount) {
        if (StringUtils.empty(country)) {
            throw new RuntimeException("Argument country must be specified");
        }

        if (currencyAndAmount == null) {
            throw new RuntimeException("Argument currencyAndAmount must be specified");
        }

        RegulatoryReporting3 result;
        switch (country) {
            case "IN":
                result = getRegulatoryReporting3ForIndia(isToCountry, currencyAndAmount);
                break;
            default:
                result = getInternalRegulatoryReporting3(isToCountry, country, currencyAndAmount);
                break;
        }

        return result;
    }

    public static RegulatoryReporting3 getRegulatoryReporting3ForIndia(boolean isToCountry,
                                            ActiveOrHistoricCurrencyAndAmount currencyAndAmount) {

        String[] indiaFXCode = {"00.P0006", "00.P0008", "13.P1301", "13.P1302"};
        int idx = (int)(Math.random() * indiaFXCode.length);

        RegulatoryReporting3 regulatoryReporting3 = new RegulatoryReporting3();
        try {
            if (isToCountry) {
                regulatoryReporting3.setDbtCdtRptgInd(RegulatoryReportingType1Code.CRED)
                        .setAuthrty(new RegulatoryAuthority2().setNm(regulatoryAuthorities.get("IN"))
                                .setCtry("IN")).addDtls(new StructuredRegulatoryReporting3()
                                .setTp("Export Reporting")
                                .setDt(DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.now().toString()))
                                .setCtry("IN")
                                .setCd(indiaFXCode[idx])
                                .setAmt(currencyAndAmount)
                        );
            } else {
                // outbound transfer
                regulatoryReporting3.setDbtCdtRptgInd(RegulatoryReportingType1Code.DEBT)
                        .setAuthrty(new RegulatoryAuthority2().setNm(regulatoryAuthorities.get("IN"))
                                .setCtry("IN")).addDtls(new StructuredRegulatoryReporting3()
                                .setTp("Import Reporting")
                                .setDt(DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.now().toString()))
                                .setCtry("IN")
                                .setCd(indiaFXCode[idx])
                                .setAmt(currencyAndAmount)
                        );
            }
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }

        return regulatoryReporting3;
    }

    private static RegulatoryReporting3 getInternalRegulatoryReporting3(
            boolean isToCountry, String countryCode, ActiveOrHistoricCurrencyAndAmount currencyAndAmount) {

        int idx = (int)(Math.random() * regulatoryCodes.length);
        int idxTp = (int)(Math.random() * regulatoryTps.length);
        RegulatoryReporting3 regulatoryReporting3 = new RegulatoryReporting3();

        try {
            if (isToCountry) {
                regulatoryReporting3.setDbtCdtRptgInd(RegulatoryReportingType1Code.CRED)
                        .setAuthrty(new RegulatoryAuthority2().setNm(regulatoryAuthorities.get(countryCode))
                                .setCtry(countryCode)).addDtls(new StructuredRegulatoryReporting3()
                                .setTp(regulatoryTps[idxTp])
                                .setDt(DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.now().toString()))
                                .setCtry(countryCode)
                                .setCd(regulatoryCodes[idx])
                                .setAmt(currencyAndAmount)
                        );
            } else {
                // outbound transfer from the country
                regulatoryReporting3.setDbtCdtRptgInd(RegulatoryReportingType1Code.DEBT)
                        .setAuthrty(new RegulatoryAuthority2().setNm(regulatoryAuthorities.get(countryCode))
                                .setCtry(countryCode)).addDtls(new StructuredRegulatoryReporting3()
                                .setTp(regulatoryTps[idxTp])
                                .setDt(DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.now().toString()))
                                .setCtry(countryCode)
                                .setCd(regulatoryCodes[idx])
                                .setAmt(currencyAndAmount)
                        );
            }
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }

        return regulatoryReporting3;
    }
}
