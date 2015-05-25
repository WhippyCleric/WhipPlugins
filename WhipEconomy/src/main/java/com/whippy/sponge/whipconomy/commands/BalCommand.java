/**
 *  Copyright Murex S.A.S., 2003-2015. All Rights Reserved.
 * 
 *  This software program is proprietary and confidential to Murex S.A.S and its affiliates ("Murex") and, without limiting the generality of the foregoing reservation of rights, shall not be accessed, used, reproduced or distributed without the
 *  express prior written consent of Murex and subject to the applicable Murex licensing terms. Any modification or removal of this copyright notice is expressly prohibited.
 */
package com.whippy.sponge.whipconomy.commands;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Optional;
import com.google.inject.Inject;

import com.typesafe.config.ConfigList;

import com.whippy.cponge.whipconomy.orchestrator.EconomyCache;
import com.whippy.sponge.whipconomy.cache.ConfigurationLoader;

import org.slf4j.Logger;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;


public class BalCommand implements CommandCallable {

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
        return Texts.builder("/bal").color(TextColors.GOLD).build();
    }

    @Override
    public Optional<CommandResult> process(CommandSource sender, String arguments) throws CommandException {
        if (sender instanceof Player) {
            Player player = (Player) sender;
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
            ConfigurationLoader.getLogger().warn("Bal called by non player entity");
        }
        return null;
    }

    @Override
    public boolean testPermission(CommandSource arg0) {
        return true;
    }

}
