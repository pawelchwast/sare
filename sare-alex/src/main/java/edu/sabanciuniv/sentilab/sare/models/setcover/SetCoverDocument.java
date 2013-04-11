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

package edu.sabanciuniv.sentilab.sare.models.setcover;

import javax.persistence.*;

import edu.sabanciuniv.sentilab.sare.models.base.document.*;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.LinguisticText;

/**
 * The class for set cover documents.
 * @author Mus'ab Husaini
 */
@Entity
@DiscriminatorValue("setcover-document")
public class SetCoverDocument
	extends MergableDocument implements IWeightedDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5638920050574370647L;
	
	private void updateWeight() {
		if (this.weight == null) {
			this.setWeight(this.getWeight());
		}
	}

	/**
	 * Sets the weight of this document.
	 * @param weight the weight to set.
	 * @return the {@code this} object.
	 */
	private SetCoverDocument setWeight(Double weight) {
		this.weight = weight;
		return this;
	}

	@Override
	protected void preCreate() {
		updateWeight();
		super.preCreate();
	}
	
	@Override
	protected void preUpdate() {
		updateWeight();
		super.preUpdate();
	}

	/**
	 * Creates a new instance of {@link SetCoverDocument}.
	 */
	public SetCoverDocument() {
		this.setCovered(true);
	}
	
	/**
	 * Creates a new instance of {@link SetCoverDocument}.
	 * @param baseDocument the {@code PersistentDocument} used as the base document for this instance.
	 */
	public SetCoverDocument(PersistentDocument baseDocument) {
		this();
		this.setBaseDocument(baseDocument);
	}
	
	@Override
	public SetCoverDocument setBaseDocument(PersistentDocument baseDocument) {
		super.setBaseDocument(baseDocument);
		this.title = null;
		this.getContent(true);
		return this;
	}
	
	@Override
	public String getContent() {
		return this.getContent(false);
	}
	
	/**
	 * Gets the (possibly NLP-enriched) content of this document.
	 * @param enhanced {@code true} if NLP-enhanced content is desired, {@code false} otherwise.
	 * @return the (possibly NLP-enhanced) content of this document.
	 */
	public String getContent(boolean enhanced) {
		if (enhanced) {
			if (this.getBaseDocument() != null && this.getBaseDocument() instanceof FullTextDocument &&
				(this.title == null
				|| (this.getLastUpdatedDate() != null && this.getBaseDocument().getLastUpdatedDate() != null
				&& this.getLastUpdatedDate().getTime() <= this.getBaseDocument().getLastUpdatedDate().getTime()))) {
				
				FullTextDocument ftDoc = (FullTextDocument)this.getBaseDocument();
				LinguisticText parsedContent = ftDoc.getParsedContent();
				if (parsedContent != null) {
					this.title = parsedContent.toString(true);
				}
			}
			
			return this.title;
		}
		return this.getBaseDocument() != null ? this.getBaseDocument().getContent() : null;
	}
	
	/**
	 * Gets the weight of this document.
	 * @return the weight of this document.
	 */
	@Override
	public Double getWeight() {
		return this.weight != null ? this.weight : this.getTotalTokenWeight();
	}
	
	/**
	 * Resets the weight of this document to {@code null}.
	 * @return the {@code this} object.
	 */
	public SetCoverDocument resetWeight() {
		this.setWeight(null);
		return this;
	}
	
	/**
	 * Gets a flag indicating whether this document is covered or not.
	 * @return {@code true} if covered; {@code false} otherwise.
	 */
	public boolean isCovered() {
		return this.flag;
	}
	
	/**
	 * Sets a flag indicating whether this document is covered or not.
	 * @param covered {@code true} if covered; {@code false} otherwise.
	 * @return the {@code this} object.
	 */
	public SetCoverDocument setCovered(boolean covered) {
		this.flag = covered;
		return this;
	}

	@Override
	public String toString() {
		return this.getBaseDocument() != null ? this.getBaseDocument().toString() : super.toString(); 
	}
}