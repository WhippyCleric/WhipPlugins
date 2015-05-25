
package com.whippy.sponge.auction.main;

import java.io.IOException;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.ServerStartingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.command.CommandService;

import com.google.inject.Inject;
import com.whippy.sponge.auction.commands.AucCommand;


@Plugin(id = "WhipAuction", name = "WhipAuction")
public class WhipAuction {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    @Inject
    Game game;

    @Inject
    Logger logger;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    @Subscribe
    public void onServerStarting(ServerStartingEvent event) throws IOException, ParseException {

    }

    @Subscribe
    public void onPreInitializationEvent(ServerStartingEvent event) {
        CommandService cmdService = game.getCommandDispatcher();
        cmdService.register(this, new AucCommand(), "auc");

    }

}
