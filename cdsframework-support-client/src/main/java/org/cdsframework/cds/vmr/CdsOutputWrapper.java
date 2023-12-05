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

import org.cdsframework.base.BaseCdsObject;
import org.opencds.vmr.v1_0.schema.CDSOutput;
import org.opencds.vmr.v1_0.schema.ObservationResult;
import org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent;
import org.opencds.vmr.v1_0.schema.SubstanceAdministrationProposal;

/**
 * A class for wrapping the JAXB generated CdsOutput class. Eases the construction of CdsOutput objects.
 *
 * For Example:
 *
 * <pre>
 *     CdsOutputWrapper output = CdsOutputWrapper.getCdsOutputWrapper();
 *     output.setPatientGender("F");
 *     output.setPatientBirthTime("19830630");
 *     output.addImmunityObservationResult(new Date(), "070.30", "DISEASE_DOCUMENTED", "IS_IMMUNE");
 *
 *     SubstanceAdministrationEvent hepBComponent1 =
 *     CdsOutputWrapper.getEvaluationSubstanceAdministrationEvent("45", "20080223", "VALID", "100", "");
 *     SubstanceAdministrationEvent hepBComponent2 =
 *     CdsOutputWrapper.getEvaluationSubstanceAdministrationEvent("08", "20080223", "INVALID", "100", "BELOW_MINIMUM_INTERVAL");
 *     SubstanceAdministrationEvent hepBComponent3 =
 *     CdsOutputWrapper.getEvaluationSubstanceAdministrationEvent("110", "20080223", "INVALID", "100", "EXTRA_DOSE");
 *
 *     output.addSubstanceAdministrationEvent("45", "20080223", "12345", new SubstanceAdministrationEvent[]{hepBComponent1, hepBComponent2});
 *
 *     output.addSubstanceAdministrationEvent("110", "20080223", "12346", new SubstanceAdministrationEvent[]{hepBComponent3});
 *
 *     output.addSubstanceAdministrationProposal("100", "45", "20111201", "100", "RECOMMENDED", new String[]{"DUE_NOW"});
 * </pre>
 *
 * @see BaseCdsObject
 * @see ObservationResult
 * @see SubstanceAdministrationEvent
 * @see SubstanceAdministrationProposal
 * @see CdsOutput
 * @author HLN Consulting, LLC
 */
public class CdsOutputWrapper extends BaseCdsObject<CDSOutput> {

    /**
     * Construct the instance with no args.
     */
	public CdsOutputWrapper() {
		super(CdsOutputWrapper.class, CDSOutput.class);
		logger.debug("no arg constructor called");
	}

    /**
     * Construct the instance with a CdsOutput instance.
     *
     * @see CdsOutput
     * @param cdsOutput the CdsOutput instance
     */
	public CdsOutputWrapper(CDSOutput cdsOutput) {
		super(CdsOutputWrapper.class, CDSOutput.class, cdsOutput);
		logger.debug("CdsOutput arg constructor called: " + cdsOutput);
	}

    /**
     * Factory-ish method for generating a CdsOutputWrapper instance.
     *
     * @return initialized instance of CdsOutputWrapper
     */
	public static CdsOutputWrapper getCdsOutputWrapper() {
        return new CdsOutputWrapper();
    }

    /**
     * Factory-ish method for generating a CdsOutputWrapper instance.
     *
     * @see CdsOutput
     * @param cdsOutput the CdsOutput instance
     * @return initialized instance of CdsOutputWrapper
     */
	public static CdsOutputWrapper getCdsOutputWrapper(CDSOutput cdsOutput) {
		if (cdsOutput == null) {
			return new CdsOutputWrapper();
		} else {
			return new CdsOutputWrapper(cdsOutput);
        }
    }
}
