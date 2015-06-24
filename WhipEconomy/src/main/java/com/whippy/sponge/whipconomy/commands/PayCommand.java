package com.whippy.sponge.whipconomy.commands;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;

import com.google.common.base.Optional;
import com.whippy.sponge.whipconomy.beans.StaticsHandler;
import com.whippy.sponge.whipconomy.cache.EconomyCache;

public class PayCommand implements CommandCallable {
	
	@Override
	public Optional<Text> getHelp(CommandSource arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<Text> getShortDescription(CommandSource arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
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
	public Text getUsage(CommandSource arg0) {
		return Texts.builder("/pay <to> <amount>").color(TextColors.GOLD).build();
	}

	@Override
	public Optional<CommandResult> process(CommandSource sender, String arguments)
			throws CommandException {
		Logger logger = StaticsHandler.getLogger();
		if(sender instanceof Player){
			Player player = (Player) sender;
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

	@Override
	public boolean testPermission(CommandSource arg0) {
		return true;
	}

}
