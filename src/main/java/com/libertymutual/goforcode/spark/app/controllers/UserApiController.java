package com.libertymutual.goforcode.spark.app.controllers;

import com.libertymutual.goforcode.spark.app.model.User;
import com.libertymutual.goforcode.spark.app.utilities.AutoCloseableDb;
import com.libertymutual.goforcode.spark.app.utilities.JsonHelper;
import static spark.Spark.notFound;
import java.util.Map;
import org.mindrot.jbcrypt.BCrypt;
import spark.Request;
import spark.Response;
import spark.Route;
import static spark.Spark.*;


public class UserApiController {
	
	public static final Route details = (Request req, Response res) -> {
		try (AutoCloseableDb db = new AutoCloseableDb()) {
		
			String email    = req.queryParams("email");	
			String password = req.queryParams("password");
			User user = User.findFirst("email = ?", email); 
			
			if(user != null && BCrypt.checkpw(password, user.getPassword())) {			
				req.session().attribute("currentUser", user);
			}
			notFound("Not Found");
			return "";
		}		
	};
	
	// consume POST
	public static final Route create = (Request req, Response res) -> {		
		 String json = req.body();
		 Map map = JsonHelper.toMap(json);
		 User user = new User();
		 user.fromMap(map);                  // hydrating user with data
		 
		 try (AutoCloseableDb db = new AutoCloseableDb()) {
			 user.saveIt();
			 res.status(201);
			 return user.toJson(true);
		 }
	};		
}
