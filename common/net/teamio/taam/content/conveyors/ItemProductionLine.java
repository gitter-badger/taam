package net.teamio.taam.content.conveyors;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.teamio.taam.Taam;
import net.teamio.taam.content.IRenderableItem;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.content.common.TileEntityChute;
import net.teamio.taam.conveyors.api.IConveyorAwareTE;

public class ItemProductionLine extends ItemMultiTexture implements IRenderableItem {

	public ItemProductionLine(Block block, String[] names) {
		super(block, block, names);
	}

	@Override
	public List<String> getVisibleParts(ItemStack stack) {
		int meta = stack.getMetadata();
		Taam.BLOCK_PRODUCTIONLINE_META variant = Taam.BLOCK_PRODUCTIONLINE_META.values()[meta];
		switch (variant) {
		case chute:
			return TileEntityChute.parts_conveyor_version;
		case conveyor1:
			return TileEntityConveyor.parts_1;
		case conveyor2:
			return TileEntityConveyor.parts_2;
		case conveyor3:
			return TileEntityConveyor.parts_3;
		case hopper:
			return TileEntityConveyorHopper.parts_regular;
		case hopper_hs:
			return TileEntityConveyorHopper.parts_hs;
		case sieve:
			return TileEntityConveyorSieve.parts;
		case crusher:
			return TileEntityConveyorProcessor.parts_crusher;
		case grinder:
			return TileEntityConveyorProcessor.parts_grinder;
		case shredder:
			return TileEntityConveyorProcessor.parts_shredder;
		default:
			return TileEntityConveyor.parts_invalid;
		}
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side,
			float hitX, float hitY, float hitZ, IBlockState newState) {
		EnumFacing dir = side.getOpposite();
		EnumFacing placeDir = EnumFacing.NORTH;
		boolean defaultPlacement = false;

		if (dir == EnumFacing.UP || dir == EnumFacing.DOWN) {
			defaultPlacement = true;
		} else {
			TileEntity ent = world.getTileEntity(pos.offset(dir));
			if (ent instanceof IRotatable) {
				EnumFacing otherDir = ((IRotatable) ent).getFacingDirection();
				if (otherDir == dir || otherDir == dir.getOpposite()) {
					placeDir = otherDir;
				} else {
					placeDir = dir;
				}
			} else if (ent instanceof IConveyorAwareTE) {
				placeDir = dir;
			} else {
				defaultPlacement = true;
			}
		}

		if (defaultPlacement) {
			// We hit top/bottom of a block
			double xDist = player.posX - pos.getX();
			double zDist = player.posZ - pos.getZ();
			if (Math.abs(xDist) > Math.abs(zDist)) {
				if (xDist < 0) {
					placeDir = EnumFacing.EAST;
				} else {
					placeDir = EnumFacing.WEST;
				}
			} else {
				if (zDist < 0) {
					placeDir = EnumFacing.SOUTH;
				} else {
					placeDir = EnumFacing.NORTH;
				}
			}
		}

		Taam.BLOCK_PRODUCTIONLINE_META variant = (Taam.BLOCK_PRODUCTIONLINE_META) newState.getValue(BlockProductionLine.VARIANT);

		boolean canStay;
		if (variant == Taam.BLOCK_PRODUCTIONLINE_META.conveyor1
				|| variant == Taam.BLOCK_PRODUCTIONLINE_META.conveyor2
				|| variant == Taam.BLOCK_PRODUCTIONLINE_META.conveyor3) {
			// Conveyor
			canStay = BlockProductionLine.canBlockStay(world, pos, placeDir);
		} else {
			canStay = BlockProductionLine.canBlockStay(world, pos, (EnumFacing) null);
		}

		if (canStay) {
			boolean success = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
			if (success) {
				TileEntity te = world.getTileEntity(pos);
				if (te instanceof IRotatable) {
					((IRotatable) te).setFacingDirection(placeDir);
				}
			}
			return success;
		}
		return false;
	}

}
