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

import com.google.common.base.Optional;
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
	public CommandResult execute(CommandSource source, CommandContext commandArgs) throws CommandException {
		Logger logger = StaticsHandler.getLogger();
		if(source instanceof Player){
			Player player = (Player) source;
			Optional<Object> optionalPlayerName = commandArgs.getOne("playerName");
			Optional<Object> optionalAmountName = commandArgs.getOne("amount");
			if(optionalPlayerName.isPresent() && optionalAmountName.isPresent()){
				String playerName = (String) optionalPlayerName.get();
				try{
					Double amount = Double.valueOf((String) optionalAmountName.get()); 
					EconomyCache.transfer(player, playerName, amount);
				}catch(NumberFormatException e){
					player.sendMessage(Texts.builder("Amount must be numeric").color(TextColors.RED).build());					
				}
			}else{
				
				player.sendMessage(Texts.builder("Must enter who to, and an amount to transfer").color(TextColors.RED).build());
			}
		}else{
			logger.warn("Pay called by non player entity");
		}
		return null;
	}

}
