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
 * org.apache.xerces.impl.dv.xs.QNameDV
 * 
 * Jena:
 * com.hp.hpl.jena.datatypes.xsd.XSDDatatype
 * com.hp.hpl.jena.datatypes.xsd.impl.XSDBaseStringType
 */

package it.polimi.dei.dbgroup.pedigree.androjena.xsd.impl.validators;

import it.polimi.dei.dbgroup.pedigree.androjena.xsd.XSDBuiltinTypeFormatException;
import it.polimi.dei.dbgroup.pedigree.androjena.xsd.impl.TypeValidator;

import org.apache.xerces.util.XMLChar;


public class QNameValidator extends TypeValidator {
	
	private static final String EMPTY_STRING = "".intern();

	@Override
	public Object getActualValue(String content)
			throws XSDBuiltinTypeFormatException {
        // "prefix:localpart" or "localpart"
        // get prefix and local part out of content
        String prefix, localpart;
        int colonptr = content.indexOf(":");
        if (colonptr > 0) {
        	// replaced with ValidationState implementation
            //prefix = context.getSymbol(content.substring(0,colonptr));
        	prefix = content.substring(0,colonptr).intern();
            localpart = content.substring(colonptr+1);
        } else {
            prefix = EMPTY_STRING;
            localpart = content;
        }

        // both prefix (if any) a nd localpart must be valid NCName
        if (prefix.length() > 0 && !XMLChar.isValidNCName(prefix))
            throw new XSDBuiltinTypeFormatException(content, "invalid QName data: prefix is not a valid NCName");

        if(!XMLChar.isValidNCName(localpart))
        	throw new XSDBuiltinTypeFormatException(content, "invalid QName data: local part is not a valid NCName");
        
        //TODO maybe we should try to create a QName or validate prefix?
        
        return content;
	}

	@Override
	public short getNormalizationType() {
		return NORMALIZE_TRIM;
	}

}
