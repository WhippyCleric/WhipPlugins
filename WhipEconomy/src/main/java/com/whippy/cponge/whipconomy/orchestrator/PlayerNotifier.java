package com.whippy.cponge.whipconomy.orchestrator;

import java.util.UUID;

import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Text;

import com.google.common.base.Optional;
import com.whippy.sponge.whipconomy.cache.ConfigurationLoader;

public class PlayerNotifier {
	
	public boolean notify(Text message, UUID playerId){
		Game game = ConfigurationLoader.getGame();
		Server server = game.getServer();
		Optional<Player> optionalPlayer =  server.getPlayer(playerId);
		if(optionalPlayer.isPresent()){
			Player player = optionalPlayer.get();
			player.sendMessage(message);
			return true;
		}
		else{
			return false;
		}
	
	}
	
}
