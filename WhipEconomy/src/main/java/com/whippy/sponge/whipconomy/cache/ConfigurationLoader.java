package com.whippy.sponge.whipconomy.cache;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
	private static int maxAuctions;
	private static int defaultAuctionTime;
	private static int maxTransferHistory;
	private static boolean isSavingsEnabled;
	public static final String CONFIG_PATH = ".\\config\\plugins\\whip\\config\\whippyconomy-config.properties";
	
	public static boolean init(){
		try {
			FileReader reader = new FileReader(CONFIG_PATH);
			Properties props = new Properties();
			props.load(reader);
			setValues(props);

			return true;
		} catch (IOException e) {
			Properties props = new Properties();
			props.put("currency", "$");
			props.put("decPlaces", "2");
			props.put("appendCurrency", false);
			props.put("startingBalance", "0");
			props.put("maxOverdraft", "0");
			props.put("maxTransactionHistory", "30");
			props.put("maxTransferHistory", "30");
			props.put("hasAuctions", true);
			props.put("auctionPrefix", "[WhipAuction] ");
			props.put("defaultIncrement", "1");
			props.put("minAuctionTime", "45");
			props.put("maxAuctionTime", "90");
			props.put("defaultAuctionTime", "60");
			props.put("maxAuctions", "4");
			props.put("isSavingsEnabled", true);
			setValues(props);
			try{
				File accountsFile = new File(CONFIG_PATH);
				if(!accountsFile.exists()) {
					accountsFile.getParentFile().mkdirs();
				    accountsFile.createNewFile();
				} 
				FileWriter file = new FileWriter(CONFIG_PATH);
				StringBuilder propertyBuilder = new StringBuilder();
				for (Object key : props.keySet()) {
					propertyBuilder.append(key);
					propertyBuilder.append("=");
					propertyBuilder.append(props.get(key));
					propertyBuilder.append("\n");
				}
				file.write(propertyBuilder.toString());
				file.flush();
				file.close();
			}catch(Exception e1){
				e1.printStackTrace();
				
			}
			return false;
		}
	}
	
	private static void setValues(Properties props){
		currency = props.getProperty("currency");
		decPlaces = Integer.parseInt(props.getProperty("decPlaces"));
		appendCurrency = Boolean.parseBoolean(props.getProperty("appendCurrency"));
		startingBallance = Double.parseDouble(props.getProperty("startingBalance"));
		maxOverdraft = Double.parseDouble(props.getProperty("maxOverdraft"));
		maxTransactionHistory =Integer.parseInt(props.getProperty("maxTransactionHistory"));
		hasAuctions = Boolean.parseBoolean(props.getProperty("hasAuctions"));
		isSavingsEnabled = Boolean.parseBoolean(props.getProperty("isSavingsEnabled"));
		auctionPrefix = props.getProperty("auctionPrefix");
		defaultIncrement= Double.parseDouble(props.getProperty("defaultIncrement"));
		minAuctionTime =Integer.parseInt(props.getProperty("minAuctionTime"));
		maxAuctionTime =Integer.parseInt(props.getProperty("maxAuctionTime"));
		maxAuctions =Integer.parseInt(props.getProperty("maxAuctions"));
		defaultAuctionTime = Integer.parseInt(props.getProperty("defaultAuctionTime"));
		maxTransferHistory = Integer.parseInt(props.getProperty("maxTransferHistory"));
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

	public static int getMaxAuctions() {
		return maxAuctions;
	}

	public static int getDefaultAuctionTime() {
		return defaultAuctionTime;
	}

	public static int getMaxTransferHistory() {
		return maxTransferHistory;
	}

	public static boolean isSavingsEnabled() {
		return isSavingsEnabled;
	}




}
