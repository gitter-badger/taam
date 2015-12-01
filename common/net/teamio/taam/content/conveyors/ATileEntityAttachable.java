package net.teamio.taam.content.conveyors;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.conveyors.api.IConveyorAwareTE;

public abstract class ATileEntityAttachable extends BaseTileEntity implements IConveyorAwareTE, IRotatable {

	protected ForgeDirection direction = ForgeDirection.NORTH;

	public ATileEntityAttachable() {
		super();
	}

	@Override
	public boolean isSlotAvailable(int slot) {
		switch(direction) {
		default:
		case NORTH:
			return slot == 2 || slot == 5 || slot == 8;
		case EAST:
			return slot == 6 || slot == 7 || slot == 8;
		case SOUTH:
			return slot == 0 || slot == 3 || slot == 6;
		case WEST:
			return slot == 0 || slot == 1 || slot == 2;
		}
	}

	public static boolean canAttach(IBlockAccess world, int x, int y, int z, ForgeDirection dir) {
		TileEntity ent = world.getTileEntity(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
		return ent instanceof IConveyorAwareTE;
	}
	
	/*
	 * IRotatable implementation
	 */
	
	@Override
	public ForgeDirection getFacingDirection() {
		return direction;
	}

	@Override
	public ForgeDirection getMountDirection() {
		return ForgeDirection.DOWN;
	}

	@Override
	public ForgeDirection getNextMountDirection() {
		return ForgeDirection.DOWN;
	}

	@Override
	public void setMountDirection(ForgeDirection direction) {
		// Nope, will not change that.
	}
	
	@Override
	public ForgeDirection getNextFacingDirection() {
		ForgeDirection dir = direction;
		for(int i = 0; i < 3; i++) {
			dir = dir.getRotation(ForgeDirection.UP);
			if(canAttach(worldObj, xCoord, yCoord, zCoord, dir)) {
				return dir;
			}
		}
		return direction;
	}

	@Override
	public void setFacingDirection(ForgeDirection direction) {
		this.direction = direction;
		//if(!worldObj.isRemote) {
			int dir;
			switch(direction) {
			default:
			case NORTH:
				dir = 0;
				break;
			case SOUTH:
				dir = 1;
				break;
			case EAST:
				dir = 2;
				break;
			case WEST:
				dir = 3;
				break;
			}
			int worldMeta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, (worldMeta & 3) + (dir << 2), 3);
			updateState();
		//}
	}

}