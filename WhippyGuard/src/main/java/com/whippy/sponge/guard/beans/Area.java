package com.whippy.sponge.guard.beans;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.flowpowered.math.vector.Vector3i;
import com.whippy.sponge.guard.exceptions.AreaFinalisedException;
import com.whippy.sponge.guard.exceptions.MultipleWorldInAreaException;

public class Area {

	private List<Vector3i> points;
	private boolean finalised = false;
	private String areaName;
	
	private String worldName;
	
	public Area(){
		points = new ArrayList<Vector3i>();
	}
	
	public Area(String areaName, String worldName, List<Vector3i> points) {
		finalised = true;
		this.areaName = areaName;
		this.worldName = worldName;
		this.points = points;
	
	}

	public void addPoint(WorldLocation point) throws AreaFinalisedException, MultipleWorldInAreaException {
		if(finalised){
			throw new AreaFinalisedException();
		}else{
			if(getPoints().isEmpty()){
				worldName = point.getWorldName();
				getPoints().add(new Vector3i(point.getX(), point.getY(), point.getZ()));
			}else{
				if(worldName.equals(point.getWorldName())){
					getPoints().add(new Vector3i(point.getX(), point.getY(), point.getZ()));
				}else{
					throw new MultipleWorldInAreaException();
				}
			}
		}
	}
	
	public String getName(){
		return areaName;
	}
	
	public void finalise(String areaName){
		this.areaName = areaName;
		this.finalised = true;
	}
	
	
	public boolean isFinalised(){
		return finalised;
	}

	public List<Vector3i> getPoints() {
		return points;
	}

	public JSONObject toJSONObject() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("areaName", areaName);
		jsonObject.put("worldName", areaName);
		JSONArray arrayOfPoints = new JSONArray();
		
		for (Vector3i vector3i : points) {
			JSONObject vector = new JSONObject();
			vector.put("x", vector3i.getX());
			vector.put("y", vector3i.getY());
			vector.put("z", vector3i.getZ());
			arrayOfPoints.add(vector);
		}
		jsonObject.put("points", arrayOfPoints);
		return jsonObject;
	}

	
}
