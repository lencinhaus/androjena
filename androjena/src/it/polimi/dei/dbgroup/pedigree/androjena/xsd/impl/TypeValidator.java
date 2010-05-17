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
 * org.apache.xerces.impl.dv.xs.TypeValidator
 * 
 * Jena:
 * com.hp.hpl.jena.datatypes.xsd.XSDDatatype
 */

package it.polimi.dei.dbgroup.pedigree.androjena.xsd.impl;

import it.polimi.dei.dbgroup.pedigree.androjena.xsd.XSDBuiltinTypeFormatException;
import it.polimi.dei.dbgroup.pedigree.androjena.xsd.impl.validators.AnySimpleTypeValidator;
import it.polimi.dei.dbgroup.pedigree.androjena.xsd.impl.validators.AnyURIValidator;
import it.polimi.dei.dbgroup.pedigree.androjena.xsd.impl.validators.Base64BinaryValidator;
import it.polimi.dei.dbgroup.pedigree.androjena.xsd.impl.validators.BooleanValidator;
import it.polimi.dei.dbgroup.pedigree.androjena.xsd.impl.validators.DateTimeValidator;
import it.polimi.dei.dbgroup.pedigree.androjena.xsd.impl.validators.DateValidator;
import it.polimi.dei.dbgroup.pedigree.androjena.xsd.impl.validators.DayValidator;
import it.polimi.dei.dbgroup.pedigree.androjena.xsd.impl.validators.DecimalValidator;
import it.polimi.dei.dbgroup.pedigree.androjena.xsd.impl.validators.DoubleValidator;
import it.polimi.dei.dbgroup.pedigree.androjena.xsd.impl.validators.DurationValidator;
import it.polimi.dei.dbgroup.pedigree.androjena.xsd.impl.validators.FloatValidator;
import it.polimi.dei.dbgroup.pedigree.androjena.xsd.impl.validators.HexBinaryValidator;
import it.polimi.dei.dbgroup.pedigree.androjena.xsd.impl.validators.IntValidator;
import it.polimi.dei.dbgroup.pedigree.androjena.xsd.impl.validators.IntegerValidator;
import it.polimi.dei.dbgroup.pedigree.androjena.xsd.impl.validators.LongValidator;
import it.polimi.dei.dbgroup.pedigree.androjena.xsd.impl.validators.MonthDayValidator;
import it.polimi.dei.dbgroup.pedigree.androjena.xsd.impl.validators.MonthValidator;
import it.polimi.dei.dbgroup.pedigree.androjena.xsd.impl.validators.QNameValidator;
import it.polimi.dei.dbgroup.pedigree.androjena.xsd.impl.validators.StringValidator;
import it.polimi.dei.dbgroup.pedigree.androjena.xsd.impl.validators.TimeValidator;
import it.polimi.dei.dbgroup.pedigree.androjena.xsd.impl.validators.YearMonthValidator;
import it.polimi.dei.dbgroup.pedigree.androjena.xsd.impl.validators.YearValidator;

public abstract class TypeValidator {
	//order constants
    public static final short LESS_THAN     = -1;
    public static final short EQUAL         = 0;
    public static final short GREATER_THAN  = 1;
    public static final short INDETERMINATE = 2;
    
	public static final short NORMALIZE_NONE = 0;
	public static final short NORMALIZE_TRIM = 1;
	public static final short NORMALIZE_FULL = 2;
	
	public static final TypeValidator ANY_SIMPLE_TYPE = new AnySimpleTypeValidator();
	public static final TypeValidator FLOAT = new FloatValidator();
	public static final TypeValidator DOUBLE = new DoubleValidator();
	public static final TypeValidator DECIMAL = new DecimalValidator();
	public static final TypeValidator INTEGER = new IntegerValidator();
	public static final TypeValidator LONG = new LongValidator();
	public static final TypeValidator INT = new IntValidator();
	public static final TypeValidator BOOLEAN = new BooleanValidator();
	public static final TypeValidator STRING = new StringValidator();
	public static final TypeValidator ANY_URI = new AnyURIValidator();
	public static final TypeValidator QNAME = new QNameValidator();
	public static final TypeValidator HEX_BINARY = new HexBinaryValidator();
	public static final TypeValidator BASE64_BINARY = new Base64BinaryValidator();
	public static final TypeValidator DATE_TIME = new DateTimeValidator();
	public static final TypeValidator DATE = new DateValidator();
	public static final TypeValidator TIME = new TimeValidator();
	public static final TypeValidator YEAR = new YearValidator();
	public static final TypeValidator YEAR_MONTH = new YearMonthValidator();
	public static final TypeValidator MONTH = new MonthValidator();
	public static final TypeValidator MONTH_DAY = new MonthDayValidator();
	public static final TypeValidator DAY = new DayValidator();
	public static final TypeValidator DURATION = new DurationValidator();
	
	public abstract Object getActualValue(String content) throws XSDBuiltinTypeFormatException;
	
	public abstract short getNormalizationType();

	// for ID/IDREF/ENTITY types, do some extra checking after the value is
    // checked to be valid with respect to both lexical representation and
    // facets
	public void checkExtraRules(Object value) throws XSDBuiltinTypeFormatException
	{
		// do nothing by default
	}
	
	// check the order relation between the two values
    // the parameters are in compiled form (from getActualValue)
	public int compare(Object o1, Object o2)
	{
		return LESS_THAN;
	}
	
	// get the length of the value
    // the parameters are in compiled form (from getActualValue)
    public int getDataLength(Object value) {
        return (value instanceof String) ? ((String)value).length() : -1;
    }

    // check whether the character is in the range 0x30 ~ 0x39
    public static final boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }
    
    // if the character is in the range 0x30 ~ 0x39, return its int value (0~9),
    // otherwise, return -1
    public static final int getDigit(char ch) {
        return isDigit(ch) ? ch - '0' : -1;
    }
}
