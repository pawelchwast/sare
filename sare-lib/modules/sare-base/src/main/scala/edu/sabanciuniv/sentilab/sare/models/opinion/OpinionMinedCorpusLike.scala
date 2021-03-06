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

package edu.sabanciuniv.sentilab.sare.models.opinion

import edu.sabanciuniv.sentilab.core.models.UserInaccessibleModel
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.DerivedStoreLike

/**
 * A class that implements this interface contains opinion mining results of a corpus.
 * @author Mus'ab Husaini
 */
trait OpinionMinedCorpusLike extends UserInaccessibleModel with DerivedStoreLike {
	
	/**
	 * Gets the engine code of the engine that was used to mine opinions.
	 * @return
	 */
	def getEngineCode: String
}