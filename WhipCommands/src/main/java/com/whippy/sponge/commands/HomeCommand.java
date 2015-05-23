package com.whippy.sponge.commands;

import java.util.Collections;
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
import org.spongepowered.api.world.Location;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.whippy.sponge.commands.beans.NoWorldLocation;
import com.whippy.sponge.commands.config.CommandConfiguration;

public class HomeCommand implements CommandCallable {

	@Inject
	Logger logger;
	
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
		return Collections.emptyList();
	}

	@Override
	public Text getUsage(CommandSource arg0) {
		return Texts.builder("/home").color(TextColors.GOLD).build();
	}

	@Override
	public Optional<CommandResult> process(CommandSource sender, String arg1) throws CommandException {
		if(sender instanceof Player){
			Player player = (Player) sender;
			NoWorldLocation location = CommandConfiguration.getHome(player);
			if(location!=null){
				player.setLocation(new Location(player.getWorld(), location.getX(), location.getY(), location.getZ()));
			}else{
				player.sendMessage(Texts.builder("You have no home! Use /setHome to set a home.").color(TextColors.RED).build());
			}
		}else{
			logger.warn("Home called by non player entity");
		}
		return null;
	}

	@Override
	public boolean testPermission(CommandSource arg0) {
		return true;
	}

}
