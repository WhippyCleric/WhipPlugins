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
import com.whippy.sponge.guard.beans.Area;
import com.whippy.sponge.guard.beans.StaticsHandler;

public class NewAreaCommand implements CommandCallable{ 
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
						Area area;
						if(argArray[1].equals(StaticsHandler.BOUNDLESS)){								
							area = new Area(StaticsHandler.BOUNDLESS_NUMBER, StaticsHandler.BOUNDLESS_NUMBER);
						}else{
							area = new Area(Double.valueOf(argArray[0]), StaticsHandler.BOUNDLESS_NUMBER);							
						}
						if(StaticsHandler.getAreaHandler().addAreaInProgres(player, area)){						
							player.sendMessage(Texts.builder("New area started with height: "+Double.valueOf(argArray[0])+" and boundless depth").color(TextColors.BLUE).build());		
						}else{						
							player.sendMessage(Texts.builder("Player already in progress of defining area, either run /areaCommit or /areaCancel before starting a new area").color(TextColors.RED).build());		
						}
					}else{
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
						Area area = new Area(height, depth);
						if(StaticsHandler.getAreaHandler().addAreaInProgres(player, area)){						
							player.sendMessage(Texts.builder("New area started with height: "+Double.valueOf(argArray[0])+" and depth: " +  Double.valueOf(argArray[1])).color(TextColors.BLUE).build());		
						}else{						
							player.sendMessage(Texts.builder("Player already in progress of defining area, either run /areaCommit or /areaCancel before starting a new area").color(TextColors.RED).build());		
						}
					}
				}catch(NumberFormatException e){					
					player.sendMessage(Texts.builder("Height and depth must be numeric").color(TextColors.RED).build());		
				}
			}else{
				Area area = new Area();
				StaticsHandler.getAreaHandler().addAreaInProgres(player, area);
				player.sendMessage(Texts.builder("New area started with boundless height and depth").color(TextColors.BLUE).build());		
			}	
		}else{
			StaticsHandler.getLogger().warn("Finalsie area called by non player entity!");
		}
		return null;
	}

	@Override
	public boolean testPermission(CommandSource arg0) {
		// TODO Auto-generated method stub
		return false;
	}
}