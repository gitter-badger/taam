package net.teamio.taam.conveyors;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.teamio.taam.util.TaamUtil;
import net.teamio.taam.util.inv.InventoryRange;
import net.teamio.taam.util.inv.InventoryUtils;

public abstract class OutputChute {

	IInventory outputInventory;
	boolean canDrop;

	public void refreshOutputInventory(World world, BlockPos pos) {
		outputInventory = InventoryUtils.getInventory(world, pos);
		canDrop = TaamUtil.canDropIntoWorld(world, pos);
	}

	public boolean isOperable() {
		return canDrop || outputInventory != null;
	}

	/**
	 * Output the chute content.
	 * 
	 * @param world
	 * @param pos
	 * @return Returns true if there were items transferred or there are still
	 *         items left.
	 */
	public abstract boolean output(World world, BlockPos pos);

	public void output(ItemStack stack) {

	}
	
	
	/**
	 * Tries to output into the outputInventory, or drop down into the world
	 * 
	 * @param world
	 * @param oututPosition
	 *            Position to output to, usually one block below the chute.
	 * @param outputInventory
	 *            Output inventory. If null, will output to world.
	 * @param backlog
	 *            The items to output.
	 * @param maxOutput
	 *            Maximum number of items to output. If 0, will attempt to
	 *            output all items in the backlog. Currently not implemented.
	 * @return true if it was able to output items, or unable but still has
	 *         items left.
	 */
	public static boolean chuteMechanicsOutput(World world, BlockPos oututPosition, IInventory outputInventory, ItemStack[] backlog, int maxOutput) {
		if(backlog == null) {
			return false;
		}
		
		//TODO: implement maxOutput!
		boolean wasAble = false;
		boolean hasOutputLeft = false;
		if(outputInventory == null) {
			double entX = oututPosition.getX() + 0.5;
			double entY = oututPosition.getY() + 0.7;
			double entZ = oututPosition.getZ() + 0.5;
			
			// Output to world
			for(int i = 0; i < backlog.length; i++) {
				ItemStack itemStack = backlog[i];
				if(itemStack == null) {
					continue;
				}
				EntityItem item = new EntityItem(world, entX, entY, entZ, itemStack);
		        item.motionX = 0;
		        item.motionY = 0;
		        item.motionZ = 0;
		        world.spawnEntityInWorld(item);
				wasAble = true;
				backlog[i] = null;
			}

			hasOutputLeft = false;
		} else {
			// Output to inventory
			InventoryRange range = new InventoryRange(outputInventory, EnumFacing.UP.ordinal());
			
			for(int i = 0; i < backlog.length; i++) {
				ItemStack itemStack = backlog[i];
				if(itemStack == null) {
					continue;
				}
				int unable = InventoryUtils.insertItem(range, itemStack, false);
				if(unable > 0) {
					hasOutputLeft = true;
					wasAble = unable != itemStack.stackSize;
					itemStack.stackSize = unable;
				} else {
					wasAble = true;
					backlog[i] = null;
				}
			}
		}
		return wasAble || hasOutputLeft;
	}
	
}
