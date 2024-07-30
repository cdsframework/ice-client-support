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
package org.cdsframework.cds.vmr;

import org.cdsframework.util.LogUtils;
import org.cdsframework.util.support.cds.Config;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opencds.vmr.v1_0.schema.CDSOutput;

/**
 *
 * @author HLN Consulting, LLC
 */
public class CdsOutputWrapperTest {

    private static LogUtils logger = LogUtils.getLogger(CdsOutputWrapperTest.class);

    public CdsOutputWrapperTest() {
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

    /**
     * Test of getCdsOutputWrapper method, of class CdsOutputWrapper.
     * @throws Exception
     */
    @Test
    public void testGetCdsOutputWrapper() throws Exception {
        long start;
        logger.info("Starting testGetCdsOutputWrapper...");

        // preform first to time get the static initialization of CdsObjectAssist out of the way
        CdsOutputWrapper.getCdsOutputWrapper();

        start = System.nanoTime();
        CdsOutputWrapper output = CdsOutputWrapper.getCdsOutputWrapper();
        logger.logDuration("getCdsOutputWrapper", start);

        start = System.nanoTime();
        output.setPatientGender("F", Config.getCodeSystemOid("GENDER"));
        output.setPatientBirthTime("19830630");
 
        logger.logDuration("output init", start);

        start = System.nanoTime();
        logger.info(CdsObjectAssist.cdsObjectToString(output.getCdsObject(), CDSOutput.class));
        logger.logDuration("cdsObjectToString", start);

        logger.info("Finished testGetCdsOutputWrapper...");
        assertTrue(true);
    }

}
