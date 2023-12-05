/**
 * The cdsframework support client aims at making ICE Test Case generation easier.
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
package org.cdsframework.ice.testcase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.cdsframework.cds.util.CdsObjectFactory;
import org.cdsframework.cds.vmr.CdsObjectAssist;
import org.cdsframework.enumeration.TestCasePropertyType;
import org.cdsframework.util.LogUtils;
import org.cdsframework.util.support.cds.Config;
import org.cdsframework.util.support.data.cds.testcase.TestCase;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opencds.vmr.v1_0.schema.CD;
import org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent;

/**
 *
 * @author HLN Consulting, LLC
 */
public class TestCaseWrapperTest {

    private final static LogUtils logger = LogUtils.getLogger(TestCaseWrapperTest.class);

    public TestCaseWrapperTest() {
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
     * Test of getTestCaseWrapper method, of class TestCaseWrapper.
     *
     * @throws Exception
     */
    @Test
    public void testGetTestCaseWrapper() throws Exception {
        long start;
        logger.info("Starting testGetTestCaseWrapper...");

        // preform first to time get the static initialization of CdsObjectAssist out of the way
        TestCaseWrapper.getTestCaseWrapper();

        start = System.nanoTime();
        TestCaseWrapper testcase = TestCaseWrapper.getTestCaseWrapper();
        logger.logDuration("getTestCaseWrapper", start);

        start = System.nanoTime();
        testcase.setExecutiondate(new Date());
        testcase.setSuiteName("MyTestSuite");
        testcase.setGroupName("MyTestGroup");
        testcase.setIgnore(false);
        testcase.setName("blah blah - hey hey");
        testcase.setNotes("blah blah blh blah blah ghlsdf sdfh aof asodiv das vasdvpoi asdv asdvsadvp asdfg");
        testcase.setPatientBirthTime("19700130");
        testcase.setPatientGender("M", Config.getCodeSystemOid("GENDER"));
        testcase.setPatientId("54321");
        testcase.setRuletotest("asjdfhla asdf alksd pdf ahf apahwfe qwefh3984 qenka sd87134tu3hpjahfiq asdf");
        testcase.addProperty("vaccineGroup", "HepB", TestCasePropertyType.STRING);
        testcase.addImmunityObservationResult(new Date(), null,
                "070.30", Config.getCodeSystemOid("DISEASE"),
                "PROOF_OF_IMMUNITY", Config.getCodeSystemOid("IMMUNITY_VALUE"),
                "IS_IMMUNE", Config.getCodeSystemOid("IMMUNITY_INTERPRETATION"));
        SubstanceAdministrationEvent hepBComponent
                = testcase.getEvaluationSubstanceAdministrationEvent(
                        "45", Config.getCodeSystemOid("VACCINE"),
                        "20080223",
                        "VALID", Config.getCodeSystemOid("EVALUATION_VALUE"),
                        "100", Config.getCodeSystemOid("VACCINE_GROUP"),
                        null, Config.getCodeSystemOid("EVALUATION_INTERPRETATION"));

        List<SubstanceAdministrationEvent> components = new ArrayList<SubstanceAdministrationEvent>();
        components.add(hepBComponent);

        testcase.addSubstanceAdministrationEvent(
                "45", Config.getCodeSystemOid("VACCINE"),
                "20080223",
                null, Config.getCodeSystemOid("ADMINISTRATION_ID"),
                components);

        List<CD> reasons = new ArrayList<CD>();
        CD dueNow = CdsObjectFactory.getCD("DUE_NOW", Config.getCodeSystemOid("RECOMMENDATION_INTERPRETATION"));
        reasons.add(dueNow);

        testcase.addSubstanceAdministrationProposal(
                "100", Config.getCodeSystemOid("VACCINE_GROUP"),
                null, Config.getCodeSystemOid("VACCINE"),
                "20090223",
                "100", Config.getCodeSystemOid("VACCINE_GROUP"),
                "RECOMMENDED", Config.getCodeSystemOid("RECOMMENDATION_VALUE"),
                reasons);

        reasons = new ArrayList<CD>();
        CD complete = CdsObjectFactory.getCD("COMPLETE", Config.getCodeSystemOid("RECOMMENDATION_INTERPRETATION"));
        reasons.add(complete);

        testcase.addSubstanceAdministrationProposal(
                "810", Config.getCodeSystemOid("VACCINE_GROUP"),
                null, Config.getCodeSystemOid("VACCINE"),
                "",
                "810", Config.getCodeSystemOid("VACCINE_GROUP"),
                "NOT_RECOMMENDED", Config.getCodeSystemOid("RECOMMENDATION_VALUE"),
                reasons);
        logger.logDuration("testcase init", start);

        start = System.nanoTime();
        logger.info(CdsObjectAssist.cdsObjectToString(testcase.getTestCase(), TestCase.class));
        logger.logDuration("cdsObjectToString", start);

        logger.info("Finished testGetTestCaseWrapper...");
        assertTrue(true);
    }
}
