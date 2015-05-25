package com.whippy.cponge.whipconomy.orchestrator;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
import com.whippy.sponge.whipconomy.beans.Payment;
import com.whippy.sponge.whipconomy.beans.StaticsHandler;
import com.whippy.sponge.whipconomy.cache.ConfigurationLoader;
import com.whippy.sponge.whipconomy.cache.PendingNotificaitions;
import com.whippy.sponge.whipconomy.exceptions.GetTransactionException;
import com.whippy.sponge.whipconomy.exceptions.TransferException;

public class EconomyCache {

	private static final String ACCOUNTS = "accounts";
	private static final String MAPPINGS = "mappings";
	private static final String NAME = "Name";
	private static final String ID = "ID";
	public static final String ACCOUNTS_PATH = ".\\config\\plugins\\whip\\data\\whipcoonomy-accounts.json";
	public static final String NAME_TO_UID_MAPPINGS = ".\\config\\plugins\\whip\\data\\whipconomy-uidmappings.json";
	private static BiMap<String, String> playerNameToID;
	private static Map<String, Account> playerIdsToAccounts;

	public synchronized static void transfer(Player playerFrom, String playerTo, double amount){
		try{
			Payment receiverPayment = transferWithName(playerFrom.getName(), playerTo, amount);		
			StringBuilder messageBuilder = new StringBuilder();
			messageBuilder.append("Transfer complete, new balance: ");
			if(!ConfigurationLoader.isAppendCurrency()){
				messageBuilder.append(ConfigurationLoader.getCurrency());
				messageBuilder.append(getBalance(playerFrom.getIdentifier()));
			}else{
				messageBuilder.append(getBalance(playerFrom.getIdentifier()));
				messageBuilder.append(ConfigurationLoader.getCurrency());
			}
			playerFrom.sendMessage(Texts.builder(messageBuilder.toString()).color(TextColors.GREEN).build());
			StaticsHandler.getLogger().info("[PAYMENT]" + playerFrom.getName() +  " " + playerTo + " " + amount);
			PlayerNotifier notifier = new PlayerNotifier();
			StringBuilder paymentReceived = new StringBuilder();
			paymentReceived.append("Recevied ");
			if(!ConfigurationLoader.isAppendCurrency()){
				paymentReceived.append(ConfigurationLoader.getCurrency());
				paymentReceived.append(round(amount,ConfigurationLoader.getDecPlaces()));
			}else{
				paymentReceived.append(round(amount,ConfigurationLoader.getDecPlaces()));
				paymentReceived.append(ConfigurationLoader.getCurrency());
			}
			paymentReceived.append(" from ");
			paymentReceived.append(playerFrom.getName());
			if(!notifier.notify(Texts.builder(paymentReceived.toString()).color(TextColors.GREEN).build(), UUID.fromString(EconomyCache.getId(playerTo)))){
				PendingNotificaitions.addPayment(EconomyCache.getId(playerTo), receiverPayment);
			}

		}catch(TransferException e){
			playerFrom.sendMessage(Texts.builder(e.getMessage()).color(TextColors.RED).build());
		}
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
		Account accountTo = playerIdsToAccounts.get(playerToId);
		Account accountFrom = playerIdsToAccounts.get(playerFromId);
		if(accountFrom.getBal()+ConfigurationLoader.getMaxOverdraft()<amount){
			throw new TransferException("Player " + playerFrom + " does not have enough money to make the paymentc");			
		}
		accountTo.ammendBal(amount);
		accountFrom.ammendBal(amount * -1);
		Payment payerPayment = new Payment(playerTo, playerFrom, round(amount, ConfigurationLoader.getDecPlaces()), Payment.dateFormat.format(new Date()), true);
		Payment receiverPayment = new Payment(playerTo, playerFrom, round(amount, ConfigurationLoader.getDecPlaces()), Payment.dateFormat.format(new Date()), false);
		accountTo.addPayment(receiverPayment);
		accountFrom.addPayment(payerPayment);
		pushFileAccountsUpdate();
		return receiverPayment;
	}

	public synchronized static void getLastTransactions(Player player, int number) throws GetTransactionException{
		Account account = playerIdsToAccounts.get(player.getIdentifier());
		if(account==null){
			player.sendMessage(Texts.builder("Player " + player.getName() + " does not exist!, Since this is you it would appear to be a cock up, contact WhippyCleric").color(TextColors.RED).build());
		}	
		List<Payment> transactions = account.getPayments();
		int size = transactions.size();
		if(size < number){
			for(int i = 0; i<transactions.size(); i++){
				Payment transaction = transactions.get(i);
				player.sendMessage(transaction.toText());
			}
		}else{
			for(int i = transactions.size()-number; i<transactions.size(); i++){
				Payment transaction = transactions.get(i);
				player.sendMessage(transaction.toText());
			}
		}
	}
	
	public synchronized static void pay(String playerId, double amount) throws TransferException{
		Account account = playerIdsToAccounts.get(playerId);
		if(account==null){
			throw new TransferException("Player not found, " + playerId);
		}
		if(amount<=0){
			throw new TransferException("Cannot pay player negative amount");
		}
		account.ammendBal(amount);
		pushFileAccountsUpdate();
	}

	public synchronized static void charge(String playerId, double amount) throws TransferException{
		Account account = playerIdsToAccounts.get(playerId);
		if(account==null){
			throw new TransferException("Player not found, " + playerId);
		}
		if(amount<=0){
			throw new TransferException("Cannot charge player negative amount");
		}
		if(account.getBal()+ConfigurationLoader.getMaxOverdraft()<amount){
			throw new TransferException("Player does not have enough money to make the paymentc");			
		}
		account.ammendBal(amount*-1);
		pushFileAccountsUpdate();
	}
	
	public synchronized static boolean hasAccountByName(String playerName){
		if(playerNameToID.get(playerName)==null){
			return false;
		}
		return playerIdsToAccounts.containsKey(playerNameToID.get(playerName));
	}
	
	public synchronized static boolean hasAccountById(String playerId){
		return playerIdsToAccounts.containsKey(playerId);
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
		if(!playerIdsToAccounts.containsKey(player.getIdentifier())){
			createAccount(player.getIdentifier());
		}
		pushFileMappingsUpdate();
	}
	
	private synchronized static void createAccount(String playerId){
		Account account = new Account(playerId);
		account.ammendBal(ConfigurationLoader.getStartingBallance());
		playerIdsToAccounts.put(playerId, account);
		pushFileAccountsUpdate();
	}
	
	private synchronized static void pushFileAccountsUpdate(){
		try{
			JSONObject all = new JSONObject();
			FileWriter file = new FileWriter(ACCOUNTS_PATH);
			JSONArray accounts = new JSONArray();
			for (String playerId : playerIdsToAccounts.keySet()) {
				Account account = playerIdsToAccounts.get(playerId);
				accounts.add(account.toJSONObject());
			}
			all.put(ACCOUNTS, accounts);
			file.write(all.toJSONString());
			file.flush();
			file.close();
		}catch(Exception e){
			e.printStackTrace();
			
		}
	}
	
	private synchronized static void pushFileMappingsUpdate(){
		try {
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
		return round(playerIdsToAccounts.get(playerId).getBal(), ConfigurationLoader.getDecPlaces());
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
			e.printStackTrace();
		}
	}
	
	public synchronized static void refreshAccountsFromFile(){
		try{
			playerIdsToAccounts = new HashMap<String, Account>();
			FileReader reader = new FileReader(ACCOUNTS_PATH);
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(reader);
			JSONObject jsonObject = (JSONObject) obj;
			JSONArray accountArray = (JSONArray) jsonObject.get(ACCOUNTS);
			for (Object accountObj : accountArray) {
				JSONObject jsonAccount = (JSONObject) accountObj;
				String playerId = (String) jsonAccount.get(Account.PLAYER_ID);
				Double bal = (Double) jsonAccount.get(Account.BAL_ID);
				JSONArray arrayOfPayments = (JSONArray) jsonAccount.get(Account.PAYMENTS_ID);
				Account account = new Account(playerId);
				account.ammendBal(bal);
				for (Object paymentObj : arrayOfPayments) {
					JSONObject jsonPayment = (JSONObject) paymentObj;
					String payer = (String) jsonPayment.get(Payment.PAYER);
					String receiver = (String) jsonPayment.get(Payment.RECEIVER);
					Double amount = (Double) jsonPayment.get(Payment.AMOUNT_ID);
					String date = (String) jsonPayment.get(Payment.DATE_ID);
					boolean isPayer = (Boolean) jsonPayment.get(Payment.IS_PAYER);
					account.addPayment(new Payment(receiver, payer, amount, date, isPayer));
				}
				playerIdsToAccounts.put(playerId, account);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static String getId(String playerName){
		return playerNameToID.get(playerName);
	}

}
