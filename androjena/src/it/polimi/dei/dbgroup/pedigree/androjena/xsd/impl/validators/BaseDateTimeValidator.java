/*
 * Copyright 2010 Lorenzo Carrara
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *      
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*	This code is mainly adapated from Xerces 2.6.0 and Jena 2.6.2 
 * Xerces copyright and license: 
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights reserved.
 * License http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Jena copyright and license:
 * Copyright 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * Specific source classes:
 * 
 * Xerces:
 * org.apache.xerces.impl.dv.xs.AbstractDateTimeDV
 * 
 * Jena:
 * com.hp.hpl.jena.datatypes.xsd.XSDDatatype
 * com.hp.hpl.jena.datatypes.xsd.impl.XSDAbstractDateTimeType
 */

package it.polimi.dei.dbgroup.pedigree.androjena.xsd.impl.validators;

import it.polimi.dei.dbgroup.pedigree.androjena.xsd.XSDBuiltinTypeFormatException;
import it.polimi.dei.dbgroup.pedigree.androjena.xsd.impl.TypeValidator;

import com.hp.hpl.jena.datatypes.xsd.AbstractDateTime;


public abstract class BaseDateTimeValidator extends TypeValidator {

	/** Mask to indicate whether year is present */
	public static final short YEAR_MASK = 0x1;

	/** Mask to indicate whether month is present */
	public static final short MONTH_MASK = 0x2;

	/** Mask to indicate whether day is present */
	public static final short DAY_MASK = 0x4;

	/** Mask to indicate whether time is present */
	public static final short TIME_MASK = 0x8;

	/** Mask to indicate all date/time are present */
	public static final short FULL_MASK = 0xf;

	// --------------------------------------------------------------------
	// This code is adapated from Xerces 2.6.0 AbstractDateTimeDV.
	// Copyright (c) 1999-2003 The Apache Software Foundation. All rights
	// reserved.
	// --------------------------------------------------------------------

	// define constants
	protected final static int CY = 0, M = 1, D = 2, h = 3, m = 4, s = 5,
			ms = 6, msscale = 8, utc = 7, hh = 0, mm = 1;

	// size for all objects must have the same fields:
	// CCYY, MM, DD, h, m, s, ms + timeZone
	protected final static int TOTAL_SIZE = 9;

	// define constants to be used in assigning default values for
	// all date/time excluding duration
	protected final static int YEAR = 2000;
	protected final static int MONTH = 01;
	protected final static int DAY = 15;

	@Override
	public short getNormalizationType() {
		return NORMALIZE_TRIM;
	}

	@Override
	public Object getActualValue(String content)
			throws XSDBuiltinTypeFormatException {
		try {
			return parse(content);
		} catch (Exception ex) {
			throw new XSDBuiltinTypeFormatException(content,
					"invalid dateTime data", ex);
		}
	}

	protected abstract AbstractDateTime parse(String content);

	/**
	 * Computes index of given char within StringBuffer
	 * 
	 * @param start
	 * @param end
	 * @param ch
	 *            character to look for in StringBuffer
	 * @return index of ch within StringBuffer
	 */
	protected int indexOf(String buffer, int start, int end, char ch) {
		for (int i = start; i < end; i++) {
			if (buffer.charAt(i) == ch) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Parses date CCYY-MM-DD
	 * 
	 * @param buffer
	 * @param start
	 *            start position
	 * @param end
	 *            end position
	 * @param date
	 * @exception RuntimeException
	 */
	protected int getDate(String buffer, int start, int end, int[] date)
			throws RuntimeException {

		start = getYearMonth(buffer, start, end, date);

		if (buffer.charAt(start++) != '-') {
			throw new RuntimeException("CCYY-MM must be followed by '-' sign");
		}
		int stop = start + 2;
		date[D] = parseInt(buffer, start, stop);
		return stop;
	}

	/**
	 * Parses date CCYY-MM
	 * 
	 * @param buffer
	 * @param start
	 *            start position
	 * @param end
	 *            end position
	 * @param date
	 * @exception RuntimeException
	 */
	protected int getYearMonth(String buffer, int start, int end, int[] date)
			throws RuntimeException {

		if (buffer.charAt(0) == '-') {
			// REVISIT: date starts with preceding '-' sign
			// do we have to do anything with it?
			//
			start++;
		}
		int i = indexOf(buffer, start, end, '-');
		if (i == -1)
			throw new RuntimeException("Year separator is missing or misplaced");
		int length = i - start;
		if (length < 4) {
			throw new RuntimeException("Year must have 'CCYY' format");
		} else if (length > 4 && buffer.charAt(start) == '0') {
			throw new RuntimeException(
					"Leading zeros are required if the year value would otherwise have fewer than four digits; otherwise they are forbidden");
		}
		date[CY] = parseIntYear(buffer, i);
		if (buffer.charAt(i) != '-') {
			throw new RuntimeException("CCYY must be followed by '-' sign");
		}
		start = ++i;
		i = start + 2;
		date[M] = parseInt(buffer, start, i);
		return i; // fStart points right after the MONTH
	}

	/**
	 * Given start and end position, parses string value
	 * 
	 * @param buffer
	 *            string to parse
	 * @param start
	 *            start position
	 * @param end
	 *            end position
	 * @return return integer representation of characters
	 */
	protected int parseInt(String buffer, int start, int end)
			throws NumberFormatException {
		// REVISIT: more testing on this parsing needs to be done.
		int radix = 10;
		int result = 0;
		int digit = 0;
		int limit = -Integer.MAX_VALUE;
		int multmin = limit / radix;
		int i = start;
		do {
			digit = getDigit(buffer.charAt(i));
			if (digit < 0)
				throw new NumberFormatException("'" + buffer
						+ "' has wrong format");
			if (result < multmin)
				throw new NumberFormatException("'" + buffer
						+ "' has wrong format");
			result *= radix;
			if (result < limit + digit)
				throw new NumberFormatException("'" + buffer
						+ "' has wrong format");
			result -= digit;

		} while (++i < end);
		return -result;
	}

	/**
	 * Shared code from Date and YearMonth datatypes. Finds if time zone sign is
	 * present
	 * 
	 * @param end
	 * @param date
	 * @exception RuntimeException
	 */
	protected void parseTimeZone(String buffer, int start, int end, int[] date,
			int[] timeZone) throws RuntimeException {

		// fStart points right after the date

		if (start < end) {
			int sign = findUTCSign(buffer, start, end);
			if (sign < 0) {
				throw new RuntimeException("Error in month parsing");
			} else {
				getTimeZone(buffer, date, sign, end, timeZone);
			}
		}
	}

	/**
	 * Returns <code>true</code> if the character at start is 'Z', '+' or '-'.
	 */
	protected final boolean isNextCharUTCSign(String buffer, int start, int end) {
		if (start < end) {
			char c = buffer.charAt(start);
			return (c == 'Z' || c == '+' || c == '-');
		}
		return false;
	}

	// parse Year differently to support negative value.
	protected int parseIntYear(String buffer, int end) {
		int radix = 10;
		int result = 0;
		boolean negative = false;
		int i = 0;
		int limit;
		int multmin;
		int digit = 0;

		if (buffer.charAt(0) == '-') {
			negative = true;
			limit = Integer.MIN_VALUE;
			i++;

		} else {
			limit = -Integer.MAX_VALUE;
		}
		multmin = limit / radix;
		while (i < end) {
			digit = getDigit(buffer.charAt(i++));
			if (digit < 0)
				throw new NumberFormatException("'" + buffer
						+ "' has wrong format");
			if (result < multmin)
				throw new NumberFormatException("'" + buffer
						+ "' has wrong format");
			result *= radix;
			if (result < limit + digit)
				throw new NumberFormatException("'" + buffer
						+ "' has wrong format");
			result -= digit;
		}

		if (negative) {
			if (i > 1)
				return result;
			else
				throw new NumberFormatException("'" + buffer
						+ "' has wrong format");
		}
		return -result;

	}

	/**
	 * Parses time hh:mm:ss.sss and time zone if any
	 * 
	 * @param start
	 * @param end
	 * @param data
	 * @exception RuntimeException
	 */
	protected void getTime(String buffer, int start, int end, int[] data,
			int[] timeZone) throws RuntimeException {

		int stop = start + 2;

		// get hours (hh)
		data[h] = parseInt(buffer, start, stop);

		// get minutes (mm)
		if (buffer.charAt(stop++) != ':') {
			throw new RuntimeException("Error in parsing time zone");
		}
		start = stop;
		stop = stop + 2;
		data[m] = parseInt(buffer, start, stop);

		// get seconds (ss)
		if (buffer.charAt(stop++) != ':') {
			throw new RuntimeException("Error in parsing time zone");
		}
		start = stop;
		stop = stop + 2;
		data[s] = parseInt(buffer, start, stop);

		if (stop == end)
			return;

		// get miliseconds (ms)
		start = stop;
		int milisec = buffer.charAt(start) == '.' ? start : -1;

		// find UTC sign if any
		int sign = findUTCSign(buffer, start, end);

		// parse miliseconds
		if (milisec != -1) {
			// The end of millisecond part is between . and
			// either the end of the UTC sign
			start = sign < 0 ? end : sign;
			int msEnd = start;
			while (buffer.charAt(msEnd - 1) == '0')
				msEnd--;
			data[ms] = parseInt(buffer, milisec + 1, msEnd);
			data[msscale] = msEnd - milisec - 1;
		}

		// parse UTC time zone (hh:mm)
		if (sign > 0) {
			if (start != sign)
				throw new RuntimeException("Error in parsing time zone");
			getTimeZone(buffer, data, sign, end, timeZone);
		} else if (start != end) {
			throw new RuntimeException("Error in parsing time zone");
		}
	}

	/**
	 * Return index of UTC char: 'Z', '+', '-'
	 * 
	 * @param start
	 * @param end
	 * @return index of the UTC character that was found
	 */
	protected int findUTCSign(String buffer, int start, int end) {
		int c;
		for (int i = start; i < end; i++) {
			c = buffer.charAt(i);
			if (c == 'Z' || c == '+' || c == '-') {
				return i;
			}

		}
		return -1;
	}

	protected double parseSecond(String buffer, int start, int end)
			throws NumberFormatException {
		int dot = -1;
		for (int i = start; i < end; i++) {
			char ch = buffer.charAt(i);
			if (ch == '.')
				dot = i;
			else if (ch > '9' || ch < '0')
				throw new NumberFormatException("'" + buffer
						+ "' has wrong format");
		}
		if (dot == -1) {
			if (start + 2 != end)
				throw new NumberFormatException("'" + buffer
						+ "' has wrong format");
		} else if (start + 2 != dot || dot + 1 == end) {
			throw new NumberFormatException("'" + buffer + "' has wrong format");
		}
		return Double.parseDouble(buffer.substring(start, end));
	}

	/**
	 * Parses time zone: 'Z' or {+,-} followed by hh:mm
	 * 
	 * @param data
	 * @param sign
	 * @exception RuntimeException
	 */
	protected void getTimeZone(String buffer, int[] data, int sign, int end,
			int[] timeZone) throws RuntimeException {
		data[utc] = buffer.charAt(sign);

		if (buffer.charAt(sign) == 'Z') {
			if (end > (++sign)) {
				throw new RuntimeException("Error in parsing time zone");
			}
			return;
		}
		if (sign <= (end - 6)) {

			int negate = buffer.charAt(sign) == '-' ? -1 : 1;
			// parse hr
			int stop = ++sign + 2;
			timeZone[hh] = negate * parseInt(buffer, sign, stop);
			if (buffer.charAt(stop++) != ':') {
				throw new RuntimeException("Error in parsing time zone");
			}

			// parse min
			timeZone[mm] = negate * parseInt(buffer, stop, stop + 2);

			if (stop + 2 != end) {
				throw new RuntimeException("Error in parsing time zone");
			}
		} else {
			throw new RuntimeException("Error in parsing time zone");
		}
	}

	/**
	 * Validates given date/time object accoring to W3C PR Schema [D.1 ISO 8601
	 * Conventions]
	 * 
	 * @param data
	 */
	protected void validateDateTime(int[] data, int[] timeZone) {

		// REVISIT: should we throw an exception for not valid dates
		// or reporting an error message should be sufficient?

		/**
		 * XML Schema 1.1 - RQ-123: Allow year 0000 in date related types.
		 */
		if (data[CY] == 0) {
			throw new RuntimeException(
					"The year \"0000\" is an illegal year value");

		}

		if (data[M] < 1 || data[M] > 12) {
			throw new RuntimeException("The month must have values 1 to 12");

		}

		// validate days
		if (data[D] > maxDayInMonthFor(data[CY], data[M]) || data[D] < 1) {
			throw new RuntimeException("The day must have values 1 to 31");
		}

		// validate hours
		if (data[h] > 23 || data[h] < 0) {
			if (data[h] == 24 && data[m] == 0 && data[s] == 0) {
				data[h] = 0;
				if (++data[D] > maxDayInMonthFor(data[CY], data[M])) {
					data[D] = 1;
					if (++data[M] > 12) {
						data[M] = 1;
						if (++data[CY] == 0) {
							data[CY] = 1;
						}
					}
				}
			} else {
				throw new RuntimeException(
						"Hour must have values 0-23, unless 24:00:00");
			}
		}

		// validate
		if (data[m] > 59 || data[m] < 0) {
			throw new RuntimeException("Minute must have values 0-59");
		}

		// validate
		if (data[s] >= 60 || data[s] < 0) {
			throw new RuntimeException("Second must have values 0-59");

		}
		
		// validate milliseconds
		if(data[ms] < 0 || data[msscale] < 0) {
			throw new RuntimeException("Milliseconds must be non negative");
		}

		// validate timezone
		if (timeZone != null) {
			if (timeZone[hh] > 14 || timeZone[hh] < -14) {
				throw new RuntimeException(
						"Time zone should have range -14:00 to +14:00");
			} else {
				if ((timeZone[hh] == 14 || timeZone[hh] == -14)
						&& timeZone[mm] != 0)
					throw new RuntimeException(
							"Time zone should have range -14:00 to +14:00");
				else if (timeZone[mm] > 59 || timeZone[mm] < -59)
					throw new RuntimeException("Minute must have values 0-59");
			}
		}

	}

	/**
	 * Given {year,month} computes maximum number of days for given month
	 * 
	 * @param year
	 * @param month
	 * @return integer containg the number of days in a given month
	 */
	protected int maxDayInMonthFor(int year, int month) {
		// validate days
		if (month == 4 || month == 6 || month == 9 || month == 11) {
			return 30;
		} else if (month == 2) {
			if (isLeapYear(year)) {
				return 29;
			} else {
				return 28;
			}
		} else {
			return 31;
		}
	}

	private boolean isLeapYear(int year) {

		// REVISIT: should we take care about Julian calendar?
		return ((year % 4 == 0) && ((year % 100 != 0) || (year % 400 == 0)));
	}
}
