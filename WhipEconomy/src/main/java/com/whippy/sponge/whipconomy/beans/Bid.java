package com.whippy.sponge.whipconomy.beans;

import org.spongepowered.api.entity.player.Player;

public class Bid {

	private final Player player;
	private double bid;
	private double currentBid;
	private double maxBid;

	public Bid(Player player, double bid, double maxBid) {
		super();
		this.player = player;
		this.bid = bid;
		this.currentBid = currentBid;
		this.maxBid = maxBid;
	}

	public double getBid() {
		return bid;
	}

	public void setBid(double bid) {
		this.bid = bid;
	}

	public double getMaxBid() {
		return maxBid;
	}

	public void setMaxBid(double maxBid) {
		this.maxBid = maxBid;
	}

	public Player getPlayer() {
		return player;
	}

	public double getCurrentBid() {
		return currentBid;
	}

	public void setCurrentBid(double currentBid) {
		this.currentBid = currentBid;
	}
	
	
	
}
