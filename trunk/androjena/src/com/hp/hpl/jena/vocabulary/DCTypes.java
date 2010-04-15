/*
 * (c) Copyright 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

/* CVS $Id: DCTypes.java,v 1.1 2009/06/29 08:55:36 castagna Exp $ */
package com.hp.hpl.jena.vocabulary;
 
import com.hp.hpl.jena.rdf.model.*;
 
/**
 * Vocabulary definitions from vocabularies/dublin-core_types.xml 
 * @author Auto-generated by schemagen on 13 May 2003 08:53 
 */
public class DCTypes {
    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static Model m_model = ModelFactory.createDefaultModel();
    
    /** <p>The namespace of the vocabalary as a string ({@value})</p> */
    public static final String NS = "http://purl.org/dc/dcmitype/";
    
    /** <p>The namespace of the vocabalary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabalary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
    
    /** <p>A collection is an aggregation of items. The term collection means that the 
     *  resource is described as a group; its parts may be separately described and 
     *  navigated.</p>
     */
    public static final Resource Collection = m_model.createResource( "http://purl.org/dc/dcmitype/Collection" );
    
    /** <p>A dataset is information encoded in a defined structure (for example, lists, 
     *  tables, and databases), intended to be useful for direct machine processing.</p>
     */
    public static final Resource Dataset = m_model.createResource( "http://purl.org/dc/dcmitype/Dataset" );
    
    /** <p>An event is a non-persistent, time-based occurrence. Metadata for an event 
     *  provides descriptive information that is the basis for discovery of the purpose, 
     *  location, duration, responsible agents, and links to related events and resources. 
     *  The resource of type event may not be retrievable if the described instantiation 
     *  has expired or is yet to occur. Examples - exhibition, web-cast, conference, 
     *  workshop, open-day, performance, battle, trial, wedding, tea-party, conflagration.</p>
     */
    public static final Resource Event = m_model.createResource( "http://purl.org/dc/dcmitype/Event" );
    
    /** <p>An image is a primarily symbolic visual representation other than text. For 
     *  example - images and photographs of physical objects, paintings, prints, drawings, 
     *  other images and graphics, animations and moving pictures, film, diagrams, 
     *  maps, musical notation. Note that image may include both electronic and physical 
     *  representations.</p>
     */
    public static final Resource Image = m_model.createResource( "http://purl.org/dc/dcmitype/Image" );
    
    /** <p>An interactive resource is a resource which requires interaction from the 
     *  user to be understood, executed, or experienced. For example - forms on web 
     *  pages, applets, multimedia learning objects, chat services, virtual reality.</p>
     */
    public static final Resource InteractiveResource = m_model.createResource( "http://purl.org/dc/dcmitype/InteractiveResource" );
    
    /** <p>A service is a system that provides one or more functions of value to the 
     *  end-user. Examples include: a photocopying service, a banking service, an 
     *  authentication service, interlibrary loans, a Z39.50 or Web server.</p>
     */
    public static final Resource Service = m_model.createResource( "http://purl.org/dc/dcmitype/Service" );
    
    /** <p>Software is a computer program in source or compiled form which may be available 
     *  for installation non-transiently on another machine. For software which exists 
     *  only to create an interactive environment, use interactive instead.</p>
     */
    public static final Resource Software = m_model.createResource( "http://purl.org/dc/dcmitype/Software" );
    
    /** <p>A sound is a resource whose content is primarily intended to be rendered as 
     *  audio. For example - a music playback file format, an audio compact disc, 
     *  and recorded speech or sounds.</p>
     */
    public static final Resource Sound = m_model.createResource( "http://purl.org/dc/dcmitype/Sound" );
    
    /** <p>A text is a resource whose content is primarily words for reading. For example 
     *  - books, letters, dissertations, poems, newspapers, articles, archives of 
     *  mailing lists. Note that facsimiles or images of texts are still of the genre 
     *  text.</p>
     */
    public static final Resource Text = m_model.createResource( "http://purl.org/dc/dcmitype/Text" );
    
    /** <p>An inanimate, three-dimensional object or substance. For example -- a computer, 
     *  the great pyramid, a sculpture. Note that digital representations of, or surrogates 
     *  for, these things should use Image, Text or one of the other types.</p>
     */
    public static final Resource PhysicalObject = m_model.createResource( "http://purl.org/dc/dcmitype/PhysicalObject" );
    
}

/*
 *  (c) Copyright 2003, 2004, 2005, 2006, 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 *  All rights reserved.
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
