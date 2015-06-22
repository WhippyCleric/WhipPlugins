package com.whippy.sponge.guard.beans;

import org.json.simple.JSONObject;

public class Rights {
	
	private boolean canBreak;
	private boolean canOpenDoor;
	private boolean canPlace;
	private boolean canOpenChests;
	public Rights(boolean canBreak, boolean canPlace, boolean canOpenDoor, Boolean canOpenChests) {
		super();
		this.canBreak = canBreak;
		this.canPlace = canPlace;
		this.canOpenDoor = canOpenDoor;
		this.canOpenChests = canOpenChests;
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
	public void setCanOpenDoor(boolean canOpenDoor) {
		this.canOpenDoor = canOpenDoor;
	}

	public Object toJSONObject() {
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("canBreak", canBreak);
		jsonObject.put("canPlace", canPlace);
		jsonObject.put("canOpenDoor", canOpenDoor);
		jsonObject.put("canOpenChests", canOpenChests);
		return jsonObject;
	}

	public boolean isCanOpenDoor() {
		return canOpenDoor;
	}
	
	public boolean isCanOpenChests(){
		return canOpenChests;
	}
	
	public void setCanOpenChests(boolean canOpenChests){
		this.canOpenChests  =canOpenChests;
	}
	
}
