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

import java.util.Date;
import org.cdsframework.util.LogUtils;
import org.cdsframework.util.support.cds.Config;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opencds.vmr.v1_0.schema.CDSInput;

/**
 *
 * @author HLN Consulting, LLC
 */
public class CdsInputWrapperTest {

    private static LogUtils logger = LogUtils.getLogger(CdsInputWrapperTest.class);

    public CdsInputWrapperTest() {
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
     * Test of getCdsInputWrapper method, of class CdsInputWrapper.
     * @throws Exception
     */
    @Test
    public void testGetCdsInputWrapper() throws Exception {
        long start;
        logger.info("Starting testGetCdsInputWrapper...");

        // preform first to time get the static initialization of CdsObjectAssist out of the way
        CdsInputWrapper.getCdsInputWrapper();

        start = System.nanoTime();
        CdsInputWrapper input = CdsInputWrapper.getCdsInputWrapper();
        logger.logDuration("getInput", start);

        start = System.nanoTime();
        input.setPatientGender("F", Config.getCodeSystemOid("GENDER"));
        input.setPatientBirthTime("19830630");
         logger.logDuration("input init", start);

        start = System.nanoTime();
        logger.info(CdsObjectAssist.cdsObjectToString(input.getCdsObject(), CDSInput.class));
        logger.logDuration("cdsObjectToString", start);

        logger.info("Finished testGetCdsInputWrapper...");
        assertTrue(true);
    }

}
