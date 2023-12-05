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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.cdsframework.exceptions.CdsException;
import org.cdsframework.util.LogUtils;

/**
 *
 * @author HLN Consulting, LLC
 */
public abstract class TestCaseImporter implements TestCaseImporterInterface {

    protected final LogUtils logger;

    public TestCaseImporter() {
        logger = LogUtils.getLogger(TestCaseImporter.class);
    }

    public TestCaseImporter(Class loggerClass) {
        logger = LogUtils.getLogger(loggerClass);
    }

    @Override
    public void importFromFile(String filename, TestImportCallback callback) throws CdsException {
        final String METHODNAME = "importFromFile ";
        logger.logBegin(METHODNAME);
        try {
            importFromInputStream(new FileInputStream(filename), callback);
        } catch (FileNotFoundException e) {
            logger.error(e);
            throw new CdsException(e.getMessage());
        } finally {
            logger.logEnd(METHODNAME);
        }
    }

}
