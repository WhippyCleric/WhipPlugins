package com.whippy.sponge.whipconomy.orchestrator;

import com.whippy.sponge.whipconomy.beans.StaticsHandler;
import com.whippy.sponge.whipconomy.cache.ConfigurationLoader;

public class AuctionRunner extends Thread{
	
	
	
	
	@Override
	public void run(){
		while(ConfigurationLoader.hasAuctions()){
			Auctioneer auctioneer = StaticsHandler.getAuctioneer();
			auctioneer.getAuctions();
			if(auctioneer.getAuctions().size()>0){
				auctioneer.setCurrentAuction(auctioneer.getAuctions().remove(0));
				auctioneer.getCurrentAuction().run();
			}
		}
	}
}
