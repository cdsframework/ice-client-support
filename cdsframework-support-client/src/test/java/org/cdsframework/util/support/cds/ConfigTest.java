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
package org.cdsframework.util.support.cds;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author HLN Consulting, LLC
 */
public class ConfigTest {

    private Properties defaultSystemProperties;
    private Properties codeSystemProperties;

    public ConfigTest() throws IOException {
        defaultSystemProperties = new Properties();
        defaultSystemProperties.load(new FileInputStream("src/main/resources/cdsSystemDefaults.properties"));
        codeSystemProperties = new Properties();
        codeSystemProperties.load(new FileInputStream("src/main/resources/codeSystems.properties"));
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
     * Test of getCodeSystemOid method, of class Configuration.
     */
    @Test
    public void testGetCodeSystemOid() {
        System.out.println("getCodeSystemOid");
        String codeSystemName = "DISEASE";
        String expResult = codeSystemProperties.getProperty(codeSystemName);
        String result = Config.getCodeSystemOid(codeSystemName);
        assertEquals(expResult, result);
    }

    /**
     * Test of getCdsWsdlUrl method, of class Configuration.
     *
     * @throws MalformedURLException
     * @throws IOException
     */
    @Test
    public void testGetCdsWsdlUrl() throws MalformedURLException, IOException {
        System.out.println("getCdsWsdlUrl");
        String resultContent = "";
        BufferedReader in;
        String inputLine;
        URL result = Config.getCdsWsdlUrl();
        in = new BufferedReader(new InputStreamReader(result.openStream()));
        while ((inputLine = in.readLine()) != null) {
            resultContent += inputLine;
        }
        in.close();
        assertNotNull(resultContent);
    }

    /**
     * Test of getCdsNamespaceUri method, of class Configuration.
     */
    @Test
    public void testGetCdsNamespaceUri() {
        System.out.println("getCdsNamespaceUri");
        String expResult = defaultSystemProperties.getProperty("CDS_NAMESPACE_URI");
        String result = Config.getCdsNamespaceUri();
        assertEquals(expResult, result);
    }

    /**
     * Test of getCdsLocalPart method, of class Configuration.
     */
    @Test
    public void testGetCdsLocalPart() {
        System.out.println("getCdsLocalPart");
        String expResult = defaultSystemProperties.getProperty("CDS_LOCAL_PART");
        String result = Config.getCdsLocalPart();
        assertEquals(expResult, result);
    }

    /**
     * Test of getCdsDefaultTimeout method, of class Configuration.
     */
    @Test
    public void testGetCdsDefaultTimeout() {
        System.out.println("getCdsDefaultTimeout");
        int expResult = Integer.parseInt(defaultSystemProperties.getProperty("CDS_DEFAULT_TIMEOUT"));
        int result = Config.getCdsDefaultTimeout();
        assertEquals(expResult, result);
    }

    /**
     * Test of getHttpClientStreamingChunkSize method, of class Configuration.
     */
    @Test
    public void testGetHttpClientStreamingChunkSize() {
        System.out.println("getHttpClientStreamingChunkSize");
        int expResult = Integer.parseInt(defaultSystemProperties.getProperty("HTTP_CLIENT_STREAMING_CHUNK_SIZE"));
        int result = Config.getHttpClientStreamingChunkSize();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDefaultClientLanguage method, of class Configuration.
     */
    @Test
    public void testGetDefaultClientLanguage() {
        System.out.println("getDefaultClientLanguage");
        String expResult = defaultSystemProperties.getProperty("DEFAULT_CLIENT_LANGUAGE");
        String result = Config.getDefaultClientLanguage();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDefaultClientTimezoneOffset method, of class Configuration.
     */
    @Test
    public void testGetDefaultClientTimezoneOffset() {
        System.out.println("getDefaultClientTimezoneOffset");
        String expResult = defaultSystemProperties.getProperty("DEFAULT_CLIENT_TIMEZONE_OFFSET");
        String result = Config.getDefaultClientTimezoneOffset();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDefaultInteractionId method, of class Configuration.
     */
    @Test
    public void testGetDefaultInteractionId() {
        System.out.println("getDefaultInteractionId");
        String expResult = defaultSystemProperties.getProperty("DEFAULT_INTERACTION_ID");
        String result = Config.getDefaultInteractionId();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDefaultEntityItemIdentifierSuffix method, of class Configuration.
     */
    @Test
    public void testGetDefaultEntityItemIdentifierSuffix() {
        System.out.println("getDefaultEntityItemIdentifierSuffix");
        String expResult = defaultSystemProperties.getProperty("DEFAULT_ENTITY_ITEM_IDENTIFIER_SUFFIX");
        String result = Config.getDefaultEntityItemIdentifierSuffix();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDefaultItemIdentifierItemId method, of class Configuration.
     */
    @Test
    public void testGetDefaultItemIdentifierItemId() {
        System.out.println("getDefaultItemIdentifierItemId");
        String expResult = defaultSystemProperties.getProperty("DEFAULT_ITEM_IDENTIFIER_ITEM_ID");
        String result = Config.getDefaultItemIdentifierItemId();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDefaultSemanticPayloadBusinessId method, of class Configuration.
     */
    @Test
    public void testGetDefaultSemanticPayloadBusinessId() {
        System.out.println("getDefaultSemanticPayloadBusinessId");
        String expResult = defaultSystemProperties.getProperty("DEFAULT_SEMANTIC_PAYLOAD_BUSINESS_ID");
        String result = Config.getDefaultSemanticPayloadBusinessId();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDefaultSemanticPayloadScopingEntityId method, of class Configuration.
     */
    @Test
    public void testGetDefaultSemanticPayloadScopingEntityId() {
        System.out.println("getDefaultSemanticPayloadScopingEntityId");
        String expResult = defaultSystemProperties.getProperty("DEFAULT_SEMANTIC_PAYLOAD_SCOPING_ENTITY_ID");
        String result = Config.getDefaultSemanticPayloadScopingEntityId();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDefaultSemanticPayloadVersion method, of class Configuration.
     */
    @Test
    public void testGetDefaultSemanticPayloadVersion() {
        System.out.println("getDefaultSemanticPayloadVersion");
        String expResult = defaultSystemProperties.getProperty("DEFAULT_SEMANTIC_PAYLOAD_VERSION");
        String result = Config.getDefaultSemanticPayloadVersion();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDefaultLanguageCode method, of class Configuration.
     */
    @Test
    public void testGetDefaultLanguageCode() {
        System.out.println("getDefaultLanguageCode");
        String expResult = defaultSystemProperties.getProperty("DEFAULT_LANG_CODE");
        String result = Config.getDefaultLanguageCode();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDefaultLanguageDisplayName method, of class Configuration.
     */
    @Test
    public void testGetDefaultLanguageDisplayName() {
        System.out.println("getDefaultLanguageDisplayName");
        String expResult = defaultSystemProperties.getProperty("DEFAULT_LANG_DISPLAY_NAME");
        String result = Config.getDefaultLanguageDisplayName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDefaultLanguageOid method, of class Configuration.
     */
    @Test
    public void testGetDefaultLanguageOid() {
        System.out.println("getDefaultLanguageOid");
        String expResult = codeSystemProperties.getProperty("LANG");
        String result = Config.getDefaultLanguageOid();
        assertEquals(expResult, result);
    }

    /**
     * Test of getGeneralPurposeCode method, of class Configuration.
     */
    @Test
    public void testGetGeneralPurposeCode() {
        System.out.println("getGeneralPurposeCode");
        String expResult = defaultSystemProperties.getProperty("GENERAL_PURPOSE_CODE");
        String result = Config.getGeneralPurposeCode();
        assertEquals(expResult, result);
    }
}
