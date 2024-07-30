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
package org.cdsframework.cds.service;

import java.util.Date;
import org.cdsframework.cds.vmr.CdsInputWrapper;
import org.cdsframework.cds.vmr.CdsObjectAssist;
import org.cdsframework.util.LogUtils;
import org.cdsframework.util.support.cds.Config;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opencds.vmr.v1_0.schema.CDSInput;
import org.opencds.vmr.v1_0.schema.CDSOutput;

/**
 *
 * @author HLN Consulting, LLC
 */
public class OpenCdsServiceTest {

    private static LogUtils logger = LogUtils.getLogger(OpenCdsServiceTest.class);

    public OpenCdsServiceTest() {
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
     * Test of evaluate method, of class OpenCdsService.
     * @throws Exception
     */
    @Test 
    public void testEvaluate() throws Exception {
        long start;
        logger.info("Starting testEvaluate...");

        String endPoint = Config.getCdsDefaultEndpoint();

        // preform first to time get the static initialization of OpenCdsService out of the way
        OpenCdsService.getOpenCdsService(endPoint);

        start = System.nanoTime();
        OpenCdsService service = OpenCdsService.getOpenCdsService(endPoint);
        logger.logDuration("OpenCdsService init", start);

        // preform first to time get the static initialization of CdsObjectAssist out of the way
        CdsInputWrapper.getCdsInputWrapper();

        start = System.nanoTime();
        CdsInputWrapper input = CdsInputWrapper.getCdsInputWrapper();
        input.setPatientGender("F", Config.getCodeSystemOid("GENDER"));
        input.setPatientBirthTime("19830630");
        logger.logDuration("getCdsInputWrapper", start);

        String inputXml = CdsObjectAssist.cdsObjectToString(input.getCdsObject(), CDSInput.class);

        String scopingEntityId = "org.nyc.cir";
        String businessId = "ICE";
        String version = "1.0.0";
        Date executionDate = new Date();

        start = System.nanoTime();
        byte[] cdsObjectToByteArray = CdsObjectAssist.cdsObjectToByteArray(input.getCdsObject(), CDSInput.class);
        logger.logDuration("cdsObjectToByteArray", start);

        start = System.nanoTime();
        byte[] evaluation = service.evaluate(cdsObjectToByteArray, scopingEntityId, businessId, version, executionDate);
        logger.logDuration("evaluate test 1", start);

        start = System.nanoTime();
        evaluation = service.evaluate(cdsObjectToByteArray, scopingEntityId, businessId, version, executionDate);
        logger.logDuration("evaluate test 2", start);

        start = System.nanoTime();
        evaluation = service.evaluate(cdsObjectToByteArray, scopingEntityId, businessId, version, executionDate);
        logger.logDuration("evaluate test 3", start);

        start = System.nanoTime();
        evaluation = service.evaluate(cdsObjectToByteArray, scopingEntityId, businessId, version, executionDate);
        logger.logDuration("evaluate test 4", start);

        start = System.nanoTime();
        evaluation = service.evaluate(cdsObjectToByteArray, scopingEntityId, businessId, version, executionDate);
        logger.logDuration("evaluate test 5", start);

        start = System.nanoTime();
        CDSOutput result = CdsObjectAssist.cdsObjectFromByteArray(evaluation, CDSOutput.class);
        logger.logDuration("cdsObjectFromByteArray", start);

        logger.info("Finished testEvaluate...");
        assertTrue(result instanceof CDSOutput);
    }
}
