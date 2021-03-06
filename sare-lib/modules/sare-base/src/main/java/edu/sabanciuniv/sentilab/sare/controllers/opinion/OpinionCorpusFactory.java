/*
 * Sentilab SARE: a Sentiment Analysis Research Environment
 * Copyright (C) 2013 Sabanci University Sentilab
 * http://sentilab.sabanciuniv.edu
 * 
 * This file is part of SARE.
 * 
 * SARE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * SARE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SARE. If not, see <http://www.gnu.org/licenses/>.
 */

package edu.sabanciuniv.sentilab.sare.controllers.opinion;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.apache.commons.lang3.*;
import org.apache.commons.lang3.text.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import edu.sabanciuniv.sentilab.sare.controllers.base.documentStore.NonDerivedStoreFactory;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;
import edu.sabanciuniv.sentilab.utils.CannedMessages;

/**
 * A factory for creating {@link OpinionCorpus} objects.
 * @author Mus'ab Husaini
 */
public final class OpinionCorpusFactory
		extends NonDerivedStoreFactory<OpinionCorpus> {

	@Override
	protected OpinionCorpusFactory addXmlPacket(OpinionCorpus corpus, InputStream input)
		throws ParserConfigurationException, SAXException, IOException, XPathException {
		
		Validate.notNull(corpus, CannedMessages.NULL_ARGUMENT, "corpus");
		Validate.notNull(input, CannedMessages.NULL_ARGUMENT, "input");
		
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
	    domFactory.setNamespaceAware(true);
	    Document doc = domFactory.newDocumentBuilder().parse(input);

	    XPathFactory factory = XPathFactory.newInstance();
	    XPath xpath = factory.newXPath();
	    
	    OpinionDocumentFactory opinionFactory;
	    
	    if ("document".equals(doc.getDocumentElement().getLocalName())) {
	    	opinionFactory = new OpinionDocumentFactory()
	    		.setCorpus(corpus).setXmlNode(doc.getDocumentElement());
	    	corpus.addDocument(opinionFactory.create());
	    	return this;
	    }
	    
	    Node corpusNode = (Node)xpath.compile("/corpus").evaluate(doc, XPathConstants.NODE);
	    if (corpusNode == null) {
	    	corpusNode = Validate.notNull(doc.getDocumentElement(), CannedMessages.NULL_ARGUMENT, "/corpus");
	    }
	    
	    String title = (String)xpath.compile("./@title").evaluate(corpusNode, XPathConstants.STRING);
	    String description = (String)xpath.compile("./@description").evaluate(corpusNode, XPathConstants.STRING);
	    String language = (String)xpath.compile("./@language").evaluate(corpusNode, XPathConstants.STRING);
	    
	    if (StringUtils.isNotEmpty(title)) {
	    	corpus.setTitle(title);
	    }
	    
	    if (StringUtils.isNotEmpty(description)) {
	    	corpus.setDescription(description);
	    }
	    
	    if (StringUtils.isNotEmpty(language)) {
	    	corpus.setLanguage(language);
	    }
	    
	    NodeList documentNodes = (NodeList)xpath.compile("./document").evaluate(corpusNode, XPathConstants.NODESET);
	    if (documentNodes == null || documentNodes.getLength() == 0) {
	    	documentNodes = corpusNode.getChildNodes();
	    	Validate.isTrue(documentNodes != null && documentNodes.getLength() > 0, CannedMessages.NULL_ARGUMENT, "/corpus/document");
	    }
	    
	    for (int index=0; index<documentNodes.getLength(); index++) {
	    	opinionFactory = new OpinionDocumentFactory().setCorpus(corpus).setXmlNode(documentNodes.item(index));
	    	corpus.addDocument(opinionFactory.create());
	    }
		
		return this;
	}
	
	@Override
	protected OpinionCorpusFactory addTextPacket(OpinionCorpus corpus, InputStream input, String delimiter)
		throws IOException {
		
		Validate.notNull(corpus, CannedMessages.NULL_ARGUMENT, "corpus");
		Validate.notNull(input, CannedMessages.NULL_ARGUMENT, "input");
		
		OpinionDocumentFactory opinionFactory = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		String line;
		
		while ((line = reader.readLine()) != null) {
			StrTokenizer tokenizer = new StrTokenizer(line, StrMatcher.stringMatcher(delimiter), StrMatcher.quoteMatcher());
			List<String> columns = tokenizer.getTokenList();
			if (columns.size() < 1) {
				continue;
			}
			
			opinionFactory = new OpinionDocumentFactory()
				.setCorpus(corpus)
				.setContent(columns.get(0));
			
			if (columns.size() > 1) {
				try {
					opinionFactory.setPolarity(Double.parseDouble(columns.get(1)));
				} catch (NumberFormatException e) {
					opinionFactory.setPolarity(null);
				}
			}
			
			corpus.addDocument(opinionFactory.create());
		}
		
		return this;
	}

	@Override
	protected OpinionCorpus createNew() {
		return new OpinionCorpus();
	}
}