package com.libertymutual.goforcode.spark.app.controllers;

import com.libertymutual.goforcode.spark.app.model.Apartment;
import com.libertymutual.goforcode.spark.app.model.User;
import com.libertymutual.goforcode.spark.app.utilities.AutoCloseableDb;
import com.libertymutual.goforcode.spark.app.utilities.JsonHelper;
import com.libertymutual.goforcode.spark.app.utilities.MustacheRenderer;

import static spark.Spark.notFound;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javalite.activejdbc.LazyList;

import spark.Request;
import spark.Response;
import spark.Route;


public class ApartmentApiController {
	
	
	
   // Populate model with apartments associated with a user (current), splitting active & inactive listings 
	public static final Route index = (Request req, Response res) -> {
				
		try (AutoCloseableDb db = new AutoCloseableDb()) {								
			LazyList<Apartment> apartments = Apartment.where("is_active = ?", true);
			res.header("Content-Type", "application/json");
			return apartments.toJson(true);
		}
	};

	
	// Get apartments, associated with a particular user 
	public static final Route mine = (Request req, Response res) -> {

		User currentUser = req.session().attribute("currentUser");
		long loggedUserId = (long) currentUser.getId();
		
		try (AutoCloseableDb db = new AutoCloseableDb()) {								
			LazyList<Apartment> apartments = Apartment.where("user_id = ?", loggedUserId); // it's a comparison 
			res.header("Content-Type", "application/json");
			return apartments.toJson(true);
			}
		};
        
        		
		
	public static final Route details = (Request req, Response res) -> {

		try (AutoCloseableDb db = new AutoCloseableDb()) {			
			String idAsString = req.params("id");
			int id = Integer.parseInt(idAsString);
			Apartment apartment = Apartment.findById(id);
			
			if(apartment != null) {
				// Set content-type header for the browser to understand content of the response
				res.header("Content-Type", "application/json");  
				return apartment.toJson(true);   // returns JSON representation of an apartment
			}
			notFound("Not Found");
			return "";
		}		
	};
	
	public static final Route create = (Request req, Response res) -> {		
		 String json = req.body();
		 Map map = JsonHelper.toMap(json);
		 
		 Apartment apartment = new Apartment();
		 apartment.fromMap(map);   // hydrating
		 
		 try (AutoCloseableDb db = new AutoCloseableDb()) {
			 apartment.saveIt();
			 res.status(201);
			 return apartment.toJson(true);
		 }
	};


	public static final Route activate = (Request req, Response res) -> {
			
			// For non-authenticated
			User currentUser = req.session().attribute("currentUser");
			if(req.session().attribute("currentUser") == null) {			
				res.redirect("/");
				return "";
			}		
					 
			try (AutoCloseableDb db = new AutoCloseableDb()) {									
				String idAsString = req.params("id");
				int id = Integer.parseInt(idAsString); 				  
				Apartment apartment = Apartment.findById(id);
				
				System.out.println("ACTIVATE -------------> " + id);
				
				// If logged-in User ID == user_id of the Apartment selected --> user listed the apartment
				User lister =  apartment.parent(User.class);                	
				long listerUserId = (long) lister.getId();
				long loggedUserId = (long) currentUser.getId();
								 
				if ( listerUserId == loggedUserId ) {       
				    apartment.set("is_active", true);}

				apartment.saveIt();	
				res.header("Content-Type", "application/json");
				return apartment.toJson(true);
				}
		};
		
	public static final Route deactivate = (Request req, Response res) -> {
			
			// For non-authenticated
			User currentUser = req.session().attribute("currentUser");
			if(req.session().attribute("currentUser") == null) {			
				res.redirect("/");
				return "";
			}		
					 
			try (AutoCloseableDb db = new AutoCloseableDb()) {									
				String idAsString = req.params("id");
				int id = Integer.parseInt(idAsString); 				  
				Apartment apartment = Apartment.findById(id);
				
				System.out.println("Deactivate -------------> " + id);
				
				
				// If logged-in User ID == user_id of the Apartment selected --> user listed the apartment
				User lister =  apartment.parent(User.class);                	
				long listerUserId = (long) lister.getId();
				long loggedUserId = (long) currentUser.getId();
								 
				if ( listerUserId == loggedUserId ) {       
				    apartment.set("is_active", false);}

				apartment.saveIt();	
				res.header("Content-Type", "application/json");
				return apartment.toJson(true);
				}
		};	
	
}
