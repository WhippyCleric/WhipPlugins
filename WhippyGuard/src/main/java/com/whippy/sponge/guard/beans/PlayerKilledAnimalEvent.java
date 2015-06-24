package com.whippy.sponge.guard.beans;

import java.util.Date;

import org.json.simple.JSONObject;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.world.Location;

public class PlayerKilledAnimalEvent implements EventLog{

	
	private final WorldLocation location;
	private final Date time;
	private final String playerId;
	private final Living animal;
	
	public PlayerKilledAnimalEvent(Player player, Living animal, Location location, Date time) {
		this.animal = animal;
		this.playerId = player.getIdentifier();
		this.time = time;
		this.location = new WorldLocation(animal.getWorld().getName(), location.getX(), location.getY(), location.getZ());
		
	}

	@Override
	public String getPlayerId() {
		return playerId;
	}

	@Override
	public WorldLocation getLocation() {
		return location;
	}

	@Override
	public EventType getEventType() {
		return EventLog.EventType.ANIMAL_KILL;
	}

	@Override
	public String toJSONString() {
		return getJSONObject().toJSONString();
	}

	@Override
	public JSONObject getJSONObject() {
		JSONObject eventObj = new JSONObject();
		eventObj.put("playerId", playerId);
		eventObj.put("eventType", getEventType().toString());
		eventObj.put("worldName", location.getWorldName());
		eventObj.put("x", location.getX());
		eventObj.put("y", location.getY());
		eventObj.put("z", location.getZ());
		eventObj.put("time", StaticsHandler.getFormatter().format(time));
		
		return eventObj;
	}

	@Override
	public Date getTime() {
		return time;
	}

	@Override
	public void rollbackEvent() {
		// TODO Auto-generated method stub
		
	}

	public Living getAnimal() {
		return animal;
	}

}
