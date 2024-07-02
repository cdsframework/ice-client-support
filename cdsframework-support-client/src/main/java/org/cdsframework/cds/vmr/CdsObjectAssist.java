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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdsframework.cds.util.MarshalUtils;
import org.cdsframework.exceptions.CdsException;

/**
 * Utility class with static methods for CDS object manipulation. See methods for examples.
 *
 * @author HLN Consulting, LLC
 */
public class CdsObjectAssist {

    /**
     * static logger.
     */
    protected final static Logger logger = LogManager.getLogger();

    /**
     * Converts a CDS object to a byte array.
     *
     * For example:
     * <pre>
     *     CdsOutputWrapper output = CdsOutputWrapper.getCdsOutputWrapper();
     *     output.setPatientGender("F");
     *     output.setPatientBirthTime("19830630");
     *     byte[] tmp = CdsObjectAssist.cdsObjectToByteArray(output.getCdsObject(), CdsOutput.class);
     * </pre>
     *
     * @param <S>
     * @param cdsObject
     * @param cdsObjectClass
     * @return
     * @throws CdsException
     */
    public static <S> byte[] cdsObjectToByteArray(S cdsObject, Class<S> cdsObjectClass)
            throws CdsException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        MarshalUtils.marshal(cdsObject, stream);
        return stream.toByteArray();
    }

    /**
     * Writes a CDS object out to a file.
     *
     * For example:
     * <pre>
     *     CdsOutputWrapper output = CdsOutputWrapper.getCdsOutputWrapper();
     *     output.setPatientGender("F");
     *     output.setPatientBirthTime("19830630");
     *     String filename = CdsObjectAssist.cdsObjectToFile(output.getCdsObject(), null, "sampleCdsOutput.xml");
     * </pre>
     *
     * @param <S>
     * @param cdsObject
     * @param path
     * @param filename
     * @return
     * @throws CdsException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static <S> String cdsObjectToFile(S cdsObject, String path, String filename)
            throws CdsException, FileNotFoundException, IOException {
        String fullPath = (path == null || path.isEmpty() ? "" : path + "/") + filename + ".xml";

        File file = new File(fullPath);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        try {
            MarshalUtils.marshal(cdsObject, fileOutputStream);
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                logger.error(e);
            }
        }
        return fullPath;
    }

    /**
     * Parses a CDS object from a file to an instance of that object.
     *
     * For example:
     * <pre>
     *     CdsOutput cdsOutputFromFile = CdsObjectAssist.cdsObjectFromFile("src/main/resources/sampleCdsOutput.xml", CdsOutput.class);
     *     CdsInput cdsInputFromFile = CdsObjectAssist.cdsObjectFromFile("src/main/resources/sampleCdsInput.xml", CdsInput.class);
     * </pre>
     *
     * @param <S>
     * @param fileName
     * @param cdsObjectClass
     * @return
     * @throws CdsException
     */
    public static <S> S cdsObjectFromFile(String fileName, Class<S> cdsObjectClass)
            throws CdsException {
        S cdsObject = null;
        try {
            cdsObject = cdsObjectFromStream(new FileInputStream(fileName), cdsObjectClass);
        } catch (FileNotFoundException e) {
            logger.error(e);
            throw new CdsException(e.getMessage());
        }
        return cdsObject;
    }

    /**
     * Parses a CDS object from a byte array to an instance of that object.
     *
     * For example:
     * <pre>
     *     CdsOutputWrapper output = CdsOutputWrapper.getCdsOutputWrapper();
     *     output.setPatientGender("F");
     *     output.setPatientBirthTime("19830630");
     *     byte[] tmp = CdsObjectAssist.cdsObjectToByteArray(output.getCdsObject(), CdsOutput.class);
     *     CdsOutput cdsObjectFromByteArray = CdsObjectAssist.cdsObjectFromByteArray(tmp, CdsOutput.class);
     * </pre>
     *
     * @param <S>
     * @param bytes
     * @param cdsObjectClass
     * @return
     * @throws CdsException
     */
    public static <S> S cdsObjectFromByteArray(byte[] bytes, Class<S> cdsObjectClass)
            throws CdsException {
        return cdsObjectFromStream(new ByteArrayInputStream(bytes), cdsObjectClass);
    }

    /**
     * Parses a CDS object from an InputStream to an instance of that object.
     *
     * For example:
     * <pre>
     * </pre>
     *
     * @param <S>
     * @param inputStream
     * @param cdsObjectClass
     * @return
     * @throws CdsException
     */
    public static <S> S cdsObjectFromStream(InputStream inputStream, Class<S> cdsObjectClass)
            throws CdsException {
        S cdsObject = null;
        cdsObject = MarshalUtils.unmarshal(inputStream, cdsObjectClass);

        return cdsObject;
    }

    /**
     * Converts a CDS object to a String instance.
     *
     * For example:
     * <pre>
     *     CdsOutputWrapper output = CdsOutputWrapper.getCdsOutputWrapper();
     *     output.setPatientGender("F");
     *     output.setPatientBirthTime("19830630");
     *     System.out.println(CdsObjectAssist.cdsObjectToString(output.getCdsObject(), CdsOutput.class));
     * </pre>
     *
     * @param <S>
     * @param cdsObject
     * @param cdsObjectClass
     * @return
     * @throws CdsException
     */
    public static <S> String cdsObjectToString(S cdsObject, Class<S> cdsObjectClass)
            throws CdsException {
        return new String(cdsObjectToByteArray(cdsObject, cdsObjectClass));
    }
}
