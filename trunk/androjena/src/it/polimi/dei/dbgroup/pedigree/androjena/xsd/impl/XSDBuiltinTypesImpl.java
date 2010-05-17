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
 * org.apache.xerces.impl.dv.SchemaDVFactory
 * org.apache.xerces.impl.dv.xs.SchemaDVFactoryImpl
 */
package it.polimi.dei.dbgroup.pedigree.androjena.xsd.impl;

import it.polimi.dei.dbgroup.pedigree.androjena.xsd.XSDBuiltinType;
import it.polimi.dei.dbgroup.pedigree.androjena.xsd.XSDBuiltinTypes;

import java.util.HashMap;
import java.util.Map;


public final class XSDBuiltinTypesImpl extends XSDBuiltinTypes {
	private static final Map<String, XSDBuiltinType> types = new HashMap<String, XSDBuiltinType>();

	static {
		createTypes();
	}

	private static final void createTypes() {
		// all schema simple type names
		final String ANYSIMPLETYPE = "anySimpleType";
		final String ANYURI = "anyURI";
		final String BASE64BINARY = "base64Binary";
		final String BOOLEAN = "boolean";
		final String BYTE = "byte";
		final String DATE = "date";
		final String DATETIME = "dateTime";
		final String DAY = "gDay";
		final String DECIMAL = "decimal";
		final String DOUBLE = "double";
		final String DURATION = "duration";
		final String ENTITY = "ENTITY";
		//final String ENTITIES = "ENTITIES";
		final String FLOAT = "float";
		final String HEXBINARY = "hexBinary";
		final String ID = "ID";
		final String IDREF = "IDREF";
		//final String IDREFS = "IDREFS";
		final String INT = "int";
		final String INTEGER = "integer";
		final String LANGUAGE = "language";
		final String LONG = "long";
		final String NAME = "Name";
		final String NEGATIVEINTEGER = "negativeInteger";
		final String MONTH = "gMonth";
		final String MONTHDAY = "gMonthDay";
		final String NCNAME = "NCName";
		final String NMTOKEN = "NMTOKEN";
		//final String NMTOKENS = "NMTOKENS";
		final String NONNEGATIVEINTEGER = "nonNegativeInteger";
		final String NONPOSITIVEINTEGER = "nonPositiveInteger";
		final String NORMALIZEDSTRING = "normalizedString";
		final String NOTATION = "NOTATION";
		final String POSITIVEINTEGER = "positiveInteger";
		final String QNAME = "QName";
		final String SHORT = "short";
		final String STRING = "string";
		final String TIME = "time";
		final String TOKEN = "token";
		final String UNSIGNEDBYTE = "unsignedByte";
		final String UNSIGNEDINT = "unsignedInt";
		final String UNSIGNEDLONG = "unsignedLong";
		final String UNSIGNEDSHORT = "unsignedShort";
		final String YEAR = "gYear";
		final String YEARMONTH = "gYearMonth";

		XSDFacets facets = new XSDFacets();

		XSDBuiltinTypeImpl anySimpleType = XSDBuiltinTypeImpl.ANY_SIMPLE_TYPE;
		types.put(ANYSIMPLETYPE, anySimpleType);

		types.put(FLOAT, new XSDBuiltinTypeImpl(anySimpleType, FLOAT,
				TypeValidator.FLOAT));

		types.put(DOUBLE, new XSDBuiltinTypeImpl(anySimpleType, DOUBLE,
				TypeValidator.DOUBLE));

		XSDBuiltinTypeImpl decimal = new XSDBuiltinTypeImpl(anySimpleType,
				DECIMAL, TypeValidator.DECIMAL);
		types.put(DECIMAL, decimal);

		XSDBuiltinTypeImpl integer = new XSDBuiltinTypeImpl(decimal, INTEGER,
				TypeValidator.INTEGER);
		types.put(INTEGER, integer);

		facets.maxInclusive = "0";
		XSDBuiltinTypeImpl nonPositive = new XSDBuiltinTypeImpl(integer,
				NONPOSITIVEINTEGER);
		nonPositive.applyFacetsInternal(facets, XSDFacets.MAX_INCLUSIVE);
		types.put(NONPOSITIVEINTEGER, nonPositive);

		facets.maxInclusive = "-1";
		XSDBuiltinTypeImpl negative = new XSDBuiltinTypeImpl(integer,
				NEGATIVEINTEGER);
		negative.applyFacetsInternal(facets, XSDFacets.MAX_INCLUSIVE);
		types.put(NEGATIVEINTEGER, negative);

		facets.maxInclusive = "9223372036854775807";
		facets.minInclusive = "-9223372036854775808";
		XSDBuiltinTypeImpl longType = new XSDBuiltinTypeImpl(integer, LONG,
				TypeValidator.LONG);
		longType.applyFacetsInternal(facets,
				(short) (XSDFacets.MAX_INCLUSIVE | XSDFacets.MIN_INCLUSIVE));
		types.put(LONG, longType);

		facets.maxInclusive = "2147483647";
		facets.minInclusive = "-2147483648";
		XSDBuiltinTypeImpl intType = new XSDBuiltinTypeImpl(longType, INT,
				TypeValidator.INT);
		intType.applyFacetsInternal(facets,
				(short) (XSDFacets.MAX_INCLUSIVE | XSDFacets.MIN_INCLUSIVE));
		types.put(INT, intType);

		facets.maxInclusive = "32767";
		facets.minInclusive = "-32768";
		XSDBuiltinTypeImpl shortType = new XSDBuiltinTypeImpl(intType, SHORT);
		shortType.applyFacetsInternal(facets,
				(short) (XSDFacets.MAX_INCLUSIVE | XSDFacets.MIN_INCLUSIVE));
		types.put(SHORT, shortType);

		facets.maxInclusive = "127";
		facets.minInclusive = "-128";
		XSDBuiltinTypeImpl byteType = new XSDBuiltinTypeImpl(shortType, BYTE);
		byteType.applyFacetsInternal(facets,
				(short) (XSDFacets.MAX_INCLUSIVE | XSDFacets.MIN_INCLUSIVE));
		types.put(BYTE, byteType);

		facets.minInclusive = "0";
		XSDBuiltinTypeImpl nonNegative = new XSDBuiltinTypeImpl(integer,
				NONNEGATIVEINTEGER);
		nonNegative.applyFacetsInternal(facets, XSDFacets.MIN_INCLUSIVE);
		types.put(NONNEGATIVEINTEGER, nonNegative);

		facets.maxInclusive = "18446744073709551615";
		XSDBuiltinTypeImpl unsignedLong = new XSDBuiltinTypeImpl(nonNegative,
				UNSIGNEDLONG);
		unsignedLong.applyFacetsInternal(facets, XSDFacets.MAX_INCLUSIVE);
		types.put(UNSIGNEDLONG, unsignedLong);

		facets.maxInclusive = "4294967295";
		XSDBuiltinTypeImpl unsignedInt = new XSDBuiltinTypeImpl(unsignedLong,
				UNSIGNEDINT, TypeValidator.LONG);
		unsignedInt.applyFacetsInternal(facets, XSDFacets.MAX_INCLUSIVE);
		types.put(UNSIGNEDINT, unsignedInt);

		facets.maxInclusive = "65535";
		XSDBuiltinTypeImpl unsignedShort = new XSDBuiltinTypeImpl(unsignedInt,
				UNSIGNEDSHORT, TypeValidator.INT);
		unsignedShort.applyFacetsInternal(facets, XSDFacets.MAX_INCLUSIVE);
		types.put(UNSIGNEDSHORT, unsignedShort);

		facets.maxInclusive = "255";
		XSDBuiltinTypeImpl unsignedByte = new XSDBuiltinTypeImpl(unsignedShort,
				UNSIGNEDBYTE);
		unsignedByte.applyFacetsInternal(facets, XSDFacets.MAX_INCLUSIVE);
		types.put(UNSIGNEDBYTE, unsignedByte);

		facets.minInclusive = "1";
		XSDBuiltinTypeImpl positive = new XSDBuiltinTypeImpl(nonNegative,
				POSITIVEINTEGER);
		positive.applyFacetsInternal(facets, XSDFacets.MIN_INCLUSIVE);
		types.put(POSITIVEINTEGER, positive);

		types.put(BOOLEAN, new XSDBuiltinTypeImpl(anySimpleType, BOOLEAN,
				TypeValidator.BOOLEAN));

		XSDBuiltinTypeImpl string = new XSDBuiltinTypeImpl(anySimpleType,
				STRING, TypeValidator.STRING);
		types.put(STRING, string);

		facets.whitespace = XSDFacets.WS_REPLACE;
		XSDBuiltinTypeImpl normalizedString = new XSDBuiltinTypeImpl(string,
				NORMALIZEDSTRING);
		normalizedString.applyFacetsInternal(facets, XSDFacets.WHITESPACE);
		types.put(NORMALIZEDSTRING, normalizedString);

		types.put(ANYURI, new XSDBuiltinTypeImpl(anySimpleType, ANYURI,
				TypeValidator.ANY_URI));

		facets.whitespace = XSDFacets.WS_COLLAPSE;
		XSDBuiltinTypeImpl token = new XSDBuiltinTypeImpl(normalizedString,
				TOKEN);
		token.applyFacetsInternal(facets, XSDFacets.WHITESPACE);
		types.put(TOKEN, token);

		facets.specialPattern = XSDFacets.SPECIAL_PATTERN_NAME;
		XSDBuiltinTypeImpl name = new XSDBuiltinTypeImpl(token, NAME);
		name.applyFacetsInternal(facets, XSDFacets.SPECIAL_PATTERN);
		types.put(NAME, name);

		types.put(QNAME, new XSDBuiltinTypeImpl(anySimpleType, QNAME,
				TypeValidator.QNAME));

		facets.pattern = "([a-zA-Z]{1,8})(-[a-zA-Z0-9]{1,8})*";
		XSDBuiltinTypeImpl language = new XSDBuiltinTypeImpl(token, LANGUAGE);
		language.applyFacetsInternal(facets, XSDFacets.PATTERN);
		types.put(LANGUAGE, language);

		facets.specialPattern = XSDFacets.SPECIAL_PATTERN_NMTOKEN;
		XSDBuiltinTypeImpl nmtoken = new XSDBuiltinTypeImpl(token, NMTOKEN);
		nmtoken.applyFacetsInternal(facets, XSDFacets.SPECIAL_PATTERN);
		types.put(NMTOKEN, nmtoken);

		facets.specialPattern = XSDFacets.SPECIAL_PATTERN_NCNAME;
		XSDBuiltinTypeImpl ncname = new XSDBuiltinTypeImpl(name, NCNAME);
		ncname.applyFacetsInternal(facets, XSDFacets.SPECIAL_PATTERN);
		types.put(NCNAME, ncname);

		// TODO in xerces there is a dedicated validator that checks for
		// unparsed entities
		// see org.apache.xerces.impl.dv.xs.EntityDV
		XSDBuiltinTypeImpl entity = new XSDBuiltinTypeImpl(ncname, ENTITY);
		types.put(ENTITY, entity);

		// TODO in xerces there is a dedicated validator that checks for
		// undeclared IDs
		// org.apache.xerces.impl.dv.xs.IDDV
		types.put(ID, new XSDBuiltinTypeImpl(ncname, ID));

		// TODO in xerces there is a dedicated validator
		// org.apache.xerces.impl.dv.xs.IDREFDV
		XSDBuiltinTypeImpl idref = new XSDBuiltinTypeImpl(ncname, IDREF);
		types.put(IDREF, idref);

		types.put(NOTATION, new XSDBuiltinTypeImpl(anySimpleType, NOTATION,
				TypeValidator.QNAME));

		types.put(HEXBINARY, new XSDBuiltinTypeImpl(anySimpleType, HEXBINARY,
				TypeValidator.HEX_BINARY));

		types.put(BASE64BINARY, new XSDBuiltinTypeImpl(anySimpleType,
				BASE64BINARY, TypeValidator.BASE64_BINARY));

		types.put(DATETIME, new XSDBuiltinTypeImpl(anySimpleType, DATETIME,
				TypeValidator.DATE_TIME));

		types.put(TIME, new XSDBuiltinTypeImpl(anySimpleType, TIME,
				TypeValidator.TIME));

		types.put(DATE, new XSDBuiltinTypeImpl(anySimpleType, DATE,
				TypeValidator.DATE));

		types.put(YEARMONTH, new XSDBuiltinTypeImpl(anySimpleType, YEARMONTH,
				TypeValidator.YEAR_MONTH));

		types.put(YEAR, new XSDBuiltinTypeImpl(anySimpleType, YEAR,
				TypeValidator.YEAR));

		types.put(MONTHDAY, new XSDBuiltinTypeImpl(anySimpleType, MONTHDAY,
				TypeValidator.MONTH_DAY));

		types.put(DAY, new XSDBuiltinTypeImpl(anySimpleType, DAY,
				TypeValidator.DAY));

		types.put(MONTH, new XSDBuiltinTypeImpl(anySimpleType, MONTH,
				TypeValidator.MONTH));

		types.put(DURATION, new XSDBuiltinTypeImpl(anySimpleType, DURATION,
				TypeValidator.DURATION));
	}

	public XSDBuiltinTypesImpl() {

	}

	@Override
	public XSDBuiltinType getTypeByName(String name) {
		return types.get(name);
	}

	@Override
	public Map<String, XSDBuiltinType> getTypes() {
		return types;
	}

}
