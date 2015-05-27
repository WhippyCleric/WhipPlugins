package com.whippy.sponge.whipconomy.orchestrator;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.format.TextColors;

import com.whippy.sponge.whipconomy.beans.Auction;
import com.whippy.sponge.whipconomy.beans.Bid;
import com.whippy.sponge.whipconomy.beans.StaticsHandler;
import com.whippy.sponge.whipconomy.cache.ConfigurationLoader;
import com.whippy.sponge.whipconomy.cache.EconomyCache;

public class Auctioneer extends Thread {

	private List<Auction> auctions;
	private final int maxAuctions;
	private Auction currentAuction;

	public Auctioneer(){
		this.auctions = new ArrayList<Auction>();
		this.maxAuctions = ConfigurationLoader.getMaxAuctions();
	}



	public Auction getCurrentAuction() {
		return currentAuction;
	}



	public void setCurrentAuction(Auction currentAuction) {
		this.currentAuction = currentAuction;
	}



	public List<Auction> getAuctions() {
		return auctions;
	}



	public synchronized void pushAuctionToQueue(Auction auction, Player player){
		if(auctions.size()<maxAuctions){
			auctions.add(auction);
			StringBuilder auctionNotification = new StringBuilder();
			auctionNotification .append("Auction queued number ");
			auctionNotification .append(auctions.indexOf(auction));
			auctionNotification .append(" in line");
			player.sendMessage(StaticsHandler.buildTextForEcoPlugin(auctionNotification.toString(), TextColors.BLUE));
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
					if(bid.getMaxBid()>=currentAuction.getStartingBid()){
						if(currentAuction.getCurrentBid()==null){
							if(bid.getBid()<currentAuction.getStartingBid()){
								bid.setBid(currentAuction.getStartingBid());
								bid.setCurrentBid(currentAuction.getStartingBid());
							}
							currentAuction.setCurrentBid(bid);
							sendBidBroadcast(bid);
						}else{
							Bid currentBid = currentAuction.getCurrentBid();
							double increment  =currentAuction.getIncrement();
							//They're base bid is higher than the current
							if(bid.getBid()>currentBid.getCurrentBid()+increment){
								//They also have outbid the max bid
								if(bid.getBid()>currentBid.getMaxBid()){
									currentAuction.setCurrentBid(bid);
									sendBidBroadcast(bid);
								}else{
									//They are lower than the max, how about their max bid?
									if(bid.getMaxBid()>currentBid.getMaxBid()){
										//Their max is higher
										bid.setBid(currentBid.getCurrentBid()+currentAuction.getIncrement());
										bid.setCurrentBid(currentBid.getCurrentBid()+currentAuction.getIncrement());
										currentAuction.setCurrentBid(bid);
										sendBidBroadcast(bid);
									}else{
										//Current max is higher
										bid.getPlayer().sendMessage(StaticsHandler.buildTextForEcoPlugin("You have been automatically outbid",TextColors.RED));
										currentAuction.raiseCurrentBid(bid.getMaxBid());
										sendIncreasedBidBroadcast(bid.getMaxBid());
									}
								}
							//Their bid is too low, however how is their maximum?
							}else if(bid.getMaxBid()>currentBid.getCurrentBid()+increment){
								//New max bid is higher than current max bid
								if(bid.getMaxBid()>currentBid.getMaxBid()){
									//New bid is better, do we need the increment?
									if(currentBid.getCurrentBid()>currentBid.getMaxBid()-increment){										
										bid.setBid(currentBid.getCurrentBid()+currentAuction.getIncrement());
										bid.setCurrentBid(currentBid.getCurrentBid()+currentAuction.getIncrement());
									}else{										
										bid.setBid(currentBid.getMaxBid());
										bid.setCurrentBid(currentBid.getMaxBid());
									}
									currentAuction.setCurrentBid(bid);
									sendBidBroadcast(bid);
								}else{
									//current max bid is higher, current bid is raised to match max bid
									bid.getPlayer().sendMessage(StaticsHandler.buildTextForEcoPlugin("You have been automatically outbid",TextColors.RED));
									currentAuction.raiseCurrentBid(bid.getMaxBid());
									sendIncreasedBidBroadcast(bid.getMaxBid());
								}
							}else{
								//The max bid specified does not cover the current bid + increment
								bid.getPlayer().sendMessage(StaticsHandler.buildTextForEcoPlugin("Bid too low",TextColors.RED));
							}
						}
					}else{
						bid.getPlayer().sendMessage(StaticsHandler.buildTextForEcoPlugin("Bid too low",TextColors.RED));
					}
				}else{
					bid.getPlayer().sendMessage(StaticsHandler.buildTextForEcoPlugin("Cannot bid at this time",TextColors.RED));
				}
			}else{				
				bid.getPlayer().sendMessage(StaticsHandler.buildTextForEcoPlugin("There is no auction currently running",TextColors.RED));
			}
		}
	}


	private void sendIncreasedBidBroadcast(double bid){
		StringBuilder bidMessage = new StringBuilder();
		bidMessage.append("Bid has been raised to ");
		bidMessage.append(bid);						
		StaticsHandler.getGame().getServer().broadcastMessage(StaticsHandler.buildTextForEcoPlugin(bidMessage.toString(),TextColors.BLUE));
	}
	
	private void sendBidBroadcast(Bid bid) {
		StringBuilder bidMessage = new StringBuilder();
		bidMessage.append(bid.getPlayer().getName());
		bidMessage.append(" bids ");
		bidMessage.append(bid.getBid());						
		StaticsHandler.getGame().getServer().broadcastMessage(StaticsHandler.buildTextForEcoPlugin(bidMessage.toString(),TextColors.BLUE));
	}

	public synchronized void bid(Player player, double initialBid) {
		synchronized (currentAuction){
			initialBid = EconomyCache.round(initialBid, ConfigurationLoader.getDecPlaces());
			Bid bid = new Bid(player, initialBid, initialBid);
			bid(bid);
		}
	}

	public synchronized void bid(Player player) {
		synchronized (currentAuction){
			Bid currentBid = currentAuction.getCurrentBid();
			double increment = currentAuction.getIncrement();
			Bid bid = new Bid(player, currentBid.getMaxBid() + increment, currentBid.getMaxBid() + increment);
			bid(bid);
		}
	}
	public synchronized void bid(Player player, double initialBid, double max) {
		if(max>=initialBid){
			initialBid = EconomyCache.round(initialBid, ConfigurationLoader.getDecPlaces());
			max = EconomyCache.round(max, ConfigurationLoader.getDecPlaces());
			Bid bid = new Bid(player, initialBid, max);
			bid(bid);
		}else{
			player.sendMessage(StaticsHandler.buildTextForEcoPlugin("Max bid can not be lower than intial bid",TextColors.RED));
		}
	}

}
