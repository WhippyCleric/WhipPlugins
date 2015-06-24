package com.whippy.sponge.guard.commands;

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
import com.whippy.sponge.guard.beans.StaticsHandler;

public class FinaliseCommand implements CommandCallable{ 
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
	public List<String> getSuggestions(CommandSource arg0, String arg1)
			throws CommandException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Text getUsage(CommandSource arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<CommandResult> process(CommandSource sender, String args) throws CommandException {
		if(sender instanceof Player){
			Player player = (Player) sender;
			if(args!=null && !args.isEmpty()){
				String[] argArray =  args.split(" ");
				try{
					if(argArray.length==1){					
						StaticsHandler.getAreaHandler().finaliseCurrentArea(player,argArray[0]);
					}else if(argArray.length==2){
						if(argArray[1].equals(StaticsHandler.BOUNDLESS)){							
							StaticsHandler.getAreaHandler().finaliseCurrentArea(player,argArray[0], StaticsHandler.BOUNDLESS_NUMBER);
						}
					}else if(argArray.length==3){
						Double height;
						Double depth;
						if(argArray[1].equals(StaticsHandler.BOUNDLESS)){	
							height = StaticsHandler.BOUNDLESS_NUMBER;
						}else{
							height = Double.valueOf(argArray[1]);
						}
						if(argArray[2].equals(StaticsHandler.BOUNDLESS)){	
							depth = StaticsHandler.BOUNDLESS_NUMBER;
						}else{
							depth = Double.valueOf(argArray[2]);
						}
						StaticsHandler.getAreaHandler().finaliseCurrentArea(player,argArray[0], height, depth);
					}
				}catch(NumberFormatException e){					
					player.sendMessage(Texts.builder("Maximum height and base must be numeric").color(TextColors.RED).build());		
				}
			}else{
				player.sendMessage(Texts.builder("Must specify area name").color(TextColors.RED).build());		
			}
		}else{
			StaticsHandler.getLogger().warn("Finalise area called by non player entity!");
		}
		return null;
	}

	@Override
	public boolean testPermission(CommandSource arg0) {
		// TODO Auto-generated method stub
		return false;
	}
}