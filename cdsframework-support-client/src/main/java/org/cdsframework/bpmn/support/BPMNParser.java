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
package org.cdsframework.bpmn.support;

import java.io.FileNotFoundException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import org.omg.spec.bpmn._20100524.model.Definitions;
import org.omg.spec.bpmn._20100524.model.TBusinessRuleTask;
import org.omg.spec.bpmn._20100524.model.TFlowElement;
import org.omg.spec.bpmn._20100524.model.TProcess;
import org.omg.spec.bpmn._20100524.model.TRootElement;

/**
 *
 * @author HLN Consulting, LLC
 */
public class BPMNParser {

    private List<RuleFlowGroup> ruleFlowGroups = new ArrayList<RuleFlowGroup>();
    private JAXBContext jaxbContext = null;
    private Unmarshaller unmarshaller = null;

    public BPMNParser() throws JAXBException {
        String canonicalName = Definitions.class.getCanonicalName();
        String jaxbContextString = canonicalName.substring(0, canonicalName.lastIndexOf("."));
        jaxbContext = JAXBContext.newInstance(jaxbContextString);
        unmarshaller = jaxbContext.createUnmarshaller();
    }

    public BPMNParser(String bpmnFile) throws FileNotFoundException, JAXBException {
        this();
        parse((Definitions) unmarshaller.unmarshal(new java.io.FileInputStream(bpmnFile)));
    }

    public BPMNParser(Reader reader) throws JAXBException {
        this();
        parse((Definitions) unmarshaller.unmarshal(reader));
    }

    private void parse(Definitions definitions) {

        List<JAXBElement<? extends TRootElement>> rootElements = definitions.getRootElements();
        if (!rootElements.isEmpty()) {
            for (JAXBElement<? extends TRootElement> rootElement : rootElements) {
                TProcess tProcess = (TProcess) rootElement.getValue();
                List<JAXBElement<? extends TFlowElement>> flowElements = tProcess.getFlowElements();
                for (JAXBElement<? extends TFlowElement> flowElement : flowElements) {
                    if (flowElement.getValue() instanceof TBusinessRuleTask) {
                        TBusinessRuleTask tBusinessRuleTask = (TBusinessRuleTask) flowElement.getValue();
                        Map<QName, String> otherAttributes = tBusinessRuleTask.getOtherAttributes();
                        for (Map.Entry<QName, String> otherAttribute : otherAttributes.entrySet()) {
                            ruleFlowGroups.add(new RuleFlowGroup(otherAttribute.getValue(), tBusinessRuleTask.getName()));
                        }
                    }
                }
            }
        }
    }

    public List<RuleFlowGroup> getRuleFlowGroups() {
        return ruleFlowGroups;
    }

    public void setRuleFlowGroups(List<RuleFlowGroup> ruleFlowGroups) {
        this.ruleFlowGroups = ruleFlowGroups;
    }

    public class RuleFlowGroup {

        private String ruleFlowGroupCode;
        private String ruleFlowGroupName;

        public RuleFlowGroup(String ruleFlowGroupCode, String ruleFlowGroupName) {
            this.ruleFlowGroupCode = ruleFlowGroupCode;
            this.ruleFlowGroupName = ruleFlowGroupName;
        }

        public String getRuleFlowGroupCode() {
            return ruleFlowGroupCode;
        }

        public void setRuleFlowGroupCode(String ruleFlowGroupCode) {
            this.ruleFlowGroupCode = ruleFlowGroupCode;
        }

        public String getRuleFlowGroupName() {
            return ruleFlowGroupName;
        }

        public void setRuleFlowGroupName(String ruleFlowGroupName) {
            this.ruleFlowGroupName = ruleFlowGroupName;
        }
    }
}
