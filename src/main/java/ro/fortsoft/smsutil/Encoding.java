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

import ro.fortsoft.smsutil.charset.GSM0338Charset;

/**
 * The encoding to be used for the SMS
 */
public enum  Encoding {

    /**
     * Encoding that is used for messages that have all the characters in the GSM0338Charset
     * @see GSM0338Charset
     */
    GSM_7BIT(160, 153),

    /**
     * Encoding that is used for messages that have characters outside of the Gsm7BitCharset
     * @see GSM0338Charset
     */
    GSM_UNICODE(70, 67);


    private int maxLengthSinglePart;

    /**
     * For SMS messages that are split into multiple parts, some bytes need to be used as a header to
     * establish a sequence for reassembly the parts when they arrive at the destination
     * @see <a href="http://en.wikipedia.org/wiki/User_Data_Header">UDH</a>
     */
    private int maxLengthMultiPart;

    Encoding(int maxLengthSinglePart, int maxLengthMultiPart) {
        this.maxLengthSinglePart = maxLengthSinglePart;
        this.maxLengthMultiPart = maxLengthMultiPart;
    }

    public int getMaxLengthSinglePart() {
        return maxLengthSinglePart;
    }

    public int getMaxLengthMultiPart() {
        return maxLengthMultiPart;
    }
}
