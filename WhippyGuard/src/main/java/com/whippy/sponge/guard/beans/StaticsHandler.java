package com.whippy.sponge.guard.beans;

import org.slf4j.Logger;
import org.spongepowered.api.Game;

import com.whippy.sponge.guard.orchestrator.AreaHandler;

public class StaticsHandler {

	private static AreaHandler clickHandler;
    private static Logger logger;
	private static Game game;

	public static Logger getLogger() {
		return logger;
	}

	public static void setLogger(Logger loggerNew) {
		logger = loggerNew;
	}
	
	public static Game getGame(){
		return game;
	}
	
	public static void setGame(Game gameNew){
		game = gameNew;
	}
	public static AreaHandler getClickHandler(){
		return clickHandler;
	}

	public static void setClickHandler(AreaHandler clickHandlerNew) {
		clickHandler = clickHandlerNew;
	}
	
}
