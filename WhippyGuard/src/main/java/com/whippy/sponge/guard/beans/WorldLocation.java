package com.whippy.sponge.guard.beans;

import org.json.simple.JSONObject;

public class WorldLocation{

	private double y;
	private double z;
	private double x;
	private String worldName;
	
	public WorldLocation(String worldName, double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.worldName = worldName;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		obj.put("worldName", getWorldName());
		obj.put("x", getX());
		obj.put("y", getY());
		obj.put("z", getZ());
		return obj;
	}

	public String getWorldName() {
		return worldName;
	}

}
