package com.whippy.sponge.commands;

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
import org.spongepowered.api.world.Location;

import com.google.common.base.Optional;
import com.whippy.sponge.commands.beans.NoWorldLocation;
import com.whippy.sponge.commands.beans.StaticsHandler;
import com.whippy.sponge.commands.config.CommandConfiguration;

public class SpawnCommand implements CommandCallable{

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
	public Text getUsage(CommandSource arg0) {
		return Texts.builder("/spawn").color(TextColors.GOLD).build();
	}
	
	@Override
	public Optional<CommandResult> process(CommandSource sender, String args)
			throws CommandException {
		if(sender instanceof Player){
			Player player = (Player) sender;
			NoWorldLocation spawn = CommandConfiguration.getSpawn();
			if(spawn!=null){
				player.setLocation(new Location(player.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
			}else{
				player.sendMessage(Texts.builder("No spawn has been set on this server! Use /setSpawn to set a spawn.").color(TextColors.RED).build());
			}
		}else{
			StaticsHandler.getLogger().warn("Spawn called by non player entity");
		}
		return null;
	}
	
	@Override
	public List<String> getSuggestions(CommandSource arg0, String arg1)
			throws CommandException {
		return Collections.emptyList();
	}
	
	@Override
	public boolean testPermission(CommandSource sender) {
		return true;
	}

	
}



