package com.whippy.sponge.guard.beans;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class AreaRights {

	private Map<String, Rights> playerToRights;
	private boolean defaultCanBreak;
	private boolean defaultCanPlace;

	
	
	public AreaRights(Map<String, Rights> playerToRights,boolean defaultCanBreak ,boolean defaultCanPlace) {
		this.playerToRights = playerToRights;
		this.defaultCanPlace = defaultCanPlace;
		this.defaultCanBreak = defaultCanBreak;
	}
	
	public AreaRights() {
		playerToRights  =new HashMap<String, Rights>();
		defaultCanBreak = false;
		defaultCanPlace = false;
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
		return jsonObject;
	}

	public void givePlayerFullRights(String identifier) {
		if(playerToRights.containsKey(identifier)){
			playerToRights.remove(identifier);
		}
		playerToRights.put(identifier, new Rights(true, true));
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
	
}

