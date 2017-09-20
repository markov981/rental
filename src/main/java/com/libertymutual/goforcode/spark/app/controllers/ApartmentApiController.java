package com.libertymutual.goforcode.spark.app.controllers;

import com.libertymutual.goforcode.spark.app.model.Apartment;
import com.libertymutual.goforcode.spark.app.utilities.AutoCloseableDb;
import com.libertymutual.goforcode.spark.app.utilities.JsonHelper;
import static spark.Spark.notFound;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;


public class ApartmentApiController {
	
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
	
	
	
}
