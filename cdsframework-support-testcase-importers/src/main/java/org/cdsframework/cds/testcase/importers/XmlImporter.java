/**
 * The cdsframework support testcase imports project implements some base test case importer functionality.
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

import java.io.InputStream;
import org.cdsframework.cds.util.MarshalUtils;
import org.cdsframework.exceptions.CdsException;
import org.cdsframework.ice.testcase.TestCaseWrapper;
import org.cdsframework.util.support.data.cds.testcase.TestCase;

/**
 *
 * @author HLN Consulting, LLC
 */
public class XmlImporter extends TestCaseImporter {

    public XmlImporter() {
        super(XmlImporter.class);
    }

    @Override
    public void importFromInputStream(InputStream inputStream, TestImportCallback callback) throws CdsException {
        final String METHODNAME = "importFromInputStream ";
        InputStream xslInputStream = null;
        try {
            xslInputStream = XmlImporter.class.getClassLoader().getResourceAsStream("testcase-ns-transform.xsl");
            TestCase testCase = MarshalUtils.unmarshal(inputStream, xslInputStream, TestCase.class);
            callback.callback(new TestCaseWrapper(testCase), testCase.getGroupName(), true);
        } catch (CdsException e) {
            TestCase testCase = MarshalUtils.unmarshal(inputStream, TestCase.class);
            callback.callback(new TestCaseWrapper(testCase), testCase.getGroupName(), true);
        } finally {
            try {
                if (xslInputStream != null) {
                    xslInputStream.close();
                }
            } catch (Exception e) {
                // nada
            }
        }
    }
}
