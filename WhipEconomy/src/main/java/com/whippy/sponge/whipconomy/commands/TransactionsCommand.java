
package com.whippy.sponge.whipconomy.commands;

import java.util.List;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

import com.whippy.sponge.whipconomy.beans.Payment;
import com.whippy.sponge.whipconomy.beans.StaticsHandler;
import com.whippy.sponge.whipconomy.cache.EconomyCache;

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
            List<Payment> transactions = EconomyCache.getLastTransactions(player, playerName, numberOfTransactions);
			
			if(transactions.size()==0){
				player.sendMessage(Texts.builder("No transactions found").color(TextColors.BLUE).build());
			}else{
				int size = transactions.size();
				if(size < numberOfTransactions){
					for(int i = 0; i<transactions.size(); i++){
						Payment transaction = transactions.get(i);
						player.sendMessage(transaction.toText(player.getName()));
					}
				}else{
					for(int i = transactions.size()-numberOfTransactions; i<transactions.size(); i++){
						Payment transaction = transactions.get(i);
						player.sendMessage(transaction.toText(player.getName()));
					}
				}
			}
	
            
        } else {
        	StaticsHandler.getLogger().warn("Transactions called by non player entity");
        	return CommandResult.empty();
        }
		return CommandResult.success();

    }
}
