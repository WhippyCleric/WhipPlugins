package com.whippy.cponge.whipconomy.orchestrator;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

import com.whippy.sponge.whipconomy.beans.Auction;
import com.whippy.sponge.whipconomy.beans.Bid;
import com.whippy.sponge.whipconomy.beans.StaticsHandler;
import com.whippy.sponge.whipconomy.cache.ConfigurationLoader;

public class Auctioneer extends Thread {

	private List<Auction> auctions;
	private final int maxAuctions;
	private Auction currentAuction;

	public Auctioneer(int maxAuctions){
		this.auctions = new ArrayList<Auction>();
		this.maxAuctions = maxAuctions;
	}

	@Override
	public void run(){
		while(ConfigurationLoader.hasAuctions()){
			if(auctions.size()>0){
				currentAuction = auctions.remove(0);
				currentAuction.run();
			}
		}
	}
	
	public synchronized void pushAuctionToQueue(Auction auction, Player player){
		if(auctions.size()<maxAuctions){
			auctions.add(auction);
			player.sendMessage(Texts.builder("Auction queued number " + auctions.indexOf(auction) + " in line").color(TextColors.GREEN).build());
		}else{
			player.sendMessage(StaticsHandler.buildTextForEcoPlugin("Auction queue is full, please try again later", TextColors.RED));
		}
	}

	public synchronized void cancel(Player player){
		synchronized(currentAuction){			
			if(currentAuction!=null){
				if(currentAuction.getPlayerId().equals(player.getIdentifier())){
					if(currentAuction.isCancelable()){
						currentAuction.interrupt();
						StaticsHandler.getGame().getServer().broadcastMessage(StaticsHandler.buildTextForEcoPlugin("Auction Cancelled",TextColors.BLUE));
					}else{
						player.sendMessage(StaticsHandler.buildTextForEcoPlugin("Auction can not be cancelled at this time",TextColors.RED));
					}
				}else{
					player.sendMessage(StaticsHandler.buildTextForEcoPlugin("This is not your auction to cancel",TextColors.RED));
				}
			}else{
				player.sendMessage(StaticsHandler.buildTextForEcoPlugin("There is no auction currently running",TextColors.RED));
			}
		}
	}

	private synchronized void bid(Bid bid) {
		synchronized(currentAuction){	
			if(currentAuction!=null){				
				if(currentAuction.isBidable()){
					if(currentAuction.getCurrentBid()==null){						
						currentAuction.setCurrentBid(bid);
						sendBidBroadcast(bid);
					}else{
						Bid currentBid = currentAuction.getCurrentBid();
						if(currentBid.getBid()>=bid.getMaxBid()){						
							bid.getPlayer().sendMessage(StaticsHandler.buildTextForEcoPlugin("Bid too low",TextColors.RED));
						}else if(currentBid.getMaxBid()>=bid.getMaxBid()){
							bid.getPlayer().sendMessage(StaticsHandler.buildTextForEcoPlugin("You have been automatically outbid",TextColors.RED));
							currentAuction.raiseCurrentBid(bid.getMaxBid());
						}else{
							bid.setCurrentBid(currentBid.getMaxBid());
							bid.setBid(currentBid.getMaxBid());
							currentAuction.setCurrentBid(bid);
							sendBidBroadcast(bid);
						}
					}
				}else{
					bid.getPlayer().sendMessage(StaticsHandler.buildTextForEcoPlugin("Cannot bid at this time",TextColors.RED));
				}
			}else{				
				bid.getPlayer().sendMessage(StaticsHandler.buildTextForEcoPlugin("There is no auction currently running",TextColors.RED));
			}
		}
	}


	private void sendBidBroadcast(Bid bid) {
		StringBuilder bidMessage = new StringBuilder();
		bidMessage.append(bid.getPlayer().getName());
		bidMessage.append(" bids ");
		bidMessage.append(bid.getBid());						
		StaticsHandler.getGame().getServer().broadcastMessage(StaticsHandler.buildTextForEcoPlugin(bidMessage.toString(),TextColors.RED));
	}
	
	public synchronized void bid(Player player, double bid) {
		
	}
	
	public synchronized void bid(Player player) {
		
	}
	public synchronized void bid(Player player, double initialBid, double max) {
		Bid bid = new Bid(player, initialBid, max);
		bid(bid);
	}

}
