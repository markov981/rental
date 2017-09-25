package com.libertymutual.goforcode.spark.app.controllers;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.libertymutual.goforcode.spark.app.model.Apartment;
import com.libertymutual.goforcode.spark.app.utilities.AutoCloseableDb;
import com.libertymutual.goforcode.spark.app.utilities.MustacheRenderer;
import spark.Request;
import spark.Response;
import spark.Route;

public class HomeController {

	public static final Route index = (Request req, Response res) -> {
				
			// List<Apartment> apartments = Apartment.findAll(); -----------
			List<Apartment> apartmentsActive = Apartment.where("is_active = ?", true);	

			Map<String, Object> model = new HashMap<String, Object>();
			
			try (AutoCloseableDb db = new AutoCloseableDb()) {
				model.put("apartmentsActive", apartmentsActive);
				model.put("noUser",       req.session().attribute("currentUser") == null);
				model.put("currentUser",  req.session().attribute("currentUser"));
				
				return MustacheRenderer.getInstance().render("home/index.html", model);
		}		
	};
}

