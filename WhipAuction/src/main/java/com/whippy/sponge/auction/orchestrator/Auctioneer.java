package com.whippy.sponge.auction.orchestrator;

import org.spongepowered.api.entity.player.Player;

import com.whippy.sponge.auction.beans.Auction;

public class Auctioneer {

	
	public int pushAuctionToQueue(Auction auction){
		return 1;
	}
	
	public boolean cancel(Player player){
		return true;
	}
	
}
