package com.libertymutual.goforcode.spark.app.controllers;

import com.libertymutual.goforcode.spark.app.model.User;
import com.libertymutual.goforcode.spark.app.utilities.AutoCloseableDb;
import com.libertymutual.goforcode.spark.app.utilities.MustacheRenderer;
import org.mindrot.jbcrypt.BCrypt;
import spark.Request;
import spark.Response;
import spark.Route;
import static spark.Spark.*;


public class UserController {
	
	// will handle get request
	public static final Route newForm = (Request req, Response res) -> {
		return MustacheRenderer.getInstance().render("users/newForm.html", null);		
	};
	
	// will handle post request
	public static final Route create = (Request req, Response res) -> {	
	 String encryptedPassword = BCrypt.hashpw(req.queryParams("password"), BCrypt.gensalt());	
	 User user = new User (
		req.queryParams("email"),
		encryptedPassword, 
		req.queryParams("firstName"),
		req.queryParams("lastName")	
	  );
	 
	  try (AutoCloseableDb db = new AutoCloseableDb()) {
			 user.saveIt();
			 req.session().attribute("currentUser", user);
			 res.redirect("/"); ////
			 return "";
	}
		///// user[email] - 4
	  // http://localhost:4567/api/apartments/34 
		
	  
	  
//	req.queryMap("user")
//		.toMap()
//		.entrySet()
//		.stream()
//		.map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue()[0]));	
//		.collect(Collectors.toMap(entry-> entry.getKey(), entry.getValue());
//		User user = new User();
	};
	
	
}
