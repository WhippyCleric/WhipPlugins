package com.whippy.sponge.commands.config;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.world.Location;

import com.whippy.sponge.commands.beans.WorldLocation;
import com.whippy.sponge.commands.beans.PlayerLocations;

public class CommandConfiguration {

	public static final String CONFIG_PATH = ".\\config\\plugins\\whip\\data\\whipcommands.json";
	public static final String BED_HOMES = "bedHomes";
	public static final String HOMES = "homes";
	private static WorldLocation spawn;
	private static PlayerLocations bedHomes;
	private static PlayerLocations homes;

	public synchronized static WorldLocation getSpawn() {
		return spawn;
	}

	public synchronized static void setSpawn(WorldLocation spawn2) {
		spawn = spawn2;
		pushFileUpdate();
	}
	
	public synchronized static void setBedHome(Player player){
		if(bedHomes==null){
			bedHomes = new PlayerLocations();
		}
		bedHomes.addPlayer(player);
	    pushFileUpdate();
	}
	
	public synchronized static void setHome(Player player){
		if(homes==null){
			homes = new PlayerLocations();
		}
		homes.addPlayer(player);
		pushFileUpdate();
	}
	
	public synchronized static WorldLocation getHome(Player player){
		if(homes!=null){
			return homes.getLocation(player.getIdentifier());
		}else{
			return null;
		}
	}
	
	public synchronized static WorldLocation getBedHome(Player player){
		if(bedHomes!=null){
			return bedHomes.getLocation(player.getIdentifier());
		}else{
			return null;
		}
	}
	
	private synchronized static void pushFileUpdate(){
		try {
			JSONObject all = new JSONObject();
			FileWriter file = new FileWriter(CONFIG_PATH);
			all.put("spawn", spawn.toJSONObject());
			if(bedHomes!=null){
				all.put(BED_HOMES, bedHomes.toJSONArray());
			}
			if(homes!=null){
				all.put(HOMES, homes.toJSONArray());
			}
			file.write(all.toJSONString());
			file.flush();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized static void refreshFromFile(){
		try {
			FileReader reader = new FileReader(CONFIG_PATH);
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(reader);
			JSONObject jsonObject = (JSONObject) obj;
			
			Object spawnObj = jsonObject.get("spawn");
			if(spawnObj!=null){
				JSONObject spawnJson = (JSONObject) spawnObj; 
				String worldName = (String) spawnJson.get("worldName");
				Double x = (Double) spawnJson.get("x");
				Double y = (Double) spawnJson.get("y");
				Double z = (Double) spawnJson.get("z");	
				spawn = new WorldLocation(worldName, x,y,z);
			}
			
			Object homesFromFile = jsonObject.get(HOMES);
			homes = PlayerLocations.fromJSONObject(homesFromFile);
			
			Object bedHomesFromFile = jsonObject.get(BED_HOMES);
			bedHomes = PlayerLocations.fromJSONObject( bedHomesFromFile);
			
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}
}
