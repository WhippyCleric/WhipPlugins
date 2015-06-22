package com.whippy.sponge.guard.beans;

import org.json.simple.JSONObject;

public class Rights {
	
	private boolean canBreak;
	
	private boolean canPlace;
	public Rights(boolean canBreak, boolean canPlace) {
		super();
		this.canBreak = canBreak;
		this.canPlace = canPlace;
	}
	
	public boolean isCanBreak() {
		return canBreak;
	}
	
	public void setCanBreak(boolean canBreak) {
		this.canBreak = canBreak;
	}
	
	public boolean isCanPlace() {
		return canPlace;
	}
	
	public void setCanPlace(boolean canPlace) {
		this.canPlace = canPlace;
	}

	public Object toJSONObject() {
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("canBreak", canBreak);
		jsonObject.put("canPlace", canPlace);
		return jsonObject;
	}
	
}
