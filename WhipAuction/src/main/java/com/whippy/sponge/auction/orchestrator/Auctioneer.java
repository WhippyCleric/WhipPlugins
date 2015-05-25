package com.whippy.sponge.auction.orchestrator;

import org.spongepowered.api.entity.player.Player;

import com.whippy.sponge.auction.beans.Auction;

public class Auctioneer {

	
	public synchronized int pushAuctionToQueue(Auction auction){
		return 1;
	}
	
	public synchronized void cancel(Player player){
	}

	public synchronized void bid(Player player) {
		
	}
	public synchronized void bid(Player player, double bid) {
		
	}
	public synchronized void bid(Player player, double bid, double max) {
		
	}
	
}
