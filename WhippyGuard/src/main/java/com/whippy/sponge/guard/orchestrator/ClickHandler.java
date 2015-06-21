package com.whippy.sponge.guard.orchestrator;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

import com.flowpowered.math.vector.Vector3i;
import com.whippy.sponge.guard.beans.Area;
import com.whippy.sponge.guard.beans.WorldLocation;
import com.whippy.sponge.guard.exceptions.AreaFinalisedException;
import com.whippy.sponge.guard.exceptions.MultipleWorldInAreaException;

public class ClickHandler {

	private static final String CONFIG_PATH =  ".\\config\\plugins\\whip\\data\\guardareas.json";
	private Map<String, Area> playerIDToAreaInProgress;
	private List<Area> definedAreas;
	
	public ClickHandler(){
		refreshFromFile();
	}
	
	public void playerAreaDefineClick(Player player, WorldLocation worldLocation){
		Area area = playerIDToAreaInProgress.get(player.getIdentifier());
		if(area!=null){
			try {
				area.addPoint(worldLocation);
			} catch (AreaFinalisedException | MultipleWorldInAreaException e) {
				player.sendMessage(Texts.builder(e.getMessage()).color(TextColors.RED).build());	
			}
		}else{
			//Is a new area
			try {
				area = new Area();
				area.addPoint(worldLocation);
				sendPointAddedMessage(player, worldLocation);
			} catch (AreaFinalisedException | MultipleWorldInAreaException e) {
				//not possible
			}
		}
	}
	
	
	public void finaliseCurrentArea(Player player, String areaName){
		Area area = playerIDToAreaInProgress.get(player.getIdentifier());
		if(area!=null){
			if(area.getPoints().size()<=1){				
				player.sendMessage(Texts.builder("Area must be more than 1 point!").color(TextColors.RED).build());	
			}else{
				area.finalise(areaName);
				definedAreas.add(area);
				playerIDToAreaInProgress.remove(area);
				pushFileUpdate();
			}
		}else{
			player.sendMessage(Texts.builder("No area defined!").color(TextColors.RED).build());	
		}
	}
	
	
	private void sendPointAddedMessage(Player player, WorldLocation point){
		StringBuilder messageBuilder = new StringBuilder();
		messageBuilder.append("Point x:");
		messageBuilder.append(point.getX());
		messageBuilder.append(" y:");
		messageBuilder.append(point.getY());
		messageBuilder.append(" z:");
		messageBuilder.append(point.getZ());
		messageBuilder.append(" added.");
		
		player.sendMessage(Texts.builder(messageBuilder.toString()).color(TextColors.BLUE).build());	
	}
	
	private synchronized void pushFileUpdate(){
		try {
			JSONObject all = new JSONObject();
			all.put("allAreas", areasToJSONArray());
			FileWriter file = new FileWriter(CONFIG_PATH);
			file.write(all.toJSONString());
			file.flush();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void refreshFromFile(){
		try {
			definedAreas = new ArrayList<Area>();

			FileReader reader = new FileReader(CONFIG_PATH);
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(reader);
			JSONObject jsonObject = (JSONObject) obj;
			
			Object areas= jsonObject.get("allAreas");
			JSONArray arrayAreas = (JSONArray) areas;
			for (Object areaObj : arrayAreas) {
				String areaName = (String) ((JSONObject) areaObj).get("areaName");
				String worldName = (String) ((JSONObject) areaObj).get("worldName");
				JSONArray points = (JSONArray) ((JSONObject) areaObj).get("points");
				List<Vector3i> pointList = new ArrayList<Vector3i>();
				for (Object pointObj : points) {
					Double x = (Double) ((JSONObject) pointObj).get("x");
					Double y = (Double) ((JSONObject) pointObj).get("y");
					Double z = (Double) ((JSONObject) pointObj).get("z");
					pointList.add(new Vector3i(x,y,z));
				}
				Area area = new Area(areaName, worldName, pointList);
				definedAreas.add(area);
			}			
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}
	
	public JSONArray areasToJSONArray() {
		JSONArray allAreas = new JSONArray();
		for (Area area : definedAreas) {
			allAreas.add(area.toJSONObject());
		}
		return allAreas;
	}
}