package net.teamio.taam.content.conveyors;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.conveyors.IConveyorAppliance;
import net.teamio.taam.conveyors.IConveyorApplianceHost;

public abstract class ATileEntityAppliance extends BaseTileEntity implements IConveyorAppliance {

	protected EnumFacing direction = EnumFacing.NORTH;
	
	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setInteger("direction", direction.ordinal());
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		direction = EnumFacing.getFront(tag.getInteger("direction"));
	}

	/*
	 * IRotatable implementation
	 */

	@Override
	public EnumFacing getFacingDirection() {
		return direction;
	}

	@Override
	public EnumFacing getNextFacingDirection() {
		EnumFacing dir = direction;
		for(int i = 0; i < 3; i++) {
			dir = dir.rotateY();
			TileEntity te = worldObj.getTileEntity(pos.offset(dir));
			if(te instanceof IConveyorApplianceHost) {
				return dir;
			}
		}
		return direction;
	}

	@Override
	public void setFacingDirection(EnumFacing direction) {
		if(this.direction != direction) {
			// Only update if necessary
			this.direction = direction;
			// Also do a block update, in case neighboring machines need to know...
			updateState(false, true, true);
		}
	}

}
