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
import org.spongepowered.api.util.command.args.GenericArguments;
import org.spongepowered.api.util.command.spec.CommandSpec;

import com.google.inject.Inject;
import com.whippy.sponge.whipconomy.beans.Payment;
import com.whippy.sponge.whipconomy.beans.StaticsHandler;
import com.whippy.sponge.whipconomy.cache.AuctionCache;
import com.whippy.sponge.whipconomy.cache.ConfigurationLoader;
import com.whippy.sponge.whipconomy.cache.EconomyCache;
import com.whippy.sponge.whipconomy.cache.PendingNotificaitions;
import com.whippy.sponge.whipconomy.commands.AucCommand;
import com.whippy.sponge.whipconomy.commands.BalCommand;
import com.whippy.sponge.whipconomy.commands.BankCommand;
import com.whippy.sponge.whipconomy.commands.BidCommand;
import com.whippy.sponge.whipconomy.commands.ChargeCommand;
import com.whippy.sponge.whipconomy.commands.GiftCommand;
import com.whippy.sponge.whipconomy.commands.PayCommand;
import com.whippy.sponge.whipconomy.commands.SavingsAccHistoryCommand;
import com.whippy.sponge.whipconomy.commands.TransactionsCommand;
import com.whippy.sponge.whipconomy.commands.TransferCommand;
import com.whippy.sponge.whipconomy.commands.WithdrawCommand;
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
		
		if(!ConfigurationLoader.initProperties()){
			logger.warn("Failed to load configuration defaults will be used");
		}
		if(!ConfigurationLoader.initAliases()){
			logger.warn("Failed to load aliases defaults will be used");			
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

		CommandSpec balCommandSpec = CommandSpec.builder()
			    .description(Texts.of("Retrieve the ballance of a player"))
			    .permission("whippyconomy.bal.own")
			    .arguments(GenericArguments.optional(GenericArguments.string((Texts.of("playerName")))))
			    .executor(new BalCommand())
			    .build();

		cmdService.register(this, balCommandSpec, ConfigurationLoader.getBalAliases());
		
		
		CommandSpec payCommandSpec = CommandSpec.builder()
			    .description(Texts.of("Pay another player"))
			    .permission("whippyconomy.pay")
			    .arguments(GenericArguments.onlyOne(GenericArguments.string(Texts.of("playerName")))
			    ,GenericArguments.onlyOne(GenericArguments.string(Texts.of("amount"))))
			    .executor(new PayCommand())
			    .build();
		
		cmdService.register(this, payCommandSpec, ConfigurationLoader.getPayAliases());
		
		CommandSpec accHistoryCommandSpec = CommandSpec.builder()
			    .description(Texts.of("List a history of transactions"))
			    .permission("whippyconomy.accHistory.own")
			    .arguments(GenericArguments.optional(GenericArguments.integer((Texts.of("numberOfTransactions"))), 10)
			    ,GenericArguments.optional(GenericArguments.string(Texts.of("playerName"))))
			    .executor(new TransactionsCommand())
			    .build();
		
		cmdService.register(this, accHistoryCommandSpec, ConfigurationLoader.getAccHistoryAliases());
		
		CommandSpec chargeCommandSpec = CommandSpec.builder()
				.description(Texts.of("Charge another player money"))
				.permission("whippyconomy.charge")
				.arguments(GenericArguments.onlyOne(GenericArguments.string(Texts.of("playerName")))
				,GenericArguments.onlyOne(GenericArguments.string(Texts.of("amount"))))
				.executor(new ChargeCommand())
				.build();
		
		cmdService.register(this, chargeCommandSpec,ConfigurationLoader.getChargeAliases());
		
		CommandSpec transferCommandSpec = CommandSpec.builder()
				.description(Texts.of("Transfer money from one player to another"))
				.permission("whippyconomy.transfer")
				.arguments(GenericArguments.onlyOne(GenericArguments.string(Texts.of("playerNameFrom")))
				,GenericArguments.onlyOne(GenericArguments.string(Texts.of("playerNameTo")))
				,GenericArguments.onlyOne(GenericArguments.string(Texts.of("amount"))))
				.executor(new TransferCommand())
				.build();
		
		cmdService.register(this, transferCommandSpec, ConfigurationLoader.getTransferAliases());

		CommandSpec giftCommandSpec = CommandSpec.builder()
				.description(Texts.of("Give a player an amount of money from the server"))
				.permission("whippyconomy.give")
				.arguments(GenericArguments.onlyOne(GenericArguments.string(Texts.of("playerName")))
						,GenericArguments.onlyOne(GenericArguments.string(Texts.of("amount"))))
						.executor(new GiftCommand())
						.build();
		
		cmdService.register(this, giftCommandSpec,ConfigurationLoader.getGiftAliases());
	
		CommandSpec bankCommandSpec = CommandSpec.builder()
				.description(Texts.of("Transfer money from current account to savings account"))
				.permission("whippyconomy.bank")
				.arguments(GenericArguments.onlyOne(GenericArguments.string(Texts.of("amount"))))
						.executor(new BankCommand())
						.build();
		
		cmdService.register(this, bankCommandSpec, ConfigurationLoader.getBalAliases());
		
		CommandSpec withdrawCommandSpec = CommandSpec.builder()
				.description(Texts.of("Transfer money from savings account to current account"))
				.permission("whippyconomy.withdraw")
				.arguments(GenericArguments.onlyOne(GenericArguments.string(Texts.of("amount"))))
				.executor(new WithdrawCommand())
				.build();
		
		cmdService.register(this, withdrawCommandSpec, ConfigurationLoader.getWithdrawAliases());
		
		
		CommandSpec savingsHistoryCommandSpec = CommandSpec.builder()
			    .description(Texts.of("List a history of savings transfers"))
			    .permission("whippyconomy.savingsHistort")
			    .arguments(GenericArguments.optional(GenericArguments.integer((Texts.of("numberOfTransfers"))), 10))
			    .executor(new SavingsAccHistoryCommand())
			    .build();
		
		cmdService.register(this, savingsHistoryCommandSpec, ConfigurationLoader.getSavingsHistoryAliases());
		

		if(ConfigurationLoader.hasAuctions() && ConfigurationLoader.getMaxAuctions()>0){
			cmdService.register(this, new AucCommand(), "auc");
	        cmdService.register(this, new BidCommand(), "bid");
		}
		StaticsHandler.setAuctionCache(new AuctionCache());
		StaticsHandler.setAuctioneer(new Auctioneer());
		StaticsHandler.setAuctionRunner(new AuctionRunner());
	}
	
}
