package com.whippy.sponge.whipconomy.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.PlayerJoinEvent;
import org.spongepowered.api.event.state.ServerStartingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.command.CommandService;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

import com.google.inject.Inject;
import com.whippy.sponge.whipconomy.beans.Payment;
import com.whippy.sponge.whipconomy.beans.StaticsHandler;
import com.whippy.sponge.whipconomy.cache.AuctionCache;
import com.whippy.sponge.whipconomy.cache.ConfigurationLoader;
import com.whippy.sponge.whipconomy.cache.EconomyCache;
import com.whippy.sponge.whipconomy.cache.PendingNotificaitions;
import com.whippy.sponge.whipconomy.commands.AucCommand;
import com.whippy.sponge.whipconomy.commands.BalCommand;
import com.whippy.sponge.whipconomy.commands.BidCommand;
import com.whippy.sponge.whipconomy.commands.TransactionsCommand;
import com.whippy.sponge.whipconomy.commands.PayCommand;
import com.whippy.sponge.whipconomy.orchestrator.AuctionRunner;
import com.whippy.sponge.whipconomy.orchestrator.Auctioneer;

@Plugin(id = "Whipconomy", name = "Whipconomy")
public class Whipconomy {

	@Inject
	Game game;

	@Inject
	Logger logger;
	
	@Subscribe
    public void onServerStarting(ServerStartingEvent event)  {
		StaticsHandler.setLogger(logger);
		StaticsHandler.setGame(game);
		
		if(!ConfigurationLoader.init()){
			logger.warn("Failed to load configuration defaults will be used");
		}
		EconomyCache.refreshMappingsFromFile();
		EconomyCache.refreshAccountsFromFile();
		PendingNotificaitions.refreshPendingFromFile();
		
	}

	@Subscribe
	public void onPlayerJoin(PlayerJoinEvent event){
		EconomyCache.updatePlayerMapping(event.getUser());
		List<Payment> pendingPayments = PendingNotificaitions.getAndRemovePendingPayments(event.getUser().getIdentifier());
		Map<String, List<Payment>> payerToPayments = new HashMap<String, List<Payment>>();
		if(pendingPayments!=null & pendingPayments.size()!=0){
			event.getUser().sendMessage(Texts.builder("Following transactions occured whilst offline: ").color(TextColors.BLUE).build());
		}
		for (Payment payment : pendingPayments) {
			String payer = payment.getPlayerNamePayer();
			if(payerToPayments.get(payer)==null){
				List<Payment> payments = new ArrayList<Payment>();
				payerToPayments.put(payer,  payments);
			}
			List<Payment> payments = payerToPayments.get(payer);
			payments.add(payment);
			payerToPayments.put(payer,payments);
		}
		
		for (String payer : payerToPayments.keySet()) {
			double amount = 0;
			for (Payment payment : payerToPayments.get(payer)) {
				amount += payment.getAmount();
			}
			StringBuilder messageBuilder = new StringBuilder();
			messageBuilder.append("Received ");
			if(!ConfigurationLoader.isAppendCurrency()){
				messageBuilder.append(ConfigurationLoader.getCurrency());
				messageBuilder.append(EconomyCache.round(amount, ConfigurationLoader.getDecPlaces()));
			}else{
				messageBuilder.append(EconomyCache.round(amount, ConfigurationLoader.getDecPlaces()));
				messageBuilder.append(ConfigurationLoader.getCurrency());
			}
			messageBuilder.append(" from ");
			messageBuilder.append(payer);
			
			event.getUser().sendMessage(Texts.builder(messageBuilder.toString()).color(TextColors.GREEN).build());
		}
		
	}

	
	@Subscribe
	public void onPreInitializationEvent(ServerStartingEvent event) {
		CommandService cmdService = game.getCommandDispatcher();
		cmdService.register(this, new BalCommand(), "bal");
		cmdService.register(this, new PayCommand(), "pay");
		cmdService.register(this, new TransactionsCommand(), "accHistory");
		if(ConfigurationLoader.hasAuctions() && ConfigurationLoader.getMaxAuctions()>0){
			cmdService.register(this, new AucCommand(), "auc");
	        cmdService.register(this, new BidCommand(), "bid");
		}
		StaticsHandler.setAuctionCache(new AuctionCache());
		StaticsHandler.setAuctioneer(new Auctioneer());
		AuctionRunner runner = new AuctionRunner();
		runner.run();
	}
	
}
