package net.teamio.taam.content.conveyors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.teamio.taam.Config;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.conveyors.ItemWrapper;
import net.teamio.taam.conveyors.api.IConveyorAwareTE;

/**
 * Conveyor Trash Can.
 * Non-Ticking TE
 * @author founderio
 *
 */
public class TileEntityConveyorTrashCan extends ATileEntityAttachable implements IConveyorAwareTE, IInventory, IRotatable {

	public float fillLevel;
	public TileEntityConveyorTrashCan() {
	}
	
	@Override
	public boolean canUpdate() {
		return false;
	}
	
	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setFloat("fillLevel", fillLevel);
		tag.setInteger("direction", direction.ordinal());
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		fillLevel = tag.getFloat("fillLevel");
		direction = ForgeDirection.getOrientation(tag.getInteger("direction"));
	}

	public void clearOut() {
		fillLevel = 0;
		updateState();
	}
	
	/*
	 * IInventory implementation
	 */
	
	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return null;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		float added = stack.stackSize / (float)stack.getMaxStackSize();
		if(fillLevel + added < Config.pl_trashcan_maxfill) {
			fillLevel += added;
			updateState();
		}
	}

	@Override
	public String getInventoryName() {
		return "tile.taam.productionline_attachable.trashcan.name";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
		return true;
	}

	@Override
	public void openInventory() {
		// Nothing to do.
	}

	@Override
	public void closeInventory() {
		// Nothing to do.
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		float add = stack.stackSize / (float)stack.getMaxStackSize();
		return fillLevel + add < Config.pl_trashcan_maxfill;
	}

	/*
	 * IConveyorAwareTE implementation
	 */

	@Override
	public boolean shouldRenderItemsDefault() {
		return false;
	}

	@Override
	public ForgeDirection getMovementDirection() {
		return ForgeDirection.DOWN;
	}
	
	@Override
	public int insertItemAt(ItemStack item, int slot) {
		// insertItem returns item count unable to insert.
		float added = item.stackSize / (float)item.getMaxStackSize();
		if(fillLevel + added < Config.pl_trashcan_maxfill) {
			fillLevel += added;
			updateState();
			return item.stackSize;
		}
		return 0;
	}
	
	@Override
	public boolean canSlotMove(int slot) {
		return false;
	}
	
	@Override
	public int getMovementProgress(int slot) {
		return 0;
	}

	@Override
	public byte getSpeedsteps() {
		return 1;
	}

	@Override
	public int posX() {
		return xCoord;
	}

	@Override
	public int posY() {
		return yCoord;
	}

	@Override
	public int posZ() {
		return zCoord;
	}

	@Override
	public ItemWrapper getSlot(int slot) {
		return ItemWrapper.EMPTY;
	}

	@Override
	public double getInsertMaxY() {
		return 0.9;
	}

	@Override
	public double getInsertMinY() {
		return 0.3;
	}

	public ForgeDirection getNextSlot(int slot) {
		return ForgeDirection.UNKNOWN;
	}


	
}
