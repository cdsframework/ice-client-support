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
package org.cdsframework.cds.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBResult;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import org.cdsframework.exceptions.CdsException;
import org.cdsframework.util.LogUtils;
import org.opencds.vmr.v1_0.schema.CDSInput;
import org.opencds.vmr.v1_0.schema.CDSOutput;
import org.opencds.vmr.v1_0.schema.ObjectFactory;
import org.xml.sax.ContentHandler;

/**
 * A static utility class for dealing with marshaling and un-marshaling objects.
 *
 * @author HLN Consulting, LLC
 */
public class MarshalUtils {

    /**
     * static logger
     */
    protected final static LogUtils logger = LogUtils.getLogger(MarshalUtils.class);
    private static final Map<String, JAXBContext> jaxbContextMap = new HashMap<String, JAXBContext>();

    /**
     * Get the jaxb context for the package name.
     *
     * @param classPackageName
     * @return
     * @throws CdsException
     */
    private static JAXBContext getJAXBContext(String classPackageName) throws CdsException {
        JAXBContext jaxbContext = null;
        try {
            if (classPackageName == null) {
                throw new CdsException("classPackageName cannot be null.");
            }
            if (!jaxbContextMap.containsKey(classPackageName)) {
                jaxbContext = JAXBContext.newInstance(classPackageName);
                jaxbContextMap.put(classPackageName, jaxbContext);
            } else {
                jaxbContext = jaxbContextMap.get(classPackageName);
            }
        } catch (JAXBException e) {
            logger.error(e);
            throw new CdsException(e.getMessage());
        }
        return jaxbContext;
    }

    private static Marshaller getMarshaller(Object jaxbElement) throws CdsException {
        if (jaxbElement == null) {
            throw new CdsException("jaxbElement cannot be null.");
        }
        return getMarshaller(jaxbElement.getClass());
    }

    private static Marshaller getMarshaller(Class klass) throws CdsException {
        Marshaller result = null;
        try {
            if (klass == null) {
                throw new CdsException("klass cannot be null.");
            }
            String classPackageName = ClassUtils.getClassPackageName(klass);
            logger.debug("classPackageName=", classPackageName);
            result = getJAXBContext(classPackageName).createMarshaller();
            result.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        } catch (JAXBException e) {
            logger.error(e);
            throw new CdsException(e.getMessage());
        }
        return result;
    }

    private static Unmarshaller getUnmarshaller(Class klass) throws CdsException {
        Unmarshaller result = null;
        try {
            if (klass == null) {
                throw new CdsException("klass cannot be null.");
            }
            String classPackageName = ClassUtils.getClassPackageName(klass);
            result = getJAXBContext(classPackageName).createUnmarshaller();
        } catch (JAXBException e) {
            logger.error(e);
            throw new CdsException(e.getMessage());
        }
        return result;
    }

    /**
     * Marshal an object to a supplied destination.
     *
     * @param jaxbElement
     * @param dst
     * @param schema
     * @throws CdsException
     */
    public static void marshal(Object jaxbElement, Object dst, Schema schema) throws CdsException {
        try {
            Marshaller marshaller = getMarshaller(jaxbElement);
            marshaller.setSchema(schema);
            if (jaxbElement instanceof CDSInput) {
                jaxbElement = new ObjectFactory().createCdsInput((CDSInput) jaxbElement);
            }
            if (jaxbElement instanceof CDSOutput) {
                jaxbElement = new ObjectFactory().createCdsOutput((CDSOutput) jaxbElement);
            }
            if (dst instanceof ContentHandler) {
                marshaller.marshal(jaxbElement, (ContentHandler) dst);
            } else if (dst instanceof OutputStream) {
                marshaller.marshal(jaxbElement, (OutputStream) dst);
            } else {
                throw new CdsException("Unsupported dst type: " + dst);
            }
        } catch (JAXBException e) {
            logger.error(e);
            throw new CdsException(e.getMessage());
        }
    }

    /**
     * Marshal an object to an OutputStream.
     *
     * @param jaxbElement
     * @param os
     * @throws CdsException
     */
    public static void marshal(Object jaxbElement, OutputStream os) throws CdsException {
        marshal(jaxbElement, os, null);
    }

    /**
     * Un-marshal an object from an InputStream.
     *
     * @param <S>
     * @param inputStream
     * @param returnType
     * @return
     * @throws CdsException
     */
    public static <S> S unmarshal(InputStream inputStream, Class<S> returnType) throws CdsException {
        return unmarshal(inputStream, true, returnType);
    }

    /**
     * Un-marshal an object from an InputStream after transforming the XML with
     * the supplied XSLT.
     *
     * @param <S>
     * @param inputStream
     * @param xslInputStream
     * @param returnType
     * @return
     * @throws CdsException
     */
    public static <S> S unmarshal(InputStream inputStream, InputStream xslInputStream, Class<S> returnType) throws
            CdsException {
        return unmarshal(inputStream, true, xslInputStream, returnType);
    }

    /**
     * Un-marshal an object from an InputStream with the option to ignore
     * namespaces.
     *
     * @param <S>
     * @param inputStream
     * @param namespaceAware
     * @param returnType
     * @return
     * @throws CdsException
     */
    public static <S> S unmarshal(InputStream inputStream, boolean namespaceAware, Class<S> returnType) throws
            CdsException {
        return unmarshal(inputStream, namespaceAware, null, returnType);
    }

    /**
     * Un-marshal an object from an InputStream after transforming the XML with
     * the supplied XSLT (optional) with the option to ignore namespaces.
     *
     * @param <S>
     * @param inputStream
     * @param namespaceAware
     * @param xslInputStream
     * @param returnType
     * @return
     * @throws CdsException
     */
    public static <S> S unmarshal(InputStream inputStream, boolean namespaceAware, InputStream xslInputStream, Class<S> returnType)
            throws
            CdsException {
        S result = null;
        try {
            Unmarshaller unmarshaller = getUnmarshaller(returnType);
            if (namespaceAware && xslInputStream == null) {
                Object unmarshalledObject = unmarshaller.unmarshal(inputStream);
                if (unmarshalledObject instanceof JAXBElement) {
                    result = (S) ((JAXBElement) unmarshalledObject).getValue();
                } else {
                    result = (S) unmarshalledObject;
                }
            } else {
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                InputStream localTransform = xslInputStream;
                if (!namespaceAware && xslInputStream == null) {
                    localTransform = MarshalUtils.class.getClassLoader().getResourceAsStream("nsrm.xsl");
                }
                StreamSource xslStreamSource = new StreamSource(localTransform);
                Transformer transformer = transformerFactory.newTransformer(xslStreamSource);
                StreamSource xmlStreamSource = new StreamSource(inputStream);
                JAXBContext jc = JAXBContext.newInstance(returnType);
                JAXBResult jaxbResult = new JAXBResult(jc);
                transformer.transform(xmlStreamSource, jaxbResult);
                result = (S) jaxbResult.getResult();
            }
        } catch (JAXBException e) {
            logger.error(e);
            throw new CdsException(e.getMessage());
        } catch (ClassCastException e) {
            logger.error(e);
            throw new CdsException(e.getMessage());
        } catch (TransformerConfigurationException e) {
            logger.error(e);
            throw new CdsException(e.getMessage());
        } catch (TransformerException e) {
            logger.error(e);
            throw new CdsException(e.getMessage());
        }
        return result;
    }

    /**
     * Marshal an object to a byte array.
     *
     * @param dataObject
     * @return
     * @throws CdsException
     */
    public static byte[] marshalObject(Object dataObject) throws CdsException {
        byte[] result = null;
        try {
            if (dataObject == null) {
                throw new IllegalArgumentException("dataObject cannot be null.");
            }
            Marshaller marshaller = getMarshaller(dataObject.getClass());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            marshaller.marshal(dataObject, stream);
            result = stream.toByteArray();
        } catch (JAXBException e) {
            logger.error("JAXB marshall error: ", e.getMessage());
        } finally {
        }
        return result;
    }

    /**
     * Un-marshal an object from an InputStream to the supplied object type.
     *
     * @param <S>
     * @param inputStream
     * @param cdsObjectClass
     * @return
     * @throws CdsException
     */
    public static <S> S unmarshalObject(InputStream inputStream, Class<S> cdsObjectClass) throws CdsException {
        S result = null;
        try {
            Unmarshaller unmarshaller = getUnmarshaller(cdsObjectClass);
            result = (S) unmarshaller.unmarshal(inputStream);
        } catch (JAXBException e) {
            logger.error("JAXB unmarshall error: ", e.getMessage());
        } finally {
        }
        return result;
    }
}
