package ro.fortsoft.smsutil;

import javafx.util.Pair;
import org.junit.Test;
import ro.fortsoft.smsutil.charset.GSM0338Charset;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class SmsUtilsTest {

    @Test
    public void charactersFromGsm7BitCharsetTakeUpTwoSpaces() {
        String message = "\f^{}\\[~]|€";

        assertTrue("GSM7Bit extended-charset characters are escaped and take twice as much space",
                SmsUtils.escapeAny7BitExtendedCharsetInContent(message).length() == message.length() * 2);
    }

    @Test
    public void encodingIsSwitchedForMessagesWithCharactersNotInGsm7Bit() {
        String message = "Hello țar";
        assertTrue("Encoding is changed to Unicode", SmsUtils.splitSms(message).getKey() == Encoding.GSM_UNICODE);
    }

    @Test
    public void messageFitsInsideSinglePartSmsWithGsm7BitEncoding() {
        String message =
                "11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111" +
                        "11111111111111111111111111111111111111111111111111111111111111111";

        assert message.length() == 160; //
        Pair<Encoding, List<String>> parts = SmsUtils.splitSms(message);

        assertTrue("Encoding is Gsm7Bit", parts.getKey() == Encoding.GSM_7BIT);

        assertTrue("Fits inside a single part sms of 160", parts.getValue().size() == 1 &&
                parts.getValue().get(0).length() == 160);
    }

    @Test
    public void messageOverSinglePartSmsWithGsm7BitEncodingIsSplitIntoTwoPartsEachOfMaxMultipartSizeOf153() {
        String message =
                "11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111" +
                        "111111111111111111111111111111111111111111111111111111111111111112";

        assert message.length() == 161; //
        Pair<Encoding, List<String>> parts = SmsUtils.splitSms(message);

        assertTrue("Encoding is Gsm7Bit", parts.getKey() == Encoding.GSM_7BIT);

        assertTrue("Simple message fits inside a two part sms", parts.getValue().size() == 2);
        assertTrue("First part size = 153", parts.getValue().get(0).length() == 153);
        assertTrue("Second part size = (161 - 153) = 8", parts.getValue().get(1).length() == 8);
    }

    @Test
    public void messageIsSplitInto3PartsBecauseOfEndingWithTheGsm7BitEscapeCharacter() {
        String message = "11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000091€";
        assert message.length() == 306; //Theoretically would fit inside 2part * 153

        Pair<Encoding, List<String>> parts = SmsUtils.splitSms(message);
        assertTrue("Encoding is Gsm7Bit", parts.getKey() == Encoding.GSM_7BIT);

        assertTrue("It takes 3 parts", parts.getValue().size() == 3);

        assertTrue("Second part is smaller by one(since Euro character escape cannot sit alone)",
                parts.getValue().get(1).length() == 152);

        Pair<Encoding, Integer> numParts = SmsUtils.getNumberOfParts(message);
        assertTrue("Encoding is Gsm7Bit - numParts", numParts.getKey() == Encoding.GSM_7BIT);
        assertTrue("It takes 3 parts - numParts", numParts.getValue() == 3);
    }

    @Test
    public void messageThatNeedsUnicodeCanHaveMaxLenghtOf70() {
        String message = "Д111111111111111111111111111111111111111111111111111111111111111111111";
        assert message.length() == 70;

        Pair<Encoding, List<String>> parts = SmsUtils.splitSms(message);
        assertTrue("Encoding is Unicode", parts.getKey() == Encoding.GSM_UNICODE);
        assertTrue("Fits inside a single part sms but with Unicode max length of 70",
                parts.getValue().size() == 1 && parts.getValue().get(0).length() == 70);

        Pair<Encoding, Integer> numParts = SmsUtils.getNumberOfParts(message);
        assertTrue("Encoding is Unicode - numParts", numParts.getKey() == Encoding.GSM_UNICODE);
        assertTrue("Fits inside a single part sms", numParts.getValue() == 1);
    }

    @Test
    public void messageThatNeedsUnicodeCanHaveMaxLengthOf67IfSplitMultipart() {
        String message = "Д1111111111111111111111111111111111111111111111111111111111111111111112";
        assert message.length() == 71;

        Pair<Encoding, List<String>> parts = SmsUtils.splitSms(message);
        assertTrue("Encoding is Unicode", parts.getKey() == Encoding.GSM_UNICODE);
        assertTrue("Fits inside a 2 part sms", parts.getValue().size() == 2);
        assertTrue("First part size = 67", parts.getValue().get(0).length() == 67);
        assertTrue("Second part size = (71 - 67)", parts.getValue().get(1).length() == 4);

        Pair<Encoding, Integer> numParts = SmsUtils.getNumberOfParts(message);
        assertTrue("Encoding is Unicode - numParts", numParts.getKey() == Encoding.GSM_UNICODE);
        assertTrue("It takes 2 parts - numParts", numParts.getValue() == 2);
    }

    @Test
    public void messageThatNeedsUnicodeDontRequireEscapeCharacterForCharactersInExtendedGsm7BitCharset() {
        String message = "€" + "Д"; //€ is in the extended GSM0338Charset however since the needed change to Unicode
        //required by the Д character, it's no longer required to escape the €

        Pair<Encoding, List<String>> parts = SmsUtils.splitSms(message);
        assertTrue("Encoding is Unicode", parts.getKey() == Encoding.GSM_UNICODE);
        assertTrue("Message takes up 2 spaces", parts.getValue().get(0).length() == 2);
    }

    @Test
    public void smsPartDetectionBypassOptimization() {
        String message = "11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111€000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000009€222222222222222222222222222222222333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333€44444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444€567";

        Pair<Encoding, Integer> numParts = SmsUtils.getNumberOfParts(message);
        assertTrue("Message takes up 5 spaces", numParts.getValue() == 5);

        message = "11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111€000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000009€222222222222222222222222222222222333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333€44444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444€";
        numParts = SmsUtils.getNumberOfParts(message);
        assertTrue("Message takes up 5 spaces", numParts.getValue() == 4);
    }
}
