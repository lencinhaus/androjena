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
 * org.apache.xerces.impl.dv.XSFacets
 */

package it.polimi.dei.dbgroup.pedigree.androjena.xsd.impl;

public final class XSDFacets {
	public static final short NONE = 0;
	public static final short MAX_INCLUSIVE = 1;
	public static final short MIN_INCLUSIVE = 2;
	public static final short WHITESPACE = 4;
	public static final short PATTERN = 8;
	public static final short MIN_LENGTH = 16;
	public static final short SPECIAL_PATTERN = 32;
	
	/** preserve the white spaces */
    public static final short WS_PRESERVE = 0;
    /** replace the white spaces */
    public static final short WS_REPLACE  = 1;
    /** collapse the white spaces */
    public static final short WS_COLLAPSE = 2;
    
    public static final short SPECIAL_PATTERN_NONE     = 0;
    public static final short SPECIAL_PATTERN_NMTOKEN  = 1;
    public static final short SPECIAL_PATTERN_NAME     = 2;
    public static final short SPECIAL_PATTERN_NCNAME   = 3;
	
	public String maxInclusive = null;
	public String minInclusive = null;
	public short whitespace = WS_PRESERVE;
	public String pattern = null;
	public int minLength = -1;
	public short specialPattern = SPECIAL_PATTERN_NONE;
}