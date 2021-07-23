/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */
package rapide.iso20022.message.generators.fieldrandomizer;

import lombok.extern.slf4j.Slf4j;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.GregorianCalendar;
import java.util.UUID;
import java.security.SecureRandom;

@Slf4j
public class Helper {

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
}
