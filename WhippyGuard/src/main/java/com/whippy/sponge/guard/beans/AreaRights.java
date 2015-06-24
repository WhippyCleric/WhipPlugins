package com.whippy.sponge.guard.beans;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class AreaRights {

	private Map<String, Rights> playerToRights;
	private boolean defaultCanBreak;
	private boolean defaultCanPlace;
	private boolean defaultCanOpenDoor;
	private boolean defaultCanOpenChests;
	private boolean defaultCanAttackPlayers;
	private boolean defaultCanAttackAnimals;
	
	
	public AreaRights(Map<String, Rights> playerToRights,boolean defaultCanBreak ,boolean defaultCanPlace, boolean defaultCanOpenDoor, boolean defaultCanOpenChests, boolean defaultCanAttackPlayers, boolean defaultCanAttackAnimals) {
		this.playerToRights = playerToRights;
		this.defaultCanPlace = defaultCanPlace;
		this.defaultCanBreak = defaultCanBreak;
		this.defaultCanOpenDoor = defaultCanOpenDoor;
		this.defaultCanOpenChests =defaultCanOpenChests;
		this.defaultCanAttackPlayers = defaultCanAttackPlayers;
		this.defaultCanAttackAnimals = defaultCanAttackAnimals;
	}
	
	public boolean isDefaultCanBreak() {
		return defaultCanBreak;
	}

	public void setDefaultCanBreak(boolean defaultCanBreak) {
		this.defaultCanBreak = defaultCanBreak;
	}

	public boolean isDefaultCanPlace() {
		return defaultCanPlace;
	}

	public void setDefaultCanPlace(boolean defaultCanPlace) {
		this.defaultCanPlace = defaultCanPlace;
	}

	public boolean isDefaultCanOpenDoor() {
		return defaultCanOpenDoor;
	}

	public void setDefaultCanOpenDoor(boolean defaultCanOpenDoor) {
		this.defaultCanOpenDoor = defaultCanOpenDoor;
	}

	public boolean isDefaultCanOpenChests() {
		return defaultCanOpenChests;
	}

	public void setDefaultCanOpenChests(boolean defaultCanOpenChests) {
		this.defaultCanOpenChests = defaultCanOpenChests;
	}

	public boolean isDefaultCanAttackPlayers() {
		return defaultCanAttackPlayers;
	}

	public void setDefaultCanAttackPlayers(boolean defaultCanAttackPlayers) {
		this.defaultCanAttackPlayers = defaultCanAttackPlayers;
	}

	public boolean isDefaultCanAttackAnimals() {
		return defaultCanAttackAnimals;
	}

	public void setDefaultCanAttackAnimals(boolean defaultCanAttackAnimals) {
		this.defaultCanAttackAnimals = defaultCanAttackAnimals;
	}

	public AreaRights() {
		playerToRights  = new HashMap<String, Rights>();
		defaultCanBreak = false;
		defaultCanPlace = false;
		defaultCanOpenDoor  = false;
		defaultCanOpenChests = false;
		defaultCanAttackPlayers = false;
		defaultCanAttackAnimals = false;
	}



	public Map<String, Rights> getPlayerToRights() {
		return playerToRights;
	}

	public JSONObject toJSONObject() {
		JSONObject jsonObject = new JSONObject();
		JSONArray rights = new JSONArray();
		for (String playerId : playerToRights.keySet()) {
			JSONObject playerRightsObject = new JSONObject();
			playerRightsObject .put("playerId", playerId);
			playerRightsObject .put("rights", playerToRights.get(playerId).toJSONObject());
			rights.add(playerRightsObject);
		}
		jsonObject.put("playerRights", rights);
		jsonObject.put("defaultCanPlace", defaultCanPlace);
		jsonObject.put("defaultCanBreak", defaultCanBreak);
		jsonObject.put("defaultCanOpenDoor", defaultCanOpenDoor);
		jsonObject.put("defaultCanOpenChests", defaultCanOpenChests);
		jsonObject.put("defaultCanAttackPlayers", defaultCanAttackPlayers);
		jsonObject.put("defaultCanAttackAnimals", defaultCanAttackAnimals);
		return jsonObject;
	}

	public void givePlayerFullRights(String identifier) {
		if(playerToRights.containsKey(identifier)){
			playerToRights.remove(identifier);
		}
		playerToRights.put(identifier, new Rights(true, true, true, true, true, true));
	}

	public boolean canBreak(String identifier) {
		if(playerToRights.containsKey(identifier)){
			return playerToRights.get(identifier).isCanBreak();
		}else{
			return defaultCanBreak;
		}
	}

	public boolean canPlace(String identifier) {
		if(playerToRights.containsKey(identifier)){
			return playerToRights.get(identifier).isCanPlace();
		}else{
			return defaultCanPlace;
		}
	}

	public boolean canOpenDoor(String identifier) {
		if(playerToRights.containsKey(identifier)){
			return playerToRights.get(identifier).isCanOpenDoor();
		}else{
			return defaultCanOpenDoor;
		}
	}

	public boolean canOpenChests(String identifier) {
		if(playerToRights.containsKey(identifier)){
			return playerToRights.get(identifier).isCanOpenChests();
		}else{
			return defaultCanOpenChests;
		}
	}

	public boolean canAttackAnimals(String identifier) {
		if(playerToRights.containsKey(identifier)){
			return playerToRights.get(identifier).isCanAttackAnimals();
		}else{
			return defaultCanAttackAnimals;
		}
	}

	public boolean canAttackPlayers(String identifier) {
		if(playerToRights.containsKey(identifier)){
			return playerToRights.get(identifier).isCanAttackPlayers();
		}else{
			return defaultCanAttackPlayers;
		}
	}
	
}

