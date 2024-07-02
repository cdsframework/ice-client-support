/**
 * The cdsframework support client aims at making vMR generation easier.
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
package org.cdsframework.cds.vmr;

import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdsframework.util.support.cds.Config;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opencds.vmr.v1_0.schema.CDSOutput;

/**
 *
 * @author HLN Consulting, LLC
 */
public class CdsObjectAssistTest {

    private final static Logger logger = LogManager.getLogger();
    private byte[] tmp;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testCdsObjectToString() throws Exception {
        logger.info("Starting testCdsObjectToString...");
        CdsOutputWrapper output = CdsOutputWrapper.getCdsOutputWrapper();
        output.setPatientGender("F", Config.getCodeSystemOid("GENDER"));
        output.setPatientBirthTime("19830630");
        logger.info(CdsObjectAssist.cdsObjectToString(output.getCdsObject(), CDSOutput.class));
        logger.info("Finished testCdsObjectToString...");
        assertTrue(true);
    }

    @Test
    public void testCdsObjectToByteArray() throws Exception {
        logger.info("Starting testCdsObjectToByteArray...");
        CdsOutputWrapper output = CdsOutputWrapper.getCdsOutputWrapper();
        output.setPatientGender("F", Config.getCodeSystemOid("GENDER"));
        output.setPatientBirthTime("19830630");
        tmp = CdsObjectAssist.cdsObjectToByteArray(output.getCdsObject(), CDSOutput.class);
        logger.info("Finished testCdsObjectToByteArray...");
        assertTrue(true);
    }

    @Test
    public void testCdsObjectFromByteArray() throws Exception {
        logger.info("Starting testCdsObjectFromByteArray...");
        CdsOutputWrapper output = CdsOutputWrapper.getCdsOutputWrapper();
        output.setPatientGender("F", Config.getCodeSystemOid("GENDER"));
        output.setPatientBirthTime("19830630");
        tmp = CdsObjectAssist.cdsObjectToByteArray(output.getCdsObject(), CDSOutput.class);
        CDSOutput cdsObjectFromByteArray = CdsObjectAssist.cdsObjectFromByteArray(tmp, CDSOutput.class);
        logger.info("Gender: " + cdsObjectFromByteArray.getVmrOutput().getPatient().getDemographics().getGender().getCode());
        logger.info("Finished testCdsObjectFromByteArray...");
        assertTrue(true);
    }

    @Test
    public void testCdsObjectToFromFile() throws Exception {
        logger.info("Starting testCdsObjectToFile...");
        
        CdsOutputWrapper output = CdsOutputWrapper.getCdsOutputWrapper();
        output.setPatientGender("F", Config.getCodeSystemOid("GENDER"));
        output.setPatientBirthTime("19830630");
        String filename = CdsObjectAssist.cdsObjectToFile(output.getCdsObject(), null, "sampleCdsOutput.xml");
        logger.info(filename);
        CDSOutput cdsOutputFromFile = CdsObjectAssist.cdsObjectFromFile(filename, CDSOutput.class);
        logger.info("Gender: " + cdsOutputFromFile.getVmrOutput().getPatient().getDemographics().getGender().getCode());
        File file = new File(filename);
        file.delete();
        logger.info("Finished testCdsObjectToFromFile...");
        assertTrue(true);
    }
}
