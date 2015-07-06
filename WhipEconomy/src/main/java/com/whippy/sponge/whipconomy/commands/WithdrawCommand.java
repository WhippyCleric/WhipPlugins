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

import com.whippy.sponge.whipconomy.beans.StaticsHandler;
import com.whippy.sponge.whipconomy.cache.ConfigurationLoader;
import com.whippy.sponge.whipconomy.cache.EconomyCache;
import com.whippy.sponge.whipconomy.exceptions.TransferException;

public class WithdrawCommand implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource source, CommandContext commandArgs)throws CommandException {
		Logger logger = StaticsHandler.getLogger();
		if(source instanceof Player){
			Player player = (Player) source;
			try{				
				Double amount = Double.valueOf((String) commandArgs.getOne("amount").get());
				amount = EconomyCache.round(amount, ConfigurationLoader.getDecPlaces());
				EconomyCache.withdraw(player.getName(), amount);
				player.sendMessage(Texts.builder(StaticsHandler.getAmountWithCurrency(amount) + " transfered into current account").color(TextColors.BLUE).build());
			}catch(NumberFormatException e){
				player.sendMessage(Texts.builder("Amount to transfer must be numeric!").color(TextColors.RED).build());
				return CommandResult.empty();
			} catch (TransferException e) {
				player.sendMessage(Texts.builder(e.getMessage()).color(TextColors.RED).build());
				return CommandResult.empty();
			}
		}else{
			logger.warn("Withdraw called by non player entity");
			return CommandResult.empty();
		}
		return CommandResult.success();
	}

}
