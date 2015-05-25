package com.whippy.cponge.whipconomy.orchestrator;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

import com.whippy.sponge.whipconomy.beans.Auction;
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

	public synchronized void bid(Player player) {
		synchronized(currentAuction){			
			
		}
	}
	public synchronized void bid(Player player, double bid) {
		synchronized(currentAuction){			
			
		}
	}
	public synchronized void bid(Player player, double bid, double max) {
		synchronized(currentAuction){			
			
		}
	}

}
