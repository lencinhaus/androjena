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

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import android.text.TextUtils;

// namespace-aware DOM transformer (to SAX handler)
public class DOMTransformer {
	private DOMTransformer() {

	}

	private static class PrefixMapping {
		public String prefix;
		public String namespaceURI;
	}

	private static class TransformEvent {
		public static final int EVENT_NODE = 0;
		public static final int EVENT_END_PREFIX_MAPPING = 1;
		public static final int EVENT_END_ELEMENT = 2;
		public static final int EVENT_END_DOCUMENT = 3;

		public int type;
		public Node node;

		public TransformEvent(int type, Node node) {
			this.type = type;
			this.node = node;
		}
	}

	private static void handleNamespace(Node node,
			Stack<TransformEvent> events, Stack<PrefixMapping> mappings,
			DefaultHandler handler) throws SAXException {
		if (!TextUtils.isEmpty(node.getNamespaceURI())) {
			String prefix = TextUtils.isEmpty(node.getPrefix()) ? "" : node
					.getPrefix();
			PrefixMapping mapping = null;
			for (int i = mappings.size() - 1; i >= 0; i--) {
				if (mappings.get(i).prefix.equals(prefix)) {
					mapping = mappings.get(i);
					break;
				}
			}

			if (mapping != null) {
				if (!node.getNamespaceURI().equals(mapping.namespaceURI))
					mapping = null;
			}

			if (mapping == null) {
				mapping = new PrefixMapping();
				mapping.namespaceURI = node.getNamespaceURI();
				mapping.prefix = prefix;
				mappings.push(mapping);
				events.push(new TransformEvent(
						TransformEvent.EVENT_END_PREFIX_MAPPING, null));
				handler.startPrefixMapping(prefix, node.getNamespaceURI());
			}
		}
	}

	public static void transform(Node first, DefaultHandler handler)
			throws SAXException {
		transform(first, handler, null);
	}

	public static void transform(Node first, DefaultHandler handler,
			LexicalHandler lexicalHandler) throws SAXException {
		if (first == null)
			throw new NullPointerException("first cannot be null");
		if (handler == null)
			throw new NullPointerException("handler cannot be null");

		Stack<PrefixMapping> mappings = new Stack<PrefixMapping>();
		Stack<TransformEvent> events = new Stack<TransformEvent>();
		Map<String, char[]> resolvedEntities = new HashMap<String, char[]>();
		Set<String> unresolvedEntities = new HashSet<String>();

		events.push(new TransformEvent(TransformEvent.EVENT_NODE, first));
		while (!events.isEmpty()) {
			TransformEvent event = events.pop();

			switch (event.type) {
				case TransformEvent.EVENT_NODE:
					boolean parseChildren = false;
					switch (event.node.getNodeType()) {
						case Node.DOCUMENT_NODE:
							events.push(new TransformEvent(
									TransformEvent.EVENT_END_DOCUMENT, null));
							handler.startDocument();
							parseChildren = true;
							break;
						case Node.ELEMENT_NODE:
							Element el = (Element) event.node;
							handleNamespace(el, events, mappings, handler);

							NamedNodeMap attrs = el.getAttributes();
							AttributesImpl saxAttrs = new AttributesImpl();
							for (int i = 0; i < attrs.getLength(); i++) {
								Attr attr = (Attr) attrs.item(i);
								handleNamespace(attr, events, mappings, handler);
								saxAttrs.addAttribute(TextUtils.isEmpty(attr
										.getNamespaceURI()) ? "" : attr
										.getNamespaceURI(), TextUtils
										.isEmpty(attr.getLocalName()) ? ""
										: attr.getLocalName(), TextUtils
										.isEmpty(attr.getName()) ? "" : attr
										.getName(), "CDATA", attr.getValue());
							}

							events.push(new TransformEvent(
									TransformEvent.EVENT_END_ELEMENT, el));

							handler.startElement(TextUtils.isEmpty(el
									.getNamespaceURI()) ? "" : el
									.getNamespaceURI(), TextUtils.isEmpty(el
									.getLocalName()) ? "" : el.getLocalName(),
									TextUtils.isEmpty(el.getTagName()) ? ""
											: el.getTagName(), saxAttrs);

							parseChildren = true;
							break;
						case Node.TEXT_NODE:
							Text text = (Text) event.node;
							char[] textData = text.getData().toCharArray();
							handler.characters(textData, 0, textData.length);
							break;
						case Node.ENTITY_REFERENCE_NODE:
							// TODO finish
							EntityReference reference = (EntityReference) event.node;
							char[] entityData = null;
							if (!unresolvedEntities.contains(reference
									.getNodeName())) {
								if (!resolvedEntities.containsKey(reference
										.getNodeName())) {
									// try to resolve the entity
									Entity ent = null;
									Exception resolveError = null;
									try {
										if (reference.getOwnerDocument() != null
												&& reference.getOwnerDocument()
														.getDoctype() != null) {
											DocumentType doctype = reference
													.getOwnerDocument()
													.getDoctype();
											if (doctype.getEntities() != null) {
												ent = (Entity) doctype
														.getEntities()
														.getNamedItem(
																reference
																		.getNodeName());
												if (ent != null) {
													InputSource source = handler
															.resolveEntity(
																	ent
																			.getPublicId(),
																	ent
																			.getSystemId());
													if (source != null) {
														BufferedReader reader = new BufferedReader(
																source
																		.getCharacterStream());
														StringBuilder sb = new StringBuilder();
														String s = null;
														while ((s = reader
																.readLine()) != null) {
															sb.append(s);
															sb.append("\n");
														}
														reader.close();
														entityData = sb
																.toString()
																.toCharArray();
														resolvedEntities
																.put(
																		reference
																				.getNodeName(),
																		entityData);
													}

												}
											}
										}
									} catch (Exception ex) {
										resolveError = ex;
									}

									if (entityData == null) {
										StringBuilder sb = new StringBuilder();
										sb.append("cannot resolve entity ");
										sb.append(reference.getNodeName());
										if (ent != null) {
											if (!TextUtils.isEmpty(ent
													.getPublicId())) {
												sb.append(", publicId: ");
												sb.append(ent.getPublicId());
											}
											if (!TextUtils.isEmpty(ent
													.getSystemId())) {
												sb.append(", systemId: ");
												sb.append(ent.getSystemId());
											}
										}
										SAXParseException ex = null;
										if (resolveError != null)
											ex = new SAXParseException(sb
													.toString(), null,
													resolveError);
										else
											ex = new SAXParseException(sb
													.toString(), null);
										handler.error(ex);
										unresolvedEntities.add(reference
												.getNodeName());
									}
								} else
									entityData = resolvedEntities.get(reference
											.getNodeName());
							}

							if (entityData != null) {
								if (lexicalHandler != null)
									lexicalHandler.startEntity(reference
											.getNodeName());
								handler.characters(entityData, 0,
										entityData.length);
								if (lexicalHandler != null)
									lexicalHandler.endEntity(reference
											.getNodeName());
							} else
								handler.skippedEntity(reference.getNodeName());
							break;
						case Node.COMMENT_NODE:
							if (lexicalHandler != null) {
								Comment comment = (Comment) event.node;
								char[] commentData = comment.getData()
										.toCharArray();
								lexicalHandler.comment(commentData, 0,
										commentData.length);
							}
							break;
						case Node.CDATA_SECTION_NODE:
							CDATASection cdata = (CDATASection) event.node;
							char[] cdataData = cdata.getData().toCharArray();

							if (lexicalHandler != null)
								lexicalHandler.startCDATA();
							handler.characters(cdataData, 0, cdataData.length);
							if (lexicalHandler != null)
								lexicalHandler.endCDATA();
							break;
						case Node.PROCESSING_INSTRUCTION_NODE:
							ProcessingInstruction pi = (ProcessingInstruction) event.node;
							handler.processingInstruction(pi.getTarget(), pi
									.getData());
							break;
						case Node.DOCUMENT_TYPE_NODE:
							DocumentType dt = (DocumentType) event.node;
							if (lexicalHandler != null) {
								lexicalHandler.startDTD(dt.getName(), dt
										.getPublicId(), dt.getSystemId());
							}
							if (dt.getNotations() != null) {
								NamedNodeMap notations = dt.getNotations();
								for (int i = 0; i < notations.getLength(); i++) {
									Notation notation = (Notation) notations
											.item(i);
									handler.notationDecl(
											notation.getNodeName(), notation
													.getPublicId(), notation
													.getSystemId());
								}
							}
							if (dt.getEntities() != null) {
								// we just check for unparsed external entities
								// (which must be notified to
								// the handler); references to other entities
								// will be already expanded or referenced
								// via EntityReference in the document tree.
								NamedNodeMap entities = dt.getEntities();
								for (int i = 0; i < entities.getLength(); i++) {
									Entity entity = (Entity) entities.item(i);
									if (!TextUtils.isEmpty(entity
											.getNotationName())) {
										// unparsed external entity
										handler.unparsedEntityDecl(entity
												.getNodeName(), entity
												.getPublicId(), entity
												.getSystemId(), entity
												.getNotationName());
									}
								}
							}
							if (lexicalHandler != null) {
								lexicalHandler.endDTD();
							}
							break;
					}

					if (parseChildren) {
						NodeList children = event.node.getChildNodes();
						for (int i = children.getLength() - 1; i >= 0; i--) {
							events
									.push(new TransformEvent(
											TransformEvent.EVENT_NODE, children
													.item(i)));
						}
					}
					break;
				case TransformEvent.EVENT_END_PREFIX_MAPPING:
					PrefixMapping mapping = mappings.pop();
					handler.endPrefixMapping(mapping.prefix);
					break;
				case TransformEvent.EVENT_END_ELEMENT:
					Element el = (Element) event.node;
					handler.endElement(el.getNamespaceURI(), el.getLocalName(),
							el.getTagName());
					break;
				case TransformEvent.EVENT_END_DOCUMENT:
					handler.endDocument();
					break;
			}
		}
	}
}
