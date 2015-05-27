package com.whippy.sponge.whipconomy.beans;

import org.spongepowered.api.Game;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.format.TextColors;

import com.whippy.sponge.whipconomy.cache.EconomyCache;
import com.whippy.sponge.whipconomy.exceptions.TransferException;

public class Auction extends Thread{

	private String itemId;
	private String itemName;
	private int numberOfItem;
	private double startingBid;
	private double increment;
	private int time;
	private String playerName;
	private boolean cancelable;
	private Bid currentBid;
	private boolean isBidable;
	private String playerId;
	private boolean isCancelled;
	
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
		this.cancelable = true;
		this.isCancelled=false;
	}

	public String getPlayerName() {
		return playerName;
	}

	public String getPlayerId() {
		return playerId;
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
	public boolean isCancelable() {
		return cancelable;
	}
	public void setCancelable(boolean cancelable) {
		this.cancelable = cancelable;
	}
	
	public Bid getCurrentBid() {
		return currentBid;
	}
	
	public void setCurrentBid(Bid currentBid) {
		this.currentBid = currentBid;
	}

	public boolean isBidable() {
		return isBidable;
	}

	public void raiseCurrentBid(double maxBid) {
		currentBid.setCurrentBid(maxBid);
	}
	
	
	public void cancelAuction(){
		isCancelled=true;
	}
	
	@Override
	public void run(){
			Game game = StaticsHandler.getGame();
			StringBuilder auctionStartingBuilder = new StringBuilder();
			auctionStartingBuilder.append(getPlayerName());
			auctionStartingBuilder.append(" is auctioning ");
			auctionStartingBuilder.append(getNumberOfItem());
			auctionStartingBuilder.append(" ");
			auctionStartingBuilder.append(getItemName());
			auctionStartingBuilder.append(". Starting bid: ");
			auctionStartingBuilder.append(getStartingBid());
			auctionStartingBuilder.append(". Increment: ");
			auctionStartingBuilder.append(getIncrement());
			auctionStartingBuilder.append(". This auction will last ");
			auctionStartingBuilder.append(getTime());
			auctionStartingBuilder.append(" seconds.");

			int time = getTime();
			game.getServer().broadcastMessage(StaticsHandler.buildTextForEcoPlugin(auctionStartingBuilder.toString(),TextColors.BLUE));
			isBidable = true;
			try {
				for(int i=0; i<time-30;i++){
					Thread.sleep(1000);
					if(isCancelled){
						break;
					}
				}
				
				
				if(!isCancelled){
					setCancelable(false);
					game.getServer().broadcastMessage(StaticsHandler.buildTextForEcoPlugin("30 seconds remaining", TextColors.BLUE));

					Thread.sleep(20000);
					game.getServer().broadcastMessage(StaticsHandler.buildTextForEcoPlugin("10 seconds remaining", TextColors.BLUE));
					
					Thread.sleep(7000);
					game.getServer().broadcastMessage(StaticsHandler.buildTextForEcoPlugin("3 seconds remaining", TextColors.BLUE));
	
					Thread.sleep(1000);
					game.getServer().broadcastMessage(StaticsHandler.buildTextForEcoPlugin("2 seconds remaining", TextColors.BLUE));
	
					Thread.sleep(1000);
					game.getServer().broadcastMessage(StaticsHandler.buildTextForEcoPlugin("1 seconds remaining", TextColors.BLUE));
					
					Thread.sleep(1000);
					isBidable = false;
					Bid finalBid = getCurrentBid();
					setCurrentBid(null);
					if(finalBid==null){
						game.getServer().broadcastMessage(StaticsHandler.buildTextForEcoPlugin("Auction completed with no bids", TextColors.BLUE));
					}else{
						StringBuilder auctionNotification = new StringBuilder();
						auctionNotification.append(finalBid.getPlayer().getName());
						auctionNotification.append(" won the auction with a bid of ");
						auctionNotification.append(finalBid.getCurrentBid());
						game.getServer().broadcastMessage(StaticsHandler.buildTextForEcoPlugin(auctionNotification.toString(), TextColors.BLUE));
						if(finalBid.getMaxBid()!=finalBid.getCurrentBid()){							
							EconomyCache.pay(finalBid.getPlayer().getIdentifier(), finalBid.getMaxBid()-finalBid.getCurrentBid());
						}
						EconomyCache.pay(playerId, finalBid.getCurrentBid());
					}
				}
			} catch (InterruptedException | TransferException e) {
				System.out.println(e);
			}
	}



	
}
