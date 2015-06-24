package com.whippy.sponge.guard.beans;

import org.json.simple.JSONObject;

public class Rights {
	
	private boolean canBreak;
	private boolean canOpenDoor;
	private boolean canPlace;
	private boolean canOpenChests;
	private boolean canAttackPlayers;
	private boolean canAttackAnimals;
	
	public Rights(boolean canBreak, boolean canPlace, boolean canOpenDoor, boolean canOpenChests, boolean canAttackPlayers, boolean canAttackAnimals) {
		super();
		this.canBreak = canBreak;
		this.canPlace = canPlace;
		this.canOpenDoor = canOpenDoor;
		this.canOpenChests = canOpenChests;
		this.canAttackPlayers = canAttackPlayers;
		this.canAttackAnimals = canAttackAnimals;
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
		jsonObject.put("canAttackAnimals", canAttackAnimals);
		jsonObject.put("canAttackPlayers", canAttackPlayers);
		return jsonObject;
	}

	public boolean isCanAttackPlayers() {
		return canAttackPlayers;
	}

	public void setCanAttackPlayers(boolean canAttackPlayers) {
		this.canAttackPlayers = canAttackPlayers;
	}

	public boolean isCanAttackAnimals() {
		return canAttackAnimals;
	}

	public void setCanAttackAnimals(boolean canAttackAnimals) {
		this.canAttackAnimals = canAttackAnimals;
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
