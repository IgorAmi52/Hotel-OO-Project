package com.service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.exceptions.ElementAlreadyExistsException;
import com.exceptions.NoPricingException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.models.Pricing;
import com.models.Reservation;

public class PricingService {
	
	private static Gson gson = new Gson();
	private static FileReader reader;
	private static FileWriter writer;

	public static Pricing[] getPricing(Boolean isRoom) throws IOException {
		
		reader = new FileReader("data/pricing.json");
		JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
		reader.close();
		
		List<Pricing> pricingArrList = new ArrayList<Pricing>();
		
		for(String type: jsonObject.keySet()) {
			if(isRoom && Arrays.asList(RoomService.getRoomTypes()).contains(type)) {
				JsonArray roomsArr = jsonObject.getAsJsonArray(type); 
				for(int i = 0; i < roomsArr.size(); i++) {		
					Pricing currentPricing = gson.fromJson(roomsArr.get(i), Pricing.class);
					pricingArrList.add(currentPricing);
				}
			}
			else if(!isRoom && !Arrays.asList(RoomService.getRoomTypes()).contains(type)) {
				JsonArray addArr = jsonObject.getAsJsonArray(type); 
				for(int i = 0; i < addArr.size(); i++) {
					Pricing currentPricing = gson.fromJson(addArr.get(i), Pricing.class);
					pricingArrList.add(currentPricing);
				}
			}
		}
		Pricing[] ret = new Pricing[pricingArrList.size()];
		
		int i = 0;
		for(Pricing pricing: pricingArrList) {
			ret[i++] = pricing;
		}
		return ret;
	}
	public static void deletePricing(Pricing pricing) throws IOException{
		reader = new FileReader("data/pricing.json");

		JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
		reader.close();
		JsonArray arr = jsonObject.getAsJsonArray(pricing.getType());
        for (int i = 0; i < arr.size(); i++) {
            JsonObject current = arr.get(i).getAsJsonObject();
            Pricing currentPricing = gson.fromJson(current, Pricing.class);

            if (currentPricing.equals(pricing)) {
                arr.remove(i);
                break;
            }
        }
        jsonObject.add(pricing.getType(), arr);
        writer = new FileWriter("data/pricing.json");
        writer.write(gson.toJson(jsonObject));
        writer.close();
	}
	public static void addPricing(Pricing pricing) throws IOException, ElementAlreadyExistsException {

		reader = new FileReader("data/pricing.json");

		JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
		reader.close();

		JsonArray arr = jsonObject.getAsJsonArray(pricing.getType());
		
		if(arr==null) {
			arr = new JsonArray();
		}
		else {
			String fromDate = pricing.getFromDate();
			String toDate = pricing.getToDate();
			for(int i = 0; i < arr.size(); i++) {
	            JsonObject current = arr.get(i).getAsJsonObject();
	            String currentFromDate = current.get("fromDate").getAsString();
	            String currentToDate = current.get("toDate").getAsString();
	            
	            if(DateLabelFormatter.checkIntervalOverlap(fromDate, toDate, currentFromDate, currentToDate)) {
	            	throw new ElementAlreadyExistsException("Pricing for this service already exists for this time interval!");
	            }
			}
		}

		arr.add(pricing.getJson());
		jsonObject.add(pricing.getType(), arr);

		FileWriter writer = new FileWriter("data/pricing.json");
		writer.write(new Gson().toJson(jsonObject));
		writer.close();
		
	}
	public static Double calculatePricing(Reservation reservation) throws IOException, NoPricingException {
		
		Double ret = 0.0;
		String roomType = reservation.getRoomType();
		String checkInDate = reservation.getCheckInDate();
		String checkOutDate = reservation.getCheckOutDate();
		String[] addServices = reservation.getAddServices();
		String[] services = new String[addServices.length+1];
		
		int i = 0;
		services[i] = roomType;
		for(String service:addServices) {
			services[++i] = service;
		}
		
		reader = new FileReader("data/pricing.json");
		JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
		reader.close();
		
		for(String service:services) {
			if(!jsonObject.has(service)) {
				throw new NoPricingException("No pricing set for " + service + " service!");
			}
			JsonArray pricesJsonArr = jsonObject.getAsJsonArray(service);
			
			Set<String> datesSet = DateLabelFormatter.getDateRange(checkInDate, DateLabelFormatter.previousDate(checkOutDate));
			
			for(JsonElement priceJson: pricesJsonArr) {
				if(datesSet.isEmpty()) {
					break;
				}
				Pricing pricing = gson.fromJson(priceJson, Pricing.class);
				Set<String> pricingSet = DateLabelFormatter.getDateRange(pricing.getFromDate(), pricing.getToDate());
				int totalDays = datesSet.size();
				datesSet.removeAll(pricingSet);
				int difference  = totalDays - datesSet.size();
				
				Double dailyPrice = pricing.getPrice();
				
				ret+= dailyPrice*difference;
			}
			if(!datesSet.isEmpty()) {
				throw new NoPricingException("Missing pricing for "+ service);
			}
		}
		return ret;
	}

}
