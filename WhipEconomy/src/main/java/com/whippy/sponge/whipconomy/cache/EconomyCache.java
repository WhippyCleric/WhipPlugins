package com.whippy.sponge.whipconomy.cache;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
	public static final String NAME_TO_UID_MAPPINGS = ".\\config\\plugins\\whip\\data\\whipconomy-uidmappings.json";
	private static BiMap<String, String> playerNameToID;
	private static Map<String, CurrentAccount> playerIdsToCurrentAccounts;
	private static Map<String, SavingsAccount> playerIdsToSavingAccounts;

	public synchronized static void transfer(Player player, String playerFrom, String playerTo, double amount){
		try {
			transferWithName(playerFrom, playerTo, amount);
			StaticsHandler.getLogger().info("[PAYMENT]" + playerFrom +  " " + playerTo + " " + amount);
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

	public synchronized static void getLastTransactions(Player player, String playerName, int number) {
		String playerID = playerNameToID.get(playerName);
		if(playerID==null||playerID.isEmpty()){
			player.sendMessage(Texts.builder("Player " + playerName + " does not exist!").color(TextColors.RED).build());
		}else{
			CurrentAccount account = playerIdsToCurrentAccounts.get(playerID);
			if(account==null){
				player.sendMessage(Texts.builder("Player " + playerName + " does not exist!").color(TextColors.RED).build());
			}else{			
				List<Payment> transactions = account.getPayments();
				if(transactions.size()==0){
					player.sendMessage(Texts.builder("No transactions found").color(TextColors.BLUE).build());
				}else{
					int size = transactions.size();
					if(size < number){
						for(int i = 0; i<transactions.size(); i++){
							Payment transaction = transactions.get(i);
							player.sendMessage(transaction.toText(player.getName()));
						}
					}else{
						for(int i = transactions.size()-number; i<transactions.size(); i++){
							Payment transaction = transactions.get(i);
							player.sendMessage(transaction.toText(player.getName()));
						}
					}
				}
			}
		}
	}
	
	public static void gift(String playerName, Double amount)throws TransferException{
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
		pushFileMappingsUpdate();
	}

	private synchronized static void createCurrentAccount(String playerId){
		CurrentAccount account = new CurrentAccount(playerId);
		account.ammendBal(ConfigurationLoader.getStartingBallance());
		playerIdsToCurrentAccounts.put(playerId, account);
		pushFileAccountsUpdate();
	}

	public synchronized static void pushFileAccountsUpdate(){
		try{
			File accountsFile = new File(ACCOUNTS_PATH);
			if(!accountsFile.exists()) {
				accountsFile.getParentFile().mkdirs();
				accountsFile.createNewFile();
			} 
			JSONObject all = new JSONObject();
			FileWriter file = new FileWriter(ACCOUNTS_PATH);
			JSONArray accounts = new JSONArray();
			for (String playerId : playerIdsToCurrentAccounts.keySet()) {
				CurrentAccount account = playerIdsToCurrentAccounts.get(playerId);
				accounts.add(account.toJSONObject());
			}
			for (String playerId : playerIdsToSavingAccounts.keySet()) {
				SavingsAccount account = playerIdsToSavingAccounts.get(playerId);
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
			FileReader reader = new FileReader(ACCOUNTS_PATH);
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
					playerIdsToSavingAccounts.put(playerId, account);
				}
			}
		}catch(Exception e){
			StaticsHandler.getLogger().info("No accounts file found");
		}
	}

	public static String getId(String playerName){
		return playerNameToID.get(playerName);
	}



}
