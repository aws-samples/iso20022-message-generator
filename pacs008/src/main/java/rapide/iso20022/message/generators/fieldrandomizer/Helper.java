/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */
package rapide.iso20022.message.generators.fieldrandomizer;

import lombok.extern.slf4j.Slf4j;
import rapide.iso20022.cli.config.ConfigProperties;
import rapide.iso20022.util.StringUtils;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
public class Helper {
    private static final String DEFAULT_CURRENCY_CODE = "USD";

    public static String getUUIDv4() {
        String result="";
        try {
            UUID uuid = UUID.randomUUID();
            result=uuid.randomUUID().toString();
        } catch (Exception e) {
            log.error("Exception in getUUIDv4", e);
        }
        return result;
    }

    public static String getRandomAlphaNumeric(int size) {
        String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
        String CHAR_UPPER = CHAR_LOWER.toUpperCase();
        String NUMBER = "0123456789";
        // String SPECIAL = "-?:().,'+";
        String SPECIAL = "";
        String DATA_FOR_RANDOM_STRING = CHAR_LOWER + CHAR_UPPER + NUMBER + SPECIAL;
        SecureRandom random = new SecureRandom();

        if (size < 1) throw new IllegalArgumentException();

        StringBuilder sb = new StringBuilder(size);

        for (int i = 0; i < size; i++) {
            int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);
            sb.append(rndChar);
        }
        return sb.toString();
    }

    public static XMLGregorianCalendar getXMLGregorianCalendarNow() {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        DatatypeFactory datatypeFactory = null;
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        XMLGregorianCalendar now = datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
        return now;
    }

    public static BigDecimal getLongDigitsRandomNumber(long min, long max) {
        if (min>=max) {
            throw new IllegalArgumentException("Min cannot exceed Max.");
        }
        SecureRandom random = new SecureRandom();
        long result = random.longs(min, max + 1).findFirst().getAsLong();
        return BigDecimal.valueOf(result).setScale(2,RoundingMode.CEILING);
    }

    public static String getCountryFromBIC(final String bic) {
        if (bic != null && bic.length() >= 8) return bic.substring(4, 6);
        else return "";
    }

    // Returns default currency code if currency code for the country is not defined
    public static String getCountryCurrencyCode(ConfigProperties configProperties, final String countryCode) {
        List<HashMap<String, String>> currencies = configProperties.getCurrency();
        log.debug("Currencies: " + currencies);
        log.debug("Country: " + countryCode);
        String currencyCode = DEFAULT_CURRENCY_CODE;
        if (currencies != null) {
            List<HashMap<String, String>> ccMapList = currencies.stream().filter(map ->
                    map.get("Country").equals(countryCode))
                    .collect(Collectors.toList());
            if (!ccMapList.isEmpty()) {
                Map<String, String> m = ccMapList.get(0);
                currencyCode = m.get("Code");
                log.debug("Currency: " + currencyCode);
            }
            currencyCode = StringUtils.empty(currencyCode) ? DEFAULT_CURRENCY_CODE : currencyCode;
        }
        return currencyCode;
    }

    // Returns a random currency code
    public static String getRandomCurrencyCode(ConfigProperties configProperties) {
        List<HashMap<String, String>> currencies = configProperties.getCurrency();
        log.debug("List of Currencies: " + currencies);
        String currencyCode = DEFAULT_CURRENCY_CODE;
        if (currencies != null) {
            SecureRandom random = new SecureRandom();
            int index = random.nextInt(currencies.size());
            String ccCode = currencies.get(index).get("Code");
            currencyCode = ccCode == null ? DEFAULT_CURRENCY_CODE : ccCode;
        }
        return currencyCode;
    }

    public static LocalDate between(LocalDate startInclusive, LocalDate endExclusive) {
        long startEpochDay = startInclusive.toEpochDay();
        long endEpochDay = endExclusive.toEpochDay();
        long randomDay = ThreadLocalRandom
                .current()
                .nextLong(startEpochDay, endEpochDay);

        return LocalDate.ofEpochDay(randomDay);
    }
}
