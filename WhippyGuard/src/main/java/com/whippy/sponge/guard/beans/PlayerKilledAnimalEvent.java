package com.whippy.sponge.guard.beans;

import java.util.Date;

import org.json.simple.JSONObject;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.world.Location;

public class PlayerKilledAnimalEvent implements EventLog{

	
	
	
	public PlayerKilledAnimalEvent(Player player, Entity animal, Location location, Date time) {
		// TODO Auto-generated constructor stub
	}

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
		return EventLog.EventType.ANIMAL_KILL;
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

}
