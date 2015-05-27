package com.whippy.sponge.whipconomy.cache;

import java.util.HashMap;
import java.util.Map;

import org.spongepowered.api.item.inventory.ItemStack;

public class AuctionCache {

	private String currentPlayerId;
	private double currentMaxBid;
	
	public Map<String, ItemStack> playerToItems;
	
	public AuctionCache(){
		playerToItems = new HashMap<String, ItemStack>();
	}
	
	public void addPlayerAndItem(String playerId, ItemStack itemStack){
		playerToItems.put(playerId, itemStack);
	}

	public String getCurrentPlayerId() {
		return currentPlayerId;
	}

	public void setCurrentPlayerId(String currentPlayerId) {
		this.currentPlayerId = currentPlayerId;
	}

	public double getCurrentMaxBid() {
		return currentMaxBid;
	}

	public void setCurrentMaxBid(double currentMaxBid) {
		this.currentMaxBid = currentMaxBid;
	}
	
	public void settleCache(String auctioneerId){
		
	}
	
	
	
}
