package com.whippy.sponge.whipconomy.beans;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColor.Base;

import com.whippy.sponge.whipconomy.cache.AuctionCache;
import com.whippy.sponge.whipconomy.cache.ConfigurationLoader;
import com.whippy.sponge.whipconomy.orchestrator.AuctionRunner;
import com.whippy.sponge.whipconomy.orchestrator.Auctioneer;



public class StaticsHandler {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    private static Logger logger;
    private static Auctioneer auctioneer;
    private static Game game;
    private static AuctionCache auctionCache;
    private static AuctionRunner auctionRunner;

	public static Logger getLogger() {
		return logger;
	}

	public static Game getGame() {
		return game;
	}

	public static void setGame(Game game) {
		StaticsHandler.game = game;
	}

	public static void setLogger(Logger loggerNew) {
		logger = loggerNew;
	}

	public static Auctioneer getAuctioneer() {
		return auctioneer;
	}

	public static void setAuctioneer(Auctioneer auctioneerNew) {
		auctioneer = auctioneerNew;
	}


	public static String getAmountWithCurrency(Double amount){
		if(ConfigurationLoader.isAppendCurrency()){
			return amount + ConfigurationLoader.getCurrency();
		}else{
			return ConfigurationLoader.getCurrency() + amount;
		}
	}
	
	
	public static Text buildTextForEcoPlugin(String message, Base color){
		StringBuilder notification = new StringBuilder();
		notification.append(ConfigurationLoader.getAuctionPrefix());
		notification.append(message);
		return Texts.builder(notification.toString()).color(color).build();
	}

	public static AuctionCache getAuctionCache() {
		return auctionCache;
	}

	public static void setAuctionCache(AuctionCache auctionCache) {
		StaticsHandler.auctionCache = auctionCache;
	}

	public static void setAuctionRunner(AuctionRunner auctionRunnerNew) {
		auctionRunner = auctionRunnerNew;
	}
	
	public static void startAuctionsIfNotStarted(){
		if(!auctionRunner.isAlive()){
			auctionRunner.run();
		}
	}

}
