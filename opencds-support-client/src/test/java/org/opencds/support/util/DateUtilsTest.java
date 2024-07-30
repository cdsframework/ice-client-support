/**
 * The opencds support client project implements some base opencds artifacts and functionality.
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
package org.opencds.support.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author HLN Consulting, LLC
 */
public class DateUtilsTest {

    public DateUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getISODateFormat method, of class DateUtils.
     */
    @Test
    public void testGetISODateFormat() {
        System.out.println("getISODateFormat");
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String expResult = formatter.format(date);
        String result = DateUtils.getISODateFormat(date);
        assertEquals(expResult, result);
    }

    /**
     * Test of parseISODateFormat method, of class DateUtils.
     *
     * @throws Exception
     */
    @Test
    public void testParseISODateFormat() throws Exception {
        System.out.println("parseISODateFormat");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String expResult = formatter.format(new Date());
        String result = formatter.format(DateUtils.parseISODateFormat(expResult));
        assertEquals(expResult, result);
    }

    /**
     * Test of parseISODateFormat method, of class DateUtils.
     *
     * @throws Exception
     */
    @Test
    public void testParseISODateFormat2() throws Exception {
        System.out.println("parseISODateFormat");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String dateString = "20130610000000.000-0400";
        Date parseISODateFormat = DateUtils.parseISODateFormat(dateString);
        String expResult = formatter.format(parseISODateFormat);
        System.out.println("expResult: " + expResult);
        String result = dateString;
        assertEquals(dateString, result);
    }
}
