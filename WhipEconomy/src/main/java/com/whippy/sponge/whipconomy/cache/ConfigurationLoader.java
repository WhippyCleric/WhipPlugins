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
			return true;
		} catch (IOException e) {
			currency = "$";
			decPlaces = 2;
			startingBallance = 0;
			maxOverdraft = 0;
			maxTransactionHistory = 30;
			e.printStackTrace();
			return false;
		}
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

}
