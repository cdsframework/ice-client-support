/**
 * The cdsframework support testcase imports excel project implements some specific excel based test case importer functionality.
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
package org.cdsframework.cds.testcase.importers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.cdsframework.cds.util.CdsObjectFactory;
import org.cdsframework.enumeration.TestCasePropertyType;
import org.cdsframework.exceptions.CdsException;
import org.cdsframework.ice.testcase.TestCaseWrapper;
import org.cdsframework.util.LogUtils;
import org.cdsframework.util.support.cds.Config;
import org.opencds.support.util.DateUtils;
import org.opencds.vmr.v1_0.schema.CD;
import org.opencds.vmr.v1_0.schema.ObservationResult;
import org.opencds.vmr.v1_0.schema.RelatedClinicalStatement;
import org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent;

/**
 *
 * @author HLN Consulting, LLC
 */
public class XlsxV1Helper {

    private final static LogUtils logger = LogUtils.getLogger(XlsxV1Helper.class);

    public static void importFromWorkBook(XSSFWorkbook wb, TestImportCallback callback)
            throws CdsException, FileNotFoundException, IOException, DatatypeConfigurationException {
        List<String> importedTestCases = new ArrayList<String>();
        XSSFSheet sheet = wb.getSheet("HepB Test Coverage Summary");
        if (sheet == null) {
            throw new CdsException("Bad format - missing HepB Test Coverage Summary sheet");
        }
        int lastRowNum = sheet.getLastRowNum();
        logger.debug("LastRowNum: " + lastRowNum);
        int i = 0;
        int testCount = 0;
        while (i < lastRowNum) {
            i++;
            XSSFRow row = sheet.getRow(i);
            if (row != null) {
                XSSFCell cell = row.getCell(0);
                String cellFormula = cell.getCellFormula();
                String testSheetName = cellFormula.substring(1, cellFormula.indexOf("!") - 1);
                XSSFSheet testSheet = wb.getSheet(testSheetName);
                String testSheetCellStart = cellFormula.substring(cellFormula.indexOf("!") + 1);
                int testCurrentRowNum = Integer.parseInt(testSheetCellStart.substring(3)) - 1;
                XSSFRow testCurrentRow = testSheet.getRow(testCurrentRowNum);
                XSSFCell testNumberCell = testCurrentRow.getCell(0);
                double testNumber = testNumberCell.getNumericCellValue();
                XSSFCell name = testCurrentRow.getCell(2);
                if (!name.getStringCellValue().isEmpty()) {
                    String localName = name.getStringCellValue();
                    String globalName = testSheetName + ": " + localName;
                    testCount++;
                    TestCaseWrapper testCase = TestCaseWrapper.getTestCaseWrapper();

                    // change to filename, tab name, test #, row #
                    String location = testSheetName + ", test # " + (int) testNumber + ", row # " + (testCurrentRowNum + 1);
                    testCase.setFileLocation(location);

                    logger.debug(location);

                    // test name + vaccine group
                    testCase.setName(localName);
                    String encodedName = XlsxImporterUtils.getShaHashFromString(globalName);
                    testCase.setEncodedName(encodedName);
                    if (importedTestCases.contains(encodedName)) {
                        int c = 1;
                        while (importedTestCases.contains(encodedName)) {
                            localName = localName + "(" + c + ")";
                            globalName = testSheetName + ": " + localName;
                            encodedName = XlsxImporterUtils.getShaHashFromString(globalName);
                            testCase.setName(localName);
                            testCase.setEncodedName(encodedName);
                        }
                    }
                    importedTestCases.add(encodedName);
                    logger.debug("    Test name: " + testCase.getName());
                    XSSFCell vaccineGroupCell = testCurrentRow.getCell(10);
                    double numericCellValue = vaccineGroupCell.getNumericCellValue();
                    String vaccineGroup = String.valueOf((int) numericCellValue);
                    logger.debug("    Vaccine group: " + vaccineGroup);

                    // test focus
                    testCurrentRowNum++;
                    testCurrentRow = testSheet.getRow(testCurrentRowNum);
                    XSSFCell focus = testCurrentRow.getCell(2);
                    String testFocus = focus.getStringCellValue();
                    logger.debug("    Test focus: " + testFocus);

                    // test series
                    XSSFCell series = testCurrentRow.getCell(9);
                    String testSeries = series.getStringCellValue();
                    logger.debug("    Series: " + testSeries);

                    testCase.addProperty("vaccineGroup", vaccineGroup, TestCasePropertyType.STRING);
                    testCase.addProperty("series", testSeries, TestCasePropertyType.STRING);

                    // dose number focus
                    testCurrentRowNum++;
                    testCurrentRow = testSheet.getRow(testCurrentRowNum);
                    XSSFCell doseFocusCell = testCurrentRow.getCell(2);
                    String doseFocus = doseFocusCell.getStringCellValue();
                    logger.debug("    Dose focus: " + doseFocus);

                    // rule to test description
                    testCurrentRowNum++;
                    testCurrentRow = testSheet.getRow(testCurrentRowNum);
                    XSSFCell ruleToTest = testCurrentRow.getCell(2);
                    testCase.setRuletotest(ruleToTest.getStringCellValue());
                    logger.debug("    Rule to test: " + testCase.getRuletotest());

                    // notes
                    testCurrentRowNum++;
                    testCurrentRow = testSheet.getRow(testCurrentRowNum);
                    XSSFCell testNotes = testCurrentRow.getCell(2);
                    testCase.setNotes(testNotes.getStringCellValue());
                    logger.debug("    Test notes: " + testCase.getNotes());

                    // immunityValue
                    testCurrentRowNum += 3;
                    testCurrentRow = testSheet.getRow(testCurrentRowNum);
                    XSSFCell immune = testCurrentRow.getCell(2);
                    String immunityValue = "PROOF_OF_IMMUNITY";
                    String immunityFocus = null;
                    if ("100".equals(vaccineGroup) || "070.30".equals(vaccineGroup)) {
                        immunityFocus = "070.30";
                    } else if ("810".equals(vaccineGroup) || "070.1".equals(vaccineGroup)) {
                        immunityFocus = "070.1";
                    } else if ("500".equals(vaccineGroup)) {
                        // need to be able to report separate MMR components
                        immunityFocus = "055.9";
                    } else if ("055.9".equals(vaccineGroup)) {
                        immunityFocus = "055.9";
                    } else if ("600".equals(vaccineGroup) || "052.9".equals(vaccineGroup)) {
                        immunityFocus = "052.9";
                    } else if ("072.9".equals(vaccineGroup)) {
                        immunityFocus = "072.9";
                    } else if ("056.9".equals(vaccineGroup)) {
                        immunityFocus = "056.9";
                    }
                    try {
                        Date dateCellValue = immune.getDateCellValue();
                        if (dateCellValue != null) {
                            testCase.addImmunityObservationResult(
                                    dateCellValue, null,
                                    immunityFocus,
                                    Config.getCodeSystemOid("DISEASE"),
                                    immunityValue,
                                    Config.getCodeSystemOid("IMMUNITY_VALUE"),
                                    "IS_IMMUNE",
                                    Config.getCodeSystemOid("IMMUNITY_INTERPRETATION")
                            );
                        }
                    } catch (Exception e) {
                        if (immune.getStringCellValue() != null && !immune.getStringCellValue().trim().isEmpty()) {
                            testCase.addImmunityObservationResult(new Date(), null,
                                    immunityFocus,
                                    Config.getCodeSystemOid("DISEASE"),
                                    immunityValue,
                                    Config.getCodeSystemOid("IMMUNITY_VALUE"),
                                    "IS_IMMUNE",
                                    Config.getCodeSystemOid("IMMUNITY_INTERPRETATION")
                            );
                        }
                    }
                    logger.debug("    Immune: " + immunityFocus + " - " + immunityValue);

                    // set the execution date
                    testCurrentRowNum += 2;
                    testCurrentRow = testSheet.getRow(testCurrentRowNum);
                    XSSFCell executionDate = testCurrentRow.getCell(2);
                    if (executionDate != null && executionDate.getDateCellValue() != null) {
                        testCase.setExecutiondate(executionDate.getDateCellValue());
                        logger.debug("    Execution date: " + testCase.getExecutiondatetime());
                    } else {
                        logger.warn("Execution Date is null.");
                    }

                    // add the recommendation via the substanceAdministrationProposal and observationResult
                    XSSFCell recommendation = testCurrentRow.getCell(8);
                    logger.debug("    Recommendation: " + recommendation);
                    XSSFCell recommendationReason = testCurrentRow.getCell(9);
                    logger.debug("    Reason for recommendation: " + recommendationReason);
                    XSSFCell dueDate = testCurrentRow.getCell(10);
                    logger.debug("    Date due: " + dueDate);

                    // convert DOB to ISO format and add to both vmr output and input
                    testCurrentRowNum++;
                    testCurrentRow = testSheet.getRow(testCurrentRowNum);
                    XSSFCell dob = testCurrentRow.getCell(2);
                    if (dob != null && dob.getDateCellValue() != null) {
                        testCase.setPatientBirthTime(dob.getDateCellValue());
                        logger.debug("    DOB: " + testCase.getPatientBirthTime());
                    } else {
                        logger.warn("DOB is null.");
                    }

                    // add the gender to both the vmr output and input
                    testCurrentRowNum++;
                    testCurrentRow = testSheet.getRow(testCurrentRowNum);
                    XSSFCell gender = testCurrentRow.getCell(2);
                    String genderValue = gender.getStringCellValue();
                    if ("female".equalsIgnoreCase(genderValue)) {
                        genderValue = "F";
                    } else if ("male".equalsIgnoreCase(genderValue)) {
                        genderValue = "M";
                    } else {
                        logger.error("Gender unsupported - setting to UN: " + genderValue + " @ " + location);
                        genderValue = "UN";
                    }
                    testCase.setPatientGender(genderValue, Config.getCodeSystemOid("GENDER"));
                    logger.debug("    Gender: " + testCase.getPatientGender());

                    // not in use yet
                    XSSFCell recommendedVaccine = testCurrentRow.getCell(9);
                    logger.debug("    Recommended vaccine: " + recommendedVaccine);
                    String recommendationReasonValue = recommendationReason.getStringCellValue();
                    List<CD> reasons = new ArrayList<CD>();
                    if (recommendationReasonValue != null && !recommendationReasonValue.trim().isEmpty()) {
                        reasons.add(CdsObjectFactory.getCD(recommendationReasonValue, Config.getCodeSystemOid("RECOMMENDATION_INTERPRETATION")));
                    }
//                    if ("DUE_IN_FUTURE".equalsIgnoreCase(recommendationReasonValue)) {
//                        recommendationReasonValue = "FUTURE_RECOMMENDED";
//                    }
                    try {
                        testCase.addSubstanceAdministrationProposal(
                                vaccineGroup,
                                Config.getCodeSystemOid("VACCINE_GROUP"),
                                recommendedVaccine.getStringCellValue(),
                                Config.getCodeSystemOid("VACCINE"),
                                DateUtils.getISODateFormat(dueDate.getDateCellValue()),
                                vaccineGroup,
                                Config.getCodeSystemOid("VACCINE_GROUP"),
                                recommendation.getStringCellValue(),
                                Config.getCodeSystemOid("RECOMMENDATION_VALUE"),
                                reasons);
                    } catch (Exception e) {
                        logger.error("addSubstanceAdministrationProposal Error @ " + location);
                        logger.error(e);
                        testCase.setErrorMessage(e.getMessage());
                        callback.callback(testCase, testSheetName, false);
                        continue;
                    }

                    testCurrentRowNum++;
                    testCurrentRow = testSheet.getRow(testCurrentRowNum);
                    XSSFCell shot1Vaccine = testCurrentRow.getCell(2);
                    logger.trace("    Shot 1 vaccine: " + shot1Vaccine);
                    XSSFCell shot1Cvx = testCurrentRow.getCell(3);
                    String shot1CvxCode = shot1Cvx.getRawValue();
                    if (shot1CvxCode != null && !shot1CvxCode.isEmpty()) {
                        logger.trace("    Shot 1 CVX: " + shot1Cvx.getRawValue());
                        XSSFCell shot1Evaluation = testCurrentRow.getCell(4);
                        logger.trace("    Shot 1 evaluation: " + shot1Evaluation);
                        XSSFCell shot1InvalidReason1Code = testCurrentRow.getCell(5);
                        logger.trace("    Shot 1 invalid reason 1 code: " + shot1InvalidReason1Code);
                        XSSFCell shot1InvalidReason2Code = testCurrentRow.getCell(6);
                        logger.trace("    Shot 1 invalid reason 2 code: " + shot1InvalidReason2Code);
                        XSSFCell shot1InvalidReason3Code = testCurrentRow.getCell(7);
                        logger.trace("    Shot 1 invalid reason 3 code: " + shot1InvalidReason3Code);
                        testCurrentRowNum++;
                        testCurrentRow = testSheet.getRow(testCurrentRowNum);
                        XSSFCell shot1DateofAdministration = testCurrentRow.getCell(2);
                        logger.trace("    Shot 1 date of administration: " + shot1DateofAdministration);
                        XSSFCell recommendedReasonText = testCurrentRow.getCell(9);
                        logger.trace("    Recommended reason text: " + recommendedReasonText.getStringCellValue());

                        try {
                            String reason1 = shot1InvalidReason1Code.getStringCellValue();
                            String reason2 = shot1InvalidReason2Code.getStringCellValue();
                            String reason3 = shot1InvalidReason3Code.getStringCellValue();
                            reasons = new ArrayList<CD>();
                            if (reason1 != null && !reason1.trim().isEmpty()) {
                                reasons.add(CdsObjectFactory.getCD(reason1, Config.getCodeSystemOid("EVALUATION_INTERPRETATION")));
                            }
                            if (reason2 != null && !reason2.trim().isEmpty()) {
                                reasons.add(CdsObjectFactory.getCD(reason2, Config.getCodeSystemOid("EVALUATION_INTERPRETATION")));
                            }
                            if (reason3 != null && !reason3.trim().isEmpty()) {
                                reasons.add(CdsObjectFactory.getCD(reason3, Config.getCodeSystemOid("EVALUATION_INTERPRETATION")));
                            }
                            SubstanceAdministrationEvent shot1EvaluationEvent
                                    = testCase.getEvaluationSubstanceAdministrationEvent(
                                            shot1CvxCode,
                                            Config.getCodeSystemOid("VACCINE"),
                                            shot1DateofAdministration.getDateCellValue(),
                                            shot1Evaluation.getStringCellValue().toUpperCase(),
                                            Config.getCodeSystemOid("EVALUATION_VALUE"),
                                            vaccineGroup,
                                            Config.getCodeSystemOid("VACCINE_GROUP"),
                                            reasons);
                            List<SubstanceAdministrationEvent> components = new ArrayList<SubstanceAdministrationEvent>();
                            components.add(shot1EvaluationEvent);
                            testCase.addSubstanceAdministrationEvent(
                                    shot1CvxCode,
                                    Config.getCodeSystemOid("VACCINE"),
                                    shot1DateofAdministration.getDateCellValue(),
                                    null,
                                    Config.getCodeSystemOid("ADMINISTRATION_ID"),
                                    components);
                        } catch (Exception e) {
                            logger.error("addSubstanceAdministrationEvent shot 1 Error @ " + location);
                            logger.error(e);
                            testCase.setErrorMessage(e.getMessage());
                            callback.callback(testCase, testSheetName, false);
                            continue;
                        }
                    }

                    testCurrentRowNum++;
                    testCurrentRow = testSheet.getRow(testCurrentRowNum);
                    XSSFCell shot2Vaccine = testCurrentRow.getCell(2);
                    logger.trace("    Shot 2 vaccine: " + shot2Vaccine);
                    XSSFCell shot2Cvx = testCurrentRow.getCell(3);
                    String shot2CvxCode = shot2Cvx.getRawValue();
                    if (shot2CvxCode != null && !shot2CvxCode.isEmpty()) {
                        logger.trace("    Shot 2 CVX: " + shot2Cvx.getRawValue());
                        XSSFCell shot2Evaluation = testCurrentRow.getCell(4);
                        logger.trace("    Shot 2 evaluation: " + shot2Evaluation);
                        XSSFCell shot2InvalidReason1Code = testCurrentRow.getCell(5);
                        logger.trace("    Shot 2 invalid reason 1 code: " + shot2InvalidReason1Code);
                        XSSFCell shot2InvalidReason2Code = testCurrentRow.getCell(6);
                        logger.trace("    Shot 2 invalid reason 2 code: " + shot2InvalidReason2Code);
                        XSSFCell shot2InvalidReason3Code = testCurrentRow.getCell(7);
                        logger.trace("    Shot 2 invalid reason 3 code: " + shot2InvalidReason3Code);
                        testCurrentRowNum++;
                        testCurrentRow = testSheet.getRow(testCurrentRowNum);
                        XSSFCell shot2DateofAdministration = testCurrentRow.getCell(2);
                        logger.trace("    Shot 2 date of administration: " + shot2DateofAdministration);

                        try {
                            String reason1 = shot2InvalidReason1Code.getStringCellValue();
                            String reason2 = shot2InvalidReason2Code.getStringCellValue();
                            String reason3 = shot2InvalidReason3Code.getStringCellValue();
                            reasons = new ArrayList<CD>();
                            if (reason1 != null && !reason1.trim().isEmpty()) {
                                reasons.add(CdsObjectFactory.getCD(reason1, Config.getCodeSystemOid("EVALUATION_INTERPRETATION")));
                            }
                            if (reason2 != null && !reason2.trim().isEmpty()) {
                                reasons.add(CdsObjectFactory.getCD(reason2, Config.getCodeSystemOid("EVALUATION_INTERPRETATION")));
                            }
                            if (reason3 != null && !reason3.trim().isEmpty()) {
                                reasons.add(CdsObjectFactory.getCD(reason3, Config.getCodeSystemOid("EVALUATION_INTERPRETATION")));
                            }
                            SubstanceAdministrationEvent shot2EvaluationEvent
                                    = testCase.getEvaluationSubstanceAdministrationEvent(
                                            shot2CvxCode,
                                            Config.getCodeSystemOid("VACCINE"),
                                            shot2DateofAdministration.getDateCellValue(),
                                            shot2Evaluation.getStringCellValue().toUpperCase(),
                                            Config.getCodeSystemOid("EVALUATION_VALUE"),
                                            vaccineGroup,
                                            Config.getCodeSystemOid("VACCINE_GROUP"),
                                            reasons);
                            List<SubstanceAdministrationEvent> components = new ArrayList<SubstanceAdministrationEvent>();
                            components.add(shot2EvaluationEvent);
                            testCase.addSubstanceAdministrationEvent(
                                    shot2CvxCode,
                                    Config.getCodeSystemOid("VACCINE"),
                                    shot2DateofAdministration.getDateCellValue(),
                                    null,
                                    Config.getCodeSystemOid("ADMINISTRATION_ID"),
                                    components);
                        } catch (CdsException e) {
                            logger.error("addSubstanceAdministrationEvent shot 2 Error @ " + location);
                            logger.error(e);
                            testCase.setErrorMessage(e.getMessage());
                            callback.callback(testCase, testSheetName, false);
                            continue;
                        }
                    }

                    testCurrentRowNum++;
                    testCurrentRow = testSheet.getRow(testCurrentRowNum);
                    XSSFCell shot3Vaccine = testCurrentRow.getCell(2);
                    logger.trace("    Shot 3 vaccine: " + shot3Vaccine);
                    XSSFCell shot3Cvx = testCurrentRow.getCell(3);
                    String shot3CvxCode = shot3Cvx.getRawValue();
                    if (shot3CvxCode != null && !shot3CvxCode.isEmpty()) {
                        logger.trace("    Shot 3 CVX: " + shot3Cvx.getRawValue());
                        XSSFCell shot3Evaluation = testCurrentRow.getCell(4);
                        logger.trace("    Shot 3 evaluation: " + shot3Evaluation);
                        XSSFCell shot3InvalidReason1Code = testCurrentRow.getCell(5);
                        logger.trace("    Shot 3 invalid reason 1 code: " + shot3InvalidReason1Code);
                        XSSFCell shot3InvalidReason2Code = testCurrentRow.getCell(6);
                        logger.trace("    Shot 3 invalid reason 2 code: " + shot3InvalidReason2Code);
                        XSSFCell shot3InvalidReason3Code = testCurrentRow.getCell(7);
                        logger.trace("    Shot 3 invalid reason 3 code: " + shot3InvalidReason3Code);
                        testCurrentRowNum++;
                        testCurrentRow = testSheet.getRow(testCurrentRowNum);
                        XSSFCell shot3DateofAdministration = testCurrentRow.getCell(2);
                        logger.trace("    Shot 3 date of administration: " + shot3DateofAdministration);

                        try {
                            String reason1 = shot3InvalidReason1Code.getStringCellValue();
                            String reason2 = shot3InvalidReason2Code.getStringCellValue();
                            String reason3 = shot3InvalidReason3Code.getStringCellValue();
                            reasons = new ArrayList<CD>();
                            if (reason1 != null && !reason1.trim().isEmpty()) {
                                reasons.add(CdsObjectFactory.getCD(reason1, Config.getCodeSystemOid("EVALUATION_INTERPRETATION")));
                            }
                            if (reason2 != null && !reason2.trim().isEmpty()) {
                                reasons.add(CdsObjectFactory.getCD(reason2, Config.getCodeSystemOid("EVALUATION_INTERPRETATION")));
                            }
                            if (reason3 != null && !reason3.trim().isEmpty()) {
                                reasons.add(CdsObjectFactory.getCD(reason3, Config.getCodeSystemOid("EVALUATION_INTERPRETATION")));
                            }
                            SubstanceAdministrationEvent shot3EvaluationEvent
                                    = testCase.getEvaluationSubstanceAdministrationEvent(
                                            shot3CvxCode,
                                            Config.getCodeSystemOid("VACCINE"),
                                            shot3DateofAdministration.getDateCellValue(),
                                            shot3Evaluation.getStringCellValue().toUpperCase(),
                                            Config.getCodeSystemOid("EVALUATION_VALUE"),
                                            vaccineGroup,
                                            Config.getCodeSystemOid("VACCINE_GROUP"),
                                            reasons);
                            List<SubstanceAdministrationEvent> components = new ArrayList<SubstanceAdministrationEvent>();
                            components.add(shot3EvaluationEvent);
                            testCase.addSubstanceAdministrationEvent(
                                    shot3CvxCode,
                                    Config.getCodeSystemOid("VACCINE"),
                                    shot3DateofAdministration.getDateCellValue(),
                                    null,
                                    Config.getCodeSystemOid("ADMINISTRATION_ID"),
                                    components);
                        } catch (CdsException e) {
                            logger.error("addSubstanceAdministrationEvent shot 3 Error @ " + location);
                            logger.error(e);
                            testCase.setErrorMessage(e.getMessage());
                            callback.callback(testCase, testSheetName, false);
                            continue;
                        }
                    }

                    testCurrentRowNum++;
                    testCurrentRow = testSheet.getRow(testCurrentRowNum);
                    XSSFCell shot4Vaccine = testCurrentRow.getCell(2);
                    logger.trace("    Shot 4 vaccine: " + shot4Vaccine);
                    XSSFCell shot4Cvx = testCurrentRow.getCell(3);
                    String shot4CvxCode = shot4Cvx.getRawValue();
                    if (shot4CvxCode != null && !shot4CvxCode.isEmpty()) {
                        logger.trace("    Shot 4 CVX: " + shot4Cvx.getRawValue());
                        XSSFCell shot4Evaluation = testCurrentRow.getCell(4);
                        logger.trace("    Shot 4 evaluation: " + shot4Evaluation);
                        XSSFCell shot4InvalidReason1Code = testCurrentRow.getCell(5);
                        logger.trace("    Shot 4 invalid reason 1 code: " + shot4InvalidReason1Code);
                        XSSFCell shot4InvalidReason2Code = testCurrentRow.getCell(6);
                        logger.trace("    Shot 4 invalid reason 2 code: " + shot4InvalidReason2Code);
                        XSSFCell shot4InvalidReason3Code = testCurrentRow.getCell(7);
                        logger.trace("    Shot 4 invalid reason 3 code: " + shot4InvalidReason3Code);
                        testCurrentRowNum++;
                        testCurrentRow = testSheet.getRow(testCurrentRowNum);
                        XSSFCell shot4DateofAdministration = testCurrentRow.getCell(2);
                        logger.trace("    Shot 4 date of administration: " + shot4DateofAdministration);

                        try {
                            String reason1 = shot4InvalidReason1Code.getStringCellValue();
                            String reason2 = shot4InvalidReason2Code.getStringCellValue();
                            String reason3 = shot4InvalidReason3Code.getStringCellValue();
                            reasons = new ArrayList<CD>();
                            if (reason1 != null && !reason1.trim().isEmpty()) {
                                reasons.add(CdsObjectFactory.getCD(reason1, Config.getCodeSystemOid("EVALUATION_INTERPRETATION")));
                            }
                            if (reason2 != null && !reason2.trim().isEmpty()) {
                                reasons.add(CdsObjectFactory.getCD(reason2, Config.getCodeSystemOid("EVALUATION_INTERPRETATION")));
                            }
                            if (reason3 != null && !reason3.trim().isEmpty()) {
                                reasons.add(CdsObjectFactory.getCD(reason3, Config.getCodeSystemOid("EVALUATION_INTERPRETATION")));
                            }
                            SubstanceAdministrationEvent shot4EvaluationEvent
                                    = testCase.getEvaluationSubstanceAdministrationEvent(
                                            shot4CvxCode,
                                            Config.getCodeSystemOid("VACCINE"),
                                            shot4DateofAdministration.getDateCellValue(),
                                            shot4Evaluation.getStringCellValue().toUpperCase(),
                                            Config.getCodeSystemOid("EVALUATION_VALUE"),
                                            vaccineGroup,
                                            Config.getCodeSystemOid("VACCINE_GROUP"),
                                            reasons);
                            List<SubstanceAdministrationEvent> components = new ArrayList<SubstanceAdministrationEvent>();
                            components.add(shot4EvaluationEvent);
                            testCase.addSubstanceAdministrationEvent(
                                    shot4CvxCode,
                                    Config.getCodeSystemOid("VACCINE"),
                                    shot4DateofAdministration.getDateCellValue(),
                                    null,
                                    Config.getCodeSystemOid("ADMINISTRATION_ID"),
                                    components);
                        } catch (CdsException e) {
                            logger.error("addSubstanceAdministrationEvent shot 4 Error @ " + location);
                            logger.error(e);
                            testCase.setErrorMessage(e.getMessage());
                            callback.callback(testCase, testSheetName, false);
                            continue;
                        }
                    }

                    testCurrentRowNum++;
                    testCurrentRow = testSheet.getRow(testCurrentRowNum);
                    XSSFCell shot5Vaccine = testCurrentRow.getCell(2);
                    logger.trace("    Shot 5 vaccine: " + shot5Vaccine);
                    XSSFCell shot5Cvx = testCurrentRow.getCell(3);
                    String shot5CvxCode = shot5Cvx.getRawValue();
                    if (shot5CvxCode != null && !shot5CvxCode.isEmpty()) {
                        logger.trace("    Shot 5 CVX: " + shot5Cvx.getRawValue());
                        XSSFCell shot5Evaluation = testCurrentRow.getCell(4);
                        logger.trace("    Shot 5 evaluation: " + shot5Evaluation);
                        XSSFCell shot5InvalidReason1Code = testCurrentRow.getCell(5);
                        logger.trace("    Shot 5 invalid reason 1 code: " + shot5InvalidReason1Code);
                        XSSFCell shot5InvalidReason2Code = testCurrentRow.getCell(6);
                        logger.trace("    Shot 5 invalid reason 2 code: " + shot5InvalidReason2Code);
                        XSSFCell shot5InvalidReason3Code = testCurrentRow.getCell(7);
                        logger.trace("    Shot 5 invalid reason 3 code: " + shot5InvalidReason3Code);
                        testCurrentRowNum++;
                        testCurrentRow = testSheet.getRow(testCurrentRowNum);
                        XSSFCell shot5DateofAdministration = testCurrentRow.getCell(2);
                        logger.trace("    Shot 5 date of administration: " + shot5DateofAdministration);

                        try {
                            String reason1 = shot5InvalidReason1Code.getStringCellValue();
                            String reason2 = shot5InvalidReason2Code.getStringCellValue();
                            String reason3 = shot5InvalidReason3Code.getStringCellValue();
                            reasons = new ArrayList<CD>();
                            if (reason1 != null && !reason1.trim().isEmpty()) {
                                reasons.add(CdsObjectFactory.getCD(reason1, Config.getCodeSystemOid("EVALUATION_INTERPRETATION")));
                            }
                            if (reason2 != null && !reason2.trim().isEmpty()) {
                                reasons.add(CdsObjectFactory.getCD(reason2, Config.getCodeSystemOid("EVALUATION_INTERPRETATION")));
                            }
                            if (reason3 != null && !reason3.trim().isEmpty()) {
                                reasons.add(CdsObjectFactory.getCD(reason3, Config.getCodeSystemOid("EVALUATION_INTERPRETATION")));
                            }
                            SubstanceAdministrationEvent shot5EvaluationEvent
                                    = testCase.getEvaluationSubstanceAdministrationEvent(
                                            shot5CvxCode,
                                            Config.getCodeSystemOid("VACCINE"),
                                            shot5DateofAdministration.getDateCellValue(),
                                            shot5Evaluation.getStringCellValue().toUpperCase(),
                                            Config.getCodeSystemOid("EVALUATION_VALUE"),
                                            vaccineGroup,
                                            Config.getCodeSystemOid("VACCINE_GROUP"),
                                            reasons);
                            List<SubstanceAdministrationEvent> components = new ArrayList<SubstanceAdministrationEvent>();
                            components.add(shot5EvaluationEvent);
                            testCase.addSubstanceAdministrationEvent(
                                    shot5CvxCode, Config.getCodeSystemOid("VACCINE"),
                                    shot5DateofAdministration.getDateCellValue(),
                                    null,
                                    Config.getCodeSystemOid("ADMINISTRATION_ID"),
                                    components);
                        } catch (CdsException e) {
                            logger.error("addSubstanceAdministrationEvent shot 5 Error @ " + location);
                            logger.error(e);
                            testCase.setErrorMessage(e.getMessage());
                            callback.callback(testCase, testSheetName, false);
                            continue;
                        }
                    }

                    int count = 0;
                    for (SubstanceAdministrationEvent sae : testCase.getSubstanceAdministrationEvents()) {
                        count++;
                        logger.debug("Shot " + count
                                + " present: " + sae.getSubstance().getSubstanceCode().getCode()
                                + "/" + sae.getAdministrationTimeInterval().getHigh());
                        int count2 = 0;
                        for (RelatedClinicalStatement rcs : sae.getRelatedClinicalStatement()) {
                            count2++;
                            SubstanceAdministrationEvent csae = rcs.getSubstanceAdministrationEvent();
                            logger.debug("Shot " + count
                                    + " Component " + count2
                                    + " present: " + csae.getSubstance().getSubstanceCode().getCode()
                                    + "/" + csae.getAdministrationTimeInterval().getHigh()
                                    + "/" + csae.getIsValid().isValue());
                            int count3 = 0;
                            for (RelatedClinicalStatement crcs : csae.getRelatedClinicalStatement()) {
                                count3++;
                                ObservationResult cor = crcs.getObservationResult();
                                logger.debug("Shot " + count
                                        + " Component " + count2
                                        + " OR " + count3
                                        + " present: " + cor.getObservationFocus().getCode()
                                        + "/" + cor.getObservationValue().getConcept().getCode());
                            }
                        }
                    }

                    callback.callback(testCase, testSheetName, true);
                } else {
                    // logger.warn("Sheet '" + testSheetName + "' - Row " + i + ": test name was null.");
                }
            } else {
                // logger.warn("Row " + i + " was null.");
            }
        }
        logger.info("Number of tests imported: " + testCount);
        if (testCount != importedTestCases.size()) {
            logger.warn("Count mismatch: " + testCount + " - " + importedTestCases.size());
        }
    }
}
