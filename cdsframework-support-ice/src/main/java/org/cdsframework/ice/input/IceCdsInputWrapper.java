/**
 * The cdsframework support client aims at making ICE vMR generation easier.
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
package org.cdsframework.ice.input;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.cdsframework.cds.util.CdsObjectFactory;
import org.cdsframework.cds.vmr.CdsInputWrapper;
import org.cdsframework.ice.util.IceCdsObjectFactory;
import org.opencds.support.util.DateUtils;
import org.opencds.vmr.v1_0.schema.CD;
import org.opencds.vmr.v1_0.schema.CDSInput;
import org.opencds.vmr.v1_0.schema.EvaluatedPerson;
import org.opencds.vmr.v1_0.schema.ObservationResult;
import org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent;
import org.opencds.vmr.v1_0.schema.SubstanceAdministrationProposal;

/**
 *
 * @author HLN Consulting, LLC
 */
public class IceCdsInputWrapper {

    private final CdsInputWrapper cdsInputWrapper;

    public IceCdsInputWrapper() {
        cdsInputWrapper = CdsInputWrapper.getCdsInputWrapper();
    }

    public IceCdsInputWrapper(CdsInputWrapper cdsInputWrapper) {
        this.cdsInputWrapper = cdsInputWrapper;
    }

    public IceCdsInputWrapper(CDSInput cdsInput) {
        cdsInputWrapper = CdsInputWrapper.getCdsInputWrapper(cdsInput);
    }

    public CdsInputWrapper getCdsInputWrapper() {
        return cdsInputWrapper;
    }

    public CDSInput getCdsInput() {
        return cdsInputWrapper != null ? cdsInputWrapper.getCdsObject() : null;
    }

    /**
     * Add an ObservationResult to the CDS object representing an immunity event.
     *
     * This method is an ICE specific implementation for simplifying the adding of a ObservationResult to the CDS object.
     *
     * @param offset
     * @see org.cdsframework.util.support.cds.Config
     * @see ObservationResult
     * @param observationEventTimeDate the date time of the observation
     * @param focusCode the focus of the observation
     * @param focusOid the focus oid of the observation
     * @param valueCode the observation value
     * @param valueOid the observation value oid
     * @param interpretationCode the observation interpretation
     * @param interpretationOid the observation interpretation oid
     * @return a properly constructed ObservationResult
     */
    public ObservationResult addImmunityObservationResult(
            Date observationEventTimeDate,
            String offset,
            String focusCode,
            String focusOid,
            String valueCode,
            String valueOid,
            String interpretationCode,
            String interpretationOid) {
        return addImmunityObservationResult(
                DateUtils.getISODateFormat(observationEventTimeDate), offset,
                focusCode, focusOid,
                valueCode, valueOid,
                interpretationCode, interpretationOid);

    }

    /**
     * Add an ObservationResult to the CDS object representing an immunity event.
     *
     * This method is an ICE specific implementation for simplifying the adding of a ObservationResult to the CDS object.
     *
     * @param offset
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
    public ObservationResult addImmunityObservationResult(
            String observationEventTime,
            String offset,
            String focusCode,
            String focusOid,
            String valueCode,
            String valueOid,
            String interpretationCode,
            String interpretationOid) {

        ObservationResult observationResult = IceCdsObjectFactory.getImmunityObservationResult(
                observationEventTime, offset,
                focusCode, focusOid,
                valueCode, valueOid,
                interpretationCode, interpretationOid);
        cdsInputWrapper.addObservationResult(observationResult);
        return observationResult;

    }

    /**
     * Add a SubstanceAdministrationProposal to the CDS object.
     *
     * This method is an ICE specific implementation for simplifying the adding of a SubstanceAdministrationProposal to the CDS
     * object.
     *
     * @see SubstanceAdministrationProposal
     * @param vaccineGroup the vaccine group code
     * @param vaccineGroupOid the vaccine group oid
     * @param substanceCode the substance code
     * @param substanceOid the substance code oid
     * @param administrationTimeIntervalDate the date time of the substance administration
     * @param focusCode the focus of the administration event
     * @param focusOid the focus oid of the administration event
     * @param valueCode the recommendation value
     * @param valueOid the recommendation value oid
     * @param interpretations the interpretations of the administration event
     * @return a properly constructed SubstanceAdministrationProposal
     */
    public SubstanceAdministrationProposal addSubstanceAdministrationProposal(
            String vaccineGroup,
            String vaccineGroupOid,
            String substanceCode,
            String substanceOid,
            Date administrationTimeIntervalDate,
            String focusCode,
            String focusOid,
            String valueCode,
            String valueOid,
            List<CD> interpretations) {
        return addSubstanceAdministrationProposal(
                vaccineGroup,
                vaccineGroupOid,
                substanceCode,
                substanceOid,
                DateUtils.getISODateFormat(administrationTimeIntervalDate),
                focusCode,
                focusOid,
                valueCode,
                valueOid,
                interpretations);
    }

    /**
     * Add a SubstanceAdministrationProposal to the CDS object.
     *
     * This method is an ICE specific implementation for simplifying the adding of a SubstanceAdministrationProposal to the CDS
     * object.
      *
     * @see SubstanceAdministrationProposal
     * @see org.cdsframework.util.support.cds.Config
     * @param vaccineGroup the vaccine group code
     * @param vaccineGroupOid the vaccine group oid
     * @param substanceCode the substance code
     * @param substanceOid the substance code oid
     * @param administrationTimeInterval the date time of the substance administration
     * @param focusCode the focus of the administration event
     * @param focusOid the focus oid of the administration event
     * @param valueCode the recommendation value
     * @param valueOid the recommendation value oid
     * @param interpretations the interpretations of the administration event
     * @return a properly constructed SubstanceAdministrationProposal
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
            List<CD> interpretations) {
        SubstanceAdministrationProposal substanceAdministrationProposal = IceCdsObjectFactory.getSubstanceAdministrationProposal(
                vaccineGroup,
                vaccineGroupOid,
                substanceCode,
                substanceOid,
                administrationTimeInterval);
        substanceAdministrationProposal = CdsObjectFactory.addObservationResult(
                substanceAdministrationProposal,
                focusCode, focusOid,
                valueCode, valueOid,
                interpretations);
        cdsInputWrapper.addSubstanceAdministrationProposal(substanceAdministrationProposal);
        return substanceAdministrationProposal;
    }

    public void setPatientGender(String code, String codeSystemOid) {
        cdsInputWrapper.setPatientGender(code, codeSystemOid);
    }

    public void setPatientBirthTime(String birthTime) {
        cdsInputWrapper.setPatientBirthTime(birthTime);
    }

    public void setPatientBirthTime(Date birthDate) {
        cdsInputWrapper.setPatientBirthTime(birthDate);
    }

	/**
	 * Add a SubstanceAdministrationEvent to the CDS object.
	 * 
	 * @param substanceCode the substance code
	 * @param substanceCodeOid the substance code oid
	 * @param administrationDate the date time of the substance administration
	 * @param idRoot a unique ID identifying this particular administration event
	 * @param idExtension the unique ID root
	 * @return a properly constructed SubstanceAdministrationEvent
	 */
    public SubstanceAdministrationEvent addSubstanceAdministrationEvent(
            String substanceCode,
            String substanceCodeOid,
            Date administrationDate,
            String idRoot,
            String idExtension) {
        return addSubstanceAdministrationEvent(
                substanceCode, substanceCodeOid,
                DateUtils.getISODateFormat(administrationDate),
                idRoot, idExtension);
    }

	/**
	 * Add a SubstanceAdministrationEvent to the CDS object.
	 * 
	 * @param substanceCode the substance code
	 * @param substanceCodeOid the substance code oid
	 * @param administrationDate the date time of the substance administration
	 * @param idRoot a unique ID identifying this particular administration event
	 * @param idExtension the unique ID root
	 * @param isValid dose override flag (null will not add isValid tag to VMR)
	 * @return a properly constructed SubstanceAdministrationEvent
	 */
    public SubstanceAdministrationEvent addSubstanceAdministrationEvent(
            String substanceCode,
            String substanceCodeOid,
            Date administrationDate,
            String idRoot,
            String idExtension,
			Boolean isValid) {
        return addSubstanceAdministrationEvent(
                substanceCode, substanceCodeOid,
                DateUtils.getISODateFormat(administrationDate),
                idRoot, idExtension, isValid);
    }

	/**
	 * Add a SubstanceAdministrationEvent to the CDS object.
	 * 
	 * @param substanceCode the substance code
	 * @param substanceCodeOid the substance code oid
	 * @param administrationTime the date time of the substance administration
	 * @param idRoot a unique ID identifying this particular administration event
	 * @param idExtension the unique ID root
	 * @return a properly constructed SubstanceAdministrationEvent 
	 */
    public SubstanceAdministrationEvent addSubstanceAdministrationEvent(
            String substanceCode,
            String substanceCodeOid,
            String administrationTime,
            String idRoot,
            String idExtension) {
        SubstanceAdministrationEvent substanceAdministrationEvent = IceCdsObjectFactory.getSubstanceAdministrationEvent(
                substanceCode,
                substanceCodeOid,
                administrationTime,
                idRoot,
                idExtension,
                null);
        cdsInputWrapper.addSubstanceAdministrationEvent(substanceAdministrationEvent);
        return substanceAdministrationEvent;
    }

	/**
	 * Add a SubstanceAdministrationEvent to the CDS object.
	 * 
	 * @param substanceCode the substance code
	 * @param substanceCodeOid the substance code oid
	 * @param administrationTime the date time of the substance administration
	 * @param idRoot a unique ID identifying this particular administration event
	 * @param idExtension the unique ID root
	 * @param isValid dose override flag (null will not add isValid tag to VMR)
	 * @return a properly constructed SubstanceAdministrationEvent 
	 */
    public SubstanceAdministrationEvent addSubstanceAdministrationEvent(
            String substanceCode,
            String substanceCodeOid,
            String administrationTime,
            String idRoot,
            String idExtension,
			Boolean isValid) {
        SubstanceAdministrationEvent substanceAdministrationEvent = IceCdsObjectFactory.getSubstanceAdministrationEvent(
                substanceCode,
                substanceCodeOid,
                administrationTime,
                idRoot,
                idExtension,
				isValid,
                null);
        cdsInputWrapper.addSubstanceAdministrationEvent(substanceAdministrationEvent);
        return substanceAdministrationEvent;
    }

    public void setPatientId(String patientId) {
        cdsInputWrapper.setPatientId(patientId);
    }

    /**
     * Gets the list of observation results on the CDS object.
     *
     * @see ObservationResult
     * @return the list of observation results
     */
    public List<ObservationResult> getImmunityObservationResults() {
        List<ObservationResult> result;
        EvaluatedPerson.ClinicalStatements.ObservationResults observationResults =
                cdsInputWrapper.getCdsObject().getVmrInput().getPatient().getClinicalStatements().getObservationResults();
        if (observationResults == null) {
            result = new ArrayList<ObservationResult>();
        } else {
            result = observationResults.getObservationResult();
        }
        return result;
    }

}
