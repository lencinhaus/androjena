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

package it.polimi.dei.dbgroup.pedigree.androjena.xml;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.xerces.util.XMLChar;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLReaderFactory;


/**
 * This class wraps an Expat XMLReader (Android's default SAX reader) to provide
 * compatibility with Xerces XMLReader behavior. For example, the method
 * startElement() will provide the qName argument, which is left null by Expat
 * (which is allowed, according to SAX specifications). Jena relies heavily on
 * Xerces behavior, so it has to be "simulated" in Android for Jena to work
 * properly.
 * 
 * @author lorenzo carrara
 * 
 */
public class ExpatReaderWrapper implements XMLReader {
	private static final String FEATURE_NAMESPACES = "http://xml.org/sax/features/namespaces";
	private static final String FEATURE_NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";
	private static final String NAMESPACE_DECLARATION_PREFIX = "xmlns";
	private static final String NAMESPACE_DECLARATION_URI = "http://www.w3.org/2000/xmlns/";
	private static final String XML_PREFIX = "xml";
	private static final String XML_URI = "http://www.w3.org/XML/1998/namespace";
	private boolean namespaces = true;
	private boolean namespacePrefixes = false;
	private XMLReader expatReader = null;
	private ContentHandler contentHandler = null;
	private Locator documentLocator = null;
	private final Map<String, Mapping> prefixMappings = new HashMap<String, Mapping>();
	private final Stack<Set<String>> mappedPrefixes = new Stack<Set<String>>();
	private final ContentHandler handler = new ContentHandler() {

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			contentHandler.characters(ch, start, length);
		}

		@Override
		public void endDocument() throws SAXException {
			contentHandler.endDocument();
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			ExpandedName splitName = parseQName(qName);
			uri = splitName.uri;
			localName = splitName.localName;

			contentHandler.endElement(uri, localName, qName);

			// remove prefix mappings
			if (mappedPrefixes.isEmpty())
				throwException("unmatched element close tag " + qName);
			Set<String> mapped = mappedPrefixes.pop();
			for (String prefix : mapped) {
				Mapping mapping = prefixMappings.get(prefix);
				if (mapping == null)
					throwException("internal error");
				if (mapping.next == null)
					prefixMappings.remove(prefix);
				else
					prefixMappings.put(prefix, mapping.next);
				contentHandler.endPrefixMapping(prefix);
			}
		}

		@Override
		public void endPrefixMapping(String prefix) throws SAXException {
			throwException("Expat parser should never call this handler method");
		}

		@Override
		public void ignorableWhitespace(char[] ch, int start, int length)
				throws SAXException {
			contentHandler.ignorableWhitespace(ch, start, length);
		}

		@Override
		public void processingInstruction(String target, String data)
				throws SAXException {
			contentHandler.processingInstruction(target, data);
		}

		@Override
		public void setDocumentLocator(Locator locator) {
			documentLocator = locator;
			contentHandler.setDocumentLocator(locator);
		}

		@Override
		public void skippedEntity(String name) throws SAXException {
			contentHandler.skippedEntity(name);
		}

		@Override
		public void startDocument() throws SAXException {
			contentHandler.startDocument();
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes atts) throws SAXException {
			Set<String> mapped = new HashSet<String>();
			mappedPrefixes.push(mapped);

			AttributesImpl newAtts = new AttributesImpl();
			// parse namespace declaration attributes
			if (atts != null) {
				for (int i = 0; i < atts.getLength(); i++) {
					String attQname = atts.getQName(i);
					boolean addAtt = true;
					if (isNamespaceDeclaration(attQname)) {
						String newPrefix = "";
						String newUri = atts.getValue(i);
						if (attQname.length() > NAMESPACE_DECLARATION_PREFIX
								.length()) {
							newPrefix = attQname
									.substring(NAMESPACE_DECLARATION_PREFIX
											.length() + 1);
						}

						// xmlns prefix cannot be declared
						if (NAMESPACE_DECLARATION_PREFIX.equals(newPrefix))
							throwException(NAMESPACE_DECLARATION_PREFIX
									+ " namespace prefix cannot be declared");

						// xml cannot be bound to a different uri
						if (XML_PREFIX.equals(newPrefix)) {
							if (!XML_URI.equals(newUri))
								throwException(XML_PREFIX
										+ " namespace prefix cannot be bound to an URI different from "
										+ XML_URI);
						}
						// no prefix except for xml can be bound to xml
						// namespace
						else if (XML_URI.equals(newUri))
							throwException("only " + XML_PREFIX
									+ " namespace prefix can be bound to "
									+ XML_URI);

						// check that new prefix is an ncname
						if (newPrefix.length() > 0
								&& !XMLChar.isValidNCName(newPrefix))
							throwException("namespace prefix " + newPrefix
									+ " is not an NCName");

						if (mapped.contains(newPrefix))
							throwException("namespace prefix " + newPrefix
									+ " declared twice in the same element");
						Mapping mapping = new Mapping(newUri, prefixMappings
								.get(newPrefix));
						prefixMappings.put(newPrefix, mapping);
						mapped.add(newPrefix);
						contentHandler.startPrefixMapping(newPrefix, newUri);
						addAtt = namespacePrefixes;
					}
					if (addAtt)
						newAtts.addAttribute("", "", attQname, atts.getType(i),
								atts.getValue(i));
				}
			}

			// set uris on attributes
			for (int i = 0; i < newAtts.getLength(); i++) {
				String attQname = newAtts.getQName(i);
				ExpandedName splitName = parseQName(attQname);
				newAtts.setLocalName(i, splitName.localName);
				newAtts.setURI(i, splitName.uri);
			}

			ExpandedName splitName = parseQName(qName);
			localName = splitName.localName;
			uri = splitName.uri;

			contentHandler.startElement(uri, localName, qName, newAtts);
		}

		@Override
		public void startPrefixMapping(String prefix, String uri)
				throws SAXException {
			throwException("Expat parser should never call this handler method");
		}
	};

	private static class Mapping {
		public String uri;
		public Mapping next;

		public Mapping(String uri, Mapping next) {
			this.uri = uri;
			this.next = next;
		}
	}

	private static class ExpandedName {
		public String uri;
		public String localName;

		public ExpandedName(String uri, String localName) {
			this.uri = uri;
			this.localName = localName;
		}
	}
	
	public ExpatReaderWrapper() throws SAXException {
		// initialize with an ExpatReader
		XMLReader reader = null;
		try {
			reader = XMLReaderFactory.createXMLReader("org.apache.harmony.xml.ExpatReader");
		}
		catch(SAXException ex) {
			// expat reader is not available
			// fall back to the default XML reader
			try {
				reader = XMLReaderFactory.createXMLReader();
			}
			catch(SAXException ex2) {
				// no default XML reader is set
				// fall back to xmlpull sax2 driver
				reader = XMLReaderFactory.createXMLReader("org.xmlpull.v1.sax2.Driver");
			}
		}
		
		init(reader);
	}

	public ExpatReaderWrapper(XMLReader expatReader) throws SAXException {
		init(expatReader);
	}
	
	private void init(XMLReader expatReader) throws SAXException {
		if (expatReader == null)
			throw new NullPointerException("expatReader cannot be null");
		this.expatReader = expatReader;
		expatReader.setContentHandler(handler);
		expatReader.setFeature(FEATURE_NAMESPACE_PREFIXES, true);
		expatReader.setFeature(FEATURE_NAMESPACES, false);
	}

	@Override
	public ContentHandler getContentHandler() {
		return contentHandler;
	}

	@Override
	public DTDHandler getDTDHandler() {
		return expatReader.getDTDHandler();
	}

	@Override
	public EntityResolver getEntityResolver() {
		return expatReader.getEntityResolver();
	}

	@Override
	public ErrorHandler getErrorHandler() {
		return expatReader.getErrorHandler();
	}

	@Override
	public boolean getFeature(String name) throws SAXNotRecognizedException,
			SAXNotSupportedException {
		if (FEATURE_NAMESPACES.equals(name))
			return namespaces;
		else if (FEATURE_NAMESPACE_PREFIXES.equals(name))
			return namespacePrefixes;
		else
			return expatReader.getFeature(name);
	}

	@Override
	public Object getProperty(String name) throws SAXNotRecognizedException,
			SAXNotSupportedException {
		return expatReader.getProperty(name);
	}

	@Override
	public void parse(InputSource input) throws IOException, SAXException {
		try {
			addBaseMappings();
			expatReader.parse(input);
		} finally {
			clearMappings();
		}
	}

	@Override
	public void parse(String systemId) throws IOException, SAXException {
		try {
			addBaseMappings();
			expatReader.parse(systemId);
		} finally {
			clearMappings();
		}
	}

	@Override
	public void setContentHandler(ContentHandler handler) {
		contentHandler = handler;
	}

	@Override
	public void setDTDHandler(DTDHandler handler) {
		expatReader.setDTDHandler(handler);
	}

	@Override
	public void setEntityResolver(EntityResolver resolver) {
		expatReader.setEntityResolver(resolver);
	}

	@Override
	public void setErrorHandler(ErrorHandler handler) {
		expatReader.setErrorHandler(handler);
	}

	@Override
	public void setFeature(String name, boolean value)
			throws SAXNotRecognizedException, SAXNotSupportedException {
		if (FEATURE_NAMESPACES.equals(name))
			namespaces = value;
		else if (FEATURE_NAMESPACE_PREFIXES.equals(name))
			namespacePrefixes = value;
		else
			expatReader.setFeature(name, value);
	}

	@Override
	public void setProperty(String name, Object value)
			throws SAXNotRecognizedException, SAXNotSupportedException {
		expatReader.setProperty(name, value);
	}

	private final void clearMappings() {
		mappedPrefixes.clear();
		prefixMappings.clear();
	}

	private final void addBaseMappings() {
		prefixMappings.put(NAMESPACE_DECLARATION_PREFIX, new Mapping(
				NAMESPACE_DECLARATION_URI, null));
		prefixMappings.put(XML_PREFIX, new Mapping(XML_URI, null));
	}

	private final ExpandedName parseQName(String qName) throws SAXException {
		String prefix = "";
		String uri = "";
		String localName = qName;
		int commaPos = qName.indexOf(':');
		if (commaPos > 0 && commaPos < qName.length() - 1) {
			prefix = qName.substring(0, commaPos);
			if (!NAMESPACE_DECLARATION_PREFIX.equals(prefix)
					&& !prefixMappings.containsKey(prefix))
				throwException("undeclared namespace prefix " + prefix);
			localName = qName.substring(commaPos + 1);
		}
		// check that localName is an NCName
		if (localName.length() > 0 && !XMLChar.isValidNCName(localName))
			throwException(localName + " local name is not a valid NCName");
		Mapping mapping = prefixMappings.get(prefix);
		if (mapping != null)
			uri = mapping.uri;
		return new ExpandedName(uri, localName);
	}

	private static final boolean isNamespaceDeclaration(String qName) {
		if (qName.startsWith(NAMESPACE_DECLARATION_PREFIX)) {
			if (qName.length() == NAMESPACE_DECLARATION_PREFIX.length())
				return true;
			else if (qName.charAt(NAMESPACE_DECLARATION_PREFIX.length()) == ':') {
				if (qName.length() > NAMESPACE_DECLARATION_PREFIX.length() + 1)
					return true;
			}
		}
		return false;
	}

	private final void throwException(String message) throws SAXException {
		if (documentLocator != null) {
			message = "At line " + documentLocator.getLineNumber()
					+ ", column " + documentLocator.getColumnNumber() + ": "
					+ message;
		}
		throw new SAXException(message);
	}
}
