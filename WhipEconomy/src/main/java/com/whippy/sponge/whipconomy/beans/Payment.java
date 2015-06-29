package com.whippy.sponge.whipconomy.beans;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONObject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

import com.whippy.sponge.whipconomy.cache.ConfigurationLoader;

public class Payment {

	public static final String AMOUNT_ID = "amount";
	public static final String PAYER = "payer";
	public static final String RECEIVER = "receiver";
	public static final String DATE_ID = "date";
	public static final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	
	private String playerNameReceiver;
	private String playerNamePayer;
	private double amount;
	private String date;
	
	
	public Payment(String playerNameReceiver, String playerNamePayer, double amount, String date) {
		this.playerNameReceiver = playerNameReceiver;
		this.playerNamePayer = playerNamePayer;
		this.amount = amount;
		this.date = date;
	}
	
	public Payment(String playerNameReceiver, String playerNamePayer, double amount) {
		this.playerNameReceiver = playerNameReceiver;
		this.playerNamePayer = playerNamePayer;
		this.amount = amount;
		this.date = Payment.dateFormat.format(new Date());
	}
		
	public String getPlayerNameReceiver() {
		return playerNameReceiver;
	}
	public String getPlayerNamePayer() {
		return playerNamePayer;
	}
	public double getAmount() {
		return amount;
	}

	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		obj.put(RECEIVER, playerNameReceiver);
		obj.put(PAYER, playerNamePayer);
		obj.put(AMOUNT_ID, amount);
		obj.put(DATE_ID, date);
		return obj;
	}

	
	public Text toText(String playerName){
		StringBuilder messageBuilder = new StringBuilder();
		messageBuilder.append("- ");
		messageBuilder.append(playerNamePayer);
		messageBuilder.append(" paid ");
		messageBuilder.append(playerNameReceiver);
		messageBuilder.append(" ");
		if(!ConfigurationLoader.isAppendCurrency()){
			messageBuilder.append(ConfigurationLoader.getCurrency());
			messageBuilder.append(amount);
		}else{
			messageBuilder.append(amount);
			messageBuilder.append(ConfigurationLoader.getCurrency());
		}
		if(playerName.equals(playerNamePayer)){
			return Texts.builder(date + " ").color(TextColors.YELLOW).append(Texts.builder(messageBuilder.toString()).color(TextColors.RED).build()).build();		
		}else{
			return Texts.builder(date + " ").color(TextColors.YELLOW).append(Texts.builder(messageBuilder.toString()).color(TextColors.GREEN).build()).build();		
		}
	}
	
	
	public String getDate() {
		return date;
	}
	
	
}
