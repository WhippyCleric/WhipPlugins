package com.whippy.sponge.auction.commands;

import java.util.Collections;
import java.util.List;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;

import com.google.common.base.Optional;
import com.whippy.sponge.auction.beans.StaticsHandler;


public class BidCommand implements CommandCallable {

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
		return Texts.builder("/bid").color(TextColors.GOLD).build();
	}

	@Override
	public Optional<CommandResult> process(CommandSource sender, String args) throws CommandException {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if(args==null || args.equals("")){
				StaticsHandler.getAuctioneer().bid(player);
			}else{
				String[] arguments = args.split(" ");
				if(arguments.length==1){
					try{
						double bid = Double.valueOf(arguments[0]);
						StaticsHandler.getAuctioneer().bid(player, bid);
					}catch(NumberFormatException e){
						player.sendMessage(Texts.builder("Invalid command format, received non number value for bid amount").color(TextColors.RED).build());
					}
				}else if(arguments.length==2){
					try{
						double bid = Double.valueOf(arguments[0]);
						double maxBid = Double.valueOf(arguments[1]);
						StaticsHandler.getAuctioneer().bid(player, bid, maxBid);
					}catch(NumberFormatException e){
						player.sendMessage(Texts.builder("Invalid command format, received non number value for either bid or max bid amount").color(TextColors.RED).build());
					}
					
				}else{
					player.sendMessage(Texts.builder("Invalid command format, received more than 2 arguments with bid command").color(TextColors.RED).build());
				}
			}
		}else{
			StaticsHandler.getLogger().warn("bid command called by non player entity");
		}
		return null;
	}
	
	@Override
	public boolean testPermission(CommandSource arg0) {
		return true;
	}

}
