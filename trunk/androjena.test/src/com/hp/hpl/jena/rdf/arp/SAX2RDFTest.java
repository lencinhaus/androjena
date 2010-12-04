/*
 * (c) Copyright 2004, 2005, 2006, 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

package com.hp.hpl.jena.rdf.arp;

import it.polimi.dei.dbgroup.pedigree.androjena.test.TestHelper;
import it.polimi.dei.dbgroup.pedigree.androjena.test.TestHelper.StreamFactory;
import it.polimi.dei.dbgroup.pedigree.androjena.xml.ExpatReaderWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Arrays;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFErrorHandler;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.regression.testReaderInterface;

/**
 * @author Jeremy J. Carroll
 *  
 */
public class SAX2RDFTest extends TestCase {

	protected static Logger logger = LoggerFactory.getLogger( testReaderInterface.class );
    
	static final boolean is1_4_1 =
		System.getProperty("java.version").startsWith("1.4.1");
	static final private String all[] = {
    "arp/dom/domtest.rdf",
	//"abbreviated/collection.rdf", 
	"abbreviated/container.rdf",
			"abbreviated/cookup.rdf", "abbreviated/daml.rdf",
			"abbreviated/namespaces.rdf", "abbreviated/reification.rdf",
			"abbreviated/relative-uris.rdf", "arp/comments/test01.rdf",
			"arp/comments/test02.rdf", "arp/comments/test03.rdf",
			"arp/comments/test04.rdf", "arp/comments/test05.rdf",
			"arp/comments/test06.rdf", "arp/comments/test07.rdf",
			"arp/comments/test08.rdf", "arp/comments/test09.rdf",
			"arp/comments/test10.rdf", "arp/comments/test11.rdf",
			"arp/comments/test12.rdf", "arp/comments/test13.rdf",
			"arp/error-msgs/test01.rdf", "arp/error-msgs/test02.rdf",
			"arp/error-msgs/test03.rdf", "arp/error-msgs/test04.rdf",
			"arp/error-msgs/test05.rdf",
			//		"arp/error-msgs/testutf8.rdf",
			"arp/i18n/eq-bug73_0.rdf", "arp/i18n/eq-bug73_1.rdf",
			"arp/i18n/eq-bug73_2.rdf", "arp/i18n/i18nID.rdf",
			"arp/i18n/t9000.rdf", "arp/Manifest.rdf",
			"arp/parsetype/bug68_0.rdf", "arp/qname-in-ID/bug74_0.rdf",
			"arp/rdf-nnn/bad-bug67_0.rdf", "arp/rdf-nnn/bad-bug67_1.rdf",
			"arp/rdf-nnn/bad-bug67_2.rdf", "arp/rdf-nnn/bad-bug67_3.rdf",
			"arp/rdf-nnn/bad-bug67_4.rdf", "arp/rdf-nnn/bad-bug67_5.rdf",
			"arp/rdf-nnn/bad-bug67_6.rdf", "arp/rdf-nnn/bad-bug67_7.rdf",
			"arp/rdf-nnn/bad-bug67_8.rdf", "arp/rdf-nnn/bad-bug67_9.rdf",
			"arp/relative-namespaces/bad-bug50_0.rdf",
			"arp/rfc2396-issue/bug51_0.rdf", "arp/rfc2396-issue/fileURI.rdf",
			//	"arp/scope/test01.rdf",
			"arp/scope/test02.rdf", "arp/scope/test03.rdf",
			"arp/scope/test04.rdf", "arp/scope/test05.rdf",
			"arp/syntax-errors/error001.rdf", "arp/syntax-errors/error002.rdf",
			"arp/syntax-errors/error003.rdf", "arp/xml-literals/reported1.rdf",
			"arp/xml-literals/reported2.rdf", "arp/xml-literals/reported3.rdf",
			"arp/xmlns/bad01.rdf", "arp/xmlns/food.rdf",
			"arp/xmlns/test01.rdf", "arp/xmlns/test02.rdf",
			"arp/xmlns/test03.rdf", "arp/xmlns/wine.rdf",
			"ontology/daml/Axioms/test.rdf",
			"ontology/daml/ClassExpression/test-boolean.rdf",
			"ontology/daml/ClassExpression/test-enum.rdf",
			"ontology/daml/ClassExpression/test-restriction.rdf",
			"ontology/daml/ClassExpression/test.rdf",
			"ontology/daml/list-syntax/test-proptypes.rdf",
			"ontology/daml/list-syntax/test.rdf",
			"ontology/daml/Ontology/test.rdf",
			"ontology/daml/Property/test.rdf", "ontology/list0.rdf",
			"ontology/list1.rdf", "ontology/list2.rdf", "ontology/list3.rdf",
			"ontology/list4.rdf", "ontology/list5.rdf",
			"ontology/owl/Axioms/test.rdf",
			"ontology/owl/ClassExpression/test-boolean.rdf",
			"ontology/owl/ClassExpression/test-enum.rdf",
			"ontology/owl/ClassExpression/test-restriction.rdf",
			"ontology/owl/ClassExpression/test.rdf",
			"ontology/owl/list-syntax/test-proptypes.rdf",
			"ontology/owl/list-syntax/test-with-import.rdf",
			"ontology/owl/list-syntax/test.rdf",
			"ontology/owl/Ontology/test.rdf", "ontology/owl/Property/test.rdf",
			"ontology/rdfs/ClassExpression/test.rdf",
			"ontology/rdfs/list-syntax/test.rdf",
			"ontology/rdfs/Ontology/test.rdf",
			"ontology/rdfs/Property/test.rdf", "ontology/relativenames.rdf",
			"ontology/testImport5/ont-policy.rdf", 
			
			"RDQL/model5.rdf", "RDQL/vc-db-1.rdf", "RDQL/vc-db-2.rdf",
			"RDQL/vc-db-3.rdf", "reasoners/bugs/sbug.rdf",
			"reasoners/owl/consistentData.rdf",
			"reasoners/owl/inconsistent1.rdf",
			"reasoners/owl/inconsistent2.rdf",
			"reasoners/owl/inconsistent3.rdf",
			"reasoners/owl/inconsistent4.rdf",
			"reasoners/owl/inconsistent5.rdf", "reasoners/rdfs/data1.rdf",
			"reasoners/rdfs/data2.rdf", "reasoners/rdfs/data3.rdf",
			"reasoners/rdfs/dataRDFS12.rdf",
			"reasoners/rdfs/manifest-nodirect-noresource.rdf",
			"reasoners/rdfs/manifest-nodirect.rdf",
			"reasoners/rdfs/manifest-rdfs12.rdf",
			"reasoners/rdfs/manifest-simple.rdf",
			"reasoners/rdfs/manifest-standard.rdf",
			"reasoners/rdfs/manifest.rdf", "reasoners/rdfs/result1.rdf",
			"reasoners/rdfs/result10.rdf",
			"reasoners/rdfs/result11-noresource.rdf",
			"reasoners/rdfs/result11.rdf",
			"reasoners/rdfs/result12-noresource.rdf",
			"reasoners/rdfs/result12.rdf",
			"reasoners/rdfs/result13-noresource.rdf",
			"reasoners/rdfs/result13.rdf", "reasoners/rdfs/result14.rdf",
			"reasoners/rdfs/result15.rdf",
			"reasoners/rdfs/result16-noresource.rdf",
			"reasoners/rdfs/result16.rdf", "reasoners/rdfs/result17.rdf",
			"reasoners/rdfs/result18-simple.rdf",
			"reasoners/rdfs/result18.rdf",
			"reasoners/rdfs/result19-nodirect.rdf",
			"reasoners/rdfs/result19.rdf", "reasoners/rdfs/result2.rdf",
			"reasoners/rdfs/result20-nodirect.rdf",
			"reasoners/rdfs/result20.rdf", "reasoners/rdfs/result3.rdf",
			"reasoners/rdfs/result4.rdf", "reasoners/rdfs/result7.rdf",
			"reasoners/rdfs/result8.rdf", "reasoners/rdfs/result9.rdf",
			"reasoners/rdfs/resultRDFS12.rdf", "reasoners/rdfs/tbox1.rdf",
			"reasoners/rdfs/timing-data.rdf", "reasoners/rdfs/timing-tbox.rdf",
			"reasoners/transitive/data1.rdf",
			"reasoners/transitive/data11.rdf",
			"reasoners/transitive/data2.rdf", "reasoners/transitive/data3.rdf",
			"reasoners/transitive/data6.rdf", "reasoners/transitive/data8.rdf",
			"reasoners/transitive/data9.rdf", "reasoners/transitive/empty.rdf",
			"reasoners/transitive/manifest.rdf",
			"reasoners/transitive/result11.rdf",
			"reasoners/transitive/result2.rdf",
			"reasoners/transitive/result4.rdf",
			"reasoners/transitive/result5.rdf",
			"reasoners/transitive/result6.rdf",
			"reasoners/transitive/tbox1.rdf", "reasoners/transitive/tbox7.rdf",
			"wg/AllDifferent/conclusions001.rdf",
			"wg/AllDifferent/Manifest001.rdf",
			"wg/AllDifferent/premises001.rdf",
			"wg/allValuesFrom/conclusions001.rdf",
			"wg/allValuesFrom/Manifest001.rdf",
			"wg/allValuesFrom/Manifest002.rdf",
			"wg/allValuesFrom/nonconclusions002.rdf",
			"wg/allValuesFrom/premises001.rdf",
			"wg/allValuesFrom/premises002.rdf", "wg/amp-in-url/test001.rdf",
			"wg/AnnotationProperty/conclusions002.rdf",
			"wg/AnnotationProperty/consistent003.rdf",
			"wg/AnnotationProperty/consistent004.rdf",
			"wg/AnnotationProperty/Manifest001.rdf",
			"wg/AnnotationProperty/Manifest002.rdf",
			"wg/AnnotationProperty/Manifest003.rdf",
			"wg/AnnotationProperty/Manifest004.rdf",
			"wg/AnnotationProperty/nonconclusions001.rdf",
			"wg/AnnotationProperty/premises001.rdf",
			"wg/AnnotationProperty/premises002.rdf",
			"wg/backwardCompatibleWith/consistent001.rdf",
			"wg/backwardCompatibleWith/consistent002.rdf",
			"wg/backwardCompatibleWith/Manifest001.rdf",
			"wg/backwardCompatibleWith/Manifest002.rdf",
			"wg/cardinality/conclusions001-mod.rdf",
			"wg/cardinality/conclusions001.rdf",
			"wg/cardinality/conclusions002-mod.rdf",
			"wg/cardinality/conclusions002.rdf",
			"wg/cardinality/conclusions003-mod.rdf",
			"wg/cardinality/conclusions003.rdf",
			"wg/cardinality/conclusions004-mod.rdf",
			"wg/cardinality/conclusions004.rdf",
			"wg/cardinality/conclusions005-mod.rdf",
			"wg/cardinality/conclusions005.rdf",
			"wg/cardinality/conclusions006-mod.rdf",
			"wg/cardinality/conclusions006.rdf",
			"wg/cardinality/Manifest001-mod.rdf",
			"wg/cardinality/Manifest001.rdf",
			"wg/cardinality/Manifest002-mod.rdf",
			"wg/cardinality/Manifest002.rdf",
			"wg/cardinality/Manifest003-mod.rdf",
			"wg/cardinality/Manifest003.rdf",
			"wg/cardinality/Manifest004-mod.rdf",
			"wg/cardinality/Manifest004.rdf",
			"wg/cardinality/Manifest005-mod.rdf",
			"wg/cardinality/Manifest005.rdf",
			"wg/cardinality/Manifest006-mod.rdf",
			"wg/cardinality/Manifest006.rdf",
			"wg/cardinality/premises001-mod.rdf",
			"wg/cardinality/premises001.rdf",
			"wg/cardinality/premises002-mod.rdf",
			"wg/cardinality/premises002.rdf",
			"wg/cardinality/premises003-mod.rdf",
			"wg/cardinality/premises003.rdf",
			"wg/cardinality/premises004-mod.rdf",
			"wg/cardinality/premises004.rdf",
			"wg/cardinality/premises005-mod.rdf",
			"wg/cardinality/premises005.rdf",
			"wg/cardinality/premises006-mod.rdf",
			"wg/cardinality/premises006.rdf", "wg/Class/conclusions001.rdf",
			"wg/Class/conclusions002.rdf", "wg/Class/conclusions003.rdf",
			"wg/Class/conclusions006.rdf", "wg/Class/Manifest001.rdf",
			"wg/Class/Manifest002.rdf", "wg/Class/Manifest003.rdf",
			"wg/Class/Manifest004.rdf", "wg/Class/Manifest005.rdf",
			"wg/Class/Manifest006.rdf", "wg/Class/nonconclusions004.rdf",
			"wg/Class/nonconclusions005.rdf", "wg/Class/premises002.rdf",
			"wg/Class/premises003.rdf", "wg/Class/premises004.rdf",
			"wg/Class/premises005.rdf", "wg/Class/premises006.rdf",
			"wg/complementOf/conclusions001.rdf",
			"wg/complementOf/Manifest001.rdf",
			"wg/complementOf/premises001.rdf",
			"wg/DatatypeProperty/consistent001.rdf",
			"wg/DatatypeProperty/Manifest001.rdf", "wg/datatypes/test001.rdf",
			"wg/datatypes/test002.rdf",
			"wg/description-logic/conclusions001.rdf",
			"wg/description-logic/conclusions002.rdf",
			"wg/description-logic/conclusions003.rdf",
			"wg/description-logic/conclusions004.rdf",
			"wg/description-logic/conclusions007.rdf",
			"wg/description-logic/conclusions008.rdf",
			"wg/description-logic/conclusions010.rdf",
			"wg/description-logic/conclusions011.rdf",
			"wg/description-logic/conclusions012.rdf",
			"wg/description-logic/conclusions013.rdf",
			"wg/description-logic/conclusions014.rdf",
			"wg/description-logic/conclusions015.rdf",
			"wg/description-logic/conclusions017.rdf",
			"wg/description-logic/conclusions019.rdf",
			"wg/description-logic/conclusions022.rdf",
			"wg/description-logic/conclusions023.rdf",
			"wg/description-logic/conclusions026.rdf",
			"wg/description-logic/conclusions027.rdf",
			"wg/description-logic/conclusions029.rdf",
			"wg/description-logic/conclusions030.rdf",
			"wg/description-logic/conclusions032.rdf",
			"wg/description-logic/conclusions033.rdf",
			"wg/description-logic/conclusions101.rdf",
			"wg/description-logic/conclusions102.rdf",
			"wg/description-logic/conclusions103.rdf",
			"wg/description-logic/conclusions104.rdf",
			"wg/description-logic/conclusions105.rdf",
			"wg/description-logic/conclusions106.rdf",
			"wg/description-logic/conclusions107.rdf",
			"wg/description-logic/conclusions108.rdf",
			"wg/description-logic/conclusions109.rdf",
			"wg/description-logic/conclusions110.rdf",
			"wg/description-logic/conclusions111.rdf",
			"wg/description-logic/conclusions201.rdf",
			"wg/description-logic/conclusions202.rdf",
			"wg/description-logic/conclusions203.rdf",
			"wg/description-logic/conclusions204.rdf",
			"wg/description-logic/conclusions205.rdf",
			"wg/description-logic/conclusions206.rdf",
			"wg/description-logic/conclusions207.rdf",
			"wg/description-logic/conclusions208.rdf",
			"wg/description-logic/conclusions661.rdf",
			"wg/description-logic/conclusions662.rdf",
			"wg/description-logic/conclusions663.rdf",
			"wg/description-logic/conclusions664.rdf",
			"wg/description-logic/conclusions665.rdf",
			"wg/description-logic/conclusions667.rdf",
			"wg/description-logic/conclusions901.rdf",
			"wg/description-logic/conclusions903.rdf",
			"wg/description-logic/conclusions905.rdf",
			"wg/description-logic/consistent005.rdf",
			"wg/description-logic/consistent006.rdf",
			"wg/description-logic/consistent009.rdf",
			"wg/description-logic/consistent016.rdf",
			"wg/description-logic/consistent018.rdf",
			"wg/description-logic/consistent020.rdf",
			"wg/description-logic/consistent021.rdf",
			"wg/description-logic/consistent024.rdf",
			"wg/description-logic/consistent025.rdf",
			"wg/description-logic/consistent028.rdf",
			"wg/description-logic/consistent031.rdf",
			"wg/description-logic/consistent034.rdf",
			"wg/description-logic/consistent501.rdf",
			"wg/description-logic/consistent503.rdf",
			"wg/description-logic/consistent605.rdf",
			"wg/description-logic/consistent606.rdf",
			"wg/description-logic/consistent609.rdf",
			"wg/description-logic/consistent616.rdf",
			"wg/description-logic/consistent624.rdf",
			"wg/description-logic/consistent625.rdf",
			"wg/description-logic/consistent628.rdf",
			"wg/description-logic/consistent631.rdf",
			"wg/description-logic/consistent634.rdf",
			"wg/description-logic/consistent905.rdf",
			"wg/description-logic/consistent906.rdf",
			"wg/description-logic/consistent907.rdf",
			"wg/description-logic/consistent908.rdf",
			"wg/description-logic/inconsistent001.rdf",
			"wg/description-logic/inconsistent002.rdf",
			"wg/description-logic/inconsistent003.rdf",
			"wg/description-logic/inconsistent004.rdf",
			"wg/description-logic/inconsistent007.rdf",
			"wg/description-logic/inconsistent008.rdf",
			"wg/description-logic/inconsistent010.rdf",
			"wg/description-logic/inconsistent011.rdf",
			"wg/description-logic/inconsistent012.rdf",
			"wg/description-logic/inconsistent013.rdf",
			"wg/description-logic/inconsistent014.rdf",
			"wg/description-logic/inconsistent015.rdf",
			"wg/description-logic/inconsistent017.rdf",
			"wg/description-logic/inconsistent019.rdf",
			"wg/description-logic/inconsistent022.rdf",
			"wg/description-logic/inconsistent023.rdf",
			"wg/description-logic/inconsistent026.rdf",
			"wg/description-logic/inconsistent027.rdf",
			"wg/description-logic/inconsistent029.rdf",
			"wg/description-logic/inconsistent030.rdf",
			"wg/description-logic/inconsistent032.rdf",
			"wg/description-logic/inconsistent033.rdf",
			"wg/description-logic/inconsistent035.rdf",
			"wg/description-logic/inconsistent040.rdf",
			"wg/description-logic/inconsistent101.rdf",
			"wg/description-logic/inconsistent102.rdf",
			"wg/description-logic/inconsistent103.rdf",
			"wg/description-logic/inconsistent104.rdf",
			"wg/description-logic/inconsistent105.rdf",
			"wg/description-logic/inconsistent106.rdf",
			"wg/description-logic/inconsistent107.rdf",
			"wg/description-logic/inconsistent108.rdf",
			"wg/description-logic/inconsistent109.rdf",
			"wg/description-logic/inconsistent110.rdf",
			"wg/description-logic/inconsistent111.rdf",
			"wg/description-logic/inconsistent502.rdf",
			"wg/description-logic/inconsistent504.rdf",
			"wg/description-logic/inconsistent601.rdf",
			"wg/description-logic/inconsistent602.rdf",
			"wg/description-logic/inconsistent603.rdf",
			"wg/description-logic/inconsistent604.rdf",
			"wg/description-logic/inconsistent608.rdf",
			"wg/description-logic/inconsistent610.rdf",
			"wg/description-logic/inconsistent611.rdf",
			"wg/description-logic/inconsistent612.rdf",
			"wg/description-logic/inconsistent613.rdf",
			"wg/description-logic/inconsistent614.rdf",
			"wg/description-logic/inconsistent615.rdf",
			"wg/description-logic/inconsistent617.rdf",
			"wg/description-logic/inconsistent623.rdf",
			"wg/description-logic/inconsistent626.rdf",
			"wg/description-logic/inconsistent627.rdf",
			"wg/description-logic/inconsistent629.rdf",
			"wg/description-logic/inconsistent630.rdf",
			"wg/description-logic/inconsistent632.rdf",
			"wg/description-logic/inconsistent633.rdf",
			"wg/description-logic/inconsistent641.rdf",
			"wg/description-logic/inconsistent642.rdf",
			"wg/description-logic/inconsistent643.rdf",
			"wg/description-logic/inconsistent644.rdf",
			"wg/description-logic/inconsistent646.rdf",
			"wg/description-logic/inconsistent650.rdf",
			"wg/description-logic/inconsistent909.rdf",
			"wg/description-logic/inconsistent910.rdf",
			"wg/description-logic/Manifest001.rdf",
			"wg/description-logic/Manifest002.rdf",
			"wg/description-logic/Manifest003.rdf",
			"wg/description-logic/Manifest004.rdf",
			"wg/description-logic/Manifest005.rdf",
			"wg/description-logic/Manifest006.rdf",
			"wg/description-logic/Manifest007.rdf",
			"wg/description-logic/Manifest008.rdf",
			"wg/description-logic/Manifest009.rdf",
			"wg/description-logic/Manifest010.rdf",
			"wg/description-logic/Manifest011.rdf",
			"wg/description-logic/Manifest012.rdf",
			"wg/description-logic/Manifest013.rdf",
			"wg/description-logic/Manifest014.rdf",
			"wg/description-logic/Manifest015.rdf",
			"wg/description-logic/Manifest016.rdf",
			"wg/description-logic/Manifest017.rdf",
			"wg/description-logic/Manifest018.rdf",
			"wg/description-logic/Manifest019.rdf",
			"wg/description-logic/Manifest020.rdf",
			"wg/description-logic/Manifest021.rdf",
			"wg/description-logic/Manifest022.rdf",
			"wg/description-logic/Manifest023.rdf",
			"wg/description-logic/Manifest024.rdf",
			"wg/description-logic/Manifest025.rdf",
			"wg/description-logic/Manifest026.rdf",
			"wg/description-logic/Manifest027.rdf",
			"wg/description-logic/Manifest028.rdf",
			"wg/description-logic/Manifest029.rdf",
			"wg/description-logic/Manifest030.rdf",
			"wg/description-logic/Manifest031.rdf",
			"wg/description-logic/Manifest032.rdf",
			"wg/description-logic/Manifest033.rdf",
			"wg/description-logic/Manifest034.rdf",
			"wg/description-logic/Manifest035.rdf",
			"wg/description-logic/Manifest040.rdf",
			"wg/description-logic/Manifest101.rdf",
			"wg/description-logic/Manifest102.rdf",
			"wg/description-logic/Manifest103.rdf",
			"wg/description-logic/Manifest104.rdf",
			"wg/description-logic/Manifest105.rdf",
			"wg/description-logic/Manifest106.rdf",
			"wg/description-logic/Manifest107.rdf",
			"wg/description-logic/Manifest108.rdf",
			"wg/description-logic/Manifest109.rdf",
			"wg/description-logic/Manifest110.rdf",
			"wg/description-logic/Manifest111.rdf",
			"wg/description-logic/Manifest201.rdf",
			"wg/description-logic/Manifest202.rdf",
			"wg/description-logic/Manifest203.rdf",
			"wg/description-logic/Manifest204.rdf",
			"wg/description-logic/Manifest205.rdf",
			"wg/description-logic/Manifest206.rdf",
			"wg/description-logic/Manifest207.rdf",
			"wg/description-logic/Manifest208.rdf",
			"wg/description-logic/Manifest209.rdf",
			"wg/description-logic/Manifest501.rdf",
			"wg/description-logic/Manifest502.rdf",
			"wg/description-logic/Manifest503.rdf",
			"wg/description-logic/Manifest504.rdf",
			"wg/description-logic/Manifest601.rdf",
			"wg/description-logic/Manifest602.rdf",
			"wg/description-logic/Manifest603.rdf",
			"wg/description-logic/Manifest604.rdf",
			"wg/description-logic/Manifest605.rdf",
			"wg/description-logic/Manifest606.rdf",
			"wg/description-logic/Manifest608.rdf",
			"wg/description-logic/Manifest609.rdf",
			"wg/description-logic/Manifest610.rdf",
			"wg/description-logic/Manifest611.rdf",
			"wg/description-logic/Manifest612.rdf",
			"wg/description-logic/Manifest613.rdf",
			"wg/description-logic/Manifest614.rdf",
			"wg/description-logic/Manifest615.rdf",
			"wg/description-logic/Manifest616.rdf",
			"wg/description-logic/Manifest617.rdf",
			"wg/description-logic/Manifest623.rdf",
			"wg/description-logic/Manifest624.rdf",
			"wg/description-logic/Manifest625.rdf",
			"wg/description-logic/Manifest626.rdf",
			"wg/description-logic/Manifest627.rdf",
			"wg/description-logic/Manifest628.rdf",
			"wg/description-logic/Manifest629.rdf",
			"wg/description-logic/Manifest630.rdf",
			"wg/description-logic/Manifest631.rdf",
			"wg/description-logic/Manifest632.rdf",
			"wg/description-logic/Manifest633.rdf",
			"wg/description-logic/Manifest634.rdf",
			"wg/description-logic/Manifest641.rdf",
			"wg/description-logic/Manifest642.rdf",
			"wg/description-logic/Manifest643.rdf",
			"wg/description-logic/Manifest644.rdf",
			"wg/description-logic/Manifest646.rdf",
			"wg/description-logic/Manifest650.rdf",
			"wg/description-logic/Manifest661.rdf",
			"wg/description-logic/Manifest662.rdf",
			"wg/description-logic/Manifest663.rdf",
			"wg/description-logic/Manifest664.rdf",
			"wg/description-logic/Manifest665.rdf",
			"wg/description-logic/Manifest667.rdf",
			"wg/description-logic/Manifest901.rdf",
			"wg/description-logic/Manifest902.rdf",
			"wg/description-logic/Manifest903.rdf",
			"wg/description-logic/Manifest904.rdf",
			"wg/description-logic/Manifest905.rdf",
			"wg/description-logic/Manifest906.rdf",
			"wg/description-logic/Manifest907.rdf",
			"wg/description-logic/Manifest908.rdf",
			"wg/description-logic/Manifest909.rdf",
			"wg/description-logic/Manifest910.rdf",
			"wg/description-logic/nonconclusions005.rdf",
			"wg/description-logic/nonconclusions006.rdf",
			"wg/description-logic/nonconclusions009.rdf",
			"wg/description-logic/nonconclusions016.rdf",
			"wg/description-logic/nonconclusions018.rdf",
			"wg/description-logic/nonconclusions020.rdf",
			"wg/description-logic/nonconclusions021.rdf",
			"wg/description-logic/nonconclusions024.rdf",
			"wg/description-logic/nonconclusions025.rdf",
			"wg/description-logic/nonconclusions028.rdf",
			"wg/description-logic/nonconclusions031.rdf",
			"wg/description-logic/nonconclusions034.rdf",
			"wg/description-logic/nonconclusions209.rdf",
			"wg/description-logic/nonconclusions902.rdf",
			"wg/description-logic/nonconclusions904.rdf",
			"wg/description-logic/premises001.rdf",
			"wg/description-logic/premises002.rdf",
			"wg/description-logic/premises003.rdf",
			"wg/description-logic/premises004.rdf",
			"wg/description-logic/premises005.rdf",
			"wg/description-logic/premises006.rdf",
			"wg/description-logic/premises007.rdf",
			"wg/description-logic/premises008.rdf",
			"wg/description-logic/premises009.rdf",
			"wg/description-logic/premises010.rdf",
			"wg/description-logic/premises011.rdf",
			"wg/description-logic/premises012.rdf",
			"wg/description-logic/premises013.rdf",
			"wg/description-logic/premises014.rdf",
			"wg/description-logic/premises015.rdf",
			"wg/description-logic/premises016.rdf",
			"wg/description-logic/premises017.rdf",
			"wg/description-logic/premises018.rdf",
			"wg/description-logic/premises019.rdf",
			"wg/description-logic/premises020.rdf",
			"wg/description-logic/premises021.rdf",
			"wg/description-logic/premises022.rdf",
			"wg/description-logic/premises023.rdf",
			"wg/description-logic/premises024.rdf",
			"wg/description-logic/premises025.rdf",
			"wg/description-logic/premises026.rdf",
			"wg/description-logic/premises027.rdf",
			"wg/description-logic/premises028.rdf",
			"wg/description-logic/premises029.rdf",
			"wg/description-logic/premises030.rdf",
			"wg/description-logic/premises031.rdf",
			"wg/description-logic/premises032.rdf",
			"wg/description-logic/premises033.rdf",
			"wg/description-logic/premises034.rdf",
			"wg/description-logic/premises101.rdf",
			"wg/description-logic/premises102.rdf",
			"wg/description-logic/premises103.rdf",
			"wg/description-logic/premises104.rdf",
			"wg/description-logic/premises105.rdf",
			"wg/description-logic/premises106.rdf",
			"wg/description-logic/premises107.rdf",
			"wg/description-logic/premises108.rdf",
			"wg/description-logic/premises109.rdf",
			"wg/description-logic/premises110.rdf",
			"wg/description-logic/premises111.rdf",
			"wg/description-logic/premises201.rdf",
			"wg/description-logic/premises202.rdf",
			"wg/description-logic/premises203.rdf",
			"wg/description-logic/premises204.rdf",
			"wg/description-logic/premises205.rdf",
			"wg/description-logic/premises206.rdf",
			"wg/description-logic/premises207.rdf",
			"wg/description-logic/premises208.rdf",
			"wg/description-logic/premises209.rdf",
			"wg/description-logic/premises661.rdf",
			"wg/description-logic/premises662.rdf",
			"wg/description-logic/premises663.rdf",
			"wg/description-logic/premises664.rdf",
			"wg/description-logic/premises665.rdf",
			"wg/description-logic/premises667.rdf",
			"wg/description-logic/premises901.rdf",
			"wg/description-logic/premises902.rdf",
			"wg/description-logic/premises903.rdf",
			"wg/description-logic/premises904.rdf",
			"wg/description-logic/premises905.rdf",
			"wg/differentFrom/conclusions001.rdf",
			"wg/differentFrom/conclusions002.rdf",
			"wg/differentFrom/Manifest001.rdf",
			"wg/differentFrom/Manifest002.rdf",
			"wg/differentFrom/premises001.rdf",
			"wg/differentFrom/premises002.rdf",
			"wg/disjointWith/conclusions001.rdf",
			"wg/disjointWith/conclusions002.rdf",
			"wg/disjointWith/consistent003.rdf",
			"wg/disjointWith/consistent004.rdf",
			"wg/disjointWith/consistent005.rdf",
			"wg/disjointWith/consistent006.rdf",
			"wg/disjointWith/consistent007.rdf",
			"wg/disjointWith/consistent008.rdf",
			"wg/disjointWith/consistent009.rdf",
			"wg/disjointWith/inconsistent010.rdf",
			"wg/disjointWith/Manifest001.rdf",
			"wg/disjointWith/Manifest002.rdf",
			"wg/disjointWith/Manifest003.rdf",
			"wg/disjointWith/Manifest004.rdf",
			"wg/disjointWith/Manifest005.rdf",
			"wg/disjointWith/Manifest006.rdf",
			"wg/disjointWith/Manifest007.rdf",
			"wg/disjointWith/Manifest008.rdf",
			"wg/disjointWith/Manifest009.rdf",
			"wg/disjointWith/Manifest010.rdf",
			"wg/disjointWith/premises001.rdf",
			"wg/disjointWith/premises002.rdf",
			"wg/distinctMembers/conclusions001.rdf",
			"wg/distinctMembers/Manifest001.rdf",
			"wg/distinctMembers/premises001.rdf", "wg/empty.rdf",
			"wg/equivalentClass/conclusions001.rdf",
			"wg/equivalentClass/conclusions002.rdf",
			"wg/equivalentClass/conclusions003.rdf",
			"wg/equivalentClass/conclusions004.rdf",
			"wg/equivalentClass/conclusions006.rdf",
			"wg/equivalentClass/conclusions007.rdf",
			"wg/equivalentClass/consistent009.rdf",
			"wg/equivalentClass/Manifest001.rdf",
			"wg/equivalentClass/Manifest002.rdf",
			"wg/equivalentClass/Manifest003.rdf",
			"wg/equivalentClass/Manifest004.rdf",
			"wg/equivalentClass/Manifest005.rdf",
			"wg/equivalentClass/Manifest006.rdf",
			"wg/equivalentClass/Manifest007.rdf",
			"wg/equivalentClass/Manifest008.rdf",
			"wg/equivalentClass/Manifest009.rdf",
			"wg/equivalentClass/nonconclusions005.rdf",
			"wg/equivalentClass/nonconclusions008.rdf",
			"wg/equivalentClass/premises001.rdf",
			"wg/equivalentClass/premises002.rdf",
			"wg/equivalentClass/premises003.rdf",
			"wg/equivalentClass/premises004.rdf",
			"wg/equivalentClass/premises005.rdf",
			"wg/equivalentClass/premises006.rdf",
			"wg/equivalentClass/premises007.rdf",
			"wg/equivalentClass/premises008.rdf",
			"wg/equivalentProperty/conclusions001.rdf",
			"wg/equivalentProperty/conclusions002.rdf",
			"wg/equivalentProperty/conclusions003.rdf",
			"wg/equivalentProperty/conclusions004.rdf",
			"wg/equivalentProperty/conclusions005.rdf",
			"wg/equivalentProperty/conclusions006.rdf",
			"wg/equivalentProperty/Manifest001.rdf",
			"wg/equivalentProperty/Manifest002.rdf",
			"wg/equivalentProperty/Manifest003.rdf",
			"wg/equivalentProperty/Manifest004.rdf",
			"wg/equivalentProperty/Manifest005.rdf",
			"wg/equivalentProperty/Manifest006.rdf",
			"wg/equivalentProperty/premises001.rdf",
			"wg/equivalentProperty/premises002.rdf",
			"wg/equivalentProperty/premises003.rdf",
			"wg/equivalentProperty/premises004.rdf",
			"wg/equivalentProperty/premises005.rdf",
			"wg/equivalentProperty/premises006.rdf",
			"wg/extra-credit/conclusions002.rdf",
			"wg/extra-credit/conclusions003.rdf",
			"wg/extra-credit/conclusions004.rdf",
			"wg/extra-credit/Manifest002.rdf",
			"wg/extra-credit/Manifest003.rdf",
			"wg/extra-credit/Manifest004.rdf",
			"wg/extra-credit/premises002.rdf",
			"wg/extra-credit/premises003.rdf",
			"wg/extra-credit/premises004.rdf", "wg/false.rdf",
			"wg/FunctionalProperty/conclusions001.rdf",
			"wg/FunctionalProperty/conclusions002.rdf",
			"wg/FunctionalProperty/conclusions003.rdf",
			"wg/FunctionalProperty/conclusions004.rdf",
			"wg/FunctionalProperty/conclusions005-mod.rdf",
			"wg/FunctionalProperty/conclusions005.rdf",
			"wg/FunctionalProperty/Manifest001.rdf",
			"wg/FunctionalProperty/Manifest002.rdf",
			"wg/FunctionalProperty/Manifest003.rdf",
			"wg/FunctionalProperty/Manifest004.rdf",
			"wg/FunctionalProperty/Manifest005-mod.rdf",
			"wg/FunctionalProperty/Manifest005.rdf",
			"wg/FunctionalProperty/nonconclusions004.rdf",
			"wg/FunctionalProperty/premises001.rdf",
			"wg/FunctionalProperty/premises002.rdf",
			"wg/FunctionalProperty/premises003.rdf",
			"wg/FunctionalProperty/premises004.rdf",
			"wg/FunctionalProperty/premises005-mod.rdf",
			"wg/FunctionalProperty/premises005.rdf", "wg/I3.2/bad001.rdf",
			"wg/I3.2/bad002.rdf", "wg/I3.2/bad003.rdf",
			"wg/I3.2/Manifest001.rdf", "wg/I3.2/Manifest002.rdf",
			"wg/I3.2/Manifest003.rdf", "wg/I3.4/bad001.rdf",
			"wg/I3.4/Manifest001.rdf", "wg/I4.1/bad001.rdf",
			"wg/I4.1/Manifest001.rdf", "wg/I4.5/conclusions001.rdf",
			"wg/I4.5/inconsistent002.rdf", "wg/I4.5/Manifest001.rdf",
			"wg/I4.5/Manifest002.rdf", "wg/I4.5/premises001.rdf",
			"wg/I4.6/bad006.rdf", "wg/I4.6/bad007.rdf", "wg/I4.6/bad008.rdf",
			"wg/I4.6/conclusions003.rdf", "wg/I4.6/conclusions004.rdf",
			"wg/I4.6/Manifest003.rdf", "wg/I4.6/Manifest004.rdf",
			"wg/I4.6/Manifest005.rdf", "wg/I4.6/Manifest006.rdf",
			"wg/I4.6/Manifest007.rdf", "wg/I4.6/Manifest008.rdf",
			"wg/I4.6/nonconclusions004.rdf", "wg/I4.6/nonconclusions005.rdf",
			"wg/I4.6/premises003.rdf", "wg/I4.6/premises004.rdf",
			"wg/I4.6/premises005.rdf", "wg/I5.1/conclusions001.rdf",
			"wg/I5.1/consistent010.rdf", "wg/I5.1/Manifest001.rdf",
			"wg/I5.1/Manifest010.rdf", "wg/I5.1/premises001.rdf",
			"wg/I5.2/conclusions002.rdf", "wg/I5.2/conclusions004.rdf",
			"wg/I5.2/conclusions006.rdf", "wg/I5.2/consistent001.rdf",
			"wg/I5.2/consistent003.rdf", "wg/I5.2/consistent005.rdf",
			"wg/I5.2/consistent010.rdf", "wg/I5.2/consistent011.rdf",
			"wg/I5.2/Manifest001.rdf", "wg/I5.2/Manifest002.rdf",
			"wg/I5.2/Manifest003.rdf", "wg/I5.2/Manifest004.rdf",
			"wg/I5.2/Manifest005.rdf", "wg/I5.2/Manifest006.rdf",
			"wg/I5.2/Manifest010.rdf", "wg/I5.2/Manifest011.rdf",
			"wg/I5.2/premises002.rdf", "wg/I5.2/premises004.rdf",
			"wg/I5.2/premises006.rdf", "wg/I5.21/bad001.rdf",
			"wg/I5.21/conclusions002.rdf", "wg/I5.21/Manifest001.rdf",
			"wg/I5.21/Manifest002.rdf", "wg/I5.21/premises002.rdf",
			"wg/I5.24/conclusions001.rdf", "wg/I5.24/conclusions002-mod.rdf",
			"wg/I5.24/conclusions002.rdf", "wg/I5.24/conclusions003-mod.rdf",
			"wg/I5.24/conclusions003.rdf", "wg/I5.24/conclusions004.rdf",
			"wg/I5.24/Manifest001.rdf", "wg/I5.24/Manifest002-mod.rdf",
			"wg/I5.24/Manifest002.rdf", "wg/I5.24/Manifest003-mod.rdf",
			"wg/I5.24/Manifest003.rdf", "wg/I5.24/Manifest004-mod.rdf",
			"wg/I5.24/Manifest004.rdf", "wg/I5.24/premises001.rdf",
			"wg/I5.24/premises002-mod.rdf", "wg/I5.24/premises002.rdf",
			"wg/I5.24/premises003-mod.rdf", "wg/I5.24/premises003.rdf",
			"wg/I5.24/premises004-mod.rdf", "wg/I5.24/premises004.rdf",
			"wg/I5.26/conclusions009.rdf", "wg/I5.26/conclusions010.rdf",
			"wg/I5.26/consistent001.rdf", "wg/I5.26/consistent002.rdf",
			"wg/I5.26/consistent003.rdf", "wg/I5.26/consistent004.rdf",
			"wg/I5.26/consistent005.rdf", "wg/I5.26/consistent006.rdf",
			"wg/I5.26/consistent007.rdf", "wg/I5.26/Manifest001.rdf",
			"wg/I5.26/Manifest002.rdf", "wg/I5.26/Manifest003.rdf",
			"wg/I5.26/Manifest004.rdf", "wg/I5.26/Manifest005.rdf",
			"wg/I5.26/Manifest006.rdf", "wg/I5.26/Manifest007.rdf",
			"wg/I5.26/Manifest009.rdf", "wg/I5.26/Manifest010.rdf",
			"wg/I5.26/premises009.rdf", "wg/I5.26/premises010.rdf",
			"wg/I5.3/conclusions014.rdf", "wg/I5.3/conclusions015.rdf",
			"wg/I5.3/consistent005.rdf", "wg/I5.3/consistent006.rdf",
			"wg/I5.3/consistent007.rdf", "wg/I5.3/consistent008.rdf",
			"wg/I5.3/consistent009.rdf", "wg/I5.3/consistent010.rdf",
			"wg/I5.3/consistent011.rdf", "wg/I5.3/Manifest005.rdf",
			"wg/I5.3/Manifest006.rdf", "wg/I5.3/Manifest007.rdf",
			"wg/I5.3/Manifest008.rdf", "wg/I5.3/Manifest009.rdf",
			"wg/I5.3/Manifest010.rdf", "wg/I5.3/Manifest011.rdf",
			"wg/I5.3/Manifest014.rdf", "wg/I5.3/Manifest015.rdf",
			"wg/I5.3/premises014.rdf", "wg/I5.3/premises015.rdf",
			"wg/I5.5/conclusions001.rdf", "wg/I5.5/conclusions002.rdf",
			"wg/I5.5/conclusions005.rdf", "wg/I5.5/inconsistent003.rdf",
			"wg/I5.5/inconsistent004.rdf", "wg/I5.5/Manifest001.rdf",
			"wg/I5.5/Manifest002.rdf", "wg/I5.5/Manifest003.rdf",
			"wg/I5.5/Manifest004.rdf", "wg/I5.5/Manifest005.rdf",
			"wg/I5.5/Manifest006.rdf", "wg/I5.5/Manifest007.rdf",
			"wg/I5.5/nonconclusions006.rdf", "wg/I5.5/nonconclusions007.rdf",
			"wg/I5.5/premises001.rdf", "wg/I5.5/premises002.rdf",
			"wg/I5.5/premises005.rdf", "wg/I5.5/premises006.rdf",
			"wg/I5.5/premises007.rdf", "wg/I5.8/conclusions004.rdf",
			"wg/I5.8/conclusions006.rdf", "wg/I5.8/conclusions008.rdf",
			"wg/I5.8/conclusions009.rdf", "wg/I5.8/conclusions010.rdf",
			"wg/I5.8/conclusions011.rdf", "wg/I5.8/conclusions017.rdf",
			"wg/I5.8/consistent002.rdf", "wg/I5.8/consistent012.rdf",
			"wg/I5.8/consistent013.rdf", "wg/I5.8/consistent014.rdf",
			"wg/I5.8/consistent015.rdf", "wg/I5.8/consistent016.rdf",
			"wg/I5.8/inconsistent001.rdf", "wg/I5.8/inconsistent003.rdf",
			"wg/I5.8/Manifest001.rdf", "wg/I5.8/Manifest002.rdf",
			"wg/I5.8/Manifest003.rdf", "wg/I5.8/Manifest004.rdf",
			"wg/I5.8/Manifest005.rdf", "wg/I5.8/Manifest006.rdf",
			"wg/I5.8/Manifest007.rdf", "wg/I5.8/Manifest008.rdf",
			"wg/I5.8/Manifest009.rdf", "wg/I5.8/Manifest010.rdf",
			"wg/I5.8/Manifest011.rdf", "wg/I5.8/Manifest012.rdf",
			"wg/I5.8/Manifest013.rdf", "wg/I5.8/Manifest014.rdf",
			"wg/I5.8/Manifest015.rdf", "wg/I5.8/Manifest016.rdf",
			"wg/I5.8/Manifest017.rdf", "wg/I5.8/nonconclusions005.rdf",
			"wg/I5.8/nonconclusions007.rdf", "wg/I5.8/premises004.rdf",
			"wg/I5.8/premises005.rdf", "wg/I5.8/premises006.rdf",
			"wg/I5.8/premises007.rdf", "wg/I5.8/premises008.rdf",
			"wg/I5.8/premises009.rdf", "wg/I5.8/premises010.rdf",
			"wg/I5.8/premises011.rdf", "wg/I5.8/premises017.rdf",
			"wg/I6.1/consistent001.rdf", "wg/I6.1/Manifest001.rdf",
			"wg/imports/conclusions001.rdf", "wg/imports/conclusions002.rdf",
			"wg/imports/conclusions003.rdf", "wg/imports/conclusions010.rdf",
			"wg/imports/conclusions011.rdf", "wg/imports/consistent012.rdf",
			"wg/imports/imports004.rdf", "wg/imports/imports005.rdf",
			"wg/imports/imports006.rdf", "wg/imports/imports007.rdf",
			"wg/imports/imports008.rdf", "wg/imports/imports013.rdf",
			"wg/imports/imports014.rdf", "wg/imports/main004.rdf",
			"wg/imports/main005.rdf", "wg/imports/main006.rdf",
			"wg/imports/main007.rdf", "wg/imports/main008.rdf",
			"wg/imports/main013.rdf", "wg/imports/main014.rdf",
			"wg/imports/Manifest001.rdf", "wg/imports/Manifest002.rdf",
			"wg/imports/Manifest003.rdf", "wg/imports/Manifest004.rdf",
			"wg/imports/Manifest005.rdf", "wg/imports/Manifest006.rdf",
			"wg/imports/Manifest007.rdf", "wg/imports/Manifest008.rdf",
			"wg/imports/Manifest010.rdf", "wg/imports/Manifest011.rdf",
			"wg/imports/Manifest012.rdf", "wg/imports/Manifest013.rdf",
			"wg/imports/Manifest014.rdf", "wg/imports/nonconclusions002.rdf",
			"wg/imports/premises001.rdf", "wg/imports/premises002.rdf",
			"wg/imports/premises003.rdf", "wg/imports/premises011.rdf",
			"wg/imports/support001-A.rdf", "wg/imports/support002-A.rdf",
			"wg/imports/support003-A.rdf", "wg/imports/support003-B.rdf",
			"wg/imports/support011-A.rdf",
			"wg/intersectionOf/conclusions001.rdf",
			"wg/intersectionOf/Manifest001.rdf",
			"wg/intersectionOf/premises001.rdf",
			"wg/InverseFunctionalProperty/conclusions001.rdf",
			"wg/InverseFunctionalProperty/conclusions002.rdf",
			"wg/InverseFunctionalProperty/conclusions003.rdf",
			"wg/InverseFunctionalProperty/conclusions004.rdf",
			"wg/InverseFunctionalProperty/Manifest001.rdf",
			"wg/InverseFunctionalProperty/Manifest002.rdf",
			"wg/InverseFunctionalProperty/Manifest003.rdf",
			"wg/InverseFunctionalProperty/Manifest004.rdf",
			"wg/InverseFunctionalProperty/nonconclusions004.rdf",
			"wg/InverseFunctionalProperty/premises001.rdf",
			"wg/InverseFunctionalProperty/premises002.rdf",
			"wg/InverseFunctionalProperty/premises003.rdf",
			"wg/InverseFunctionalProperty/premises004.rdf",
			"wg/inverseOf/conclusions001.rdf", "wg/inverseOf/Manifest001.rdf",
			"wg/inverseOf/premises001.rdf", "wg/localtests/conclusions001.rdf",
			"wg/localtests/conclusions002.rdf",
			"wg/localtests/conclusions003.rdf",
			"wg/localtests/conclusions004.rdf",
			"wg/localtests/conclusions005.rdf",
			"wg/localtests/conclusions006.rdf",
			"wg/localtests/conclusionsRestriction001.rdf",
			"wg/localtests/conclusionsSubclass001.rdf",
			"wg/localtests/Manifest001.rdf", "wg/localtests/Manifest002.rdf",
			"wg/localtests/Manifest003.rdf", "wg/localtests/Manifest004.rdf",
			"wg/localtests/Manifest005.rdf", "wg/localtests/Manifest006.rdf",
			"wg/localtests/ManifestRestriction001.rdf",
			"wg/localtests/ManifestSubclass001.rdf",
			"wg/localtests/premises001.rdf", "wg/localtests/premises003.rdf",
			"wg/localtests/premises004.rdf", "wg/localtests/premises005.rdf",
			"wg/localtests/premises006.rdf",
			"wg/localtests/premisesRestriction001.rdf",
			"wg/localtests/premisesSubclass001.rdf", "wg/Manifest-extra.rdf",
			"wg/Manifest-wrong.rdf", "wg/Manifest.rdf",
			"wg/maxCardinality/inconsistent001.rdf",
			"wg/maxCardinality/inconsistent002.rdf",
			"wg/maxCardinality/Manifest001.rdf",
			"wg/maxCardinality/Manifest002.rdf",
			"wg/miscellaneous/conclusions010.rdf",
			"wg/miscellaneous/conclusions011.rdf",
			"wg/miscellaneous/consistent001.rdf",
			"wg/miscellaneous/consistent002.rdf",
			"wg/miscellaneous/consistent102.rdf",
			"wg/miscellaneous/consistent103.rdf",
			"wg/miscellaneous/consistent201.rdf",
			"wg/miscellaneous/consistent202.rdf",
			"wg/miscellaneous/consistent205.rdf",
			"wg/miscellaneous/consistent303.rdf",
			"wg/miscellaneous/example001.rdf",
			"wg/miscellaneous/example002.rdf",
			"wg/miscellaneous/inconsistent203.rdf",
			"wg/miscellaneous/inconsistent204.rdf",
			"wg/miscellaneous/Manifest001.rdf",
			"wg/miscellaneous/Manifest002.rdf",
			"wg/miscellaneous/Manifest010.rdf",
			"wg/miscellaneous/Manifest011.rdf",
			"wg/miscellaneous/Manifest102.rdf",
			"wg/miscellaneous/Manifest103.rdf",
			"wg/miscellaneous/Manifest201.rdf",
			"wg/miscellaneous/Manifest202.rdf",
			"wg/miscellaneous/Manifest203.rdf",
			"wg/miscellaneous/Manifest204.rdf",
			"wg/miscellaneous/Manifest205.rdf",
			"wg/miscellaneous/Manifest301.rdf",
			"wg/miscellaneous/Manifest302.rdf",
			"wg/miscellaneous/Manifest303.rdf",
			"wg/miscellaneous/nonconclusions301.rdf",
			"wg/miscellaneous/nonconclusions302.rdf",
			"wg/miscellaneous/old-consistent001.rdf",
			"wg/miscellaneous/old-consistent002.rdf",
			"wg/miscellaneous/premises010.rdf",
			"wg/miscellaneous/premises011.rdf",
			"wg/miscellaneous/premises301.rdf",
			"wg/miscellaneous/premises302.rdf",
			"wg/Nothing/conclusions002.rdf", "wg/Nothing/inconsistent001.rdf",
			"wg/Nothing/Manifest001.rdf", "wg/Nothing/Manifest002.rdf",
			"wg/Nothing/premises002.rdf", "wg/oneOf/conclusions002.rdf",
			"wg/oneOf/conclusions003.rdf", "wg/oneOf/conclusions004.rdf",
			"wg/oneOf/consistent001.rdf", "wg/oneOf/Manifest001.rdf",
			"wg/oneOf/Manifest002.rdf", "wg/oneOf/Manifest003.rdf",
			"wg/oneOf/Manifest004.rdf", "wg/oneOf/premises002.rdf",
			"wg/oneOf/premises003.rdf", "wg/oneOf/premises004.rdf",
			"wg/Ontology/conclusions001.rdf", "wg/Ontology/conclusions004.rdf",
			"wg/Ontology/Manifest001.rdf", "wg/Ontology/Manifest003.rdf",
			"wg/Ontology/Manifest004.rdf", "wg/Ontology/nonconclusions003.rdf",
			"wg/Ontology/premises001.rdf", "wg/Ontology/premises003.rdf",
			"wg/Ontology/premises004.rdf", "wg/OWLManifest.rdf",
			"wg/rdf-charmod-literals/error001.rdf",
			"wg/rdf-charmod-literals/error002.rdf",
			"wg/rdf-charmod-literals/test001.rdf",
			"wg/rdf-charmod-uris/error001.rdf",
			"wg/rdf-charmod-uris/Manifest.rdf",
			"wg/rdf-charmod-uris/test001.rdf",
			"wg/rdf-charmod-uris/test002.rdf",
			"wg/rdf-containers-syntax-vs-schema/error001.rdf",
			"wg/rdf-containers-syntax-vs-schema/error002.rdf",
			"wg/rdf-containers-syntax-vs-schema/test001.rdf",
			"wg/rdf-containers-syntax-vs-schema/test002.rdf",
			"wg/rdf-containers-syntax-vs-schema/test003.rdf",
			"wg/rdf-containers-syntax-vs-schema/test004.rdf",

			"wg/rdf-containers-syntax-vs-schema/test005.rdf",

			"wg/rdf-containers-syntax-vs-schema/test006.rdf",
			"wg/rdf-containers-syntax-vs-schema/test007.rdf",
			"wg/rdf-containers-syntax-vs-schema/test008.rdf",
			"wg/rdf-ns-prefix-confusion/error0001.rdf",
			"wg/rdf-ns-prefix-confusion/error0002.rdf",
			"wg/rdf-ns-prefix-confusion/error0003.rdf",
			"wg/rdf-ns-prefix-confusion/error0004.rdf",
			"wg/rdf-ns-prefix-confusion/error0005.rdf",
			"wg/rdf-ns-prefix-confusion/error0006.rdf",
			"wg/rdf-ns-prefix-confusion/error0007.rdf",
			"wg/rdf-ns-prefix-confusion/error0008.rdf",
			"wg/rdf-ns-prefix-confusion/error0009.rdf",
			"wg/rdf-ns-prefix-confusion/test0001.rdf",
			"wg/rdf-ns-prefix-confusion/test0002.rdf",
			"wg/rdf-ns-prefix-confusion/test0003.rdf",
			"wg/rdf-ns-prefix-confusion/test0004.rdf",
			"wg/rdf-ns-prefix-confusion/test0005.rdf",
			"wg/rdf-ns-prefix-confusion/test0006.rdf",
			"wg/rdf-ns-prefix-confusion/test0007.rdf",
			"wg/rdf-ns-prefix-confusion/test0008.rdf",
			"wg/rdf-ns-prefix-confusion/test0009.rdf",
			"wg/rdf-ns-prefix-confusion/test0010.rdf",
			"wg/rdf-ns-prefix-confusion/test0011.rdf",
			"wg/rdf-ns-prefix-confusion/test0012.rdf",
			"wg/rdf-ns-prefix-confusion/test0013.rdf",
			"wg/rdf-ns-prefix-confusion/test0014.rdf",
			"wg/rdfms-abouteach/error001.rdf",
			"wg/rdfms-abouteach/error002.rdf",
			"wg/rdfms-difference-between-ID-and-about/error1.rdf",
			"wg/rdfms-difference-between-ID-and-about/test1.rdf",
			"wg/rdfms-difference-between-ID-and-about/test2.rdf",
			"wg/rdfms-difference-between-ID-and-about/test3.rdf",
			"wg/rdfms-duplicate-member-props/test001.rdf",
			"wg/rdfms-empty-property-elements/error001.rdf",
			"wg/rdfms-empty-property-elements/error002.rdf",
			"wg/rdfms-empty-property-elements/error003.rdf",
			"wg/rdfms-empty-property-elements/test001.rdf",
			"wg/rdfms-empty-property-elements/test002.rdf",
			"wg/rdfms-empty-property-elements/test003.rdf",
			"wg/rdfms-empty-property-elements/test004.rdf",
			"wg/rdfms-empty-property-elements/test005.rdf",
			"wg/rdfms-empty-property-elements/test006.rdf",
			"wg/rdfms-empty-property-elements/test007.rdf",
			"wg/rdfms-empty-property-elements/test008.rdf",
			"wg/rdfms-empty-property-elements/test009.rdf",
			"wg/rdfms-empty-property-elements/test010.rdf",
			"wg/rdfms-empty-property-elements/test011.rdf",
			"wg/rdfms-empty-property-elements/test012.rdf",
			"wg/rdfms-empty-property-elements/test013.rdf",
			"wg/rdfms-empty-property-elements/test014.rdf",
			"wg/rdfms-empty-property-elements/test015.rdf",
			"wg/rdfms-empty-property-elements/test016.rdf",
			"wg/rdfms-empty-property-elements/test017.rdf",
			"wg/rdfms-identity-anon-resources/test001.rdf",
			"wg/rdfms-identity-anon-resources/test002.rdf",
			"wg/rdfms-identity-anon-resources/test003.rdf",
			"wg/rdfms-identity-anon-resources/test004.rdf",
			"wg/rdfms-identity-anon-resources/test005.rdf",
			"wg/rdfms-literal-is-xml-structure/test001.rdf",
			"wg/rdfms-literal-is-xml-structure/test002.rdf",
			"wg/rdfms-literal-is-xml-structure/test003.rdf",
			"wg/rdfms-literal-is-xml-structure/test004.rdf",
			"wg/rdfms-literal-is-xml-structure/test005.rdf",
			//	"wg/rdfms-nested-bagIDs/test001.rdf",
			"wg/rdfms-nested-bagIDs/test002.rdf",
			//	"wg/rdfms-nested-bagIDs/test003.rdf",
			"wg/rdfms-nested-bagIDs/test004.rdf",
			"wg/rdfms-nested-bagIDs/test005.rdf",
			"wg/rdfms-nested-bagIDs/test006.rdf",
			"wg/rdfms-nested-bagIDs/test007.rdf",
			"wg/rdfms-nested-bagIDs/test008.rdf",
			"wg/rdfms-nested-bagIDs/test009.rdf",
			"wg/rdfms-nested-bagIDs/test010.rdf",
			"wg/rdfms-nested-bagIDs/test011.rdf",
			"wg/rdfms-nested-bagIDs/test012.rdf",
			"wg/rdfms-not-id-and-resource-attr/test001.rdf",
			"wg/rdfms-not-id-and-resource-attr/test002.rdf",
			"wg/rdfms-not-id-and-resource-attr/test003.rdf",
			"wg/rdfms-not-id-and-resource-attr/test004.rdf",
			"wg/rdfms-not-id-and-resource-attr/test005.rdf",
			"wg/rdfms-para196/test001.rdf", "wg/rdfms-parseType/error001.rdf",
			"wg/rdfms-parseType/error002.rdf",
			"wg/rdfms-parseType/error003.rdf", "wg/rdfms-rdf-id/error001.rdf",
			"wg/rdfms-rdf-id/error002.rdf", "wg/rdfms-rdf-id/error003.rdf",
			"wg/rdfms-rdf-id/error004.rdf", "wg/rdfms-rdf-id/error005.rdf",
			"wg/rdfms-rdf-id/error006.rdf", "wg/rdfms-rdf-id/error007.rdf",
			"wg/rdfms-rdf-names-use/error-001.rdf",
			"wg/rdfms-rdf-names-use/error-002.rdf",
			"wg/rdfms-rdf-names-use/error-003.rdf",
			"wg/rdfms-rdf-names-use/error-004.rdf",
			"wg/rdfms-rdf-names-use/error-005.rdf",
			"wg/rdfms-rdf-names-use/error-006.rdf",
			"wg/rdfms-rdf-names-use/error-007.rdf",
			"wg/rdfms-rdf-names-use/error-008.rdf",
			"wg/rdfms-rdf-names-use/error-009.rdf",
			"wg/rdfms-rdf-names-use/error-010.rdf",
			"wg/rdfms-rdf-names-use/error-011.rdf",
			"wg/rdfms-rdf-names-use/error-012.rdf",
			"wg/rdfms-rdf-names-use/error-013.rdf",
			"wg/rdfms-rdf-names-use/error-014.rdf",
			"wg/rdfms-rdf-names-use/error-015.rdf",
			"wg/rdfms-rdf-names-use/error-016.rdf",
			"wg/rdfms-rdf-names-use/error-017.rdf",
			"wg/rdfms-rdf-names-use/error-018.rdf",
			"wg/rdfms-rdf-names-use/error-019.rdf",
			"wg/rdfms-rdf-names-use/error-020.rdf",
			"wg/rdfms-rdf-names-use/test-001.rdf",
			"wg/rdfms-rdf-names-use/test-002.rdf",
			"wg/rdfms-rdf-names-use/test-003.rdf",
			"wg/rdfms-rdf-names-use/test-004.rdf",
			"wg/rdfms-rdf-names-use/test-005.rdf",
			"wg/rdfms-rdf-names-use/test-006.rdf",
			"wg/rdfms-rdf-names-use/test-007.rdf",
			"wg/rdfms-rdf-names-use/test-008.rdf",
			"wg/rdfms-rdf-names-use/test-009.rdf",
			"wg/rdfms-rdf-names-use/test-010.rdf",
			"wg/rdfms-rdf-names-use/test-011.rdf",
			"wg/rdfms-rdf-names-use/test-012.rdf",
			"wg/rdfms-rdf-names-use/test-013.rdf",
			"wg/rdfms-rdf-names-use/test-014.rdf",
			"wg/rdfms-rdf-names-use/test-015.rdf",
			"wg/rdfms-rdf-names-use/test-016.rdf",
			"wg/rdfms-rdf-names-use/test-017.rdf",
			"wg/rdfms-rdf-names-use/test-018.rdf",
			"wg/rdfms-rdf-names-use/test-019.rdf",
			"wg/rdfms-rdf-names-use/test-020.rdf",
			"wg/rdfms-rdf-names-use/test-021.rdf",
			"wg/rdfms-rdf-names-use/test-022.rdf",
			"wg/rdfms-rdf-names-use/test-023.rdf",
			"wg/rdfms-rdf-names-use/test-024.rdf",
			"wg/rdfms-rdf-names-use/test-025.rdf",
			"wg/rdfms-rdf-names-use/test-026.rdf",
			"wg/rdfms-rdf-names-use/test-027.rdf",
			"wg/rdfms-rdf-names-use/test-028.rdf",
			"wg/rdfms-rdf-names-use/test-029.rdf",
			"wg/rdfms-rdf-names-use/test-030.rdf",
			"wg/rdfms-rdf-names-use/test-031.rdf",
			"wg/rdfms-rdf-names-use/test-032.rdf",
			"wg/rdfms-rdf-names-use/test-033.rdf",
			"wg/rdfms-rdf-names-use/test-034.rdf",
			"wg/rdfms-rdf-names-use/test-035.rdf",
			"wg/rdfms-rdf-names-use/test-036.rdf",
			"wg/rdfms-rdf-names-use/test-037.rdf",
			"wg/rdfms-rdf-names-use/warn-001.rdf",
			"wg/rdfms-rdf-names-use/warn-002.rdf",
			"wg/rdfms-rdf-names-use/warn-003.rdf",
			"wg/rdfms-reification-required/test001.rdf",
			"wg/rdfms-seq-representation/test001.rdf",
			"wg/rdfms-syntax-incomplete/error001.rdf",
			"wg/rdfms-syntax-incomplete/error002.rdf",
			"wg/rdfms-syntax-incomplete/error003.rdf",
			"wg/rdfms-syntax-incomplete/error004.rdf",
			"wg/rdfms-syntax-incomplete/error005.rdf",
			"wg/rdfms-syntax-incomplete/error006.rdf",
			"wg/rdfms-syntax-incomplete/test001.rdf",
			"wg/rdfms-syntax-incomplete/test002.rdf",
			"wg/rdfms-syntax-incomplete/test003.rdf",
			"wg/rdfms-syntax-incomplete/test004.rdf",
			"wg/rdfms-uri-substructure/test001.rdf",
			"wg/rdfms-xml-literal-namespaces/test001.rdf",
			"wg/rdfms-xml-literal-namespaces/test002.rdf",
			"wg/rdfms-xmllang/test001.rdf", "wg/rdfms-xmllang/test002.rdf",
			"wg/rdfms-xmllang/test003.rdf", "wg/rdfms-xmllang/test004.rdf",
			"wg/rdfms-xmllang/test005.rdf", "wg/rdfms-xmllang/test006.rdf",
			"wg/rdfs-container-membership-superProperty/not1C.rdf",
			"wg/rdfs-container-membership-superProperty/not1P.rdf",
			"wg/rdfs-domain-and-range/nonconclusions005.rdf",
			"wg/rdfs-domain-and-range/nonconclusions006.rdf",
			"wg/rdfs-domain-and-range/premises005.rdf",
			"wg/rdfs-domain-and-range/premises006.rdf",
			"wg/rdfs-domain-and-range/test001.rdf",
			"wg/rdfs-domain-and-range/test002.rdf",
			"wg/rdfs-domain-and-range/test003.rdf",
			"wg/rdfs-domain-and-range/test004.rdf",
			"wg/rdfs-no-cycles-in-subClassOf/test001.rdf",
			"wg/rdfs-no-cycles-in-subPropertyOf/test001.rdf",
			"wg/Restriction/conclusions006.rdf",
			"wg/Restriction/consistent003.rdf",
			"wg/Restriction/consistent004.rdf",
			"wg/Restriction/inconsistent001.rdf",
			"wg/Restriction/inconsistent002.rdf",
			"wg/Restriction/Manifest001.rdf", "wg/Restriction/Manifest002.rdf",
			"wg/Restriction/Manifest003.rdf", "wg/Restriction/Manifest004.rdf",
			"wg/Restriction/Manifest005.rdf", "wg/Restriction/Manifest006.rdf",
			"wg/Restriction/nonconclusions005.rdf",
			"wg/Restriction/premises005.rdf", "wg/Restriction/premises006.rdf",
			"wg/sameAs/conclusions001.rdf", "wg/sameAs/Manifest001.rdf",
			"wg/sameAs/premises001.rdf",
			"wg/someValuesFrom/conclusions001.rdf",
			"wg/someValuesFrom/conclusions003.rdf",
			"wg/someValuesFrom/Manifest001.rdf",
			"wg/someValuesFrom/Manifest002.rdf",
			"wg/someValuesFrom/Manifest003.rdf",
			"wg/someValuesFrom/nonconclusions002.rdf",
			"wg/someValuesFrom/premises001.rdf",
			"wg/someValuesFrom/premises002.rdf",
			"wg/someValuesFrom/premises003.rdf",
			"wg/SymmetricProperty/conclusions001.rdf",
			"wg/SymmetricProperty/conclusions002.rdf",
			"wg/SymmetricProperty/conclusions003.rdf",
			"wg/SymmetricProperty/Manifest001.rdf",
			"wg/SymmetricProperty/Manifest002.rdf",
			"wg/SymmetricProperty/Manifest003.rdf",
			"wg/SymmetricProperty/premises001.rdf",
			"wg/SymmetricProperty/premises002.rdf",
			"wg/SymmetricProperty/premises003.rdf", "wg/testOntology.rdf",
			"wg/Thing/consistent004.rdf", "wg/Thing/inconsistent003.rdf",
			"wg/Thing/inconsistent005.rdf", "wg/Thing/Manifest003.rdf",
			"wg/Thing/Manifest004.rdf", "wg/Thing/Manifest005.rdf",
			"wg/TransitiveProperty/conclusions001.rdf",
			"wg/TransitiveProperty/conclusions002.rdf",
			"wg/TransitiveProperty/Manifest001.rdf",
			"wg/TransitiveProperty/Manifest002.rdf",
			"wg/TransitiveProperty/premises001.rdf",
			"wg/TransitiveProperty/premises002.rdf",
			"wg/unionOf/conclusions001.rdf", "wg/unionOf/conclusions002.rdf",
			"wg/unionOf/conclusions003.rdf", "wg/unionOf/conclusions004.rdf",
			"wg/unionOf/Manifest001.rdf", "wg/unionOf/Manifest002.rdf",
			"wg/unionOf/Manifest003.rdf", "wg/unionOf/Manifest004.rdf",
			"wg/unionOf/premises001.rdf", "wg/unionOf/premises002.rdf",
			"wg/unionOf/premises003.rdf", "wg/unionOf/premises004.rdf",
			"wg/unrecognised-xml-attributes/test001.rdf",
			"wg/unrecognised-xml-attributes/test002.rdf",
			"arp/dom/domtest.rdf",
			
			//"wg/xmlbase/error001.rdf", "wg/xmlbase/test001.rdf",
			"wg/xmlbase/test002.rdf", "wg/xmlbase/test003.rdf",
			"wg/xmlbase/test004.rdf", "wg/xmlbase/test005.rdf",
			"wg/xmlbase/test006.rdf", "wg/xmlbase/test007.rdf",
			"wg/xmlbase/test008.rdf", "wg/xmlbase/test009.rdf",
			"wg/xmlbase/test010.rdf", "wg/xmlbase/test011.rdf",
			"wg/xmlbase/test012.rdf", "wg/xmlbase/test013.rdf",
			"wg/xmlbase/test014.rdf", "wg/xmlbase/test015.rdf",
			"wg/xmlbase/test016.rdf",

	};

	/**
	 * @author Jeremy J. Carroll
	 *  
	 */
	static class RDFEHArray implements RDFErrorHandler {

		Vector<String> v = new Vector<String>();

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.hp.hpl.jena.rdf.model.RDFErrorHandler#warning(java.lang.Exception)
		 */
		public void warning(Exception e) {
			s("W", e);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.hp.hpl.jena.rdf.model.RDFErrorHandler#error(java.lang.Exception)
		 */
		public void error(Exception e) {
			s("E", e);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.hp.hpl.jena.rdf.model.RDFErrorHandler.errorError(java.lang.Exception)
		 */
		public void fatalError(Exception e) {
			s("F", e);
		}

		private void s(String s, Exception e) {
			String msg = s + e.getMessage();
            if (!v.contains(msg))
                   v.add(msg);
		}

	}

	static public TestSuite suite() {
		TestSuite s = new TestSuite("SAX2RDF");
//		s.addTestSuite(PushMePullYouTest.class);
		s.addTestSuite(SAX2RDFMoreTests.class);
		
		if (is1_4_1){

            logger.warn("Java version 1.4.1: DOM tests suppressed, believed not to work." );
            logger.warn("See file:doc/ARP/sax.html#dom for more details." );
            logger.warn("This only affects RDF parsing of DOM trees, new in Jena 2.2." );
		}
		//for (int j=0; j<20; j++)
		for (int i = 0; i < all.length; i += 25) {
//			String nm = all[i];
			//if (all[i].indexOf("premises663")==-1)
			//	continue;
			if (all[i].startsWith("wg/")) {
				
				addTests(s, "wg/", ARPTests.wgTestDir.toString(),
						all[i].substring(3));
			} else if (all[i].startsWith("arp/")) {
				addTests(s,"arp/", ARPTests.arpTestDir
						.toString(), all[i].substring(4));
			} else {
				addTests(s,"", "http://example.org/", all[i]);

			}
		}
		//	s.addTest(new
		// SAX2RDFTest("wg/",ARPTests.wgTestDir.toString(),"Manifest.rdf"));

		return s;
	}
	
	static private void addTests(TestSuite s, String dir, String base, String file){


		TestCase tc = new SAX2RDFTest(dir,base,file);
		tc.setName("SAX "+tc.getName());
		s.addTest(tc);
		
		tc = new DOM2RDFTest(dir,base,file);
		
		tc.setName("DOM "+tc.getName());
		if (!is1_4_1)
    		s.addTest(tc);
		

	}

	//final private String dir;
	final String base;

	final private String file;

	SAX2RDFTest(String dir, String base0, String file) {
		super(file);
		//this.dir = dir;
		this.base = base0 + file;
		this.file = "testing/" + dir + file;
		//System.err.println(base+" + "+this.file);
	}

	@Override
    public void runTest() throws Exception {
		//System.err.println(base+" + "+this.file);
		Model m = ModelFactory.createDefaultModel();
		Model m2 = ModelFactory.createDefaultModel();
		//ANDROID: changed to use classloader
		StreamFactory factory = new StreamFactory() {

			@Override
			public InputStream createStream() throws IOException {
				return TestHelper.openResource(file);
			}
			
		};
		Reader in = TestHelper.getXMLReader(factory);
//		InputStream in = new FileInputStream(file);
		RDFEHArray eh = new RDFEHArray();
		RDFReader w = m.getReader();
		w.setErrorHandler(eh);
		w.read(m, in, base);
		in.close();
		//ANDROID: changed to use reader and classloader
		in = TestHelper.getXMLReader(factory);
//		in = new FileInputStream(file);

		RDFEHArray eh2 = new RDFEHArray();

		/*
		 * w = m.getReader(); w.setErrorHandler(eh2); w.read(m2,in,base);
		 * in.close();
		 */
		loadXMLModel(m2, in, eh2);

		in.close();

		/*
		 * System.out.println("Normal:"); m.write(System.out,"N-TRIPLE");
		 * 
		 * System.out.println("New:"); m2.write(System.out,"N-TRIPLE");
		 */
		if (eh.v.size() == 0)
			assertTrue("Not isomorphic", m.isIsomorphicWith(m2));

		if (eh.v.size() != eh2.v.size()) {
			for (int i = 0; i < eh.v.size(); i++)
				System.err.println(eh.v.get(i));
			System.err.println("---");
			for (int i = 0; i < eh2.v.size(); i++)
				System.err.println(eh2.v.get(i));

		}

		assertEquals("Different number of errors", eh.v.size(), eh2.v.size());

		Object a[] = eh.v.toArray();
		Object a2[] = eh2.v.toArray();
		Arrays.sort(a);
		Arrays.sort(a2);

		for (int i = 0; i < eh.v.size(); i++) {
			assertEquals("Error " + i + " different.", a[i], a2[i]);
		}

	}

	void loadXMLModel(Model m2, Reader in, RDFEHArray eh2) throws SAXException, IOException, ParserConfigurationException {
		//ANDROID: removed Xerces dependence
		XMLReader saxParser = new ExpatReaderWrapper();
//		XMLReader saxParser = new SAXParser();
		SAX2Model handler = SAX2Model.create(base, m2);
		SAX2RDF.installHandlers(saxParser, handler);
		handler.setErrorHandler(eh2);

		InputSource ins = new InputSource(in);
		ins.setSystemId(base);
		try {
			try {
				saxParser.parse(ins);
			} finally {
				handler.close();
			}
		} catch (SAXParseException e) {
			// already reported, leave it be.
		}

	}

}

/*
 * (c) Copyright 2004, 2005, 2006, 2007, 2008, 2009 Hewlett-Packard Development Company, LP All rights
 * reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The name of the author may not
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

