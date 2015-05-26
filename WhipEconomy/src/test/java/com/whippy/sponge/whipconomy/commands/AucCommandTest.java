package com.whippy.sponge.whipconomy.commands;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.spongepowered.api.Server;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text.Literal;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.util.command.CommandException;

import com.google.common.base.Optional;
import com.whippy.sponge.whip.sponge.testing.harness.SpongeObjectCreator;
import com.whippy.sponge.whipconomy.beans.StaticsHandler;
import com.whippy.sponge.whipconomy.cache.ConfigurationLoader;
import com.whippy.sponge.whipconomy.orchestrator.AuctionRunner;
import com.whippy.sponge.whipconomy.orchestrator.Auctioneer;

@RunWith(PowerMockRunner.class)
@PrepareForTest({TextColors.class, StaticsHandler.class, ConfigurationLoader.class})
public class AucCommandTest {

	
	private static SpongeObjectCreator objectCreator;
	
	@BeforeClass
	public static void setup() throws NoSuchFieldException, SecurityException, Exception{
		objectCreator = new SpongeObjectCreator();
		mockColor(TextColors.class.getField("RED"), Color.RED);
		mockColor(TextColors.class.getField("GREEN"), Color.GREEN);
		mockColor(TextColors.class.getField("BLUE"), Color.BLUE);
		mockItemType(ItemTypes.class.getField("BONE"), "Bones");
		ConfigurationLoader.init();
        
	}
	@Test
	public void testEmptyArguments() throws CommandException{
		final Player player = objectCreator.createRandomPlayer();
		testMessageSend(player, "[WhipAuction] Invalid command format, no arguments received", new CommandExecution(){
			@Override
			public void execute() throws CommandException {
				AucCommand command = new AucCommand();
				command.process(player, "");
			}
		});
	}
	
	
	@Test
	public void testNullArguments() throws CommandException{
		final Player player = objectCreator.createRandomPlayer();
		testMessageSend(player, "[WhipAuction] Invalid command format, no arguments received", new CommandExecution(){
			@Override
			public void execute() throws CommandException {
				AucCommand command = new AucCommand();
				command.process(player, null);
			}
		});
	}
	
	@Test
	public void testAuctionNoBids() throws CommandException, InterruptedException{	
		Auctioneer auctioneer = new Auctioneer(4);
		StaticsHandler.setAuctioneer(auctioneer);
		StaticsHandler.setGame(objectCreator.getMockGame());
		ItemStack itemStack = objectCreator.createMockItemStack(ItemTypes.BONE, 1);
		Player player = objectCreator.createRandomPlayerWithObject(itemStack);
		Server server = objectCreator.mockServer();
		AuctionRunner runner = new AuctionRunner();
		runner.start();
		
		ArgumentCaptor<Literal> playerMessageCaptor = ArgumentCaptor.forClass(Literal.class);
		ArgumentCaptor<Literal> broadcastCaptor = ArgumentCaptor.forClass(Literal.class);
		AucCommand command = new AucCommand();
		
		command.process(player, "s 1 1 1 45");
		Thread.sleep(47000);
		verify(player, times(1)).sendMessage(playerMessageCaptor.capture());
		verify(server, times(7)).broadcastMessage(broadcastCaptor.capture());
		
		Literal capturedPlayerMessage = playerMessageCaptor.getValue();
		List<Literal> broadcasted = broadcastCaptor.getAllValues();
		List<String> expectedBroadcasts = new ArrayList<String>();
		expectedBroadcasts.add("[WhipAuction] " +  player.getName() +" is auctioning 1 Bones. Starting bid: 1.0. Increment: 1.0. This auction will last 31 seconds.");
		expectedBroadcasts.add("[WhipAuction] 30 seconds remaining");
		expectedBroadcasts.add("[WhipAuction] 10 seconds remaining");
		expectedBroadcasts.add("[WhipAuction] 3 seconds remaining");
		expectedBroadcasts.add("[WhipAuction] 2 seconds remaining");
		expectedBroadcasts.add("[WhipAuction] 1 seconds remaining");
		expectedBroadcasts.add("[WhipAuction] Auction completed with no bids");

		for(int i=0 ; i<7; i++){
			assertEquals(broadcasted.get(i).getContent(), expectedBroadcasts.get(i));
		}
		
		assertEquals(capturedPlayerMessage.getContent(), "[WhipAuction] Auction queued number 0 in line");
	}
	
	@Test
	public void testSellWithNegativeStartBid() throws InterruptedException, CommandException{
		Auctioneer auctioneer = new Auctioneer(4);
		StaticsHandler.setAuctioneer(auctioneer);
		StaticsHandler.setGame(objectCreator.getMockGame());
		ItemStack itemStack = objectCreator.createMockItemStack(ItemTypes.BONE, 1);
		Player player = objectCreator.createRandomPlayerWithObject(itemStack);
		AuctionRunner runner = new AuctionRunner();
		runner.start();
		
		ArgumentCaptor<Literal> playerMessageCaptor = ArgumentCaptor.forClass(Literal.class);
		
		AucCommand command = new AucCommand();
		
		command.process(player, "s 1 -1 1 45");
		verify(player, times(1)).sendMessage(playerMessageCaptor.capture());
		Literal capturedPlayerMessage = playerMessageCaptor.getValue();
		assertEquals("[WhipAuction] Starting bid must be a greater than 0" , capturedPlayerMessage.getContent());
	}
	@Test
	public void testSellWithNegativeIncrement() throws InterruptedException, CommandException{
		Auctioneer auctioneer = new Auctioneer(4);
		StaticsHandler.setAuctioneer(auctioneer);
		StaticsHandler.setGame(objectCreator.getMockGame());
		ItemStack itemStack = objectCreator.createMockItemStack(ItemTypes.BONE, 1);
		Player player = objectCreator.createRandomPlayerWithObject(itemStack);
		AuctionRunner runner = new AuctionRunner();
		runner.start();
		
		ArgumentCaptor<Literal> playerMessageCaptor = ArgumentCaptor.forClass(Literal.class);
		
		AucCommand command = new AucCommand();
		
		command.process(player, "s 1 1 -1 45");
		verify(player, times(1)).sendMessage(playerMessageCaptor.capture());
		Literal capturedPlayerMessage = playerMessageCaptor.getValue();
		assertEquals("[WhipAuction] Increment must be greater than 0" , capturedPlayerMessage.getContent());
	}
	@Test
	public void testSellWithTooLowTime() throws InterruptedException, CommandException{
		Auctioneer auctioneer = new Auctioneer(4);
		StaticsHandler.setAuctioneer(auctioneer);
		StaticsHandler.setGame(objectCreator.getMockGame());
		ItemStack itemStack = objectCreator.createMockItemStack(ItemTypes.BONE, 1);
		Player player = objectCreator.createRandomPlayerWithObject(itemStack);
		AuctionRunner runner = new AuctionRunner();
		runner.start();
		
		ArgumentCaptor<Literal> playerMessageCaptor = ArgumentCaptor.forClass(Literal.class);
		
		AucCommand command = new AucCommand();
		
		command.process(player, "s 1 1 1 44");
		verify(player, times(1)).sendMessage(playerMessageCaptor.capture());
		Literal capturedPlayerMessage = playerMessageCaptor.getValue();
		assertEquals("[WhipAuction] Time must be at least 45 seconds" , capturedPlayerMessage.getContent());
	}
	@Test
	public void testSellWithTooMuchTime() throws InterruptedException, CommandException{
		Auctioneer auctioneer = new Auctioneer(4);
		StaticsHandler.setAuctioneer(auctioneer);
		StaticsHandler.setGame(objectCreator.getMockGame());
		ItemStack itemStack = objectCreator.createMockItemStack(ItemTypes.BONE, 1);
		Player player = objectCreator.createRandomPlayerWithObject(itemStack);
		AuctionRunner runner = new AuctionRunner();
		runner.start();
		
		ArgumentCaptor<Literal> playerMessageCaptor = ArgumentCaptor.forClass(Literal.class);
		
		AucCommand command = new AucCommand();
		
		command.process(player, "s 1 1 1 91");
		verify(player, times(1)).sendMessage(playerMessageCaptor.capture());
		Literal capturedPlayerMessage = playerMessageCaptor.getValue();
		assertEquals("[WhipAuction] Time must be at most 90 seconds" , capturedPlayerMessage.getContent());
	}
	
	@Test
	public void testSellWithNegativeItemCount() throws InterruptedException, CommandException{
		Auctioneer auctioneer = new Auctioneer(4);
		StaticsHandler.setAuctioneer(auctioneer);
		StaticsHandler.setGame(objectCreator.getMockGame());
		ItemStack itemStack = objectCreator.createMockItemStack(ItemTypes.BONE, 1);
		Player player = objectCreator.createRandomPlayerWithObject(itemStack);
		AuctionRunner runner = new AuctionRunner();
		runner.start();
		
		ArgumentCaptor<Literal> playerMessageCaptor = ArgumentCaptor.forClass(Literal.class);
		
		AucCommand command = new AucCommand();
		
		command.process(player, "s -1 1 1 45");
		verify(player, times(1)).sendMessage(playerMessageCaptor.capture());
		Literal capturedPlayerMessage = playerMessageCaptor.getValue();
		assertEquals("[WhipAuction] Must hold at least 1 item to auction" , capturedPlayerMessage.getContent());
	}
	
	@Test
	public void testAuctionMultipleBids() throws CommandException, InterruptedException{
		Auctioneer auctioneer = new Auctioneer(4);
		StaticsHandler.setAuctioneer(auctioneer);
		StaticsHandler.setGame(objectCreator.getMockGame());
		ItemStack itemStack = objectCreator.createMockItemStack(ItemTypes.BONE, 1);
		Player player = objectCreator.createRandomPlayerWithObject(itemStack);
		Server server = objectCreator.mockServer();
		AuctionRunner runner = new AuctionRunner();
		runner.start();
		ArgumentCaptor<Literal> broadcastCaptor = ArgumentCaptor.forClass(Literal.class);
		AucCommand command = new AucCommand();
		command.process(player, "s 1 1 1 45");
		Thread.sleep(15000);
		BidCommand bidCommand = new BidCommand();
		
		Player bidder = objectCreator.createRandomPlayer();
		bidCommand.process(bidder, "0.5");
		Thread.sleep(100);
		bidCommand.process(bidder, "0.5 11.2");
		Thread.sleep(100);
		Player bidder2 = objectCreator.createRandomPlayer();
		bidCommand.process(bidder2, "12 20");
		Thread.sleep(100);
		ArgumentCaptor<Literal> bidder3Captor = ArgumentCaptor.forClass(Literal.class);
		Player bidder3 = objectCreator.createRandomPlayer();
		bidCommand.process(bidder3, "15");
		Thread.sleep(100);
		ArgumentCaptor<Literal> bidderCaptor = ArgumentCaptor.forClass(Literal.class);
		bidCommand.process(bidder, "10");
		Thread.sleep(100);
		bidCommand.process(bidder, "14.5");
		Thread.sleep(100);
		bidCommand.process(bidder, "5 20");
		Thread.sleep(1000);
		bidCommand.process(bidder, "5 200");
		Thread.sleep(30000);
		
		List<String> expectedBroadcasts = new ArrayList<String>();
		expectedBroadcasts.add("[WhipAuction] "+ player.getName() +" is auctioning 1 Bones. Starting bid: 1.0. Increment: 1.0. This auction will last 45 seconds.");
		expectedBroadcasts.add("[WhipAuction] 30 seconds remaining");
		expectedBroadcasts.add("[WhipAuction] " + bidder.getName() + " bids 1.0");
		expectedBroadcasts.add("[WhipAuction] " + bidder2.getName() + " bids 12.2");
		expectedBroadcasts.add("[WhipAuction] " + bidder.getName() + " bids 21.0");
		expectedBroadcasts.add("[WhipAuction] 10 seconds remaining");
		expectedBroadcasts.add("[WhipAuction] 3 seconds remaining");
		expectedBroadcasts.add("[WhipAuction] 2 seconds remaining");
		expectedBroadcasts.add("[WhipAuction] 1 seconds remaining");
		expectedBroadcasts.add("[WhipAuction] "+ bidder.getName() +" won the auction with a bid of 21.0");
		
		verify(server, times(10)).broadcastMessage(broadcastCaptor.capture());
		verify(bidder3, times(1)).sendMessage(bidder3Captor.capture());
		verify(bidder, times(4)).sendMessage(bidderCaptor.capture());
		
		assertEquals("[WhipAuction] You have been automatically outbid", bidder3Captor.getValue().getContent());
		
		List<Literal> broadcasts = broadcastCaptor.getAllValues();
		for(int i=0 ; i<10; i++){
			System.out.println(broadcasts.get(i).getContent());
			assertEquals(expectedBroadcasts.get(i), broadcasts.get(i).getContent());
		}

		List<Literal> bidderMessages = bidderCaptor.getAllValues();
		List<String> expectedBidderMessages = new ArrayList<String>();
		expectedBidderMessages.add("[WhipAuction] Bid too low");
		expectedBidderMessages.add("[WhipAuction] Bid too low");
		expectedBidderMessages.add("[WhipAuction] You have been automatically outbid");;
		expectedBidderMessages.add("[WhipAuction] You have been automatically outbid");;
		for(int i=0 ; i<4; i++){
			System.out.println(bidderMessages.get(i).getContent());
			assertEquals(expectedBidderMessages.get(i), bidderMessages.get(i).getContent());
		}
	}
	
	@Test
	public void testAuction1Bid() throws CommandException, InterruptedException{
		Auctioneer auctioneer = new Auctioneer(4);
		StaticsHandler.setAuctioneer(auctioneer);
		StaticsHandler.setGame(objectCreator.getMockGame());
		ItemStack itemStack = objectCreator.createMockItemStack(ItemTypes.BONE, 1);
		Player player = objectCreator.createRandomPlayerWithObject(itemStack);
		Server server = objectCreator.mockServer();
		AuctionRunner runner = new AuctionRunner();
		runner.start();
		ArgumentCaptor<Literal> broadcastCaptor = ArgumentCaptor.forClass(Literal.class);
		AucCommand command = new AucCommand();
		command.process(player, "s 1 1 1 45");
		Thread.sleep(15000);
		BidCommand bidCommand = new BidCommand();
		Player bidder = objectCreator.createRandomPlayer();
		bidCommand.process(bidder, "10");
		Thread.sleep(32000);
		
		List<String> expectedBroadcasts = new ArrayList<String>();
		expectedBroadcasts.add("[WhipAuction] "+ player.getName() +" is auctioning 1 Bones. Starting bid: 1.0. Increment: 1.0. This auction will last 45 seconds.");
		expectedBroadcasts.add("[WhipAuction] 30 seconds remaining");
		expectedBroadcasts.add("[WhipAuction] " + bidder.getName() + " bids 10.0");
		expectedBroadcasts.add("[WhipAuction] 10 seconds remaining");
		expectedBroadcasts.add("[WhipAuction] 3 seconds remaining");
		expectedBroadcasts.add("[WhipAuction] 2 seconds remaining");
		expectedBroadcasts.add("[WhipAuction] 1 seconds remaining");
		expectedBroadcasts.add("[WhipAuction] "+ bidder.getName() +" won the auction with a bid of 10.0");
		
		verify(server, times(8)).broadcastMessage(broadcastCaptor.capture());
		List<Literal> broadcasts = broadcastCaptor.getAllValues();
		for(int i=0 ; i<8; i++){
			assertEquals(expectedBroadcasts.get(i), broadcasts.get(i).getContent());
		}
	}
	
	private void testMessageSend(Player player, String expectedMessage, CommandExecution command) throws CommandException{
		ArgumentCaptor<Literal> textCaptor = ArgumentCaptor.forClass(Literal.class);
		command.execute();
		verify(player, times(1)).sendMessage(textCaptor.capture());
		Literal capturedText = textCaptor.getValue();
		assertEquals(capturedText.getContent(), expectedMessage);
	}

	private static void mockItemType(Field field, final String name) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException{
		 field.setAccessible(true);
		 ItemType itemType = new ItemType(){
		 @Override
		 public String getId() {
					return "1";
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
	
	private static void mockColor(Field field, final Color color) throws Exception {
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
	    field.set(null, textColor);
	}
	
	public interface CommandExecution{
		public void execute() throws CommandException;
	}
	
}
