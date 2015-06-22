package com.whippy.sponge.guard.main;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.Color;

import org.junit.Before;
import org.junit.Test;
import org.spongepowered.api.entity.EntityInteractionTypes;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.entity.player.PlayerInteractEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.Optional;
import com.whippy.sponge.guard.beans.StaticsHandler;
import com.whippy.sponge.guard.orchestrator.AreaHandler;
import com.whippy.sponge.whip.sponge.testing.harness.SpongeObjectCreator;

public class WhippyGuardTester {

	private static final String MOCK_WORLD = "mockWorld";
	private WhippyGuard whippyGuard;
	private SpongeObjectCreator spongeObjectCreator;
	
	@Before
	public void setup() throws Exception{
		whippyGuard = new WhippyGuard();
		spongeObjectCreator = new SpongeObjectCreator();
		StaticsHandler.setClickHandler(new AreaHandler());
		spongeObjectCreator.mockItemType(ItemTypes.class.getField("BONE"), "Bones", WhippyGuard.WAND_ID);
		spongeObjectCreator.mockEntityInteractionTypes(EntityInteractionTypes.class.getField("USE"), "1", "USE");
		spongeObjectCreator.mockColor(TextColors.class.getField("RED"), Color.RED);
		spongeObjectCreator.mockColor(TextColors.class.getField("GREEN"), Color.GREEN);
		spongeObjectCreator.mockColor(TextColors.class.getField("BLUE"), Color.BLUE);
	}
	
	@Test
	public void testDefineArea() throws Exception{
		PlayerInteractEvent mockEvent = mock(PlayerInteractEvent.class);
		ItemStack mockItemStack = spongeObjectCreator.createMockItemStack(ItemTypes.BONE, 1);
		Player mockPlayer = spongeObjectCreator.createRandomPlayerWithObject(mockItemStack);
		World mockWorld = mock(World.class);
		when(mockEvent.getPlayer()).thenReturn(mockPlayer);
		when(mockPlayer.getWorld()).thenReturn(mockWorld);
		when(mockWorld.getName()).thenReturn(MOCK_WORLD);
		when(mockEvent.getInteractionType()).thenReturn(EntityInteractionTypes.USE);
		Vector3d clickedPositionVector = new Vector3d(0,0,0);
		
		Optional<Vector3d> clickedPosition = Optional.of(clickedPositionVector );
		when(mockEvent.getClickedPosition()).thenReturn(clickedPosition );
		whippyGuard.onPlayerInteractEvent(mockEvent );
		whippyGuard.onPlayerInteractEvent(mockEvent );
		
		Vector3d clickedPositionVector2 = new Vector3d(1,2,3);
		Optional<Vector3d> clickedPosition2 = Optional.of(clickedPositionVector2 );
		when(mockEvent.getClickedPosition()).thenReturn(clickedPosition2 );
		whippyGuard.onPlayerInteractEvent(mockEvent );
		whippyGuard.onPlayerInteractEvent(mockEvent );
		
		StaticsHandler.getAreaHandler().finaliseCurrentArea(mockPlayer, "TestArea2", -1.0 ,-1.0);
		
		
		Vector3d clickedPositionVector3 = new Vector3d(0,0,2);
		Optional<Vector3d> clickedPosition3 = Optional.of(clickedPositionVector3 );
		when(mockEvent.getClickedPosition()).thenReturn(clickedPosition3 );
		whippyGuard.onPlayerInteractEvent(mockEvent );
		whippyGuard.onPlayerInteractEvent(mockEvent );
		
	}
	
	
	
}
