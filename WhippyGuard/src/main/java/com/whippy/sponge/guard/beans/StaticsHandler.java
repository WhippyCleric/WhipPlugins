package com.whippy.sponge.guard.beans;

import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.spongepowered.api.Game;

import com.whippy.sponge.guard.orchestrator.AreaHandler;
import com.whippy.sponge.guard.orchestrator.LoggerHandler;

public class StaticsHandler {

	private static AreaHandler clickHandler;
    private static Logger logger;
	private static Game game;
	public static final String BOUNDLESS = "boundless";
	public static final Double BOUNDLESS_NUMBER = -314159.0;
	private static boolean eventLoggerEnabled = true;
	private static LoggerHandler loggerHandler;
	private static SimpleDateFormat formatter = new SimpleDateFormat("ss:mm:hh DDMMYYYY");

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
	public static AreaHandler getAreaHandler(){
		return clickHandler;
	}

	public static void setClickHandler(AreaHandler clickHandlerNew) {
		clickHandler = clickHandlerNew;
	}

	public static boolean isEventLoggerEnabled() {
		return eventLoggerEnabled;
	}

	public static void setEventLoggerEnabled(boolean eventLoggerEnabled) {
		StaticsHandler.eventLoggerEnabled = eventLoggerEnabled;
	}

	public static LoggerHandler getLoggerHandler() {
		return loggerHandler;
	}

	public static void setLoggerHandler(LoggerHandler loggerHandler) {
		StaticsHandler.loggerHandler = loggerHandler;
	}

	public static SimpleDateFormat getFormatter() {
		return formatter;
	}

	public static void setFormatter(SimpleDateFormat formatter) {
		StaticsHandler.formatter = formatter;
	}
	
}
