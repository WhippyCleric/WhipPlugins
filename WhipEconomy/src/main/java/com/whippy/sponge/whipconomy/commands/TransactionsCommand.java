
package com.whippy.sponge.whipconomy.commands;

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
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

import com.google.common.base.Optional;
import com.whippy.sponge.whipconomy.beans.StaticsHandler;
import com.whippy.sponge.whipconomy.cache.EconomyCache;
import com.whippy.sponge.whipconomy.exceptions.GetTransactionException;


public class TransactionsCommand implements CommandExecutor{

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------


        

	@Override
	public CommandResult execute(CommandSource sender, CommandContext args)throws CommandException {
		if (sender instanceof Player) {
            
			Player player = (Player) sender;
            String playerName;
			int numberOfTransactions;
            
            
            if(args.getOne("numberOfTransactions").isPresent()){
            	numberOfTransactions = (Integer) args.getOne("numberOfTransactions").get();
            }else{
            	numberOfTransactions = 10;
            }
            if(args.getOne("playerName").isPresent()){
            	if(player.hasPermission("whippyconomy.accHistory.others")){            		
            		playerName = (String) args.getOne("playerName").get();
            	}else{
            		player.sendMessage(Texts.builder("You do not have permission to sue that command").color(TextColors.RED).build());
            		return CommandResult.empty();
            	}
            }else{
            	playerName = player.getName();
            }
	
            
        } else {
        	StaticsHandler.getLogger().warn("Transactions called by non player entity");
        	return CommandResult.empty();
        }
		return CommandResult.success();

    }
}
