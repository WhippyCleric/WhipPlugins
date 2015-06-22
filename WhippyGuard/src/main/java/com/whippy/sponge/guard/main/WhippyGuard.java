package com.whippy.sponge.guard.main;
import java.io.IOException;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.EntityInteractionTypes;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.PlayerBreakBlockEvent;
import org.spongepowered.api.event.entity.player.PlayerInteractEvent;
import org.spongepowered.api.event.state.ServerStartingEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.command.CommandService;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.whippy.sponge.guard.beans.StaticsHandler;
import com.whippy.sponge.guard.beans.WorldLocation;
import com.whippy.sponge.guard.commands.FinaliseCommand;
import com.whippy.sponge.guard.commands.ListAreasCommand;
import com.whippy.sponge.guard.orchestrator.AreaHandler;

@Plugin(id = "WhippyGuard", name = "WhippyGuard")
public class WhippyGuard {

	public static final String WAND_ID = "minecraft:bone";
	private static final boolean RIGHT_CLICK_BUG=true;
	private boolean inRightClick = false;
	

	@Inject
	Game game;

	@Inject
	Logger logger;
	
	@Subscribe
    public void onServerStarting(ServerStartingEvent event) throws IOException, ParseException  {	
		StaticsHandler.setClickHandler(new AreaHandler());
		StaticsHandler.setGame(game);
		StaticsHandler.setLogger(logger);
    }
	
	@Subscribe
	public void onPreInitializationEvent(ServerStartingEvent event) {
		CommandService cmdService = game.getCommandDispatcher();
		cmdService.register(this, new FinaliseCommand(), "areaCommit");
		cmdService.register(this, new ListAreasCommand(), "listAreas");
	}
	
	
	@Subscribe 
	public void onPlayerBreakBlockEvent(PlayerBreakBlockEvent event){
		Player player = event.getPlayer();
		Location block = event.getBlock();
		String worldName = player.getWorld().getName();
		if(!StaticsHandler.getAreaHandler().isAllowed(player, block, worldName)){
			event.setCancelled(true);
			player.sendMessage(Texts.builder("You do not have permission to do that in this area").color(TextColors.RED).build());	
		}
	
	}
	
	@Subscribe
	public void onPlayerInteractEvent(PlayerInteractEvent event){
		Optional<ItemStack> itemInHand = event.getPlayer().getItemInHand();
		if(itemInHand.isPresent()){
			ItemType item = itemInHand.get().getItem();
			String id = item.getId();
			if(id.equals(WAND_ID)){
				if(event.getInteractionType().equals(EntityInteractionTypes.USE)){	
					if((RIGHT_CLICK_BUG && !inRightClick) || !RIGHT_CLICK_BUG){
						inRightClick = true;
						if(event.getClickedPosition().isPresent()){
							Vector3d vectorPoint = event.getClickedPosition().get();
							World world = event.getPlayer().getWorld();
							WorldLocation point = new WorldLocation(world.getName(), vectorPoint.getX(), vectorPoint.getY(), vectorPoint.getZ());
							StaticsHandler.getAreaHandler().playerAreaDefineClick( event.getPlayer(), point);
						}
					}else if (RIGHT_CLICK_BUG){
						inRightClick = false;						
					}
				}else if(event.getInteractionType().equals(EntityInteractionTypes.ATTACK)){
					event.getEntity().sendMessage(Texts.builder("leftclicked").color(TextColors.RED).build());					
				}
				event.setCancelled(true);
			}
		}
	}
	
	

}
