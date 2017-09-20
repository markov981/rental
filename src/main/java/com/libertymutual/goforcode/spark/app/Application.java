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
			new User ("alex", "alex", "alex@alex.com", encryptedPassword).saveIt();	
			
			Apartment.deleteAll();
            new Apartment(6200, 2, 0d, 350, "123 Main St", "San Francisco", "CA", "95125").saveIt();
            new Apartment(1200, 4, 6d, 4000, "789 Hillcrest Drive", "Manchester", "NH", "03104").saveIt();
            new Apartment(1459, 3, 6d, 4000, "456 Cowboy Way", "Houston", "TX", "77006").saveIt();		
		}
		
		path("/apartments", () -> {
			//  protect /new path   regardless of 'before' position on the page   Filter 2nd arg
			before("/new",   SecurityFilters.isAuthenticated); 
			
			// the order matters: ':id' is a placeholder = '*' that will accept 'new' as an id - put the most specific first in that list
			get("/new",  ApartmentController.newForm);
			get("/:id",  ApartmentController.details);
			
			before("",   SecurityFilters.isAuthenticated); 					
			post("", 	 ApartmentController.create);
		});
		
		
		get("/",                HomeController.index);			
		get("/login", 			SessionController.newForm);
		post("/login", 			SessionController.create);
		get("/signup", 		    UserController.newForm);
		post("/signup", 		    UserController.create);
		
		
		path("/api", () -> {
			get("/apartments/:id", ApartmentApiController.details);      
			post("/apartments",    ApartmentApiController.create);
			post("/signup", 	   UserApiController.create);
			
			
		});
	}
}