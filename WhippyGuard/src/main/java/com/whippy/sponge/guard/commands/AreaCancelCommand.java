package com.whippy.sponge.guard.commands;

import java.util.List;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;

import com.google.common.base.Optional;
import com.whippy.sponge.guard.beans.StaticsHandler;

public class AreaCancelCommand implements CommandCallable{ 
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
			StaticsHandler.getAreaHandler().cancelArea(player);
		}else{
			StaticsHandler.getLogger().warn("Cancel area called by non player entity!");
		}
		return null;
	}

	@Override
	public boolean testPermission(CommandSource arg0) {
		// TODO Auto-generated method stub
		return false;
	}
}