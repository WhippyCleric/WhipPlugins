package com.whippy.sponge.whipconomy.beans;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColor;

import com.whippy.cponge.whipconomy.orchestrator.Auctioneer;



public class StaticsHandler {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    private static Logger logger;
    private static Auctioneer auctioneer;
    private static Game game;
    private static String auctionPrefix;

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

	public static String getAuctionPrefix() {
		return auctionPrefix;
	}

	public static void setAuctionPrefix(String auctionPrefixNew) {
		auctionPrefix = auctionPrefixNew;
	}
	
	public static Text buildTextForEcoPlugin(String message, TextColor color){
		StringBuilder notification = new StringBuilder();
		notification.append(getAuctionPrefix());
		notification.append(message);
		return Texts.builder(notification.toString()).color(color).build();
	}

}
