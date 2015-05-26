package com.whippy.sponge.whipconomy.cache;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ConfigurationLoader {

	private static String currency;
	private static int decPlaces;
	private static double startingBallance;
	private static boolean appendCurrency;
	private static double maxOverdraft;
	private static int maxTransactionHistory;
	private static String auctionPrefix;
	private static boolean hasAuctions;
	private static double defaultIncrement;
	private static int minAuctionTime;
	private static int maxAuctionTime;

	public static final String CONFIG_PATH = ".\\config\\plugins\\whip\\config\\whippyconomy-config.properties";
	
	public static boolean init(){
		try {
			FileReader reader = new FileReader(CONFIG_PATH);
			Properties props = new Properties();
			props.load(reader);
			currency = props.getProperty("Currency");
			decPlaces = Integer.parseInt(props.getProperty("MacDecimalPlaces"));
			appendCurrency = Boolean.parseBoolean(props.getProperty("AppendCurrency"));
			startingBallance = Double.parseDouble(props.getProperty("StartingBalance"));
			maxOverdraft = Double.parseDouble(props.getProperty("MaxOverdraft"));
			maxTransactionHistory =Integer.parseInt(props.getProperty("MaxTransactionHistory"));

			auctionPrefix = "[WhipAuction] ";
			defaultIncrement = 1;
			minAuctionTime = 45;
			maxAuctionTime=90;
			hasAuctions = true;
			return true;
		} catch (IOException e) {
			currency = "$";
			decPlaces = 2;
			startingBallance = 0;
			maxOverdraft = 0;
			maxTransactionHistory = 30;
			auctionPrefix = "[WhipAuction] ";
			hasAuctions = true;
			defaultIncrement = 1;
			minAuctionTime = 45;
			maxAuctionTime=90;
			e.printStackTrace();
			return false;
		}
	}
	
	public static int getMinAuctionTime() {
		return minAuctionTime;
	}

	public static int getMaxAuctionTime() {
		return maxAuctionTime;
	}

	public static boolean isAppendCurrency() {
		return appendCurrency;
	}
	
	public static String getCurrency() {
		if(currency==null){
			currency  ="$";
		}
		return currency;
	}
	public static int getDecPlaces() {
		return decPlaces;
	}
	public static double getStartingBallance() {
		return startingBallance;
	}

	public static double getMaxOverdraft() {
		return maxOverdraft;
	}

	public static int getMaxTransactionHistory() {
		return maxTransactionHistory;
	}

	public static boolean hasAuctions() {
		return hasAuctions;
	}
	
	public static String getAuctionPrefix() {
		return auctionPrefix;
	}


	public static double getDefaultIncrement() {
		return defaultIncrement;
	}



}
