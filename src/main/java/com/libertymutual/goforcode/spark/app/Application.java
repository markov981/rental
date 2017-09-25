package com.libertymutual.goforcode.spark.app;

import static spark.Spark.*;
import org.mindrot.jbcrypt.BCrypt;
import com.libertymutual.goforcode.spark.app.controllers.ApartmentApiController;
import com.libertymutual.goforcode.spark.app.controllers.ApartmentController;
import com.libertymutual.goforcode.spark.app.controllers.HomeController;
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
			get("/apartments/:id", ApartmentApiController.details);      
			post("/apartments",    ApartmentApiController.create);
			post("/signup", 	   UserApiController.create);
		});
	}
}