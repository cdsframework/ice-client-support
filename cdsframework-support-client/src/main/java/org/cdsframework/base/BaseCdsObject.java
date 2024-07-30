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
package org.cdsframework.base;

import java.util.Date;
import java.util.List;
import org.cdsframework.cds.util.CdsObjectFactory;
import org.cdsframework.exceptions.CdsException;
import org.cdsframework.util.LogUtils;
import org.cdsframework.util.support.cds.Config;
import org.opencds.support.util.DateUtils;
import org.opencds.vmr.v1_0.schema.CDSContext;
import org.opencds.vmr.v1_0.schema.CDSInput;
import org.opencds.vmr.v1_0.schema.CDSOutput;
import org.opencds.vmr.v1_0.schema.CD;
import org.opencds.vmr.v1_0.schema.EN;
import org.opencds.vmr.v1_0.schema.ENXP;
import org.opencds.vmr.v1_0.schema.EntityNamePartType;
import org.opencds.vmr.v1_0.schema.II;
import org.opencds.vmr.v1_0.schema.IVLTS;
import org.opencds.vmr.v1_0.schema.TS;
import org.opencds.vmr.v1_0.schema.AdverseEvent;
import org.opencds.vmr.v1_0.schema.EncounterEvent;
import org.opencds.vmr.v1_0.schema.EvaluatedPerson;
import org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements;
import org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.AdverseEvents;
import org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.EncounterEvents;
import org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.Goals;
import org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.ObservationResults;
import org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.Problems;
import org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.ProcedureEvents;
import org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.SubstanceAdministrationEvents;
import org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.SubstanceAdministrationProposals;
import org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.SupplyEvents;
import org.opencds.vmr.v1_0.schema.EvaluatedPerson.Demographics;
import org.opencds.vmr.v1_0.schema.Goal;
import org.opencds.vmr.v1_0.schema.ObservationResult;
import org.opencds.vmr.v1_0.schema.ObservationResult.ObservationValue;
import org.opencds.vmr.v1_0.schema.Problem;
import org.opencds.vmr.v1_0.schema.ProcedureEvent;
import org.opencds.vmr.v1_0.schema.RelatedClinicalStatement;
import org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent;
import org.opencds.vmr.v1_0.schema.SubstanceAdministrationProposal;
import org.opencds.vmr.v1_0.schema.SupplyEvent;
import org.opencds.vmr.v1_0.schema.VMR;

/**
 * Abstract base wrapper class for CdsInput and CdsOutput. See extending classes for examples.
 *
 * @see org.opencds.vmr.v1_0.schema.cdsinput.CdsInput
 * @see org.opencds.vmr.v1_0.schema.cdsoutput.CdsOutput
 * @param <T>
 * @author HLN Consulting, LLC
 */
public abstract class BaseCdsObject<T> {

    /**
     * static logger.
     */
    protected static LogUtils logger = LogUtils.getLogger(BaseCdsObject.class);
    private Class<T> cdsObjectClass;
    private T cdsObject;

    /**
     * Abstract constructor for extending class constructors.
     *
     * @param loggerClass
     * @param cdsObjectClass
     */
    protected BaseCdsObject(Class loggerClass, Class<T> cdsObjectClass) {
        logger = LogUtils.getLogger(loggerClass);
        this.cdsObjectClass = cdsObjectClass;
        if (cdsObjectClass == CDSInput.class) {
            cdsObject = (T) getCdsInput();
            logger.debug("Set cdsObject instance via getCdsInput: ", cdsObject);
        } else {
            cdsObject = (T) getCdsOutput();
            logger.debug("Set cdsObject instance via getCdsOutput: ", cdsObject);
        }
    }

    /**
     * Abstract constructor for extending class constructors.
     *
     * @param loggerClass
     * @param cdsObjectClass
     * @param newInstance
     */
    protected BaseCdsObject(Class loggerClass, Class<T> cdsObjectClass, T newInstance) {
        logger = LogUtils.getLogger(loggerClass);
        this.cdsObjectClass = cdsObjectClass;
        cdsObject = newInstance;
        logger.debug("Set cdsObject instance via newInstance: ", newInstance);
    }

    /**
     * Gets the underlying CdsInput or CdsOutput class instance.
     *
     * @return the underlying CdsInput or CdsOutput class instance
     */
    public T getCdsObject() {
        return cdsObject;
    }

    /**
     * Gets the generic class argument that was used in the class extending this class.
     *
     * @return the generic class argument that was used in the class extending this class
     */
    public Class<T> getCdsObjectClass() {
        return cdsObjectClass;
    }

    private static CDSInput getCdsInput() {
        CDSInput cdsInput = new CDSInput();
        cdsInput.getTemplateId().add(CdsObjectFactory.getII(Config.getCodeSystemOid("CDS_INPUT_ROOT")));
        cdsInput.setCdsContext(getCDSContext());
        cdsInput.setVmrInput(getVmr());
        return cdsInput;
    }

    private static CDSOutput getCdsOutput() {
        CDSOutput cdsOutput = new CDSOutput();
        cdsOutput.setVmrOutput(getVmr());
        return cdsOutput;
    }

    private static CDSContext getCDSContext() {
        CDSContext cdsContext = new CDSContext();
        cdsContext.setCdsSystemUserPreferredLanguage(
                CdsObjectFactory.getCD(Config.getDefaultLanguageCode(),
                        Config.getDefaultLanguageOid(),
                        Config.getDefaultLanguageDisplayName()));
        return cdsContext;
    }

    private static VMR getVmr() {
        VMR vmr = new VMR();
        vmr.getTemplateId().add(CdsObjectFactory.getII(Config.getCodeSystemOid("VMR_ROOT")));
        vmr.setPatient(getEvaluatedPerson());
        return vmr;
    }

    private static EvaluatedPerson getEvaluatedPerson() {
        EvaluatedPerson evaluatedPerson = new EvaluatedPerson();
        evaluatedPerson.getTemplateId().add(CdsObjectFactory.getII(Config.getCodeSystemOid("EVALUATED_PERSON_ROOT")));
        evaluatedPerson.setId(CdsObjectFactory.getII());
        evaluatedPerson.setDemographics(getDemographics());
        ClinicalStatements clinicalStatements = getClinicalStatements();
        evaluatedPerson.setClinicalStatements(clinicalStatements);
        ObservationResults observationResults = clinicalStatements.getObservationResults();
        if (observationResults == null) {
            clinicalStatements.setObservationResults(getObservationResults());
        }
        return evaluatedPerson;
    }

    private static ClinicalStatements getClinicalStatements() {
        ClinicalStatements clinicalStatements = new ClinicalStatements();
        return clinicalStatements;
    }

    private static Demographics getDemographics() {
        Demographics demographics = new Demographics();
        return demographics;
    }

    private static SubstanceAdministrationEvents getSubstanceAdministrationEvents(VMR vmr) {
        ClinicalStatements clinicalStatements = vmr.getPatient().getClinicalStatements();
        SubstanceAdministrationEvents substanceAdministrationEvents = clinicalStatements.getSubstanceAdministrationEvents();
        if (substanceAdministrationEvents == null) {
            substanceAdministrationEvents = new SubstanceAdministrationEvents();
            clinicalStatements.setSubstanceAdministrationEvents(substanceAdministrationEvents);
        }
        return substanceAdministrationEvents;
    }

    private static SupplyEvents getSupplyEvents(VMR vmr) {
        ClinicalStatements clinicalStatements = vmr.getPatient().getClinicalStatements();
        SupplyEvents supplyEvents = clinicalStatements.getSupplyEvents();
        if (supplyEvents == null) {
            supplyEvents = new SupplyEvents();
            clinicalStatements.setSupplyEvents(supplyEvents);
        }
        return supplyEvents;
    }

    private static ProcedureEvents getProcedureEvents(VMR vmr) {
        ClinicalStatements clinicalStatements = vmr.getPatient().getClinicalStatements();
        ProcedureEvents procedureEvents = clinicalStatements.getProcedureEvents();
        if (procedureEvents == null) {
            procedureEvents = new ProcedureEvents();
            clinicalStatements.setProcedureEvents(procedureEvents);
        }
        return procedureEvents;
    }

    private static AdverseEvents getAdverseEvents(VMR vmr) {
        ClinicalStatements clinicalStatements = vmr.getPatient().getClinicalStatements();
        AdverseEvents adverseEvents = clinicalStatements.getAdverseEvents();
        if (adverseEvents == null) {
            adverseEvents = new AdverseEvents();
            clinicalStatements.setAdverseEvents(adverseEvents);
        }
        return adverseEvents;
    }

    private static Goals getGoals(VMR vmr) {
        ClinicalStatements clinicalStatements = vmr.getPatient().getClinicalStatements();
        Goals goals = clinicalStatements.getGoals();
        if (goals == null) {
            goals = new Goals();
            clinicalStatements.setGoals(goals);
        }
        return goals;
    }

    private static Problems getProblems(VMR vmr) {
        ClinicalStatements clinicalStatements = vmr.getPatient().getClinicalStatements();
        Problems problems = clinicalStatements.getProblems();
        if (problems == null) {
            problems = new Problems();
            clinicalStatements.setProblems(problems);
        }
        return problems;
    }

    private static EncounterEvents getEncounterEvents(VMR vmr) {
        ClinicalStatements clinicalStatements = vmr.getPatient().getClinicalStatements();
        EncounterEvents encounterEvents = clinicalStatements.getEncounterEvents();
        if (encounterEvents == null) {
            encounterEvents = new EncounterEvents();
            clinicalStatements.setEncounterEvents(encounterEvents);
        }
        return encounterEvents;
    }

    private static ObservationResults getObservationResults() {
        ObservationResults observationResults = new ObservationResults();
        return observationResults;
    }

    private static ObservationResults getObservationResults(VMR vmr) {
        ClinicalStatements clinicalStatements = vmr.getPatient().getClinicalStatements();
        ObservationResults observationResults = clinicalStatements.getObservationResults();
        if (observationResults == null) {
            observationResults = getObservationResults();
            clinicalStatements.setObservationResults(observationResults);
        }
        return observationResults;
    }

    /**
     * Sets the patient birth date time.
     *
     * @param vmr
     * @param birthTime
     */
    protected static void setPatientBirthTime(VMR vmr, String birthTime) {
        TS ts = new TS();
        ts.setValue(birthTime);
        vmr.getPatient().getDemographics().setBirthTime(ts);
    }

    /**
     * Sets the patient gender generically.
     *
     * @param vmr
     * @param code
     * @param codeSystem
     */
    protected static void setPatientGender(VMR vmr, String code, String codeSystem) {
        vmr.getPatient().getDemographics().setGender(CdsObjectFactory.getCD(code, codeSystem));
    }

    /**
     * Internal method to add a SubstanceAdministrationEvent.
     *
     * @see Vmr
     * @see SubstanceAdministrationEvent
     * @param vmr
     * @param code
     * @param displayName
     * @param codeSystem
     * @param codeSystemName
     * @param administrationEventLowTime
     * @param administrationEventHighTime
     * @param idRoot
     * @param idExtension
     * @param components a list of the vaccine components represented by SubstanceAdministrationEvent objects
     * @return
     */
    protected static SubstanceAdministrationEvent addSubstanceAdministrationEvent(
            VMR vmr,
            String code,
            String displayName,
            String codeSystem,
            String codeSystemName,
            String administrationEventLowTime,
            String administrationEventHighTime,
            String idRoot,
            String idExtension,
            List<SubstanceAdministrationEvent> components) {

        SubstanceAdministrationEvent substanceAdministrationEvent
                = CdsObjectFactory.getSubstanceAdministrationEvent(code, displayName, codeSystem, codeSystemName, administrationEventLowTime, administrationEventHighTime);
        substanceAdministrationEvent.getId().setRoot(idRoot);
        if (idExtension != null && !idExtension.trim().isEmpty()) {
            substanceAdministrationEvent.getId().setExtension(idExtension);
        }

        List<RelatedClinicalStatement> relatedClinicalStatements = substanceAdministrationEvent.getRelatedClinicalStatement();
        if (components != null) {
            for (SubstanceAdministrationEvent sae : components) {
                RelatedClinicalStatement relatedClinicalStatement = CdsObjectFactory.getRelatedClinicalStatement("PERT");
                relatedClinicalStatement.setSubstanceAdministrationEvent(sae);
                relatedClinicalStatements.add(relatedClinicalStatement);
            }
        }

        SubstanceAdministrationEvents substanceAdministrationEvents = getSubstanceAdministrationEvents(vmr);
        substanceAdministrationEvents.getSubstanceAdministrationEvent().add(substanceAdministrationEvent);
        return substanceAdministrationEvent;
    }

    /**
     * Internal method to add a SupplyEvent.
     *
     * @see Vmr
     * @see v
     * @param vmr
     * @param code
     * @param displayName
     * @param codeSystem
     * @param codeSystemName
     * @param administrationEventLowTime
     * @param administrationEventHighTime
     * @param idRoot
     * @param idExtension
     * @return
     */
    protected static SupplyEvent addSupplyEvent(
            VMR vmr,
            String code,
            String displayName,
            String codeSystem,
            String codeSystemName,
            String administrationEventLowTime,
            String administrationEventHighTime,
            String idRoot,
            String idExtension) {

        SupplyEvent supplyEvent
                = CdsObjectFactory.getSupplyEvent(code, displayName, codeSystem, codeSystemName, administrationEventLowTime, administrationEventHighTime);
        supplyEvent.getId().setRoot(idRoot);
        if (idExtension != null && !idExtension.trim().isEmpty()) {
            supplyEvent.getId().setExtension(idExtension);
        }
        SupplyEvents supplyEvents = getSupplyEvents(vmr);
        supplyEvents.getSupplyEvent().add(supplyEvent);
        return supplyEvent;
    }

    protected static ProcedureEvent addProcedureEvent(
            VMR vmr,
            String code,
            String displayName,
            String codeSystem,
            String codeSystemName,
            String eventDate,
            String extension,
            String root) {
        ProcedureEvent procedureEvent
                = CdsObjectFactory.getProcedureEvent(code, displayName, codeSystem, codeSystemName, eventDate);
        procedureEvent.getId().setRoot(root);
        if (extension != null && !extension.trim().isEmpty()) {
            procedureEvent.getId().setExtension(extension);
        }
        ProcedureEvents procedureEvents = getProcedureEvents(vmr);
        procedureEvents.getProcedureEvent().add(procedureEvent);
        return procedureEvent;
    }

    protected static ProcedureEvent addProcedureEvent(
            VMR vmr,
            String procedureEventCode,
            String procedureEventCodeDisplayName,
            String procedureEventCodeSystem,
            String procedureEventCodeSystemName,
            String procedureEventStatusCode,
            String procedureEventStatusCodeDisplayName,
            String procedureEventStatusCodeSystem,
            String procedureEventStatusCodeSystemName,
            String procedureEventLowTime,
            String procedureEventHighTime,
            String idRoot,
            String idExtension) {
        ProcedureEvent procedureEvent
                = CdsObjectFactory.getProcedureEvent(
                        procedureEventCode,
                        procedureEventCodeDisplayName,
                        procedureEventCodeSystem,
                        procedureEventCodeSystemName,
                        procedureEventStatusCode,
                        procedureEventStatusCodeDisplayName,
                        procedureEventStatusCodeSystem,
                        procedureEventStatusCodeSystemName,
                        procedureEventLowTime,
                        procedureEventHighTime,
                        idRoot,
                        idExtension);
        ProcedureEvents procedureEvents = getProcedureEvents(vmr);
        procedureEvents.getProcedureEvent().add(procedureEvent);
        return procedureEvent;
    }

    protected static AdverseEvent addAdverseEvent(
            VMR vmr,
            String adverseEventCode,
            String adverseEventCodeDisplayName,
            String adverseEventCodeCodeSystem,
            String adverseEventCodeCodeSystemName,
            String adverseEventStatus,
            String adverseEventStatusDisplayName,
            String adverseEventStatusCodeSystem,
            String adverseEventStatusCodeSystemName,
            String adverseEventLowTime,
            String adverseEventHighTime,
            String idRoot,
            String idExtension) {
        AdverseEvent adverseEvent
                = CdsObjectFactory.getAdverseEvent(
                        adverseEventCode,
                        adverseEventCodeDisplayName,
                        adverseEventCodeCodeSystem,
                        adverseEventCodeCodeSystemName,
                        adverseEventStatus,
                        adverseEventStatusDisplayName,
                        adverseEventStatusCodeSystem,
                        adverseEventStatusCodeSystemName,
                        adverseEventLowTime,
                        adverseEventHighTime);
        adverseEvent.getId().setRoot(idRoot);
        if (idExtension != null && !idExtension.trim().isEmpty()) {
            adverseEvent.getId().setExtension(idExtension);
        }
        AdverseEvents adverseEvents = getAdverseEvents(vmr);
        adverseEvents.getAdverseEvent().add(adverseEvent);
        return adverseEvent;
    }

    protected static Goal addGoal(
            VMR vmr,
            String goalFocusCode,
            String goalFocusDisplayName,
            String goalFocusCodeSystem,
            String goalFocusCodeSystemName,
            String goalStatusCode,
            String goalStatusCodeDisplayName,
            String goalStatusCodeCodeSystem,
            String goalStatusCodeCodeSystemName,
            String goalLowTime,
            String goalHighTime,
            String idRoot,
            String idExtension) {

        Goal goal
                = CdsObjectFactory.getGoal(
                        goalFocusCode,
                        goalFocusDisplayName,
                        goalFocusCodeSystem,
                        goalFocusCodeSystemName,
                        goalStatusCode,
                        goalStatusCodeDisplayName,
                        goalStatusCodeCodeSystem,
                        goalStatusCodeCodeSystemName,
                        goalLowTime,
                        goalHighTime);
        goal.getId().setRoot(idRoot);
        if (idExtension != null && !idExtension.trim().isEmpty()) {
            goal.getId().setExtension(idExtension);
        }
        Goals goals = getGoals(vmr);
        goals.getGoal().add(goal);
        return goal;
    }

    protected static Problem addProblem(
            VMR vmr,
            String problemCode,
            String problemCodeDisplayName,
            String problemCodeSystem,
            String problemCodeSystemName,
            String statusCode,
            String statusCodeDisplayName,
            String statusCodeSystem,
            String statusCodeSystemName,
            String problemLowTime,
            String problemHighTime,
            String idRoot,
            String idExtension) {
        Problem problem
                = CdsObjectFactory.getProblem(
                        problemCode,
                        problemCodeDisplayName,
                        problemCodeSystem,
                        problemCodeSystemName,
                        statusCode,
                        statusCodeDisplayName,
                        statusCodeSystem,
                        statusCodeSystemName,
                        problemLowTime,
                        problemHighTime);
        problem.getId().setRoot(idRoot);
        if (idExtension != null && !idExtension.trim().isEmpty()) {
            problem.getId().setExtension(idExtension);
        }
        Problems problems = getProblems(vmr);
        problems.getProblem().add(problem);
        return problem;
    }

    protected static Problem addProblem(
            VMR vmr,
            String focusCode,
            String focusDisplayName,
            String focusCodeSystem,
            String focusCodeSystemName,
            String observationValueCode,
            String observationValueDisplayName,
            String observationValueCodeSystem,
            String observationValueCodeSystemName,
            String eventDate,
            String extension,
            String root) {
        Problem problem
                = CdsObjectFactory.getProblem(
                        focusCode,
                        focusDisplayName,
                        focusCodeSystem,
                        focusCodeSystemName,
                        observationValueCode,
                        observationValueDisplayName,
                        observationValueCodeSystem,
                        observationValueCodeSystemName,
                        eventDate);
        problem.getId().setRoot(root);
        if (extension != null && !extension.trim().isEmpty()) {
            problem.getId().setExtension(extension);
        }
        Problems problems = getProblems(vmr);
        problems.getProblem().add(problem);
        return problem;
    }

    protected static EncounterEvent addEncounterEvent(
            VMR vmr,
            String encounterEventCode,
            String encounterEventCodeDisplayName,
            String encounterEventCodeCodeSystem,
            String encounterEventCodeCodeSystemName,
            String encounterEventLowTime,
            String encounterEventHighTime,
            String idRoot,
            String idExtension) {
        EncounterEvent encounterEvent
                = CdsObjectFactory.getEncounterEvent(
                        encounterEventCode,
                        encounterEventCodeDisplayName,
                        encounterEventCodeCodeSystem,
                        encounterEventCodeCodeSystemName,
                        encounterEventLowTime,
                        encounterEventHighTime);
        encounterEvent.getId().setRoot(idRoot);
        if (idExtension != null && !idExtension.trim().isEmpty()) {
            encounterEvent.getId().setExtension(idExtension);
        }
        EncounterEvents encounterEvents = getEncounterEvents(vmr);
        encounterEvents.getEncounterEvent().add(encounterEvent);
        return encounterEvent;
    }

    /**
     * Internal method to add an ObservationResult.
     *
     * @see ObservationResult
     * @see Vmr
     * @param vmr
     * @param observationResult
     */
    protected static void addObservationResult(VMR vmr, ObservationResult observationResult) {
        ObservationResults observationResults = getObservationResults(vmr);
        observationResults.getObservationResult().add(observationResult);
    }

    /**
     * Gets the internal objects Vmr instance.
     *
     * @see Vmr
     * @return the internal objects Vmr instance
     * @throws CdsException
     */
    protected VMR getCdsObjectVmr() {
        VMR vmr;
        if (cdsObject == null) {
            throw new CdsException("cdsObject was null!");
        }
        if (cdsObject instanceof CDSInput) {
            vmr = ((CDSInput) cdsObject).getVmrInput();
        } else if (cdsObject instanceof CDSOutput) {
            vmr = ((CDSOutput) cdsObject).getVmrOutput();
        } else {
            throw new CdsException("Unexpected cdsObject class: " + cdsObject.getClass().getSimpleName());
        }
        return vmr;
    }

    /**
     * Sets the patient birth date time.
     *
     * @param birthDate the patient birth date
     * @throws CdsException
     */
    public void setPatientBirthTime(Date birthDate) {
        setPatientBirthTime(DateUtils.getISODateFormat(birthDate));
    }

    /**
     * Sets the patient birth date time.
     *
     * @param birthTime the patient birth date
     * @throws CdsException
     */
    public void setPatientBirthTime(String birthTime) {
        setPatientBirthTime(getCdsObjectVmr(), birthTime);
    }

    /**
     * Sets the patient gender.
     *
     * @param code the patient gender code
     * @param codeSystem the gender code system OID
     * @throws CdsException
     */
    public void setPatientGender(String code, String codeSystem) {
        setPatientGender(getCdsObjectVmr(), code, codeSystem);
    }

    /**
     * Sets the patient ID.
     *
     * The value is from the Config.getCodeSystemOid("PATIENT_ID") configured code system OID.
     *
     * @param patientId the patient ID
     */
    public void setPatientId(String patientId) {
        setPatientId(patientId, Config.getCodeSystemOid("PATIENT_ID"));
    }

    /**
     * Sets the patient ID.
     *
     * @param extension the patient ID
     * @param root the OID identifying the source of the extension
     */
    public void setPatientId(String extension, String root) {
        VMR cdsObjectVmr = getCdsObjectVmr();
        if (cdsObjectVmr != null) {
            EvaluatedPerson patient = cdsObjectVmr.getPatient();
            if (patient != null) {
                if (extension != null && !extension.trim().isEmpty()) {
                    if (root != null && !root.trim().isEmpty()) {
                        patient.getId().setRoot(root);
                        patient.getId().setExtension(extension);
                    } else {
                        logger.warn("root was null.");
                    }
                } else {
                    logger.warn("extension was null.");
                }
            } else {
                throw new CdsException("Retrieved patient object was null.");
            }
        } else {
            throw new CdsException("Retrieved VMR object was null.");
        }
    }

    /**
     * Retrieves the list of patient names.
     *
     * @return
     */
    public List<EN> getPatientNames() {
        List<EN> names = null;
        VMR cdsObjectVmr = getCdsObjectVmr();
        if (cdsObjectVmr != null) {
            EvaluatedPerson patient = cdsObjectVmr.getPatient();
            if (patient != null) {
                Demographics demographics = patient.getDemographics();
                if (demographics != null) {
                    names = demographics.getName();
                } else {
                    throw new CdsException("Retrieved demographics object was null.");
                }
            } else {
                throw new CdsException("Retrieved patient object was null.");
            }
        } else {
            throw new CdsException("Retrieved VMR object was null.");
        }
        return names;
    }

    /**
     * Retrieves the patient given (first) name
     *
     * @return
     */
    public String getPatientGivenName() {
        String result = null;
        List<EN> patientNames = getPatientNames();
        if (patientNames.size() > 1) {
            throw new CdsException("There is more than one name for the patient.");
        } else if (!patientNames.isEmpty()) {
            EN name = patientNames.get(0);
            for (ENXP part : name.getPart()) {
                if (part.getType() == EntityNamePartType.GIV) {
                    result = part.getValue();
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Retrieves the patient family (last) name
     *
     * @return
     */
    public String getPatientFamilyName() {
        String result = null;
        List<EN> patientNames = getPatientNames();
        if (patientNames.size() > 1) {
            throw new CdsException("There is more than one name for the patient.");
        } else if (!patientNames.isEmpty()) {
            EN name = patientNames.get(0);
            for (ENXP part : name.getPart()) {
                if (part.getType() == EntityNamePartType.FAM) {
                    result = part.getValue();
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Sets the patient name.
     *
     * @param givenName the patient given name
     * @param familyName the patient family name
     */
    public void setPatientName(String givenName, String familyName) {
        if (givenName != null || familyName != null) {
            List<EN> patientNames = getPatientNames();
            EN name = new EN();
            if (givenName != null && !givenName.trim().isEmpty()) {
                ENXP enxp = new ENXP();
                enxp.setType(EntityNamePartType.GIV);
                enxp.setValue(givenName);
                name.getPart().add(enxp);
            } else {
                logger.warn("root was null.");
            }
            if (familyName != null && !familyName.trim().isEmpty()) {
                ENXP enxp = new ENXP();
                enxp.setType(EntityNamePartType.FAM);
                enxp.setValue(familyName);
                name.getPart().add(enxp);
            } else {
                logger.warn("root was null.");
            }
            patientNames.add(name);

        }
    }

    /**
     * Add an ObservationResult to the output object representing an immunity event.
     *
     * This method is a generic implementation for simplifying the adding of a ObservationResult to the output object.
     *
     * @see ObservationResult
     * @param observationResult the observation result to add
     * @throws CdsException
     */
    public void addObservationResult(ObservationResult observationResult) {

        addObservationResult(getCdsObjectVmr(), observationResult);
    }

    /**
     * Add an ObservationResult to the CDS object representing an immunity event.
     *
     * This method is a generic implementation for simplifying the adding of a ObservationResult to the CDS object.
     *
     * @see org.cdsframework.util.support.cds.Config
     * @param focusCode the focus code
     * @param focusDisplayName the focus display name
     * @param focusCodeSystem the focus code system
     * @param focusCodeSystemName the focus code system name
     * @param valueCode the observation value code
     * @param valueDisplayName the observation value display name
     * @param valueCodeSystem the observation value code system
     * @param valueCodeSystemName the observation value code system name
     * @param extension a unique ID identifying this particular observation result
     * @param root the OID identifying the source of the extension
     */
    public void addObservationResult(
            String focusCode,
            String focusDisplayName,
            String focusCodeSystem,
            String focusCodeSystemName,
            String valueCode,
            String valueDisplayName,
            String valueCodeSystem,
            String valueCodeSystemName,
            String extension,
            String root) {

        ObservationResult observationResult = CdsObjectFactory.getObservationResult(extension, root);

        ObservationValue observationValue = CdsObjectFactory.getObservationValue(
                valueCode,
                valueCodeSystem,
                valueCodeSystemName,
                valueDisplayName);
        observationResult.setObservationValue(observationValue);

        observationResult.setObservationFocus(CdsObjectFactory.getCD(focusCode, focusCodeSystem, focusDisplayName, focusCodeSystemName));

        addObservationResult(getCdsObjectVmr(), observationResult);
    }

    /**
     * Add an ObservationResult to the CDS object representing an immunity event.
     *
     * This method is a generic implementation for simplifying the adding of a ObservationResult to the CDS object.
     *
     * @return
     * @see org.cdsframework.util.support.cds.Config
     * @param focusCode the focus code
     * @param focusDisplayName the focus display name
     * @param focusCodeSystem the focus code system
     * @param focusCodeSystemName the focus code system name
     * @param lowTime the low date time of the observation result
     * @param highTime the high date time of the observation result
     * @param idRoot the OID identifying the source of the extension
     * @param idExtension a unique ID identifying this particular observation result
     */
    public ObservationResult addObservationResult(
            String focusCode,
            String focusDisplayName,
            String focusCodeSystem,
            String focusCodeSystemName,
            String lowTime,
            String highTime,
            String idRoot,
            String idExtension) {

        return addObservationResult(focusCode, focusDisplayName, focusCodeSystem, focusCodeSystemName, null, null, null, null, lowTime, highTime, idRoot, idExtension);
    }

    /**
     * Add an ObservationResult to the CDS object representing an immunity event.
     *
     * This method is a generic implementation for simplifying the adding of a ObservationResult to the CDS object.
     *
     * @return
     * @see org.cdsframework.util.support.cds.Config
     * @param focusCode the focus code
     * @param focusDisplayName the focus display name
     * @param focusCodeSystem the focus code system
     * @param focusCodeSystemName the focus code system name
     * @param valueCode the observation value code
     * @param valueDisplayName the observation value display name
     * @param valueCodeSystem the observation value code system
     * @param valueCodeSystemName the observation value code system name
     * @param lowTime the low date time of the observation result
     * @param highTime the high date time of the observation result
     * @param idRoot the OID identifying the source of the extension
     * @param idExtension a unique ID identifying this particular observation result
     */
    public ObservationResult addObservationResult(
            String focusCode,
            String focusDisplayName,
            String focusCodeSystem,
            String focusCodeSystemName,
            String valueCode,
            String valueDisplayName,
            String valueCodeSystem,
            String valueCodeSystemName,
            String lowTime,
            String highTime,
            String idRoot,
            String idExtension) {

        ObservationResult observationResult = CdsObjectFactory.getObservationResult(idExtension, idRoot);

        if (valueCode != null && valueCodeSystem != null) {
            ObservationValue observationValue = CdsObjectFactory.getObservationValue(
                    valueCode,
                    valueCodeSystem,
                    valueCodeSystemName,
                    valueDisplayName);
            observationResult.setObservationValue(observationValue);
        }

        observationResult.setObservationFocus(CdsObjectFactory.getCD(focusCode, focusCodeSystem, focusDisplayName, focusCodeSystemName));

        IVLTS ivlts = new IVLTS();
        ivlts.setHigh(highTime);
        ivlts.setLow(lowTime);
        observationResult.setObservationEventTime(ivlts);

        addObservationResult(getCdsObjectVmr(), observationResult);

        return observationResult;
    }

    /**
     * Gets the list of SubstanceAdministrationProposals on the CDS object.
     *
     * @see SubstanceAdministrationProposal
     * @return the list of SubstanceAdministrationProposals
     */
    public List<SubstanceAdministrationProposal> getSubstanceAdministrationProposals() {
        VMR vmrOutput = getCdsObjectVmr();
        if (vmrOutput == null) {
            throw new CdsException("vmrOutput is null!");
        }
        SubstanceAdministrationProposals substanceAdministrationProposals = CdsObjectFactory.getSubstanceAdministrationProposals(vmrOutput);
        if (substanceAdministrationProposals == null) {
            throw new CdsException("substanceAdministrationProposals is null!");
        }
        return substanceAdministrationProposals.getSubstanceAdministrationProposal();
    }

    public ProcedureEvent addProcedureEvent(
            String code,
            String displayName,
            String codeSystem,
            String codeSystemName,
            String eventDate,
            String extension,
            String root) {
        return addProcedureEvent(getCdsObjectVmr(), code, displayName, codeSystem, codeSystemName, eventDate, extension, root);
    }

    public ProcedureEvent addProcedureEvent(
            String procedureEventCode,
            String procedureEventCodeDisplayName,
            String procedureEventCodeSystem,
            String procedureEventCodeSystemName,
            String procedureEventStatusCode,
            String procedureEventStatusCodeDisplayName,
            String procedureEventStatusCodeSystem,
            String procedureEventStatusCodeSystemName,
            String procedureEventLowTime,
            String procedureEventHighTime,
            String idRoot,
            String idExtension) {
        return addProcedureEvent(
                getCdsObjectVmr(),
                procedureEventCode,
                procedureEventCodeDisplayName,
                procedureEventCodeSystem,
                procedureEventCodeSystemName,
                procedureEventStatusCode,
                procedureEventStatusCodeDisplayName,
                procedureEventStatusCodeSystem,
                procedureEventStatusCodeSystemName,
                procedureEventLowTime,
                procedureEventHighTime,
                idRoot,
                idExtension);
    }

    public AdverseEvent addAdverseEvent(
            String adverseEventCode,
            String adverseEventCodeDisplayName,
            String adverseEventCodeCodeSystem,
            String adverseEventCodeCodeSystemName,
            String adverseEventStatus,
            String adverseEventStatusDisplayName,
            String adverseEventStatusCodeSystem,
            String adverseEventStatusCodeSystemName,
            String adverseEventLowTime,
            String adverseEventHighTime,
            String idRoot,
            String idExtension) {
        return addAdverseEvent(
                getCdsObjectVmr(),
                adverseEventCode,
                adverseEventCodeDisplayName,
                adverseEventCodeCodeSystem,
                adverseEventCodeCodeSystemName,
                adverseEventStatus,
                adverseEventStatusDisplayName,
                adverseEventStatusCodeSystem,
                adverseEventStatusCodeSystemName,
                adverseEventLowTime,
                adverseEventHighTime,
                idRoot,
                idExtension);
    }

    public Problem addProblem(
            String focusCode,
            String focusDisplayName,
            String focusCodeSystem,
            String focusCodeSystemName,
            String observationValueCode,
            String observationValueDisplayName,
            String observationValueCodeSystem,
            String observationValueCodeSystemName,
            String eventDate,
            String extension,
            String root) {
        return addProblem(
                getCdsObjectVmr(),
                focusCode,
                focusDisplayName,
                focusCodeSystem,
                focusCodeSystemName,
                observationValueCode,
                observationValueDisplayName,
                observationValueCodeSystem,
                observationValueCodeSystemName,
                eventDate,
                extension,
                root);
    }

    public Problem addProblem(
            String problemCode,
            String problemCodeDisplayName,
            String problemCodeSystem,
            String problemCodeSystemName,
            String statusCode,
            String statusCodeDisplayName,
            String statusCodeSystem,
            String statusCodeSystemName,
            String problemLowTime,
            String problemHighTime,
            String idRoot,
            String idExtension) {
        return addProblem(
                getCdsObjectVmr(),
                problemCode,
                problemCodeDisplayName,
                problemCodeSystem,
                problemCodeSystemName,
                statusCode,
                statusCodeDisplayName,
                statusCodeSystem,
                statusCodeSystemName,
                problemLowTime,
                problemHighTime,
                idRoot,
                idExtension);
    }

    public Goal addGoal(
            String goalFocusCode,
            String goalFocusDisplayName,
            String goalFocusCodeSystem,
            String goalFocusCodeSystemName,
            String goalStatusCode,
            String goalStatusCodeDisplayName,
            String goalStatusCodeCodeSystem,
            String goalStatusCodeCodeSystemName,
            String goalLowTime,
            String goalHighTime,
            String idRoot,
            String idExtension) {
        return addGoal(
                getCdsObjectVmr(),
                goalFocusCode,
                goalFocusDisplayName,
                goalFocusCodeSystem,
                goalFocusCodeSystemName,
                goalStatusCode,
                goalStatusCodeDisplayName,
                goalStatusCodeCodeSystem,
                goalStatusCodeCodeSystemName,
                goalLowTime,
                goalHighTime,
                idRoot,
                idExtension);
    }

    public EncounterEvent addEncounterEvent(
            String encounterEventCode,
            String encounterEventCodeDisplayName,
            String encounterEventCodeCodeSystem,
            String encounterEventCodeCodeSystemName,
            String encounterEventLowTime,
            String encounterEventHighTime,
            String idRoot,
            String idExtension) {
        return addEncounterEvent(
                getCdsObjectVmr(),
                encounterEventCode,
                encounterEventCodeDisplayName,
                encounterEventCodeCodeSystem,
                encounterEventCodeCodeSystemName,
                encounterEventLowTime,
                encounterEventHighTime,
                idRoot,
                idExtension);
    }

    /**
     * Add a SubstanceAdministrationEvent to the CDS object.
     *
     * This method is a generic implementation for simplifying the adding of a SubstanceAdministrationEvent to the CDS object.
     *
     * @see SubstanceAdministrationEvent
     * @see org.cdsframework.util.support.cds.Config
     * @param code the substance code
     * @param displayName the substance code display name
     * @param codeSystem the substance code code system
     * @param codeSystemName the substance code code system name
     * @param administrationEventLowTime the low date time of the substance administration
     * @param administrationEventHighTime the high date time of the substance administration
     * @param idRoot a unique ID identifying this particular administration event
     * @param idExtension the OID identifying the source of the extension
     * @param components
     * @return a properly constructed SubstanceAdministrationEvent with an ObservationResult on it
     */
    public SubstanceAdministrationEvent addSubstanceAdministrationEvent(
            String code,
            String displayName,
            String codeSystem,
            String codeSystemName,
            String administrationEventLowTime,
            String administrationEventHighTime,
            String idRoot,
            String idExtension,
            List<SubstanceAdministrationEvent> components) {
        return addSubstanceAdministrationEvent(
                getCdsObjectVmr(),
                code, displayName,
                codeSystem, codeSystemName,
                administrationEventLowTime, administrationEventHighTime,
                idRoot, idExtension,
                components);
    }

    /**
     * Add a SubstanceAdministrationEvent to the CDS object.
     *
     * This method is a generic implementation for simplifying the adding of a SubstanceAdministrationEvent to the CDS object.
     *
     * @see SubstanceAdministrationEvent
     * @see org.cdsframework.util.support.cds.Config
     * @param administrationEventLowTime the low date time of the substance administration
     * @param administrationEventHighTime the high date time of the substance administration
     * @param idRoot the OID identifying the source of the idExtension
     * @param idExtension a unique ID identifying this particular administration event
     * @return a properly constructed SubstanceAdministrationEvent with an ObservationResult on it
     */
    public SubstanceAdministrationEvent addSubstanceAdministrationEvent(
            String administrationEventLowTime,
            String administrationEventHighTime,
            String idRoot,
            String idExtension) {
        return addSubstanceAdministrationEvent(
                getCdsObjectVmr(),
                null,
                null,
                null,
                null,
                administrationEventLowTime,
                administrationEventHighTime,
                idRoot,
                idExtension,
                null);
    }

    /**
     * Add a SubstanceAdministrationProposal to the CDS object.
     *
     * This method is an ICE specific implementation for simplifying the adding of a SubstanceAdministrationProposal to the CDS
     * object.
     *
     * The vaccineGroup is from the Config.getCodeSystemOid("VACCINE_GROUP") configured code system OID.
     *
     * The substanceCode is from the Config.getCodeSystemOid("VACCINE") configured code system OID.
     *
     * The value code is from the Config.getCodeSystemOid("RECOMMENDATION_VALUE") configured code system OID.
     *
     * The focus code is from the Config.getCodeSystemOid("VACCINE_GROUP") configured code system OID.
     *
     * The interpretation codes (reasons) are from the Config.getCodeSystemOid("RECOMMENDATION_INTERPRETATION") configured code
     * system OID.
     *
     * @see SubstanceAdministrationProposal
     * @see org.cdsframework.util.support.cds.Config
     * @param vaccineGroup the vaccine group code
     * @param vaccineGroupOid the vaccine group oid
     * @param substanceCode the substance code
     * @param substanceOid the substance oid
     * @param administrationTimeInterval the date time of the substance administration
     * @param focusCode the focus of the administration event
     * @param focusOid the focus oid of the administration event
     * @param valueCode the recommendation value
     * @param valueOid the recommendation value oid
     * @param interpretations the interpretations of the administration event
     * @return a properly constructed SubstanceAdministrationProposal
     * @throws CdsException
     */
    public SubstanceAdministrationProposal addSubstanceAdministrationProposal(
            String vaccineGroup,
            String vaccineGroupOid,
            String substanceCode,
            String substanceOid,
            String administrationTimeInterval,
            String focusCode,
            String focusOid,
            String valueCode,
            String valueOid,
            List<CD> interpretations) throws CdsException {
        SubstanceAdministrationProposal substanceAdministrationProposal = CdsObjectFactory.addSubstanceAdministrationProposal(
                getCdsObjectVmr(),
                vaccineGroup, vaccineGroupOid,
                substanceCode, substanceOid,
                administrationTimeInterval);
        substanceAdministrationProposal = CdsObjectFactory.addObservationResult(
                substanceAdministrationProposal,
                focusCode, focusOid,
                valueCode, valueOid,
                interpretations);
        return substanceAdministrationProposal;
    }

    /**
     * Add a SubstanceAdministrationProposal to the CDS object.
     *
     * This method is an ICE specific implementation for simplifying the adding of a SubstanceAdministrationProposal to the CDS
     * object.
     *
     * The vaccineGroup is from the Config.getCodeSystemOid("VACCINE_GROUP") configured code system OID.
     *
     * The substanceCode is from the Config.getCodeSystemOid("VACCINE") configured code system OID.
     *
     * The value code is from the Config.getCodeSystemOid("RECOMMENDATION_VALUE") configured code system OID.
     *
     * The focus code is from the Config.getCodeSystemOid("VACCINE_GROUP") configured code system OID.
     *
     * The interpretation codes (reasons) are from the Config.getCodeSystemOid("RECOMMENDATION_INTERPRETATION") configured code
     * system OID.
     *
     * @see SubstanceAdministrationProposal
     * @see org.cdsframework.util.support.cds.Config
     * @param vaccineGroup the vaccine group code
     * @param vaccineGroupOid the vaccine group oid
     * @param substanceCode the substance code
     * @param substanceOid the substance oid
     * @param proposedAdministrationTimeIntervalLow
     * @param proposedAdministrationTimeIntervalHigh
     * @param validAdministrationTimeIntervalLow
     * @param validAdministrationTimeIntervalHigh
     * @param focusCode the focus of the administration event
     * @param focusOid the focus oid of the administration event
     * @param valueCode the recommendation value
     * @param valueOid the recommendation value oid
     * @param interpretations the interpretations of the administration event
     * @return a properly constructed SubstanceAdministrationProposal
     * @throws CdsException
     */
    public SubstanceAdministrationProposal addSubstanceAdministrationProposal(
            String vaccineGroup,
            String vaccineGroupOid,
            String substanceCode,
            String substanceOid,
            String proposedAdministrationTimeIntervalLow,
            String proposedAdministrationTimeIntervalHigh,
            String validAdministrationTimeIntervalLow,
            String validAdministrationTimeIntervalHigh,
            String focusCode,
            String focusOid,
            String valueCode,
            String valueOid,
            List<CD> interpretations) throws CdsException {
        SubstanceAdministrationProposal substanceAdministrationProposal = CdsObjectFactory.addSubstanceAdministrationProposal(
                getCdsObjectVmr(),
                vaccineGroup, vaccineGroupOid,
                substanceCode, substanceOid,
                proposedAdministrationTimeIntervalLow,
                proposedAdministrationTimeIntervalHigh,
                validAdministrationTimeIntervalLow,
                validAdministrationTimeIntervalHigh);
        substanceAdministrationProposal = CdsObjectFactory.addObservationResult(
                substanceAdministrationProposal,
                focusCode, focusOid,
                valueCode, valueOid,
                interpretations);
        return substanceAdministrationProposal;
    }

    /**
     * Add a SupplyEvent to the CDS object.
     *
     * This method is a generic implementation for simplifying the adding of a SupplyEvent to the CDS object.
     *
     * @see SupplyEvent
     * @see org.cdsframework.util.support.cds.Config
     * @param code the status code
     * @param displayName the status code display name
     * @param codeSystem the status code code system
     * @param codeSystemName the status code code system name
     * @param administrationEventLowTime the low date time of the supply event
     * @param administrationEventHighTime the high date time of the supply event
     * @param idRoot the OID identifying the source of the idExtension
     * @param idExtension a unique ID identifying this particular event
     * @return a properly constructed SupplyEvent
     */
    public SupplyEvent addSupplyEvent(
            String code,
            String displayName,
            String codeSystem,
            String codeSystemName,
            String administrationEventLowTime,
            String administrationEventHighTime,
            String idRoot,
            String idExtension) {
        return addSupplyEvent(
                getCdsObjectVmr(),
                code,
                displayName,
                codeSystem,
                codeSystemName,
                administrationEventLowTime,
                administrationEventHighTime,
                idRoot,
                idExtension);
    }

    /**
     * Gets the list of SubstanceAdministrationEvents on the CDS object.
     *
     * @see SubstanceAdministrationEvent
     * @return the list of SubstanceAdministrationEvents
     */
    public List<SubstanceAdministrationEvent> getSubstanceAdministrationEvents() {
        SubstanceAdministrationEvents substanceAdministrationEvents = getCdsObjectVmr().getPatient().getClinicalStatements().getSubstanceAdministrationEvents();
        if (substanceAdministrationEvents == null) {
            substanceAdministrationEvents = new SubstanceAdministrationEvents();
            getCdsObjectVmr().getPatient().getClinicalStatements().setSubstanceAdministrationEvents(substanceAdministrationEvents);
        }
        return substanceAdministrationEvents.getSubstanceAdministrationEvent();
    }

    public String getPatientGender() {
        String result = null;
        VMR cdsObjectVmr = getCdsObjectVmr();
        if (cdsObjectVmr != null) {
            EvaluatedPerson patient = cdsObjectVmr.getPatient();
            if (patient != null) {
                Demographics demographics = patient.getDemographics();
                if (demographics != null) {
                    CD gender = demographics.getGender();
                    if (gender != null) {
                        result = gender.getCode();
                    }
                }
            }
        }
        return result;
    }

    public String getPatientBirthTime() {
        String result = null;
        VMR cdsObjectVmr = getCdsObjectVmr();
        if (cdsObjectVmr != null) {
            EvaluatedPerson patient = cdsObjectVmr.getPatient();
            if (patient != null) {
                Demographics demographics = patient.getDemographics();
                if (demographics != null) {
                    TS birthTime = demographics.getBirthTime();
                    if (birthTime != null) {
                        result = birthTime.getValue();
                    }
                }
            }
        }
        return result;
    }

    public String getPatientId() {
        String result = null;
        VMR cdsObjectVmr = getCdsObjectVmr();
        if (cdsObjectVmr != null) {
            EvaluatedPerson patient = cdsObjectVmr.getPatient();
            if (patient != null) {
                II id = patient.getId();
                if (id.getExtension() != null && !id.getExtension().trim().isEmpty()) {
                    result = id.getExtension().trim();
                } else {
                    result = id.getRoot();
                }
            }
        }
        return result;
    }
}
