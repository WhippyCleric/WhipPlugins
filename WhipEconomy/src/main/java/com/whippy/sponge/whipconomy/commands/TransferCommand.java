package com.whippy.sponge.whipconomy.commands;

import org.slf4j.Logger;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

import com.whippy.sponge.whipconomy.beans.Payment;
import com.whippy.sponge.whipconomy.beans.StaticsHandler;
import com.whippy.sponge.whipconomy.cache.ConfigurationLoader;
import com.whippy.sponge.whipconomy.cache.EconomyCache;
import com.whippy.sponge.whipconomy.orchestrator.PlayerNotifier;

public class TransferCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource source, CommandContext commandArgs) throws CommandException {
		Logger logger = StaticsHandler.getLogger();
		String playerNameFrom = (String) commandArgs.getOne("playerNameFrom").get();
		String playerNameTo = (String) commandArgs.getOne("playerNameTo").get();
		Double amount = Double.valueOf((String) commandArgs.getOne("amount").get());
		amount = EconomyCache.round(amount, ConfigurationLoader.getDecPlaces());
		if(source instanceof Player){
			Player player = (Player) source;
			if(EconomyCache.transfer(player, playerNameFrom, playerNameTo, amount)){				
				Payment payment = new Payment(playerNameTo, playerNameFrom, amount);
				PlayerNotifier.notifyEvenIfOffline(payment);
			}
		}else{
			logger.warn("Pay called by non player entity");
			EconomyCache.transfer(null, playerNameFrom, playerNameTo, amount);
			Payment payment = new Payment(playerNameTo, playerNameFrom, amount);
			PlayerNotifier.notifyEvenIfOffline(payment);
		}
		return CommandResult.success();
	}

}
