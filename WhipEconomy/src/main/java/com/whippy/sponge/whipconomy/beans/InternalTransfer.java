package com.whippy.sponge.whipconomy.beans;

import java.util.Date;

import org.json.simple.JSONObject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

public class InternalTransfer {

	
	public static final String AMOUNT_ID = "amount";
	public static final String DATE_ID = "date";
	public static final String IS_WITHDRAWL = "isWithdrawl";
	
	private final boolean isWithdrawl;
	private final String date;
	private final double amount;
	
	public InternalTransfer(boolean isWithdrawl, double amount) {
		this.isWithdrawl = isWithdrawl;
		this.amount = amount;
		this.date = Payment.dateFormat.format(new Date());
	}

	public InternalTransfer(boolean isWithdrawl,double amount, String date) {
		this.isWithdrawl = isWithdrawl;
		this.date = date;
		this.amount = amount;
	}

	public boolean isWithdrawl() {
		return isWithdrawl;
	}

	public String getDate() {
		return date;
	}

	public double getAmount() {
		return amount;
	}

	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		obj.put(AMOUNT_ID, amount);
		obj.put(DATE_ID, date);
		obj.put(IS_WITHDRAWL, isWithdrawl);
		return obj;
	}
	
	public Text toText(String playerName){
		StringBuilder messageBuilder = new StringBuilder();
		messageBuilder.append("- ");
		if(isWithdrawl){
			messageBuilder.append(" Withdrew ");
		}else{
			messageBuilder.append(" Banked ");
			
		}
		messageBuilder.append(StaticsHandler.getAmountWithCurrency(amount));
		if(isWithdrawl){
			return Texts.builder(date + " ").color(TextColors.YELLOW).append(Texts.builder(messageBuilder.toString()).color(TextColors.RED).build()).build();		
		}else{
			return Texts.builder(date + " ").color(TextColors.YELLOW).append(Texts.builder(messageBuilder.toString()).color(TextColors.GREEN).build()).build();		
		}
	}

}
