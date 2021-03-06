package net.teamio.taam;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.teamio.taam.content.common.BlockOre;
import net.teamio.taam.content.piping.MachineFluidDrier;
import net.teamio.taam.content.piping.MachineMixer;
import net.teamio.taam.content.piping.MachinePipe;
import net.teamio.taam.content.piping.MachinePump;
import net.teamio.taam.content.piping.MachineTank;
import net.teamio.taam.conveyors.IConveyorSlots;
import net.teamio.taam.gui.advanced.IAdvancedMachineGUI;
import net.teamio.taam.machines.IMachine;
import net.teamio.taam.machines.IMachineMetaInfo;
import net.teamio.taam.machines.MachineBlock;
import net.teamio.taam.machines.MachineItemBlock;
import net.teamio.taam.machines.MachineItemMultipart;
import net.teamio.taam.machines.MachineMultipart;
import net.teamio.taam.machines.MachineTileEntity;
import net.teamio.taam.piping.IPipe;
import net.teamio.taam.rendering.TankRenderInfo;

public final class Taam {
	private Taam() {
		//Util Class
	}

	/*
	 * Capabilities
	 *
	 * REMEMBER TO REGISTER THEM in TaamMain, end of preInit!
	 * Else there will be conflicts (null value!)
	 */

	@CapabilityInject(IPipe.class)
	public static Capability<IPipe> CAPABILITY_PIPE;
	@CapabilityInject(TankRenderInfo[].class)
	public static Capability<TankRenderInfo[]> CAPABILITY_RENDER_TANK;
	@CapabilityInject(IConveyorSlots.class)
	public static Capability<IConveyorSlots> CAPABILITY_CONVEYOR;
	@CapabilityInject(IAdvancedMachineGUI.class)
	public static Capability<IAdvancedMachineGUI> CAPABILITY_ADVANCED_GUI;

	public static final String MOD_ID = "taam";
	public static final String MOD_NAME = "Taam";
	public static final String MOD_VERSION = "@VERSION@";
	public static final String MOD_AUTHOR1 = "founderio";
	public static final String MOD_AUTHOR2 = "Xander112";
	public static final String MOD_DESCRIPTION = "Taam: Tech and Acessories Mod\nTaam is open source, puplished under the MIT license. Feel free to poke around the source code! Any support is appreciated, so check out our Patreon page and our website and report any issues on GitHub!";
	public static final String MOD_CREDITS = "Taam is a mod by Team I/O";
	public static final String MOD_URL = "https://team-io.net/taam.php";
	public static final String MOD_UPDATE_URL = "https://team-io.net/taam-updates.php";
	public static final String MOD_LOGO_PATH = "/assets/taam/logo_80dpi_blue.png";

	/**
	 * Network channel name
	 */
	public static final String CHANNEL_NAME = "TAAM";

	public static final String GUI_FACTORY_CLASS = "net.teamio.taam.gui.GuiFactory";

	/*
	 * Integration
	 */

	public static final String INTEGRATION_JEI_CAT_GRINDER = "taam.integration.jei.grinder";
	public static final String INTEGRATION_JEI_CAT_CRUSHER = "taam.integration.jei.crusher";
	public static final String INTEGRATION_JEI_CAT_FLUIDDRIER = "taam.integration.jei.fluiddrier";
	public static final String INTEGRATION_JEI_CAT_MIXER = "taam.integration.jei.mixer";

	public static final String INTEGRATION_JEI_CATNAME_GRINDER = "taam.integration.jei.categories.grinder";
	public static final String INTEGRATION_JEI_CATNAME_CRUSHER = "taam.integration.jei.categories.crusher";
	public static final String INTEGRATION_JEI_CATNAME_FLUIDDRIER = "taam.integration.jei.categories.fluiddrier";
	public static final String INTEGRATION_JEI_CATNAME_MIXER = "taam.integration.jei.categories.mixer";

	public static final String INTEGRATION_JEI_LORE_INTERNAL_CAPACITY = "taam.integration.jei.lore.internalcapacity";
	public static final String INTEGRATION_JEI_LORE_INTERNAL_CAPACITY_IN = "taam.integration.jei.lore.internalcapacity_in";
	public static final String INTEGRATION_JEI_LORE_INTERNAL_CAPACITY_OUT = "taam.integration.jei.lore.internalcapacity_out";

	/*
	 * Sounds
	 */

	public static final ResourceLocation SOUND_SIP_AH = new ResourceLocation("taam", "sip_ah");

	/*
	 * Blocks
	 */

	public static final String BLOCK_LAMP = "lamp";
	public static final String BLOCK_SENSOR = "sensor";
	public static final String BLOCK_MACHINES = "machines";
	public static final String BLOCK_PRODUCTIONLINE = "productionline";
	public static final String BLOCK_PRODUCTIONLINE_ATTACHABLE = "productionline_attachable";
	public static final String BLOCK_PRODUCTIONLINE_APPLIANCE = "productionline_appliance";
	public static final String BLOCK_LOGISTICS = "logistics";
	public static final String BLOCK_SLIDINGDOOR = "slidingdoor";
	public static final String BLOCK_SENSOR_MOTION = "sensor.motion";
	public static final String BLOCK_SENSOR_MINECT = "sensor.minect";
	public static final String BLOCK_ORE = "ore";
	public static final String BLOCK_CONCRETE = "concrete";
	public static final String BLOCK_MAGNET_RAIL = "magnet_rail";
	public static final String BLOCK_SUPPORT_BEAM = "support_beam";

	/**
	 * Wrapper for multipart-based machines, if multipart is not found or disabled via config.
	 *
	 * See {@link MACHINE_META} for the single meta values (not actually block metadata.)
	 */
	public static final String BLOCK_MACHINE_WRAPPER = "machine";

	public static enum BLOCK_ORE_META implements IStringSerializable {
		/*0*/copper		(true, true, "Copper",		14, 7, 0, 59),
		/*1*/tin		(true, true, "Tin",			13, 7, 0, 59),
		/*2*/aluminum	(true, true, "Aluminum",	2,  3,  0, 59),
		/*3*/bauxite	(false, true, "Bauxite",	35, 10, 0, 128),  //No Ingot
		/*4*/kaolinite	(false, true, "Kaolinite", 	35, 5, 0, 100), //No Ingot
		// Reserved for future use as blocks
		/*5*/reserved1	(false, false),
		/*6*/reserved2	(false, false),
		/*7*/reserved3	(false, false),
		/*8*/reserved4	(false, false),
		/*9*/reserved5	(false, false),
		/*10*/reserved6	(false, false),
		/*11*/reserved7	(false, false),
		/*12*/reserved8	(false, false),
		/*13*/reserved9	(false, false),
		/*14*/reserved10(false, false),
		/*15*/reserved11(false, false),

		//Vanilla requires only the "custom" stuff
		/*16*/gold		(false, true),
		/*17*/iron		(false, true),
		/*18*/coal		(false, true),

		// Non-Ore stuff
		/*19*/stone		(false, true),
		;

		public final boolean ore, ingot, dust;

		public final int gen_default_size, gen_default_count, gen_default_above, gen_default_below;

		public final String config_name;

		private BLOCK_ORE_META(boolean ingot, boolean dust) {
			ore = false;
			this.ingot = ingot;
			this.dust = dust;
			gen_default_size = 0;
			gen_default_count = 0;
			gen_default_above = 0;
			gen_default_below = 0;
			config_name = name();
		}

		private BLOCK_ORE_META(boolean ingot, boolean dust, String config_name, int default_size, int default_count, int default_above, int default_below) {
			ore = true;
			this.ingot = ingot;
			this.dust = dust;
			gen_default_size = default_size;
			gen_default_count = default_count;
			gen_default_above = default_above;
			gen_default_below = default_below;
			this.config_name = config_name;
		}

		public static String[] valuesAsString() {
			Enum<?>[] valuesAsEnum = values();
			String[] valuesAsString = new String[valuesAsEnum.length];
			for(int i = 0; i < valuesAsEnum.length; i++) {
				valuesAsString[i] = valuesAsEnum[i].name();
			}
			return valuesAsString;
		}

		public static BLOCK_ORE_META valueOf(int meta) {
			BLOCK_ORE_META[] valuesAsEnum = values();
			if(meta < 0 || meta > valuesAsEnum.length) {
				return copper;
			}
			return valuesAsEnum[meta];
		}

		@Override
		public String getName() {
			return name();
		}

		public static IBlockState getOre(Taam.BLOCK_ORE_META ore) {
			return TaamMain.blockOre.getDefaultState().withProperty(BlockOre.VARIANT, ore);
		}
	}

	public static enum BLOCK_CONCRETE_META implements IStringSerializable {
		rough,
		rough_chiseled,
		fine,
		fine_chiseled,
		coated,
		coated_chiseled,
		black,
		black_chiseled,
		warn1,
		warn2
		;

		public static String[] valuesAsString() {
			Enum<?>[] valuesAsEnum = values();
			String[] valuesAsString = new String[valuesAsEnum.length];
			for(int i = 0; i < valuesAsEnum.length; i++) {
				valuesAsString[i] = valuesAsEnum[i].name();
			}
			return valuesAsString;
		}

		@Override
		public String getName() {
			return name();
		}
	}

	/**
	 * Non-Multipart machines that are not part of the conveyor-system.
	 *
	 * @author Oliver Kahrmann
	 *
	 */
	public static enum BLOCK_MACHINES_META implements IStringSerializable {
		chute,
		creativecache,
		creativewell
		;
		public static String[] valuesAsString() {
			Enum<?>[] valuesAsEnum = values();
			String[] valuesAsString = new String[valuesAsEnum.length];
			for(int i = 0; i < valuesAsEnum.length; i++) {
				valuesAsString[i] = valuesAsEnum[i].name();
			}
			return valuesAsString;
		}

		@Override
		public String getName() {
			return name();
		}
	}

	/**
	 * Productionline-Machines, i.e. part of the conveyor system.
	 *
	 * @author Oliver Kahrmann
	 *
	 */
	public static enum BLOCK_PRODUCTIONLINE_META implements IStringSerializable {
		conveyor1,
		conveyor2,
		conveyor3,
		hopper,
		hopper_hs,
		sieve,
		shredder,
		grinder,
		crusher,
		chute,
		elevator,
		;
		public static String[] valuesAsString() {
			Enum<?>[] valuesAsEnum = values();
			String[] valuesAsString = new String[valuesAsEnum.length];
			for(int i = 0; i < valuesAsEnum.length; i++) {
				valuesAsString[i] = valuesAsEnum[i].name();
			}
			return valuesAsString;
		}

		@Override
		public String getName() {
			return name();
		}
	}

	public static enum BLOCK_PRODUCTIONLINE_ATTACHABLE_META implements IStringSerializable {
		itembag,
		trashcan,
		;
		public static String[] valuesAsString() {
			Enum<?>[] valuesAsEnum = values();
			String[] valuesAsString = new String[valuesAsEnum.length];
			for(int i = 0; i < valuesAsEnum.length; i++) {
				valuesAsString[i] = valuesAsEnum[i].name();
			}
			return valuesAsString;
		}

		@Override
		public String getName() {
			return name();
		}
	}

	public static enum BLOCK_PRODUCTIONLINE_APPLIANCE_META implements IStringSerializable {
		sprayer,
		aligner,
		;
		public static String[] valuesAsString() {
			Enum<?>[] valuesAsEnum = values();
			String[] valuesAsString = new String[valuesAsEnum.length];
			for(int i = 0; i < valuesAsEnum.length; i++) {
				valuesAsString[i] = valuesAsEnum[i].name();
			}
			return valuesAsString;
		}

		@Override
		public String getName() {
			return name();
		}
	}

	public static enum BLOCK_LOGISTICS_META {
		logistics_manager,
		logistics_station
		;
		public static String[] valuesAsString() {
			Enum<?>[] valuesAsEnum = values();
			String[] valuesAsString = new String[valuesAsEnum.length];
			for(int i = 0; i < valuesAsEnum.length; i++) {
				valuesAsString[i] = valuesAsEnum[i].name();
			}
			return valuesAsString;
		}
	}

	public static final String MULTIPART_MULTINET_CABLE = "multinet.cable";
	public static final String MULTIPART_MULTINET_MULTITRONIX = "multinet.multitronix";

	public static final String ITEM_MULTINET_CABLE = "cable";
	public static final String ITEM_DEBUG_TOOL = "debugger";
	public static final String ITEM_WRENCH = "wrench";
	public static final String ITEM_MULTINET_MULTITRONIX = "multitronix";
	public static final String ITEM_MATERIAL = "material";
	public static final String ITEM_PART = "part";
	public static final String ITEM_TOOL = "tool";
	public static final String ITEM_INGOT = "ingot";
	public static final String ITEM_DUST = "dust";
	public static final String ITEM_LOGISTICS_CART = "logistics_cart";

	public static enum ITEM_LOGISTICS_CART_META {
		basic
		;
		public static String[] valuesAsString() {
			Enum<?>[] valuesAsEnum = values();
			String[] valuesAsString = new String[valuesAsEnum.length];
			for(int i = 0; i < valuesAsEnum.length; i++) {
				valuesAsString[i] = valuesAsEnum[i].name();
			}
			return valuesAsString;
		}
	}

	public static enum ITEM_TOOL_META {
		saw
		;
		public static String[] valuesAsString() {
			Enum<?>[] valuesAsEnum = values();
			String[] valuesAsString = new String[valuesAsEnum.length];
			for(int i = 0; i < valuesAsEnum.length; i++) {
				valuesAsString[i] = valuesAsEnum[i].name();
			}
			return valuesAsString;
		}
	}

	public static enum ITEM_MATERIAL_META {
		plastic_sheet,
		rubber_bar,
		graphite,
		iron_nugget,
		silicon_wafer,
		wooden_board,
		aluminum_plate,
		resin,
		cement,
		cementRough,
		pigment_black,
		pigment_red,
		pigment_green,
		pigment_brown,
		pigment_blue,
		pigment_purple,
		pigment_cyan,
		pigment_lightGray,
		pigment_gray,
		pigment_pink,
		pigment_lime,
		pigment_yellow,
		pigment_lightBlue,
		pigment_magenta,
		pigment_orange,
		pigment_white
		;
		public static String[] valuesAsString() {
			Enum<?>[] valuesAsEnum = values();
			String[] valuesAsString = new String[valuesAsEnum.length];
			for(int i = 0; i < valuesAsEnum.length; i++) {
				valuesAsString[i] = valuesAsEnum[i].name();
			}
			return valuesAsString;
		}
	}
	public static enum ITEM_PART_META {
		photocell,
		motor,
		support_frame_wood,
		support_frame,
		circuit_basic,
		circuit_advanced,
		logistics_chip,
		rubber_band,
		pump,
		tank,
		nozzle,
		magnetic_coil,
		iron_frame,
		metal_bearing,
		copper_wire,
		sieve,
		redirector
		;
		public static String[] valuesAsString() {
			Enum<?>[] valuesAsEnum = values();
			String[] valuesAsString = new String[valuesAsEnum.length];
			for(int i = 0; i < valuesAsEnum.length; i++) {
				valuesAsString[i] = valuesAsEnum[i].name();
			}
			return valuesAsString;
		}
	}

	public static final String TILEENTITY_SENSOR = "taam.sensor";
	public static final String TILEENTITY_CHUTE = "taam.chute";
	public static final String TILEENTITY_CREATIVECACHE = "taam.creativecache";
	public static final String TILEENTITY_SLIDINGDOOR = "taam.slidingdoor";

	public static final String TILEENTITY_CONVEYOR = "taam.conveyor";
	public static final String TILEENTITY_CONVEYOR_HOPPER = "taam.conveyor_hopper";
	public static final String TILEENTITY_CONVEYOR_PROCESSOR = "taam.conveyor_processor";
	public static final String TILEENTITY_LOGISTICS_STATION = "taam.logistics_station";
	public static final String TILEENTITY_LOGISTICS_MANAGER = "taam.logistics_manager";
	public static final String TILEENTITY_CONVEYOR_ITEMBAG = "taam.itembag";
	public static final String TILEENTITY_CONVEYOR_TRASHCAN = "taam.trashcan";
	public static final String TILEENTITY_CONVEYOR_SIEVE = "taam.sieve";
	public static final String TILEENTITY_CONVEYOR_ELEVATOR = "taam.elevator";

	public static final String TILEENTITY_CREATIVEWELL = "taam.creativewell";

	public static final String TILEENTITY_APPLIANCE_SPRAYER = "taam.appliance.sprayer";
	public static final String TILEENTITY_APPLIANCE_ALIGNER = "taam.appliance.aligner";

	public static final String TILEENTITY_MACHINE_WRAPPER = "taam.machine_wrapper";

	public static final String ENTITY_LOGISTICS_CART = "taam.logistics_manager";

	public static enum FLUID_DYE_META {
		black,
		red,
		green,
		brown,
		blue,
		purple,
		cyan,
		lightGray,
		gray,
		pink,
		lime,
		yellow,
		lightBlue,
		magenta,
		orange,
		white
		;
		public static String[] valuesAsString() {
			Enum<?>[] valuesAsEnum = values();
			String[] valuesAsString = new String[valuesAsEnum.length];
			for(int i = 0; i < valuesAsEnum.length; i++) {
				valuesAsString[i] = valuesAsEnum[i].name();
			}
			return valuesAsString;
		}
	}

	public static final String FLUID_DYE = "dye_";

	public static enum FLUID_MATERIAL_META {
		concreteFine("concreteFine", 2000, 8000),
		concreteRough("concreteRough", 2000, 10000),
		coating("coating", 900, 4000)
		;

		public final String registryName;
		public final int viscosity;
		public final int density;

		private FLUID_MATERIAL_META(String registryName, int viscosity, int density) {
			this.registryName = registryName;
			this.viscosity = viscosity;
			this.density = density;
		}

		public static String[] valuesAsString() {
			Enum<?>[] valuesAsEnum = values();
			String[] valuesAsString = new String[valuesAsEnum.length];
			for(int i = 0; i < valuesAsEnum.length; i++) {
				valuesAsString[i] = valuesAsEnum[i].name();
			}
			return valuesAsString;
		}
	}

	/**
	 * Meta info for all multipart-based machines. Used for items, the wrapper block & multipart registry.
	 *
	 * {@link MachineBlock} + {@link MachineTileEntity} -> Wrapper block & TE if multipart is not available.
	 *
	 * {@link MachineItemBlock} -> Item for the wrapper block, mutually exclusive with {@link MachineItemMultipart}.
	 * Only one of them is used, depending on availability of multipart.
	 *
	 * {@link MachineMultipart} -> Multipart wrapper for all these machines.
	 *
	 * @author Oliver Kahrmann
	 *
	 */
	public static enum MACHINE_META implements IMachineMetaInfo {

		pipe(MachinePipe.class, "pipe", null),
		tank(MachineTank.class, "tank", null),
		pump(MachinePump.class, "pump", null),
		mixer(MachineMixer.class, "mixer", null),
		fluid_drier(MachineFluidDrier.class, "fluid_drier", null);

		private Class<? extends IMachine> machineClass;
		private String unlocalizedName;
		private String[] info;



		/**
		 * @param machineClass
		 * @param unlocalizedName
		 * @param info
		 */
		private MACHINE_META(Class<? extends IMachine> machineClass, String unlocalizedName, String[] info) {
			this.machineClass = machineClass;
			this.unlocalizedName = unlocalizedName;
			this.info = info;
		}

		/*
		 * IMachineMetaInfo implementation
		 */

		@Override
		public IMachine createMachine() {
			try {
				return machineClass.newInstance();
			} catch (InstantiationException e) {
				Log.error("Could not create machine instance. Returning null. THIS IS AN ERROR, please report!", e);
			} catch (IllegalAccessException e) {
				Log.error("Could not create machine instance. Returning null. THIS IS AN ERROR, please report!", e);
			}
			return null;
		}

		@Override
		public int metaData() {
			return ordinal();
		}

		@Override
		public String unlocalizedName() {
			return unlocalizedName;
		}

		@Override
		public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
			if(info != null) {
				Collections.addAll(tooltip, info);
			}
		}

		/*
		 * IStringSerializable implementation
		 */

		@Override
		public String getName() {
			return unlocalizedName();
		}

		/*
		 * Static stuff
		 */

		private static Map<String, MACHINE_META> nameToInstanceMap = new HashMap<String, MACHINE_META>();

		static {
			for(MACHINE_META value : values()) {
				nameToInstanceMap.put(value.unlocalizedName(), value);
			}
		}

		public static IMachineMetaInfo fromId(String id) {
			return nameToInstanceMap.get(id);
		}

		public static String[] valuesAsString() {
			MACHINE_META[] valuesAsEnum = values();
			String[] valuesAsString = new String[valuesAsEnum.length];
			for(int i = 0; i < valuesAsEnum.length; i++) {
				valuesAsString[i] = valuesAsEnum[i].unlocalizedName();
			}
			return valuesAsString;
		}
	}
}
