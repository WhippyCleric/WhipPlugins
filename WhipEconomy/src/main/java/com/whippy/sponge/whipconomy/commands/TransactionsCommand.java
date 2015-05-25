
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

import com.google.common.base.Optional;
import com.whippy.cponge.whipconomy.orchestrator.EconomyCache;
import com.whippy.sponge.whipconomy.cache.ConfigurationLoader;
import com.whippy.sponge.whipconomy.exceptions.GetTransactionException;


public class TransactionsCommand implements CommandCallable {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

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
    public List<String> getSuggestions(CommandSource arg0, String arg1) throws CommandException {
        return Collections.emptyList();
    }

    @Override
    public Text getUsage(CommandSource arg0) {
        return Texts.builder("/transactions <number>").color(TextColors.GOLD).build();
    }

    @Override
    public Optional<CommandResult> process(CommandSource sender, String arguments) throws CommandException {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (arguments == null) {
                player.sendMessage(Texts.builder("Must enter number of transactions to print!").color(TextColors.RED).build());
            } else {
                String[] args = arguments.split(" ");
                if (args.length != 1) {
                    player.sendMessage(Texts.builder("Only 1 argument needed for this command!").color(TextColors.RED).build());
                } else {
                    String numberString = args[0];
                    try {
                        if (numberString == null) {
                            numberString = "10";
                        }
                        int number = Integer.parseInt(numberString);
                        if (number <= 0) {
                            number = 10;
                        }
                        EconomyCache.getLastTransactions(player, number);
                    } catch (NumberFormatException e) {
                        process(sender, "10");
                    } catch (GetTransactionException e) {
                        player.sendMessage(Texts.builder(e.getMessage()).color(TextColors.RED).build());
                    }
                }
            }
        } else {
            ConfigurationLoader.getLogger().warn("Transactions called by non player entity");
        }
        return null;
    }

    @Override
    public boolean testPermission(CommandSource arg0) {
        return true;
    }

}
