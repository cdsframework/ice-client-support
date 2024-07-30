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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdsframework.ice.util;

import java.util.ArrayList;
import java.util.List;
import org.cdsframework.cds.util.CdsObjectFactory;
import org.opencds.vmr.v1_0.schema.AdministrableSubstance;
import org.opencds.vmr.v1_0.schema.BL;
import org.opencds.vmr.v1_0.schema.CD;
import org.opencds.vmr.v1_0.schema.IVLTS;
import org.opencds.vmr.v1_0.schema.ObservationResult;
import org.opencds.vmr.v1_0.schema.RelatedClinicalStatement;
import org.opencds.vmr.v1_0.schema.ST;
import org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent;
import org.opencds.vmr.v1_0.schema.SubstanceAdministrationProposal;

/**
 *
 * @author HLN Consulting, LLC
 */
public class IceCdsObjectFactory {

    /**
     * Generate an ObservationResult to the CDS object representing an immunity event.
     *
     * This method is an ICE specific implementation for simplifying the adding of a ObservationResult to the CDS object.
     *
     * @see org.cdsframework.util.support.cds.Config
     * @see ObservationResult
     * @param observationEventTime the date time of the observation
     * @param focusCode the focus of the observation
     * @param focusOid the focus oid of the observation
     * @param valueCode the observation value
     * @param valueOid the observation value oid
     * @param interpretationCode the observation interpretation
     * @param interpretationOid the observation interpretation oid
     * @return a properly constructed ObservationResult
     */
    public static ObservationResult getImmunityObservationResult(
            String observationEventTime,
            String offset,
            String focusCode,
            String focusOid,
            String valueCode,
            String valueOid,
            String interpretationCode,
            String interpretationOid) {

        CD focusValue = CdsObjectFactory.getCD(focusCode, focusOid);
        ObservationResult.ObservationValue observationValue = CdsObjectFactory.getObservationValue(valueCode, valueOid);
        if (offset != null) {
            ST st = new ST();
            st.setValue(offset);
            observationValue.setText(st);
        }
        List<CD> interpretationValues = new ArrayList<CD>();
        CD interpretationValue = CdsObjectFactory.getCD(interpretationCode, interpretationOid);
        interpretationValues.add(interpretationValue);

        ObservationResult observationResult = CdsObjectFactory.getObservationResult(
                focusValue,
                observationValue,
                interpretationValues);
        if (observationEventTime != null) {
            IVLTS ivlts = new IVLTS();
            ivlts.setHigh(observationEventTime);
            ivlts.setLow(observationEventTime);
            observationResult.setObservationEventTime(ivlts);
        }
        return observationResult;

    }

    /**
     * Generate a SubstanceAdministrationEvent object.
     *
     * This method is an ICE specific implementation for simplifying the creation of a SubstanceAdministrationEvent.
     *
     * @see SubstanceAdministrationEvent
     * @see org.cdsframework.util.support.cds.Config
     * @param substanceCode the substance code
     * @param substanceOid the substance code oid
     * @param administrationTimeInterval the date time of the substance administration
     * @param validityCode the validity code of the shot (optional)
     * @param validityOid the validity oid of the shot (optional)
     * @param focusCode the focus of the administration event
     * @param focusOid the focus oid of the administration event
     * @param interpretationCode the interpretation code of the administration event
     * @param interpretationOid the interpretation oid of the administration event
     * @return a properly constructed SubstanceAdministrationEvent with an ObservationResult on it
     */
    public static SubstanceAdministrationEvent getEvaluationSubstanceAdministrationEvent(
            String substanceCode,
            String substanceOid,
            String administrationTimeInterval,
            String validityCode,
            String validityOid,
            String focusCode,
            String focusOid,
            String interpretationCode,
            String interpretationOid) {
        List<CD> interpretations = new ArrayList<CD>();
        interpretations.add(CdsObjectFactory.getCD(interpretationCode, interpretationOid));

        return getEvaluationSubstanceAdministrationEvent(
                substanceCode,
                substanceOid,
                administrationTimeInterval,
                validityCode,
                validityOid,
                focusCode,
                focusOid,
                interpretations);
    }

    /**
     * Generate a SubstanceAdministrationEvent object.
     *
     * This method is an ICE specific implementation for simplifying the creation of a SubstanceAdministrationEvent.
     *
     * @see SubstanceAdministrationEvent
     * @param substanceCode the substance code
     * @param substanceOid the substance code oid
     * @param administrationTimeInterval the date time of the substance administration
     * @param validityCode the validity code of the shot (optional)
     * @param validityOid the validity oid of the shot (optional)
     * @param focusCode the focus of the administration event
     * @param focusOid the focus oid of the administration event
     * @param interpretations the interpretations of the administration event
     * @return a properly constructed SubstanceAdministrationEvent with an ObservationResult on it
     */
    public static SubstanceAdministrationEvent getEvaluationSubstanceAdministrationEvent(
            String substanceCode,
            String substanceOid,
            String administrationTimeInterval,
            String validityCode,
            String validityOid,
            String focusCode,
            String focusOid,
            List<CD> interpretations) {
        boolean valid = !"INVALID".equalsIgnoreCase(validityCode);
        SubstanceAdministrationEvent substanceAdministrationEvent
                = CdsObjectFactory.getSubstanceAdministrationEvent(substanceCode, null, substanceOid, null, administrationTimeInterval, administrationTimeInterval);
        substanceAdministrationEvent.setIsValid(CdsObjectFactory.getBL(valid));
        substanceAdministrationEvent = CdsObjectFactory.addObservationResult(
                substanceAdministrationEvent,
                focusCode,
                focusOid,
                validityCode,
                validityOid,
                interpretations);
        return substanceAdministrationEvent;
    }

    /**
     * Generate a SubstanceAdministrationProposal.
     *
     * @see SubstanceAdministrationProposal
     * @param vaccineGroup the vaccine group code
     * @param vaccineGroupOid the vaccine group oid
     * @param substanceCode the substance code
     * @param substanceOid the substance code oid
     * @return SubstanceAdministrationProposal
     */
    public static SubstanceAdministrationProposal getSubstanceAdministrationProposal(
            String vaccineGroup,
            String vaccineGroupOid,
            String substanceCode,
            String substanceOid,
            String administrationTimeInterval) {

        SubstanceAdministrationProposal substanceAdministrationProposal = CdsObjectFactory.getSubstanceAdministrationProposal();

        AdministrableSubstance substance = CdsObjectFactory.getAdministrableSubstance();

        if (substanceCode != null && !substanceCode.trim().isEmpty()) {
            substance.setSubstanceCode(CdsObjectFactory.getCD(substanceCode, substanceOid));
        } else {
            substance.setSubstanceCode(CdsObjectFactory.getCD(vaccineGroup, vaccineGroupOid));
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
     * Add a SubstanceAdministrationEvent to the CDS object.
     *
     * This method is an ICE specific implementation for simplifying the adding of a SubstanceAdministrationEvent to the CDS object.
     *
     * The substanceCode is from the Config.getCodeSystemOid("VACCINE") configured code system OID.
     *
     * @see SubstanceAdministrationEvent
     * @see org.cdsframework.util.support.cds.Config
     * @param substanceCode the substance code
     * @param substanceOid the substance code oid
     * @param administrationTimeInterval the date time of the substance administration
     * @param idRoot a unique ID identifying this particular administration event
     * @param idExtension the unique ID root
     * @param components a list of the vaccine components represented by SubstanceAdministrationEvent objects
     * @return a properly constructed SubstanceAdministrationEvent with an ObservationResult on it
     */
    public static SubstanceAdministrationEvent getSubstanceAdministrationEvent(
            String substanceCode,
            String substanceOid,
            String administrationTimeInterval,
            String idRoot,
            String idExtension,
            List<SubstanceAdministrationEvent> components) {
		
		return getSubstanceAdministrationEvent(substanceCode,
											   substanceOid,
											   administrationTimeInterval,
											   idRoot,
											   idExtension,
											   null,
											   components);
	}
	
	
    /**
     * Add a SubstanceAdministrationEvent to the CDS object.This method is an ICE specific implementation for simplifying the adding of a SubstanceAdministrationEvent to the CDS object.
     *
     * The substanceCode is from the Config.getCodeSystemOid("VACCINE") configured code system OID.
     *
     * @see SubstanceAdministrationEvent
     * @see org.cdsframework.util.support.cds.Config
     * @param substanceCode the substance code
     * @param substanceOid the substance code oid
     * @param administrationTimeInterval the date time of the substance administration
     * @param idRoot a unique ID identifying this particular administration event
     * @param idExtension the unique ID root
	 * @param isValid dose override flag (null will not add isValid tag to VMR)
     * @param components a list of the vaccine components represented by SubstanceAdministrationEvent objects
     * @return a properly constructed SubstanceAdministrationEvent with an ObservationResult on it
     */
    public static SubstanceAdministrationEvent getSubstanceAdministrationEvent(
            String substanceCode,
            String substanceOid,
            String administrationTimeInterval,
            String idRoot,
            String idExtension,
			Boolean isValid,
            List<SubstanceAdministrationEvent> components) {
		
        SubstanceAdministrationEvent substanceAdministrationEvent
                = CdsObjectFactory.getSubstanceAdministrationEvent(substanceCode, null, substanceOid, null, administrationTimeInterval, administrationTimeInterval);
        if (idRoot != null && !idRoot.trim().isEmpty()) {
            substanceAdministrationEvent.getId().setRoot(idRoot);
        }
        if (idExtension != null && !idExtension.trim().isEmpty()) {
            substanceAdministrationEvent.getId().setExtension(idExtension);
        }
		if (isValid != null) {
			BL blIsValue = new BL();
			blIsValue.setValue(isValid);
			substanceAdministrationEvent.setIsValid(blIsValue);
		}

        List<RelatedClinicalStatement> relatedClinicalStatements = substanceAdministrationEvent.getRelatedClinicalStatement();
        if (components != null) {
            for (SubstanceAdministrationEvent sae : components) {
                RelatedClinicalStatement relatedClinicalStatement = CdsObjectFactory.getRelatedClinicalStatement("PERT");
                relatedClinicalStatement.setSubstanceAdministrationEvent(sae);
                relatedClinicalStatements.add(relatedClinicalStatement);
            }
        }
        return substanceAdministrationEvent;
    }

}
