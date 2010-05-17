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
 * org.apache.xerces.impl.dv.xs.DurationDV
 * 
 * Jena:
 * com.hp.hpl.jena.datatypes.xsd.XSDDatatype
 * com.hp.hpl.jena.datatypes.xsd.impl.XSDAbstractDateTimeType
 * com.hp.hpl.jena.datatypes.xsd.impl.XSDDurationType
 */

package it.polimi.dei.dbgroup.pedigree.androjena.xsd.impl.validators;

import com.hp.hpl.jena.datatypes.DatatypeFormatException;
import com.hp.hpl.jena.datatypes.xsd.AbstractDateTime;
import com.hp.hpl.jena.datatypes.xsd.XSDDuration;

public class DurationValidator extends BaseDateTimeValidator {

	public static final int DURATION_TYPE = 0;
	public static final int YEARMONTHDURATION_TYPE = 1;
	public static final int DAYTIMEDURATION_TYPE = 2;

	@Override
	protected AbstractDateTime parse(String str) {
		int len = str.length();
		int[] date = new int[TOTAL_SIZE];

		int start = 0;
		char c = str.charAt(start++);
		if (c != 'P' && c != '-') {
			throw new DatatypeFormatException(
					"Internal error: validated duration failed to parse(1)");
		} else {
			date[utc] = (c == '-') ? '-' : 0;
			if (c == '-' && str.charAt(start++) != 'P') {
				throw new DatatypeFormatException(
						"Internal error: validated duration failed to parse(2)");
			}
		}

		int negate = 1;
		// negative duration
		if (date[utc] == '-') {
			negate = -1;

		}

		// at least one number and designator must be seen after P
		boolean designator = false;

		int endDate = indexOf(str, start, len, 'T');
		if (endDate == -1) {
			endDate = len;
		}
		// find 'Y'
		int end = indexOf(str, start, endDate, 'Y');
		if (end != -1) {
			// scan year
			date[CY] = negate * parseInt(str, start, end);
			start = end + 1;
			designator = true;
		}

		end = indexOf(str, start, endDate, 'M');
		if (end != -1) {
			// scan month
			date[M] = negate * parseInt(str, start, end);
			start = end + 1;
			designator = true;
		}

		end = indexOf(str, start, endDate, 'D');
		if (end != -1) {
			// scan day
			date[D] = negate * parseInt(str, start, end);
			start = end + 1;
			designator = true;
		}

		if (len == endDate && start != len) {
			throw new DatatypeFormatException(
					"Internal error: validated duration failed to parse(3)");
		}
		if (len != endDate) {
			// scan hours, minutes, seconds
			// REVISIT: can any item include a decimal fraction or only seconds?
			//

			end = indexOf(str, ++start, len, 'H');
			if (end != -1) {
				// scan hours
				date[h] = negate * parseInt(str, start, end);
				start = end + 1;
				designator = true;
			}

			end = indexOf(str, start, len, 'M');
			if (end != -1) {
				// scan min
				date[m] = negate * parseInt(str, start, end);
				start = end + 1;
				designator = true;
			}

			end = indexOf(str, start, len, 'S');
			if (end != -1) {
				// scan seconds
				int mlsec = indexOf(str, start, end, '.');
				if (mlsec > 0) {
					date[s] = negate * parseInt(str, start, mlsec);
					int msEnd = end;
					while (str.charAt(msEnd - 1) == '0')
						msEnd--;
					date[ms] = negate * parseInt(str, mlsec + 1, msEnd);
					date[msscale] = msEnd - mlsec - 1;
				} else {
					date[s] = negate * parseInt(str, start, end);
				}
				start = end + 1;
				designator = true;
			}

			// no additional data shouls appear after last item
			// P1Y1M1DT is illigal value as well
			if (start != len || str.charAt(--start) == 'T') {
				throw new RuntimeException();
			}
		}

		if (!designator) {
			throw new RuntimeException("no number or designator seen after P");
		}

		return new XSDDuration(date);
	}

}
