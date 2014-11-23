package founderio.taam;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.taam.blocks.BlockMagnetRail;
import founderio.taam.blocks.BlockOre;
import founderio.taam.blocks.BlockProductionLine;
import founderio.taam.blocks.BlockSensor;
import founderio.taam.blocks.BlockSlidingDoor;
import founderio.taam.blocks.TileEntityConveyor;
import founderio.taam.blocks.TileEntityConveyorHopper;
import founderio.taam.blocks.TileEntitySensor;
import founderio.taam.blocks.multinet.ItemMultinetCable;
import founderio.taam.blocks.multinet.ItemMultinetMultitronix;
import founderio.taam.blocks.multinet.MultinetHandler;
import founderio.taam.blocks.multinet.MultinetPartFactory;
import founderio.taam.blocks.multinet.cables.OperatorRedstone;
import founderio.taam.client.gui.GuiHandler;
import founderio.taam.conveyors.ApplianceRegistry;
import founderio.taam.conveyors.appliances.ApplianceSprayer;
import founderio.taam.fluids.DyeFluid;
import founderio.taam.items.ItemConveyorAppliance;
import founderio.taam.items.ItemDebugTool;
import founderio.taam.items.ItemWithMetadata;
import founderio.taam.items.ItemWrench;
import founderio.taam.multinet.Multinet;


@Mod(modid = Taam.MOD_ID, name = Taam.MOD_NAME, version = Taam.MOD_VERSION, dependencies = "required-after:ForgeMultipart", guiFactory = Taam.GUI_FACTORY_CLASS)
public class TaamMain {
	@Instance(Taam.MOD_ID)
	public static TaamMain instance;

	@SidedProxy(clientSide = "founderio.taam.TaamClientProxy", serverSide = "founderio.taam.TaamCommonProxy")
	public static TaamCommonProxy proxy;

	public static MultinetPartFactory multinetMultipart;
	
	public static ItemMultinetCable itemMultinetCable;
	public static ItemMultinetMultitronix itemMultinetMultitronix;
	public static ItemDebugTool itemDebugTool;
	public static ItemWrench itemWrench;
	public static ItemWithMetadata itemMaterial;
	public static ItemWithMetadata itemPart;
	public static ItemWithMetadata itemIngot;
	public static ItemConveyorAppliance itemConveyorAppliance;
	
	public static CreativeTabs creativeTab;

	public static BlockSensor blockSensor;
	public static BlockProductionLine blockProductionLine;
	public static BlockSlidingDoor blockSlidingDoor;
	public static BlockOre blockOre;
	public static BlockMagnetRail blockMagnetRail;
	
	public static DyeFluid[] fluidsDye;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ModMetadata meta = event.getModMetadata();
		meta.authorList.add(Taam.MOD_AUTHOR1);
		meta.authorList.add(Taam.MOD_AUTHOR2);
		meta.description = Taam.MOD_DESCRIPTION;
		meta.logoFile = Taam.MOD_LOGO_PATH;
		meta.autogenerated = false;
		
		MinecraftForge.EVENT_BUS.register(new MultinetHandler());
		FMLCommonHandler.instance().bus().register(new Config());
				
		Config.init(event.getSuggestedConfigurationFile());
		
		creativeTab = new CreativeTabs(Taam.MOD_ID) {

			@Override
			@SideOnly(Side.CLIENT)
			public ItemStack getIconItemStack() {
				return new ItemStack(blockSensor);
			}
			
			@Override
			@SideOnly(Side.CLIENT)
			public Item getTabIconItem() {
				return null;
			}
		};

		blockSensor = new BlockSensor();
		blockSensor.setBlockName(Taam.BLOCK_SENSOR);
		blockSensor.setCreativeTab(creativeTab);

		blockProductionLine = new BlockProductionLine();
		blockProductionLine.setBlockName(Taam.BLOCK_PRODUCTIONLINE);
		blockProductionLine.setCreativeTab(creativeTab);
		
		blockSlidingDoor = new BlockSlidingDoor();
		blockSlidingDoor.setBlockName(Taam.BLOCK_SLIDINGDOOR);
		blockSlidingDoor.setCreativeTab(creativeTab);
		
		blockOre = new BlockOre();
		blockOre.setBlockName(Taam.BLOCK_ORE);
		blockOre.setCreativeTab(creativeTab);
		
		blockMagnetRail = new BlockMagnetRail();
		blockMagnetRail.setBlockName(Taam.BLOCK_MAGNET_RAIL);
		blockMagnetRail.setCreativeTab(creativeTab);
		
		itemMultinetCable = new ItemMultinetCable();
		itemMultinetCable.setUnlocalizedName(Taam.ITEM_MULTINET_CABLE);
		itemMultinetCable.setCreativeTab(creativeTab);
		
		itemDebugTool = new ItemDebugTool();
		itemDebugTool.setUnlocalizedName(Taam.ITEM_DEBUG_TOOL);
		itemDebugTool.setCreativeTab(creativeTab);
		
		itemWrench = new ItemWrench();
		itemWrench.setUnlocalizedName(Taam.ITEM_WRENCH);
		itemWrench.setCreativeTab(creativeTab);
		
		itemMultinetMultitronix = new ItemMultinetMultitronix();
		itemMultinetMultitronix.setUnlocalizedName(Taam.ITEM_MULTINET_MULTITRONIX);
		itemMultinetMultitronix.setCreativeTab(creativeTab);
		
		itemMaterial = new ItemWithMetadata("material", Taam.ITEM_MATERIAL_META);
		itemMaterial.setUnlocalizedName(Taam.ITEM_MATERIAL);
		itemMaterial.setCreativeTab(creativeTab);
		
		itemPart = new ItemWithMetadata("part", Taam.ITEM_PART_META);
		itemPart.setUnlocalizedName(Taam.ITEM_PART);
		itemPart.setCreativeTab(creativeTab);
		
		itemIngot = new ItemWithMetadata("ingot", Taam.BLOCK_ORE_META);
		itemIngot.setUnlocalizedName(Taam.ITEM_INGOT);
		itemIngot.setCreativeTab(creativeTab);
		
		itemConveyorAppliance = new ItemConveyorAppliance();
		itemConveyorAppliance.setUnlocalizedName(Taam.ITEM_CONVEYOR_APPLIANCE);
		itemConveyorAppliance.setCreativeTab(creativeTab);
		
		Multinet.registerOperator(new OperatorRedstone("redstone"));

		GameRegistry.registerItem(itemMaterial, Taam.ITEM_MATERIAL, Taam.MOD_ID);
		GameRegistry.registerItem(itemPart, Taam.ITEM_PART, Taam.MOD_ID);
		GameRegistry.registerItem(itemIngot, Taam.ITEM_INGOT, Taam.MOD_ID);

		GameRegistry.registerItem(itemMultinetCable, Taam.ITEM_MULTINET_CABLE, Taam.MOD_ID);
		GameRegistry.registerItem(itemDebugTool, Taam.ITEM_DEBUG_TOOL, Taam.MOD_ID);
		GameRegistry.registerItem(itemWrench, Taam.ITEM_WRENCH, Taam.MOD_ID);
		GameRegistry.registerItem(itemMultinetMultitronix, Taam.ITEM_MULTINET_MULTITRONIX, Taam.MOD_ID);

		GameRegistry.registerItem(itemConveyorAppliance, Taam.ITEM_CONVEYOR_APPLIANCE, Taam.MOD_ID);
		
		GameRegistry.registerBlock(blockSensor, ItemBlock.class, Taam.BLOCK_SENSOR);
		GameRegistry.registerBlock(blockProductionLine, null, Taam.BLOCK_PRODUCTIONLINE);
		//TODO: custom item implementation for production line with lore (see ItemConveyorAppliance), because of Lore
		GameRegistry.registerItem(new ItemMultiTexture(blockProductionLine, blockProductionLine, Taam.BLOCK_PRODUCTIONLINE_META), Taam.BLOCK_PRODUCTIONLINE, Taam.MOD_ID);
//		GameRegistry.registerBlock(blockSlidingDoor, ItemBlock.class, Taam.BLOCK_SLIDINGDOOR);
		GameRegistry.registerBlock(blockOre, null, Taam.BLOCK_ORE);
		GameRegistry.registerBlock(blockMagnetRail, ItemBlock.class, Taam.BLOCK_MAGNET_RAIL);
		GameRegistry.registerItem(new ItemMultiTexture(blockOre, blockOre, Taam.BLOCK_ORE_META), Taam.BLOCK_ORE, Taam.MOD_ID);
		
		GameRegistry.registerTileEntity(TileEntitySensor.class, Taam.TILEENTITY_SENSOR);
		GameRegistry.registerTileEntity(TileEntityConveyor.class, Taam.TILEENTITY_CONVEYOR);
		GameRegistry.registerTileEntity(TileEntityConveyorHopper.class, Taam.TILEENTITY_CONVEYOR_HOPPER);
//		GameRegistry.registerTileEntity(TileEntitySlidingDoor.class, Taam.TILEENTITY_SLIDINGDOOR);
		
		GameRegistry.registerWorldGenerator(new OreGenerator(), 2);
		
		ApplianceRegistry.registerFactory(Taam.APPLIANCE_SPRAYER, new ApplianceSprayer.Factory());
		
		fluidsDye = new DyeFluid[Taam.FLUID_DYE_META.length];
		for(int i = 0; i < Taam.FLUID_DYE_META.length; i++) {
			fluidsDye[i] = new DyeFluid(Taam.FLUID_DYE + Taam.FLUID_DYE_META[i]);
			FluidRegistry.registerFluid(fluidsDye[i]);
		}
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {

		multinetMultipart = new MultinetPartFactory();
		proxy.registerRenderStuff();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

		oreRegistration();
		TaamRecipes.addRecipes();
		TaamRecipes.addSmeltingRecipes();
		TaamRecipes.addOreRecipes();
		
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}
	
	public static void oreRegistration(){
		OreDictionary.registerOre("oreCopper", new ItemStack(blockOre, 1, 0));
		OreDictionary.registerOre("oreTin", new ItemStack(blockOre, 1, 1));
		OreDictionary.registerOre("oreAluminum", new ItemStack(blockOre, 1, 2));
		
		OreDictionary.registerOre("ingotCopper", new ItemStack(itemIngot, 1, 0));
		OreDictionary.registerOre("ingotTin", new ItemStack(itemIngot, 1, 1));
		OreDictionary.registerOre("ingotAluminum", new ItemStack(itemIngot, 1, 2));
		
		OreDictionary.registerOre("materialPlastic", new ItemStack(itemMaterial, 1, 0));
		OreDictionary.registerOre("materialRubber", new ItemStack(itemMaterial, 1, 1));
		OreDictionary.registerOre("materialGraphite", new ItemStack(itemMaterial, 1, 2));
		
		OreDictionary.registerOre("partPhotocell", new ItemStack(itemPart, 1, 0));
		OreDictionary.registerOre("partMotor", new ItemStack(itemPart, 1, 1));
	}
}

