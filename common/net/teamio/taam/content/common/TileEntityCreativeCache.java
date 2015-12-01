package net.teamio.taam.content.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IWorldInteractable;

public class TileEntityCreativeCache extends BaseTileEntity implements IInventory, IWorldInteractable {
	
	private ItemStack template = null;

	public void setTemplate(ItemStack stack) {
		if(stack == null) {
			template = null;
		} else {
			template = stack.copy();
		}
	}
	
	public ItemStack getTemplate() {
		return template;
	}
	
	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		if(template != null) {
			NBTTagCompound templateTag = new NBTTagCompound();
			template.writeToNBT(templateTag);
			tag.setTag("template", templateTag);
		}
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		NBTTagCompound templateTag = tag.getCompoundTag("template");
		if(templateTag != null) {
			template = ItemStack.loadItemStackFromNBT(templateTag);
		} else {
			template = null;
		}
	}


	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, boolean hasWrench, int side, float hitX,
			float hitY, float hitZ) {
		ItemStack stack = player.inventory.getStackInSlot(player.inventory.currentItem);
		if(stack == null) {
			template = null;
		} else {
			template = stack.copy();
		}
		return true;
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
		if(template == null) {
			return null;
		}
		return template.copy();
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		if(template == null) {
			return null;
		}
		ItemStack clone = template.copy();
		clone.stackSize = Math.min(amount, clone.getMaxStackSize());
		return clone;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		if(template == null) {
			return null;
		}
		return template.copy();
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
	}

	@Override
	public String getInventoryName() {
		return "tile.taam.machines.creativecache.name";
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
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return false;
	}
}
