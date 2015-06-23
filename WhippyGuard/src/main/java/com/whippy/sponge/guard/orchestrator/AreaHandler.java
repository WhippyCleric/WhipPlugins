package com.whippy.sponge.guard.orchestrator;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;

import com.flowpowered.math.vector.Vector3i;
import com.whippy.sponge.guard.beans.Area;
import com.whippy.sponge.guard.beans.AreaRights;
import com.whippy.sponge.guard.beans.Rights;
import com.whippy.sponge.guard.beans.WorldLocation;
import com.whippy.sponge.guard.exceptions.AreaFinalisedException;
import com.whippy.sponge.guard.exceptions.MultipleWorldInAreaException;

public class AreaHandler {

	private static final String AREA_DEFINITION_PATH =  ".\\config\\plugins\\whip\\data\\guardareas.json";
	private Map<String, Area> playerIDToAreaInProgress;
	private Map<String, Area> definedAreas;
	
	public AreaHandler(){
		playerIDToAreaInProgress  =new HashMap<String, Area>();
		refreshFromFile();
	}
	
	public boolean addAreaInProgres(Player player, Area area){
		if(playerIDToAreaInProgress.containsKey(player.getIdentifier())){
			return false;
		}else{
			playerIDToAreaInProgress.put(player.getIdentifier(), area);
			return true;
		}
	}
	
	public void playerAreaDefineClick(Player player, WorldLocation worldLocation){
		Area area = playerIDToAreaInProgress.remove(player.getIdentifier());
		if(area!=null){
			try {
				boolean isOverlap = checkOverlap(worldLocation);
				if(!isOverlap){					
					area.addPoint(worldLocation);
					playerIDToAreaInProgress.put(player.getIdentifier(), area);
					sendPointAddedMessage(player, worldLocation);
				}else{					
					playerIDToAreaInProgress.put(player.getIdentifier(), area);
					player.sendMessage(Texts.builder("Area overlaps with another").color(TextColors.RED).build());	
				}
			} catch (AreaFinalisedException | MultipleWorldInAreaException e) {
			}
		}else{
			//Is a new area
			try {
				boolean isOverlap = checkOverlap(worldLocation);
				if(!isOverlap){										
					area = new Area();
					area.addPoint(worldLocation);
					playerIDToAreaInProgress.put(player.getIdentifier(), area);
					sendPointAddedMessage(player, worldLocation);
					player.sendMessage(Texts.builder("New area started created with boundless height and depth").color(TextColors.BLUE).build());	
				}else{					
					player.sendMessage(Texts.builder("Area overlaps with another").color(TextColors.RED).build());	
				}
			} catch (AreaFinalisedException | MultipleWorldInAreaException e) {
				//not possible
			}
		}
	}
	
	public boolean checkOverlap(WorldLocation worldLocation){
		boolean isOverlap = false;
		for (Area area : definedAreas.values()) {			
			isOverlap =  area.contains(worldLocation);
			if(isOverlap){
				return true;
			}
		}
		return isOverlap;
	}
	public boolean checkOverlap(Area areaToFinalise){
		boolean isOverlap = false;
		for (Area area : definedAreas.values()) {			
			isOverlap =  area.overlaps(area);
			if(isOverlap){
				return true;
			}
		}
		return isOverlap;
	}
	
	
	public void finaliseCurrentArea(Player player, String areaName, Double height, Double base){
		Area area = playerIDToAreaInProgress.get(player.getIdentifier());
		if(area!=null){
			if(area.getPoints().size()<=1){				
				player.sendMessage(Texts.builder("Area must be more than 1 point!").color(TextColors.RED).build());	
			}else if(areaName==null || areaName.isEmpty()){
				player.sendMessage(Texts.builder("Must specify area name").color(TextColors.RED).build());
			}else if(definedAreas.containsKey(areaName)){
				player.sendMessage(Texts.builder("Area with name " + areaName + " already exists!").color(TextColors.RED).build());
			}else{
				if(!checkOverlap(area)){					
					area.finalise(areaName,height,base);
					area.giveFullRights(player.getIdentifier());
					definedAreas.put(areaName, area);
					playerIDToAreaInProgress.remove(area);
					pushFileUpdate();
				}else{				
					playerIDToAreaInProgress.remove(player.getIdentifier());
					player.sendMessage(Texts.builder("Area overlaps with another, current seleciton wiped").color(TextColors.RED).build());	
				}
			}
		}else{
			player.sendMessage(Texts.builder("No area defined!").color(TextColors.RED).build());	
		}
	}
	
	
	private void sendPointAddedMessage(Player player, WorldLocation point){
		StringBuilder messageBuilder = new StringBuilder();
		messageBuilder.append("Point x:");
		messageBuilder.append(point.getX());
		messageBuilder.append(" z:");
		messageBuilder.append(point.getZ());
		messageBuilder.append(" added.");
		
		player.sendMessage(Texts.builder(messageBuilder.toString()).color(TextColors.BLUE).build());	
	}
	
	private synchronized void pushFileUpdate(){
		try {
			JSONObject all = new JSONObject();
			all.put("allAreas", areasToJSONArray());
			File areasFile = new File(AREA_DEFINITION_PATH);
			if(!areasFile.exists()) {
				areasFile.getParentFile().mkdirs();
				areasFile.createNewFile();
			} 
			FileWriter file = new FileWriter(AREA_DEFINITION_PATH);
			file.write(all.toJSONString());
			file.flush();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void refreshFromFile(){
		try {
			
			definedAreas = new HashMap<String, Area>();

			FileReader reader = new FileReader(AREA_DEFINITION_PATH);
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(reader);
			JSONObject jsonObject = (JSONObject) obj;
			
			Object areas= jsonObject.get("allAreas");
			JSONArray arrayAreas = (JSONArray) areas;
			for (Object areaObj : arrayAreas) {
				String areaName = (String) ((JSONObject) areaObj).get("areaName");
				String worldName = (String) ((JSONObject) areaObj).get("worldName");
				Double height= (Double) ((JSONObject) areaObj).get("height");
				Double base = (Double)  ((JSONObject) areaObj).get("base");
				JSONArray points = (JSONArray) ((JSONObject) areaObj).get("points");
				List<Vector3i> pointList = new ArrayList<Vector3i>();
				for (Object pointObj : points) {
					Double x = new Double((Long) ((JSONObject) pointObj).get("x"));
					Double y = new Double((Long) ((JSONObject) pointObj).get("y"));
					Double z = new Double((Long) ((JSONObject) pointObj).get("z"));
					
					pointList.add(new Vector3i(x,y,z));
				}
				JSONObject areaRights = (JSONObject) ((JSONObject) areaObj).get("areaRights");
				JSONArray playerRights = (JSONArray) areaRights.get("playerRights");
				Boolean defaultCanPlace= (Boolean) areaRights.get("defaultCanPlace");
				Boolean defaultCanBreak= (Boolean) areaRights.get("defaultCanBreak");
				Boolean defaultCanOpenDoor = (Boolean) areaRights.get("defaultCanOpenDoor");
				Boolean defaultCanOpenChests = (Boolean) areaRights.get("defaultCanOpenChests");
				Map<String, Rights> playerAreaRights = new HashMap<String, Rights>();
				for (Object playerRight : playerRights) {
					String playerId = (String) ((JSONObject) playerRight).get("playerId");
					JSONObject individualRights = (JSONObject) ((JSONObject) playerRight).get("rights");
					Boolean canBreak = (Boolean) individualRights.get("canBreak");
					Boolean canPalce = (Boolean) individualRights.get("canPlace");
					Boolean canOpenDoor = (Boolean) individualRights.get("canOpenDoor");
					Boolean canOpenChests = (Boolean) individualRights.get("canOpenChests");
					playerAreaRights.put(playerId, new Rights(canBreak, canPalce, canOpenDoor, canOpenChests));
				}
				Area area = new Area(areaName, worldName, pointList, height, base, new AreaRights(playerAreaRights,defaultCanBreak, defaultCanPlace, defaultCanOpenDoor, defaultCanOpenChests));
				definedAreas.put(areaName, area);
			}			
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}
	
	public JSONArray areasToJSONArray() {
		JSONArray allAreas = new JSONArray();
		for (Area area : definedAreas.values()) {
			allAreas.add(area.toJSONObject());
		}
		return allAreas;
	}

	public void listAreas(Player player) {
		for (Area area : definedAreas.values()) {
			player.sendMessage(Texts.builder(area.getName()).color(TextColors.BLUE).build());
		}
		
	}

	public boolean canBreak(Player player, Location block, String worldName) {
		for (Area area : definedAreas.values()) {
			boolean isInArea = area.contains(new WorldLocation(worldName, block.getX(), block.getY(), block.getZ()));
			if(isInArea){
				return area.canBreak(player.getIdentifier());
			}
		}
		return true;
		
	}
	public boolean canOpenDoor(Player player, Location block, String worldName) {
		for (Area area : definedAreas.values()) {
			boolean isInArea = area.contains(new WorldLocation(worldName, block.getX(), block.getY(), block.getZ()));
			if(isInArea){
				return area.canOpenDoor(player.getIdentifier());
			}
		}
		return true;
		
	}
	public boolean canOpenChests(Player player, Location block, String worldName) {
		for (Area area : definedAreas.values()) {
			boolean isInArea = area.contains(new WorldLocation(worldName, block.getX(), block.getY(), block.getZ()));
			if(isInArea){
				return area.canOpenChests(player.getIdentifier());
			}
		}
		return true;
		
	}

	public boolean canPlace(Player player, Location block, String worldName) {
		for (Area area : definedAreas.values()) {
			boolean isInArea = area.contains(new WorldLocation(worldName, block.getX(), block.getY(), block.getZ()));
			if(isInArea){
				return area.canPlace(player.getIdentifier());
			}
		}
		return true;
	}
}
