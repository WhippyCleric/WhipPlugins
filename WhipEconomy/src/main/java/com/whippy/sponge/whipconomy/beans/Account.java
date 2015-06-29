package com.whippy.sponge.whipconomy.beans;

import com.whippy.sponge.whipconomy.cache.ConfigurationLoader;
import com.whippy.sponge.whipconomy.cache.EconomyCache;

public class Account {

	public static final String PLAYER_ID = "playerId";
	public static final String BAL_ID = "bal";
	public static final String ACCOUNT_TYPE = "accountType";
	public static final String PAYMENTS_ID = "payments";
	private String playerId;
	private double bal;

	private AccountType accountType;
	
	public enum AccountType {
		CURRENT, SAVINGS
	}
	
	public Account(String playerId) {
		this.playerId = playerId;
		bal = 0;
	}
	
	public void ammendBal(Double amount){
		amount = EconomyCache.round(amount, ConfigurationLoader.getDecPlaces());
		bal += amount;
	}

	public String getPlayerId() {
		return playerId;
	}
	
	
	public double getBal() {
		return bal;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}
	
}
