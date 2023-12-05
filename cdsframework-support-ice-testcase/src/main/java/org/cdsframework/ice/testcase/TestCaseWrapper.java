/**
 * The cdsframework support client aims at making ICE Test Case generation easier.
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
package org.cdsframework.ice.testcase;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import org.cdsframework.cds.vmr.CdsInputWrapper;
import org.cdsframework.cds.vmr.CdsOutputWrapper;
import org.cdsframework.enumeration.TestCasePropertyType;
import org.cdsframework.exceptions.CdsException;
import org.cdsframework.ice.input.IceCdsInputWrapper;
import org.cdsframework.ice.output.IceCdsOutputWrapper;
import org.cdsframework.ice.util.IceCdsObjectFactory;
import org.cdsframework.util.LogUtils;
import org.cdsframework.util.support.data.cds.testcase.TestCase;
import org.cdsframework.util.support.data.cds.testcase.TestCaseProperty;
import org.opencds.support.util.DateUtils;
import org.opencds.vmr.v1_0.schema.CD;
import org.opencds.vmr.v1_0.schema.CDSInput;
import org.opencds.vmr.v1_0.schema.CDSOutput;
import org.opencds.vmr.v1_0.schema.TS;
import org.opencds.vmr.v1_0.schema.ObservationResult;
import org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent;
import org.opencds.vmr.v1_0.schema.SubstanceAdministrationProposal;

/**
 * A class for wrapping the JAXB generated TestCase class. Eases the construction of TestCase properties, patient demographics,
 * CdsInput, CdsOutput, Vmr, ObservationResult, SubstanceAdministrationEvent, and SubstanceAdministrationProposal child class
 * objects.
 *
 * For Example:
 *
 * <pre>
 *     TestCaseWrapper testcase = TestCaseWrapper.getTestCaseWrapper();
 *     testcase.setExecutiondate(new Date());
 *     testcase.setSuiteName("MyTestSuite");
 *     testcase.setGroupName("MyTestGroup");
 *     testcase.setIgnore(false);
 *     testcase.setName("blah blah - hey hey");
 *     testcase.setNotes("blah blah blh blah blah ghlsdf sdfh aof asodiv das vasdvpoi asdv asdvsadvp asdfg");
 *     testcase.setPatientBirthTime("19750123");
 *     testcase.setPatientGender("M");
 *     testcase.setPatientId("54321");
 *     testcase.setRuletotest("asjdfhla asdf alksd pdf ahf apahwfe qwefh3984 qenka sd87134tu3hpjahfiq asdf");
 *     testcase.addProperty("vaccineGroup", "HepB", TestCasePropertyType.STRING);
 *     testcase.addImmunityObservationResult(new Date(), "070.30", "PROOF_OF_IMMUNITY", "IS_IMMUNE");
 *     SubstanceAdministrationEvent hepBComponent = testcase.getEvaluationSubstanceAdministrationEvent("45", "20080223", "VALID", "100", "");
 *     testcase.addSubstanceAdministrationEvent("45", "20080223", null, new SubstanceAdministrationEvent[]{hepBComponent});
 *     testcase.addSubstanceAdministrationProposal("100", "", "20090223", "100", "RECOMMENDED", new String[]{"DUE_NOW"});
 *     testcase.addSubstanceAdministrationProposal("810", "", "", "810", "NOT_RECOMMENDED", new String[]{"COMPLETE"});
 * </pre>
 *
 * @see TestCase
 * @see ObservationResult
 * @see SubstanceAdministrationEvent
 * @see SubstanceAdministrationProposal
 * @see CdsInput
 * @see CdsOutput
 * @author HLN Consulting, LLC
 */
public class TestCaseWrapper {

    /**
     * static logger.
     */
    protected static LogUtils logger = LogUtils.getLogger(TestCaseWrapper.class);
    final private TestCase testCase;
    private IceCdsInputWrapper input;
    private IceCdsOutputWrapper output;
    private String encodedName;
    private String fileLocation;
    private String errorMessage;

    /**
     * no args constructor
     */
    public TestCaseWrapper() {
        this(new TestCase());
    }

    /**
     * Construct the instance with a TestCase instance.
     *
     * @see TestCase
     * @param testCase the TestCase instance
     * @throws IllegalArgumentException if parameter testCase is null
     */
    public TestCaseWrapper(TestCase testCase) {
        if (testCase == null) {
            throw new IllegalArgumentException("testCase was null!");
        }
        this.testCase = testCase;
        input = new IceCdsInputWrapper(testCase.getCdsInput());
        output = new IceCdsOutputWrapper(testCase.getCdsOutput());
        if (testCase.getCdsInput() == null) {
            testCase.setCdsInput(input.getCdsInput());
        }
        if (testCase.getCdsOutput() == null) {
            testCase.setCdsOutput(output.getCdsOutput());
        }
        logger.debug("1testCase.getCdsInput()=" + testCase.getCdsInput());
        logger.debug("1testCase.getCdsOutput()=" + testCase.getCdsOutput());
        logger.debug("1input.getCdsInput()=" + input.getCdsInput());
        logger.debug("1output.getCdsOutput()=" + output.getCdsOutput());
    }

    /**
     * Construct the instance with a TestCase, CdsInput, and CdsOutput instance.
     *
     * @see TestCase
     * @see CdsInput
     * @see CdsOutput
     * @param testCase the TestCase instance
     * @param cdsInput the CdsInput instance
     * @param cdsOutput the CdsOutput instance
     * @throws IllegalArgumentException if any parameters are null
     */
    public TestCaseWrapper(TestCase testCase, CDSInput cdsInput, CDSOutput cdsOutput) {
        if (testCase == null) {
            throw new IllegalArgumentException("testCase was null!");
        }
        if (cdsInput == null) {
            throw new IllegalArgumentException("cdsInput was null!");
        }
        if (cdsOutput == null) {
            throw new IllegalArgumentException("cdsOutput was null!");
        }
        this.testCase = testCase;
        input = new IceCdsInputWrapper(cdsInput);
        output = new IceCdsOutputWrapper(cdsOutput);
        testCase.setCdsInput(input.getCdsInput());
        testCase.setCdsOutput(output.getCdsOutput());
        logger.debug("2testCase.getCdsInput()=" + testCase.getCdsInput());
        logger.debug("2testCase.getCdsOutput()=" + testCase.getCdsOutput());
        logger.debug("2input.getCdsInput()=" + input.getCdsInput());
        logger.debug("2output.getCdsOutput()=" + output.getCdsOutput());
    }

    /**
     * Factory-ish method for generating a TestCaseWrapper instance.
     *
     * @see TestCase
     * @see CdsInput
     * @see CdsOutput
     * @param testCase the TestCase instance
     * @param cdsInput the CdsInput instance
     * @param cdsOutput the CdsOutput instance
     * @return initialized instance of TestCaseWrapper
     * @throws IllegalArgumentException if any parameters are null
     */
    public static TestCaseWrapper getTestCaseWrapper(TestCase testCase, CDSInput cdsInput, CDSOutput cdsOutput) {
        return new TestCaseWrapper(testCase, cdsInput, cdsOutput);
    }

    /**
     * Factory-ish method for generating a TestCaseWrapper instance.
     *
     * @see TestCase
     * @param testCase the TestCase instance
     * @return initialized instance of TestCaseWrapper
     * @throws IllegalArgumentException if any parameters are null
     */
    public static TestCaseWrapper getTestCaseWrapper(TestCase testCase) {
        return new TestCaseWrapper(testCase);
    }

    /**
     * Factory-ish method for generating a TestCaseWrapper instance.
     *
     * @return initialized instance of TestCaseWrapper
     */
    public static TestCaseWrapper getTestCaseWrapper() {
        return new TestCaseWrapper();
    }

    /**
     * Returns the underlying JAXB generated class TestCase instance.
     *
     * @see TestCase
     * @return the TestCase instance
     */
    public TestCase getTestCase() {
        return testCase;
    }

    /**
     * Returns the underlying IceCdsInputWrapper instance.
     *
     * @see IceCdsInputWrapper
     * @return the IceCdsInputWrapper instance
     */
    public IceCdsInputWrapper getIceCdsInputWrapper() {
        return input;
    }

    /**
     * Returns the underlying CdsInputWrapper instance.
     *
     * @see CdsInputWrapper
     * @return the CdsInputWrapper instance
     */
    public CdsInputWrapper getCdsInputWrapper() {
        return input.getCdsInputWrapper();
    }

    /**
     * Returns the underlying CdsOutputWrapper instance.
     *
     * @see CdsOutputWrapper
     * @return the CdsOutputWrapper instance
     */
    public CdsOutputWrapper getCdsOutputWrapper() {
        return output.getCdsOutputWrapper();
    }

    /**
     * Returns the list of underlying JAXB generated class TestCaseProperty instances.
     *
     * @see TestCaseProperty
     * @return the List of TestCaseProperty instances
     */
    public List<TestCaseProperty> getProperties() {
        return testCase.getProperties();
    }

    /**
     * Returns an instance of TestCaseProperty for a given property name (if it exists) - otherwise returns null.
     *
     * @see TestCaseProperty
     * @param propertyName the name of the property
     * @return an instance of TestCaseProperty or null
     */
    public TestCaseProperty getProperty(String propertyName) {
        TestCaseProperty result = null;
        if (propertyName != null) {
            for (TestCaseProperty testCaseProperty : getProperties()) {
                if (propertyName.equals(testCaseProperty.getPropertyName())) {
                    result = testCaseProperty;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Add a property to the TestCase instance's list of properties with the given name/value/type.
     *
     * @see TestCaseProperty
     * @see TestCasePropertyType
     * @param name the property name
     * @param value the property value
     * @param testCasePropertyType the property type
     */
    public void addProperty(String name, String value, TestCasePropertyType testCasePropertyType) {
        TestCaseProperty testCaseProperty = new TestCaseProperty();
        testCaseProperty.setPropertyName(name);
        testCaseProperty.setPropertyValue(value);
        testCaseProperty.setPropertyType(testCasePropertyType.getTypeString());
        List<TestCaseProperty> properties = testCase.getProperties();
        properties.add(testCaseProperty);
    }

    /**
     * Sets the create ID. Uses the underlying TestCaseProperty instance as the storage mechanism.
     *
     * @see TestCaseProperty
     * @param createId the create ID
     */
    public void setCreateId(String createId) {
        addProperty("createId", createId, TestCasePropertyType.STRING);
    }

    /**
     * Gets the create ID. Uses the underlying TestCaseProperty instance as the storage mechanism.
     *
     * @see TestCaseProperty
     * @return the create ID
     */
    public String getCreateId() {
        String result = null;
        TestCaseProperty property = getProperty("createId");
        if (property != null && property.getPropertyValue() != null) {
            result = property.getPropertyValue();
        }
        return result;
    }

    /**
     * Sets the last modified ID. Uses the underlying TestCaseProperty instance as the storage mechanism.
     *
     * @see TestCaseProperty
     * @param lastModId the last modified ID
     */
    public void setLastModId(String lastModId) {
        addProperty("lastModId", lastModId, TestCasePropertyType.STRING);
    }

    /**
     * Gets the last modified ID. Uses the underlying TestCaseProperty instance as the storage mechanism.
     *
     * @see TestCaseProperty
     * @return the last modified ID
     */
    public String getLastModId() {
        String result = null;
        TestCaseProperty property = getProperty("lastModId");
        if (property != null && property.getPropertyValue() != null) {
            result = property.getPropertyValue();
        }
        return result;
    }

    /**
     * Sets the create date time. Uses the underlying TestCaseProperty instance as the storage mechanism.
     *
     * @see TestCaseProperty
     * @param createDatetime the create date time
     */
    public void setCreateDatetime(Date createDatetime) {
        addProperty("createDatetime", DateUtils.getISODatetimeFormat(createDatetime), TestCasePropertyType.STRING);
    }

    /**
     * Gets the create date time. Uses the underlying TestCaseProperty instance as the storage mechanism.
     *
     * @see TestCaseProperty
     * @return the create date time
     * @throws ParseException if the data format can't be parsed
     */
    public Date getCreateDatetime() throws ParseException {
        Date result = null;
        TestCaseProperty property = getProperty("createDatetime");
        if (property != null && property.getPropertyValue() != null) {
            result = DateUtils.parseISODatetimeFormat(property.getPropertyValue());
        }
        return result;
    }

    /**
     * Sets the last modified date time. Uses the underlying TestCaseProperty instance as the storage mechanism.
     *
     * @see TestCaseProperty
     * @param lastModDatetime the last modified date time
     */
    public void setLastModDatetime(Date lastModDatetime) {
        addProperty("lastModDatetime", DateUtils.getISODatetimeFormat(lastModDatetime), TestCasePropertyType.STRING);
    }

    /**
     * Gets the last modified date time. Uses the underlying TestCaseProperty instance as the storage mechanism.
     *
     * @see TestCaseProperty
     * @return the last modified date time
     * @throws ParseException if the data format can't be parsed
     */
    public Date getLastModDatetime() throws ParseException {
        Date result = null;
        TestCaseProperty property = getProperty("lastModDatetime");
        if (property != null && property.getPropertyValue() != null) {
            result = DateUtils.parseISODatetimeFormat(property.getPropertyValue());
        }
        return result;
    }

    /**
     * Gets the test suite name.
     *
     * @return the test suite name
     */
    public String getSuiteName() {
        return testCase.getSuiteName();
    }

    /**
     * Sets the test suite name.
     *
     * @param suiteName the test suite name
     */
    public void setSuiteName(String suiteName) {
        testCase.setSuiteName(suiteName);
    }

    /**
     * Gets the test group name.
     *
     * @return the test group name
     */
    public String getGroupName() {
        return testCase.getGroupName();
    }

    /**
     * Sets the test group name.
     *
     * @param groupName the test group name
     */
    public void setGroupName(String groupName) {
        testCase.setGroupName(groupName);
    }

    /**
     * Gets the test suite last modified date time. Uses the underlying TestCaseProperty instance as the storage mechanism.
     *
     * @see TestCaseProperty
     * @return the test suite last modified date time
     * @throws ParseException if the data format can't be parsed
     */
    public Date getSuiteLastModDatetime() throws ParseException {
        Date result = null;
        TestCaseProperty property = getProperty("suiteLastModDatetime");
        if (property != null && property.getPropertyValue() != null) {
            result = DateUtils.parseISODatetimeFormat(property.getPropertyValue());
        }
        return result;
    }

    /**
     * Sets the test suite last modified date time. Uses the underlying TestCaseProperty instance as the storage mechanism.
     *
     * @see TestCaseProperty
     * @param suiteLastModDatetime the test suite last modified date time
     */
    public void setSuiteLastModDatetime(Date suiteLastModDatetime) {
        addProperty("suiteLastModDatetime", DateUtils.getISODatetimeFormat(suiteLastModDatetime), TestCasePropertyType.STRING);
    }

    /**
     * Gets the test suite last modified ID. Uses the underlying TestCaseProperty instance as the storage mechanism.
     *
     * @see TestCaseProperty
     * @return the test suite last modified ID
     */
    public String getSuiteLastModId() {
        String result = null;
        TestCaseProperty property = getProperty("suiteLastModId");
        if (property != null && property.getPropertyValue() != null) {
            result = property.getPropertyValue();
        }
        return result;
    }

    /**
     * Sets the test suite last modified ID. Uses the underlying TestCaseProperty instance as the storage mechanism.
     *
     * @see TestCaseProperty
     * @param suiteLastModId the test suite last modified ID
     */
    public void setSuiteLastModId(String suiteLastModId) {
        addProperty("suiteLastModId", suiteLastModId, TestCasePropertyType.STRING);
    }

    /**
     * Gets the test suite create ID. Uses the underlying TestCaseProperty instance as the storage mechanism.
     *
     * @see TestCaseProperty
     * @return the test suite create ID
     */
    public String getSuiteCreateId() {
        String result = null;
        TestCaseProperty property = getProperty("suiteCreateId");
        if (property != null && property.getPropertyValue() != null) {
            result = property.getPropertyValue();
        }
        return result;
    }

    /**
     * Sets the test suite create ID. Uses the underlying TestCaseProperty instance as the storage mechanism.
     *
     * @see TestCaseProperty
     * @param suiteCreateId the test suite create ID
     */
    public void setSuiteCreateId(String suiteCreateId) {
        addProperty("suiteCreateId", suiteCreateId, TestCasePropertyType.STRING);
    }

    /**
     * Gets the test suite create date time. Uses the underlying TestCaseProperty instance as the storage mechanism.
     *
     * @see TestCaseProperty
     * @return the test suite create date time
     * @throws ParseException if the data format can't be parsed
     */
    public Date getSuiteCreateDatetime() throws ParseException {
        Date result = null;
        TestCaseProperty property = getProperty("suiteCreateDatetime");
        if (property != null && property.getPropertyValue() != null) {
            result = DateUtils.parseISODatetimeFormat(property.getPropertyValue());
        }
        return result;
    }

    /**
     * Sets the test suite create date time. Uses the underlying TestCaseProperty instance as the storage mechanism.
     *
     * @see TestCaseProperty
     * @param suiteCreateDatetime the test suite create date time
     */
    public void setSuiteCreateDatetime(Date suiteCreateDatetime) {
        addProperty("suiteCreateDatetime", DateUtils.getISODatetimeFormat(suiteCreateDatetime), TestCasePropertyType.STRING);
    }

    /**
     * Gets the test group last modified date time. Uses the underlying TestCaseProperty instance as the storage mechanism.
     *
     * @see TestCaseProperty
     * @return the test group last modified date time
     * @throws ParseException if the data format can't be parsed
     */
    public Date getGroupLastModDatetime() throws ParseException {
        Date result = null;
        TestCaseProperty property = getProperty("groupLastModDatetime");
        if (property != null && property.getPropertyValue() != null) {
            result = DateUtils.parseISODatetimeFormat(property.getPropertyValue());
        }
        return result;
    }

    /**
     * Sets the test group last modified date time. Uses the underlying TestCaseProperty instance as the storage mechanism.
     *
     * @see TestCaseProperty
     * @param groupLastModDatetime the test group last modified date time
     */
    public void setGroupLastModDatetime(Date groupLastModDatetime) {
        addProperty("groupLastModDatetime", DateUtils.getISODatetimeFormat(groupLastModDatetime), TestCasePropertyType.STRING);
    }

    /**
     * Gets the test group last modified ID. Uses the underlying TestCaseProperty instance as the storage mechanism.
     *
     * @see TestCaseProperty
     * @return the test group last modified ID
     */
    public String getGroupLastModId() {
        String result = null;
        TestCaseProperty property = getProperty("groupLastModId");
        if (property != null && property.getPropertyValue() != null) {
            result = property.getPropertyValue();
        }
        return result;
    }

    /**
     * Sets the test group last modified ID. Uses the underlying TestCaseProperty instance as the storage mechanism.
     *
     * @see TestCaseProperty
     * @param groupLastModId the test group last modified ID
     */
    public void setGroupLastModId(String groupLastModId) {
        addProperty("groupLastModId", groupLastModId, TestCasePropertyType.STRING);
    }

    /**
     * Gets the test group create ID. Uses the underlying TestCaseProperty instance as the storage mechanism.
     *
     * @see TestCaseProperty
     * @return the test group create ID
     */
    public String getGroupCreateId() {
        String result = null;
        TestCaseProperty property = getProperty("groupCreateId");
        if (property != null && property.getPropertyValue() != null) {
            result = property.getPropertyValue();
        }
        return result;
    }

    /**
     * Sets the test group create ID. Uses the underlying TestCaseProperty instance as the storage mechanism.
     *
     * @see TestCaseProperty
     * @param groupCreateId the test group create ID
     */
    public void setGroupCreateId(String groupCreateId) {
        addProperty("groupCreateId", groupCreateId, TestCasePropertyType.STRING);
    }

    /**
     * Gets the test group create date time. Uses the underlying TestCaseProperty instance as the storage mechanism.
     *
     * @see TestCaseProperty
     * @return the test group create date time
     * @throws ParseException if the data format can't be parsed
     */
    public Date getGroupCreateDatetime() throws ParseException {
        Date result = null;
        TestCaseProperty property = getProperty("groupCreateDatetime");
        if (property != null && property.getPropertyValue() != null) {
            result = DateUtils.parseISODatetimeFormat(property.getPropertyValue());
        }
        return result;
    }

    /**
     * Sets the test group create date time. Uses the underlying TestCaseProperty instance as the storage mechanism.
     *
     * @see TestCaseProperty
     * @param groupCreateDatetime the test group create date time
     */
    public void setGroupCreateDatetime(Date groupCreateDatetime) {
        addProperty("groupCreateDatetime", DateUtils.getISODatetimeFormat(groupCreateDatetime), TestCasePropertyType.STRING);
    }

    /**
     * Gets the state of the igonre property.
     *
     * @return the state of the igonre property
     */
    public boolean isIgnore() {
        return testCase.isIgnore();
    }

    /**
     * sets the state of the igonre property.
     *
     * @param ignore the state of the igonre property
     */
    public void setIgnore(boolean ignore) {
        testCase.setIgnore(ignore);
    }

    /**
     * Gets the execution date time.
     *
     * @return the execution date time
     */
    public String getExecutiondatetime() {
        return DateUtils.getISODateFormat(testCase.getExecutiondate());
    }

    /**
     * Gets the execution date.
     *
     * @return the execution date
     * @throws ParseException
     */
    public Date getExecutiondate() throws ParseException {
        return testCase.getExecutiondate();
    }

    /**
     * Sets the execution date.
     *
     * @param value the string value of the execution date
     * @throws DatatypeConfigurationException
     * @throws ParseException
     */
    public void setExecutiondate(String value) throws DatatypeConfigurationException, ParseException {
        testCase.setExecutiondate(DateUtils.parseISODateFormat(value));
    }

    /**
     * Sets the execution date.
     *
     * @param value the date object of the value
     * @throws DatatypeConfigurationException
     */
    public void setExecutiondate(Date value) throws DatatypeConfigurationException {
        testCase.setExecutiondate(value);
    }

    /**
     * Gets the test case name.
     *
     * @return the test case name
     */
    public String getName() {
        return testCase.getName();
    }

    /**
     * Sets the test case name.
     *
     * @param value the test case name
     */
    public void setName(String value) {
        testCase.setName(value);
    }

    /**
     * Gets the test case notes.
     *
     * @return the test case notes
     */
    public String getNotes() {
        return testCase.getNotes();
    }

    /**
     * Sets the test case notes.
     *
     * @param value the test case notes
     */
    public void setNotes(String value) {
        testCase.setNotes(value);
    }

    /**
     * Gets the test case rule to test value.
     *
     * @return the test case rule to test value
     */
    public String getRuletotest() {
        return testCase.getRuletotest();
    }

    /**
     * Sets the test case rule to test value.
     *
     * @param value the test case rule to test value
     */
    public void setRuletotest(String value) {
        testCase.setRuletotest(value);
    }

    /**
     * Gets the encoded name.
     *
     * @return the encoded name
     */
    public String getEncodedName() {
        return encodedName;
    }

    /**
     * Sets the encoded name.
     *
     * @param encodedName the encoded name
     */
    public void setEncodedName(String encodedName) {
        this.encodedName = encodedName;
    }

    /**
     * Gets the file location.
     *
     * @return the file location
     */
    public String getFileLocation() {
        return fileLocation;
    }

    /**
     * Sets the file location.
     *
     * @param fileLocation the file location
     */
    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    /**
     * Gets the error message.
     *
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the error message.
     *
     * @param errorMessage the error message
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Gets the patient birth date time.
     *
     * @return the patient birth date time
     */
    public String getPatientBirthTime() {
        TS birthTime = input.getCdsInput().getVmrInput().getPatient().getDemographics().getBirthTime();
        String birthtimeValue = null;
        if (birthTime != null) {
            birthtimeValue = birthTime.getValue();
        }
        return birthtimeValue;
    }

    /**
     * Gets the patient birth date.
     *
     * @return the patient birth date
     * @throws ParseException
     */
    public Date getPatientBirthDate() throws ParseException {
        return DateUtils.parseISODateFormat(getPatientBirthTime());
    }

    /**
     * Sets the patient birth date time.
     *
     * @param value the patient birth date time
     * @throws CdsException
     */
    public void setPatientBirthTime(String value) throws CdsException {
        input.setPatientBirthTime(value);
        output.setPatientBirthTime(value);
    }

    /**
     * Sets the patient birth date time.
     *
     * @param value the patient birth date
     * @throws CdsException
     */
    public void setPatientBirthTime(Date value) throws CdsException {
        input.setPatientBirthTime(value);
        output.setPatientBirthTime(value);
    }

    /**
     * Gets the patient gender.
     *
     * @return the patient gender
     */
    public CD getPatientGender() {
        return output.getCdsOutput().getVmrOutput().getPatient().getDemographics().getGender();
    }

    /**
     * Sets the patient gender.
     *
     * @param code
     * @param oid
     * @throws CdsException
     */
    public void setPatientGender(String code, String oid) throws CdsException {
        input.setPatientGender(code, oid);
        output.setPatientGender(code, oid);
    }

    /**
     * Gets the patient ID.
     *
     * @return the patient ID
     */
    public String getPatientId() {
        return output.getCdsOutput().getVmrOutput().getPatient().getId().getExtension();
    }

    /**
     * Sets the patient ID.
     *
     * The value is from the Config.getCodeSystemOid("PATIENT_ID") configured code system OID.
     *
     * @param value the patient ID
     * @throws CdsException
     */
    public void setPatientId(String value) throws CdsException {
        input.setPatientId(value);
        output.setPatientId(value);
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
     * @param validityCode the validity of the shot (optional)
     * @param validityOid the validity oid of the shot (optional)
     * @param focusCode the focus of the administration event
     * @param focusOid the focus oid of the administration event
     * @param interpretationCode the interpretation of the administration event
     * @param interpretationOid the interpretation oid of the administration event
     * @return a properly constructed SubstanceAdministrationEvent with an ObservationResult on it
     * @throws CdsException
     */
    public SubstanceAdministrationEvent getEvaluationSubstanceAdministrationEvent(
             String substanceCode,
            String substanceOid,
            String administrationTimeInterval,
            String validityCode,
            String validityOid,
            String focusCode,
            String focusOid,
            String interpretationCode,
            String interpretationOid) {
        return IceCdsObjectFactory.getEvaluationSubstanceAdministrationEvent(
                substanceCode,
                substanceOid,
                administrationTimeInterval,
                validityCode,
                validityOid,
                focusCode,
                focusOid,
                interpretationCode,
                interpretationOid);

    }

    /**
     * Generate a SubstanceAdministrationEvent object.
     *
     * This method is an ICE specific implementation for simplifying the creation of a SubstanceAdministrationEvent.
     *
     * This method does not add an event to the test case - it generates an SubstanceAdministrationEvent for a component of a
     * vaccine.
     *
     * @see SubstanceAdministrationEvent
     * @see org.cdsframework.util.support.cds.Config
     * @param substanceCode the substance code
     * @param substanceOid the substance code oid
     * @param administrationTimeIntervalDate the date time of the substance administration
     * @param validityCode the validity of the shot (optional)
     * @param validityOid the validity oid of the shot (optional)
     * @param focusCode the focus of the administration event
     * @param focusOid the focus oid of the administration event
     * @param interpretationCode the interpretation of the administration event
     * @param interpretationOid the interpretation oid of the administration event
     * @return a properly constructed SubstanceAdministrationEvent with an ObservationResult on it
     */
    public SubstanceAdministrationEvent getEvaluationSubstanceAdministrationEvent(
            String substanceCode,
            String substanceOid,
            Date administrationTimeIntervalDate,
            String validityCode,
            String validityOid,
            String focusCode,
            String focusOid,
            String interpretationCode,
            String interpretationOid) {
        return getEvaluationSubstanceAdministrationEvent(
                substanceCode,
                substanceOid,
                DateUtils.getISODateFormat(administrationTimeIntervalDate),
                validityCode,
                validityOid,
                focusCode,
                focusOid,
                interpretationCode,
                interpretationOid);
    }

    /**
     * Generate a SubstanceAdministrationEvent object.
     *
     * This method is an ICE specific implementation for simplifying the creation of a SubstanceAdministrationEvent.
     *
     * This method does not add an event to the test case - it generates an SubstanceAdministrationEvent for a component of a
     * vaccine.
     *
     * @see SubstanceAdministrationEvent
     * @see org.cdsframework.util.support.cds.Config
     * @param substanceCode the substance code
     * @param substanceOid the substance code oid
     * @param administrationTimeInterval the date time of the substance administration
     * @param validityCode the validity of the shot (optional)
     * @param validityOid the validity oid of the shot (optional)
     * @param focusCode the focus of the administration event
     * @param focusOid the focus oid of the administration event
     * @param reasons the interpretations of the administration event
     * @return a properly constructed SubstanceAdministrationEvent with an ObservationResult on it
     * @throws CdsException
     */
    public SubstanceAdministrationEvent getEvaluationSubstanceAdministrationEvent(
            String substanceCode,
            String substanceOid,
            String administrationTimeInterval,
            String validityCode,
            String validityOid,
            String focusCode,
            String focusOid,
            List<CD> reasons) throws CdsException {
        return IceCdsObjectFactory.getEvaluationSubstanceAdministrationEvent(
                substanceCode,
                substanceOid,
                administrationTimeInterval,
                validityCode,
                validityOid,
                focusCode,
                focusOid,
                reasons);
    }

    /**
     * Generate a SubstanceAdministrationEvent object.
     *
     * This method is an ICE specific implementation for simplifying the creation of a SubstanceAdministrationEvent.
     *
     * This method does not add an event to the test case - it generates an SubstanceAdministrationEvent for a component of a
     * vaccine.
     *
     * @see SubstanceAdministrationEvent
     * @see org.cdsframework.util.support.cds.Config
     * @param substanceCode the substance code
     * @param substanceOid the substance code oid
     * @param administrationTimeIntervalDate the date time of the substance administration
     * @param validityCode the validity of the shot (optional)
     * @param validityOid the validity oid of the shot (optional)
     * @param focusCode the focus of the administration event
     * @param focusOid the focus oid of the administration event
     * @param reasons the interpretations of the administration event
     * @return a properly constructed SubstanceAdministrationEvent with an ObservationResult on it
     * @throws CdsException
     */
    public SubstanceAdministrationEvent getEvaluationSubstanceAdministrationEvent(
            String substanceCode,
            String substanceOid,
            Date administrationTimeIntervalDate,
            String validityCode,
            String validityOid,
            String focusCode,
            String focusOid,
            List<CD> reasons) throws CdsException {
        return getEvaluationSubstanceAdministrationEvent(
                substanceCode,
                substanceOid,
                DateUtils.getISODateFormat(administrationTimeIntervalDate),
                validityCode,
                validityOid,
                focusCode,
                focusOid,
                reasons);
    }

    /**
     * Add a SubstanceAdministrationEvent to the test case.
     *
     * This method is an ICE specific implementation for simplifying the adding of a SubstanceAdministrationEvent to the test case.
     *
     * @see SubstanceAdministrationEvent
     * @see org.cdsframework.util.support.cds.Config
     * @param substanceCode the substance code
     * @param substanceCodeOid the substance code oid
     * @param administrationTimeInterval the date time of the substance administration
     * @param idExtension a unique ID identifying this particular administration event
     * @param idRoot the unique ID oid
     * @param components a list of the vaccine components represented by SubstanceAdministrationEvent objects
     * @return a properly constructed SubstanceAdministrationEvent with an ObservationResult on it
     * @throws CdsException
     */
    public SubstanceAdministrationEvent addSubstanceAdministrationEvent(
            String substanceCode,
            String substanceCodeOid,
            String administrationTimeInterval,
            String idExtension,
            String idRoot,
            List<SubstanceAdministrationEvent> components) throws CdsException {
        // the input (CdsInput) object doesn't need the compoents
        input.addSubstanceAdministrationEvent(
                substanceCode,
                substanceCodeOid,
                administrationTimeInterval,
                idRoot,
                idExtension);
        return output.addSubstanceAdministrationEvent(
                substanceCode,
                substanceCodeOid,
                administrationTimeInterval,
                idRoot,
                idExtension,
                components);
    }

    /**
     * Add a SubstanceAdministrationEvent to the test case.
     *
     * This method is an ICE specific implementation for simplifying the adding of a SubstanceAdministrationEvent to the test case.
     *
     * @see SubstanceAdministrationEvent
     * @see org.cdsframework.util.support.cds.Config
     * @param substanceCode the substance code
     * @param substanceCodeOid the substance code oid
     * @param administrationTimeIntervalDate the date time of the substance administration
     * @param idExtension a unique ID identifying this particular administration event
     * @param idRoot the unique ID oid
     * @param components a list of the vaccine components represented by SubstanceAdministrationEvent objects
     * @return a properly constructed SubstanceAdministrationEvent with an ObservationResult on it
     * @throws CdsException
     */
    public SubstanceAdministrationEvent addSubstanceAdministrationEvent(
            String substanceCode,
            String substanceCodeOid,
            Date administrationTimeIntervalDate,
            String idExtension,
            String idRoot,
            List<SubstanceAdministrationEvent> components) throws CdsException {
        return addSubstanceAdministrationEvent(
                substanceCode, substanceCodeOid,
                DateUtils.getISODateFormat(administrationTimeIntervalDate),
                idExtension, idRoot, components);
    }

    /**
     * Add a SubstanceAdministrationProposal to the test case.
     *
     * This method is an ICE specific implementation for simplifying the adding of a SubstanceAdministrationProposal to the test
     * case.
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
     * @param reasons the interpretations of the administration event
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
            List<CD> reasons) {
        return output.addSubstanceAdministrationProposal(
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

    /**
     * Add a SubstanceAdministrationProposal to the test case.
     *
     * This method is an ICE specific implementation for simplifying the adding of a SubstanceAdministrationProposal to the test
     * case.
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
     * @param reasons the interpretations of the administration event
     * @return a properly constructed SubstanceAdministrationProposal
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
            List<CD> reasons) {
        return output.addSubstanceAdministrationProposal(
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

    /**
     * Add a SubstanceAdministrationProposal to the test case.
     *
     * This method is an ICE specific implementation for simplifying the adding of a SubstanceAdministrationProposal to the test
     * case.
     *
     * @see SubstanceAdministrationProposal
     * @see org.cdsframework.util.support.cds.Config
     * @param vaccineGroup the vaccine group code
     * @param vaccineGroupOid the vaccine group oid
     * @param substanceCode the substance code
     * @param substanceOid the substance oid
     * @param proposedAdministrationTimeIntervalLowDate
     * @param proposedAdministrationTimeIntervalHighDate
     * @param validAdministrationTimeIntervalLowDate
     * @param validAdministrationTimeIntervalHighDate
     * @param focusCode the focus of the administration event
     * @param focusOid the focus oid of the administration event
     * @param valueCode the recommendation value
     * @param valueOid the recommendation value oid
     * @param reasons the interpretations of the administration event
     * @return a properly constructed SubstanceAdministrationProposal
     */
    public SubstanceAdministrationProposal addSubstanceAdministrationProposal(
            String vaccineGroup,
            String vaccineGroupOid,
            String substanceCode,
            String substanceOid,
            Date proposedAdministrationTimeIntervalLowDate,
            Date proposedAdministrationTimeIntervalHighDate,
            Date validAdministrationTimeIntervalLowDate,
            Date validAdministrationTimeIntervalHighDate,
            String focusCode,
            String focusOid,
            String valueCode,
            String valueOid,
            List<CD> reasons) {
        return addSubstanceAdministrationProposal(
                vaccineGroup, vaccineGroupOid,
                substanceCode, substanceOid,
                DateUtils.getISODateFormat(proposedAdministrationTimeIntervalLowDate),
                DateUtils.getISODateFormat(proposedAdministrationTimeIntervalHighDate),
                DateUtils.getISODateFormat(validAdministrationTimeIntervalLowDate),
                DateUtils.getISODateFormat(validAdministrationTimeIntervalHighDate),
                focusCode, focusOid,
                valueCode, valueOid,
                reasons);
    }

    /**
     * Add a SubstanceAdministrationProposal to the test case.
     *
     * This method is an ICE specific implementation for simplifying the adding of a SubstanceAdministrationProposal to the test
     * case.
     *
     * @see SubstanceAdministrationProposal
     * @see org.cdsframework.util.support.cds.Config
     * @param vaccineGroup the vaccine group code
     * @param vaccineGroupOid the vaccine group oid
     * @param substanceCode the substance code
     * @param substanceOid the substance oid
     * @param administrationTimeIntervalDate the date time of the substance administration
     * @param focusCode the focus of the administration event
     * @param focusOid the focus oid of the administration event
     * @param valueCode the recommendation value
     * @param valueOid the recommendation value oid
     * @param reasons the interpretations of the administration event
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
            List<CD> reasons) {
        return addSubstanceAdministrationProposal(
                vaccineGroup, vaccineGroupOid,
                substanceCode, substanceOid,
                DateUtils.getISODateFormat(administrationTimeIntervalDate),
                focusCode, focusOid,
                valueCode, valueOid,
                reasons);
    }

    /**
     * Gets the list of observation results on the test case.
     *
     * @see ObservationResult
     * @return the list of observation results
     * @throws CdsException
     */
    public List<ObservationResult> getImmunityObservationResults() throws CdsException {
        return input.getImmunityObservationResults();
    }

    /**
     * Gets the list of SubstanceAdministrationProposals on the test case.
     *
     * @see SubstanceAdministrationProposal
     * @return the list of SubstanceAdministrationProposals
     * @throws CdsException
     */
    public List<SubstanceAdministrationProposal> getSubstanceAdministrationProposals() throws CdsException {
        return output.getSubstanceAdministrationProposals();
    }

    /**
     * Gets the list of SubstanceAdministrationEvents on the test case.
     *
     * @see SubstanceAdministrationEvent
     * @return the list of SubstanceAdministrationEvents
     * @throws CdsException
     */
    public List<SubstanceAdministrationEvent> getSubstanceAdministrationEvents() throws CdsException {
        return output.getSubstanceAdministrationEvents();
    }

    public void addImmunityObservationResult(
            Date observationEventTime,
            String offset,
            String focusCode,
            String focusOid,
            String valueCode,
            String valueOid,
            String interpretationCode,
            String interpretationOid) {
        input.addImmunityObservationResult(
                observationEventTime, offset,
                focusCode, focusOid,
                valueCode, valueOid,
                interpretationCode, interpretationOid);
        output.addImmunityObservationResult(
                observationEventTime, offset,
                focusCode, focusOid,
                valueCode, valueOid,
                interpretationCode, interpretationOid);
    }

    public String getVaccineCodeFromGroup() throws CdsException {

        if (testCase.getGroupName()==null || testCase.getGroupName().length()==0) {
            throw new CdsException("Group name is null or empty");

        }

        String[] groupArr = testCase.getGroupName().split("-");

        if (groupArr.length == 0) {
            return makeSafe(testCase.getGroupName());

        }

        return groupArr[0].replace(" ", "");

    }

    private String makeSafe(String text) {
        String subDirName = text.replaceAll(">=", "GTE");
        subDirName = subDirName.replaceAll("\\(", "_");
        subDirName = subDirName.replaceAll("\\)", "_");
        subDirName = subDirName.replaceAll("<=", "LTE");
        subDirName = subDirName.replaceAll("=>", "GTE");
        subDirName = subDirName.replaceAll("=<", "LTE");
        subDirName = subDirName.replaceAll(">", "GT");
        subDirName = subDirName.replaceAll("<", "LT");
        subDirName = subDirName.replaceAll("=", "EQ");
        subDirName = subDirName.replaceAll("\\+", "PLUS");

        subDirName = subDirName.replaceAll("[^a-zA-Z0-9\\-]", "_");

        subDirName = subDirName.replaceAll("__", "_");
        subDirName = subDirName.replaceAll("_$", "");

        return subDirName;
    }
}
