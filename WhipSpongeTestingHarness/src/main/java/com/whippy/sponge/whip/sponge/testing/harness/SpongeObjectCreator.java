package com.whippy.sponge.whip.sponge.testing.harness;

import static org.mockito.Mockito.when;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Random;

import org.mockito.Mockito;
import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.entity.EntityInteractionType;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.translation.Translation;

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
	
	public void mockItemType(Field field, final String name, final String id) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException{
		 field.setAccessible(true);
		 ItemType itemType = new ItemType(){
		 @Override
		 public String getId() {
					return id;
				}

				@Override
				public Translation getTranslation() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public <T extends Property<?, ?>> Optional<T> getDefaultProperty(
						Class<T> arg0) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public int getMaxStackQuantity() {
					return 64;
				}

				@Override
				public String getName() {
					return name;
				}
		    };
		    Field modifiersField = Field.class.getDeclaredField("modifiers");
		    modifiersField.setAccessible(true);
		    modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		    
		    field.set(null, itemType);
	}
	
	public void mockEntityInteractionTypes(Field field,final String id, final String name) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException{
		 field.setAccessible(true);
		 EntityInteractionType entityInteractionType = new EntityInteractionType(){

			@Override
			public String getId() {
				// TODO Auto-generated method stub
				return id;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return name;
			}
			 
		 };
		 
		 Field modifiersField = Field.class.getDeclaredField("modifiers");
		 modifiersField.setAccessible(true);
		 modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		 field.set(null, entityInteractionType);
	}
	
	public void mockColor(Field field, final Color color) throws Exception {
	    field.setAccessible(true);
	    TextColor.Base textColor = new TextColor.Base(){
			@Override
			public String getId() {
				return color.toString();
			}
			@Override
			public String getName() {
				return color.toString();
			}
			@Override
			public Color getColor() {
				return color;
			}
			@Override
			public char getCode() {
				return 0;
			}};
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			 modifiersField.setAccessible(true);
			 modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
	    field.set(null, textColor);
	}

}
