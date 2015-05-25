package com.whippy.sponge.whipconomy.commands;

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
					if(arguments.length==5){
						fiveArgumentCommand(arguments, player);
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
			sellCommand(arguments, player);
		}else{								
			player.sendMessage(StaticsHandler.buildTextForEcoPlugin("Invalid command format, received 5 arguments but not a sell command",TextColors.RED));
		}
	}
	
	private void sellCommand(String[] arguments, Player player){
		Optional<ItemStack> holdingOptional = player.getItemInHand();
		if(holdingOptional.isPresent()){
			ItemStack item = holdingOptional.get();
			String itemId = item.getItem().getId();
			String itemName = item.getItem().getId();
			try{
				int numberOfItem = Integer.valueOf(arguments[1]);
				double startingBid = Double.valueOf(arguments[2]);
				double increment  = Double.valueOf(arguments[3]);
				int time = Integer.valueOf(arguments[4]);
				Auction auction = new Auction(itemId, itemName,numberOfItem, startingBid, increment, time, player);
				StaticsHandler.getAuctioneer().pushAuctionToQueue(auction, player);
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