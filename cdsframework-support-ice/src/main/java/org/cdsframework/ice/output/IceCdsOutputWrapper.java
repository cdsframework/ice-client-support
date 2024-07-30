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
package org.cdsframework.ice.output;

import java.util.Date;
import java.util.List;
import org.cdsframework.cds.vmr.CdsOutputWrapper;
import org.cdsframework.ice.util.IceCdsObjectFactory;
import org.opencds.support.util.DateUtils;
import org.opencds.vmr.v1_0.schema.CD;
import org.opencds.vmr.v1_0.schema.CDSOutput;
import org.opencds.vmr.v1_0.schema.ObservationResult;
import org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent;
import org.opencds.vmr.v1_0.schema.SubstanceAdministrationProposal;

/**
 *
 * @author sdn
 */
public class IceCdsOutputWrapper {

    private final CdsOutputWrapper cdsOutputWrapper;

    public IceCdsOutputWrapper() {
        cdsOutputWrapper = CdsOutputWrapper.getCdsOutputWrapper();
    }

    public IceCdsOutputWrapper(CdsOutputWrapper cdsInputWrapper) {
        this.cdsOutputWrapper = cdsInputWrapper;
    }

    public IceCdsOutputWrapper(CDSOutput cdsOutput) {
        cdsOutputWrapper = CdsOutputWrapper.getCdsOutputWrapper(cdsOutput);
    }

    public CdsOutputWrapper getCdsOutputWrapper() {
        return cdsOutputWrapper;
    }

    public CDSOutput getCdsOutput() {
        return cdsOutputWrapper != null ? cdsOutputWrapper.getCdsObject() : null;
    }

    public void setPatientBirthTime(String value) {
        cdsOutputWrapper.setPatientBirthTime(value);
    }

    public void setPatientBirthTime(Date value) {
        cdsOutputWrapper.setPatientBirthTime(value);
    }

    public void setPatientGender(String code, String oid) {
        cdsOutputWrapper.setPatientGender(code, oid);
    }

    public void setPatientId(String value) {
        cdsOutputWrapper.setPatientId(value);
    }

    public SubstanceAdministrationEvent addSubstanceAdministrationEvent(
            String substanceCode,
            String substanceCodeOid,
            String administrationTimeInterval,
            String idRoot,
            String idExtension,
            List<SubstanceAdministrationEvent> components) {
        return cdsOutputWrapper.addSubstanceAdministrationEvent(
                substanceCode, null,
                substanceCodeOid, null,
                administrationTimeInterval, administrationTimeInterval,
                idRoot, idExtension,
                components);
    }

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
            List<CD> reasons) {
        return cdsOutputWrapper.addSubstanceAdministrationProposal(
                vaccineGroup,
                vaccineGroupOid,
                substanceCode,
                substanceOid,
                administrationTimeInterval,
                focusCode,
                focusOid,
                valueCode,
                valueOid,
                reasons);
    }

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
            List<CD> reasons) {
        return cdsOutputWrapper.addSubstanceAdministrationProposal(
                vaccineGroup,
                vaccineGroupOid,
                substanceCode,
                substanceOid,
                proposedAdministrationTimeIntervalLow,
                proposedAdministrationTimeIntervalHigh,
                validAdministrationTimeIntervalLow,
                validAdministrationTimeIntervalHigh,
                focusCode,
                focusOid,
                valueCode,
                valueOid,
                reasons);
    }

    public List<SubstanceAdministrationProposal> getSubstanceAdministrationProposals() {
        return cdsOutputWrapper.getSubstanceAdministrationProposals();
    }

    public List<SubstanceAdministrationEvent> getSubstanceAdministrationEvents() {
        return cdsOutputWrapper.getSubstanceAdministrationEvents();
    }

    public ObservationResult addImmunityObservationResult(Date observationEventTime, String offset, String focusCode, String focusOid, String valueCode, String valueOid, String interpretationCode, String interpretationOid) {
        return addImmunityObservationResult(
                DateUtils.getISODateFormat(observationEventTime), offset,
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
        cdsOutputWrapper.addObservationResult(observationResult);
        return observationResult;

    }

}
