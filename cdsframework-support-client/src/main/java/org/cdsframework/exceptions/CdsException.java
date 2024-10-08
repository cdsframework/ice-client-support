/**
 * The cdsframework support client aims at making vMR generation easier.
 *
 * Copyright 2024 HLN Consulting, LLC
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
 *
 * For more information about the this software, see https://www.hln.com/services/open-source/ or send
 * correspondence to scm@cdsframework.org.
 */
package org.cdsframework.exceptions;

/**
 *
 * @author HLN Consulting, LLC
 */
public class CdsException extends RuntimeException {

    private static final long serialVersionUID = -7814774067631545156L;

    /**
     * Creates a new instance of <code>CdsException</code> without detail message.
     */
    public CdsException() {
    }

    /**
     * Constructs an instance of <code>CdsException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public CdsException(String msg) {
        super(msg);
    }
}
