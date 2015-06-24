package com.whippy.sponge.guard.orchestrator;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.whippy.sponge.guard.beans.EventLog;

public class LoggerHandler {

	private static final String EVENT_LOG_PATH = ".\\config\\plugins\\whip\\data\\eventlog.json";;
	private Map<String, List<EventLog>> events;
	
	public LoggerHandler() throws IOException, ParseException{
		events  = new HashMap<String, List<EventLog>>();
		refreshFromFile();
	}
	
	public synchronized void pushEvent(EventLog event){
		if(events.containsKey(event.getPlayerId())){
			List<EventLog> currentEvents = events.remove(event.getPlayerId());
			currentEvents.add(event);
			events.put(event.getPlayerId(), currentEvents);
		}else{
			events.put(event.getPlayerId(), Arrays.asList(event));
		}
		pushFileUpdate();
	}
	
	private synchronized void pushFileUpdate(){
		try {
			JSONObject all = new JSONObject();
			JSONArray eventsJSONArray = new JSONArray();
			for (List<EventLog> eventList : events.values()) {
				for (EventLog event : eventList) {					
					eventsJSONArray.add(event.getJSONObject());
				}
			}
			all.put("allEvents", eventsJSONArray);
			File areasFile = new File(EVENT_LOG_PATH);
			if(!areasFile.exists()) {
				areasFile.getParentFile().mkdirs();
				areasFile.createNewFile();
			} 
			FileWriter file = new FileWriter(EVENT_LOG_PATH);
			
			file.write(all.toJSONString());
			file.flush();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private synchronized void refreshFromFile() throws IOException, ParseException{
		events = new HashMap<String, List<EventLog>>();

		FileReader reader = new FileReader(EVENT_LOG_PATH);
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(reader);
		JSONObject jsonObject = (JSONObject) obj;
		
		Object allEventsObj = jsonObject.get("allEvents");
		JSONArray allEvents = (JSONArray) allEventsObj;
		for (Object eventObj : allEvents) {
			JSONObject eventJson = (JSONObject) eventObj;
			pushEvent(getEventFromJSONObject(eventJson));
		}
	}
	
	private EventLog getEventFromJSONObject(JSONObject eventJson){
		String eventType = (String) eventJson.get("eventType");
		String playerId = (String) eventJson.get("playerId");
		return null;
	}
	
}
