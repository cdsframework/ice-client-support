/**
 * The cdsframework support testcase imports excel project implements some specific excel based test case importer functionality.
 *
 * Copyright 2016 HLN Consulting, LLC
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
package org.cdsframework.cds.testcase.importers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.cdsframework.util.LogUtils;

/**
 *
 * @author HLN Consulting, LLC
 */
public class XlsxImporterUtils {

    private final static LogUtils logger = LogUtils.getLogger(XlsxImporterUtils.class);

    /**
     * Return a hash from a plain text string.
     *
     * @param string
     * @return
     */
    public static String getShaHashFromString(String string) {
        String hash = "";
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(string.getBytes());
            byte[] byteData = md.digest();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            hash = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error(e);
        }
        return hash;
    }
}
