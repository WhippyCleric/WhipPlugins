package com.whippy.sponge.guard.beans;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class AreaRights {

	private Map<String, Rights> playerToRights;

	
	
	public AreaRights(Map<String, Rights> playerToRights) {
		this.playerToRights = playerToRights;
	}
	
	public AreaRights() {
		playerToRights  =new HashMap<String, Rights>();
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
		return jsonObject;
	}

	public void givePlayerFullRights(String identifier) {
		if(playerToRights.containsKey(identifier)){
			playerToRights.remove(identifier);
		}
		playerToRights.put(identifier, new Rights(true, true));
	}
	
}

