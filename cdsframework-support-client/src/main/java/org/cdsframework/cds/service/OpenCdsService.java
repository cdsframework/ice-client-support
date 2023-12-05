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
package org.cdsframework.cds.service;

import com.sun.xml.ws.client.BindingProviderProperties;
import com.sun.xml.ws.developer.JAXWSProperties;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import org.cdsframework.cds.vmr.CdsObjectAssist;
import org.cdsframework.exceptions.CdsException;
import org.cdsframework.util.LogUtils;
import org.cdsframework.util.support.cds.Config;
import org.omg.spec.cdss._201105.dss.DataRequirementItemData;
import org.omg.spec.cdss._201105.dss.EntityIdentifier;
import org.omg.spec.cdss._201105.dss.EvaluationRequest;
import org.omg.spec.cdss._201105.dss.EvaluationResponse;
import org.omg.spec.cdss._201105.dss.FinalKMEvaluationResponse;
import org.omg.spec.cdss._201105.dss.InteractionIdentifier;
import org.omg.spec.cdss._201105.dss.ItemIdentifier;
import org.omg.spec.cdss._201105.dss.KMEvaluationRequest;
import org.omg.spec.cdss._201105.dss.KMEvaluationResultData;
import org.omg.spec.cdss._201105.dss.SemanticPayload;
import org.omg.spec.cdss._201105.dsswsdl.DSSRuntimeExceptionFault;
import org.omg.spec.cdss._201105.dsswsdl.DecisionSupportService;
import org.omg.spec.cdss._201105.dsswsdl.Evaluation;
import org.omg.spec.cdss._201105.dsswsdl.EvaluationExceptionFault;
import org.omg.spec.cdss._201105.dsswsdl.InvalidDriDataFormatExceptionFault;
import org.omg.spec.cdss._201105.dsswsdl.InvalidTimeZoneOffsetExceptionFault;
import org.omg.spec.cdss._201105.dsswsdl.RequiredDataNotProvidedExceptionFault;
import org.omg.spec.cdss._201105.dsswsdl.UnrecognizedLanguageExceptionFault;
import org.omg.spec.cdss._201105.dsswsdl.UnrecognizedScopedEntityExceptionFault;
import org.omg.spec.cdss._201105.dsswsdl.UnsupportedLanguageExceptionFault;
import org.opencds.vmr.v1_0.schema.CDSInput;
import org.opencds.vmr.v1_0.schema.CDSOutput;

/**
 * A class for wrapping the JAX-WS generated interface to the OpenCDS WSDL. Eases communication with the OpenCDS service.
 *
 * For Example:
 * <pre>
 *     String endPoint = Config.getCdsDefaultEndpoint();
 *     OpenCdsService service = OpenCdsService.getOpenCdsService(endPoint);
 *
 *     CdsInputWrapper input = CdsInputWrapper.getCdsInputWrapper();
 *     input.setPatientGender("F");
 *     input.setPatientBirthTime("19830630");
 *     input.addSubstanceAdministrationEvent("45", "20080223", null);
 *     input.addSubstanceAdministrationEvent("10", "20080223", "12345");
 *     input.addImmunityObservationResult(new Date(), "070.30", "DISEASE_DOCUMENTED", "IS_IMMUNE");
 *     input.addSubstanceAdministrationEvent("43", "20080223", "12346");
 *     input.addSubstanceAdministrationEvent("08", "20090223", "12347");
 *     input.addSubstanceAdministrationEvent("43", "20080223", "12348");
 *
 *     String scopingEntityId = "org.nyc.cir";
 *     String businessId = "ICE";
 *     String version = "1.0.0";
 *     Date executionDate = new Date();
 *
 *     CdsOutput result = service.evaluate(input.getCdsObject(), scopingEntityId, businessId, version, executionDate);
 * </pre>
 *
 * @see Evaluation
 * @see DecisionSupportService
 * @see org.opencds.vmr.v1_0.schema.vmr.Vmr
 * @see CdsInput
 * @see CdsOutput
 * @author HLN Consulting, LLC
 */
public class OpenCdsService {

    private final static LogUtils logger = LogUtils.getLogger(OpenCdsService.class);
    private final static QName qName = new QName(Config.getCdsNamespaceUri(), Config.getCdsLocalPart());
    private final static DecisionSupportService decisionSupportService = new DecisionSupportService(Config.getCdsWsdlUrl(), qName);
    private Evaluation evaluatePort;
    private String endPoint;
    private int requestTimeout = Config.getCdsDefaultTimeout();
    private int connectTimeout = Config.getCdsDefaultTimeout();

    /**
     * No arg constructor uses default timeouts and default endpoint
     */
    public OpenCdsService() {
        this.endPoint = Config.getCdsDefaultEndpoint();
        initializeService();
    }

    /**
     * Constructor for supplying endpoint.
     *
     * @param endPoint the service endpoint
     */
    public OpenCdsService(String endPoint) {
        this.endPoint = endPoint;
        initializeService();
    }

    /**
     * Constructor for supplying endpoint and timeout.
     *
     * @param endPoint the service endpoint
     * @param timeout the timeout for both requests and connects
     */
    public OpenCdsService(String endPoint, int timeout) {
        this.endPoint = endPoint;
        this.requestTimeout = timeout;
        this.requestTimeout = timeout;
        initializeService();
    }

    /**
     * Constructor for supplying endpoint and request and connect timeouts.
     *
     * @param endPoint the service endpoint
     * @param requestTimeout the request timeout
     * @param connectTimeout the connect timeout
     */
    public OpenCdsService(String endPoint, int requestTimeout, int connectTimeout) {
        this.endPoint = endPoint;
        this.requestTimeout = requestTimeout;
        this.connectTimeout = connectTimeout;
        initializeService();
    }

    private void initializeService() {
        evaluatePort = decisionSupportService.getEvaluate();
        Map<String, Object> ctxt = ((BindingProvider) evaluatePort).getRequestContext();
        ctxt.put(JAXWSProperties.HTTP_CLIENT_STREAMING_CHUNK_SIZE, Config.getHttpClientStreamingChunkSize());
        ctxt.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endPoint);
        ctxt.put(BindingProviderProperties.REQUEST_TIMEOUT, requestTimeout);
        ctxt.put(BindingProviderProperties.CONNECT_TIMEOUT, connectTimeout);
        ctxt.put("com.sun.xml.internal.ws.connect.timeout", connectTimeout);
        ctxt.put("com.sun.xml.internal.ws.request.timeout", requestTimeout);
        ctxt.put("org.jboss.ws.timeout", requestTimeout);
        ctxt.put(JAXWSProperties.CONNECT_TIMEOUT, connectTimeout);
        ctxt.put(JAXWSProperties.REQUEST_TIMEOUT, requestTimeout);
    }

    /**
     * Factory-ish method for getting and instance for a particular endpoint.
     *
     * @param endPoint the service endpoint
     * @return a properly constructed OpenCdsService instance
     */
    public static OpenCdsService getOpenCdsService(String endPoint) {
        return new OpenCdsService(endPoint);
    }

    /**
     * Factory-ish method for getting and instance for a particular endpoint and timeout.
     *
     * @param endPoint the service endpoint
     * @param timeout the timeout for both requests and connects
     * @return a properly constructed OpenCdsService instance
     */
    public static OpenCdsService getOpenCdsService(String endPoint, int timeout) {
        return new OpenCdsService(endPoint, timeout);
    }

    /**
     * Factory-ish method for getting and instance for a particular endpoint and timeout.
     *
     * @param endPoint the service endpoint
     * @param requestTimeout the request timeout
     * @param connectTimeout the connect timeout
     * @return a properly constructed OpenCdsService instance
     */
    public static OpenCdsService getOpenCdsService(String endPoint, int requestTimeout, int connectTimeout) {
        return new OpenCdsService(endPoint, requestTimeout, connectTimeout);
    }

    /**
     * Given the supplied CdsInput object call the OpenCDS service and return the CdsOutput response.
     *
     * @param cdsInput an instance of a CdsInput object
     * @param scopingEntityId the scoping entity ID
     * @param businessId the business ID
     * @param version the version
     * @param executionDate the execution date
     * @return the response in CdsOutput format
     * @throws CdsException
     */
    public CDSOutput evaluate(CDSInput cdsInput, String scopingEntityId, String businessId, String version, Date executionDate)
            throws CdsException {
        byte[] cdsObjectToByteArray = CdsObjectAssist.cdsObjectToByteArray(cdsInput, CDSInput.class);
        byte[] evaluation = evaluate(cdsObjectToByteArray, scopingEntityId, businessId, version, executionDate);
        CDSOutput cdsOutput = CdsObjectAssist.cdsObjectFromByteArray(evaluation, CDSOutput.class);
        return cdsOutput;
    }

    /**
     * Given the supplied CdsInput byte array object call the OpenCDS service and return the CdsOutput byte array response.
     *
     * @param cdsInputByteArray a byte array containing the XML for a CdsInput object
     * @param scopingEntityId the scoping entity ID
     * @param businessId the business ID
     * @param version the version
     * @param executionDate the execution date
     * @return a byte array containing the XML for a CdsOutput object
     * @throws CdsException
     */
    public byte[] evaluate(byte[] cdsInputByteArray, String scopingEntityId, String businessId, String version, Date executionDate)
            throws CdsException {
        return evaluate(
                cdsInputByteArray,
                scopingEntityId,
                businessId,
                version,
                executionDate,
                Config.getDefaultInteractionId(),
                Config.getDefaultClientLanguage(),
                Config.getDefaultClientTimezoneOffset(),
                Config.getDefaultItemIdentifierItemId(),
                businessId + Config.getDefaultEntityItemIdentifierSuffix(),
                Config.getDefaultSemanticPayloadBusinessId(),
                Config.getDefaultSemanticPayloadScopingEntityId(),
                Config.getDefaultSemanticPayloadVersion());
    }

    /**
     * Given the supplied CdsInput byte array object call the OpenCDS service and return the CdsOutput byte array response.
     *
     * @param cdsInputByteArray a byte array containing the XML for a CdsInput object
     * @param scopingEntityId the scoping entity ID
     * @param businessId the business ID
     * @param version the version
     * @param executionDate the execution date
     * @param interactionId the interaction ID
     * @param clientLanguage the client language
     * @param clientTimezoneOffset the client time zone offset
     * @param itemIdentifierId the item identifier ID
     * @param entityItemId the entity item ID
     * @param semanticPayloadBusinessId the semantic payload business ID
     * @param semanticPayloadScopingEntityId the semantic payload scoping entity ID
     * @param semanticPayloadVersion the semantic payload version
     * @return a byte array containing the XML for a CdsOutput object
     * @throws CdsException
     */
    public byte[] evaluate(
            byte[] cdsInputByteArray,
            String scopingEntityId,
            String businessId,
            String version,
            Date executionDate,
            String interactionId,
            String clientLanguage,
            String clientTimezoneOffset,
            String itemIdentifierId,
            String entityItemId,
            String semanticPayloadBusinessId,
            String semanticPayloadScopingEntityId,
            String semanticPayloadVersion) throws CdsException {
        final String METHODNAME = "evaluate ";
        if (logger.isDebugEnabled()) {
            logger.info(METHODNAME
                    + "calling evaluateAtSpecifiedTime with businessId '"
                    + businessId
                    + "; scopingEntityId "
                    + scopingEntityId
                    + "; version "
                    + version
                    + "; executionDate "
                    + executionDate
                    + "' @ "
                    + endPoint
                    + " with requestTimeout:"
                    + requestTimeout
                    + " and connectTimeout:"
                    + connectTimeout);
        }
        long start = System.nanoTime();
        EvaluationResponse response = null;
        byte[] result = null;
        GregorianCalendar gc = new GregorianCalendar();

        if (executionDate == null) {
            throw new CdsException("executionDate is null!");
        }
        gc.setTime(executionDate);

        try {
            XMLGregorianCalendar executionTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);

            InteractionIdentifier interactionIdentifier = getInteractionIdentifier(interactionId, scopingEntityId);
            EvaluationRequest evaluationRequest = getEvaluationRequest(
                    cdsInputByteArray,
                    scopingEntityId,
                    businessId,
                    version,
                    clientLanguage,
                    clientTimezoneOffset,
                    itemIdentifierId,
                    entityItemId,
                    semanticPayloadBusinessId,
                    semanticPayloadScopingEntityId,
                    semanticPayloadVersion);
            logger.logDuration("evaluate init", start);

            start = System.nanoTime();
            response = evaluatePort.evaluateAtSpecifiedTime(interactionIdentifier, executionTime, evaluationRequest);
            logger.logDuration("evaluate execute", start);

            start = System.nanoTime();
            if (response == null) {
                throw new CdsException("response is null!");
            }
            List<FinalKMEvaluationResponse> finalKMEvaluationResponses = response.getFinalKMEvaluationResponses();
            if (finalKMEvaluationResponses == null) {
                throw new CdsException("finalKMEvaluationResponse is null!");
            }
            if (finalKMEvaluationResponses.size() != 1) {
                throw new CdsException("finalKMEvaluationResponse size wrong: " + finalKMEvaluationResponses.size());
            }
            FinalKMEvaluationResponse kmEvaluationResponse = finalKMEvaluationResponses.get(0);
            List<KMEvaluationResultData> kmEvaluationResultData = kmEvaluationResponse.getKmEvaluationResultDatas();
            if (kmEvaluationResultData == null) {
                throw new CdsException("kmEvaluationResultData is null!");
            }
            if (kmEvaluationResultData.size() != 1) {
                throw new CdsException("kmEvaluationResultData size wrong: " + kmEvaluationResultData.size());
            }
            KMEvaluationResultData resultData = kmEvaluationResultData.get(0);
            if (resultData == null) {
                throw new CdsException("resultData is null!");
            }
            SemanticPayload data = resultData.getData();
            if (data == null) {
                throw new CdsException("data is null!");
            }
            List<byte[]> base64EncodedPayload = data.getBase64EncodedPayload();
            if (base64EncodedPayload == null) {
                throw new CdsException("base64EncodedPayload is null!");
            }
            if (base64EncodedPayload.size() != 1) {
                throw new CdsException("base64EncodedPayload size wrong: " + base64EncodedPayload.size());
            }
            result = base64EncodedPayload.get(0);
            if (result == null) {
                throw new CdsException("bytes is null!");
            }

        } catch (DSSRuntimeExceptionFault e) {
            logger.error(e);
            throw new CdsException(e.getMessage());
        } catch (EvaluationExceptionFault e) {
            logger.error(e);
            throw new CdsException(e.getMessage());
        } catch (InvalidDriDataFormatExceptionFault e) {
            logger.error(e);
            throw new CdsException(e.getMessage());
        } catch (InvalidTimeZoneOffsetExceptionFault e) {
            logger.error(e);
            throw new CdsException(e.getMessage());
        } catch (RequiredDataNotProvidedExceptionFault e) {
            logger.error(e);
            throw new CdsException(e.getMessage());
        } catch (UnrecognizedLanguageExceptionFault e) {
            logger.error(e);
            throw new CdsException(e.getMessage());
        } catch (UnrecognizedScopedEntityExceptionFault e) {
            logger.error(e);
            throw new CdsException(e.getMessage());
        } catch (UnsupportedLanguageExceptionFault e) {
            logger.error(e);
            throw new CdsException(e.getMessage());
        } catch (DatatypeConfigurationException e) {
            logger.error(e);
            throw new CdsException(e.getMessage());
        } finally {
            logger.debug(METHODNAME + "end...");
        }
        logger.logDuration("evaluate post process", start);
        return result;
    }

    private EntityIdentifier getIIEntityIdentifier(String scopingEntityId, String version, String entityItemId) {
        EntityIdentifier iiEntityIdentifier = new EntityIdentifier();
        iiEntityIdentifier.setScopingEntityId(scopingEntityId);
        iiEntityIdentifier.setVersion(version);
        iiEntityIdentifier.setBusinessId(entityItemId);
        return iiEntityIdentifier;
    }

    private ItemIdentifier getItemIdentifier(
            String scopingEntityId,
            String version,
            String itemIdentifierId,
            String entityItemId) {
        ItemIdentifier itemIdentifier = new ItemIdentifier();
        itemIdentifier.setItemId(itemIdentifierId);
        itemIdentifier.setContainingEntityId(getIIEntityIdentifier(scopingEntityId, version, entityItemId));
        return itemIdentifier;
    }

    private EntityIdentifier getSPEntityIdentifier(
            String semanticPayloadBusinessId,
            String semanticPayloadScopingEntityId,
            String semanticPayloadVersion) {
        EntityIdentifier spEntityIdentifier = new EntityIdentifier();
        spEntityIdentifier.setBusinessId(semanticPayloadBusinessId);
        spEntityIdentifier.setScopingEntityId(semanticPayloadScopingEntityId);
        spEntityIdentifier.setVersion(semanticPayloadVersion);
        return spEntityIdentifier;
    }

    private SemanticPayload getSemanticPayload(
            byte[] cdsInputByteArray,
            String semanticPayloadBusinessId,
            String semanticPayloadScopingEntityId,
            String semanticPayloadVersion) {
        SemanticPayload semanticPayload = new SemanticPayload();
        EntityIdentifier spEntityIdentifier = getSPEntityIdentifier(
                semanticPayloadBusinessId,
                semanticPayloadScopingEntityId,
                semanticPayloadVersion);
        semanticPayload.setInformationModelSSId(spEntityIdentifier);
        semanticPayload.getBase64EncodedPayload().add(cdsInputByteArray);
        return semanticPayload;
    }

    private DataRequirementItemData getDataRequirementItemData(
            byte[] cdsInputByteArray,
            String scopingEntityId,
            String version,
            String itemIdentifierId,
            String entityItemId,
            String semanticPayloadBusinessId,
            String semanticPayloadScopingEntityId,
            String semanticPayloadVersion) {
        DataRequirementItemData dataRequirementItemData = new DataRequirementItemData();
        dataRequirementItemData.setDriId(getItemIdentifier(scopingEntityId, version, itemIdentifierId, entityItemId));
        SemanticPayload semanticPayload = getSemanticPayload(
                cdsInputByteArray,
                semanticPayloadBusinessId,
                semanticPayloadScopingEntityId,
                semanticPayloadVersion);
        dataRequirementItemData.setData(semanticPayload);
        return dataRequirementItemData;
    }

    private InteractionIdentifier getInteractionIdentifier(String interactionId, String scopingEntityId) throws
            DatatypeConfigurationException {
        InteractionIdentifier interactionIdentifier = new InteractionIdentifier();
        interactionIdentifier.setInteractionId(interactionId);
        interactionIdentifier.setScopingEntityId(scopingEntityId);
        return interactionIdentifier;
    }

    private EntityIdentifier getKMEntityIdentifier(String scopingEntityId, String businessId, String version) {
        EntityIdentifier kmEntityIdentifier = new EntityIdentifier();
        kmEntityIdentifier.setScopingEntityId(scopingEntityId);
        kmEntityIdentifier.setVersion(version);
        kmEntityIdentifier.setBusinessId(businessId);
        return kmEntityIdentifier;
    }

    private KMEvaluationRequest getKMEvaluationRequest(String scopingEntityId, String businessId, String version) {
        KMEvaluationRequest kmEvaluationRequest = new KMEvaluationRequest();
        kmEvaluationRequest.setKmId(getKMEntityIdentifier(scopingEntityId, businessId, version));
        return kmEvaluationRequest;
    }

    private EvaluationRequest getEvaluationRequest(
            byte[] cdsInputByteArray,
            String scopingEntityId,
            String businessId,
            String version,
            String clientLanguage,
            String clientTimezoneOffset,
            String itemIdentifierId,
            String entityItemId,
            String semanticPayloadBusinessId,
            String semanticPayloadScopingEntityId,
            String semanticPayloadVersion) {
        EvaluationRequest evaluationRequest = new EvaluationRequest();
        evaluationRequest.setClientLanguage(clientLanguage);
        evaluationRequest.setClientTimeZoneOffset(clientTimezoneOffset);
        evaluationRequest.getKmEvaluationRequests().add(getKMEvaluationRequest(scopingEntityId, businessId, version));
        DataRequirementItemData dataRequirementItemData = getDataRequirementItemData(
                cdsInputByteArray,
                scopingEntityId,
                version,
                itemIdentifierId,
                entityItemId,
                semanticPayloadBusinessId,
                semanticPayloadScopingEntityId,
                semanticPayloadVersion);
        evaluationRequest.getDataRequirementItemDatas().add(dataRequirementItemData);
        return evaluationRequest;
    }
}
