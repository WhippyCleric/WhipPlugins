package com.whippy.sponge.whipconomy.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.whippy.sponge.whipconomy.beans.Account;
import com.whippy.sponge.whipconomy.beans.Account.AccountType;
import com.whippy.sponge.whipconomy.beans.CurrentAccount;
import com.whippy.sponge.whipconomy.beans.InternalTransfer;
import com.whippy.sponge.whipconomy.beans.Payment;
import com.whippy.sponge.whipconomy.beans.SavingsAccount;
import com.whippy.sponge.whipconomy.beans.StaticsHandler;
import com.whippy.sponge.whipconomy.exceptions.TransferException;

public class EconomyCache {

	private static final String ACCOUNTS = "accounts";
	private static final String MAPPINGS = "mappings";
	private static final String NAME = "Name";
	private static final String ID = "ID";
	public static final String ACCOUNTS_PATH = ".\\config\\plugins\\whip\\data\\whipcoonomy-accounts.json";
	public static final String TEMP_ACCOUNTS_PATH = ".\\config\\plugins\\whip\\data\\whipcoonomy-accounts-temp.json";
	public static final String NAME_TO_UID_MAPPINGS = ".\\config\\plugins\\whip\\data\\whipconomy-uidmappings.json";
	private static BiMap<String, String> playerNameToID;
	private static Map<String, CurrentAccount> playerIdsToCurrentAccounts;
	private static Map<String, SavingsAccount> playerIdsToSavingsAccounts;

	public synchronized static void transfer(Player player, String playerFrom, String playerTo, double amount){
		try {
			transferWithName(playerFrom, playerTo, amount);
			StaticsHandler.getLogger().info("[PAYMENT]" + playerFrom +  " " + playerTo + " " + StaticsHandler.getAmountWithCurrency(amount));
		} catch (TransferException e) {
			if(player==null){
				StaticsHandler.getLogger().warn(e.getMessage());
			}else{
				player.sendMessage(Texts.builder(e.getMessage()).color(TextColors.RED).build());
			}
		}	
	}
	
	public synchronized static void transfer(Player playerFrom, String playerTo, double amount){
		transfer(playerFrom, playerFrom.getName(), playerTo, amount);
	}
	

	public static Set<String> getAllPlayers(){
		return playerNameToID.keySet();
	}

	public synchronized static Payment transferWithName(String playerFrom, String playerTo, double amount) throws TransferException{
		String playerToId = playerNameToID.get(playerTo);
		String playerFromId = playerNameToID.get(playerFrom);
		if(amount<=0){
			throw new TransferException("Amount must be greater than 0");
		}
		if(playerToId==null){
			throw new TransferException("Player " + playerTo + " does not exist!");			
		}
		if(playerFromId==null){
			throw new TransferException("Player " + playerFrom + " does not exist!, Since this is you it would appear to be a cock up, contact WhippyCleric");			
		}
		CurrentAccount accountTo = playerIdsToCurrentAccounts.get(playerToId);
		CurrentAccount accountFrom = playerIdsToCurrentAccounts.get(playerFromId);
		if(accountFrom.getBal()+ConfigurationLoader.getMaxOverdraft()<amount){
			throw new TransferException("Player " + playerFrom + " does not have enough money to make the paymentc");			
		}
		accountTo.ammendBal(amount);
		accountFrom.ammendBal(amount * -1);
		Payment payerPayment = new Payment(playerTo, playerFrom, round(amount, ConfigurationLoader.getDecPlaces()));
		Payment receiverPayment = new Payment(playerTo, playerFrom, round(amount, ConfigurationLoader.getDecPlaces()));
		accountTo.addPayment(receiverPayment);
		accountFrom.addPayment(payerPayment);
		pushFileAccountsUpdate();
		return receiverPayment;
	}

	public synchronized static List<Payment> getLastTransactions(Player player, String playerName) {
		String playerID = playerNameToID.get(playerName);
		if(playerID==null||playerID.isEmpty()){
			player.sendMessage(Texts.builder("Player " + playerName + " does not exist!").color(TextColors.RED).build());
		}else{
			CurrentAccount account = playerIdsToCurrentAccounts.get(playerID);
			if(account==null){
				player.sendMessage(Texts.builder("Player " + playerName + " does not exist!").color(TextColors.RED).build());
			}else{			
				List<Payment> transactions = account.getPayments();
				return transactions;
			}
		}
		return null;
	}
	
	public synchronized static void withdraw(String playerName, Double amount)throws TransferException{
		if(amount<=0){
			throw new TransferException("Can not withdraw negative amount");
		}
		String playerId = playerNameToID.get(playerName);
		if(playerId==null || playerId.isEmpty()){
			throw new TransferException(playerName + " does not exist");
		}
		SavingsAccount playerSavings = playerIdsToSavingsAccounts.get(playerId);
		CurrentAccount playerCurrent = playerIdsToCurrentAccounts.get(playerId);
		
		if(playerSavings==null || playerCurrent ==null){
			throw new TransferException("Unable to find either savings or current account for " + playerName);
		}
		
		if(playerSavings.getBal()<amount){
			throw new TransferException("Not enough savings to withdraw " + StaticsHandler.getAmountWithCurrency(amount) + ". Current savings ballance is " + playerSavings.getBal());			
		}
		
		playerSavings.ammendBal(amount*-1);
		playerCurrent.ammendBal(amount);
		InternalTransfer transfer = new InternalTransfer(true, amount);
		playerSavings.addInternalTransfer(transfer );
		pushFileAccountsUpdate();
		
	}
	public synchronized static void bank(String playerName, Double amount)throws TransferException{
		if(amount<=0){
			throw new TransferException("Can not bank negative amount");
		}
		String playerId = playerNameToID.get(playerName);
		if(playerId==null || playerId.isEmpty()){
			throw new TransferException(playerName + " does not exist");
		}
		SavingsAccount playerSavings = playerIdsToSavingsAccounts.get(playerId);
		CurrentAccount playerCurrent = playerIdsToCurrentAccounts.get(playerId);
		
		if(playerSavings==null || playerCurrent ==null){
			throw new TransferException("Unable to find either savings or current account for " + playerName);
		}
		
		if(playerCurrent.getBal()<amount){
			throw new TransferException("Not enough money to bank"+ StaticsHandler.getAmountWithCurrency(amount) +". Current savings ballance is " + playerCurrent.getBal());			
		}
		
		playerSavings.ammendBal(amount);
		playerCurrent.ammendBal(amount*-1);
		InternalTransfer transfer = new InternalTransfer(false, amount);
		playerSavings.addInternalTransfer(transfer );
		pushFileAccountsUpdate();
	}
	
	
	
	public synchronized static List<InternalTransfer> getSavingsHistory(Player player){
		String playerID = playerNameToID.get(player.getName());
		if(playerID==null||playerID.isEmpty()){
			player.sendMessage(Texts.builder("Player " + player.getName() + " does not exist!").color(TextColors.RED).build());
		}else{
			SavingsAccount account = playerIdsToSavingsAccounts.get(playerID);
			if(account==null){
				player.sendMessage(Texts.builder("Player " + player.getName() + " does not exist!").color(TextColors.RED).build());
			}else{			
				List<InternalTransfer> transactions = account.getInternalTransfers();
				return transactions;
			}
		}
		return null;
	}

	
	public synchronized static void gift(String playerName, Double amount)throws TransferException{
		payWithoutPush(getId(playerName), amount);
		pushFileAccountsUpdate();
	}

	public synchronized static void pay(String playerId, double amount) throws TransferException{
		payWithoutPush(playerId, amount);
		pushFileAccountsUpdate();
	}
	public synchronized static void payWithoutPush(String playerId, double amount) throws TransferException{
		Account account = playerIdsToCurrentAccounts.get(playerId);
		if(account==null){
			throw new TransferException("Player not found, " + playerId);
		}
		if(amount<=0){
			throw new TransferException("Cannot pay player negative amount");
		}
		account.ammendBal(amount);
	}

	public synchronized static void charge(String playerId, double amount) throws TransferException{
		chargeWithoutPush(playerId, amount);
		pushFileAccountsUpdate();
	}
	public synchronized static void chargeWithoutPush(String playerId, double amount) throws TransferException{
		Account account = playerIdsToCurrentAccounts.get(playerId);
		if(account==null){
			throw new TransferException("Player not found, " + playerId);
		}
		if(amount<=0){
			throw new TransferException("Cannot charge player negative amount");
		}
		if(account.getBal()+ConfigurationLoader.getMaxOverdraft()<amount){
			throw new TransferException("Player does not have enough money to make the payment");			
		}
		account.ammendBal(amount*-1);
	}

	public synchronized static boolean hasAccountByName(String playerName){
		if(playerNameToID.get(playerName)==null){
			return false;
		}
		return playerIdsToCurrentAccounts.containsKey(playerNameToID.get(playerName));
	}

	public synchronized static boolean hasAccountById(String playerId){
		return playerIdsToCurrentAccounts.containsKey(playerId);
	}

	public synchronized static void updatePlayerMapping(Player player){
		if(playerNameToID.containsValue(player.getIdentifier())){
			if(playerNameToID.get(player.getName()).equals(player.getIdentifier())){
				//Nothing to do, player and uid match
			}else{
				//Player name has changed
				playerNameToID.forcePut(player.getName(), player.getIdentifier());
			}
		}else{
			if(playerNameToID.containsKey(player.getName())){
				//Our name currently has another players uid mapped to it, oh shit
				playerNameToID.remove(player.getName());
				playerNameToID.forcePut(player.getName(), player.getIdentifier());
			}else{
				//A new player
				playerNameToID.put(player.getName(), player.getIdentifier());
			}
		}
		if(!playerIdsToCurrentAccounts.containsKey(player.getIdentifier())){
			createCurrentAccount(player.getIdentifier());
		}
		if(ConfigurationLoader.isSavingsEnabled()){			
			if(!playerIdsToSavingsAccounts.containsKey(player.getIdentifier())){
				createSavingsAccount(player.getIdentifier());
			}
		}
		pushFileMappingsUpdate();
	}

	private synchronized static void createCurrentAccount(String playerId){
		CurrentAccount account = new CurrentAccount(playerId);
		account.ammendBal(ConfigurationLoader.getStartingBallance());
		playerIdsToCurrentAccounts.put(playerId, account);
		pushFileAccountsUpdate();
	}
	private synchronized static void createSavingsAccount(String playerId){
		SavingsAccount account = new SavingsAccount(playerId);
		playerIdsToSavingsAccounts.put(playerId, account);
		pushFileAccountsUpdate();
	}

	public synchronized static void pushFileAccountsUpdate(){
		try{
			File accountsFile = new File(ACCOUNTS_PATH);
			if(!accountsFile.exists()) {
				accountsFile.getParentFile().mkdirs();
				accountsFile.createNewFile();
			} 
			File tempAccountsFile = new File(TEMP_ACCOUNTS_PATH);
			if(!tempAccountsFile.exists()) {
				tempAccountsFile.getParentFile().mkdirs();
				tempAccountsFile.createNewFile();
			} 
			
			FileChannel src = new FileInputStream(accountsFile).getChannel();
			FileChannel dest = new FileOutputStream(tempAccountsFile).getChannel();
			dest.transferFrom(src, 0, src.size());
			
			
			JSONObject all = new JSONObject();
			FileWriter fileWriter = new FileWriter(ACCOUNTS_PATH);
			JSONArray accounts = new JSONArray();
			for (String playerId : playerIdsToCurrentAccounts.keySet()) {
				CurrentAccount account = playerIdsToCurrentAccounts.get(playerId);
				accounts.add(account.toJSONObject());
			}
			for (String playerId : playerIdsToSavingsAccounts.keySet()) {
				SavingsAccount account = playerIdsToSavingsAccounts.get(playerId);
				accounts.add(account.toJSONObject());
			}
			all.put(ACCOUNTS, accounts);
			fileWriter.write(all.toJSONString());
			fileWriter.flush();
			fileWriter.close();
			tempAccountsFile.delete();
			src.close();
			dest.close();
		}catch(Exception e){
			e.printStackTrace();

		}
	}

	public synchronized static void pushFileMappingsUpdate(){
		try {
			File accountsFile = new File(NAME_TO_UID_MAPPINGS);
			if(!accountsFile.exists()) {
				accountsFile.getParentFile().mkdirs();
				accountsFile.createNewFile();
			} 
			JSONObject all = new JSONObject();
			FileWriter file = new FileWriter(NAME_TO_UID_MAPPINGS);
			JSONObject mappings = new JSONObject();
			JSONArray arrayOfMappings = new JSONArray();
			for (String playerName : playerNameToID.keySet()) {
				JSONObject mapping = new JSONObject();
				mapping.put(ID, playerNameToID.get(playerName));
				mapping.put(NAME, playerName);
				arrayOfMappings.add(mapping);
			}
			mappings.put(MAPPINGS, arrayOfMappings);
			file.write(mappings.toJSONString());
			file.flush();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized static double getBalance(String playerId){
		return round(playerIdsToCurrentAccounts.get(playerId).getBal(), ConfigurationLoader.getDecPlaces());
	}

	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public synchronized static void refreshMappingsFromFile(){
		try {
			playerNameToID = HashBiMap.create();
			FileReader reader = new FileReader(NAME_TO_UID_MAPPINGS);
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(reader);
			JSONObject jsonObject = (JSONObject) obj;
			JSONArray arrayMappings = (JSONArray) jsonObject.get(MAPPINGS);
			for (Object mapping : arrayMappings) {
				JSONObject mappingObj = (JSONObject) mapping;
				String playerId = (String) mappingObj.get(ID);
				String playerName = (String) mappingObj.get(NAME);
				playerNameToID.put(playerName, playerId);
			}
		} catch (IOException | ParseException e) {
			StaticsHandler.getLogger().info("No configuration found");
		}
	}

	public synchronized static void refreshAccountsFromFile(){
		try{
			playerIdsToCurrentAccounts = new HashMap<String, CurrentAccount>();
			playerIdsToSavingsAccounts = new HashMap<String, SavingsAccount>();
			FileReader reader = new FileReader(ACCOUNTS_PATH);
			extractAccounts(reader);
		}catch(Exception e){
			StaticsHandler.getLogger().info("No accounts file found or file is corrupted, trying to load from temporary");
			File tempAccountsFile = new File(TEMP_ACCOUNTS_PATH);
			if(tempAccountsFile.exists()) {
				FileReader reader;
				try {
					reader = new FileReader(TEMP_ACCOUNTS_PATH);
					extractAccounts(reader);
				} catch (IOException e1) {
					StaticsHandler.getLogger().info("Error loading temporary accounts file found");				
				} catch (ParseException e1) {
					StaticsHandler.getLogger().error("Unable to parse temporary accounts file: ", e1);				
				}		
			}else{
				StaticsHandler.getLogger().info("No temporary accounts file found");				
			}
		}
	}

	private static void extractAccounts(FileReader reader) throws IOException, ParseException {
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(reader);
		JSONObject jsonObject = (JSONObject) obj;
		JSONArray accountArray = (JSONArray) jsonObject.get(ACCOUNTS);
		for (Object accountObj : accountArray) {
			JSONObject jsonAccount = (JSONObject) accountObj;
			String playerId = (String) jsonAccount.get(Account.PLAYER_ID);
			Double bal = (Double) jsonAccount.get(Account.BAL_ID);
			AccountType accountType = AccountType.valueOf((String) jsonAccount.get(Account.ACCOUNT_TYPE));
			if(AccountType.CURRENT.equals(accountType)){					
				JSONArray arrayOfPayments = (JSONArray) jsonAccount.get(CurrentAccount.PAYMENTS_ID);
				CurrentAccount account = new CurrentAccount(playerId);
				account.ammendBal(bal);
				for (Object paymentObj : arrayOfPayments) {
					JSONObject jsonPayment = (JSONObject) paymentObj;
					String payer = (String) jsonPayment.get(Payment.PAYER);
					String receiver = (String) jsonPayment.get(Payment.RECEIVER);
					Double amount = (Double) jsonPayment.get(Payment.AMOUNT_ID);
					String date = (String) jsonPayment.get(Payment.DATE_ID);
					account.addPayment(new Payment(receiver, payer, amount, date));
				}
				playerIdsToCurrentAccounts.put(playerId, account);
			}else{
				JSONArray arrayOfTransfers = (JSONArray) jsonAccount.get(SavingsAccount.TRANSFERS_ID);
				SavingsAccount account = new SavingsAccount(playerId);
				account.ammendBal(bal);
				for (Object transferObj : arrayOfTransfers) {
					JSONObject jsonTransfer = (JSONObject) transferObj;
					Double amount = (Double) jsonTransfer.get(InternalTransfer.AMOUNT_ID);
					String date = (String) jsonTransfer.get(InternalTransfer.DATE_ID);
					Boolean isWithdrawl = (Boolean) jsonTransfer.get(InternalTransfer.IS_WITHDRAWL);
					account.addInternalTransfer(new InternalTransfer(isWithdrawl, amount, date));
				}
				playerIdsToSavingsAccounts.put(playerId, account);
			}
		}
	}

	public static String getId(String playerName){
		return playerNameToID.get(playerName);
	}



}
