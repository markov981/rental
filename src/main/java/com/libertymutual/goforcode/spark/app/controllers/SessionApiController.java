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


public class SessionApiController {
	
	public static final Route create = (Request req, Response res) -> {	
					
	String json = req.body();
	Map credentials = JsonHelper.toMap(json);
	String email    = credentials.get("email").toString();	
	String password = credentials.get("password").toString();
	res.header("Content-Type", "application/json");
		 
	try (AutoCloseableDb db = new AutoCloseableDb()) {
		 User user = User.first("email = ?", email);
			 
		 if(user != null && BCrypt.checkpw(password, user.getPassword())) {						 
			req.session().attribute("currentUser", user);
			res.status(201);
			return user.toJson(true);
		 }
		 res.status(200);
		 return "{}";	
	   }
	};	
	
	public static final Route destroy = (Request req, Response res) -> {	
		req.session().removeAttribute("currentUser");
		res.header("Content-Type", "application/json");
		res.status(200);
		return "{}";	
	};
		
		
}
