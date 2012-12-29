package controllers.base;

import org.codehaus.jackson.JsonNode;

import actors.*;
import models.*;
import models.base.*;
import controllers.factories.ViewModelFactory;
import play.*;
import play.db.ebean.Transactional;
import play.mvc.*;

import views.html.*;

@Transactional
@With({ SessionedAction.class, ErrorHandledAction.class, LoggedAction.class })
public class Application extends Controller {
	
	protected static <T> ViewModel createViewModel(T model) {
		ViewModel viewModel = new ViewModelFactory().create(new ViewModelFactoryOptions().setModel(model));
		if (viewModel == null) {
			throw new IllegalArgumentException();
		}
		
		return viewModel;
	}
	
	protected static ViewModel createViewModel(JsonNode json) {
		ViewModel viewModel = new ViewModelFactory().create(new ViewModelFactoryOptions().setJson(json));
		if (viewModel == null) {
			throw new IllegalArgumentException();
		}
		
		return viewModel;
	}
	
	protected static <T> ViewModel createViewModelQuietly(T model, ViewModel defaultViewModel) {
		try {
			return createViewModel(model);
		} catch (IllegalArgumentException e) {
			return defaultViewModel;
		}
	}
	
	protected static <T> ViewModel createViewModelQuietly(T model) {
		return createViewModelQuietly(model, new ViewModel());
	}
	
	protected static ViewModel createViewModelQuietly(JsonNode json, ViewModel defaultViewModel) {
		try {
			return createViewModel(json);
		} catch (IllegalArgumentException e) {
			return defaultViewModel;
		}
	}
	
	protected static ViewModel createViewModelQuietly(JsonNode json) {
		return createViewModelQuietly(json, new ViewModel());
	}
	
	public static Result index() {
		return ok(index.render());
	}

	public static Result keepAlive() {
		Logger.info(LoggedAction.getLogEntry("keeping session alive"));
		return ok();
	}
	
	public static Result login() {
		return TODO;
	}

	public static Result logout() {
		WebSession session = SessionedAction.getWebSession();
		if (session == null) {
			return badRequest();
		}
		
		SessionCleaner.clean(session);
		return ok();
	}
	
	public static Result signin() {
		return TODO;
	}

	public static Result signout() {
		return TODO;
	}
}