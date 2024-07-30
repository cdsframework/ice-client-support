/**
 * The cdsframework support client aims at making ICE vMR generation easier.
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
package org.cdsframework.ice.wrapper;

import java.util.Date;
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

/**
 *
 * @author HLN Consulting, LLC
 */
public class IceCdsInputWrapperTest {

    private static LogUtils logger = LogUtils.getLogger(IceCdsInputWrapperTest.class);

    public IceCdsInputWrapperTest() {
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
        start = System.nanoTime();

        // preform first to time get the static initialization of CdsObjectAssist out of the way
        IceCdsInputWrapper iceCdsInputWrapper = new IceCdsInputWrapper();

        logger.logDuration("getInput", start);

        start = System.nanoTime();
        iceCdsInputWrapper.setPatientGender("F", Config.getCodeSystemOid("GENDER"));
        iceCdsInputWrapper.setPatientBirthTime("19830630");
        iceCdsInputWrapper.addSubstanceAdministrationEvent(
                "45", Config.getCodeSystemOid("VACCINE"),
                "20080223",
                "12345", Config.getCodeSystemOid("ADMINISTRATION_ID"));
        iceCdsInputWrapper.addSubstanceAdministrationEvent(
                "08", Config.getCodeSystemOid("VACCINE"),
                "20080223",
                "12346", Config.getCodeSystemOid("ADMINISTRATION_ID"));
        iceCdsInputWrapper.addSubstanceAdministrationEvent(
                "151", Config.getCodeSystemOid("VACCINE"),
                "20080223",
                "12347", Config.getCodeSystemOid("ADMINISTRATION_ID"), true);
        iceCdsInputWrapper.addSubstanceAdministrationEvent(
                "47", Config.getCodeSystemOid("VACCINE"),
                "20080223",
                "12347", Config.getCodeSystemOid("ADMINISTRATION_ID"), false);
        iceCdsInputWrapper.addSubstanceAdministrationEvent(
                "28", Config.getCodeSystemOid("VACCINE"),
                "20080223",
                "12347", Config.getCodeSystemOid("ADMINISTRATION_ID"), null);
        iceCdsInputWrapper.addImmunityObservationResult(new Date(), null,
                "070.30", Config.getCodeSystemOid("DISEASE"),
                "DISEASE_DOCUMENTED", Config.getCodeSystemOid("IMMUNITY_VALUE"),
                "IS_IMMUNE", Config.getCodeSystemOid("IMMUNITY_INTERPRETATION"));
        logger.logDuration("input init", start);

        start = System.nanoTime();
        logger.info(CdsObjectAssist.cdsObjectToString(iceCdsInputWrapper.getCdsInput(), CDSInput.class));
        logger.logDuration("cdsObjectToString", start);

        logger.info("Finished testGetCdsInputWrapper...");
        assertTrue(true);
    }

    /**
     * Test of addSubstanceAdministrationEvent method, of class CdsInputWrapper.
     * @throws Exception
     */
    @Test
    public void testAddSubstanceAdministrationEvent_StringDate() throws Exception {
        long start;
        logger.info("Starting testAddSubstanceAdministrationEvent_StringDate...");

        IceCdsInputWrapper iceCdsInputWrapper = new IceCdsInputWrapper();
        start = System.nanoTime();
        iceCdsInputWrapper.addSubstanceAdministrationEvent(
                "45", Config.getCodeSystemOid("VACCINE"),
                "20080223",
                "12345", Config.getCodeSystemOid("ADMINISTRATION_ID"));
        logger.logDuration("input init", start);

        logger.info("Finished testAddSubstanceAdministrationEvent_StringDate...");
        assertTrue(true);
    }

    /**
     * Test of addSubstanceAdministrationEvent method, of class CdsInputWrapper.
     * @throws Exception
     */
    @Test
    public void testAddSubstanceAdministrationEvent_Date() throws Exception {
        long start;
        logger.info("Starting testAddSubstanceAdministrationEvent_Date...");

        IceCdsInputWrapper iceCdsInputWrapper = new IceCdsInputWrapper();
        start = System.nanoTime();
        iceCdsInputWrapper.addSubstanceAdministrationEvent(
                "45", Config.getCodeSystemOid("VACCINE"),
                new Date(),
                "12345", Config.getCodeSystemOid("ADMINISTRATION_ID"));
        logger.logDuration("input init", start);

        logger.info("Finished testAddSubstanceAdministrationEvent_Date...");
        assertTrue(true);
    }

    /**
     * Test of addImmunityObservationResult method, of class CdsInputWrapper.
     * @throws Exception
     */
    @Test
    public void testAddImmunityObservationResult() throws Exception {
        long start;
        logger.info("Starting testAddImmunityObservationResult...");

        IceCdsInputWrapper iceCdsInputWrapper = new IceCdsInputWrapper();
        start = System.nanoTime();
        iceCdsInputWrapper.addImmunityObservationResult(new Date(), null,
                "070.30", Config.getCodeSystemOid("DISEASE"),
                "DISEASE_DOCUMENTED", Config.getCodeSystemOid("IMMUNITY_VALUE"),
                "IS_IMMUNE", Config.getCodeSystemOid("IMMUNITY_INTERPRETATION"));
        logger.logDuration("input init", start);

        logger.info("Finished testAddImmunityObservationResult...");
        assertTrue(true);
    }
}
