package com.whippy.sponge.whipconomy.beans;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.whippy.sponge.whipconomy.cache.ConfigurationLoader;

public class SavingsAccount extends Account{

	public static final String TRANSFERS_ID = "transfers";
	private List<InternalTransfer> transfers;

	public SavingsAccount(String playerId) {
		super(playerId);
		transfers = new ArrayList<InternalTransfer>();
		super.setAccountType(AccountType.SAVINGS);
	}

	
	public void addInternalTransfer(InternalTransfer transfer){
		if(transfers.size()>=ConfigurationLoader.getMaxTransferHistory()){
			transfers.remove(0);
		}
		transfers.add(transfer);
	}

	public List<InternalTransfer> getInternalTransfers() {
		return transfers;
	}

	
	public JSONObject toJSONObject(){
		JSONObject obj = new JSONObject();
		obj.put(PLAYER_ID, super.getPlayerId());
		obj.put(BAL_ID, super.getBal());
		obj.put(ACCOUNT_TYPE, super.getAccountType());
		JSONArray transferArray = new JSONArray();
		for (InternalTransfer tansfer : transfers) {
			transferArray.add(tansfer.toJSONObject());
		}
		obj.put(TRANSFERS_ID, transferArray);
		return obj;
	}
}
