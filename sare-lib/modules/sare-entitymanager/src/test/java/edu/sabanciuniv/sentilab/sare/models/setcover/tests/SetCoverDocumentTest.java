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

package edu.sabanciuniv.sentilab.sare.models.setcover.tests;

import static org.junit.Assert.*;

import org.junit.*;

import edu.sabanciuniv.sentilab.sare.models.base.document.*;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;
import edu.sabanciuniv.sentilab.sare.models.setcover.*;
import edu.sabanciuniv.sentilab.sare.tests.PersistenceTestsBase;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.PosTag;

public class SetCoverDocumentTest
	extends PersistenceTestsBase {

	private OpinionCorpus testCorpus;
	private OpinionDocument testDocument;
	
	private DocumentSetCover testSetCover;
	private SetCoverDocument testSetCoverDocument;
	
	private TokenizingOptions testTokenizingOptions;
	
	@Before
	public void setUp() throws Exception {
		testTokenizingOptions = new TokenizingOptions()
			.setLemmatized(true)
			.setTags(PosTag.NOUN);
			
		testCorpus = (OpinionCorpus)new OpinionCorpus()
			.setLanguage("en")
			.setTitle("test corpus");
		testDocument = (OpinionDocument)new OpinionDocument()
			.setContent("this is a test document")
			.setTokenizingOptions(testTokenizingOptions)
			.setStore(testCorpus);
		testCorpus.addDocument(testDocument);
		
		em.getTransaction().begin();
		em.persist(testCorpus);
		em.persist(testDocument);
		em.getTransaction().commit();
		
		testSetCover = new DocumentSetCover(testCorpus);
		testSetCoverDocument = (SetCoverDocument)new SetCoverDocument(testDocument)
			.setTokenizingOptions(testTokenizingOptions)
			.setStore(testSetCover);
	}

	@After
	public void tearDown() throws Exception {
		//
	}

	@Test
	public void testContent() {
		assertEquals(testDocument.getContent(), testSetCoverDocument.getContent());
	}

	@Test
	public void testWeight() {
		em.getTransaction().begin();
		em.persist(testSetCover);
		em.persist(testSetCoverDocument);
		em.getTransaction().commit();
		
		assertEquals(testSetCoverDocument.getTotalTokenWeight(), testSetCoverDocument.getWeight(), 0);
		
		em.clear();
		SetCoverDocument actualSetCoverDocument = em.find(SetCoverDocument.class, testSetCoverDocument.getId());
		
		assertNotNull(actualSetCoverDocument);
		assertEquals(testSetCoverDocument.getTotalTokenWeight(), actualSetCoverDocument.getWeight(), 0);
	}

	@Test
	public void testResetWeight() {
		OpinionDocument anotherDocument = (OpinionDocument)new OpinionDocument()
			.setContent("this is another test document")
			.setTokenizingOptions(testTokenizingOptions)
			.setStore(testCorpus);
		
		testSetCoverDocument.merge(anotherDocument);
		
		assertFalse(testSetCoverDocument.getWeight() == testDocument.getTotalTokenWeight());
		
		em.getTransaction().begin();
		em.persist(testSetCover);
		em.persist(testSetCoverDocument);
		em.getTransaction().commit();
		
		em.clear();
		SetCoverDocument actualSetCoverDocument = em.find(SetCoverDocument.class, testSetCoverDocument.getId());
		
		assertNotNull(actualSetCoverDocument);
		
		actualSetCoverDocument
			.setTokenizingOptions(testTokenizingOptions)
			.retokenize();
		
		actualSetCoverDocument.resetWeight();
		assertEquals(testDocument.getTotalTokenWeight(), actualSetCoverDocument.getWeight(), 0);
	}
	
	@Test
	public void testEnrichedContentChangesWithBase() {
		em.getTransaction().begin();
		em.persist(testSetCover);
		em.persist(testSetCoverDocument);
		em.getTransaction().commit();
		em.refresh(testDocument);
		
		testDocument.setContent("this is a modified test document.");
		em.getTransaction().begin();
		em.merge(testDocument);
		em.getTransaction().commit();
		em.clear();
		
		SetCoverDocument actualSetCoverDocument = em.find(SetCoverDocument.class, testSetCoverDocument.getId());
		assertEquals(((FullTextDocument)actualSetCoverDocument.getBaseDocument()).getParsedContent().toString(true), actualSetCoverDocument.getContent(true));
	}
}