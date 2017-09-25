package com.libertymutual.goforcode.spark.app.controllers;

import com.libertymutual.goforcode.spark.app.model.User;
import com.libertymutual.goforcode.spark.app.utilities.AutoCloseableDb;
import com.libertymutual.goforcode.spark.app.utilities.MustacheRenderer;
import org.mindrot.jbcrypt.BCrypt;
import spark.Request;
import spark.Response;
import spark.Route;
import java.util.HashMap;
import java.util.Map;

public class UserController {

	// will handle get request
	public static final Route newForm = (Request req, Response res) -> {
		Map<String, Object> model = new HashMap<String, Object>(); 			 // BF
		model.put("noUser", req.session().attribute("currentUser") == null); // BF
		model.put("currentUser", req.session().attribute("currentUser"));

		return MustacheRenderer.getInstance().render("users/newForm.html", model);
	};

	// will handle post request
	public static final Route create = (Request req, Response res) -> {
		String encryptedPassword = BCrypt.hashpw(req.queryParams("password"), BCrypt.gensalt());
		User user = new User(req.queryParams("first_name"), req.queryParams("last_name"), req.queryParams("email"), encryptedPassword);
		try (AutoCloseableDb db = new AutoCloseableDb()) {
			user.saveIt();
			Map<String, Object> model = new HashMap<String, Object>(); 	
			model.put("currentUser",  req.session().attribute("currentUser"));			
			req.session().attribute("currentUser", user);
			res.redirect("/");
			return "";
		}

	};
}
