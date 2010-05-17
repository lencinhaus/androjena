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
 * org.apache.xerces.impl.dv.xs.DoubleDV
 * 
 * Jena:
 * com.hp.hpl.jena.datatypes.xsd.XSDDatatype
 * com.hp.hpl.jena.datatypes.xsd.impl.XSDDouble
 */

package it.polimi.dei.dbgroup.pedigree.androjena.xsd.impl.validators;

import it.polimi.dei.dbgroup.pedigree.androjena.xsd.XSDBuiltinTypeFormatException;
import it.polimi.dei.dbgroup.pedigree.androjena.xsd.impl.TypeValidator;

public class DoubleValidator extends TypeValidator {

	@Override
	public Object getActualValue(String content) throws XSDBuiltinTypeFormatException {
		if (isPossibleFP(content)) {
            return Double.parseDouble(content);
        }
        else if ( content.equals("INF") ) {
            return Double.POSITIVE_INFINITY;
        }
        else if ( content.equals("-INF") ) {
            return Double.NEGATIVE_INFINITY;
        }
        else if ( content.equals("NaN" ) ) {
            return Double.NaN;
        }
        else {
            throw new XSDBuiltinTypeFormatException(content, "value is not a valid double");
        }
	}

	@Override
	public short getNormalizationType() {
		return NORMALIZE_TRIM;
	}

	/** 
     * Returns true if it's possible that the given
     * string represents a valid floating point value
     * (excluding NaN, INF and -INF).
     */
    static boolean isPossibleFP(String val) {
        final int length = val.length();
        for (int i = 0; i < length; ++i) {
            char c = val.charAt(i);
            if (!(c >= '0' && c <= '9' || c == '.' || 
                c == '-' || c == '+' || c == 'E' || c == 'e')) {
                return false;
            }
        }
        return true;
    }

	@Override
	public int compare(Object o1, Object o2) {
		if(o1 instanceof Double && o2 instanceof Double)
		{
			double d1 = (Double) o1;
			double d2 = (Double) o2;

            // this < other
            if (d1 < d2)
                return LESS_THAN;
            // this > other
            if (d1 > d2)
                return GREATER_THAN;
            // this == other
            // NOTE: we don't distinguish 0.0 from -0.0
            if (d1 == d2)
                return EQUAL;

            // one of the 2 values or both is/are NaN(s)

            if (d1 != d1) {
                // this = NaN = other
                if (d2 != d2)
                    return EQUAL;
                // this is NaN <> other
                return INDETERMINATE;
            }

            // other is NaN <> this
            return INDETERMINATE;
		}
		return super.compare(o1, o2);
	}
    
    
}
