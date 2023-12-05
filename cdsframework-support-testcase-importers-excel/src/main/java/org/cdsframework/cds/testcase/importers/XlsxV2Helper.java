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
import java.util.LinkedList;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import org.apache.poi.POIXMLProperties;
import org.apache.poi.POIXMLProperties.CoreProperties;
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
public class XlsxV2Helper {

    private final static LogUtils logger = LogUtils.getLogger(XlsxV2Helper.class);

    public static void importFromWorkBook(XSSFWorkbook wb, TestImportCallback callback)
            throws CdsException, FileNotFoundException, IOException, DatatypeConfigurationException {
        List<String> importedTestCases = new ArrayList<String>();
        POIXMLProperties properties = wb.getProperties();
        CoreProperties coreProperties = properties.getCoreProperties();
        String category = coreProperties.getCategory();
        logger.trace("category: " + category);
        XSSFSheet sheet = wb.getSheet("Test Coverage Summary");
        if (sheet == null) {
            throw new CdsException("Bad format - missing Test Coverage Summary sheet");
        }
        int lastRowNum = sheet.getLastRowNum();
        logger.trace("LastRowNum: " + lastRowNum);
        int i = 0;
        int testCount = 0;
        while (i < lastRowNum) {
            i++;
            XSSFRow row = sheet.getRow(i);
            if (row != null) {
                XSSFCell cell = row.getCell(0);
                if (cell != null) {
                    String cellFormula = cell.getCellFormula();
                    logger.info("importing from cellFormula=" + cellFormula);
                    String testSheetName = cellFormula.substring(1, cellFormula.indexOf("!") - 1);
                    logger.trace("testSheetName=" + testSheetName);
                    XSSFSheet testSheet = wb.getSheet(testSheetName);
                    if (testSheet == null) {
                        continue;
                    }
                    logger.trace("testSheet=" + testSheet);
                    String testSheetCellStart = cellFormula.substring(cellFormula.indexOf("!") + 1);
                    logger.trace("testSheetCellStart=" + testSheetCellStart);
                    int testCurrentRowNum = Integer.parseInt(testSheetCellStart.substring(3)) - 1;
                    XSSFRow testCurrentRow = testSheet.getRow(testCurrentRowNum);
                    XSSFCell testNumberCell = testCurrentRow.getCell(0);
                    String testNumber = testNumberCell.getStringCellValue();
                    logger.trace("testNumber=" + testNumber);
                    XSSFCell name = testCurrentRow.getCell(2);
                    if (!name.getStringCellValue().isEmpty()) {
                        String localName = name.getStringCellValue();
                        logger.trace("localName=" + localName);
                        String globalName = testSheetName + ": " + localName;
                        logger.trace("globalName=" + globalName);
                        testCount++;
                        TestCaseWrapper testCase = TestCaseWrapper.getTestCaseWrapper();

                        // change to filename, tab name, test #, row #
                        String location = testSheetName + ", test # " + testNumber + ", row # " + (testCurrentRowNum + 1);
                        testCase.setFileLocation(location);

                        logger.trace(location);

                        // test name + vaccine group
                        testCase.setName(localName);
                        String encodedName = XlsxImporterUtils.getShaHashFromString(globalName);
                        logger.trace("encodedName=" + encodedName);
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
                        logger.trace("    Test name: " + testCase.getName());
                        XSSFCell vaccineGroupCell = testCurrentRow.getCell(10);
                        String vaccineGroup = null;
                        try {
                            double numericCellValue = vaccineGroupCell.getNumericCellValue();
                            vaccineGroup = String.valueOf((int) numericCellValue);
                        } catch (IllegalStateException e) {
                            vaccineGroup = vaccineGroupCell.getStringCellValue();
                        }
                        logger.trace("    Vaccine group: " + vaccineGroup);

                        // test focus
                        testCurrentRowNum++;
                        testCurrentRow = testSheet.getRow(testCurrentRowNum);
                        XSSFCell focusCell = testCurrentRow.getCell(2);
                        String focus = focusCell.getStringCellValue();
                        logger.trace("    Test focus: " + focus);
                        if (focus == null || focus.trim().isEmpty()) {
                            throw new UnsupportedOperationException("    Test focus: " + focus + " - " + location);
                        }

                        // Combo test?
                        XSSFCell comboCell = testCurrentRow.getCell(6);
                        String comboString = comboCell.getStringCellValue();
                        if (comboString != null) {
                            comboString = comboString.split(" ")[0].trim();
                        }
                        boolean combo = "Yes".equalsIgnoreCase(comboString);
                        logger.trace("    Test combo: " + combo);

                        // series
                        XSSFCell series = testCurrentRow.getCell(9);
                        String testSeries = series.getStringCellValue();
                        logger.trace("    Series: " + testSeries);

                        testCase.addProperty("vaccineGroup", vaccineGroup, TestCasePropertyType.STRING);
                        testCase.addProperty("series", testSeries, TestCasePropertyType.STRING);

                        // dose number focus
                        testCurrentRowNum++;
                        testCurrentRow = testSheet.getRow(testCurrentRowNum);
                        XSSFCell doseFocusCell = testCurrentRow.getCell(2);
                        String doseFocus = doseFocusCell.getStringCellValue();
                        logger.trace("    Dose focus: " + doseFocus);

                        // rule to test description
                        testCurrentRowNum++;
                        testCurrentRow = testSheet.getRow(testCurrentRowNum);
                        XSSFCell ruleToTest = testCurrentRow.getCell(2);
                        testCase.setRuletotest(ruleToTest.getStringCellValue());
                        logger.trace("    Rule to test: " + testCase.getRuletotest());

                        // notes
                        testCurrentRowNum++;
                        testCurrentRow = testSheet.getRow(testCurrentRowNum);
                        XSSFCell testNotes = testCurrentRow.getCell(2);
                        testCase.setNotes(testNotes.getStringCellValue());
                        logger.trace("    Test notes: " + testCase.getNotes());

                        // antigen
                        testCurrentRowNum += 2;
                        testCurrentRow = testSheet.getRow(testCurrentRowNum);
                        XSSFCell antigenCell = testCurrentRow.getCell(3);
                        String antigen = antigenCell.getStringCellValue();
                        if (antigen != null && antigen.trim().isEmpty()) {
                            antigen = null;
                        }

                        // immunityValue
                        testCurrentRowNum++;
                        testCurrentRow = testSheet.getRow(testCurrentRowNum);
                        XSSFCell immunityValueCell = testCurrentRow.getCell(3);
                        String immunityValue = immunityValueCell.getStringCellValue();
                        if ("T".equalsIgnoreCase(immunityValue)) {
                            immunityValue = "PROOF_OF_IMMUNITY";
                        } else if ("H".equals(immunityValue)) {
                            immunityValue = "DISEASE_DOCUMENTED";
                        }
                        if (immunityValue != null && immunityValue.trim().isEmpty()) {
                            immunityValue = null;
                        }

                        // immunityDate
                        testCurrentRowNum++;
                        testCurrentRow = testSheet.getRow(testCurrentRowNum);
                        XSSFCell immunityDateCell = testCurrentRow.getCell(2);
                        Date immunityDate = null;
                        try {
                            immunityDate = immunityDateCell.getDateCellValue();
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }

                        if (antigen != null && immunityValue != null && immunityDate != null) {
                            testCase.addImmunityObservationResult(immunityDate, null,
                                    antigen, Config.getCodeSystemOid("DISEASE"),
                                    immunityValue, Config.getCodeSystemOid("IMMUNITY_VALUE"),
                                    "IS_IMMUNE", Config.getCodeSystemOid("IMMUNITY_INTERPRETATION")
                            );
                            logger.trace("    Immune: " + antigen + " - " + immunityValue + " - " + immunityDate);
                        } else if (antigen != null || immunityValue != null || immunityDate != null) {
                            logger.error("Missing values on disease/immunity input:");
                            logger.error("    antigen: " + antigen);
                            logger.error("    immunityValue: " + immunityValue);
                            logger.error("    immunityDate: " + immunityDate);
                            testCase.setErrorMessage(
                                    location
                                    + ", RowNum "
                                    + testCurrentRowNum
                                    + " - Missing values on disease/immunity input: antigen="
                                    + antigen
                                    + "; immunityValue="
                                    + immunityValue
                                    + "; immunityDate="
                                    + immunityDate);
                        }

                        // set the execution date
                        testCurrentRowNum++;
                        testCurrentRow = testSheet.getRow(testCurrentRowNum);
                        XSSFCell executionDate = testCurrentRow.getCell(2);
                        if (executionDate != null && executionDate.getDateCellValue() != null) {
                            testCase.setExecutiondate(executionDate.getDateCellValue());
                            logger.trace("    Execution date: " + testCase.getExecutiondatetime());
                        } else {
                            logger.error("Execution Date is null.");
                        }

                        // add the recommendation via the substanceAdministrationProposal and observationResult
                        XSSFCell recommendationCell = testCurrentRow.getCell(8);
                        String recommendation = recommendationCell.getStringCellValue();
                        logger.trace("    Recommendation: " + recommendation);
                        XSSFCell recommendationReasonCell = testCurrentRow.getCell(9);
                        String recommendationReason = recommendationReasonCell.getStringCellValue();
                        logger.trace("    Reason for recommendation: " + recommendationReasonCell);
                        List<CD> reasons = new ArrayList<CD>();
                        if (recommendationReason != null && !recommendationReason.trim().isEmpty()) {
                            reasons.add(CdsObjectFactory.getCD(recommendationReason, Config.getCodeSystemOid("RECOMMENDATION_INTERPRETATION")));
                        }
                        XSSFCell dueDateCell = testCurrentRow.getCell(10);
                        Date dueDate = dueDateCell.getDateCellValue();
                        logger.trace("    Date due: " + dueDate);

                        // convert DOB to ISO format and add to both vmr output and input
                        testCurrentRowNum++;
                        testCurrentRow = testSheet.getRow(testCurrentRowNum);
                        XSSFCell dobCell = testCurrentRow.getCell(2);
                        if (dobCell != null && dobCell.getDateCellValue() != null) {
                            testCase.setPatientBirthTime(dobCell.getDateCellValue());
                            logger.trace("    DOB: " + testCase.getPatientBirthTime());
                        } else {
                            logger.error("DOB is null.");
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
                        logger.trace("    Gender: " + testCase.getPatientGender());

                        // not in use yet
                        XSSFCell recommendedVaccineCell = testCurrentRow.getCell(10);
                        String recommendedVaccine = null;
                        try {
                            double recommendedVaccineDouble = recommendedVaccineCell.getNumericCellValue();
                            recommendedVaccine = String.valueOf((int) recommendedVaccineDouble);
                        } catch (IllegalStateException e) {
                            // ignore - its empty
                        }
                        logger.trace("    Recommended vaccine: " + recommendedVaccine);

                        if (vaccineGroup == null && recommendedVaccine == null) {
                            logger.error("Neither a vaccine group or code recommendation exists!");
                            testCase.setErrorMessage("Neither a vaccine group or code recommendation exists!");
                            callback.callback(testCase, testSheetName, false);
                            continue;
                        }
                        try {
                            testCase.addSubstanceAdministrationProposal(
                                    vaccineGroup,
                                    Config.getCodeSystemOid("VACCINE_GROUP"),
                                    recommendedVaccine,
                                    Config.getCodeSystemOid("VACCINE"),
                                    DateUtils.getISODateFormat(dueDate),
                                    vaccineGroup,
                                    Config.getCodeSystemOid("VACCINE_GROUP"),
                                    recommendation,
                                    Config.getCodeSystemOid("RECOMMENDATION_VALUE"),
                                    reasons);
                        } catch (Exception e) {
                            logger.error("addSubstanceAdministrationProposal Error @ " + location + " - " + e.getMessage());
                            logger.error(e.getMessage(), e);
                            testCase.setErrorMessage(e.getMessage());
                            callback.callback(testCase, testSheetName, false);
                            continue;
                        }

                        for (int shotNum : new int[]{1, 2, 3, 4, 5}) {
                            testCurrentRowNum++;
                            testCurrentRow = testSheet.getRow(testCurrentRowNum);
                            XSSFCell administeredVaccine = testCurrentRow.getCell(2);
                            logger.trace("    Shot " + shotNum + " vaccine: " + administeredVaccine);
                            XSSFCell administeredVaccineCvxCell = testCurrentRow.getCell(3);
                            String administeredVaccineCvx = administeredVaccineCvxCell.getRawValue();
                            if (administeredVaccineCvx != null && !administeredVaccineCvx.isEmpty()) {
                                logger.trace("    Shot " + shotNum + " CVX: " + administeredVaccineCvx);
                                Date administrationDate = null;
                                List<SubstanceAdministrationEvent> componentEvents = new LinkedList<SubstanceAdministrationEvent>();
                                if (combo) {
                                    logger.trace("  Processing combo evaluation...");
                                    // date of administration
                                    testCurrentRowNum++;
                                    testCurrentRow = testSheet.getRow(testCurrentRowNum);
                                    XSSFCell administrationDateCell = testCurrentRow.getCell(2);
                                    administrationDate = administrationDateCell.getDateCellValue();
                                    logger.trace("    Shot " + shotNum + " date of administration: " + administrationDate);

                                    for (int compNum : new int[]{1, 2, 3, 4}) {
                                        testCurrentRowNum++;
                                        testCurrentRow = testSheet.getRow(testCurrentRowNum);

                                        // vac code
                                        XSSFCell componentVaccineCell = testCurrentRow.getCell(3);
                                        String componentVaccine = null;
                                        try {
                                            double numericCellValue = componentVaccineCell.getNumericCellValue();
                                            componentVaccine = String.valueOf((int) numericCellValue);
                                        } catch (IllegalStateException e) {
                                            // ignore - it is null
                                        }
                                        logger.trace("    Component Vaccine " + compNum + ": " + componentVaccine);

                                        // evaluation
                                        XSSFCell evaluationCell = testCurrentRow.getCell(4);
                                        String evaluation = evaluationCell.getStringCellValue();
                                        logger.trace("    Component Vaccine evaluation " + compNum + ": " + evaluation);

                                        reasons = new ArrayList<CD>();

                                        for (int reasonNum : new int[]{1, 2, 3}) {
                                            XSSFCell evaluationReasonCell = testCurrentRow.getCell(4 + reasonNum);
                                            String evaluationReason = evaluationReasonCell.getStringCellValue();
                                            if (evaluationReason != null && !evaluationReason.trim().isEmpty()) {
                                                reasons.add(CdsObjectFactory.getCD(evaluationReason, Config.getCodeSystemOid("EVALUATION_INTERPRETATION")));
                                                logger.trace("      found reason code: " + evaluationReason);
                                            }
                                        }
                                        if (componentVaccine != null) {
                                            SubstanceAdministrationEvent substanceAdministrationEvent
                                                    = testCase.getEvaluationSubstanceAdministrationEvent(
                                                            componentVaccine,
                                                            Config.getCodeSystemOid("VACCINE"),
                                                            administrationDate,
                                                            evaluation.toUpperCase(),
                                                            Config.getCodeSystemOid("EVALUATION_VALUE"),
                                                            vaccineGroup,
                                                            Config.getCodeSystemOid("VACCINE_GROUP"),
                                                            reasons);
                                            componentEvents.add(substanceAdministrationEvent);
                                        }
                                    }
                                } else {
                                    logger.trace("Processing single component evaluation...");
                                    // evaluation
                                    XSSFCell evaluationCell = testCurrentRow.getCell(4);
                                    String evaluation = evaluationCell.getStringCellValue();
                                    logger.trace("    Vaccine evaluation " + shotNum + ": " + evaluation);

                                    reasons = new ArrayList<CD>();

                                    for (int reasonNum : new int[]{1, 2, 3}) {
                                        XSSFCell evaluationReasonCell = testCurrentRow.getCell(4 + reasonNum);
                                        String evaluationReason = evaluationReasonCell.getStringCellValue();
                                        if (evaluationReason != null && !evaluationReason.trim().isEmpty()) {
                                            reasons.add(CdsObjectFactory.getCD(evaluationReason, Config.getCodeSystemOid("EVALUATION_INTERPRETATION")));
                                            logger.trace("      found reason code: " + evaluationReason);
                                        }
                                    }

                                    testCurrentRowNum++;
                                    testCurrentRow = testSheet.getRow(testCurrentRowNum);
                                    XSSFCell administrationDateCell = testCurrentRow.getCell(2);
                                    administrationDate = administrationDateCell.getDateCellValue();
                                    logger.trace("    Shot " + shotNum + " date of administration: " + administrationDate);

                                    SubstanceAdministrationEvent substanceAdministrationEvent
                                            = testCase.getEvaluationSubstanceAdministrationEvent(
                                                    administeredVaccineCvx,
                                                    Config.getCodeSystemOid("VACCINE"),
                                                    administrationDate,
                                                    evaluation.toUpperCase(),
                                                    Config.getCodeSystemOid("EVALUATION_VALUE"),
                                                    vaccineGroup,
                                                    Config.getCodeSystemOid("VACCINE_GROUP"),
                                                    reasons);
                                    componentEvents.add(substanceAdministrationEvent);
                                    testCurrentRowNum += 4;
                                }

                                try {
                                    testCase.addSubstanceAdministrationEvent(
                                            administeredVaccineCvx,
                                            Config.getCodeSystemOid("VACCINE"),
                                            administrationDate,
                                            null,
                                            Config.getCodeSystemOid("ADMINISTRATION_ID"),
                                            componentEvents);
                                } catch (CdsException e) {
                                    logger.error("addSubstanceAdministrationEvent shot " + shotNum + " Error @ " + location);
                                    logger.error(e);
                                    testCase.setErrorMessage(e.getMessage());
                                    callback.callback(testCase, testSheetName, false);
                                    continue;
                                }
                            }
                        }

                        int count = 0;
                        for (SubstanceAdministrationEvent sae : testCase.getSubstanceAdministrationEvents()) {
                            count++;
                            logger.trace("Shot " + count
                                    + " present: " + sae.getSubstance().getSubstanceCode().getCode()
                                    + "/" + sae.getAdministrationTimeInterval().getHigh());
                            int count2 = 0;
                            for (RelatedClinicalStatement rcs : sae.getRelatedClinicalStatement()) {
                                count2++;
                                SubstanceAdministrationEvent csae = rcs.getSubstanceAdministrationEvent();
                                logger.trace("Shot " + count
                                        + " Component " + count2
                                        + " present: " + csae.getSubstance().getSubstanceCode().getCode()
                                        + "/" + csae.getAdministrationTimeInterval().getHigh()
                                        + "/" + csae.getIsValid().isValue());
                                int count3 = 0;
                                for (RelatedClinicalStatement crcs : csae.getRelatedClinicalStatement()) {
                                    count3++;
                                    ObservationResult cor = crcs.getObservationResult();
                                    logger.trace("Shot " + count
                                            + " Component " + count2
                                            + " OR " + count3
                                            + " present: " + cor.getObservationFocus().getCode()
                                            + "/" + cor.getObservationValue().getConcept().getCode());
                                }
                            }
                        }
                        callback.callback(testCase, testSheetName, true);
                    } else {
                        // logger.trace("Sheet '" + testSheetName + "' - Row " + i + ": test name was null.");
                    }
                } else {
                    // logger.trace("cell is null");
                }
            } else {
                // logger.trace("Row " + i + " was null.");
            }
        }
        logger.info("Number of tests imported: " + testCount);
        if (testCount != importedTestCases.size()) {
            logger.error("Count mismatch: " + testCount + " - " + importedTestCases.size());
        }
    }
}
