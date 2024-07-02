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
package org.cdsframework.bpmn.support;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import jakarta.xml.bind.JAXBException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author HLN Consulting, LLC
 */
public class BPMNParserTest {

    public BPMNParserTest() {
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
     * Test of getRuleFlowGroups method, of class BPMNParser.
     * @throws JAXBException
     * @throws FileNotFoundException
     * @throws IOException
     */
    @Test
    public void testGetRuleFlowGroups() throws JAXBException, FileNotFoundException, IOException {
        System.out.println("getRuleFlowGroups");
        String bpmnFile = "org.nyc.cir^ICE^1.0.0.bpmn";
        BPMNParser bpmnParser = new BPMNParser(bpmnFile);

        List<BPMNParser.RuleFlowGroup> ruleFlowGroups = bpmnParser.getRuleFlowGroups();
        for (BPMNParser.RuleFlowGroup ruleFlowGroup : ruleFlowGroups) {
            System.out.println(ruleFlowGroup.getRuleFlowGroupCode() + "/" + ruleFlowGroup.getRuleFlowGroupName());
        }

        // Read the file in and convert to byte array
        System.out.println("read " + bpmnFile + " and convert to bytes");
        FileInputStream fileInputStream = new java.io.FileInputStream(bpmnFile);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        try {
            for (int readNum; (readNum = fileInputStream.read(buffer)) != -1;) {
                byteArrayOutputStream.write(buffer, 0, readNum); //no doubt here is 0
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        byte[] bytes = byteArrayOutputStream.toByteArray();
        System.out.println("bytes.length " + bytes.length);
        fileInputStream.close();
        byteArrayOutputStream.close();

        System.out.println("Use ByteArrayInputStream with Bufferred reader");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(byteArrayInputStream));
        bpmnParser = new BPMNParser(bufferedReader);
        byteArrayInputStream.close();
        bufferedReader.close();

        ruleFlowGroups = bpmnParser.getRuleFlowGroups();
        for (BPMNParser.RuleFlowGroup ruleFlowGroup : ruleFlowGroups) {
            System.out.println(ruleFlowGroup.getRuleFlowGroupCode() + "/" + ruleFlowGroup.getRuleFlowGroupName());
        }
    }
}
