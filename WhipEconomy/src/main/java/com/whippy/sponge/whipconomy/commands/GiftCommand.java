package com.whippy.sponge.whipconomy.commands;

import org.slf4j.Logger;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

import com.whippy.sponge.whipconomy.beans.Payment;
import com.whippy.sponge.whipconomy.beans.StaticsHandler;
import com.whippy.sponge.whipconomy.cache.ConfigurationLoader;
import com.whippy.sponge.whipconomy.cache.EconomyCache;
import com.whippy.sponge.whipconomy.exceptions.TransferException;
import com.whippy.sponge.whipconomy.orchestrator.PlayerNotifier;

public class GiftCommand implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource source, CommandContext commandArgs) throws CommandException {
		Logger logger = StaticsHandler.getLogger();
		Player player = null;
		if(source instanceof Player){
			player = (Player) source;
		}else{
			logger.warn("Pay called by non player entity");
		}
		String playerName = (String) commandArgs.getOne("playerName").get();
		try{				
			Double amount = Double.valueOf((String) commandArgs.getOne("amount").get());
			amount = EconomyCache.round(amount, ConfigurationLoader.getDecPlaces());
			EconomyCache.gift(playerName, amount);
			Payment payment = new Payment(playerName, null, amount);
			PlayerNotifier.notifyEvenIfOffline(payment);
		}catch(NumberFormatException e){
			if(player!=null){					
				player.sendMessage(Texts.builder("Amount to give must be numeric!").color(TextColors.RED).build());
			}else{				
				logger.warn("Amount to give must be numeric!");
			}
			return CommandResult.empty();
		} catch (TransferException e) {
			if(player!=null){					
				player.sendMessage(Texts.builder(e.getMessage()).color(TextColors.RED).build());					
			}else{
				logger.warn(e.getMessage());
			}
			return CommandResult.empty();
		}
		return CommandResult.success();
	}

}
