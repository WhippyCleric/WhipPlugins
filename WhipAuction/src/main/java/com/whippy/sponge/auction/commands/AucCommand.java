package com.whippy.sponge.auction.commands;

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
import com.whippy.sponge.auction.beans.Auction;
import com.whippy.sponge.auction.beans.StaticsHandler;


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
			Optional<ItemStack> holdingOptional = player.getItemInHand();
			if(holdingOptional.isPresent()){
				ItemStack item = holdingOptional.get();
				String itemId = item.getItem().getId();
				String itemName = item.getItem().getId();
				if(args!=null && !args.equals("")){
					String[] arguments = args.split(" ");
					if(arguments.length!=5){
						player.sendMessage(Texts.builder("Invalid command format").color(TextColors.RED).build());
					}else{
							String subCommand = arguments[0];
							if(subCommand.equalsIgnoreCase("s")){
								try{
								
									int numberOfItem = Integer.valueOf(arguments[1]);
									double startingBid = Double.valueOf(arguments[2]);
									double increment  = Double.valueOf(arguments[3]);
									int time = Integer.valueOf(arguments[4]);
									Auction auction = new Auction(itemId, itemName,numberOfItem, startingBid, increment, time);
									int result = StaticsHandler.getAuctioneer().pushAuctionToQueue(auction);
									if(result == -1){
										player.sendMessage(Texts.builder("Auction queue is full, please try again later").color(TextColors.RED).build());
									}else{
										player.sendMessage(Texts.builder("Auction queued number " + result + " in line").color(TextColors.RED).build());
									}
								}catch(NumberFormatException e){
									player.sendMessage(Texts.builder("Invalid command format, text received instead of number").color(TextColors.RED).build());
								}
							}else{								
								player.sendMessage(Texts.builder("Invalid command format, missing s").color(TextColors.RED).build());
							}
					}
				}else{
					player.sendMessage(Texts.builder("Invalid command format, no arguments received").color(TextColors.RED).build());
				}
			}else{
				player.sendMessage(Texts.builder("Please have the item you wish to auction in your hand.").color(TextColors.RED).build());
			}
		}else{
			StaticsHandler.getLogger().warn("auc command called by non player entity");
		}
		return null;
	}

	@Override
	public boolean testPermission(CommandSource arg0) {
		return true;
	}

}
