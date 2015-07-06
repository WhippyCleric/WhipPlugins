
package com.whippy.sponge.whipconomy.commands;


import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

import com.whippy.sponge.whipconomy.beans.StaticsHandler;
import com.whippy.sponge.whipconomy.cache.ConfigurationLoader;
import com.whippy.sponge.whipconomy.cache.EconomyCache;


public class BalCommand implements CommandExecutor {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

	@Override
	public CommandResult execute(CommandSource source, CommandContext args)throws CommandException {

		
		
		if(source instanceof Player) {
			Player player = (Player) source;
			String playerName;
			if(args.hasAny("playerName")){
				if(player.hasPermission("whippyconomy.bal.others")){            		
					playerName = (String) args.getOne("playerName").get();
				}else{
					player.sendMessage(Texts.builder("You do not have permission to use that command").color(TextColors.RED).build());
					return CommandResult.empty();
				}
			}else{
				playerName = player.getName();
			}
			String playerId = EconomyCache.getId(playerName);
			if(playerId==null || playerId.isEmpty()){
				player.sendMessage(Texts.builder(playerName + " does not exist!").color(TextColors.RED).build());
			}else{				
				double bal = EconomyCache.getBalance(playerId);
				bal = EconomyCache.round(bal, ConfigurationLoader.getDecPlaces());
				StringBuilder messageBuilder = new StringBuilder();
				messageBuilder.append("Balance: ");
				if (!ConfigurationLoader.isAppendCurrency()) {
					messageBuilder.append(ConfigurationLoader.getCurrency());
					messageBuilder.append(bal);
				} else {
					messageBuilder.append(bal);
					messageBuilder.append(ConfigurationLoader.getCurrency());
				}
				player.sendMessage(Texts.builder(messageBuilder.toString()).color(TextColors.GREEN).build());
			}
	        } else {
	        	StaticsHandler.getLogger().warn("Bal called by non player entity");
	        	return CommandResult.empty();
	        }
	        return CommandResult.success();
	}

}
