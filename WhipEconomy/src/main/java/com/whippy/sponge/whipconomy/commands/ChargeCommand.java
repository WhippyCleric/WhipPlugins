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

import com.whippy.sponge.whipconomy.beans.Payment;
import com.whippy.sponge.whipconomy.beans.StaticsHandler;
import com.whippy.sponge.whipconomy.cache.EconomyCache;
import com.whippy.sponge.whipconomy.exceptions.TransferException;
import com.whippy.sponge.whipconomy.orchestrator.PlayerNotifier;
public class ChargeCommand implements CommandExecutor {


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
		boolean isPlayer = false;


		if(source instanceof Player){
			isPlayer=true;
		}
		Player player = null;
		if(isPlayer){
			player = (Player) source;
		}
		String playerName = (String) commandArgs.getOne("playerName").get();
		Double amount = Double.valueOf((String) commandArgs.getOne("amount").get());

		try{
			String playerId = EconomyCache.getId(playerName);
			if(playerId!=null && !playerId.isEmpty()){
				EconomyCache.charge(playerId, amount);
				Payment payment = new Payment(null, playerName, amount);
				PlayerNotifier.notifyEvenIfOffline(payment);
				if(isPlayer){					
					player.sendMessage(Texts.builder(playerName + " has been charged " + StaticsHandler.getAmountWithCurrency(amount)).color(TextColors.GREEN).build());					
				}else{
					logger.warn(playerName + " has been charged " + StaticsHandler.getAmountWithCurrency(amount));
				}
			}else{
				if(isPlayer){					
					player.sendMessage(Texts.builder(playerName + " does not exist!").color(TextColors.RED).build());					
				}else{
					logger.warn(playerName + " does not exist!");
				}
				return CommandResult.empty();
			}
		} catch (TransferException e) {
			if(isPlayer){					
				player.sendMessage(Texts.builder(e.getMessage()).color(TextColors.RED).build());					
			}else{
				logger.warn(e.getMessage());
			}
			return CommandResult.empty();
		}
		return CommandResult.success();
	}



}
