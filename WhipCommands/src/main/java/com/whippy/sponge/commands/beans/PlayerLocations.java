package com.whippy.sponge.commands.beans;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.world.Location;

public class PlayerLocations {
	
	private  Map<String, WorldLocation> playerToLocations;

	
	public PlayerLocations(){
		playerToLocations = new HashMap<String, WorldLocation>();
	}
	
	public WorldLocation getLocation(String playerId){
		return playerToLocations.get(playerId);
	}
	
	public void addPlayer(Player player){
	    if(playerToLocations.containsKey(player.getIdentifier())){
	    	playerToLocations.remove(player.getIdentifier());
	    }
	    Location playerLocation = player.getLocation();
	    WorldLocation noWorldLocation = new WorldLocation(player.getWorld().getName(), playerLocation.getX(), playerLocation.getY(),playerLocation.getZ());
	    playerToLocations.put(player.getIdentifier(), noWorldLocation);
	}
	
	public Map<String, WorldLocation> getPlayerToLocations() {
		return playerToLocations;
	}

	public void setPlayerToLocations(Map<String, WorldLocation> playerToLocations) {
		this.playerToLocations = playerToLocations;
	}

	public JSONArray toJSONArray() {
		JSONArray all = new JSONArray();
		for (String player : playerToLocations.keySet()) {
			JSONObject playerToLocation = new JSONObject();
			WorldLocation location = playerToLocations.get(player);
			playerToLocation.put("playerId", player);
			playerToLocation.put("worldName", location.getWorldName());
			playerToLocation.put("x", location.getX());
			playerToLocation.put("y", location.getY());
			playerToLocation.put("z", location.getZ());
			all.add(playerToLocation);
		}
		return all;
	}
	
	
	public static PlayerLocations fromJSONObject(Object object){
		PlayerLocations playerLocations = new PlayerLocations();
		Map<String, WorldLocation> playerToLocations = new HashMap<String, WorldLocation>();
		if(object!=null){
			JSONArray arrayHomes = (JSONArray) object;
			for (Object arrayHome : arrayHomes) {
				String playerId = (String) ((JSONObject) arrayHome).get("playerId"); 
				String worldName = (String) ((JSONObject) arrayHome).get("worldName"); 
				Double xHome = (Double) ((JSONObject) arrayHome).get("x");
				Double yHome = (Double) ((JSONObject) arrayHome).get("y");
				Double zHome = (Double) ((JSONObject) arrayHome).get("z");
				playerToLocations.put(playerId, new WorldLocation(worldName, xHome, yHome, zHome));
			}
		}
		playerLocations.setPlayerToLocations(playerToLocations);
		return playerLocations;
	}
}
