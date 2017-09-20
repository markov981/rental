package com.libertymutual.goforcode.spark.app.controllers;

import static spark.Spark.get;

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
				
			List<Apartment> apartments = Apartment.findAll();
			Map<String, Object> model = new HashMap<String, Object>();
			
			try (AutoCloseableDb db = new AutoCloseableDb()) {
				model.put("apartments", apartments);
				model.put("noUser",       req.session().attribute("currentUser") == null);
				model.put("currentUser",  req.session().attribute("currentUser"));
				
				return MustacheRenderer.getInstance().render("home/index.html", model);
		}
		
		
		
	};
}	