package com.whippy.sponge.whipconomy.beans;

import org.spongepowered.api.entity.player.Player;

public class Auction {

	private String itemId;
	private String itemName;
	private int numberOfItem;
	private double startingBid;
	private double increment;
	private int time;
	private String playerName;
	public String getPlayerName() {
		return playerName;
	}

	public String getPlayerId() {
		return playerId;
	}
	private String playerId;
	
	public Auction(String itemId, String itemName, int numberOfItem,
			double startingBid, double increment, int time, Player player) {
		super();
		this.itemId = itemId;
		this.itemName = itemName;
		this.numberOfItem = numberOfItem;
		this.startingBid = startingBid;
		this.increment = increment;
		this.time = time;
		this.playerName = player.getName();
		this.playerId = player.getIdentifier();
	}
	
	public String getItemId() {
		return itemId;
	}
	public String getItemName() {
		return itemName;
	}
	public int getNumberOfItem() {
		return numberOfItem;
	}
	public double getStartingBid() {
		return startingBid;
	}
	public double getIncrement() {
		return increment;
	}
	public int getTime() {
		return time;
	}

}
