package com.whippy.sponge.whip.sponge.testing.harness;

import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import org.mockito.Mockito;
import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import com.google.common.base.Optional;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class SpongeObjectCreator {

	private static final int NUMBER_OF_NAMES = 1219;
	private BiMap<String, String> namesToUid;
	private Game mockGame;
	
	public SpongeObjectCreator(){
		loadNames();
		mockGame = Mockito.mock(Game.class);
	}
	
	public Server mockServer(){
		Server mockServer = Mockito.mock(Server.class);
		when(getMockGame().getServer()).thenReturn(mockServer);
		return mockServer;
	}
	
	public Player createRandomPlayer(){
		Player mockPlayer = Mockito.mock(Player.class);
		int indexToRemove = getRandomNumber(0, namesToUid.size());
		ArrayList<String> names = new ArrayList<String>();
		names.addAll(namesToUid.keySet());
		String name = names.get(indexToRemove);
		String uid = namesToUid.remove(name);
		when(mockPlayer.getName()).thenReturn(name);
		when(mockPlayer.getIdentifier()).thenReturn(uid);
		return mockPlayer;
	}
	
	public Player createRandomPlayerWithObject(ItemStack itemStack){
		Player player = createRandomPlayer();
		Optional<ItemStack> optionalStack = Optional.of(itemStack);
		when(player.getItemInHand()).thenReturn(optionalStack);
		return player;
	}
	
	public ItemStack createMockItemStack(ItemType type, int quantity){
		ItemStack mockStack = Mockito.mock(ItemStack.class);
		when(mockStack.getItem()).thenReturn(type);
		when(mockStack.getQuantity()).thenReturn(quantity);
		return mockStack;
	}
	
	public int getRandomNumber(int low, int high){
		Random r = new Random();
		return r.nextInt(high-low) + low;
	}
	
	private void loadNames(){
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("com/whippy/sponge/whip/sponge/testing/harness/names.csv");
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
	 
		try {
	 
			br = new BufferedReader(new InputStreamReader(in));
			
			namesToUid = HashBiMap.create(NUMBER_OF_NAMES);
			while ((line = br.readLine()) != null) {
			        // use comma as separator
				String[] namesAndUids = line.split(cvsSplitBy);
				namesToUid.put(namesAndUids[0], namesAndUids[1]);
	 
			}
	 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	 
	}

	public Game getMockGame() {
		return mockGame;
	}

}
