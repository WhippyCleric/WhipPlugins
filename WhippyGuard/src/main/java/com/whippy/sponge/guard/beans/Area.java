package com.whippy.sponge.guard.beans;

import java.awt.geom.GeneralPath;
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
	private GeneralPath polygon;

	private String worldName;

	
	private void buildPolygon(){		
		
		Vector3i start = points.get(0);
		
		GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD,points.size());
		polygon.moveTo(start.getX(), start.getZ());
		
		for (int i = 1; i < points.size(); i++) {
			polygon.lineTo(points.get(i).getX(), points.get(i).getZ());
		};
		
		polygon.closePath();
		
		this.polygon = polygon;
	}
	
	
	public Area(){
		points = new ArrayList<Vector3i>();
		height = -1.0;
		base = -1.0;
		rights = new AreaRights();
	}

	public Area(double height, double base){
		points = new ArrayList<Vector3i>();
		this.height = height;
		this.base = base;
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
		buildPolygon();
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
		buildPolygon();
	}


	public boolean isFinalised(){
		return finalised;
	}

	public List<Vector3i> getPoints() {
		return points;
	}

	public boolean contains(WorldLocation worldLocation) {
		if(isFinalised()){
			if(!worldLocation.getWorldName().equals(worldName)){
				return false;
			}
			return polygon.contains(worldLocation.getX(), worldLocation.getZ());
		}else{
			return false;
		}
	}

	public JSONObject toJSONObject() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("areaName", areaName);
		jsonObject.put("worldName", worldName);
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

	public boolean canOpenDoor(String identifier) {
		return rights.canOpenDoor(identifier); 	
	}

	public boolean canOpenChests(String identifier) {
		return rights.canOpenChests(identifier); 	
	}


}
