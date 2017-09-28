package com.libertymutual.goforcode.spark.app;

import static spark.Spark.*;
import org.mindrot.jbcrypt.BCrypt;
import com.libertymutual.goforcode.spark.app.controllers.ApartmentApiController;
import com.libertymutual.goforcode.spark.app.controllers.ApartmentController;
import com.libertymutual.goforcode.spark.app.controllers.HomeController;
import com.libertymutual.goforcode.spark.app.controllers.SessionApiController;
import com.libertymutual.goforcode.spark.app.controllers.SessionController;
import com.libertymutual.goforcode.spark.app.controllers.UserApiController;
import com.libertymutual.goforcode.spark.app.controllers.UserController;
import com.libertymutual.goforcode.spark.app.filters.SecurityFilters;
import com.libertymutual.goforcode.spark.app.model.Apartment;
import com.libertymutual.goforcode.spark.app.model.User;
import com.libertymutual.goforcode.spark.app.utilities.AutoCloseableDb;

import spark.Request;
import spark.Response;


public class Application {
	
	public static void main(String []args) {
		
		String encryptedPassword = BCrypt.hashpw("password", BCrypt.gensalt()); 

	    // create a user
		try (AutoCloseableDb db = new AutoCloseableDb()) {
			User.deleteAll();
			User alex = new User ("alex", "alex", "alex@alex.com", encryptedPassword);
			alex.saveIt();	
					
			Apartment.deleteAll();
			Apartment apartment = new Apartment(6200, 2, 1d, 350, "123 Main St", "San Francisco", "CA", "95125", true);
			apartment.saveIt();                
			alex.add(apartment);   
			
			apartment = new Apartment(1200, 4, 6d, 4000, "789 Hillcrest Drive", "Manchester", "NH", "03104", true);
			apartment.saveIt();
			alex.add(apartment);   
			
		}
	
			    
				// one origin & all methods and options, "localhost:4200" - lock it down to
				enableCORS("http://localhost:4200", "*", "*");
				
				
				// before(request, response)
		
		
		
		path("/apartments", () -> {
			
			//  protect /new path   regardless of 'before' position on the page   Filter 2nd arg
			// the order matters: ':id' is a placeholder = '*' that will accept 'new' as an id - put the most specific first in that list			
			before("/new",    			SecurityFilters.isAuthenticated); 
				get("/new",  				ApartmentController.newForm);
			before("/mine",   			SecurityFilters.isAuthenticated); 
				get("/mine", 				ApartmentController.index);		
				get("/:id",  				ApartmentController.details);				
				get("/:id/activations",  	ApartmentController.activate);
				get("/:id/deactivations",  	ApartmentController.deactivate);
			before("",   	  			SecurityFilters.isAuthenticated); 
			    post("/:id/likes", 	 		ApartmentController.likes);
				post("", 	 				ApartmentController.create);
			    
		});
		
		
		get("/",                HomeController.index);			
		get("/login", 			SessionController.newForm);
		post("/login", 			SessionController.create);
		post("/logout", 		SessionController.logout);
		get("/users/new", 		UserController.newForm);
		post("/users", 			UserController.create);
		
		
		path("/api", () -> {
			get("/apartments/mine",  ApartmentApiController.mine);
			get("/apartments/:id",   ApartmentApiController.details);
			get("/apartments",       ApartmentApiController.index);
			post("/apartments",      ApartmentApiController.create);
			
			//post("/signup", 	     UserApiController.create);
			post("/sessions", 	     SessionApiController.create);
			delete("/sessions/mine", SessionApiController.destroy);
		});
	}
	
	
	
	
	// Enables CORS on requests. This method is an initialization method and should be called once.
	private static void enableCORS(final String origin, final String methods, final String headers) {

	    options("/*", (request, response) -> {
	        String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
	        if (accessControlRequestHeaders != null) {
	            response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
	        }

	        String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
	        if (accessControlRequestMethod != null) {
	            response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
	        }

	        return "OK";
	    });

	    before((request, response) -> {
	        response.header("Access-Control-Allow-Origin", origin);
	        response.header("Access-Control-Request-Method", methods);
	        response.header("Access-Control-Allow-Headers", headers);
	        // This may or may not be necessary in your particular application
//	        response.type("application/json");
	        response.header("Access-Control-Allow-Credentials", "true");
	    });
	}	
	
	
	
	
	
	
	
	
	
}