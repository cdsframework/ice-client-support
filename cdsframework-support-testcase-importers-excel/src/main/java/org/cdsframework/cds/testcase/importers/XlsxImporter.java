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

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.datatype.DatatypeConfigurationException;
import org.apache.poi.POIXMLProperties;
import org.apache.poi.POIXMLProperties.CoreProperties;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.cdsframework.exceptions.CdsException;

/**
 *
 * @author HLN Consulting, LLC
 */
public class XlsxImporter extends TestCaseImporter {

	public XlsxImporter() {
		super(XlsxImporter.class);
	}

	public void importFromFile(byte[] data, TestImportCallback callback)
			throws CdsException, FileNotFoundException, IOException,
			DatatypeConfigurationException {
		importFromInputStream(new ByteArrayInputStream(data), callback);
	}

    @Override
	public void importFromFile(String filename, TestImportCallback callback)
			throws CdsException {
		try {
			importFromInputStream(new FileInputStream(filename), callback);
		} catch (FileNotFoundException e) {
			throw new CdsException(e.getMessage());
		} finally {

		}
	}

	@Override
	public void importFromInputStream(InputStream inputStream,
			TestImportCallback callback) throws CdsException {
		final String METHODNAME = "importFromInputStream ";
		logger.logBegin(METHODNAME);
		try {

			XSSFWorkbook wb = new XSSFWorkbook(inputStream);
			POIXMLProperties properties = wb.getProperties();
			CoreProperties coreProperties = properties.getCoreProperties();
			String category = coreProperties.getCategory();
			logger.info("category: " + category);
			if ("V1".equals(category)) {
				XlsxV1Helper.importFromWorkBook(wb, callback);
			} else if ("CDC".equals(category)) {
				XlsxCdcHelper.importFromWorkBook(wb, callback);
			} else {
				XlsxV2Helper.importFromWorkBook(wb, callback);
			}
		} catch (FileNotFoundException e) {
			logger.error(e);
			throw new CdsException(e.getMessage());
		} catch (CdsException e) {
			logger.error(e);
			throw new CdsException(e.getMessage());
		} catch (IOException e) {
			logger.error(e);
			throw new CdsException(e.getMessage());
		} catch (DatatypeConfigurationException e) {
			logger.error(e);
			throw new CdsException(e.getMessage());
		} finally {
			logger.logEnd(METHODNAME);
		}
	}
}
