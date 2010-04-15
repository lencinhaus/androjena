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
 * org.apache.xerces.impl.dv.XSSimpleType
 * org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl
 */
package it.polimi.dei.dbgroup.pedigree.androjena.xsd.impl;

import it.polimi.dei.dbgroup.pedigree.androjena.xsd.XSDBuiltinType;
import it.polimi.dei.dbgroup.pedigree.androjena.xsd.XSDBuiltinTypeFormatException;

import java.util.regex.Pattern;

import org.apache.xerces.util.XMLChar;


public class XSDBuiltinTypeImpl implements XSDBuiltinType {
	
	public static final XSDBuiltinTypeImpl ANY_SIMPLE_TYPE = new XSDBuiltinTypeImpl(null, "anySimpleType", TypeValidator.ANY_SIMPLE_TYPE);
	
	private static final String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema";

	private String mName;
	private XSDBuiltinTypeImpl mBase;
	private TypeValidator mValidator;

	private short mDefinedFacets = 0;
	private Object mMaxInclusive = null;
	private Object mMinInclusive = null;
	private short mWhitespace = XSDFacets.WS_PRESERVE;
	private Pattern mPattern = null;
	private String mPatternStr = null;
	private int mMinLength = -1;
	private short mSpecialPattern = XSDFacets.SPECIAL_PATTERN_NONE;
	private static final String[] SPECIAL_PATTERN_STRING   = {
        "NONE", "NMTOKEN", "Name", "NCName"
    };

	public XSDBuiltinTypeImpl(XSDBuiltinTypeImpl base, String name,
			TypeValidator validator) {
		this.mBase = base;
		this.mName = name;
		this.mValidator = validator;

		inheritFacetsFromBase();
	}
	
	public XSDBuiltinTypeImpl(XSDBuiltinTypeImpl base, String name) {
		this(base, name, base.mValidator);
	}
	
	@Override
	public boolean isEqual(Object value1, Object value2) {
        if (value1 == null) {
            return false;
        }
        return value1.equals(value2);
    }

	public void applyFacets(XSDFacets facets, short actualFacets)
			throws XSDBuiltinTypeFormatException {
		if ((actualFacets & XSDFacets.MAX_INCLUSIVE) != 0) {
			mMaxInclusive = mBase.parse(facets.maxInclusive);
		}

		if ((actualFacets & XSDFacets.MIN_INCLUSIVE) != 0) {
			mMinInclusive = mBase.parse(facets.minInclusive);
		}

		if ((actualFacets & XSDFacets.WHITESPACE) != 0) {
			mWhitespace = facets.whitespace;
		}

		if ((actualFacets & XSDFacets.PATTERN) != 0) {
			mPatternStr = facets.pattern;
			mPattern = Pattern.compile(mPatternStr);
		}

		if ((actualFacets & XSDFacets.MIN_LENGTH) != 0) {
			mMinLength = facets.minLength;
		}
		
		if((actualFacets & XSDFacets.SPECIAL_PATTERN) != 0) {
			mSpecialPattern = facets.specialPattern;
		}

		mDefinedFacets |= actualFacets;
	}
	
	void applyFacetsInternal(XSDFacets facets, short actualFacets)
	{
		try
		{
			applyFacets(facets, actualFacets);
		}
		catch(XSDBuiltinTypeFormatException ex)
		{
			throw new RuntimeException("internal error", ex);
		}
	}

	@Override
	public String getName() {
		return mName;
	}

	@Override
	public String getNamespace() {
		return XSD_NAMESPACE;
	}

	@Override
	public XSDBuiltinType getBaseType() {
		return mBase;
	}

	@Override
	public Object parse(String lexicalContent)
			throws XSDBuiltinTypeFormatException {
		String normalized = normalize(lexicalContent);

		if (isFacetDefined(XSDFacets.PATTERN)) {
			if (!mPattern.matcher(normalized).matches())
				throw new XSDBuiltinTypeFormatException(lexicalContent, this,
						"lexical content does not match type pattern: "
								+ mPatternStr);
		}
		
		if(isFacetDefined(XSDFacets.SPECIAL_PATTERN))
		{
			if(mSpecialPattern != XSDFacets.SPECIAL_PATTERN_NONE)
			{
				boolean seenErr = false;
                if (mSpecialPattern == XSDFacets.SPECIAL_PATTERN_NMTOKEN) {
                    // PATTERN "\\c+"
                    seenErr = !XMLChar.isValidNmtoken(normalized);
                }
                else if (mSpecialPattern == XSDFacets.SPECIAL_PATTERN_NAME) {
                    // PATTERN "\\i\\c*"
                    seenErr = !XMLChar.isValidName(normalized);
                }
                else if (mSpecialPattern == XSDFacets.SPECIAL_PATTERN_NCNAME) {
                    // PATTERN "[\\i-[:]][\\c-[:]]*"
                    seenErr = !XMLChar.isValidNCName(normalized);
                }
                if (seenErr) {
                	throw new XSDBuiltinTypeFormatException(normalized, this, "normalized value is not a valid " + SPECIAL_PATTERN_STRING[mSpecialPattern]);
                }
			}
		}
		
		Object actualValue = mValidator.getActualValue(normalized);
		
		checkFacets(normalized, actualValue);
		
		mValidator.checkExtraRules(actualValue);
		
		return actualValue;
	}

	private String normalize(String lexicalContent) {
		if (lexicalContent == null)
			return null;

		if (!isFacetDefined(XSDFacets.PATTERN)) {
			short normType = mValidator.getNormalizationType();
			if (normType == TypeValidator.NORMALIZE_NONE) {
				return lexicalContent;
			} else if (normType == TypeValidator.NORMALIZE_TRIM) {
				return XMLChar.trim(lexicalContent);
			}
		}

		int len = lexicalContent == null ? 0 : lexicalContent.length();
		if (len == 0 || mWhitespace == XSDFacets.WS_PRESERVE)
			return lexicalContent;

		StringBuffer sb = new StringBuffer();
		if (mWhitespace == XSDFacets.WS_REPLACE) {
			char ch;
			// when it's replace, just replace #x9, #xa, #xd by #x20
			for (int i = 0; i < len; i++) {
				ch = lexicalContent.charAt(i);
				if (ch != 0x9 && ch != 0xa && ch != 0xd)
					sb.append(ch);
				else
					sb.append((char) 0x20);
			}
		} else {
			char ch;
			int i;
			boolean isLeading = true;
			// when it's collapse
			for (i = 0; i < len; i++) {
				ch = lexicalContent.charAt(i);
				// append real characters, so we passed leading ws
				if (ch != 0x9 && ch != 0xa && ch != 0xd && ch != 0x20) {
					sb.append(ch);
					isLeading = false;
				} else {
					// for whitespaces, we skip all following ws
					for (; i < len - 1; i++) {
						ch = lexicalContent.charAt(i + 1);
						if (ch != 0x9 && ch != 0xa && ch != 0xd && ch != 0x20)
							break;
					}
					// if it's not a leading or tailing ws, then append a space
					if (i < len - 1 && !isLeading)
						sb.append((char) 0x20);
				}
			}
		}

		return sb.toString();
	}
	
	private void checkFacets(String normalized, Object actualValue) throws XSDBuiltinTypeFormatException
	{
		if(mDefinedFacets == 0 || mDefinedFacets == XSDFacets.WHITESPACE) return;
		
		if(mValidator != TypeValidator.QNAME && isFacetDefined(XSDFacets.MIN_LENGTH))
		{
			int length = mValidator.getDataLength(actualValue);
			if(length < mMinLength) throw new XSDBuiltinTypeFormatException(normalized, this, "data length is less than specified minimum length (" + length + " < " + mMinLength + ")");
		}
		
		int compare;
		
		if(isFacetDefined(XSDFacets.MAX_INCLUSIVE))
		{
			compare = mValidator.compare(actualValue, mMaxInclusive);
			if(compare != TypeValidator.LESS_THAN && compare != TypeValidator.EQUAL) throw new XSDBuiltinTypeFormatException(normalized, this, "data is greater than specified maximum (inclusive)");
		}
		
		if(isFacetDefined(XSDFacets.MIN_INCLUSIVE))
		{
			compare = mValidator.compare(actualValue, mMinInclusive);
			if(compare != TypeValidator.GREATER_THAN && compare != TypeValidator.EQUAL) throw new XSDBuiltinTypeFormatException(normalized, this, "data is less than specified minimum (inclusive)");
		}
	}

	private boolean isFacetDefined(short facet) {
		return (mDefinedFacets & facet) != 0;
	}

	private void inheritFacetsFromBase() {
		if (mBase != null) {
			mDefinedFacets = mBase.mDefinedFacets;
			mMaxInclusive = mBase.mMaxInclusive;
			mMinInclusive = mBase.mMinInclusive;
			mPattern = mBase.mPattern;
			mPatternStr = mBase.mPatternStr;
			mWhitespace = mBase.mWhitespace;
			mMinLength = mBase.mMinLength;
			mSpecialPattern = mBase.mSpecialPattern;
		}
	}
}
