package com.whippy.sponge.whipconomy.cache;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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
	private static int maxSavingsHistory;
	private static boolean isSavingsEnabled;
	private static List<String> balAliases;

	private static List<String> bankAliases;
	private static List<String> chargeAliases;
	private static List<String> giftAliases;
	private static List<String> payAliases;
	private static List<String> savingsHistoryAliases;
	private static List<String> accHistoryAliases;
	private static List<String> transferAliases;
	private static List<String> withdrawAliases;
	
	public static final String CONFIG_PATH = ".\\config\\plugins\\whip\\config\\whippyconomy-config.properties";
	public static final String ALIASES_PATH = ".\\config\\plugins\\whip\\config\\whippyconomy-alias.properties";
	
	public static boolean initAliases(){
		try{
			FileReader reader = new FileReader(ALIASES_PATH);
			Properties props = new Properties();
			props.load(reader);
			setAliasValues(props);
			return true;
		} catch (IOException e) {
			Properties props = new Properties();
			props.put("bank.aliases", "");
			props.put("bank.only.aliases", "false");
			props.put("bal.aliases", "");
			props.put("bal.only.aliases", "false");
			props.put("charge.aliases", "");
			props.put("charge.only.aliases", "false");
			props.put("gift.aliases", "");
			props.put("gift.only.aliases", "false");
			props.put("pay.aliases", "");
			props.put("pay.only.aliases", "false");
			props.put("savings.history.aliases", "");
			props.put("savings.history.only.aliases", "false");
			props.put("acc.history.aliases", "");
			props.put("acc.history.only.aliases", "false");
			props.put("transfer.aliases", "");
			props.put("transfer.only.aliases", "false");
			props.put("withdraw.aliases", "");
			props.put("withdraw.only.aliases", "false");
		
			setAliasValues(props);
			try{
				File accountsFile = new File(ALIASES_PATH);
				if(!accountsFile.exists()) {
					accountsFile.getParentFile().mkdirs();
				    accountsFile.createNewFile();
				} 
				FileWriter file = new FileWriter(ALIASES_PATH);
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
	
	public static boolean initProperties(){
		try {
			FileReader reader = new FileReader(CONFIG_PATH);
			Properties props = new Properties();
			props.load(reader);
			setPropertyValues(props);
			return true;
		} catch (IOException e) {
			Properties props = new Properties();
			props.put("currency", "$");
			props.put("decPlaces", "2");
			props.put("appendCurrency", false);
			props.put("startingBalance", "0");
			props.put("maxOverdraft", "0");
			props.put("maxTransactionHistory", "30");
			props.put("maxSavingsHistory", "30");
			props.put("hasAuctions", true);
			props.put("auctionPrefix", "[WhipAuction] ");
			props.put("defaultIncrement", "1");
			props.put("minAuctionTime", "45");
			props.put("maxAuctionTime", "90");
			props.put("defaultAuctionTime", "60");
			props.put("maxAuctions", "4");
			props.put("isSavingsEnabled", true);
			setPropertyValues(props);
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
	
	private static void setPropertyValues(Properties props){
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
		maxSavingsHistory = Integer.parseInt(props.getProperty("maxSavingsHistory"));
	}
	private static void setAliasValues(Properties props){
		bankAliases = getAlias(props, "bank.aliases", "bank.only.aliases", "bank");
		balAliases = getAlias(props, "bal.aliases", "bal.only.aliases", "bal");
		chargeAliases = getAlias(props, "charge.aliases", "charge.only.aliases", "charge");
		giftAliases = getAlias(props, "gift.aliases", "gift.only.aliases", "gift");
		payAliases = getAlias(props, "pay.aliases", "pay.only.aliases", "pay");
		savingsHistoryAliases = getAlias(props, "savings.history.aliases", "savings.history.only.aliases", "savingsHistory");
		accHistoryAliases = getAlias(props, "acc.history.aliases", "acc.history.only.aliases", "accHistory");
		transferAliases = getAlias(props, "transfer.aliases", "transfer.only.aliases", "transfer");
		withdrawAliases = getAlias(props, "withdraw.aliases", "withdraw.only.aliases", "withdraw");
	}
	
	private static List<String> getAlias(Properties props, String aliasProperty, String aliasOnlyProperty, String defaultCommand){
		List<String> aliases = Arrays.asList(props.getProperty(aliasProperty).split(","));
		if(!(props.getProperty(aliasOnlyProperty)=="true")){
			aliases.add(defaultCommand);
		}
		aliases.remove("");
		return aliases;
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

	public static int getMaxSavingsHistory() {
		return maxSavingsHistory;
	}

	public static boolean isSavingsEnabled() {
		return isSavingsEnabled;
	}

	public static List<String> getBalAliases() {
		return balAliases;
	}

	public static List<String> getBankAliases() {
		return bankAliases;
	}

	public static List<String> getChargeAliases() {
		return chargeAliases;
	}

	public static List<String> getGiftAliases() {
		return giftAliases;
	}

	public static List<String> getPayAliases() {
		return payAliases;
	}

	public static List<String> getSavingsHistoryAliases() {
		return savingsHistoryAliases;
	}

	public static List<String> getAccHistoryAliases() {
		return accHistoryAliases;
	}

	public static List<String> getTransferAliases() {
		return transferAliases;
	}

	public static List<String> getWithdrawAliases() {
		return withdrawAliases;
	}





}
