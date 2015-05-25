package com.whippy.sponge.auction.beans;

import org.slf4j.Logger;
import org.spongepowered.api.Game;

import com.whippy.sponge.auction.orchestrator.Auctioneer;


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

}
