package com.whippy.sponge.whipconomy.commands;

import java.util.ArrayList;
import java.util.List;

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
import com.whippy.sponge.whipconomy.cache.EconomyCache;

public class PayCommand implements CommandExecutor {
	

	public List<String> getSuggestions(CommandSource sender, String semicomplete)throws CommandException {
		List<String> allPlayersSemi = new ArrayList<String>();
		List<String> allPlayers = new ArrayList<String>();
		List<String> playersToSuggest = new ArrayList<String>();
		allPlayers.addAll(EconomyCache.getAllPlayers());
		for (String player : allPlayers) {
			if(player.startsWith(semicomplete)){
				allPlayersSemi.add(player);
			}
		}
		playersToSuggest.addAll(allPlayersSemi);
		if(sender instanceof Player){
			playersToSuggest.remove(sender.getName());
		}
		return playersToSuggest;
	}
	
	@Override
	public CommandResult execute(CommandSource source, CommandContext arg1) throws CommandException {
		Logger logger = StaticsHandler.getLogger();
		if(source instanceof Player){
			Player player = (Player) source;
			if(arguments==null){
				player.sendMessage(Texts.builder("Must enter who to, and an amount to transfer").color(TextColors.RED).build());
			}else{
				String[] args = arguments.split(" ");
				if(args.length<2){
					player.sendMessage(Texts.builder("Must enter who to, and an amount to transfer").color(TextColors.RED).build());
				}else{
					String whoTo = args[0];
					String amountString = args[1];
					Double amount = Double.parseDouble(amountString);
					EconomyCache.transfer(player, whoTo, amount);
				}
			}
		}else{
			logger.warn("Transfer called by non player entity");
		}
		return null;
	}

}
