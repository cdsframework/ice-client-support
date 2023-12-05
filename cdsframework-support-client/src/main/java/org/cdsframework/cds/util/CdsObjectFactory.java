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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdsframework.cds.util;

import java.text.ParseException;
import java.util.List;
import java.util.UUID;
import org.cdsframework.exceptions.CdsException;
import org.cdsframework.util.support.cds.Config;
import org.opencds.support.util.DateUtils;
import org.opencds.vmr.v1_0.schema.AD;
import org.opencds.vmr.v1_0.schema.ADXP;
import org.opencds.vmr.v1_0.schema.AddressPartType;
import org.opencds.vmr.v1_0.schema.AdministrableSubstance;
import org.opencds.vmr.v1_0.schema.AdverseEvent;
import org.opencds.vmr.v1_0.schema.BL;
import org.opencds.vmr.v1_0.schema.CD;
import org.opencds.vmr.v1_0.schema.EncounterEvent;
import org.opencds.vmr.v1_0.schema.EvaluatedPerson;
import org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.SubstanceAdministrationProposals;
import org.opencds.vmr.v1_0.schema.Goal;
import org.opencds.vmr.v1_0.schema.II;
import org.opencds.vmr.v1_0.schema.IVLTS;
import org.opencds.vmr.v1_0.schema.ObservationOrder;
import org.opencds.vmr.v1_0.schema.ObservationResult;
import org.opencds.vmr.v1_0.schema.ObservationResult.ObservationValue;
import org.opencds.vmr.v1_0.schema.PQ;
import org.opencds.vmr.v1_0.schema.PostalAddressUse;
import org.opencds.vmr.v1_0.schema.Problem;
import org.opencds.vmr.v1_0.schema.ProcedureEvent;
import org.opencds.vmr.v1_0.schema.RelatedClinicalStatement;
import org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent;
import org.opencds.vmr.v1_0.schema.SubstanceAdministrationProposal;
import org.opencds.vmr.v1_0.schema.SupplyEvent;
import org.opencds.vmr.v1_0.schema.TS;
import org.opencds.vmr.v1_0.schema.VMR;

/**
 *
 * @author HLN Consulting, LLC
 */
public class CdsObjectFactory {

    /**
     * Generate a properly constructed CD object.
     *
     * @param code
     * @param codeSystem
     * @param displayName
     * @param codeSystemName
     * @return
     */
    public static CD getCD(String code, String codeSystem, String displayName, String codeSystemName) {
        CD cd = new CD();
        if (code != null) {
            cd.setCode(code);
        }
        if (codeSystem != null) {
            cd.setCodeSystem(codeSystem);
        }
        if (displayName != null) {
            cd.setDisplayName(displayName);
        }
        if (codeSystemName != null) {
            cd.setCodeSystemName(codeSystemName);
        }
        return cd;
    }

    /**
     * Generate a properly constructed CD object.
     *
     * @param code
     * @param codeSystem
     * @param displayName
     * @return
     */
    public static CD getCD(String code, String codeSystem, String displayName) {
        return getCD(code, codeSystem, displayName, null);
    }

    /**
     * Generate a properly constructed CD object.
     *
     * @param code
     * @param codeSystem
     * @return
     */
    public static CD getCD(String code, String codeSystem) {
        return getCD(code, codeSystem, null, null);
    }

    /**
     * Generate a properly constructed ObservationValue object.
     *
     * @see ObservationValue
     * @param code the code of the value concept
     * @param codeSystem the code system of the value concept
     * @return
     */
    public static ObservationValue getObservationValue(String code, String codeSystem) {
        return getObservationValue(code, codeSystem, null, null);
    }

    /**
     * Generate a properly constructed ObservationValue object.
     *
     * @see ObservationValue
     * @param code the code of the value concept
     * @param codeSystem the code system of the value concept
     * @param codeSystemName the code system name of the value concept
     * @param displayName the display name of the value concept
     * @return
     */
    public static ObservationValue getObservationValue(String code, String codeSystem, String codeSystemName, String displayName) {
        CD cd = getCD(code, codeSystem, displayName, codeSystemName);
        ObservationResult.ObservationValue observationValue = new ObservationResult.ObservationValue();
        observationValue.setConcept(cd);
        return observationValue;
    }

    /**
     * Generate a properly constructed IVLTS object.
     *
     * @param lowValue
     * @param highValue
     * @return
     */
    public static IVLTS getIVLTS(String lowValue, String highValue) {
        IVLTS ivlts = new IVLTS();
        if (lowValue != null) {
            ivlts.setLow(lowValue);
        }
        if (highValue != null) {
            ivlts.setHigh(highValue);
        }
        return ivlts;
    }

    /**
     * Generate a properly constructed IVLTS object.
     *
     * @return
     */
    public static IVLTS getIVLTS() {
        IVLTS ivlts = new IVLTS();
        return ivlts;
    }

    /**
     * Returns an II instance with the supplied root value.
     *
     * @param idRoot
     * @return
     */
    public static II getII(String idRoot) {
        II ii = new II();
        ii.setRoot(idRoot);
        return ii;
    }

    /**
     * Returns an II instance with the supplied root and extension values. If
     * root is null a random UUID is assigned.
     *
     * @param idRoot
     * @param idExtension
     * @return
     */
    public static II getII(String idRoot, String idExtension) {
        II ii = new II();
        if (idRoot != null && !idRoot.trim().isEmpty()) {
            ii.setRoot(idRoot);
        } else {
            ii.setRoot(UUID.randomUUID().toString());
        }
        if (idExtension != null && !idExtension.trim().isEmpty()) {
            ii.setExtension(idExtension);
        }
        return ii;
    }

    public static PQ getPQ(String value, String unit) {
        PQ pq = new PQ();
        pq.setValue(Double.valueOf(value));
        pq.setUnit(unit);
        return pq;
    }

    /**
     * Returns an II instance with a random UUID assigned.
     *
     * @return
     */
    public static II getII() {
        II ii = new II();
        ii.setRoot(UUID.randomUUID().toString());
        return ii;
    }

    /**
     * Generate a properly constructed BL object.
     *
     * @param value
     * @return
     */
    public static BL getBL(boolean value) {
        BL bl = new BL();
        bl.setValue(value);
        return bl;
    }

    public static TS getTS(String value, boolean dateOnly) throws ParseException {
        if (value == null) {
            throw new IllegalArgumentException("value was null!");
        }
        value = value.trim();
        if (value.length() < 8) {
            throw new IllegalArgumentException("value was too short: " + value);
        }
        if (dateOnly && value.length() > 8) {
            value = value.substring(0, 8);
            DateUtils.parseISODateFormat(value);
        }
        if (!dateOnly) {
            DateUtils.parseISODatetimeFormat(value);
        }
        TS ts = new TS();
        ts.setValue(value);
        return ts;
    }

    public static AD getAD(PostalAddressUse use, String sal, String cty, String sta, String zip, String cpa, String cnt) {

        AD ad = new AD();

        ad.getUse().add(use);

        ADXP adxp = new ADXP();
        adxp.setType(AddressPartType.SAL);
        adxp.setValue(sal);
        ad.getPart().add(adxp);

        adxp = new ADXP();
        adxp.setType(AddressPartType.CTY);
        adxp.setValue(cty);
        ad.getPart().add(adxp);

        adxp = new ADXP();
        adxp.setType(AddressPartType.STA);
        adxp.setValue(sta);
        ad.getPart().add(adxp);

        adxp = new ADXP();
        adxp.setType(AddressPartType.ZIP);
        adxp.setValue(zip);
        ad.getPart().add(adxp);

        adxp = new ADXP();
        adxp.setType(AddressPartType.CPA);
        adxp.setValue(cpa);
        ad.getPart().add(adxp);

        adxp = new ADXP();
        adxp.setType(AddressPartType.CNT);
        adxp.setValue(cnt);
        ad.getPart().add(adxp);

        return ad;
    }

    /**
     * Generate a properly constructed ObservationResult object.
     *
     * Defaults the template id to the value of
     * Config.getCodeSystemOid("OBSERVATION_RESULT_ROOT")
     *
     * @see ObservationResult
     * @return
     */
    public static ObservationResult getObservationResult() {
        ObservationResult observationResult = new ObservationResult();
        observationResult.getTemplateId().add(getII(Config.getCodeSystemOid("OBSERVATION_RESULT_ROOT")));
        observationResult.setId(getII());
        return observationResult;
    }

    /**
     * Generate a properly constructed ObservationResult object.
     *
     * Defaults the template id to the value of
     * Config.getCodeSystemOid("OBSERVATION_RESULT_ROOT")
     *
     * @see ObservationResult
     * @param extension a unique ID identifying this particular observation
     * result
     * @param root the OID identifying the source of the extension
     * @return
     */
    public static ObservationResult getObservationResult(String extension, String root) {
        ObservationResult observationResult = new ObservationResult();
        observationResult.getTemplateId().add(getII(Config.getCodeSystemOid("OBSERVATION_RESULT_ROOT")));
        observationResult.setId(getII(root, extension));
        return observationResult;
    }

    /**
     * Generate a properly constructed ObservationOrder object.
     *
     * Defaults the template id to the value of
     * Config.getCodeSystemOid("OBSERVATION_RESULT_ROOT")
     *
     * @see ObservationOrder
     * @param extension a unique ID identifying this particular observation
     * result
     * @param root the OID identifying the source of the extension
     * @return
     */
    public static ObservationOrder getObservationOrder(String extension, String root) {
        ObservationOrder observationOrder = new ObservationOrder();
        observationOrder.getTemplateId().add(getII(Config.getCodeSystemOid("OBSERVATION_RESULT_ROOT")));
        observationOrder.setId(getII(root, extension));
        return observationOrder;
    }

    /**
     * Generates a properly constructed ObservationResult instance.
     *
     * @param focusValue
     * @param observationValue
     * @param interpretationValues
     * @return
     */
    public static ObservationResult getObservationResult(
            CD focusValue,
            ObservationResult.ObservationValue observationValue,
            List<CD> interpretationValues) {
        ObservationResult observationResult = getObservationResult();
        observationResult.getInterpretation().addAll(interpretationValues);
        observationResult.setObservationValue(observationValue);
        observationResult.setObservationFocus(focusValue);

        return observationResult;
    }

    public static AdministrableSubstance getAdministrableSubstance() {
        AdministrableSubstance administerableSubstance = new AdministrableSubstance();
        administerableSubstance.setId(getII());
        return administerableSubstance;
    }

    public static AdministrableSubstance getAdministrableSubstance(String idRoot, String idExtension) {
        AdministrableSubstance administerableSubstance = new AdministrableSubstance();
        administerableSubstance.setId(getII(idRoot, idExtension));
        return administerableSubstance;
    }

    /**
     * Generates a properly constructed SubstanceAdministrationEvent instance.
     *
     * @param code
     * @param displayName
     * @param codeSystem
     * @param codeSystemName
     * @param administrationEventLowTime
     * @param administrationEventHighTime
     * @return
     */
    public static SubstanceAdministrationEvent getSubstanceAdministrationEvent(
            String code,
            String displayName,
            String codeSystem,
            String codeSystemName,
            String administrationEventLowTime,
            String administrationEventHighTime) {
        SubstanceAdministrationEvent substanceAdministrationEvent = getSubstanceAdministrationEvent();

        if (code != null && codeSystem != null) {
            AdministrableSubstance substance = getAdministrableSubstance();
            substance.setSubstanceCode(getCD(code, codeSystem, displayName, codeSystemName));
            substanceAdministrationEvent.setSubstance(substance);
        }

        IVLTS ivlts = new IVLTS();
        ivlts.setHigh(administrationEventHighTime);
        ivlts.setLow(administrationEventLowTime);

        substanceAdministrationEvent.setAdministrationTimeInterval(ivlts);

        return substanceAdministrationEvent;
    }

    /**
     * Generates a properly constructed SupplyEvent instance.
     *
     * @param code
     * @param displayName
     * @param codeSystem
     * @param codeSystemName
     * @param administrationEventLowTime
     * @param administrationEventHighTime
     * @return
     */
    public static SupplyEvent getSupplyEvent(
            String code,
            String displayName,
            String codeSystem,
            String codeSystemName,
            String administrationEventLowTime,
            String administrationEventHighTime) {
        SupplyEvent supplyEvent = getSupplyEvent();

        supplyEvent.setSupplyCode(getCD(code, codeSystem, displayName, codeSystemName));

        IVLTS ivlts = new IVLTS();
        ivlts.setHigh(administrationEventHighTime);
        ivlts.setLow(administrationEventLowTime);

        supplyEvent.setSupplyTime(ivlts);

        return supplyEvent;
    }

    public static ProcedureEvent getProcedureEvent(
            String code,
            String displayName,
            String codeSystem,
            String codeSystemName,
            String eventDate) {
        ProcedureEvent procedureEvent = getProcedureEvent();

        procedureEvent.setProcedureCode(getCD(code, codeSystem, displayName, codeSystemName));

        IVLTS ivlts = new IVLTS();
        ivlts.setHigh(eventDate);
        ivlts.setLow(eventDate);

        procedureEvent.setProcedureTime(ivlts);

        return procedureEvent;
    }

    public static ProcedureEvent getProcedureEvent(
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

        ProcedureEvent procedureEvent = getProcedureEvent();

        procedureEvent.getId().setRoot(idRoot);
        if (idExtension != null && !idExtension.trim().isEmpty()) {
            procedureEvent.getId().setExtension(idExtension);
        }

        procedureEvent.setProcedureCode(getCD(procedureEventCode, procedureEventCodeSystem, procedureEventCodeDisplayName, procedureEventCodeSystemName));

        ObservationResult observationResult = getObservationResult();
        ObservationResult.ObservationValue observationValue = getObservationValue(
                procedureEventStatusCode,
                procedureEventStatusCodeSystem,
                procedureEventStatusCodeSystemName,
                procedureEventStatusCodeDisplayName);
        observationResult.setObservationValue(observationValue);
        RelatedClinicalStatement relatedClinicalStatement = getRelatedClinicalStatement("PERT");
        relatedClinicalStatement.setObservationResult(observationResult);
        procedureEvent.getRelatedClinicalStatement().add(relatedClinicalStatement);

        IVLTS ivlts = new IVLTS();
        ivlts.setHigh(procedureEventHighTime);
        ivlts.setLow(procedureEventLowTime);

        procedureEvent.setProcedureTime(ivlts);

        return procedureEvent;
    }

    public static Goal getGoal(
            String goalFocusCode,
            String goalFocusDisplayName,
            String goalFocusCodeSystem,
            String goalFocusCodeSystemName,
            String goalStatusCode,
            String goalStatusCodeDisplayName,
            String goalStatusCodeCodeSystem,
            String goalStatusCodeCodeSystemName,
            String goalLowTime,
            String goalHighTime) {

        Goal goal = getGoal();

        goal.setGoalFocus(getCD(goalFocusCode, goalFocusCodeSystem, goalFocusDisplayName, goalFocusCodeSystemName));
        goal.setGoalStatus(getCD(goalStatusCode, goalStatusCodeCodeSystem, goalStatusCodeDisplayName, goalStatusCodeCodeSystemName));

        IVLTS ivlts = new IVLTS();
        ivlts.setHigh(goalHighTime);
        ivlts.setLow(goalLowTime);

        goal.setGoalObserverEventTime(ivlts);

        return goal;
    }

    public static AdverseEvent getAdverseEvent(
            String adverseEventCode,
            String adverseEventCodeDisplayName,
            String adverseEventCodeCodeSystem,
            String adverseEventCodeCodeSystemName,
            String adverseEventStatus,
            String adverseEventStatusDisplayName,
            String adverseEventStatusCodeSystem,
            String adverseEventStatusCodeSystemName,
            String adverseEventLowTime,
            String adverseEventHighTime) throws CdsException {
        AdverseEvent adverseEvent = getAdverseEvent();

        adverseEvent.setAdverseEventCode(getCD(adverseEventCode, adverseEventCodeCodeSystem, adverseEventCodeDisplayName, adverseEventCodeCodeSystemName));
        adverseEvent.setAdverseEventStatus(getCD(adverseEventStatus, adverseEventStatusCodeSystem, adverseEventStatusDisplayName, adverseEventStatusCodeSystemName));

        IVLTS ivlts = new IVLTS();
        ivlts.setHigh(adverseEventHighTime);
        ivlts.setLow(adverseEventLowTime);

        adverseEvent.setAdverseEventTime(ivlts);

        return adverseEvent;
    }

    public static Problem getProblem(
            String problemCode,
            String problemCodeDisplayName,
            String problemCodeSystem,
            String problemCodeSystemName,
            String statusCode,
            String statusCodeDisplayName,
            String statusCodeSystem,
            String statusCodeSystemName,
            String problemLowTime,
            String problemHighTime) throws CdsException {

        Problem problem = getProblem();

        problem.setProblemCode(getCD(problemCode, problemCodeSystem, problemCodeDisplayName, problemCodeSystemName));
        problem.setProblemStatus(getCD(statusCode, statusCodeSystem, statusCodeDisplayName, statusCodeSystemName));

        IVLTS ivlts = new IVLTS();
        ivlts.setHigh(problemHighTime);
        ivlts.setLow(problemLowTime);

        problem.setProblemEffectiveTime(ivlts);

        return problem;
    }

    public static Problem getProblem(
            String focusCode,
            String focusDisplayName,
            String focusCodeSystem,
            String focusCodeSystemName,
            String observationValueCode,
            String observationValueDisplayName,
            String observationValueCodeSystem,
            String observationValueCodeSystemName,
            String eventDate) throws CdsException {

        Problem problem = getProblem();

        ObservationResult observationResult = getObservationResult();
        ObservationResult.ObservationValue observationValue = getObservationValue(
                observationValueCode,
                observationValueCodeSystem,
                observationValueCodeSystemName,
                observationValueDisplayName);
        observationResult.setObservationValue(observationValue);
        observationResult.setObservationFocus(getCD(focusCode, focusCodeSystem, focusDisplayName, focusCodeSystemName));

        RelatedClinicalStatement relatedClinicalStatement = getRelatedClinicalStatement("PERT");
        relatedClinicalStatement.setObservationResult(observationResult);

        problem.getRelatedClinicalStatement().add(relatedClinicalStatement);

        IVLTS ivlts = new IVLTS();
        ivlts.setHigh(eventDate);
        ivlts.setLow(eventDate);

        problem.setProblemEffectiveTime(ivlts);

        return problem;
    }

    public static EncounterEvent getEncounterEvent(
            String encounterEventCode,
            String encounterEventCodeDisplayName,
            String encounterEventCodeCodeSystem,
            String encounterEventCodeCodeSystemName,
            String encounterEventLowTime,
            String encounterEventHighTime) throws CdsException {

        EncounterEvent encounterEvent = getEncounterEvent();
        encounterEvent.setEncounterType(getCD(encounterEventCode, encounterEventCodeCodeSystem, encounterEventCodeDisplayName, encounterEventCodeCodeSystemName));

        IVLTS ivlts = new IVLTS();
        ivlts.setHigh(encounterEventHighTime);
        ivlts.setLow(encounterEventLowTime);

        encounterEvent.setEncounterEventTime(ivlts);

        return encounterEvent;
    }

    /**
     * Internal method to add a SubstanceAdministrationProposal.
     *
     * @see SubstanceAdministrationProposal
     * @see Vmr
     * @param vmr
     * @param vaccineGroup the vaccine group code
     * @param vaccineGroupOid the vaccine group oid
     * @param substanceCode the substance code
     * @param substanceOid the substance oid
     * @param administrationTimeInterval
     * @return
     * @throws CdsException
     */
    public static SubstanceAdministrationProposal addSubstanceAdministrationProposal(
            VMR vmr,
            String vaccineGroup,
            String vaccineGroupOid,
            String substanceCode,
            String substanceOid,
            String administrationTimeInterval)
            throws CdsException {

        SubstanceAdministrationProposal substanceAdministrationProposal = getSubstanceAdministrationProposal();
        SubstanceAdministrationProposals substanceAdministrationProposals = getSubstanceAdministrationProposals(vmr);
        substanceAdministrationProposals.getSubstanceAdministrationProposal().add(substanceAdministrationProposal);

        AdministrableSubstance substance = getAdministrableSubstance();

        if (substanceCode != null && !substanceCode.trim().isEmpty()) {
            substance.setSubstanceCode(getCD(substanceCode, substanceOid));
        } else {
            substance.setSubstanceCode(getCD(vaccineGroup, vaccineGroupOid));
        }

        substanceAdministrationProposal.setSubstance(substance);

        if (administrationTimeInterval != null && !administrationTimeInterval.trim().isEmpty()) {
            IVLTS ivlts = new IVLTS();
            ivlts.setHigh(administrationTimeInterval);
            ivlts.setLow(administrationTimeInterval);

            substanceAdministrationProposal.setProposedAdministrationTimeInterval(ivlts);
        }

        return substanceAdministrationProposal;
    }

    /**
     * Internal method to add a SubstanceAdministrationProposal.
     *
     * @see SubstanceAdministrationProposal
     * @see Vmr
     * @param vmr
     * @param vaccineGroup the vaccine group code
     * @param vaccineGroupOid the vaccine group oid
     * @param substanceCode the substance code
     * @param substanceOid the substance oid
     * @param proposedAdministrationTimeIntervalLow
     * @param proposedAdministrationTimeIntervalHigh
     * @param validAdministrationTimeIntervalLow
     * @param validAdministrationTimeIntervalHigh
     * @return
     * @throws CdsException
     */
    public static SubstanceAdministrationProposal addSubstanceAdministrationProposal(
            VMR vmr,
            String vaccineGroup,
            String vaccineGroupOid,
            String substanceCode,
            String substanceOid,
            String proposedAdministrationTimeIntervalLow,
            String proposedAdministrationTimeIntervalHigh,
            String validAdministrationTimeIntervalLow,
            String validAdministrationTimeIntervalHigh)
            throws CdsException {

        SubstanceAdministrationProposal substanceAdministrationProposal = getSubstanceAdministrationProposal();
        SubstanceAdministrationProposals substanceAdministrationProposals = getSubstanceAdministrationProposals(vmr);
        substanceAdministrationProposals.getSubstanceAdministrationProposal().add(substanceAdministrationProposal);

        AdministrableSubstance substance = getAdministrableSubstance();

        if (substanceCode != null && !substanceCode.trim().isEmpty()) {
            substance.setSubstanceCode(getCD(substanceCode, substanceOid));
        } else {
            substance.setSubstanceCode(getCD(vaccineGroup, vaccineGroupOid));
        }

        substanceAdministrationProposal.setSubstance(substance);

        IVLTS proposedAdministrationTimeInterval = null;

        if (proposedAdministrationTimeIntervalLow != null && !proposedAdministrationTimeIntervalLow.trim().isEmpty()) {
            proposedAdministrationTimeInterval = new IVLTS();
            proposedAdministrationTimeInterval.setLow(proposedAdministrationTimeIntervalLow);

        }

        if (proposedAdministrationTimeIntervalHigh != null && !proposedAdministrationTimeIntervalHigh.trim().isEmpty()) {
            if (proposedAdministrationTimeInterval == null) {
                proposedAdministrationTimeInterval = new IVLTS();
            }
            proposedAdministrationTimeInterval.setHigh(proposedAdministrationTimeIntervalHigh);
        }

        if (proposedAdministrationTimeInterval != null) {
            substanceAdministrationProposal.setProposedAdministrationTimeInterval(proposedAdministrationTimeInterval);
        }

        IVLTS validAdministrationTimeInterval = null;

        if (validAdministrationTimeIntervalLow != null && !validAdministrationTimeIntervalLow.trim().isEmpty()) {
            validAdministrationTimeInterval = new IVLTS();
            validAdministrationTimeInterval.setLow(validAdministrationTimeIntervalLow);

        }

        if (validAdministrationTimeIntervalHigh != null && !validAdministrationTimeIntervalHigh.trim().isEmpty()) {
            if (validAdministrationTimeInterval == null) {
                validAdministrationTimeInterval = new IVLTS();
            }
            validAdministrationTimeInterval.setHigh(validAdministrationTimeIntervalHigh);
        }

        if (validAdministrationTimeInterval != null) {
            substanceAdministrationProposal.setValidAdministrationTimeInterval(validAdministrationTimeInterval);
        }

        return substanceAdministrationProposal;
    }

    /**
     * Gets the SubstanceAdministrationProposals from a supplied Vmr instance.
     *
     * @see Vmr
     * @see SubstanceAdministrationProposals
     * @param vmr the Vmr instance to retrieve the
     * SubstanceAdministrationProposals from
     * @return the SubstanceAdministrationProposals
     */
    public static SubstanceAdministrationProposals getSubstanceAdministrationProposals(VMR vmr) {
        EvaluatedPerson.ClinicalStatements clinicalStatements = vmr.getPatient().getClinicalStatements();
        SubstanceAdministrationProposals substanceAdministrationProposals = clinicalStatements.getSubstanceAdministrationProposals();
        if (substanceAdministrationProposals == null) {
            substanceAdministrationProposals = new SubstanceAdministrationProposals();
            clinicalStatements.setSubstanceAdministrationProposals(substanceAdministrationProposals);
        }
        return substanceAdministrationProposals;
    }

    /**
     * Generic addObservationResult method.
     *
     * @param <S>
     * @param substanceAdministrationObject
     * @param focusCode
     * @param focusOid
     * @param valueCode
     * @param valueOid
     * @param interpretations
     * @return
     */
    public static <S> S addObservationResult(
            S substanceAdministrationObject,
            String focusCode,
            String focusOid,
            String valueCode,
            String valueOid,
            List<CD> interpretations) {

        RelatedClinicalStatement relatedClinicalStatement = null;
        CD focusValue = getCD(focusCode, focusOid);
        ObservationValue observationValue = getObservationValue(valueCode, valueOid);

        if (substanceAdministrationObject instanceof SubstanceAdministrationEvent) {
            SubstanceAdministrationEvent substanceAdministrationEvent = (SubstanceAdministrationEvent) substanceAdministrationObject;
            relatedClinicalStatement = getRelatedClinicalStatement("PERT");
            substanceAdministrationEvent.getRelatedClinicalStatement().add(relatedClinicalStatement);
        } else if (substanceAdministrationObject instanceof SubstanceAdministrationProposal) {
            relatedClinicalStatement = getRelatedClinicalStatement("RSON");
            ((SubstanceAdministrationProposal) substanceAdministrationObject).getRelatedClinicalStatement().add(relatedClinicalStatement);
        }
        addObservationResult(relatedClinicalStatement, focusValue, observationValue, interpretations);
        return substanceAdministrationObject;
    }

    public static void addObservationResult(
            RelatedClinicalStatement relatedClinicalStatement,
            CD focusValue,
            ObservationValue observationValue,
            List<CD> interpretationValues) {
        ObservationResult observationResult = getObservationResult(focusValue, observationValue, interpretationValues);
        relatedClinicalStatement.setObservationResult(observationResult);
    }

    public static CD getGeneralPurposeCode() {
        return getCD(Config.getGeneralPurposeCode(),
                Config.getCodeSystemOid("GENERAL_PURPOSE"));
    }

    /**
     * Generate a properly constructed SubstanceAdministrationEvent object.
     *
     * Defaults the template id to the value of
     * Config.getCodeSystemOid("SUBSTANCE_ADMINISTRATION_EVENT_ROOT")
     *
     * @see SubstanceAdministrationEvent
     * @return an instance of SubstanceAdministrationEvent
     */
    public static SubstanceAdministrationEvent getSubstanceAdministrationEvent() {
        SubstanceAdministrationEvent substanceAdministrationEvent = new SubstanceAdministrationEvent();
        substanceAdministrationEvent.getTemplateId().add(getII(Config.getCodeSystemOid("SUBSTANCE_ADMINISTRATION_EVENT_ROOT")));
        substanceAdministrationEvent.setId(getII());
        substanceAdministrationEvent.setSubstanceAdministrationGeneralPurpose(getGeneralPurposeCode());
        return substanceAdministrationEvent;
    }

    /**
     * Generate a properly constructed SupplyEvent object.
     *
     * Defaults the template id to the value of
     * Config.getCodeSystemOid("SUPPLY_EVENT_ROOT")
     *
     * @see SubstanceAdministrationEvent
     * @return an instance of SubstanceAdministrationEvent
     */
    public static SupplyEvent getSupplyEvent() {
        SupplyEvent supplyEvent = new SupplyEvent();
        supplyEvent.getTemplateId().add(getII(Config.getCodeSystemOid("SUPPLY_EVENT_ROOT")));
        supplyEvent.setId(getII());
        return supplyEvent;
    }

    public static SubstanceAdministrationProposal getSubstanceAdministrationProposal() {
        SubstanceAdministrationProposal substanceAdministrationProposal = new SubstanceAdministrationProposal();
        substanceAdministrationProposal.getTemplateId().add(getII(Config.getCodeSystemOid("SUBSTANCE_ADMINISTRATION_PROPOSAL_ROOT")));
        substanceAdministrationProposal.setId(getII());
        substanceAdministrationProposal.setSubstanceAdministrationGeneralPurpose(getGeneralPurposeCode());
        return substanceAdministrationProposal;
    }

    public static ProcedureEvent getProcedureEvent() {
        ProcedureEvent procedureEvent = new ProcedureEvent();
        procedureEvent.getTemplateId().add(getII(Config.getCodeSystemOid("PROCEDURE_EVENT_ROOT")));
        procedureEvent.setId(getII());
        return procedureEvent;
    }

    public static AdverseEvent getAdverseEvent() {
        AdverseEvent adverseEvent = new AdverseEvent();
        adverseEvent.getTemplateId().add(getII(Config.getCodeSystemOid("ADVERSE_EVENT_ROOT")));
        adverseEvent.setId(getII());
        return adverseEvent;
    }

    public static Goal getGoal() {
        Goal goal = new Goal();
        goal.getTemplateId().add(getII(Config.getCodeSystemOid("GOAL_ROOT")));
        goal.setId(getII());
        return goal;
    }

    public static Problem getProblem() {
        Problem problem = new Problem();
        problem.getTemplateId().add(getII(Config.getCodeSystemOid("PROBLEM_ROOT")));
        problem.setId(getII());
        return problem;
    }

    public static EncounterEvent getEncounterEvent() {
        EncounterEvent encounterEvent = new EncounterEvent();
        encounterEvent.getTemplateId().add(getII(Config.getCodeSystemOid("ENCOUNTER_EVENT_ROOT")));
        encounterEvent.setId(getII());
        return encounterEvent;
    }

    /**
     * Generate a properly constructed RelatedClinicalStatement object.
     *
     * The targetRelationshipToSourceCode is from the
     * Config.getCodeSystemOid("ACT_RELATIONSHIP_TYPE") configured code system
     * OID.
     *
     * @see RelatedClinicalStatement
     * @param targetRelationshipToSourceCode the targets relationship to source
     * @return an instance of RelatedClinicalStatement
     */
    public static RelatedClinicalStatement getRelatedClinicalStatement(String targetRelationshipToSourceCode) {
        RelatedClinicalStatement relatedClinicalStatement = new RelatedClinicalStatement();
        relatedClinicalStatement.setTargetRelationshipToSource(getCD(targetRelationshipToSourceCode, Config.getCodeSystemOid("ACT_RELATIONSHIP_TYPE")));
        return relatedClinicalStatement;
    }

}
