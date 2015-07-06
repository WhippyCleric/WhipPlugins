package com.whippy.sponge.whipconomy.orchestrator;

import java.util.UUID;

import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Text.Literal;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

import com.google.common.base.Optional;
import com.whippy.sponge.whipconomy.beans.Payment;
import com.whippy.sponge.whipconomy.beans.StaticsHandler;
import com.whippy.sponge.whipconomy.cache.EconomyCache;
import com.whippy.sponge.whipconomy.cache.PendingNotificaitions;

public class PlayerNotifier {

	public static boolean notify(Payment payment, String playerName){
		Game game = StaticsHandler.getGame();
		Server server = game.getServer();
		Optional<Player> optionalPlayer =  server.getPlayer(UUID.fromString(EconomyCache.getId(playerName)));
		if(optionalPlayer.isPresent()){
			Player player = optionalPlayer.get();
			String receiver = payment.getPlayerNameReceiver();
			String sender = payment.getPlayerNamePayer();
			double amount = payment.getAmount();
			Literal message;
			if(receiver!=null && receiver.equals(playerName)){
				message = Texts.builder("Received " + StaticsHandler.getAmountWithCurrency(amount) + " from " + sender).color(TextColors.GREEN).build();
			}else if(receiver==null){
				message = Texts.builder("Charged " + StaticsHandler.getAmountWithCurrency(amount) + " by the server").color(TextColors.GREEN).build();				
			}else if(sender!=null && sender.equals(playerName)){
				message = Texts.builder("Paid " + StaticsHandler.getAmountWithCurrency(amount) + " to " + receiver).color(TextColors.RED).build();				
			}else{
				message = Texts.builder("Received " + StaticsHandler.getAmountWithCurrency(amount) + " from the server").color(TextColors.GREEN).build();								
			}
			player.sendMessage(message);
			return true;
		}
		else{
			return false;
		}

	}

	public static void notifyEvenIfOffline(Payment payment){
		if(payment.getPlayerNamePayer()!=null){			
			if(!notify(payment, payment.getPlayerNamePayer())){
				PendingNotificaitions.addPayment(EconomyCache.getId(payment.getPlayerNamePayer()), payment);
			}
		}
		if(payment.getPlayerNameReceiver()!=null){			
			if(!notify(payment, payment.getPlayerNameReceiver())){
				PendingNotificaitions.addPayment(EconomyCache.getId(payment.getPlayerNameReceiver()), payment);
			}
		}
	}

}
