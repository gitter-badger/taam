package net.teamio.taam;

import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.teamio.taam.content.common.TileEntityChute;
import net.teamio.taam.content.common.TileEntitySensor;
import net.teamio.taam.content.conveyors.TileEntityConveyor;
import net.teamio.taam.content.conveyors.TileEntityConveyorHopper;
import net.teamio.taam.content.conveyors.TileEntityConveyorProcessor;
import net.teamio.taam.rendering.TaamRenderer;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;

public class TaamClientProxy extends TaamCommonProxy {

	public TaamRenderer taamRenderer;

	@Override
	public void registerRenderStuff() {
		taamRenderer = new TaamRenderer();
		
		// Tile Entity Rendering
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySensor.class, taamRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityChute.class, taamRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConveyor.class, taamRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConveyorHopper.class, taamRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConveyorProcessor.class, taamRenderer);
		
		// Item Rendering
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TaamMain.blockSensor), taamRenderer);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TaamMain.blockChute), taamRenderer);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TaamMain.blockProductionLine), taamRenderer);
		MinecraftForgeClient.registerItemRenderer(TaamMain.itemConveyorAppliance, taamRenderer);
		
		// Receive event for Client Ticks
		FMLCommonHandler.instance().bus().register(taamRenderer);

	}
}