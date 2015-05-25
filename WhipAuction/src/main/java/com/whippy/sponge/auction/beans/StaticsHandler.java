package com.whippy.sponge.auction.beans;

import org.slf4j.Logger;

import com.whippy.sponge.auction.orchestrator.Auctioneer;


public class StaticsHandler {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    private static Logger logger;
    private static Auctioneer auctioneer;

	public static Logger getLogger() {
		return logger;
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

}
