package edu.sabanciuniv.sentilab.sare.models.documentStore.base;

import java.util.*;

import javax.persistence.*;

import com.google.common.collect.Lists;

import edu.sabanciuniv.sentilab.sare.models.document.base.PersistentDocument;
import edu.sabanciuniv.sentilab.sare.models.base.*;

/**
 * The base class for all objects that can store documents.
 * @author Mus'ab Husaini
 */
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
public abstract class DocumentStoreBase
	extends PersistentObject
	implements IDocumentStore {

	/**
	 * 
	 */
	private static final long serialVersionUID = 24809167105583854L;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "base_store_id")
	protected DocumentStoreBase baseStore;
	
	@OneToMany(mappedBy = "baseStore")
	protected Collection<DocumentStoreBase> derivedStores;
	
	@Column
	protected String title;
	
	@Column
	protected String language;
	
	@Column
	protected String description;
	
	@OneToMany(mappedBy = "store")
	protected List<PersistentDocument> documents;

	/**
	 * Gets the base store for this store.
	 * @return the {@link DocumentStoreBase} object that is the base store for this store.
	 */
	public DocumentStoreBase getBaseStore() {
		return baseStore;
	}

	/**
	 * Sets the base store for this store.
	 * @param baseStore the base store.
	 * @return the {@code this} object.
	 */
	public DocumentStoreBase setBaseStore(DocumentStoreBase baseStore) {
		this.baseStore = baseStore;
		this.addReference(baseStore);
		
		if (baseStore != null && !baseStore.getDerivedStores().contains(this)) {
			baseStore.getDerivedStores().add(this);
		}
		
		return this;
	}

	/**
	 * Gets the derived stores of this store.
	 * @return the {@link Collection} of {@link DocumentStoreBase} objects that derive from this store.
	 */
	public Collection<DocumentStoreBase> getDerivedStores() {
		if (this.derivedStores == null) {
			this.derivedStores = Lists.newArrayList();
		}
		
		return this.derivedStores;
	}

	/**
	 * Sets the derived store of this store.
	 * @param derivedStores the {@link Collection} of {@link DocumentStoreBase} objects to set as the derived stores of this store.
	 * @return the {@code this} object.
	 */
	public DocumentStoreBase setDerivedStores(Collection<DocumentStoreBase> derivedStores) {
		this.derivedStores = derivedStores;
		
		if (this.derivedStores != null) {
			for (DocumentStoreBase derivedStore : this.derivedStores) {
				if (derivedStore.getBaseStore() != this) {
					derivedStore.setBaseStore(this);
				}
			}
		}
		
		return this;
	}

	@Override
	public String getTitle() {
		return this.title;
	}
	
	/**
	 * Sets the title of this store.
	 * @param title the title of this store.
	 * @return the {@code this} object.
	 */
	public DocumentStoreBase setTitle(String title) {
		this.title = title;
		return this;
	}
	
	@Override
	public String getLanguage() {
		return this.language == null ? "en"	: this.language;
	}

	/**
	 * Sets the language that this store's documents are in.
	 * @param language the language of this store.
	 * @return the {@code this} object.
	 */
	public DocumentStoreBase setLanguage(String language) {
		this.language = language;
		return this;
	}

	@Override
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * Sets the description of this store.
	 * @param description the description of this store.
	 * @return the {@code this} object.
	 */
	public DocumentStoreBase setDescription(String description) {
		this.description = description;
		return this;
	}
}