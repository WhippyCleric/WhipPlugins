package com.whippy.sponge.guard.beans;

import java.util.Date;

import org.json.simple.JSONObject;

public class PlayerDestroyBlockEvent implements EventLog {

	@Override
	public String getPlayerId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WorldLocation getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventType getEventType() {
		return EventLog.EventType.BLOCK_DESTROY;
	}

	@Override
	public String toJSONString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject getJSONObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void rollbackEvent() {
		// TODO Auto-generated method stub
		
	}

}
