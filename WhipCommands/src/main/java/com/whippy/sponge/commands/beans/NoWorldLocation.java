package com.whippy.sponge.commands.beans;

import org.json.simple.JSONObject;

public class NoWorldLocation{

	private double y;
	private double z;
	private double x;
	
	public NoWorldLocation(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
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
		obj.put("x", getX());
		obj.put("y", getY());
		obj.put("z", getZ());
		return obj;
	}
	
}
