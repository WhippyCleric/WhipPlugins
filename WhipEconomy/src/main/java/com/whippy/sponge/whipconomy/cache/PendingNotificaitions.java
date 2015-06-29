package com.whippy.sponge.whipconomy.cache;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.whippy.sponge.whipconomy.beans.Account;
import com.whippy.sponge.whipconomy.beans.Payment;
import com.whippy.sponge.whipconomy.beans.StaticsHandler;

public class PendingNotificaitions {

	private static final String PENDING_PATH = ".\\config\\plugins\\whip\\data\\whipcoonomy-notifications.json";
	private static final Object PENDINGS = "pendings";
	private static Map<String, List<Payment>> playerIdsToPayments;
	
	public static void addPayment(String playerId, Payment payment){
		List<Payment> currentPayments = playerIdsToPayments.get(playerId);
		if(currentPayments ==null){
			currentPayments = new ArrayList<Payment>();
		}
		currentPayments.add(payment);
		playerIdsToPayments.put(playerId, currentPayments);
		pushFilePendingUpdate();
	}
	
	public static List<Payment> getAndRemovePendingPayments(String userId){
		List<Payment> notifications = playerIdsToPayments.get(userId);
		if(notifications==null){
			return new ArrayList<Payment>();
		}else{
			playerIdsToPayments.remove(userId);
			pushFilePendingUpdate();
			return notifications;
		}
	}
	
	public synchronized static void refreshPendingFromFile(){
		try{
			playerIdsToPayments = new HashMap<String, List<Payment>>();
			FileReader reader = new FileReader(PENDING_PATH);
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(reader);
			JSONObject jsonObject = (JSONObject) obj;
			JSONArray accountArray = (JSONArray) jsonObject.get(PENDINGS);
			for (Object accountObj : accountArray) {
				JSONObject jsonAccount = (JSONObject) accountObj;
				String playerId = (String) jsonAccount.get(Account.PLAYER_ID);
				JSONArray arrayOfPayments = (JSONArray) jsonAccount.get(Account.PAYMENTS_ID);
				List<Payment> payments = new ArrayList<Payment>();
				for (Object paymentObj : arrayOfPayments) {
					JSONObject jsonPayment = (JSONObject) paymentObj;
					String payer = (String) jsonPayment.get(Payment.PAYER);
					String receiver = (String) jsonPayment.get(Payment.RECEIVER);
					Double amount = (Double) jsonPayment.get(Payment.AMOUNT_ID);
					String date = (String) jsonPayment.get(Payment.DATE_ID);
					payments.add(new Payment(receiver, payer, amount, date));
				}
				playerIdsToPayments.put(playerId, payments);
			}
		}catch(Exception e){
			StaticsHandler.getLogger().info("No pending notifications file found");
		}
	}
	
	
	private synchronized static void pushFilePendingUpdate(){
		try{
			JSONObject all = new JSONObject();
			FileWriter file = new FileWriter(PENDING_PATH);
			JSONArray players = new JSONArray();
			for (String playerId : playerIdsToPayments.keySet()) {
				JSONObject paymentListJson = new JSONObject();
				paymentListJson.put(Account.PLAYER_ID, playerId);
				JSONArray payments = new JSONArray();
				for (Payment payment : playerIdsToPayments.get(playerId)) {
					JSONObject paymentJSON = new JSONObject();
					paymentJSON.put(Payment.PAYER, payment.getPlayerNamePayer());
					paymentJSON.put(Payment.RECEIVER, payment.getPlayerNameReceiver());
					paymentJSON.put(Payment.AMOUNT_ID, payment.getAmount());
					paymentJSON.put(Payment.DATE_ID, payment.getDate());
					payments.add(paymentJSON);
				}
				paymentListJson.put(Account.PAYMENTS_ID, payments);	
				players.add(paymentListJson);
			}
			all.put(PENDINGS, players);
			file.write(all.toJSONString());
			file.flush();
			file.close();
		}catch(Exception e){
			e.printStackTrace();
			
		}
	}
}
