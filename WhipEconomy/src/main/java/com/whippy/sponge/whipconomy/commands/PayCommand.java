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

import sun.security.ec.ECDHKeyAgreement;
import sun.security.provider.SHA;

import com.whippy.sponge.whipconomy.beans.Payment;
import com.whippy.sponge.whipconomy.beans.StaticsHandler;
import com.whippy.sponge.whipconomy.cache.ConfigurationLoader;
import com.whippy.sponge.whipconomy.cache.EconomyCache;
import com.whippy.sponge.whipconomy.orchestrator.PlayerNotifier;

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
			String playerName = (String) commandArgs.getOne("playerName").get();
			try{				
				Double amount = Double.valueOf((String) commandArgs.getOne("amount").get());
				if(playerName.equals("*")){
					for (Player playerToPay : StaticsHandler.getGame().getServer().getOnlinePlayers()) {
						if(!playerToPay.getIdentifier().equals(player.getIdentifier())){							
							payPlayer(amount, player, playerToPay.getName());
						}
					}
				}else if(playerName.equals("*offline")){
					for (String playerToPay : EconomyCache.getAllPlayers()) {
						if(!player.getIdentifier().equals(EconomyCache.getId(playerToPay))){							
							payPlayer(amount, player, playerToPay);						
						}
					}
				}else{					
					payPlayer(amount, player, playerName);
				}
			}catch(NumberFormatException e){
				player.sendMessage(Texts.builder("Amount to transfer must be numeric!").color(TextColors.RED).build());
			}
		}else{
			logger.warn("Pay called by non player entity");
			return CommandResult.empty();
		}
		return CommandResult.success();
	}

	private void payPlayer(Double amount, Player player, String playerName) {
		amount = EconomyCache.round(amount, ConfigurationLoader.getDecPlaces());
		if(EconomyCache.transfer(player, playerName, amount)){					
			Payment payment = new Payment(playerName, player.getName(), amount);
			PlayerNotifier.notifyEvenIfOffline(payment);
		}
	}

}
