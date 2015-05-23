package com.whippy.sponge.whipconomy.beans;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.whippy.cponge.whipconomy.orchestrator.EconomyCache;
import com.whippy.sponge.whipconomy.cache.ConfigurationLoader;

public class Account {

	public static final String PLAYER_ID = "playerId";
	public static final String BAL_ID = "bal";
	public static final String PAYMENTS_ID = "payments";
	private String playerId;
	private double bal;
	private List<Payment> payments;
	
	public Account(String playerId) {
		this.playerId = playerId;
		bal = 0;
		payments = new ArrayList<Payment>();
	}
	
	public void ammendBal(Double amount){
		amount = EconomyCache.round(amount, ConfigurationLoader.getDecPlaces());
		bal += amount;
	}

	public String getPlayerId() {
		return playerId;
	}
	
	public void addPayment(Payment payment){
		if(payments.size()>=ConfigurationLoader.getMaxTransactionHistory()){
			payments.remove(0);
		}
		payments.add(payment);
	}

	public double getBal() {
		return bal;
	}

	public List<Payment> getPayments() {
		return payments;
	}

	public JSONObject toJSONObject(){
		JSONObject obj = new JSONObject();
		obj.put(PLAYER_ID, playerId);
		obj.put(BAL_ID, bal);
		JSONArray paymentArray = new JSONArray();
		for (Payment payment : payments) {
			paymentArray.add(payment.toJSONObject());
		}
		obj.put(PAYMENTS_ID, paymentArray);
		return obj;
	}
	
}
