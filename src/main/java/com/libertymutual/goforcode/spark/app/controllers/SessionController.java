package com.libertymutual.goforcode.spark.app.controllers;

import java.util.HashMap;
import java.util.Map;

import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.Model;
import org.mindrot.jbcrypt.BCrypt;

import com.libertymutual.goforcode.spark.app.model.User;
import com.libertymutual.goforcode.spark.app.utilities.AutoCloseableDb;
import com.libertymutual.goforcode.spark.app.utilities.MustacheRenderer;

import spark.Request;
import spark.Response;
import spark.Route;

public class SessionController {
	

	public static final Route newForm = (Request req, Response res) -> {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("returnPath", req.queryParams("returnPath"));		
			model.put("noUser",       req.session().attribute("currentUser") == null); // BF
			model.put("currentUser",  req.session().attribute("currentUser"));		
		return MustacheRenderer.getInstance().render("session/newForm.html", model);	
	};
	
	
	// Create a new session (not the user): login
	public static final Route create = (Request req, Response res) -> {
		String email    = req.queryParams("email");
		String password = req.queryParams("password");	
				
		// Find a user, by email		
		try (AutoCloseableDb db = new AutoCloseableDb()) {
			User user = User.findFirst("email = ?", email);  
			if(user != null && BCrypt.checkpw(password, user.getPassword())) {			
				req.session().attribute("currentUser", user);
			 }
		}		
		res.redirect(req.queryParamOrDefault("returnPath", "/"));		
		return "";         
	};	
		
	
	// Logout
	public static final Route logout = (Request req, Response res) -> {
		req.session().removeAttribute("currentUser");				
		res.redirect("/");	
		return "";
	};
}
