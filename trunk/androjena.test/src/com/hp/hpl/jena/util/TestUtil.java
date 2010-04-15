/*
 * (c) Copyright 2009 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package com.hp.hpl.jena.util;

//ANDROID: removed dependence over JUnit 4

import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.hp.hpl.jena.rdf.model.impl.Util;
public class TestUtil extends TestCase
{
     public static TestSuite suite() {
         return new TestSuite(TestUtil.class) ;
     }
    
    // Intermediate : JUNIt 3 and JUnit 4.
     
    public void testSplitNS_01()
    { split("http://example/xyz", "http://example/", "xyz") ; }
    
    public void testSplitNS_02()
    { split("http://example/ns#xyz", "http://example/ns#", "xyz") ; }
    
    public void testSplitNS_03()
    { no_split("http://example/ns#") ; }
    
    public void testSplitNS_04()
    { no_split("http://example/") ; }
    
    public void testSplitNS_05()  // Illegal URI
    { split("http://example", "http://", "example") ; }
    
    public void testSplitNS_06()   // localname must be at least the NCStartChar - not empty
    { split("mailto:me", "mailto:m", "e") ; }

    public void testSplitNS_07()
    { split("urn:abc:xyz","urn:abc:", "xyz") ; } 

    public void testSplitNS_08()
    { no_split("urn:abc:xyz:") ; } 

    public void testSplitNS_09()
    { split("http://bio2rdf.org/pdb:Pentane-3%2C4-diol-5-phosphate", "http://bio2rdf.org/pdb:Pentane-3%2C4-", "diol-5-phosphate") ; }

    public void testSplitNS_10()
    { split("http://bio2rdf.org/pdb:Pentane-3,4-diol-5-phosphate", "http://bio2rdf.org/pdb:Pentane-3,4-", "diol-5-phosphate") ; }

    // Don't split inside a %encoding. 
    public void testSplitNS_11()
    { split("http://host/abc%AAdef", "http://host/abc%AA", "def") ; } 

    public void testSplitNS_12()
    { split("http://host/abc%1Adef", "http://host/abc%1A", "def") ; } 
    
    public void testSplitNS_13()
    { split("http://host/abc%A1def", "http://host/abc%A1", "def") ; } 
    
    public void testSplitNS_14()
    { split("http://host/abc%AA22def", "http://host/abc%AA22", "def") ; } 

    public void testSplitNS_15()
    { no_split("http://host/abc%AA22") ; } 

    // Other schemes
    
    public void testSplitNS_50()
    { split("file:///x/y", "file:///x/", "y") ; } 

    public void testSplitNS_51()
    { split("file:///x", "file:///", "x") ; } 

    public void testSplitNS_52()
    { split("file:x", "file:", "x") ; } 

    public void testSplitNS_53()
    // Not ideal but some URI schemes dislike a URI with just the scheme
    { split("file:foo", "file:", "foo") ; } 

    public void testSplitNS_54()
    { split("file:c:/foo", "file:c:/", "foo") ; } 
    
    // urn:uuid:d871c7f4-2926-11b2-8073-a5e169788449 - legal type 1 uuid as urn
    // uuid:3cf3e43a-3a5d-40d8-a93c-8697b162a1c0 - legal type 4 uuid as uri
    
    public void testSplitNS_55()
    { split("urn:uuid:d871c7f4-2926-11b2-8073-a5e169788449", "urn:uuid:", "d871c7f4-2926-11b2-8073-a5e169788449") ; }

    public void testSplitNS_56()
    { split("uuid:3cf3e43a-3a5d-40d8-a93c-8697b162a1c0", "uuid:3", "cf3e43a-3a5d-40d8-a93c-8697b162a1c0") ; }
    
    public void testSplitNS_57()
    { split("urn:abc:def", "urn:abc:", "def") ; }

    // --------
    
    static void  no_split(String string)
    { split(string, null, null) ; }

    static void split(String uriStr, String namespace, String localname)
    {
        if ( namespace == null && localname != null )
            fail("Bad test - namespace is null but local name is not") ;
        if ( namespace != null && localname == null )
            fail("Bad test - namespace is not null but local name is") ;
        
        int idx = Util.splitNamespace(uriStr) ;
        if ( idx == uriStr.length() ) 
        {
            // No split.
            if ( namespace != null )
                fail("Expected a split ("+namespace+","+localname+") - but none found") ;
            return ;
            
        }
        // Split
        String ns = uriStr.substring(0,idx) ;
        String ln = uriStr.substring(idx) ;
        assertEquals(namespace, ns) ;
        assertEquals(localname, ln) ;
    }
    
    
}

/*
 * (c) Copyright 2009 Hewlett-Packard Development Company, LP
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
 */