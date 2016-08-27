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

package ro.fortsoft.smsutil.domain;

import ro.fortsoft.smsutil.Encoding;

public class SmsParts {

    private final Encoding encoding;

    private final String[] parts;

    public SmsParts(Encoding encoding, String[] parts) {
        this.encoding = encoding;
        this.parts = parts;
    }

    public Encoding getEncoding() {
        return encoding;
    }

    public String[] getParts() {
        return parts;
    }
}
