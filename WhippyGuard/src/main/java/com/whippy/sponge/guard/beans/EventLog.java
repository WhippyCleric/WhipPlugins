package com.whippy.sponge.guard.beans;

import java.util.Date;

import org.json.simple.JSONObject;

public interface EventLog {
	
	public enum EventType {
	    ANIMAL_KILL, BLOCK_DESTROY, BLOCK_PLACE
	}
	
	public String getPlayerId();
	
	public WorldLocation getLocation();
	
	public EventType getEventType();
	
	public String toJSONString();

	public JSONObject getJSONObject();
	
	public Date getTime();

}
