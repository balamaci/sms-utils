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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ro.fortsoft.smsutil.SmsUtils.escapeAny7BitExtendedCharsetInContent;
import static ro.fortsoft.smsutil.SmsUtils.getGsmEncoding;

/**
 * @author sbalamaci
 */
class SmsSplitter {


    static Pair<Encoding, List<String>> splitSms(String content) {
        Encoding encoding = getGsmEncoding(content);

        if (encoding == Encoding.GSM_7BIT) {
            String escapedContent = escapeAny7BitExtendedCharsetInContent(content);
            if (content.length() <= Encoding.GSM_7BIT.getMaxLengthSinglePart()) {
                return new Pair<>(Encoding.GSM_7BIT,
                        Collections.singletonList(escapedContent));
            } else {
                return new Pair<>(Encoding.GSM_7BIT, splitGsm7BitEncodedMessage(escapedContent));
            }
        } else {
            if (content.length() <= Encoding.GSM_UNICODE.getMaxLengthSinglePart()) {
                return new Pair<>(Encoding.GSM_UNICODE, Collections.singletonList(content));
            } else {
                return new Pair<>(Encoding.GSM_UNICODE, splitUnicodeEncodedMessage(content));
            }
        }
    }

    static List<String> splitGsm7BitEncodedMessage(String content) {
        List<String> parts = new ArrayList<>();
        StringBuilder contentString = new StringBuilder(content);

        int maxLengthMultipart = Encoding.GSM_7BIT.getMaxLengthMultiPart();

        while (contentString.length() > 0) {
            if (contentString.length() >= (maxLengthMultipart)) {
                int endPosition = maxLengthMultipart;
                if(isMultipartSmsLastCharGsm7BitEscapeChar(contentString.toString())) {
                    endPosition = endPosition - 1;
                }
                parts.add(contentString.substring(0, endPosition));
                contentString.delete(0, endPosition);
            } else {
                parts.add(contentString.toString());
                break;
            }
        }

        return parts;
    }

    private static List<String> splitUnicodeEncodedMessage(String content) {
        List<String> parts = new ArrayList<>();

        StringBuilder contentString = new StringBuilder(content);

        int maxLengthMultipart = Encoding.GSM_UNICODE.getMaxLengthMultiPart();

        while (contentString.length() > 0) {
            if (contentString.length() >= (maxLengthMultipart)) {

                parts.add(contentString.substring(0, maxLengthMultipart));
                contentString.delete(0, maxLengthMultipart);
            } else {
                parts.add(contentString.toString());
                break;
            }
        }

        return parts;
    }

    private static boolean isMultipartSmsLastCharGsm7BitEscapeChar(String content) {
        return content.charAt(Encoding.GSM_7BIT.getMaxLengthMultiPart() - 1) == GSM0338Charset.ESCAPE_CHAR;
    }

}
