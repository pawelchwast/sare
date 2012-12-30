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

package controllers;

import java.util.*;

import javax.annotation.Nullable;

import models.ModuleView;
import models.base.*;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.reflections.Reflections;

import com.google.common.base.*;
import com.google.common.collect.*;

import play.Play;
import play.libs.Json;
import play.mvc.*;
import views.html.*;
import controllers.base.*;
import controllers.modules.base.Module;

public class ModuleController extends Application {

	private static Set<ModuleView> getNextModules(String input) {
		// get all the supplied view models.
		List<ViewModel> suppliedViewModels = Lists.newArrayList();
		JsonNode inputJson = Json.parse(input);
		
		// convert json nodes to view models.
		if (inputJson != null && inputJson.isArray()) {
			suppliedViewModels = Lists.newArrayList(Iterators.transform(inputJson.getElements(),
				new Function<JsonNode, ViewModel>() {
					@Override
					@Nullable
					public ViewModel apply(@Nullable JsonNode input) {
						return createViewModelQuietly(input, null);
					}
				}));
		} else if (inputJson != null && inputJson.isObject()) {
			suppliedViewModels.add(createViewModelQuietly(inputJson, null));
		}
		
		suppliedViewModels = Lists.newArrayList(Iterables.filter(suppliedViewModels, Predicates.notNull()));
		
		// get all the modules that can use these inputs.
		Set<ModuleView> modules = Sets.newHashSet();
		Reflections reflections = new Reflections("controllers.modules", Play.application().classloader());
		for (Class<? extends Module> moduleClass : reflections.getSubTypesOf(Module.class)) {
			// get the Module.Requires annotation for each module class.
			Module.Requires reqAnnotation = null;
			if (moduleClass.isAnnotationPresent(Module.Requires.class)) {
				reqAnnotation = moduleClass.getAnnotation(Module.Requires.class);
			}
			
			final Set<Class<? extends ViewModel>> requiredViewModelClasses = Sets.newHashSet();
			if (reqAnnotation != null) {
				Collections.addAll(requiredViewModelClasses, reqAnnotation.types());
			} // no annotation means no requirement
			
			// get all the supplied view modules that are relevant to this module.
			List<ViewModel> usefulViewModels = Lists.newArrayList(Iterables.filter(suppliedViewModels,
				new Predicate<ViewModel>() {
					@Override
					public boolean apply(@Nullable ViewModel input) {
						// if this class is required, then return true.
						if (requiredViewModelClasses.contains(input.getClass())) {
							return true;
						}
						
						// if any of its super classes are required, that also works.
						for (Class<?> superClass : ClassUtils.getAllSuperclasses(input.getClass())) {
							if (requiredViewModelClasses.contains(superClass)) {
								return true;
							}
						}
						
						return false;
					}
				}));
			
			// if all the requirements were satisfied.
			if (usefulViewModels.size() >= requiredViewModelClasses.size()) {
				// try to create an instance of the module.
				Module module = null;
				try {
					module = moduleClass.newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					module = null;
				} finally {
					if (module == null) {
						// in this case, there is a problem with the module, so just ignore.
						continue;
					}
				}
				
				// set the module view model properties and add.
				ModuleView moduleViewModel = new ModuleView(module);
				moduleViewModel.route = module.getRoute(usefulViewModels);
				// let's not divide by zero!
				moduleViewModel.relevancyScore = suppliedViewModels.size() != 0 ?
					usefulViewModels.size() / (double)suppliedViewModels.size() : 1.0;

				modules.add(moduleViewModel);
			}
		}

		return modules;
	}
	
	public static Result options(String input) {
		return ok(play.libs.Json.toJson(getNextModules(input)));
	}
	
	public static Result next(String input) {
		Set<ModuleView> modules = getNextModules(input);
		if (modules == null || modules.size() == 0) {
			throw new IllegalArgumentException();
		} else if (modules.size() == 1 && StringUtils.isNotEmpty(Iterables.getFirst(modules, null).getRoute())) {
			return redirect(Iterables.getFirst(modules, null).getRoute());
		}
		
		return ok(moduleSelection.render(getNextModules(input)));
	}
}