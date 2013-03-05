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

package edu.sabanciuniv.sentilab.sare.controllers.aspect;

import edu.sabanciuniv.sentilab.core.models.factory.IllegalFactoryOptionsException;
import edu.sabanciuniv.sentilab.sare.controllers.base.documentStore.*;
import edu.sabanciuniv.sentilab.sare.models.aspect.*;

public class AspectLexiconController
	extends PersistentDocumentStoreFactory<AspectLexicon, AspectLexiconFactoryOptions>
	implements IDocumentStoreController {

	@Override
	protected AspectLexicon createPrivate(AspectLexiconFactoryOptions options, AspectLexicon lexicon)
		throws IllegalFactoryOptionsException {
		
		if (lexicon == null) {
			lexicon = new AspectLexicon(options.getBaseStore());
		}
		
		// TODO: add support for uploading file.
		
		return lexicon;
	}
}