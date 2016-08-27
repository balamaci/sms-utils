/*
 * Copyright [2016] the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ro.fortsoft.smsutil;

import javafx.util.Pair;
import ro.fortsoft.smsutil.charset.GSM0338Charset;

import java.util.List;

/**
 * Utility class for SMS messages
 */
public class SmsUtils {

    /**
     * Determines the necessary Gsm encoding to be used based on the characters in the message
     * @param message message content
     * @return Encoding that needs to be used
     */
    public static Encoding getGsmEncoding(String message) {
        if(! GSM0338Charset.containsOnlyCharsetCharacters(message, true)) {
            return Encoding.GSM_UNICODE;
        }

        return Encoding.GSM_7BIT;
    }

    /**
     * Determines the necessary encoding based upon the characters in the message and the number of parts the sms
     * needs to be split into
     *
     * @param content message
     * @return Pair&lt;Encoding, number of parts&gt;
     */
    public static Pair<Encoding, Integer> getNumberOfParts(String content) {
        Encoding encoding = getGsmEncoding(content);

        if (encoding == Encoding.GSM_7BIT) {
            return new Pair<>(Encoding.GSM_7BIT, getNumberOfPartsFor7BitEncoding(content));
        } else {
            if (content.length() <= Encoding.GSM_UNICODE.getMaxLengthSinglePart()) {
                return new Pair<>(Encoding.GSM_UNICODE, 1);
            } else {
                return new Pair<>(Encoding.GSM_UNICODE,
                        (int) Math.ceil(content.length() / (float) Encoding.GSM_UNICODE.getMaxLengthMultiPart()));
            }
        }
    }

    private static int getNumberOfPartsFor7BitEncoding(String content) {
        String content7bit = escapeAny7BitExtendedCharsetInContent(content);

        int messageLength = content7bit.length();

        if (content7bit.length() <= Encoding.GSM_7BIT.getMaxLengthSinglePart()) {
            return 1;
        }

        //number of parts if we don't consider that a message part cannot just end with GSM0338Charset.ESCAPE_CHAR
        int parts = (int) Math.ceil(messageLength / (float) Encoding.GSM_7BIT.getMaxLengthMultiPart());

        //we do some quick "optimisation" checking
        //if we have enough left characters in a message part, check if it would fill a worst case scenario
        //where each part was ending with the escape character
        int lastPartChars = messageLength % Encoding.GSM_7BIT.getMaxLengthMultiPart();
        int freeChars = 0;

        if(lastPartChars > 0) {
            freeChars = Encoding.GSM_7BIT.getMaxLengthMultiPart() - lastPartChars;
        }

        // There are characters left, don't care about escape character at the end of multiparts
        if (parts <= freeChars) { //optimization
            return parts;
        }

        // Otherwise "manually" split the message
        return SmsSplitter.splitGsm7BitEncodedMessage(content7bit).size();
    }

    /**
     * Escape any characters from the GSM0338Charset which belong to the extended charset
     *
     * @param message message
     * @return new String with escaped characters
     * @throws IllegalArgumentException when the message contains characters outside the GSM0338Charset
     */
    public static String escapeAny7BitExtendedCharsetInContent(String message) {
        StringBuilder content7bit = new StringBuilder();

        for (char ch : message.toCharArray()) {

            // Add escape characters for extended charset
            if(GSM0338Charset.isExtendedCharsetCharacter(ch)) {
                content7bit.append(GSM0338Charset.ESCAPE_CHAR);
            } else {
                if(! GSM0338Charset.isBaseCharsetCharacter(ch)) { //also not in the base charset
                    throw new IllegalArgumentException("Message contains '" + ch + "' which is not in GSM0338Charset");
                }
            }

            content7bit.append(Character.toString(ch));
        }

        return content7bit.toString();
    }

    /**
     * Split the SMS into as many parts as necessary according to the determined encoding
     *
     * @param message message
     * @return Pair&lt;Encoding, List &lt;String&gt;&gt; the encoding and the list of parts the sms has been split into
     */
    public static Pair<Encoding, List<String>> splitSms(String message) {
        return SmsSplitter.splitSms(message);
    }

}
