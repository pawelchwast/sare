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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SARE. If not, see <http://www.gnu.org/licenses/>.
 */

package edu.sabanciuniv.sentilab.sare.models.base.document;

import javax.persistence.*;

import com.google.gson.JsonElement;

/**
 * A class that has its own textual content.
 * @author Mus'ab Husaini
 * 
 * @param <T> a circular reference to this type of document; must derive from {@link TextDocument}.
 */
@MappedSuperclass
public abstract class TextDocument<T extends TextDocument<T>>
	extends GenericDocument<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5392373511223975393L;

	@Override
	public String getContent() {
		JsonElement content = this.getOtherData().get("content");
		return content != null ? content.getAsString() : null;
	}
	
	/**
	 * Sets the content of this document.
	 * @param content the content to set.
	 * @return the {@code this} object.
	 */
	public TextDocument<T> setContent(String content) {
		this.getOtherData().addProperty("content", content);
		return this;
	}
}