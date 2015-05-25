package com.whippy.sponge.whipconomy.beans;

import org.spongepowered.api.entity.player.Player;

public class Bid {

	private final Player player;
	private int bid;
	private int maxBid;

	public Bid(Player player, int bid, int maxBid) {
		super();
		this.player = player;
		this.bid = bid;
		this.maxBid = maxBid;
	}

	public int getBid() {
		return bid;
	}

	public void setBid(int bid) {
		this.bid = bid;
	}

	public int getMaxBid() {
		return maxBid;
	}

	public void setMaxBid(int maxBid) {
		this.maxBid = maxBid;
	}

	public Player getPlayer() {
		return player;
	}
	
	
	
}
