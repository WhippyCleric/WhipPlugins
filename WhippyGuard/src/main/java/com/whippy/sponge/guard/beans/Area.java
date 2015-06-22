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
	private double height;
	private double base;
	private AreaRights rights;
	

	private String worldName;

	public Area(){
		points = new ArrayList<Vector3i>();
		height = -1.0;
		base = -1.0;
		rights = new AreaRights();
	}

	public Area(String areaName, String worldName, List<Vector3i> points, Double height, Double base, AreaRights playerAreaRights) {
		finalised = true;
		this.areaName = areaName;
		this.worldName = worldName;
		this.points = points;
		this.height = height;
		this.base = base;
		this.rights = playerAreaRights;
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

	public void finalise(String areaName, Double height, Double base){
		this.areaName = areaName;
		this.height = height;
		this.base = base;
		this.finalised = true;
	}


	public boolean isFinalised(){
		return finalised;
	}

	public List<Vector3i> getPoints() {
		return points;
	}

	public boolean contains(WorldLocation worldLocation) {
		int i;
		int j;
		boolean result = false;
		if(!worldLocation.getWorldName().equals(worldName)){
			return false;
		}
		for (i = 0, j = points.size() - 1; i < points.size(); j = i++) {
			if ((points.get(i).getZ() > worldLocation.getZ()) != (points.get(j).getZ() > worldLocation.getZ()) &&
					(worldLocation.getX() < (points.get(j).getX() - points.get(i).getX()) * (worldLocation.getZ() - points.get(i).getZ()) / (points.get(j).getZ()-points.get(i).getZ()) + points.get(i).getX())) {
				result = !result;
			}
		}
		return result;

	}

	public JSONObject toJSONObject() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("areaName", areaName);
		jsonObject.put("worldName", areaName);
		jsonObject.put("height", height);
		jsonObject.put("base", base);
		JSONArray arrayOfPoints = new JSONArray();

		for (Vector3i vector3i : points) {
			JSONObject vector = new JSONObject();
			vector.put("x", vector3i.getX());
			vector.put("y", vector3i.getY());
			vector.put("z", vector3i.getZ());
			arrayOfPoints.add(vector);
		}
		jsonObject.put("points", arrayOfPoints);
		jsonObject.put("areaRights", rights.toJSONObject());
		return jsonObject;
	}

	public boolean overlaps(Area area) {
		boolean doesOverlap = false;
		for (Vector3i vector3i : area.getPoints()) {
			doesOverlap = this.contains(new WorldLocation(area.getName(), vector3i.getX(), vector3i.getY(), vector3i.getZ()));
			if(doesOverlap){
				return true;
			}
		}
		return doesOverlap;
	}

	public void giveFullRights(String identifier) {
		rights.givePlayerFullRights(identifier);
		
	}

	public boolean canBreak(String identifier) {
		return rights.canBreak(identifier); 		
	}

	public boolean canPlace(String identifier) {
		return rights.canPlace(identifier); 		
	}


}
