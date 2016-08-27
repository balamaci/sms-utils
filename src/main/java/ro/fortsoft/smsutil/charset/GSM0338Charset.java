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

package ro.fortsoft.smsutil.charset;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * The Gsm7BitCharset contains the list of characters specified in GSM 03.38 standard
 * @see <a href="https://en.wikipedia.org/wiki/GSM_03.38">Wikipedia GSM 03.38</a>
 * @see <a href="http://unicode.org/Public/MAPPINGS/ETSI/GSM0338.TXT">Unicode mapping to the ETSI GSM 03.38 charset</a>
 *
 * Apart from the base list of characters in this charset there are extra characters which
 * make up the 'Extended Charset'. These extra characters need to be escaped with a special escape character,
 * which means that the characters from the extended charset are counted as 2 characters,
 * but still can be encoded in 7bit.
 *
 */
public class GSM0338Charset {

    public static final char ESCAPE_CHAR = '\u001b';


    public static final Set<String> BASE_CHARSET = new HashSet<String>(Arrays.asList(
            new String[] {
                    "@", "£", "$", "¥", "è", "é", "ù", "ì", "ò", "ç", "\n", "Ø", "ø", "\r", "Å", "å",
                    "Δ", "_", "Φ", "Γ", "Λ", "Ω", "Π", "Ψ", "Σ", "Θ", "Ξ", "\u001b", "Æ", "æ", "ß", "É",
                    " ", "!", "'", "#", "¤", "%", "&", "\"", "(", ")", "*", "+", ",", "-", ".", "/",
                    "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ":", ";", "<", "=", ">", "?",
                    "¡", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
                    "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "Ä", "Ö", "Ñ", "Ü", "§",
                    "¿", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o",
                    "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "ä", "ö", "ñ", "ü", "à"
            }
    ));

    /**
     * The GSM 03.38 specifies an extended charset that still qualify for 7Bit encoding but for which an escape
     * character must be added before the character thus a character for this charset takes up 2 positions in the sms
     */
    public static final Set<String> EXTENDED_CHARSET = new HashSet<String>(Arrays.asList(
            new String[] {
                    "\f", "^", "{", "}", "\\", "[", "~", "]", "|", "€"
            }
    ));

    public static boolean containsOnlyBaseCharsetCharacters(String content) {
        return containsOnlyCharsetCharacters(content, false);
    }

    /**
     * Checks that the character belongs to the base charset
     * @param ch character
     * @return character belongs to base charset
     */
    public static boolean isBaseCharsetCharacter(char ch) {
        return BASE_CHARSET.contains(Character.toString(ch));
    }

    /**
     * Checks that the character belongs to the extended charset
     * @param ch character
     * @return character belongs to extended charset
     */
    public static boolean isExtendedCharsetCharacter(char ch) {
        return EXTENDED_CHARSET.contains(Character.toString(ch));
    }

    /**
     * Checks that the message contains only characters that belong
     *
     * @param message message
     * @param includeExtendedCharset if we should also use the characters in the extended charset in the check or not
     * @return true if the message doesn't contain characters outside the charset
     */
    public static boolean containsOnlyCharsetCharacters(String message, boolean includeExtendedCharset) {
        for (char ch : message.toCharArray()) {
            if (! (BASE_CHARSET.contains(Character.toString(ch)) ||
                    (includeExtendedCharset && isExtendedCharsetCharacter(ch))
                  )) {
                return false;
            }
        }

        return true;
    }

}
