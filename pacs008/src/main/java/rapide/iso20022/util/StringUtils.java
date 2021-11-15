/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */
package rapide.iso20022.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StringUtils {

    public static boolean empty(final String s) {
        return s == null || s.isBlank();
    }

    public static String removeNonPrintableChars(String source){
        //return source.replaceAll("\\p{C}", "");
        return cleanTextContent(source);
    }

    public static String StringStripper(String source, int length)
    {
        if (source==null) return "";

        if (source.length()<=length && source.length()>0)
        {
            return source;
        }
        else if (source.length()>length)
        {
            return source.substring(0,length);
        }
        else
        {
            log.error("Error in StringStripper, Length not fit input string");
            return null;
        }
    }

    private static String cleanTextContent(String text)
    {
        // strips off all non-ASCII characters
        text = text.replaceAll("[^\\x00-\\x7F]", "");

        // erases all the ASCII control characters
        text = text.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");

        // removes non-printable characters from Unicode
        text = text.replaceAll("\\p{C}", "");

        return text.trim();
    }
}
