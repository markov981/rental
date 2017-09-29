//package com.libertymutual.goforcode.spark.app.controllers;
//
//import com.libertymutual.goforcode.spark.app.model.Apartment;
//import com.libertymutual.goforcode.spark.app.utilities.AutoCloseableDb;
//import spark.Request;
//import spark.Response;
//import spark.Route;
//
//public class ActivateController {
//    public static final Route update = (Request req, Response res) -> {
//        try(AutoCloseableDb db = new AutoCloseableDb()) {
//            boolean activationStatus = Boolean.valueOf(req.queryParams("activationStatus"));
//            Apartment apartment = Apartment.findById(Integer.parseInt(req.params("id")));
//            System.out.println("AS: " + activationStatus );
//            apartment.setIsActive(activationStatus);
//            apartment.saveIt();
//        }
//        res.redirect("/apartments/" + req.params("id"));
//        return "";
//    };
//}