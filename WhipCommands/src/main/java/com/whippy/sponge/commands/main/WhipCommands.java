package com.whippy.sponge.commands.main;
import java.io.IOException;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.PlayerEnterBedEvent;
import org.spongepowered.api.event.state.ServerStartingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.command.CommandService;

import com.google.inject.Inject;
import com.whippy.sponge.commands.BedCommand;
import com.whippy.sponge.commands.HomeCommand;
import com.whippy.sponge.commands.SetHomeCommand;
import com.whippy.sponge.commands.SetSpawnCommand;
import com.whippy.sponge.commands.SpawnCommand;
import com.whippy.sponge.commands.beans.StaticsHandler;
import com.whippy.sponge.commands.config.CommandConfiguration;

@Plugin(id = "WhipCommands", name = "WhipCommands")
public class WhipCommands {

	@Inject
	Game game;

	@Inject
	Logger logger;
	
	@Subscribe
    public void onServerStarting(ServerStartingEvent event) throws IOException, ParseException  {
		StaticsHandler.setLogger(logger);
		CommandConfiguration.refreshFromFile();
    }
	
	@Subscribe
	public void onPreInitializationEvent(ServerStartingEvent event) {
		CommandService cmdService = game.getCommandDispatcher();
		cmdService.register(this, new SpawnCommand(), "spawn");
		cmdService.register(this, new SetSpawnCommand(), "setSpawn");
		cmdService.register(this, new SetHomeCommand(), "setHome");
		cmdService.register(this, new HomeCommand(), "home");
		cmdService.register(this, new BedCommand(), "bed");
	}
	
	@Subscribe
	public void onPlayerEnterBed(PlayerEnterBedEvent event) {
		CommandConfiguration.setBedHome(event.getPlayer());
	}
	
	

}
