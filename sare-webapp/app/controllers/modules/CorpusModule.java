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

package controllers.modules;

import static controllers.base.SessionedAction.*;
import static controllers.base.SareTransactionalAction.*;

import java.util.*;

import javax.annotation.Nullable;

import org.codehaus.jackson.node.*;

import com.google.common.base.Function;
import com.google.common.collect.*;

import play.Logger;
import play.api.templates.Html;
import play.libs.Json;
import play.mvc.*;
import views.html.*;
import views.html.tags.*;
import models.base.*;
import models.documentStore.PersistentDocumentStoreModel;
import controllers.base.SareTransactionalAction;
import controllers.modules.base.Module;
import edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.PersistentDocumentStoreController;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.PersistentDocumentStore;
import edu.sabanciuniv.sentilab.utils.text.nlp.annotations.LinguisticProcessorInfo;
import edu.sabanciuniv.sentilab.utils.text.nlp.factory.LinguisticProcessorFactory;

@With(SareTransactionalAction.class)
@Module.Requires
public class CorpusModule extends Module {

	@Override
	public String getDisplayName() {
		return "Corpus selection";
	}

	@Override
	public String getRoute(Iterable<ViewModel> viewModels) {
		return controllers.modules.routes.CorpusModule.module(false).url();
	}
	
	public static List<LinguisticProcessorInfo> getSupportedLanguages() {
		return Lists.newArrayList(LinguisticProcessorFactory.getSupportedProcessors());
	}
	
	public static Result supportedLanguages() {
		ArrayNode jsonArray = Json.newObject().arrayNode();
		for (LinguisticProcessorInfo lpi : getSupportedLanguages()) {
			ObjectNode json = Json.newObject();
			json.put("code", lpi.languageCode());
			json.put("name", lpi.languageName());
			jsonArray.add(json);
		}
		
		return ok(jsonArray);
	}
	
	public static Result module(boolean partial) {
		Logger.debug(partial + "");
		PersistentDocumentStoreController docStoreController = new PersistentDocumentStoreController();
		List<String> uuids = docStoreController.getAllUuids(em(), getUsername());
		List<PersistentDocumentStoreModel> stores = Lists.transform(uuids,
			new Function<String, PersistentDocumentStoreModel>() {
				@Override
				@Nullable
				public PersistentDocumentStoreModel apply(@Nullable String input) {
					return new PersistentDocumentStoreModel(fetchResource(input, PersistentDocumentStore.class));
				}
			});
		
		Html partialView = storeList.render(stores, true);
		if (!partial) {
			return ok(moduleView.render(partialView, null, null));
		} else {
			return ok(partialView);
		}
	}
	
	public static Result storeDetailsForm(String collection) {
		PersistentDocumentStoreModel viewModel = (PersistentDocumentStoreModel)createViewModel(
			fetchResource(collection, PersistentDocumentStore.class));
		return ok(storeDetails.render(viewModel, true));
	}
}