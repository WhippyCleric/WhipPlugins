
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
	public CommandResult execute(CommandSource source, CommandContext context)throws CommandException {

		if(source instanceof Player) {
	            Player player = (Player) source;
	            double bal = EconomyCache.getBalance(player.getIdentifier());
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
	        } else {
	        	StaticsHandler.getLogger().warn("Bal called by non player entity");
	        	return CommandResult.empty();
	        }
	        return CommandResult.success();
	}

}
