package founderio.taam.content.conveyors;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import codechicken.lib.inventory.InventoryUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.taam.Taam;
import founderio.taam.TaamMain;
import founderio.taam.content.BaseBlock;
import founderio.taam.content.IRotatable;
import founderio.taam.conveyors.ConveyorUtil;
import founderio.taam.conveyors.api.IConveyorApplianceHost;
import founderio.taam.util.TaamUtil;

public class BlockProductionLine extends BaseBlock {
	
	public BlockProductionLine() {
		super(Material.iron);
		this.setHardness(3.5f);
		this.setStepSound(Block.soundTypeMetal);
		this.setHarvestLevel("pickaxe", 1);
		this.setBlockTextureName(Taam.MOD_ID + ":tech_block");
	}

	public String getUnlocalizedName(ItemStack itemStack) {
		int i = itemStack.getItemDamage();

		if (i < 0 || i >= Taam.BLOCK_PRODUCTIONLINE_META.length) {
			i = 0;
		}

		return super.getUnlocalizedName() + "." + Taam.BLOCK_PRODUCTIONLINE_META[i];
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs creativeTab, List list) {
		for (int i = 0; i < Taam.BLOCK_PRODUCTIONLINE_META.length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}
	
	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}
	
	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	public TileEntity createTileEntity(World world, int metadata) {
		if(metadata == 0) {
			// Plain Conveyor
			return new TileEntityConveyor();
		} else if(metadata == 1) {
			// Hopper, Regular
			return new TileEntityConveyorHopper(false);
		} else if(metadata == 2) {
			// Hopper, High-Speed
			return new TileEntityConveyorHopper(true);
		} else if(metadata == 3) {
			// Sieve
			return null;
		} else if(metadata == 4) {
			// Shredder
			return new TileEntityConveyorProcessor(TileEntityConveyorProcessor.Shredder);
		} else if(metadata == 5) {
			// Grinder
			return new TileEntityConveyorProcessor(TileEntityConveyorProcessor.Grinder);
		} else if(metadata == 6) {
			// Crusher
			return new TileEntityConveyorProcessor(TileEntityConveyorProcessor.Crusher);
		}
		return null;
	}
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world,
			int x, int y, int z) {
//		int meta = world.getBlockMetadata(x, y, z);
//		int rotation = meta & 7;
//		ForgeDirection dir = ForgeDirection.getOrientation(rotation);
		
		super.setBlockBoundsBasedOnState(world, x, y, z);
	}
//	@Override
//	public boolean canProvidePower() {
//		return true;
//	}

//	@Override
//	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z,
//			int side) {
//		int meta = world.getBlockMetadata(x, y, z);
//		int rotation = meta & 7;
//		ForgeDirection dir = ForgeDirection.getOrientation(rotation);
//		ForgeDirection sideDir = ForgeDirection.getOrientation(side);
//		if(dir == sideDir) {
//			TileEntitySensor te = ((TileEntitySensor) world.getTileEntity(x, y, z));
//			return te.isPowering();
//		} else {
//			return 0;
//		}
//	}
//	
//	@Override
//	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z,
//			int side) {
//		int meta = world.getBlockMetadata(x, y, z);
//		int rotation = meta & 7;
//		ForgeDirection dir = ForgeDirection.getOrientation(rotation);
//		ForgeDirection sideDir = ForgeDirection.getOrientation(side);
//		if(dir == sideDir) {
//			TileEntitySensor te = ((TileEntitySensor) world.getTileEntity(x, y, z));
//			return te.isPowering();
//		} else {
//			return 0;
//		}
//	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isBlockNormalCube() {
		return false;
	}

	@Override
	public boolean isBlockSolid(IBlockAccess world, int x,
			int y, int z, int side) {
		return false;
	}
	
	@Override
	public void onPostBlockPlaced(World par1World, int par2, int par3,
			int par4, int par5) {
//		updateBlocksAround(par1World, par2, par3, par4);
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world,
			int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		if(meta == 20) {
			// Standalone (not in use at the moment)
			this.maxY = 1;
		} else {
			// Conveyor Machinery
			this.maxY = 0.5f;
		}
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}
	
	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int meta) {
		IInventory inventory = InventoryUtils.getInventory(world, x, y, z);
		if(inventory == null) {
			return 0;
		} else {
			return Container.calcRedstoneFromInventory(inventory);
		}
	}
	
	@Override
	public boolean onBlockActivated(World world, int x,
			int y, int z, EntityPlayer player,
			int side, float hitX, float hitY,
			float hitZ) {
			
		boolean playerHasWrench = TaamUtil.playerHasWrench(player);
		
		//TODO: Handle wrenching somewhere else
		//TODO: Interaction with other mods??
		TileEntity te = world.getTileEntity(x, y, z);
		if(te instanceof IConveyorApplianceHost) {
			if(playerHasWrench) {
				TileEntityConveyor conveyor = (TileEntityConveyor) te;
				if(player.isSneaking()) {
					if(ConveyorUtil.dropAppliance(conveyor, player, world, x, y, z)) {
						conveyor.removeAppliance();
						return true;
					}
				}
			}
		}
		
		if(playerHasWrench && !player.isSneaking() && te instanceof IRotatable) {
			IRotatable rotatable = (IRotatable) te;
			rotatable.setFacingDirection(rotatable.getNextFacingDirection());
			return true;
		}
		if(player.isSneaking()) {
			return false;
		}

		if(!world.isRemote) {
			if(te instanceof TileEntityConveyorHopper) {
				player.openGui(TaamMain.instance, 0, world, x, y, z);
			}
		}
		return true;
	}

//	@Override
//	public int onBlockPlaced(World par1World, int x, int y, int z,
//			int side, float hitx, float hity, float hitz, int meta) {
//		int metaPart = meta & 8;
//        int resultingRotation = side;
//        return metaPart | resultingRotation;
//	}
	
	@Override
	public void breakBlock(World world, int x, int y,
			int z, Block block, int meta) {
		//TODO: Drop Items
		super.breakBlock(world, x, y, z, block, meta);
	}
	
	@Override
	public void onNeighborChange(IBlockAccess world, int x, int y, int z,
			int tileX, int tileY, int tileZ) {
		// TODO Auto-generated method stub
		super.onNeighborChange(world, x, y, z, tileX, tileY, tileZ);
	}
	
}