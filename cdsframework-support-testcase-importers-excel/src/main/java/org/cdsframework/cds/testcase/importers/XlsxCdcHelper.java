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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.datatype.DatatypeConfigurationException;
import org.apache.poi.POIXMLProperties;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.cdsframework.cds.service.OpenCdsService;
import org.cdsframework.cds.util.CdsObjectFactory;
import org.cdsframework.cds.vmr.CdsObjectAssist;
import org.cdsframework.enumeration.TestCasePropertyType;
import org.cdsframework.exceptions.CdsException;
import org.cdsframework.ice.testcase.TestCaseWrapper;
import org.cdsframework.util.LogUtils;
import org.cdsframework.util.support.cds.Config;
import org.cdsframework.util.support.data.cds.testcase.TestCase;
import org.opencds.support.util.DateUtils;
import org.opencds.vmr.v1_0.schema.AdministrableSubstance;
import org.opencds.vmr.v1_0.schema.CD;
import org.opencds.vmr.v1_0.schema.CDSInput;
import org.opencds.vmr.v1_0.schema.CDSOutput;
import org.opencds.vmr.v1_0.schema.RelatedClinicalStatement;
import org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent;
import org.opencds.vmr.v1_0.schema.SubstanceAdministrationProposal;

/**
 *
 * @author HLN Consulting, LLC
 */
public class XlsxCdcHelper {

    private final static LogUtils logger = LogUtils.getLogger(XlsxCdcHelper.class);

    public static void importFromWorkBook(XSSFWorkbook wb, TestImportCallback callback)
            throws CdsException, FileNotFoundException, IOException, DatatypeConfigurationException {

        final String METHODNAME = "importFromWorkBook";

        String endPoint = Config.getCdsDefaultEndpoint();
        OpenCdsService service = OpenCdsService.getOpenCdsService(endPoint);
        String scopingEntityId = "org.nyc.cir";
        String businessId = "ICE";
        String version = "1.0.0";
        int changedRecCount = 0;

        Map<String, String> vaccineGroupMap = new HashMap<String, String>();
        vaccineGroupMap.put("DTaP", "200");
        vaccineGroupMap.put("Tdap", "200");
        vaccineGroupMap.put("Td", "200");
        vaccineGroupMap.put("Td", "200");
        vaccineGroupMap.put("Rota", "820");
        vaccineGroupMap.put("Var", "600");
        vaccineGroupMap.put("Hib", "300");
        vaccineGroupMap.put("HepA", "810");
        vaccineGroupMap.put("HepB", "100");
        vaccineGroupMap.put("Flu", "800");
        vaccineGroupMap.put("HPV", "840");
        vaccineGroupMap.put("MCV", "830");
        vaccineGroupMap.put("MMR", "500");
        vaccineGroupMap.put("PCV", "750");
        vaccineGroupMap.put("POL", "400");
        vaccineGroupMap.put("Zoster", "620");
        vaccineGroupMap.put("MeningB", "835");
        List<String> reasonsToMap = new ArrayList<String>();
        List<String> recommendationToMap = new ArrayList<String>();
        POIXMLProperties properties = wb.getProperties();
        POIXMLProperties.CoreProperties coreProperties = properties.getCoreProperties();
        String category = coreProperties.getCategory();
        logger.info("category: " + category);
        XSSFSheet sheet = wb.getSheet("Healthy Child_Adult Test Cases");
        if (sheet == null) {
            throw new CdsException("Bad format - missing tab Healthy Child_Adult Test Cases");
        }
        String testSheetName = sheet.getSheetName();
        int lastRowNum = sheet.getLastRowNum();
        logger.info("LastRowNum: " + lastRowNum);
        int i = 0;
        int testCount = 0;
        XSSFRow row = sheet.getRow(i);
        Iterator<Cell> cellIterator = row.cellIterator();
        int cellNumber = 0;
        while (cellIterator.hasNext()) {
            Cell nextCell = cellIterator.next();
            logger.info("Cell number: ", cellNumber, " - ", nextCell.toString());
            cellNumber++;
        }
        while (i < lastRowNum) {
            i++;
            logger.info("parsing row: ", i);
            row = sheet.getRow(i);

            // testId
            Integer testId = null;
            String testIdCellValue = null;
            try {
                XSSFCell testIdCell = row.getCell(0);
                testIdCellValue = testIdCell.getStringCellValue();
                testId = Integer.parseInt(testIdCellValue.split("-")[1]);
                logger.info("Got testId: ", testId);
            } catch (Exception e) {
                testId = 0;
                logger.error("Error getting testId!");
                logger.error(e);
            }

            // testName
            String testName = null;
            try {
                XSSFCell testNameCell = row.getCell(1);
                testName = testNameCell.getStringCellValue();
                logger.info("Got testNme: ", testName);
            } catch (Exception e) {
                logger.error("Error getting testName!");
                logger.error(e);
                throw new CdsException("Test name is null!");
            }

            if (!testName.isEmpty()) {
                testCount++;
                logger.debug("Adding test count: ", testCount);
                TestCaseWrapper testCase = TestCaseWrapper.getTestCaseWrapper();
                String globalName = testId + " - " + testName;
                String encodedName = XlsxImporterUtils.getShaHashFromString(globalName);
                logger.debug("encodedName=" + encodedName);
                testCase.setEncodedName(encodedName);
                testCase.setPatientId(testId.toString());
                testCase.setSuiteName("CDC Testcases");
                testCase.setName(testName);
                testCase.setNotes("Incoming test ID: " + testIdCellValue + "\n");

                String location = testSheetName + ", test # " + testId + ", row # " + i;
                testCase.setFileLocation(location);
                logger.debug("Test file location: ", location);

                // testDob
                Date testDob = null;
                try {
                    XSSFCell testDobCell = row.getCell(2);
                    testDob = testDobCell.getDateCellValue();
                    logger.debug("Got testDob: ", testDob);
                } catch (Exception e) {
                    logger.error("Error getting testDob!");
                    logger.error(e);
                }
                testCase.setPatientBirthTime(testDob);

                // testGender
                String testGender = null;
                try {
                    XSSFCell testGenderCell = row.getCell(3);
                    testGender = testGenderCell.getStringCellValue();
                    logger.debug("Got testGender: ", testGender);
                } catch (Exception e) {
                    logger.error("Error getting testGender!");
                    logger.error(e);
                }
                testCase.setPatientGender(testGender, Config.getCodeSystemOid("GENDER"));

                // vaccine group
                String vaccineGroup = null;
                try {
                    XSSFCell vaccineGroupCell = row.getCell(54);
                    vaccineGroup = vaccineGroupCell.getStringCellValue();
                    if (!vaccineGroupMap.containsKey(vaccineGroup)) {
                        throw new CdsException("Vaccine Group did not map: " + vaccineGroup);
                    }
                    vaccineGroup = vaccineGroupMap.get(vaccineGroup);
                    testCase.addProperty("vaccineGroup", vaccineGroup, TestCasePropertyType.STRING);
                    logger.debug("Got vaccineGroup: " + vaccineGroup);
                } catch (Exception e) {
                    logger.error("Error getting vaccineGroup!");
                    logger.error(e);
                }

                // execution date
                Date executionDate = null;
                try {
                    XSSFCell executionDateCell = row.getCell(55);
                    executionDate = executionDateCell.getDateCellValue();
                    testCase.setExecutiondate(executionDate);
                    logger.debug("Got executionDate: " + executionDate);
                } catch (Exception e) {
                    logger.error("Error getting executionDate!");
                    logger.error(e);
                }

                // test group/eval type
                String testGroup = null;
                try {
                    XSSFCell testGroupCell = row.getCell(56);
                    testGroup = testGroupCell.getStringCellValue();
                    testCase.setGroupName(vaccineGroup + " - " + testGroup);
                    testCase.setRuletotest("Evaluation Test Type: " + testGroup);
                    logger.debug("Got testGroup: " + testGroup);
                } catch (Exception e) {
                    logger.error("Error getting testGroup!");
                    logger.error(e);
                }

                // added date
                Date addedDate = null;
                try {
                    XSSFCell addedDateCell = row.getCell(57);
                    addedDate = addedDateCell.getDateCellValue();
                    testCase.setNotes(testCase.getNotes() + "Date Added: " + addedDate + "\n");
                    logger.debug("Got addedDate: " + addedDate);
                } catch (Exception e) {
                    logger.error("Error getting addedDate!");
                    logger.error(e);
                }

                // updated date
                Date updatedDate = null;
                try {
                    XSSFCell updatedDateCell = row.getCell(58);
                    updatedDate = updatedDateCell.getDateCellValue();
                    testCase.setNotes(testCase.getNotes() + "Date Updated: " + updatedDate + "\n");
                    logger.debug("Got updatedDate: " + updatedDate);
                } catch (Exception e) {
                    logger.error("Error getting updatedDate!");
                    logger.error(e);
                }

                // forecast type
                String forecastType = null;
                try {
                    XSSFCell forecastTypeCell = row.getCell(59);
                    forecastType = forecastTypeCell.getStringCellValue();
                    testCase.setRuletotest("Forecast Test Type: " + forecastType);
                    logger.debug("Got forecastType: ", forecastType);
                    if (!recommendationToMap.contains(forecastType)) {
                        recommendationToMap.add(forecastType);
                    }
                } catch (Exception e) {
                    logger.error("Error getting forecastType!");
                    logger.error(e);
                }

                String reasonForChange;
                try {
                    XSSFCell reasonForChangeCell = row.getCell(60);
                    reasonForChange = reasonForChangeCell.getStringCellValue();
                    testCase.setRuletotest(testCase.getRuletotest() + "\nReason for Change: " + reasonForChange);
                    logger.debug("Got reasonForChange: ", reasonForChange);
                } catch (Exception e) {
                    logger.error("Error getting reasonForChange!");
                    logger.error(e);
                }

                String generalDescription;
                try {
                    XSSFCell generalDescriptionCell = row.getCell(62);
                    generalDescription = generalDescriptionCell.getStringCellValue();
                    testCase.setRuletotest(testCase.getRuletotest() + "\nGeneral Description: " + generalDescription);
                    logger.debug("Got generalDescription: ", generalDescription);
                } catch (Exception e) {
                    logger.error("Error getting generalDescription!");
                    logger.error(e);
                }

                int rowIndex = 7;
                for (int doseNum : new int[]{1, 2, 3, 4, 5, 6, 7}) {
                    List<SubstanceAdministrationEvent> componentEvents = new LinkedList<SubstanceAdministrationEvent>();

                    rowIndex++;

                    // dose date
                    Date dateDose = null;
                    try {
                        XSSFCell dateDoseCell = row.getCell(rowIndex);
                        dateDose = dateDoseCell.getDateCellValue();
                    } catch (Exception e) {
                        // ignore
//                        logger.debug("Error getting dateDose" + doseNum + "!");
//                        logger.debug(e);
                    }

                    if (dateDose != null) {
                        logger.debug("Got dateDose" + doseNum + ": " + dateDose);

                        rowIndex++;
                        rowIndex++;

                        // dose cvx
                        String administeredVaccineCvxDose = null;
                        try {
                            XSSFCell administeredVaccineCvxDoseCell = row.getCell(rowIndex);
                            try {
                                administeredVaccineCvxDose = administeredVaccineCvxDoseCell.getStringCellValue();
                            } catch (IllegalStateException e) {
                                if (!"Cannot get a STRING value from a NUMERIC cell".equalsIgnoreCase(e.getMessage())) {
                                    logger.info(e);
                                }
                                administeredVaccineCvxDose = "" + (int) administeredVaccineCvxDoseCell.getNumericCellValue();
                            }
                            logger.debug("Got administeredVaccineCvxDose" + doseNum + ": " + administeredVaccineCvxDose);
                        } catch (Exception e) {
                            logger.error("Error getting administeredVaccineCvxDose" + doseNum + "!");
                            logger.error(e);
                            throw new CdsException(e.getMessage());
                        }

                        rowIndex++;
                        rowIndex++;

                        // dose evaluation value
                        String evalValueDose = null;
                        try {
                            XSSFCell evalValueDoseCell = row.getCell(rowIndex);
                            evalValueDose = evalValueDoseCell.getStringCellValue();
                            evalValueDose = evalValueDose.toUpperCase();
                            if ("Not Valid".equalsIgnoreCase(evalValueDose)) {
                                evalValueDose = "INVALID";
                            } else if ("EXTRANEOUS".equalsIgnoreCase(evalValueDose)) {
                                evalValueDose = "ACCEPTED";
                            }
                            logger.debug("Got evalValueDose" + doseNum + ": " + evalValueDose);
                        } catch (Exception e) {
                            logger.error("Error getting evalValueDose" + doseNum + "!");
                            logger.error(e);
                        }

                        rowIndex++;

                        List<CD> reasons = new ArrayList<CD>();

                        // dose evaluation interpretation
                        String evalInterDose = null;
                        try {
                            XSSFCell evalInterDoseCell = row.getCell(rowIndex);
                            evalInterDose = evalInterDoseCell.getStringCellValue();
                            if (!reasonsToMap.contains(evalInterDose)) {
                                reasonsToMap.add(evalInterDose);
                            }
                            if (evalInterDose != null && !evalInterDose.trim().isEmpty()) {
                                evalInterDose = evalInterDose.trim();
                                if ("Age: Too Young".equalsIgnoreCase(evalInterDose)) {
                                    evalInterDose = "BELOW_MINIMUM_AGE_SERIES";
                                } else if ("Interval: Too Short".equalsIgnoreCase(evalInterDose)) {
                                    evalInterDose = "BELOW_MINIMUM_INTERVAL";
                                } else if ("Vaccine: Invalid Usage".equalsIgnoreCase(evalInterDose)) {
                                    evalInterDose = "INSUFFICIENT_ANTIGEN";
                                } else if ("Live Virus Conflict".equalsIgnoreCase(evalInterDose)) {
                                    evalInterDose = "TOO_EARLY_LIVE_VIRUS";
                                } else if ("Series Already Complete".equalsIgnoreCase(evalInterDose)) {
                                    evalInterDose = "EXTRA_DOSE";
                                } else if ("Age: Too Old".equalsIgnoreCase(evalInterDose)) {
                                    evalInterDose = "ABOVE_REC_AGE_SERIES";
                                } else if ("Inadvertent Vaccine: Inadvertent Administration".equalsIgnoreCase(evalInterDose) && vaccineGroupMap.get("HPV").equalsIgnoreCase(vaccineGroup)) {
                                    evalInterDose = "VACCINE_NOT_LICENSED_FOR_MALES";
                                } else if ("Vaccine: InValid Usage".equalsIgnoreCase(evalInterDose)) {
                                    evalInterDose = "INSUFFICIENT_ANTIGEN";
                                } else {
                                    logger.warn("Skipping evaluation reason - not mapped: ", evalInterDose);
                                    evalInterDose = null;
                                }
                                logger.debug("Got evalInterDose" + doseNum + ": " + evalInterDose);
                                if (evalInterDose != null) {
                                    reasons.add(CdsObjectFactory.getCD(evalInterDose, Config.getCodeSystemOid("EVALUATION_INTERPRETATION")));
                                }
                            }
                        } catch (Exception e) {
                            logger.error("Error getting evalInterDose" + doseNum + "!");
                            logger.error(e);
                        }
                        if ("VACCINE_NOT_LICENSED_FOR_MALES".equalsIgnoreCase(evalInterDose) && "INVALID".equalsIgnoreCase(evalValueDose)) {
                            evalValueDose = "ACCEPTED";
                        }
                        if (("INVALID".equalsIgnoreCase(evalValueDose) || "ACCEPTED".equalsIgnoreCase(evalValueDose)) && reasons.isEmpty()) {
                            reasons.add(CdsObjectFactory.getCD("NOT_DEFINED", Config.getCodeSystemOid("EVALUATION_INTERPRETATION")));
                        }
                        if ("INVALID".equalsIgnoreCase(evalValueDose) || "ACCEPTED".equalsIgnoreCase(evalValueDose)) {
                            logger.info(encodedName + " - " + evalValueDose + " REASONS: ", reasons);
                        }
                        SubstanceAdministrationEvent substanceAdministrationEvent
                                = testCase.getEvaluationSubstanceAdministrationEvent(
                                        administeredVaccineCvxDose,
                                        Config.getCodeSystemOid("VACCINE"),
                                        dateDose,
                                        evalValueDose,
                                        Config.getCodeSystemOid("EVALUATION_VALUE"),
                                        vaccineGroup,
                                        Config.getCodeSystemOid("VACCINE_GROUP"),
                                        reasons);
                        componentEvents.add(substanceAdministrationEvent);
                        try {
                            testCase.addSubstanceAdministrationEvent(
                                    administeredVaccineCvxDose,
                                    Config.getCodeSystemOid("VACCINE"),
                                    dateDose,
                                    null,
                                    Config.getCodeSystemOid("ADMINISTRATION_ID"),
                                    componentEvents);
                        } catch (CdsException e) {
                            logger.error("addSubstanceAdministrationEvent shot " + doseNum + " Error @ " + location);
                            logger.error(e);
                            testCase.setErrorMessage(e.getMessage());
                            callback.callback(testCase, testSheetName, false);
                            continue;
                        }
                    }
                }

                // proposal
                // earliest date
                Date earliestDate = null;
                try {
                    XSSFCell earliestDateCell = row.getCell(51);
                    earliestDate = earliestDateCell.getDateCellValue();
                    testCase.setNotes(testCase.getNotes() + "Earliest Date: " + earliestDate + "\n");
                    logger.info("Got earliestDate: " + earliestDate);
                } catch (Exception e) {
                    logger.error("Error getting earliestDate!");
                    logger.error(e);
                }

                // recommended date
                Date recommendedDate = null;
                try {
                    XSSFCell recommendedDateCell = row.getCell(52);
                    recommendedDate = recommendedDateCell.getDateCellValue();
                    testCase.setNotes(testCase.getNotes() + "Recommended Date: " + recommendedDate + "\n");
                    logger.info("Got recommendedDate: " + recommendedDate);
                } catch (Exception e) {
                    logger.error("Error getting recommendedDate!");
                    logger.error(e);
                }

                // past due date
                Date pastDueDate = null;
                try {
                    XSSFCell pastDueDateCell = row.getCell(53);
                    pastDueDate = pastDueDateCell.getDateCellValue();
                    testCase.setNotes(testCase.getNotes() + "Past Due Date: " + pastDueDate + "\n");
                    logger.info("Got pastDueDate: " + pastDueDate);
                } catch (Exception e) {
                    logger.error("Error getting pastDueDate!");
                    logger.error(e);
                }

                // proposal value
                // proposal reason
                String proposalValue = null;
                String proposalReason = null;
                List<CD> reasons = new ArrayList<CD>();
                if (forecastType != null && forecastType.toUpperCase().startsWith("REC")) {
                    if (recommendedDate != null && recommendedDate.after(executionDate)) {
                        proposalValue = "FUTURE_RECOMMENDED";
                    } else {
                        proposalValue = "RECOMMENDED";
                    }
                } else if (forecastType != null && forecastType.toUpperCase().startsWith("NOT")) {
                    proposalValue = "NOT_RECOMMENDED";
                }
                if (forecastType != null && forecastType.split(":").length > 1) {
                    proposalReason = forecastType.split(":")[1].trim();
                } else if (forecastType != null) {
                    proposalReason = forecastType.trim();
                }

                if (proposalReason != null) {
                    if ("FUTURE_RECOMMENDED".equalsIgnoreCase(proposalValue)) {
                        proposalReason = "DUE_IN_FUTURE";
                    } else if ("Recommended based on age".equalsIgnoreCase(proposalReason)
                            || "Recommended based on minimum interval from previous dose (catch-up)".equalsIgnoreCase(proposalReason)
                            || "Recommended based on interval".equalsIgnoreCase(proposalReason)
                            || "Recommended based on minimum interval from invalid dose".equalsIgnoreCase(proposalReason)
                            || "Recommended based on seasonal start date".equalsIgnoreCase(proposalReason)
                            || "Recommended based on minimum interval from live virus vaccine".equalsIgnoreCase(proposalReason)) {
                        proposalReason = "DUE_NOW";
                    } else if ("series complete".equalsIgnoreCase(proposalReason)) {
                        proposalReason = "COMPLETE";
                    } else if ("too old".equalsIgnoreCase(proposalReason)) {
                        proposalReason = "TOO_OLD";
                    } else if ("immune".equalsIgnoreCase(proposalReason)) {
                        proposalReason = "PROOF_OF_IMMUNITY";
                    } else {
                        logger.info("Skipping recommendation reason: ", proposalReason);
                        proposalReason = null;
                    }
                }
                if (proposalReason != null) {
                    reasons.add(CdsObjectFactory.getCD(proposalReason, Config.getCodeSystemOid("RECOMMENDATION_INTERPRETATION")));
                }

                try {
//                    if (pastDueDate != null) {
//                        Calendar cal = Calendar.getInstance();
//                        cal.setTime(pastDueDate);
//                        cal.add(Calendar.DATE, -1);
//                        pastDueDate = cal.getTime();
//                    }
                    testCase.addSubstanceAdministrationProposal(
                            vaccineGroup,
                            Config.getCodeSystemOid("VACCINE_GROUP"),
                            null,
                            Config.getCodeSystemOid("VACCINE"),
                            DateUtils.getISODateFormat(recommendedDate),
                            DateUtils.getISODateFormat(pastDueDate),
                            DateUtils.getISODateFormat(earliestDate),
                            null,
                            vaccineGroup,
                            Config.getCodeSystemOid("VACCINE_GROUP"),
                            proposalValue,
                            Config.getCodeSystemOid("RECOMMENDATION_VALUE"),
                            reasons);
                } catch (CdsException e) {
                    logger.error("addSubstanceAdministrationProposal Error @ " + location + " - " + e.getMessage());
                    logger.error(e.getMessage(), e);
                    testCase.setErrorMessage(e.getMessage());
                    callback.callback(testCase, testSheetName, false);
                    continue;
                }

                TestCase rawTestCase = testCase.getTestCase();
                CDSOutput cdsOutput = rawTestCase.getCdsOutput();

                // if there is a recommendation
                if (cdsOutput != null
                        && cdsOutput.getVmrOutput() != null
                        && cdsOutput.getVmrOutput().getPatient() != null
                        && cdsOutput.getVmrOutput().getPatient().getClinicalStatements() != null
                        && cdsOutput.getVmrOutput().getPatient().getClinicalStatements().getSubstanceAdministrationProposals() != null
                        && cdsOutput.getVmrOutput().getPatient().getClinicalStatements().getSubstanceAdministrationProposals().getSubstanceAdministrationProposal() != null) {

                    logger.info(METHODNAME, "proposal present - checking opencds for vaccine level recommendation");
                    CDSInput cdsInput = rawTestCase.getCdsInput();
                    Date iceExecutionDate = rawTestCase.getExecutiondate();
                    byte[] cdsObjectToByteArray = CdsObjectAssist.cdsObjectToByteArray(cdsInput, CDSInput.class);
                    byte[] evaluation = service.evaluate(cdsObjectToByteArray, scopingEntityId, businessId, version, iceExecutionDate);
                    CDSOutput result = CdsObjectAssist.cdsObjectFromByteArray(evaluation, CDSOutput.class);

                    if (result != null
                            && result.getVmrOutput() != null
                            && result.getVmrOutput().getPatient() != null
                            && result.getVmrOutput().getPatient().getClinicalStatements() != null
                            && result.getVmrOutput().getPatient().getClinicalStatements().getSubstanceAdministrationProposals() != null
                            && result.getVmrOutput().getPatient().getClinicalStatements().getSubstanceAdministrationProposals().getSubstanceAdministrationProposal() != null) {

                        logger.info(METHODNAME, "checking ICE result for vaccine recommendation...");

                        List<SubstanceAdministrationProposal> substanceAdministrationProposals = result.getVmrOutput().getPatient().getClinicalStatements().getSubstanceAdministrationProposals().getSubstanceAdministrationProposal();
                        List<SubstanceAdministrationProposal> existingSubstanceAdministrationProposals = cdsOutput.getVmrOutput().getPatient().getClinicalStatements().getSubstanceAdministrationProposals().getSubstanceAdministrationProposal();

                        logger.info(METHODNAME, "substanceAdministrationProposals=", substanceAdministrationProposals);
                        logger.info(METHODNAME, "existingSubstanceAdministrationProposals=", existingSubstanceAdministrationProposals);

                        for (SubstanceAdministrationProposal expectedSubstanceAdministrationProposal : existingSubstanceAdministrationProposals) {
                            if (expectedSubstanceAdministrationProposal != null) {
                                AdministrableSubstance existingSubstance = expectedSubstanceAdministrationProposal.getSubstance();
                                List<RelatedClinicalStatement> existingRelatedClinicalStatements = expectedSubstanceAdministrationProposal.getRelatedClinicalStatement();
                                if (existingSubstance != null
                                        && existingSubstance.getSubstanceCode() != null
                                        && existingRelatedClinicalStatements != null
                                        && existingRelatedClinicalStatements.size() == 1) {
                                    CD existingSubstanceCode = existingSubstance.getSubstanceCode();
                                    RelatedClinicalStatement existingRelatedClinicalStatement = existingRelatedClinicalStatements.get(0);
                                    if (existingRelatedClinicalStatement != null
                                            && existingRelatedClinicalStatement.getObservationResult() != null
                                            && existingRelatedClinicalStatement.getObservationResult().getObservationFocus() != null) {
                                        CD existingObservationFocus = existingRelatedClinicalStatement.getObservationResult().getObservationFocus();
                                        if (existingObservationFocus.getCode() != null
                                                && existingObservationFocus.getCodeSystem() != null
                                                && existingSubstanceCode.getCode() != null
                                                && existingSubstanceCode.getCodeSystem() != null) {
                                            for (SubstanceAdministrationProposal substanceAdministrationProposal : substanceAdministrationProposals) {
                                                if (substanceAdministrationProposal != null) {
                                                    AdministrableSubstance substance = substanceAdministrationProposal.getSubstance();
                                                    List<RelatedClinicalStatement> relatedClinicalStatements = substanceAdministrationProposal.getRelatedClinicalStatement();
                                                    if (substance != null
                                                            && substance.getSubstanceCode() != null
                                                            && relatedClinicalStatements != null
                                                            && relatedClinicalStatements.size() == 1) {
                                                        CD substanceCode = substance.getSubstanceCode();
                                                        RelatedClinicalStatement relatedClinicalStatement = relatedClinicalStatements.get(0);
                                                        if (relatedClinicalStatement != null
                                                                && relatedClinicalStatement.getObservationResult() != null
                                                                && relatedClinicalStatement.getObservationResult().getObservationFocus() != null) {
                                                            CD observationFocus = relatedClinicalStatement.getObservationResult().getObservationFocus();
                                                            if (existingObservationFocus.getCode().equalsIgnoreCase(observationFocus.getCode())
                                                                    && existingObservationFocus.getCodeSystem().equalsIgnoreCase(observationFocus.getCodeSystem())) {
                                                                if (!existingSubstanceCode.getCode().equalsIgnoreCase(substanceCode.getCode())
                                                                        && !existingSubstanceCode.getCodeSystem().equalsIgnoreCase(substanceCode.getCodeSystem())
                                                                        && Config.getCodeSystemOid("VACCINE").equalsIgnoreCase(substanceCode.getCodeSystem())) {
                                                                    logger.info(METHODNAME, "found ICE recommending vaccine substance for test: ", testIdCellValue);
                                                                    logger.info(METHODNAME, "    changing recommendation to code ", substanceCode.getCode(), " from ", existingSubstanceCode.getCode());
                                                                    logger.info(METHODNAME, "    changing recommendation to display name ", substanceCode.getDisplayName(), " from ", existingSubstanceCode.getDisplayName());
                                                                    logger.info(METHODNAME, "    changing recommendation to code system oid ", substanceCode.getCodeSystem(), " from ", existingSubstanceCode.getCodeSystem());
                                                                    logger.info(METHODNAME, "    changing recommendation to code system name ", substanceCode.getCodeSystemName(), " from ", existingSubstanceCode.getCodeSystemName());
                                                                    logger.info(METHODNAME, "    changing recommendation to original text ", substanceCode.getOriginalText(), " from ", existingSubstanceCode.getOriginalText());
                                                                    existingSubstanceCode.setCode(substanceCode.getCode());
                                                                    existingSubstanceCode.setDisplayName(substanceCode.getDisplayName());
                                                                    existingSubstanceCode.setCodeSystem(substanceCode.getCodeSystem());
                                                                    existingSubstanceCode.setCodeSystemName(substanceCode.getCodeSystemName());
                                                                    existingSubstanceCode.setOriginalText(substanceCode.getOriginalText());
                                                                    changedRecCount++;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                callback.callback(testCase, testCase.getGroupName(), true);
            }
//            if (i == 2) {
//                throw new UnsupportedOperationException("Blah");
//            }
        }
        logger.info(METHODNAME, "changedRecCount=", changedRecCount);
        logger.info("Evaluation Reasons to map: ", reasonsToMap);
        logger.info("Recommendation Value/Reasons to map: ", recommendationToMap);

    }
}
