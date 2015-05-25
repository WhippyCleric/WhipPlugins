/**
 *  Copyright Murex S.A.S., 2003-2015. All Rights Reserved.
 * 
 *  This software program is proprietary and confidential to Murex S.A.S and its affiliates ("Murex") and, without limiting the generality of the foregoing reservation of rights, shall not be accessed, used, reproduced or distributed without the
 *  express prior written consent of Murex and subject to the applicable Murex licensing terms. Any modification or removal of this copyright notice is expressly prohibited.
 */
package com.whippy.sponge.auction.main;

import java.io.IOException;

import com.google.inject.Inject;

import org.json.simple.parser.ParseException;

import org.slf4j.Logger;

import org.spongepowered.api.Game;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.PlayerEnterBedEvent;
import org.spongepowered.api.event.state.ServerStartingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.command.CommandService;


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
        //cmdService.register(this, new SpawnCommand(), "spawn");

    }

}
