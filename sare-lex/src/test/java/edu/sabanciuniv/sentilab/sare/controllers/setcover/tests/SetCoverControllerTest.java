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

package edu.sabanciuniv.sentilab.sare.controllers.setcover.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.util.*;

import org.junit.*;

import com.google.common.collect.Iterables;

import edu.sabanciuniv.sentilab.core.models.factory.IllegalFactoryOptionsException;
import edu.sabanciuniv.sentilab.sare.controllers.opinion.OpinionCorpusFactory;
import edu.sabanciuniv.sentilab.sare.controllers.setcover.SetCoverController;
import edu.sabanciuniv.sentilab.sare.models.base.document.*;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;
import edu.sabanciuniv.sentilab.sare.models.setcover.*;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.PosTag;

public class SetCoverControllerTest {

	private String testXmlCorpusFilename;
	private OpinionCorpus testCorpus;
	private TokenizingOptions testTokenizingOptions;
	private SetCoverController testController;
	
	@Before
	public void setUp() throws Exception {
		testXmlCorpusFilename = "/test-small-corpus.xml";
		
		OpinionCorpusFactory factory = new OpinionCorpusFactory();
		testCorpus = factory.create((OpinionCorpusFactoryOptions)new OpinionCorpusFactoryOptions()
			.setFile(new File(getClass().getResource(testXmlCorpusFilename).getPath())));
		
		testTokenizingOptions = new TokenizingOptions()
			.setLemmatized(true)
			.setTags(PosTag.NOUN);
		
		testController = new SetCoverController();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testClearClears() {
		DocumentSetCover setCover;
		try {
			setCover = testController.create(new SetCoverFactoryOptions()
				.setStore(testCorpus).setTokenizingOptions(testTokenizingOptions));
		} catch (IllegalFactoryOptionsException e) {
			fail("could not create set cover");
			return;
		}
		
		assertTrue(Iterables.size(setCover.getDocuments()) > 0);
		testController.clear(setCover);
		assertFalse(Iterables.size(setCover.getDocuments()) > 0);
		assertNull(setCover.getTokenizingOptions());
		assertNull(setCover.getWeightCoverage());
	}
	
	@Test
	public void testCreateWithTokenizingOptions() {
		DocumentSetCover setCover;
		try {
			setCover = testController.create(new SetCoverFactoryOptions()
				.setStore(testCorpus).setTokenizingOptions(testTokenizingOptions));
		} catch (IllegalFactoryOptionsException e) {
			fail("could not create set cover");
			return;
		}
		
		assertNotNull(setCover);
		assertEquals(8, Iterables.size(setCover.getDocuments()));
		
		int index=0;
		SetCoverDocument firstDoc = Iterables.get(setCover.wrapGeneric(SetCoverDocument.class).getDocuments(), index);
		assertNotNull(firstDoc);
		assertNotNull(setCover.getTokenizingOptions());
		assertTrue(Iterables.size(setCover.getTokenizingOptions().getTags()) > 0);
		assertEquals(94.0, firstDoc.getWeight(), 0);
		
		for (SetCoverDocument doc : setCover.wrapGeneric(SetCoverDocument.class).getDocuments()) {
			assertFalse(doc.getContent().equals("This hotel was great; I loved the bathroom!"));
		}
	}
	
	@Test
	public void testCreateWithWeightRatio() {
		double weightCoverage = 0.8;
		DocumentSetCover setCover;
		try {
			setCover = testController.create(new SetCoverFactoryOptions()
				.setStore(testCorpus)
				.setTokenizingOptions(testTokenizingOptions)
				.setWeightCoverage(weightCoverage));
		} catch (IllegalFactoryOptionsException e) {
			fail("could not create set cover");
			return;
		}
		
		assertNotNull(setCover);
		assertEquals(5, Iterables.size(setCover.getDocuments()));
		assertEquals(weightCoverage, setCover.getWeightCoverage(), 0);
		
		int index=0;
		SetCoverDocument firstDoc = Iterables.get(setCover.wrapGeneric(SetCoverDocument.class).getDocuments(), index);
		assertNotNull(firstDoc);
		
		double firstWeight = 94.0;
		assertEquals(firstWeight, firstDoc.getWeight(), 0);
		
		for (SetCoverDocument doc : setCover.wrapGeneric(SetCoverDocument.class).getDocuments()) {
			assertFalse(doc.getContent().equals("This hotel was great; I loved the bathroom!"));
		}
	}
	
	@Test
	public void testCalculateCoverageMatrix() {
		DocumentSetCover setCover;
		try {
			setCover = testController.create(new SetCoverFactoryOptions()
				.setStore(testCorpus).setTokenizingOptions(testTokenizingOptions));
		} catch (IllegalFactoryOptionsException e) {
			fail("could not create set cover");
			return;
		}
		
		assertNotNull(setCover);
		
		Map<Integer, Double> matrix = testController.calculateCoverageMatrix(setCover, 10);
		
		assertNotNull(matrix);
		assertEquals(11, matrix.size());
		assertNotNull(matrix.get(50));
		assertEquals(0.2, matrix.get(50), 0.0005);
	}
}