package com.whippy.sponge.whipconomy.beans;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.whippy.sponge.whipconomy.cache.ConfigurationLoader;

public class CurrentAccount extends Account{

	private List<Payment> payments;
	
	public CurrentAccount(String playerId) {
		super(playerId);
		payments = new ArrayList<Payment>();
	}
	
	public void addPayment(Payment payment){
		if(payments.size()>=ConfigurationLoader.getMaxTransactionHistory()){
			payments.remove(0);
		}
		payments.add(payment);
	}

	public List<Payment> getPayments() {
		return payments;
	}

	
	public JSONObject toJSONObject(){
		JSONObject obj = new JSONObject();
		obj.put(PLAYER_ID, super.getPlayerId());
		obj.put(BAL_ID, super.getBal());
		obj.put(ACCOUNT_TYPE, super.getAccountType());
		JSONArray paymentArray = new JSONArray();
		for (Payment payment : payments) {
			paymentArray.add(payment.toJSONObject());
		}
		obj.put(PAYMENTS_ID, paymentArray);
		return obj;
	}
	
}
