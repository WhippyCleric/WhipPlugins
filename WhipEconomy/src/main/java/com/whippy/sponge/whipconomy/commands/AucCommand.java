package com.whippy.sponge.whipconomy.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;

import com.google.common.base.Optional;
import com.whippy.sponge.whipconomy.beans.Auction;
import com.whippy.sponge.whipconomy.beans.StaticsHandler;
import com.whippy.sponge.whipconomy.cache.ConfigurationLoader;
import com.whippy.sponge.whipconomy.cache.EconomyCache;


public class AucCommand implements CommandCallable {

	//~ ----------------------------------------------------------------------------------------------------------------
	//~ Methods 
	//~ ----------------------------------------------------------------------------------------------------------------

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
	public List<String> getSuggestions(CommandSource arg0, String arg1) throws CommandException {
		return Collections.emptyList();
	}

	@Override
	public Text getUsage(CommandSource arg0) {
		return Texts.builder("/auc").color(TextColors.GOLD).build();
	}

	@Override
	public Optional<CommandResult> process(CommandSource sender, String args) throws CommandException {
		if (sender instanceof Player) {
			Player player = (Player) sender;
				if(args!=null && !args.equals("")){
					String[] arguments = args.split(" ");
					if(arguments.length==6){
						sixArgumentCommand(arguments, player);
					}else if(arguments.length==5){
						fiveArgumentCommand(arguments, player);
					}else if(arguments.length==4){
						fourArgumentCommand(arguments, player);
					}else if(arguments.length==1){
						singleArgumentCommand(arguments, player);
					}else{
						player.sendMessage(StaticsHandler.buildTextForEcoPlugin("Invalid command format",TextColors.RED));
					}
				}else{
					player.sendMessage(StaticsHandler.buildTextForEcoPlugin("Invalid command format, no arguments received",TextColors.RED));
				}
		}else{
			StaticsHandler.getLogger().warn("auc command called by non player entity");
		}
		return null;
	}
	
	private void sixArgumentCommand(String[] arguments, Player player) {
		String subCommand = arguments[0];
		if(subCommand.equalsIgnoreCase("sbn")){
			List<String> arguementsList = new ArrayList<String>();
			for (String argument : arguments) {
				arguementsList.add(argument);
			}
			sellCommand(arguementsList, player);
		}else{								
			player.sendMessage(StaticsHandler.buildTextForEcoPlugin("Invalid command format, received 6 arguments but not a sell buy it now command",TextColors.RED));
		}
	}

	private void singleArgumentCommand(String[] arguments, Player player) {
		String subCommand = arguments[0];
		if(subCommand.equalsIgnoreCase("c")){
			cancelCommand(arguments, player);
		}else{								
			player.sendMessage(StaticsHandler.buildTextForEcoPlugin("Invalid command format, received 1 arguments but not a cancel command",TextColors.RED));
		}
	}

	private void cancelCommand(String[] arguments, Player player) {
		StaticsHandler.getAuctioneer().cancel(player);
	}

	private void fiveArgumentCommand(String[] arguments, Player player){
		String subCommand = arguments[0];
		if(subCommand.equalsIgnoreCase("s")){
			List<String> arguementsList = new ArrayList<String>();
			for (String argument : arguments) {
				arguementsList.add(argument);
			}
			sellCommand(arguementsList, player);
		}else if(subCommand.equalsIgnoreCase("sbn")){
			List<String> arguementsList = new ArrayList<String>();
			for (String argument : arguments) {
				arguementsList.add(argument);
			}
			arguementsList.add(3, "" + ConfigurationLoader.getDefaultIncrement());
			sellCommand(arguementsList, player);
		}else{								
			player.sendMessage(StaticsHandler.buildTextForEcoPlugin("Invalid command format, received 5 arguments but not a sell command",TextColors.RED));
		}
	}

	private void fourArgumentCommand(String[] arguments, Player player){
		String subCommand = arguments[0];
		if(subCommand.equalsIgnoreCase("s")){
			List<String> arguementsList = new ArrayList<String>();
			for (String argument : arguments) {
				arguementsList.add(argument);
			}
			arguementsList.add(3, "" + ConfigurationLoader.getDefaultIncrement());
			sellCommand(arguementsList, player);
		}else{								
			player.sendMessage(StaticsHandler.buildTextForEcoPlugin("Invalid command format, received 5 arguments but not a sell command",TextColors.RED));
		}
	}
	
	private void sellCommand(List<String> arguments, Player player){
		Optional<ItemStack> holdingOptional = player.getItemInHand();
		if(holdingOptional.isPresent()){
			ItemStack item = holdingOptional.get();
			String itemId = item.getItem().getId();
			String itemName = item.getItem().getName();
			try{
				int numberOfItem = Integer.valueOf(arguments.get(1));
				double startingBid = EconomyCache.round(Double.valueOf(arguments.get(2)), ConfigurationLoader.getDecPlaces());
				double increment  = EconomyCache.round(Double.valueOf(arguments.get(3)), ConfigurationLoader.getDecPlaces());
				int time = Integer.valueOf(arguments.get(4));
				int buyItNow=-1;
				if(arguments.size()==6){
					buyItNow = Integer.parseInt(arguments.get(5));
					if(buyItNow<=0){						
						player.sendMessage(StaticsHandler.buildTextForEcoPlugin("Buy it now price must be a positive number",TextColors.RED));
					}else if(buyItNow<startingBid){
						player.sendMessage(StaticsHandler.buildTextForEcoPlugin("Buy it now price must be at least starting bid",TextColors.RED));						
					}else{						
						Auction auction = new Auction(itemId, itemName,numberOfItem, startingBid, increment, time, player, buyItNow);
						StaticsHandler.getAuctioneer().pushAuctionToQueue(auction, player);
					}
				}else if(numberOfItem<1){
					player.sendMessage(StaticsHandler.buildTextForEcoPlugin("Must hold at least 1 item to auction",TextColors.RED));
				}else if(startingBid<=0){
					player.sendMessage(StaticsHandler.buildTextForEcoPlugin("Starting bid must be a greater than 0",TextColors.RED));
				}else if(increment<=0){
					player.sendMessage(StaticsHandler.buildTextForEcoPlugin("Increment must be greater than 0",TextColors.RED));
				}else if(time<ConfigurationLoader.getMinAuctionTime()){
					player.sendMessage(StaticsHandler.buildTextForEcoPlugin("Time must be at least " + ConfigurationLoader.getMinAuctionTime() + " seconds",TextColors.RED));
				}else if(time>ConfigurationLoader.getMaxAuctionTime()){
					player.sendMessage(StaticsHandler.buildTextForEcoPlugin("Time must be at most " + ConfigurationLoader.getMaxAuctionTime() + " seconds",TextColors.RED));
				}else{					
					Auction auction = new Auction(itemId, itemName,numberOfItem, startingBid, increment, time, player, buyItNow);
					StaticsHandler.getAuctioneer().pushAuctionToQueue(auction, player);
				}
			}catch(NumberFormatException e){
				player.sendMessage(StaticsHandler.buildTextForEcoPlugin("Invalid command format, text received instead of number",TextColors.RED));
			}
		}else{
			player.sendMessage(StaticsHandler.buildTextForEcoPlugin("Please have the item you wish to auction in your hand.",TextColors.RED));
		}
	}

	@Override
	public boolean testPermission(CommandSource arg0) {
		return true;
	}

}
