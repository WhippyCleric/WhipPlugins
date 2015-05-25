package com.whippy.cponge.whipconomy.orchestrator;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.Game;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

import com.whippy.sponge.whipconomy.beans.Auction;
import com.whippy.sponge.whipconomy.beans.Bid;
import com.whippy.sponge.whipconomy.beans.StaticsHandler;

public class Auctioneer extends Thread {

	private List<Auction> auctions;
	private final int maxAuctions;
	private Auction currentAuction;
	private Bid currentBid;

	public Auctioneer(int maxAuctions){
		this.auctions = new ArrayList<Auction>();
		this.maxAuctions = maxAuctions;
	}

	@Override
	public void run(){
		if(!auctions.isEmpty()){
			currentAuction = auctions.remove(0);
			Game game = StaticsHandler.getGame();

			StringBuilder auctionStartingBuilder = new StringBuilder();
			auctionStartingBuilder.append(StaticsHandler.getAuctionPrefix());
			auctionStartingBuilder.append(currentAuction.getPlayerName());
			auctionStartingBuilder.append(" is auctioning ");
			auctionStartingBuilder.append(currentAuction.getNumberOfItem());
			auctionStartingBuilder.append(" ");
			auctionStartingBuilder.append(currentAuction.getItemName());
			auctionStartingBuilder.append(". Starting bid: ");
			auctionStartingBuilder.append(currentAuction.getStartingBid());
			auctionStartingBuilder.append(". Increment: ");
			auctionStartingBuilder.append(currentAuction.getIncrement());
			auctionStartingBuilder.append(". This auction will last ");
			auctionStartingBuilder.append(currentAuction.getTime());
			auctionStartingBuilder.append(" seconds.");

			int time = currentAuction.getTime();
			game.getServer().broadcastMessage(Texts.builder(auctionStartingBuilder.toString()).color(TextColors.BLUE).build());
			
			try {
				Thread.sleep(time-30);
				StringBuilder auctionNotification = new StringBuilder();
				auctionStartingBuilder.append(StaticsHandler.getAuctionPrefix());
				auctionNotification.append("30 seconds remaining");
				game.getServer().broadcastMessage(Texts.builder(auctionNotification.toString()).color(TextColors.BLUE).build());
				
				Thread.sleep(20);
				auctionNotification = new StringBuilder();
				auctionStartingBuilder.append(StaticsHandler.getAuctionPrefix());
				auctionNotification.append("10 seconds remaining");
				game.getServer().broadcastMessage(Texts.builder(auctionNotification.toString()).color(TextColors.BLUE).build());
				
				Thread.sleep(7);
				auctionNotification = new StringBuilder();
				auctionStartingBuilder.append(StaticsHandler.getAuctionPrefix());
				auctionNotification.append("3 seconds remaining");
				game.getServer().broadcastMessage(Texts.builder(auctionNotification.toString()).color(TextColors.BLUE).build());

				Thread.sleep(1);
				auctionNotification = new StringBuilder();
				auctionStartingBuilder.append(StaticsHandler.getAuctionPrefix());
				auctionNotification.append("2 seconds remaining");
				game.getServer().broadcastMessage(Texts.builder(auctionNotification.toString()).color(TextColors.BLUE).build());

				Thread.sleep(1);
				auctionNotification = new StringBuilder();
				auctionStartingBuilder.append(StaticsHandler.getAuctionPrefix());
				auctionNotification.append("1 second remaining");
				game.getServer().broadcastMessage(Texts.builder(auctionNotification.toString()).color(TextColors.BLUE).build());
				
				Thread.sleep(1);
				if(currentBid==null){
					auctionNotification = new StringBuilder();
					auctionStartingBuilder.append(StaticsHandler.getAuctionPrefix());
					auctionNotification.append("Auction completed with no bids");
					game.getServer().broadcastMessage(Texts.builder(auctionNotification.toString()).color(TextColors.BLUE).build());
				}else{
					auctionNotification = new StringBuilder();
					auctionStartingBuilder.append(StaticsHandler.getAuctionPrefix());
					auctionStartingBuilder.append(currentBid.getPlayer().getName());
					auctionStartingBuilder.append("won the auction with a bid of ");
					auctionStartingBuilder.append(currentBid.getBid());
				}
				currentAuction = null;
			} catch (InterruptedException e) {
				// Auction has finished
			}
		}		
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
