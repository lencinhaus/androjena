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
 * org.apache.xerces.impl.dv.xs.DecimalDV
 * 
 * Jena:
 * com.hp.hpl.jena.datatypes.xsd.XSDDatatype
 * com.hp.hpl.jena.datatypes.xsd.impl.XSDBaseNumericType
 */

package it.polimi.dei.dbgroup.pedigree.androjena.xsd.impl.validators;

import it.polimi.dei.dbgroup.pedigree.androjena.xsd.XSDBuiltinTypeFormatException;
import it.polimi.dei.dbgroup.pedigree.androjena.xsd.impl.TypeValidator;

public abstract class BaseDecimalValidator extends TypeValidator {

	@Override
	public Object getActualValue(String content)
			throws XSDBuiltinTypeFormatException {

		return getDecimalValue(parseDecimal(content));
	}
	
	

	@Override
	public short getNormalizationType() {
		return NORMALIZE_TRIM;
	}

	@Override
	public int compare(Object o1, Object o2) {
		try {
			DecimalInfo d1 = parseDecimal(o1.toString());
			DecimalInfo d2 = parseDecimal(o2.toString());
			return decimalCompare(d1, d2);
		} catch (XSDBuiltinTypeFormatException ex) {
			return super.compare(o1, o2);
		}
	}
	
	private static final int decimalCompare(DecimalInfo d1, DecimalInfo d2)
	{
		if(d1.sign != d2.sign) return d1.sign > d2.sign ? GREATER_THAN : LESS_THAN;
		if(d1.sign == 0) return EQUAL;
		int ret = d1.sign * partsCompare(d1, d2);
		return ret > 0 ? GREATER_THAN : (ret < 0 ? LESS_THAN : EQUAL);
	}
	
	private static final int partsCompare(DecimalInfo d1, DecimalInfo d2)
	{
		int lenInt1 = d1.integerPart.length();
		int lenInt2 = d2.integerPart.length();
		if(lenInt1 != lenInt2) return lenInt1 - lenInt2;
		int ret = d1.integerPart.compareTo(d2.integerPart);
		if(ret != 0) return ret;
		return d1.fractionPart.compareTo(d2.fractionPart);
	}

	protected static class DecimalInfo {
		public int sign = 1;
		public String original = "";
		public String integerPart = "";
		public String fractionPart = "";
	}

	private static final DecimalInfo parseDecimal(String content)
			throws XSDBuiltinTypeFormatException {
		int len = content.length();
		if (len == 0)
			throwInvalidDecimalException(content, "empty");

		// these 4 variables are used to indicate where the integre/fraction
		// parts start/end.
		int intStart = 0, intEnd = 0, fracStart = 0, fracEnd = 0;
		final DecimalInfo decimal = new DecimalInfo();
		decimal.original = content;
		decimal.sign = 1;

		// Deal with leading sign symbol if present
		if (content.charAt(0) == '+') {
			// skip '+', so intStart should be 1
			intStart = 1;
		} else if (content.charAt(0) == '-') {
			// keep '-', so intStart is stil 0
			intStart = 1;
			decimal.sign = -1;
		}

		// skip leading zeroes in integer part
		int actualIntStart = intStart;
		while (actualIntStart < len && content.charAt(actualIntStart) == '0') {
			actualIntStart++;
		}

		// Find the ending position of the integer part
		for (intEnd = actualIntStart; intEnd < len
				&& TypeValidator.isDigit(content.charAt(intEnd)); intEnd++)
			;

		// Not reached the end yet
		if (intEnd < len) {
			// the remaining part is not ".DDD", error
			if (content.charAt(intEnd) != '.')
				throwInvalidDecimalException(content, "unexpected character '"
						+ content.charAt(intEnd) + "' at position " + intEnd);

			// fraction part starts after '.', and ends at the end of the input
			fracStart = intEnd + 1;
			fracEnd = len;
		}

		// no integer part, no fraction part, error.
		if (intStart == intEnd && fracStart == fracEnd)
			throwInvalidDecimalException(content,
					"no integer nor fraction part found");

		// ignore trailing zeroes in fraction part
		while (fracEnd > fracStart && content.charAt(fracEnd - 1) == '0') {
			fracEnd--;
		}

		// check whether there is non-digit characters in the fraction part
		for (int fracPos = fracStart; fracPos < fracEnd; fracPos++) {
			if (!TypeValidator.isDigit(content.charAt(fracPos)))
				throwInvalidDecimalException(content,
						"non-digit characters in fraction part");
		}

		int intDigits = intEnd - actualIntStart;
		int fracDigits = fracEnd - fracStart;
		decimal.integerPart = "";
		decimal.fractionPart = "";

		if (intDigits <= 0 && fracDigits <= 0)
			// ".00", treat it as "0"
			decimal.sign = 0;
		else {
			if (intDigits > 0)
				decimal.integerPart = content.substring(actualIntStart, intEnd);

			if (fracDigits > 0)
				decimal.fractionPart = content.substring(fracStart, fracEnd);
		}

		return decimal;
	}

	private static final void throwInvalidDecimalException(String content,
			String message) throws XSDBuiltinTypeFormatException {
		throw new XSDBuiltinTypeFormatException(content,
				"invalid decimal value: " + message);
	}

	protected abstract Object getDecimalValue(DecimalInfo decimal)
			throws XSDBuiltinTypeFormatException;
}
