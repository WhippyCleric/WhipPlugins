package com.whippy.sponge.commands.beans;

import org.slf4j.Logger;
import org.spongepowered.api.Game;


public class StaticsHandler {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

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

}
