/**
 * The cdsframework support client aims at making ICE vMR generation easier.
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
package org.cdsframework.ice.service;

import java.util.Date;
import org.cdsframework.cds.service.OpenCdsService;
import org.cdsframework.cds.vmr.CdsObjectAssist;
import org.cdsframework.ice.input.IceCdsInputWrapper;
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
public class IceServiceTest {

    private static LogUtils logger = LogUtils.getLogger(IceServiceTest.class);

    public IceServiceTest() {
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
        IceCdsInputWrapper iceCdsInputWrapper = new IceCdsInputWrapper();

        start = System.nanoTime();
        iceCdsInputWrapper.setPatientGender("F", Config.getCodeSystemOid("GENDER"));
        iceCdsInputWrapper.setPatientBirthTime("19830630");
        iceCdsInputWrapper.addSubstanceAdministrationEvent("45", Config.getCodeSystemOid("VACCINE"), "20080223", null, null);
        logger.logDuration("getCdsInputWrapper", start);

        iceCdsInputWrapper.addSubstanceAdministrationEvent(
                "10", Config.getCodeSystemOid("VACCINE"),
                "20080223",
                "12345", Config.getCodeSystemOid("ADMINISTRATION_ID"));
        iceCdsInputWrapper.addImmunityObservationResult(new Date(), null,
                "070.30", Config.getCodeSystemOid("DISEASE"),
                "DISEASE_DOCUMENTED", Config.getCodeSystemOid("IMMUNITY_VALUE"), 
                "IS_IMMUNE", Config.getCodeSystemOid("IMMUNITY_INTERPRETATION"));

        iceCdsInputWrapper.addSubstanceAdministrationEvent(
                "43", Config.getCodeSystemOid("VACCINE"),
                "20080223",
                "12346", Config.getCodeSystemOid("ADMINISTRATION_ID"));

        iceCdsInputWrapper.addSubstanceAdministrationEvent(
                "08", Config.getCodeSystemOid("VACCINE"),
                "20090223",
                "12347", Config.getCodeSystemOid("ADMINISTRATION_ID"));

        iceCdsInputWrapper.addSubstanceAdministrationEvent(
                "43", Config.getCodeSystemOid("VACCINE"),
                "20080223",
                "12348", Config.getCodeSystemOid("ADMINISTRATION_ID"));

        String inputXml = CdsObjectAssist.cdsObjectToString(iceCdsInputWrapper.getCdsInput(), CDSInput.class);

        String scopingEntityId = "org.nyc.cir";
        String businessId = "ICE";
        String version = "1.0.0";
        Date executionDate = new Date();

        start = System.nanoTime();
        byte[] cdsObjectToByteArray = CdsObjectAssist.cdsObjectToByteArray(iceCdsInputWrapper.getCdsInput(), CDSInput.class);
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
