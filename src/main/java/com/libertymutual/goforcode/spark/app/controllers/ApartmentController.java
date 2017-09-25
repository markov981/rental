package com.libertymutual.goforcode.spark.app.controllers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;

import com.libertymutual.goforcode.spark.app.model.Apartment;
import com.libertymutual.goforcode.spark.app.model.ApartmentsUsers;
import com.libertymutual.goforcode.spark.app.model.User;
import com.libertymutual.goforcode.spark.app.utilities.AutoCloseableDb;
import com.libertymutual.goforcode.spark.app.utilities.MustacheRenderer;

import spark.Request;
import spark.Response;
import spark.Route;

public class ApartmentController {
		
	// Display selected Apartment (by ID)
	public static final Route details = (Request req, Response res) -> {		
	
		try (AutoCloseableDb db = new AutoCloseableDb()) {				
			
			String idAsString = req.params("id");
			long id = Long.parseLong(idAsString);			
			Apartment apartment = Apartment.findById(id);	
			
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("apartment", apartment);
			
			model.put("noUser", req.session().attribute("currentUser") == null); // BF
			model.put("currentUser", req.session().attribute("currentUser"));
			
			// Display if: logged in && 
			boolean loggedNotlikedNolister = false;
			User currentUser = req.session().attribute("currentUser");
			User lister = apartment.parent(User.class);  			 

			if (currentUser != null) {
				List<Apartment> apartmentLiked = currentUser.get(Apartment.class, "id = ?", id);
				
				if(!lister.getId().equals(currentUser.getId()) && apartmentLiked.size() == 0 ) {
					loggedNotlikedNolister = true;		
				}
			}
			model.put("loggedNotlikedNolister", loggedNotlikedNolister); 
			
			return MustacheRenderer.getInstance().render("apartment/details.html", model);
			}
	};
	
	
	public static final Route newForm = (Request req, Response res) -> {		
		Map<String, Object> model = new HashMap<String, Object>(); 			 // BF
		model.put("noUser", req.session().attribute("currentUser") == null); // BF
		model.put("currentUser", req.session().attribute("currentUser"));
		
		return MustacheRenderer.getInstance().render("apartment/newForm.html", null);
	};

	
	// User creates a listing, listing is associated with the user 
	public static final Route create = (Request req, Response res) -> {
		
		Map<String, Object> model = new HashMap<String, Object>(); 			 // BF
		model.put("noUser", req.session().attribute("currentUser") == null); // BF
		model.put("currentUser", req.session().attribute("currentUser"));
		
		 try (AutoCloseableDb db = new AutoCloseableDb()) {
			 Apartment apartment = new Apartment (
				Integer.parseInt(req.queryParams("rent")),
				Integer.parseInt(req.queryParams("number_of_bedrooms")),
				Double.parseDouble(req.queryParams("number_of_bathrooms")), 
				Integer.parseInt(req.queryParams("square_footage")),
				req.queryParams("address"), 
				req.queryParams("city"),
				req.queryParams("state"),
				req.queryParams("zipCode"),	
				true
			  );
			 
			 User user = req.session().attribute("currentUser");    	  
			 apartment.saveIt();
			 user.add(apartment);		 
			 res.redirect("/apartments/mine"); 
			 return "";
		 }		
	};
	
	

    // Populate model with apartments associated with a user (current), splitting active & inactive listings 
	public static final Route index = (Request req, Response res) -> {
				
		try (AutoCloseableDb db = new AutoCloseableDb()) {
								
			Map<String, Object> model = new HashMap<String, Object>();
			
			// Common navigation the dumb way
			model.put("noUser",      req.session().attribute("currentUser") == null);     // BF
			model.put("currentUser", req.session().attribute("currentUser"));
			
			// Select active listings 
			List<Apartment> apartmentsActive = Apartment.where("is_active = ?", true);	
			model.put("apartmentsActive", apartmentsActive);	
			
			// Select in active listings 
			List<Apartment> apartmentsInactive = Apartment.where("is_active = ?", false);
			model.put("apartmentsInactive", apartmentsInactive);	
										
		    return MustacheRenderer.getInstance().render("apartment/index.html", model);
		}
	};


	// Activate apartment
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
			// If logged-in User ID == user_id of the Apartment selected --> user listed the apartment
			User lister =  apartment.parent(User.class);                	
			long listerUserId = (long) lister.getId();
			long loggedUserId = (long) currentUser.getId();
							 
			if ( listerUserId == loggedUserId ) {    // lister.getId().equals(currentUser.getId()    
			    apartment.setIsActive(true);}
			
			apartment.saveIt();	
			currentUser.add(apartment);
			res.redirect("/apartments/" + id);
			return "";
			}
	};
	
	
	// De-activate apartment
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
			// If logged-in User ID == user_id of the Apartment selected --> user listed the apartment
			User lister =  apartment.parent(User.class);                	
			long listerUserId = (long) lister.getId();
			long loggedUserId = (long) currentUser.getId();
							 
			if ( listerUserId == loggedUserId ) {    // lister.getId().equals(currentUser.getId()    
			    apartment.setIsActive(false);}
			
			apartment.saveIt();	
			currentUser.add(apartment);
			res.redirect("/apartments/" + id);
			return "";
			}
	};
	
	
	// Associate user with apartment ('user likes it')
	public static final Route likes = (Request req, Response res) -> {
		// For non-authenticated
		User currentUser = req.session().attribute("currentUser");
		if(req.session().attribute("currentUser") == null) {			
			res.redirect("/");
			return "";
		}
		
		try (AutoCloseableDb db = new AutoCloseableDb()) {				
			// 1st condition
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("noUser",      req.session().attribute("currentUser") == null);     // BF
			model.put("currentUser", req.session().attribute("currentUser"));
			
			
			// 2nd condition
			boolean loggedNotlikedNolister = false;
			  // Is a lister?
			String idAsString = req.params("id");
			int id = Integer.parseInt(idAsString); 				  
			Apartment apartment = Apartment.findById(id);
			User lister = apartment.parent(User.class);  			 
			  // Has liked?
			List<Apartment> apartmentLiked = currentUser.get(Apartment.class, "id = ?", id);			
			    
			if( req.session().attribute("currentUser") != null  && !lister.getId().equals(currentUser.getId()) && apartmentLiked.size() == 0 ) 
				loggedNotlikedNolister = true;		
				apartment.add(currentUser);   // ?
			model.put("loggedNotlikedNolister", loggedNotlikedNolister); 
	
				
			// 3rd condition - list of users who liked the apt
			boolean loggedListed = false;
			
			List<User> usersWhoLiked = apartment.getAll(User.class);		
			model.put("usersWhoLiked", usersWhoLiked);
			
			if( req.session().attribute("currentUser") != null  && lister.getId().equals(currentUser.getId()) ) 
				loggedListed = true;		
			model.put("loggedListed", loggedListed); 
					
			model.put("isActive",     apartment.getIsActive());			
			model.put("isNotActive", !apartment.getIsActive());	
						
			// nothing to save 				
			res.redirect("/apartments/" + id);
			return "";
		}		
	};
	
}