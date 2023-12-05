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

import java.util.List;
import org.cdsframework.base.BaseCdsObject;
import org.cdsframework.cds.util.CdsObjectFactory;
import org.opencds.vmr.v1_0.schema.AdministrableSubstance;
import org.opencds.vmr.v1_0.schema.CDSContext;
import org.opencds.vmr.v1_0.schema.CDSInput;
import org.opencds.vmr.v1_0.schema.EncounterEvent;
import org.opencds.vmr.v1_0.schema.EvaluatedPerson;
import org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.SubstanceAdministrationProposals;
import org.opencds.vmr.v1_0.schema.II;
import org.opencds.vmr.v1_0.schema.ObservationOrder;
import org.opencds.vmr.v1_0.schema.ObservationResult;
import org.opencds.vmr.v1_0.schema.Problem;
import org.opencds.vmr.v1_0.schema.ProcedureEvent;
import org.opencds.vmr.v1_0.schema.RelatedClinicalStatement;
import org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent;
import org.opencds.vmr.v1_0.schema.SubstanceAdministrationProposal;
import org.opencds.vmr.v1_0.schema.VMR;

/**
 * A class for wrapping the JAXB generated CdsInput class. Eases the construction of CdsInput objects.
 *
 * For Example:
 *
 * <pre>
 *     CdsInputWrapper input = CdsInputWrapper.getCdsInputWrapper();
 *     input.setPatientGender("F");
 *     input.setPatientBirthTime("19830630");
 *     input.addSubstanceAdministrationEvent("45", "20080223", "12345");
 *     input.addSubstanceAdministrationEvent("08", "20080223", "12346");
 *     input.addImmunityObservationResult(new Date(), "070.30", "DISEASE_DOCUMENTED", "IS_IMMUNE");
 * </pre>
 *
 * @see BaseCdsObject
 * @see ObservationResult
 * @see SubstanceAdministrationEvent
 * @see CdsInput
 * @author HLN Consulting, LLC
 */
public class CdsInputWrapper extends BaseCdsObject<CDSInput> {

    /**
     * Construct the instance with no args.
     */
    public CdsInputWrapper() {
        super(CdsInputWrapper.class, CDSInput.class);
        logger.debug("no arg constructor called");
    }

    /**
     * Construct the instance with a CdsInput instance.
     *
     * @see CdsInput
     * @param cdsInput the CdsInput instance
     */
    public CdsInputWrapper(CDSInput cdsInput) {
        super(CdsInputWrapper.class, CDSInput.class, cdsInput);
        logger.debug("CdsInput arg constructor called: " + cdsInput);
    }

    /**
     * Factory-ish method for generating a CdsInputWrapper instance.
     *
     * @return initialized instance of CdsInputWrapper
     */
    public static CdsInputWrapper getCdsInputWrapper() {
        return new CdsInputWrapper();
    }

    /**
     * Factory-ish method for generating a CdsInputWrapper instance.
     *
     * @see CdsInput
     * @param cdsInput the CdsInput instance
     * @return initialized instance of CdsInputWrapper
     */
    public static CdsInputWrapper getCdsInputWrapper(CDSInput cdsInput) {
        if (cdsInput == null) {
            return new CdsInputWrapper();
        } else {
            return new CdsInputWrapper(cdsInput);
        }
    }

    public CDSContext getInstanceCdsContext() {
        CDSContext cdsContext = getCdsObject().getCdsContext();
        if (cdsContext == null) {
            cdsContext = new CDSContext();
            getCdsObject().setCdsContext(cdsContext);
        }
        return cdsContext;
    }

    public EvaluatedPerson.Demographics getInstanceDemographics() {
        EvaluatedPerson patient = getInstancePatient();
        EvaluatedPerson.Demographics demographics = patient.getDemographics();
        if (demographics == null) {
            demographics = new EvaluatedPerson.Demographics();
            patient.setDemographics(demographics);
        }
        return demographics;
    }

    /**
     * Sets an ObservationResult clinical statement on a vMR.
     *
     * @param observationResult
     */
    public void setObservationResultClinicalStatement(ObservationResult observationResult) {
        getInstanceObservationResults().getObservationResult().add(observationResult);
    }

    public EvaluatedPerson.ClinicalStatements.ObservationResults getInstanceObservationResults() {
        EvaluatedPerson.ClinicalStatements clinicalStatements = getInstanceClinicalStatements();
        EvaluatedPerson.ClinicalStatements.ObservationResults observationResults = clinicalStatements.getObservationResults();
        if (observationResults == null) {
            observationResults = new EvaluatedPerson.ClinicalStatements.ObservationResults();
            clinicalStatements.setObservationResults(observationResults);
        }
        return observationResults;
    }

    public EvaluatedPerson.ClinicalStatements getInstanceClinicalStatements() {
        EvaluatedPerson patient = getInstancePatient();
        EvaluatedPerson.ClinicalStatements clinicalStatements = patient.getClinicalStatements();
        if (clinicalStatements == null) {
            clinicalStatements = new EvaluatedPerson.ClinicalStatements();
            patient.setClinicalStatements(clinicalStatements);
        }
        return clinicalStatements;
    }

    public EvaluatedPerson.ClinicalStatements.ObservationOrders getInstanceObservationOrders() {
        EvaluatedPerson.ClinicalStatements clinicalStatements = getInstanceClinicalStatements();
        EvaluatedPerson.ClinicalStatements.ObservationOrders observationOrders = clinicalStatements.getObservationOrders();
        if (observationOrders == null) {
            observationOrders = new EvaluatedPerson.ClinicalStatements.ObservationOrders();
            clinicalStatements.setObservationOrders(observationOrders);
        }
        return observationOrders;
    }

    public EvaluatedPerson.ClinicalStatements.EncounterEvents getInstanceEncounterEvents() {
        EvaluatedPerson.ClinicalStatements clinicalStatements = getInstanceClinicalStatements();
        EvaluatedPerson.ClinicalStatements.EncounterEvents encounterEvents = clinicalStatements.getEncounterEvents();
        if (encounterEvents == null) {
            encounterEvents = new EvaluatedPerson.ClinicalStatements.EncounterEvents();
            clinicalStatements.setEncounterEvents(encounterEvents);
        }
        return encounterEvents;
    }

    public EvaluatedPerson.ClinicalStatements.Problems getInstanceProblems() {
        EvaluatedPerson.ClinicalStatements clinicalStatements = getInstanceClinicalStatements();
        EvaluatedPerson.ClinicalStatements.Problems problems = clinicalStatements.getProblems();
        if (problems == null) {
            problems = new EvaluatedPerson.ClinicalStatements.Problems();
            clinicalStatements.setProblems(problems);
        }
        return problems;
    }

    public VMR getInstanceVmr() {
        VMR vmr = getCdsObject().getVmrInput();
        if (vmr == null) {
            vmr = new VMR();
            getCdsObject().setVmrInput(vmr);
        }

        return vmr;
    }

    public EvaluatedPerson getInstancePatient() {
        VMR vmr = getInstanceVmr();
        EvaluatedPerson patient = vmr.getPatient();
        if (patient == null) {
            patient = new EvaluatedPerson();
            vmr.setPatient(patient);
        }
        return patient;
    }

    /**
     * Sets a Problem clinical statement on a vMR.
     *
     * @param problem
     */
    public void setProblemClinicalStatement(Problem problem) {
        getInstanceProblems().getProblem().add(problem);
    }

    /**
     * Sets an EncounterEvent clinical statement on a vMR.
     *
     * @param encounterEvent
     */
    public void setEncounterEventClinicalStatement(EncounterEvent encounterEvent) {
        getInstanceEncounterEvents().getEncounterEvent().add(encounterEvent);
    }

    /**
     * Sets an ObservationOrder clinical statement on a vMR.
     *
     * @param observationOrder
     */
    public void setObservationOrderClinicalStatement(ObservationOrder observationOrder) {
        getInstanceObservationOrders().getObservationOrder().add(observationOrder);
    }

    /**
     * Returns a base ObservationResult.
     *
     * @param templateId
     * @param idRoot
     * @param idExtension
     * @return
     */
    public static ObservationResult getObservationResult(
            String templateId,
            String idRoot, String idExtension) {
        ObservationResult observationResult = new ObservationResult();
        observationResult.getTemplateId().add(CdsObjectFactory.getII(templateId));
        observationResult.setId(CdsObjectFactory.getII(idRoot, idExtension));
        ObservationResult.ObservationValue observationValue = new ObservationResult.ObservationValue();
        observationResult.setObservationValue(observationValue);
        return observationResult;
    }

    /**
     * Returns an ObservationResult with a focus, event time and interpretation.
     *
     * @param templateId
     * @param idRoot
     * @param idExtension
     * @param focusCode
     * @param focusOid
     * @param focusDisplayName
     * @param focusCodeSystemName
     * @param eventTimeLow
     * @param eventTimeHigh
     * @param interpretationCode
     * @param interpretationOid
     * @param interpretationDisplayName
     * @param interpretationCodeSystemName
     * @return
     */
    public static ObservationResult getObservationResult(
            String templateId,
            String idRoot,
            String idExtension,
            String focusCode,
            String focusOid,
            String focusDisplayName,
            String focusCodeSystemName,
            String eventTimeLow,
            String eventTimeHigh,
            String interpretationCode,
            String interpretationOid,
            String interpretationDisplayName,
            String interpretationCodeSystemName) {
        ObservationResult observationResult = getObservationResult(templateId, idRoot, idExtension);
        setObservationFocusOnObservationResult(observationResult, focusCode, focusOid, focusDisplayName, focusCodeSystemName);
        setObservationEventTimeOnObservationResult(observationResult, eventTimeLow, eventTimeHigh);
        setInterpretationOnObservationResult(observationResult, interpretationCode, interpretationOid, interpretationDisplayName, interpretationCodeSystemName);
        return observationResult;
    }

    /**
     * Returns a base EncounterEvent.
     *
     * @param templateId
     * @param idRoot
     * @param idExtension
     * @return
     */
    public static EncounterEvent getEncounterEvent(String templateId, String idRoot, String idExtension) {
        EncounterEvent encounterEvent = new EncounterEvent();
        encounterEvent.getTemplateId().add(CdsObjectFactory.getII(templateId));
        encounterEvent.setId(CdsObjectFactory.getII(idRoot, idExtension));
        return encounterEvent;
    }

    /**
     * Sets the encounter type on an EncounterEvent.
     *
     * @param encounterEvent
     * @param code
     * @param oid
     * @param displayName
     * @param codeSystemName
     */
    public static void setEncounterTypeOnEncounterEvent(EncounterEvent encounterEvent, String code, String oid, String displayName, String codeSystemName) {
        final String METHODNAME = "setEncounterTypeOnEncounterEvent ";
        if (encounterEvent != null) {
            if (code != null && oid != null) {
                encounterEvent.setEncounterType(CdsObjectFactory.getCD(code, oid, displayName, codeSystemName));
            } else {
                logger.error(METHODNAME, "code and/or oid is null: ", code, " - ", oid);
            }
        } else {
            logger.error(METHODNAME, "encounterEvent is null!");
        }
    }

    /**
     * Sets the encounter event time on an EncounterEvent.
     *
     * @param encounterEvent
     * @param low
     * @param high
     */
    public static void setEncounterEventTimeOnEncounterEvent(EncounterEvent encounterEvent, String low, String high) {
        final String METHODNAME = "setEncounterEventTimeOnEncounterEvent ";
        if (encounterEvent != null) {
            if (low != null && high != null) {
                encounterEvent.setEncounterEventTime(CdsObjectFactory.getIVLTS(low, high));
            } else {
                logger.error(METHODNAME, "eventTimeLow and/or eventTimeHigh is null: ", low, " - ", high);
            }
        } else {
            logger.error(METHODNAME, "encounterEvent is null!");
        }
    }

    /**
     * Retrieves a Problem class instance from an EncounterEvent. If it doesn't exist, it is created.
     *
     * @param encounterEvent
     * @param templateId
     * @param idRoot
     * @param idExtension
     * @param relationshipCode
     * @param relationshipOid
     * @param relationshipDisplayName
     * @param relationshipCodeSystemName
     * @return
     */
    public static Problem getProblemFromEncounterEvent(
            EncounterEvent encounterEvent,
            String templateId,
            String idRoot,
            String idExtension,
            String relationshipCode,
            String relationshipOid,
            String relationshipDisplayName,
            String relationshipCodeSystemName) {
        final String METHODNAME = "getProblemFromEncounterEvent ";
        Problem result = null;
        if (encounterEvent != null) {
            if (templateId != null && idRoot != null && idExtension != null) {
                if (relationshipCode != null && relationshipOid != null) {
                    List<RelatedClinicalStatement> relatedClinicalStatements = encounterEvent.getRelatedClinicalStatement();
                    for (RelatedClinicalStatement relatedClinicalStatement : relatedClinicalStatements) {
                        if (relatedClinicalStatement != null) {
                            Problem problem = relatedClinicalStatement.getProblem();
                            if (problem != null) {
                                II tId = problem.getTemplateId().get(0);
                                if (tId != null) {
                                    II id = problem.getId();
                                    if (id != null) {
                                        if (templateId.equals(tId.getRoot()) && idRoot.equals(id.getRoot()) && idExtension.equals(id.getExtension())) {
                                            result = problem;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (result == null) {
                        RelatedClinicalStatement relatedClinicalStatement = new RelatedClinicalStatement();
                        relatedClinicalStatement.setTargetRelationshipToSource(CdsObjectFactory.getCD(relationshipCode, relationshipOid, relationshipDisplayName, relationshipCodeSystemName));
                        relatedClinicalStatements.add(relatedClinicalStatement);
                        result = new Problem();
                        relatedClinicalStatement.setProblem(result);
                        result.getTemplateId().add(CdsObjectFactory.getII(templateId));
                        result.setId(CdsObjectFactory.getII(idRoot, idExtension));
                    }
                } else {
                    logger.error(METHODNAME, "relationshipCode and/or relationshipOid is null: ", relationshipCode, " - ", relationshipOid);
                }
            } else {
                logger.error(METHODNAME, "templateId and/or idRoot and/or idExtension is null: ", templateId, " - ", idRoot, " - ", idExtension);
            }
        } else {
            logger.error(METHODNAME, "encounterEvent is null!");
        }
        return result;
    }

    /**
     * Retrieves a ProcedureEvent class from an EncounterEvent. If it doesn't exist it is created.
     *
     * @param encounterEvent
     * @param templateId
     * @param idRoot
     * @param idExtension
     * @param relationshipCode
     * @param relationshipOid
     * @param relationshipDisplayName
     * @param relationshipCodeSystemName
     * @return
     */
    public static ProcedureEvent getProcedureEventFromEncounterEvent(
            EncounterEvent encounterEvent,
            String templateId,
            String idRoot,
            String idExtension,
            String relationshipCode,
            String relationshipOid,
            String relationshipDisplayName,
            String relationshipCodeSystemName) {
        final String METHODNAME = "getProcedureEventFromEncounterEvent ";
        ProcedureEvent result = null;
        if (encounterEvent != null) {
            if (templateId != null && idRoot != null && idExtension != null) {
                if (relationshipCode != null && relationshipOid != null) {
                    List<RelatedClinicalStatement> relatedClinicalStatements = encounterEvent.getRelatedClinicalStatement();
                    for (RelatedClinicalStatement relatedClinicalStatement : relatedClinicalStatements) {
                        if (relatedClinicalStatement != null) {
                            ProcedureEvent procedureEvent = relatedClinicalStatement.getProcedureEvent();
                            if (procedureEvent != null) {
                                II tId = procedureEvent.getTemplateId().get(0);
                                if (tId != null) {
                                    II id = procedureEvent.getId();
                                    if (id != null) {
                                        if (templateId.equals(tId.getRoot()) && idRoot.equals(id.getRoot()) && idExtension.equals(id.getExtension())) {
                                            result = procedureEvent;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (result == null) {
                        RelatedClinicalStatement relatedClinicalStatement = new RelatedClinicalStatement();
                        relatedClinicalStatement.setTargetRelationshipToSource(CdsObjectFactory.getCD(relationshipCode, relationshipOid, relationshipDisplayName, relationshipCodeSystemName));
                        encounterEvent.getRelatedClinicalStatement().add(relatedClinicalStatement);
                        result = new ProcedureEvent();
                        relatedClinicalStatement.setProcedureEvent(result);
                        result.getTemplateId().add(CdsObjectFactory.getII(templateId));
                        result.setId(CdsObjectFactory.getII(idRoot, idExtension));
                    }
                } else {
                    logger.error(METHODNAME, "relationshipCode and/or relationshipOid is null: ", relationshipCode, " - ", relationshipOid);
                }
            } else {
                logger.error(METHODNAME, "templateId and/or idRoot and/or idExtension is null: ", templateId, " - ", idRoot, " - ", idExtension);
            }
        } else {
            logger.error(METHODNAME, "encounterEvent is null!");
        }
        return result;
    }

    /**
     * Sets the procedure code on a ProcedureEvent.
     *
     * @param procedureEvent
     * @param code
     * @param oid
     * @param displayName
     * @param codeSystemName
     */
    public static void setProcedureCodeOnProcedureEvent(ProcedureEvent procedureEvent, String code, String oid, String displayName, String codeSystemName) {
        final String METHODNAME = "setProcedureCodeOnProblem ";
        if (procedureEvent != null) {
            if (code != null && oid != null) {
                procedureEvent.setProcedureCode(CdsObjectFactory.getCD(code, oid, displayName, codeSystemName));
            } else {
                logger.error(METHODNAME, "code and/or oid is null: ", code, " - ", oid);
            }
        } else {
            logger.error(METHODNAME, "procedureEvent is null!");
        }
    }

    /**
     * Sets the encounter event time on an EncounterEvent.
     *
     * @param procedureEvent
     * @param low
     * @param high
     */
    public static void setProcedureTimeOnProcedureEvent(ProcedureEvent procedureEvent, String low, String high) {
        final String METHODNAME = "setProcedureTimeOnProcedureEvent ";
        if (procedureEvent != null) {
            if (low != null && high != null) {
                procedureEvent.setProcedureTime(CdsObjectFactory.getIVLTS(low, high));
            } else {
                logger.error(METHODNAME, "eventTimeLow and/or eventTimeHigh is null: ", low, " - ", high);
            }
        } else {
            logger.error(METHODNAME, "procedureEvent is null!");
        }
    }

    /**
     * Returns a base ObservationOrder.
     *
     * @param templateId
     * @param idRoot
     * @param idExtension
     * @return
     */
    public static ObservationOrder getObservationOrder(
            String templateId,
            String idRoot, String idExtension) {
        ObservationOrder observationOrder = new ObservationOrder();
        observationOrder.getTemplateId().add(CdsObjectFactory.getII(templateId));
        observationOrder.setId(CdsObjectFactory.getII(idRoot, idExtension));
        return observationOrder;
    }

    /**
     * Sets an observation focus code on an ObservationOrder.
     *
     * @param observationOrder
     * @param code
     * @param oid
     * @param displayName
     * @param codeSystemName
     */
    public static void setObservationFocusOnObservationOrder(ObservationOrder observationOrder, String code, String oid, String displayName, String codeSystemName) {
        final String METHODNAME = "setObservationFocusOnObservationOrder ";
        if (observationOrder != null) {
            if (code != null && oid != null) {
                observationOrder.setObservationFocus(CdsObjectFactory.getCD(code, oid, displayName, codeSystemName));
            } else {
                logger.error(METHODNAME, "code and/or oid is null: ", code, " - ", oid);
            }
        } else {
            logger.error(METHODNAME, "observationOrder is null!");
        }
    }

    /**
     * Sets the order event time on an ObservationOrder.
     *
     * @param observationOrder
     * @param low
     * @param high
     */
    public static void setOrderEventTimeOnObservationOrder(ObservationOrder observationOrder, String low, String high) {
        final String METHODNAME = "setOrderEventTimeOnObservationOrder ";
        if (observationOrder != null) {
            if (low != null && high != null) {
                observationOrder.setOrderEventTime(CdsObjectFactory.getIVLTS(low, high));
            } else {
                logger.error(METHODNAME, "low and/or high is null: ", low, " - ", high);
            }
        } else {
            logger.error(METHODNAME, "observationOrder is null!");
        }
    }

    /**
     * Sets an substance code on an SubstaneAdministrationEvent.
     *
     * @param substanceAdministrationEvent
     * @param code
     * @param oid
     * @param displayName
     * @param codeSystemName
     */
    public static void setSubstanceCodeOnSubstanceAdministrationEvent(SubstanceAdministrationEvent substanceAdministrationEvent, String code, String oid, String displayName, String codeSystemName) {
        final String METHODNAME = "setSubstanceCodeOnSubstanceAdministrationEvent ";
        if (substanceAdministrationEvent != null) {
            if (code != null && oid != null) {
                AdministrableSubstance administrableSubstance = new AdministrableSubstance();
                administrableSubstance.setSubstanceCode(CdsObjectFactory.getCD(code, oid, displayName, codeSystemName));
                substanceAdministrationEvent.setSubstance(administrableSubstance);
            } else {
                logger.error(METHODNAME, "code and/or oid is null: ", code, " - ", oid);
            }
        } else {
            logger.error(METHODNAME, "substanceAdministrationEvent is null!");
        }
    }

    /**
     * Sets the event time on an SubstaneAdministrationEvent.
     *
     * @param substanceAdministrationEvent
     * @param low
     * @param high
     */
    public static void setAdministrationTimeIntervalOnSubstanceAdministrationEvent(SubstanceAdministrationEvent substanceAdministrationEvent, String low, String high) {
        final String METHODNAME = "setAdministrationTimeIntervalOnSubstanceAdministrationEvent ";
        if (substanceAdministrationEvent != null) {
            if (low != null && high != null) {
                substanceAdministrationEvent.setAdministrationTimeInterval(CdsObjectFactory.getIVLTS(low, high));
            } else {
                logger.error(METHODNAME, "low and/or high is null: ", low, " - ", high);
            }
        } else {
            logger.error(METHODNAME, "substanceAdministrationEvent is null!");
        }
    }

    /**
     * Sets an observation focus on an ObservationResult.
     *
     * @param observationResult
     * @param code
     * @param oid
     * @param displayName
     * @param codeSystemName
     */
    public static void setObservationFocusOnObservationResult(
            ObservationResult observationResult,
            String code,
            String oid,
            String displayName,
            String codeSystemName) {
        final String METHODNAME = "setObservationFocusOnObservationResult ";
        if (observationResult != null) {
            if (code != null && oid != null) {
                observationResult.setObservationFocus(CdsObjectFactory.getCD(code, oid, displayName, codeSystemName));
            } else {
                logger.error(METHODNAME, "code and/or oid is null: ", code, " - ", oid);
            }
        } else {
            logger.error(METHODNAME, "observationResult is null!");
        }
    }

    /**
     * Sets an observation event time on an ObservationResult.
     *
     * @param observationResult
     * @param low
     * @param high
     */
    public static void setObservationEventTimeOnObservationResult(
            ObservationResult observationResult,
            String low,
            String high) {
        final String METHODNAME = "setObservationEventTimeOnObservationResult ";
        if (observationResult != null) {
            if (low != null && high != null) {
                observationResult.setObservationEventTime(CdsObjectFactory.getIVLTS(low, high));
            } else {
                logger.error(METHODNAME, "eventTimeLow and/or eventTimeHigh is null: ", low, " - ", high);
            }
        } else {
            logger.error(METHODNAME, "observationResult is null!");
        }
    }

    /**
     * Sets an interpretation on an ObservationResult.
     *
     * @param observationResult
     * @param code
     * @param oid
     * @param displayName
     * @param codeSystemName
     */
    public static void setInterpretationOnObservationResult(
            ObservationResult observationResult,
            String code,
            String oid,
            String displayName,
            String codeSystemName) {
        final String METHODNAME = "setInterpretationOnObservationResult ";
        if (observationResult != null) {
            if (code != null && oid != null) {
                observationResult.getInterpretation().add(CdsObjectFactory.getCD(code, oid, displayName, codeSystemName));
            } else {
                logger.error(METHODNAME, "code and/or oid is null: ", code, " - ", oid);
            }
        } else {
            logger.error(METHODNAME, "observationResult is null!");
        }
    }

    /**
     * Sets a physical quantity value on an ObservationResult.
     *
     * @param observationResult
     * @param value
     * @param unit
     */
    public static void setPhysicalQuantityValueOnObservationResult(ObservationResult observationResult, String value, String unit) {
        final String METHODNAME = "setPhysicalQuantityValueOnObservationResult ";
        if (observationResult != null) {
            if (value != null && unit != null) {
                ObservationResult.ObservationValue observationValue = observationResult.getObservationValue();
                if (observationValue == null) {
                    observationValue = new ObservationResult.ObservationValue();
                    observationResult.setObservationValue(observationValue);
                }
                observationValue.setPhysicalQuantity(CdsObjectFactory.getPQ(value, unit));
            } else {
                logger.error(METHODNAME, "value and/or unit is null: ", value, " - ", unit);
            }
        } else {
            logger.error(METHODNAME, "observationResult is null!");
        }
    }

    /**
     * Sets a concept value on an ObservationResult.
     *
     * @param observationResult
     * @param code
     * @param oid
     * @param displayName
     * @param codeSystemName
     */
    public static void setConceptValueOnObservationResult(
            ObservationResult observationResult,
            String code,
            String oid,
            String displayName,
            String codeSystemName) {
        final String METHODNAME = "setPhysicalQuantityValueOnObservationResult ";
        if (observationResult != null) {
            if (code != null && oid != null) {
                ObservationResult.ObservationValue observationValue = observationResult.getObservationValue();
                if (observationValue == null) {
                    observationValue = new ObservationResult.ObservationValue();
                    observationResult.setObservationValue(observationValue);
                }
                observationValue.setConcept(CdsObjectFactory.getCD(code, oid, displayName, codeSystemName));
            } else {
                logger.error(METHODNAME, "code and/or oid is null: ", code, " - ", oid);
            }
        } else {
            logger.error(METHODNAME, "observationResult is null!");
        }
    }

    /**
     * Retrieve a Problem object.
     *
     * @param templateId
     * @param idRoot
     * @param idExtension
     * @return
     */
    public static Problem getProblem(String templateId,
            String idRoot,
            String idExtension) {
        final String METHODNAME = "getProblem ";
        Problem result = new Problem();
        result.getTemplateId().add(CdsObjectFactory.getII(templateId));
        result.setId(CdsObjectFactory.getII(idRoot, idExtension));
        return result;
    }

    /**
     * Retrieve a Problem object.
     *
     * @param templateId
     * @param idRoot
     * @param idExtension
     * @return
     */
    public static SubstanceAdministrationEvent getSubstanceAdministrationEvent(String templateId,
            String idRoot,
            String idExtension) {
        final String METHODNAME = "getProblem ";
        SubstanceAdministrationEvent result = new SubstanceAdministrationEvent();
        result.getTemplateId().add(CdsObjectFactory.getII(templateId));
        result.setId(CdsObjectFactory.getII(idRoot, idExtension));
        return result;
    }

    /**
     * Sets the problem code on a Problem.
     *
     * @param problem
     * @param code
     * @param oid
     * @param displayName
     * @param codeSystemName
     */
    public static void setProblemCodeOnProblem(Problem problem, String code, String oid, String displayName, String codeSystemName) {
        final String METHODNAME = "setProblemCodeOnProblem ";
        if (problem != null) {
            if (code != null && oid != null) {
                problem.setProblemCode(CdsObjectFactory.getCD(code, oid, displayName, codeSystemName));
            } else {
                logger.error(METHODNAME, "code and/or oid is null: ", code, " - ", oid);
            }
        } else {
            logger.error(METHODNAME, "problem is null!");
        }
    }

    /**
     * Sets the problem status on a Problem.
     *
     * @param problem
     * @param code
     * @param oid
     * @param displayName
     * @param codeSystemName
     */
    public static void setProblemStatusOnProblem(Problem problem, String code, String oid, String displayName, String codeSystemName) {
        final String METHODNAME = "setProblemStatusOnProblem ";
        if (problem != null) {
            if (code != null && oid != null) {
                problem.setProblemStatus(CdsObjectFactory.getCD(code, oid, displayName, codeSystemName));
            } else {
                logger.error(METHODNAME, "code and/or oid is null: ", code, " - ", oid);
            }
        } else {
            logger.error(METHODNAME, "problem is null!");
        }
    }

    /**
     * Sets the problem effective time on a Problem.
     *
     * @param problem
     * @param low
     * @param high
     */
    public static void setProblemEffectiveTimeOnProblem(Problem problem, String low, String high) {
        final String METHODNAME = "setProblemEffectiveTimeOnProblem ";
        if (problem != null) {
            if (low != null && high != null) {
                problem.setProblemEffectiveTime(CdsObjectFactory.getIVLTS(low, high));
            } else {
                logger.error(METHODNAME, "low and/or high is null: ", low, " - ", high);
            }
        } else {
            logger.error(METHODNAME, "problem is null!");
        }
    }

    /**
     * Sets the problem diagnostic event time on a Problem.
     *
     * @param problem
     * @param low
     * @param high
     */
    public static void setProblemDiagnosticEventTimeOnProblem(Problem problem, String low, String high) {
        final String METHODNAME = "setProblemDiagnosticEventTimeOnProblem ";
        if (problem != null) {
            if (low != null && high != null) {
                problem.setDiagnosticEventTime(CdsObjectFactory.getIVLTS(low, high));
            } else {
                logger.error(METHODNAME, "low and/or high is null: ", low, " - ", high);
            }
        } else {
            logger.error(METHODNAME, "problem is null!");
        }
    }

    public void addSubstanceAdministrationProposal(SubstanceAdministrationProposal substanceAdministrationProposal) {
        SubstanceAdministrationProposals substanceAdministrationProposals = CdsObjectFactory.getSubstanceAdministrationProposals(getInstanceVmr());
        substanceAdministrationProposals.getSubstanceAdministrationProposal().add(substanceAdministrationProposal);
    }

    public void addSubstanceAdministrationEvent(SubstanceAdministrationEvent substanceAdministrationEvent) {
        List<SubstanceAdministrationEvent> substanceAdministrationEvents = getSubstanceAdministrationEvents();
        substanceAdministrationEvents.add(substanceAdministrationEvent);
    }

}
