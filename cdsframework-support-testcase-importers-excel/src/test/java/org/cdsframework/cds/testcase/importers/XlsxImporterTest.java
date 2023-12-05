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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.cdsframework.cds.vmr.CdsObjectAssist;
import org.cdsframework.exceptions.CdsException;
import org.cdsframework.ice.testcase.TestCaseWrapper;
import org.cdsframework.util.LogUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author HLN Consulting, LLC
 */
public class XlsxImporterTest {

    private final static LogUtils logger = LogUtils.getLogger(XlsxImporterTest.class);

    public XlsxImporterTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
//
//    /**
//     * Test of importFromFile method, of class XlsxV1Helper.
//     * @throws Exception
//     */
//    @Ignore
//    @Test
//    public void testImportFromFileV1() throws Exception {
//        logger.info("importFromFile");
//        String filename = "ICE3 Test Cases - HepB - V1.xlsx";
//        XlsxImporter.importFromFile(filename, new TestImportCallback() {
//
//            @Override
//            public void callback(TestcaseWrapper testcase, String Group, boolean success) throws CdsException {
//                try {
//                    CdsObjectAssist.cdsObjectToFile(testcase.getTestcase(), "imported-tests", testcase.getEncodedName());
//                } catch (FileNotFoundException e) {
//                    throw new CdsException(e.getMessage());
//                } catch (IOException e) {
//                    throw new CdsException(e.getMessage());
//                }
//            }
//        });
//        assertTrue(true);
//    }
//
//    /**
//     * Test of importFromFile method, of class XlsxV1Helper.
//     * @throws Exception
//     */
//    @Ignore
//    @Test
//    public void testImportFromFileV2b() throws Exception {
//        logger.info("importFromFile");
//        String filename = "ICE3 Test Cases - HepB - V2.xlsx";
//        XlsxImporter.importFromFile(filename, new TestImportCallback() {
//
//            @Override
//            public void callback(TestcaseWrapper testcase, String Group, boolean success) throws CdsException {
//                try {
//                    CdsObjectAssist.cdsObjectToFile(testcase.getTestcase(), "imported-tests", testcase.getEncodedName());
//                } catch (FileNotFoundException e) {
//                    throw new CdsException(e.getMessage());
//                } catch (IOException e) {
//                    throw new CdsException(e.getMessage());
//                }
//            }
//        });
//        assertTrue(true);
//    }

    /**
     * Test of importFromFile method, of class XlsxV2Helper.
     * @throws Exception
     */
    @Ignore
    @Test
    public void testImportFromFileV2a() throws Exception {
        logger.info("importFromFile");
        String filename = "/Volumes/HDD2/Users/fuzzy/IdeaProjects/cdsframework-client-support/cdsframework-support-testcase-importers-excel/CDSi-Test-Cases-V1-0.xlsx";
        XlsxImporter xlsxImporter = new XlsxImporter();
        xlsxImporter.importFromFile(filename, new TestImportCallback() {

            @Override
            public void callback(TestCaseWrapper testcase, String Group, boolean success) throws CdsException {
                try {

                    Files.createDirectories(Paths.get("/Volumes/HDD2/Users/fuzzy/IdeaProjects/cdsframework-client-support/cdsframework-support-testcase-importers-excel/imported-tests-cdc/" + testcase.getVaccineCodeFromGroup()));
                    CdsObjectAssist.cdsObjectToFile(testcase.getTestCase(), "/Volumes/HDD2/Users/fuzzy/IdeaProjects/cdsframework-client-support/cdsframework-support-testcase-importers-excel/imported-tests-cdc/" + testcase.getVaccineCodeFromGroup(), testcase.getEncodedName());
                } catch (FileNotFoundException e) {
                    throw new CdsException(e.getMessage());
                } catch (IOException e) {
                    throw new CdsException(e.getMessage());
                }
            }
        });
        assertTrue(true);
    }
}
