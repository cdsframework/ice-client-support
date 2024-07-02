/**
 * The opencds support client project implements some base opencds artifacts and functionality.
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
package org.opencds.support.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import jakarta.xml.bind.DatatypeConverter;

/**
 *
 * @author HLN Consulting, LLC
 */
public class DateUtils {

    public static String getISODateFormat(Date date) {
        String result;
        if (date == null) {
            result = "";
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            result = formatter.format(date);
        }
        return result;
    }

    public static String getISODatetimeFormat(Date date) {
        String result;
        if (date == null) {
            result = "";
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            result = formatter.format(date);
        }
        return result;
    }

    public static Date parseISODateFormat(String dateString) throws ParseException {
        Date result;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        if (dateString == null || dateString.trim().isEmpty()) {
            result = null;
        } else {
            result = formatter.parse(dateString);
        }
        return result;
    }

    public static Date parseISODatetimeFormat(String dateString) throws ParseException {
        Date result;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        if (dateString == null || dateString.trim().isEmpty()) {
            result = null;
        } else {
            result = formatter.parse(dateString);
        }
        return result;
    }

    public static Date parseDate(String s) {
        if (s == null) {
            return null;
        }
        return DatatypeConverter.parseDate(s).getTime();
    }

    public static String printDate(Date dt) {
        if (dt == null) {
            return null;
        }
        Calendar cal = new GregorianCalendar();
        cal.setTime(dt);
        return DatatypeConverter.printDate(cal);
    }

    public static Date parseDateTime(String s) {
        if (s == null) {
            return null;
        }
        return DatatypeConverter.parseDateTime(s).getTime();
    }

    public static String printDateTime(Date dt) {
        if (dt == null) {
            return null;
        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return df.format(dt);
    }
}
