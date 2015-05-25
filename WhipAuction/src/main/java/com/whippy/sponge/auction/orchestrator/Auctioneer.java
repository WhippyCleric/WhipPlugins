package com.whippy.sponge.auction.orchestrator;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

import com.whippy.sponge.auction.beans.Auction;

public class Auctioneer {

	
	private List<Auction> auctions;
	private final int maxAuctions;
	
	public Auctioneer(int maxAuctions){
		this.auctions = new ArrayList<Auction>();
		this.maxAuctions = maxAuctions;
	}
	
	public synchronized void pushAuctionToQueue(Auction auction, Player player){
		if(auctions.size()<maxAuctions){
			auctions.add(auction);
			player.sendMessage(Texts.builder("Auction queued number " + auctions.indexOf(auction) + " in line").color(TextColors.GREEN).build());
		}else{
			player.sendMessage(Texts.builder("Auction queue is full, please try again later").color(TextColors.RED).build());
		}
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
