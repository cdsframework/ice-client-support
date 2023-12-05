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
package org.cdsframework.util.support.cds;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Configuration utility class for pulling some system defaults from property files.
 *
 * @author HLN Consulting, LLC
 */
public class Config {

    private static final String CODE_SYSTEM_PROPERTY_FILE_LOCATION = "codeSystems.properties";
    private static final String CDS_SYSTEM_DEFAULTS_FILE_LOCATION = "cdsSystemDefaults.properties";
    private static URL CDS_WSDL_URL;
    private static String CDS_NAMESPACE_URI;
    private static String CDS_LOCAL_PART;
    private static String CDS_DEFAULT_ENDPOINT;
    private static int CDS_DEFAULT_TIMEOUT;
    private static String DEFAULT_LANG_CODE;
    private static String DEFAULT_LANG_DISPLAY_NAME;
    private static String DEFAULT_LANG_OID;
    private static String DEFAULT_CLIENT_LANGUAGE;
    private static String DEFAULT_CLIENT_TIMEZONE_OFFSET;
    private static String DEFAULT_ENTITY_ITEM_IDENTIFIER_SUFFIX;
    private static String DEFAULT_ITEM_IDENTIFIER_ITEM_ID;
    private static String DEFAULT_INTERACTION_ID;
    private static String DEFAULT_SEMANTIC_PAYLOAD_BUSINESS_ID;
    private static String DEFAULT_SEMANTIC_PAYLOAD_SCOPING_ENTITY_ID;
    private static String DEFAULT_SEMANTIC_PAYLOAD_VERSION;
    private static int HTTP_CLIENT_STREAMING_CHUNK_SIZE;
    private static String GENERAL_PURPOSE_CODE;
    private static final Map<String, String> codeSystemNameOidMap = new HashMap<String, String>();

    /**
     * Gets a code system OID with the supplied name from the code system properties file.
     *
     * @param codeSystemName the name used as the key in the code system properties file.
     * @return the OID
     */
    public static String getCodeSystemOid(String codeSystemName) {
        final String METHODNAME = "getCodeSystemOid ";
        if (codeSystemName == null) {
            throw new IllegalArgumentException(METHODNAME + "codeSystemName was null!");
        }
        if (codeSystemNameOidMap.isEmpty()) {
            InputStream resource = Config.class.getClassLoader().getResourceAsStream(CODE_SYSTEM_PROPERTY_FILE_LOCATION);
            Properties codeSystemMapProperties = new Properties();
            try {
                codeSystemMapProperties.load(resource);
            } catch (IOException e) {
                throw new IllegalArgumentException(METHODNAME + e.getMessage() + " - error retrieving " + CODE_SYSTEM_PROPERTY_FILE_LOCATION);
            } finally {
                try {
                    if (resource != null) {
                        resource.close();
                    }
                } catch (Exception e) {
                    // do nothing
                }
            }
            for (String codeSystem : codeSystemMapProperties.stringPropertyNames()) {
                codeSystemNameOidMap.put(codeSystem.trim(), codeSystemMapProperties.getProperty(codeSystem).trim());
            }
        }
        return codeSystemNameOidMap.get(codeSystemName.trim().toUpperCase());
    }

    /**
     * Gets the default OpenCDS WSDL URL from the system properties file.
     *
     * @return the default OpenCDS WSDL URL
     */
    public static URL getCdsWsdlUrl() {
        if (CDS_WSDL_URL == null) {
            CDS_WSDL_URL = Config.class.getClassLoader().getResource(getCdsSystemDefaultProperty("CDS_WSDL_URL"));
        }
        return CDS_WSDL_URL;
    }

    /**
     * Gets the default OpenCDS namespace URI from the system properties file.
     *
     * @return the default OpenCDS namespace URI
     */
    public static String getCdsNamespaceUri() {
        if (CDS_NAMESPACE_URI == null) {
            CDS_NAMESPACE_URI = getCdsSystemDefaultProperty("CDS_NAMESPACE_URI");
        }
        return CDS_NAMESPACE_URI;
    }

    /**
     * Gets the default OpenCDS local part for the service connection from the system properties file.
     *
     * @return the default OpenCDS local part for the service connection
     */
    public static String getCdsLocalPart() {
        if (CDS_LOCAL_PART == null) {
            CDS_LOCAL_PART = getCdsSystemDefaultProperty("CDS_LOCAL_PART");
        }
        return CDS_LOCAL_PART;
    }

    /**
     * Gets the default OpenCDS service endpoint from the system properties file.
     *
     * @return the default OpenCDS service endpoint
     */
    public static String getCdsDefaultEndpoint() {
        if (CDS_DEFAULT_ENDPOINT == null) {
            CDS_DEFAULT_ENDPOINT = getCdsSystemDefaultProperty("CDS_DEFAULT_ENDPOINT");
        }
        return CDS_DEFAULT_ENDPOINT;
    }

    /**
     * Gets the default OpenCDS service timeout from the system properties file.
     *
     * @return the default OpenCDS service timeout
     */
    public static int getCdsDefaultTimeout() {
        if (CDS_DEFAULT_TIMEOUT == 0) {
            CDS_DEFAULT_TIMEOUT = Integer.parseInt(getCdsSystemDefaultProperty("CDS_DEFAULT_TIMEOUT"));
        }
        return CDS_DEFAULT_TIMEOUT;
    }

    /**
     * Gets the default HTTP client streaming chunk size from the system properties file.
     *
     * @return the default HTTP client streaming chunk size
     */
    public static int getHttpClientStreamingChunkSize() {
        if (HTTP_CLIENT_STREAMING_CHUNK_SIZE == 0) {
            HTTP_CLIENT_STREAMING_CHUNK_SIZE = Integer.parseInt(getCdsSystemDefaultProperty("HTTP_CLIENT_STREAMING_CHUNK_SIZE"));
        }
        return HTTP_CLIENT_STREAMING_CHUNK_SIZE;
    }

    /**
     * Gets the default language code from the system properties file.
     *
     * @return the default language code
     */
    public static String getDefaultLanguageCode() {
        if (DEFAULT_LANG_CODE == null) {
            DEFAULT_LANG_CODE = getCdsSystemDefaultProperty("DEFAULT_LANG_CODE");
        }
        return DEFAULT_LANG_CODE;
    }

    /**
     * Gets the default language display name from the system properties file.
     *
     * @return the default language display name
     */
    public static String getDefaultLanguageDisplayName() {
        if (DEFAULT_LANG_DISPLAY_NAME == null) {
            DEFAULT_LANG_DISPLAY_NAME = getCdsSystemDefaultProperty("DEFAULT_LANG_DISPLAY_NAME");
        }
        return DEFAULT_LANG_DISPLAY_NAME;
    }

    /**
     * Gets the default language OID from the system properties file.
     *
     * @return the default language OID
     */
    public static String getDefaultLanguageOid() {
        if (DEFAULT_LANG_OID == null) {
            DEFAULT_LANG_OID = getCodeSystemOid("LANG");
        }
        return DEFAULT_LANG_OID;
    }

    /**
     * Gets the default client language from the system properties file.
     *
     * @return the default client language
     */
    public static String getDefaultClientLanguage() {
        if (DEFAULT_CLIENT_LANGUAGE == null) {
            DEFAULT_CLIENT_LANGUAGE = getCdsSystemDefaultProperty("DEFAULT_CLIENT_LANGUAGE");
        }
        return DEFAULT_CLIENT_LANGUAGE;
    }

    /**
     * Gets the default client time zone offset from the system properties file.
     *
     * @return the default client time zone offset
     */
    public static String getDefaultClientTimezoneOffset() {
        if (DEFAULT_CLIENT_TIMEZONE_OFFSET == null) {
            DEFAULT_CLIENT_TIMEZONE_OFFSET = getCdsSystemDefaultProperty("DEFAULT_CLIENT_TIMEZONE_OFFSET");
        }
        return DEFAULT_CLIENT_TIMEZONE_OFFSET;
    }

    /**
     * Gets the default interaction ID from the system properties file.
     *
     * @return the default interaction ID
     */
    public static String getDefaultInteractionId() {
        if (DEFAULT_INTERACTION_ID == null) {
            DEFAULT_INTERACTION_ID = getCdsSystemDefaultProperty("DEFAULT_INTERACTION_ID");
        }
        return DEFAULT_INTERACTION_ID;
    }

    /**
     * Gets the default entity item identifier suffix from the system properties file.
     *
     * @return the default entity item identifier suffix
     */
    public static String getDefaultEntityItemIdentifierSuffix() {
        if (DEFAULT_ENTITY_ITEM_IDENTIFIER_SUFFIX == null) {
            DEFAULT_ENTITY_ITEM_IDENTIFIER_SUFFIX = getCdsSystemDefaultProperty("DEFAULT_ENTITY_ITEM_IDENTIFIER_SUFFIX");
        }
        return DEFAULT_ENTITY_ITEM_IDENTIFIER_SUFFIX;
    }

    /**
     * Gets the default item identifier item ID from the system properties file.
     *
     * @return the default item identifier item ID
     */
    public static String getDefaultItemIdentifierItemId() {
        if (DEFAULT_ITEM_IDENTIFIER_ITEM_ID == null) {
            DEFAULT_ITEM_IDENTIFIER_ITEM_ID = getCdsSystemDefaultProperty("DEFAULT_ITEM_IDENTIFIER_ITEM_ID");
        }
        return DEFAULT_ITEM_IDENTIFIER_ITEM_ID;
    }

    /**
     * Gets the default semantic payload business ID from the system properties file.
     *
     * @return the default semantic payload business ID
     */
    public static String getDefaultSemanticPayloadBusinessId() {
        if (DEFAULT_SEMANTIC_PAYLOAD_BUSINESS_ID == null) {
            DEFAULT_SEMANTIC_PAYLOAD_BUSINESS_ID = getCdsSystemDefaultProperty("DEFAULT_SEMANTIC_PAYLOAD_BUSINESS_ID");
        }
        return DEFAULT_SEMANTIC_PAYLOAD_BUSINESS_ID;
    }

    /**
     * Gets the default semantic payload scoping entity ID from the system properties file.
     *
     * @return the default semantic payload scoping entity ID
     */
    public static String getDefaultSemanticPayloadScopingEntityId() {
        if (DEFAULT_SEMANTIC_PAYLOAD_SCOPING_ENTITY_ID == null) {
            DEFAULT_SEMANTIC_PAYLOAD_SCOPING_ENTITY_ID = getCdsSystemDefaultProperty("DEFAULT_SEMANTIC_PAYLOAD_SCOPING_ENTITY_ID");
        }
        return DEFAULT_SEMANTIC_PAYLOAD_SCOPING_ENTITY_ID;
    }

    /**
     * Gets the default semantic payload version from the system properties file.
     *
     * @return the default semantic payload version
     */
    public static String getDefaultSemanticPayloadVersion() {
        if (DEFAULT_SEMANTIC_PAYLOAD_VERSION == null) {
            DEFAULT_SEMANTIC_PAYLOAD_VERSION = getCdsSystemDefaultProperty("DEFAULT_SEMANTIC_PAYLOAD_VERSION");
        }
        return DEFAULT_SEMANTIC_PAYLOAD_VERSION;
    }

    /**
     * Gets the default general purpose code from the system properties file.
     *
     * @return the default general purpose code
     */
    public static String getGeneralPurposeCode() {
        if (GENERAL_PURPOSE_CODE == null) {
            GENERAL_PURPOSE_CODE = getCdsSystemDefaultProperty("GENERAL_PURPOSE_CODE");
        }
        return GENERAL_PURPOSE_CODE;
    }

    private static String getCdsSystemDefaultProperty(String propertyName) {
        final String METHODNAME = "getCdsSystemDefaultProperty ";
        String result = null;
        if (propertyName == null) {
            throw new IllegalArgumentException(METHODNAME + "propertyName is null!");
        }
        InputStream resource = Config.class.getClassLoader().getResourceAsStream(CDS_SYSTEM_DEFAULTS_FILE_LOCATION);
        Properties cdsSystemDefaultProperties = new Properties();
        try {
            cdsSystemDefaultProperties.load(resource);
            result = cdsSystemDefaultProperties.getProperty(propertyName);
        } catch (IOException e) {
            throw new IllegalArgumentException(METHODNAME + e.getMessage() + " - error retrieving " + CDS_SYSTEM_DEFAULTS_FILE_LOCATION);
        } finally {
            try {
                if (resource != null) {
                    resource.close();
                }
            } catch (Exception e) {
                // do nothing
            }
        }

        if (result == null) {
            throw new IllegalStateException(METHODNAME + "result was null!");
        }
        return result;
    }
}