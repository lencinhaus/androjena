/*
  (c) Copyright 2003, 2004, 2005, 2006, 2007, 2008, 2009 Hewlett-Packard Development Company, LP, all rights reserved.
  [See end of file]
  $Id: ExpressionFunctionURIs.java,v 1.1 2009/06/29 08:55:45 castagna Exp $
*/

package com.hp.hpl.jena.graph.query;

/**
	ExpressionFunctionURIs: constants expressing the URIs for functions that
    may be recognised or generated by expression constructors and analysers.

	@author kers
*/
public interface ExpressionFunctionURIs 
    {
    public static final String prefix = "urn:x-jena:expr:";
    
    /**
         Operator used to AND conditions together. The Query.addConstraint()
         method explodes ANDed expressions into their components and keeps
         them separately.
    */
    
    public static final String AND = prefix + "AND";
    
    /**
         Function identfier for "L endsWith string literal R", generated by Rewrite.
    */
    public static final String J_EndsWith = prefix + "J_endsWith";
    
    /**
         Function identfier for "L startsWith string literal R", generated by Rewrite.
    */    
    public static final String J_startsWith = prefix + "J_startsWith";
    
    public static final String J_startsWithInsensitive = prefix + "J_startsWithInsensitive";
    
    public static final String J_endsWithInsensitive = prefix + "J_endsWithInsensitive";
    
    /**
         Function identfier for "L contains string literal R", generated by Rewrite.
    */
    public static final String J_contains = prefix + "J_contains";
    
    public static final String J_containsInsensitive = prefix + "J_containsInsensitive";

    /**
         Function identifier for RDQL-style string-match operation. This is recognised
         by Query and rewritten by Rewrite to the J_* methods. The left operand
         may be any expression, but the right operand must be a PatternLiteral.
    */
    public static final String Q_StringMatch = prefix + "Q_StringMatch"; 
    
    }

/*
    (c) Copyright 2003, 2004, 2005, 2006, 2007, 2008, 2009 Hewlett-Packard Development Company, LP
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions
    are met:

    1. Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.

    2. Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.

    3. The name of the author may not be used to endorse or promote products
       derived from this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
    IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
    OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
    IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
    INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
    NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
    DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
    THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
    THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/