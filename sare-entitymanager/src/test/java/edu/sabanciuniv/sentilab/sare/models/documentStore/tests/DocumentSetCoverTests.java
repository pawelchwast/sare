package edu.sabanciuniv.sentilab.sare.models.documentStore.tests;

import static org.junit.Assert.*;

import java.util.EnumSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Iterables;

import edu.sabanciuniv.sentilab.sare.models.base.ModelTestsBase;
import edu.sabanciuniv.sentilab.sare.models.document.OpinionDocument;
import edu.sabanciuniv.sentilab.sare.models.document.SetCoverDocument;
import edu.sabanciuniv.sentilab.sare.models.document.base.TokenizingOptions;
import edu.sabanciuniv.sentilab.sare.models.document.base.TokenizingOptions.TagCaptureOptions;
import edu.sabanciuniv.sentilab.sare.models.documentStore.DocumentSetCover;
import edu.sabanciuniv.sentilab.sare.models.documentStore.OpinionCorpus;

public class DocumentSetCoverTests extends ModelTestsBase {

	private OpinionCorpus testCorpus;
	private OpinionDocument testDocument;
	
	private DocumentSetCover testSetCover;
	private SetCoverDocument testSetCoverDocument;
	
	private TokenizingOptions testTokenizingOptions;

	@Before
	public void setUp() throws Exception {
		em = emFactory.createEntityManager();
		
		testTokenizingOptions = new TokenizingOptions()
			.setLemmatized(true)
			.setTags(EnumSet.of(TagCaptureOptions.STARTS_WITH, TagCaptureOptions.IGNORE_CASE), "nn");
			
		testCorpus = (OpinionCorpus)new OpinionCorpus()
			.setLanguage("en")
			.setTitle("test corpus")
			.setDescription("this is a test corpus");
		testDocument = (OpinionDocument)new OpinionDocument()
			.setContent("this is a test document")
			.setStore(testCorpus)
			.setTokenizingOptions(testTokenizingOptions);
		testCorpus.addDocument(testDocument);
		
		em.getTransaction().begin();
		em.persist(testCorpus);
		em.persist(testDocument);
		em.getTransaction().commit();
		
		testSetCover = new DocumentSetCover(testCorpus);
		testSetCoverDocument = (SetCoverDocument)new SetCoverDocument(testDocument)
			.setStore(testSetCover)
			.setTokenizingOptions(testTokenizingOptions);
		testSetCover.addDocument(testSetCoverDocument);

	}

	@After
	public void tearDown() throws Exception {
		if (em.getTransaction().isActive()) {
			em.getTransaction().rollback();
		}
		
		em.getTransaction().begin();
		
		if (em.contains(testCorpus)) {
			em.remove(testCorpus);
		}
		
		if (em.contains(testSetCover)) {
			em.remove(testSetCover);
		}
		
		em.getTransaction().commit();
		
		em.close();
	}

	@Test
	public void testTitle() {
		assertEquals(testCorpus.getTitle(), testSetCover.getTitle());
		
		testSetCover.setTitle("test set cover");
		
		assertFalse(testCorpus.getTitle().equals(testSetCover.getTitle()));
	}

	@Test
	public void testLanguage() {
		assertEquals(testCorpus.getLanguage(), testSetCover.getLanguage());
		
		testSetCover.setLanguage("tr");
		
		assertFalse(testCorpus.getLanguage().equals(testSetCover.getLanguage()));
	}

	@Test
	public void testDescription() {
		assertEquals(testCorpus.getDescription(), testSetCover.getDescription());
		
		testSetCover.setDescription("this is a set cover");
		
		assertFalse(testCorpus.getDescription().equals(testSetCover.getDescription()));
	}

	@Test
	public void testReplaceDocuments() {
		SetCoverDocument anotherSetCoverDocument = new SetCoverDocument();
		
		boolean replaced = testSetCover.replaceDocuments(testSetCoverDocument, anotherSetCoverDocument);
		
		assertTrue(replaced);
		assertFalse(Iterables.contains(testSetCover.getDocuments(), testSetCoverDocument));
		assertTrue(Iterables.contains(testSetCover.getDocuments(), anotherSetCoverDocument));
	}
}