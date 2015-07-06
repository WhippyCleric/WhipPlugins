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

import com.whippy.sponge.whipconomy.beans.InternalTransfer;
import com.whippy.sponge.whipconomy.beans.StaticsHandler;
import com.whippy.sponge.whipconomy.cache.EconomyCache;

public class SavingsAccHistoryCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource sender, CommandContext commandArgs) throws CommandException {
	
		if (sender instanceof Player) {
            
			Player player = (Player) sender;
			int numberOfTransfers;

            if(commandArgs.getOne("numberOfTransfers").isPresent()){
            	numberOfTransfers = (Integer) commandArgs.getOne("numberOfTransfers").get();
            }else{
            	numberOfTransfers = 10;
            }
            List<InternalTransfer> transfers = EconomyCache.getSavingsHistory(player);
			
			if(transfers.size()==0){
				player.sendMessage(Texts.builder("No transfers found").color(TextColors.BLUE).build());
			}else{
				int size = transfers.size();
				if(size < numberOfTransfers){
					for(int i = 0; i<transfers.size(); i++){
						InternalTransfer transfer = transfers.get(i);
						player.sendMessage(transfer.toText(player.getName()));
					}
				}else{
					for(int i = transfers.size()-numberOfTransfers; i<transfers.size(); i++){
						InternalTransfer transfer = transfers.get(i);
						player.sendMessage(transfer.toText(player.getName()));
					}
				}
			}
        } else {
        	StaticsHandler.getLogger().warn("Savings account history called by non player entity");
        	return CommandResult.empty();
        }
		return CommandResult.success();
	}

}
