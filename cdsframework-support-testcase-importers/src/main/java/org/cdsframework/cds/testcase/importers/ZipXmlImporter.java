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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.cdsframework.exceptions.CdsException;

/**
 *
 * @author HLN Consulting, LLC
 */
public class ZipXmlImporter extends TestCaseImporter {

    public ZipXmlImporter() {
        super(ZipXmlImporter.class);
    }

    @Override
    public void importFromInputStream(InputStream inputStream, TestImportCallback callback) throws CdsException {
        final String METHODNAME = "importFromInputStream ";
        logger.logBegin(METHODNAME);
        ZipEntry zipEntry = null;
        File tmpFile = null;
        FileOutputStream tmpFileOS = null;
        ZipInputStream zipInputStream = null;
        try {
            if (inputStream != null) {
                zipInputStream = new ZipInputStream(inputStream);
                if (zipInputStream != null) {
                    XmlImporter xmlImporter = new XmlImporter();
                    while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                        logger.info("Processing: ", zipEntry.getName());
                        if (zipEntry.getName() != null && zipEntry.getName().endsWith("xml")) {
                            tmpFile = File.createTempFile("g54bbrtbe", ".zip");
                            if (tmpFile != null) {
                                tmpFile.deleteOnExit();
                                tmpFileOS = new FileOutputStream(tmpFile);
                                if (tmpFileOS != null) {
                                    int nRead;
                                    byte[] buffer = new byte[16384];
                                    while ((nRead = zipInputStream.read(buffer, 0, buffer.length)) != -1) {
                                        tmpFileOS.write(buffer, 0, nRead);
                                    }
                                    zipInputStream.closeEntry();
                                    tmpFileOS.close();
                                    InputStream entryInputStream = null;
                                    try {
                                        entryInputStream = new FileInputStream(tmpFile);
                                        xmlImporter.importFromInputStream(entryInputStream, callback);
                                    } finally {
                                        try {
                                            entryInputStream.close();
                                        } catch (Exception e) {
                                            // do nothing...
                                        }
                                    }
                                } else {
                                    logger.error("tmpFileOS is null!");
                                }
                            } else {
                                logger.error("tmpFile is null!");
                            }
                        } else {
                            logger.warn("Skipping: ", zipEntry.getName());
                            zipInputStream.closeEntry();
                        }
                    }
                } else {
                    logger.error("zipInputStream is null!");
                }
            } else {
                logger.error("inputStream is null!");
            }
        } catch (IOException e) {
            logger.error(e);
            throw new CdsException(e.getMessage());
        } finally {
            try {
                zipInputStream.close();
            } catch (Exception e) {
                // do nothing.
            }
            try {
                tmpFileOS.close();
            } catch (Exception e) {
                // do nothing.
            }
        }
    }
}
