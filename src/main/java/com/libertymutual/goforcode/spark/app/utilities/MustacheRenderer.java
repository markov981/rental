package com.libertymutual.goforcode.spark.app.utilities;

import java.io.StringWriter;
import java.util.Map;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;

public class MustacheRenderer {

	private DefaultMustacheFactory factory;

	// a singleton
	private static final MustacheRenderer instance = new MustacheRenderer("templates");		
	
	// A private constructor - only the class itself can generate a new Mustache renderer - else use getInstance()
	private MustacheRenderer(String folderName) {
		factory = new DefaultMustacheFactory(folderName);
	}
	
	public static MustacheRenderer getInstance() {
		return instance;		
	}
	
	
	// mustache render
	public String render(String templatePath, Map<String, Object> model) {
		Mustache mustache = factory.compile(templatePath);       // "template.html" 
		
		StringWriter writer = new StringWriter();
		mustache.execute(writer, model);    // writes from  mustache template into the destination - a string goes to the browser
		
		return writer.toString();
	}	
}
