SMS utils: Utility methods for SMS messages
=====================================

Small utility library for SMS. Can be used to determine the necessary encoding and in how many sms parts it must be split into multiple sms parts. 

Apache License.

Requires JDK 1.6 or higher.

Latest release
--------------

```xml
<dependency>
  <groupId>ro.fortsoft.sms-util</groupId>
  <artifactId>sms-util</artifactId>
  <version>1.0</version>
</dependency>
```

SMS Standard
--------------
Q: What is the max size of a text message that I can send through SMS?

A: Theoretically unlimited, as you can send multiple SMS-es and use part of their contents(a standard header) to specify they are all parts of the same text message. 
Practically the number of SMS **parts** is limited by the SMS provider. Could be 3, 35 or whatever. 


Q: What is the max text size of a single SMS?

A: It depends on the characters in the message. As long at the message contains only characters in the [GSM 03.38]() charset, the max size of a message is 160 characters. 
A bigger text message can be sent by splitting it into multiple sms-es. As said above, a chunk of the message(the [UDS](http://en.wikipedia.org/wiki/User_Data_Header) header) can be used to mark the sms parts as being a sequence of the same message. Therefore the max size of the text drops down to 153.


Should the text contain characters outside **GSM 03.38 charset**, the encoding is changed to **Unicode (UCS-2) UTF-16**. 
The characters in the **GSM 03.38 charset** can be encoded in a **7 bit** per char encoding. 
Since we're using more bytes per each char(16) the maximum number of that can fit in the message is lower. Now only 70 chars fit in a single part sms and 53 for a multipart one.  
This results from: An sms package has 140 bytes(140 * 8 bits) 
GSM 03.38 charset  => 7 bit per char => 140 * 8 / 7 = 160
Unicode            => 16 bits pe char => 140 * 8 / 16 = 70 

### SMS size
|                | Single Part | Multipart |
|----------------|-------------|-----------|
| Gsm338 charset | 160         | 153       |
|                |             |           |
| Unicode        |  70         |  67       |


### GSM 03.38 extended charset
Besides the base charset the are a few extra characters(€, |, {, ..) which still can be encoded as 7 bit, however another character(a by a special escape character _(0x1B)_) must be added before them. This means that the characters from this extended charset count as 2.  
In multipart sms-es at the end of an sms part the sms escape character _0x1B_ cannot stand by itself, so instead a new sms part must be added that will start with the escape char _0x1B_ + '{' for example.

### Sms calculator
You can see visually how a text is split by using this [SMS length calculator](http://messente.com/documentation/sms-length-calculator) 


### FAQ

Q: Can a text message be split into multi part SMS-es each part with different encoding? Like 1st part GSM338, 2nd Unicode, etc.

A: No, the whole SMS encoding must be the same. So if have a message with all characters from GSM338 and you add a character not in it (like a greek alphabet) causing the encoding to be switched to Unicode, the size for each part changes and how it's split must be recalculated.