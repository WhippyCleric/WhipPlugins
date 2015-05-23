package com.whippy.sponge.whipconomy.beans;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
	public static final Object IS_PAYER = "isPayer";
	
	private boolean isPayer;
	private String playerNameReceiver;
	private String playerNamePayer;
	private double amount;
	private String date;
	
	
	public Payment(String playerNameReceiver, String playerNamePayer, double amount, String date, boolean isPayer) {
		this.playerNameReceiver = playerNameReceiver;
		this.playerNamePayer = playerNamePayer;
		this.amount = amount;
		this.date = date;
		this.isPayer = isPayer;
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
		obj.put(IS_PAYER, isPayer);
		return obj;
	}

	
	public Text toText(){
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
		if(isPayer){
			return Texts.builder(date + " ").color(TextColors.YELLOW).append(Texts.builder(messageBuilder.toString()).color(TextColors.RED).build()).build();		
		}else{
			return Texts.builder(date + " ").color(TextColors.YELLOW).append(Texts.builder(messageBuilder.toString()).color(TextColors.GREEN).build()).build();		
		}
	}
	
	public boolean isPayer(){
		return isPayer;
	}
	
	public String getDate() {
		return date;
	}
	
	
}
