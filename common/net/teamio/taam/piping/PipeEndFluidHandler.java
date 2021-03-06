package net.teamio.taam.piping;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

/**
 * Pipe end, used in machines to connect to a pipe "network". This delegates any
 * addFluid or getFluids to the IFluidHandler used when creating this pipe end.
 *
 * @author Oliver Kahrmann
 *
 */
public class PipeEndFluidHandler implements IPipe {
	/**
	 * One array per PipeEnd, used to optimize the
	 * {@link IPipeTE#getPipesForSide(EnumFacing)} as usually there is only one
	 * pipe end per side.
	 */
	private final IPipe[] pipeArray;

	private EnumFacing side;
	private IFluidHandler fluidHandler;
	private int pressure;
	private int suction;
	private boolean active;
	public boolean occluded;

	public PipeEndFluidHandler(IFluidHandler fluidHandler, EnumFacing side, boolean active) {
		this.fluidHandler = fluidHandler;
		this.side = side;
		this.active = active;
		pipeArray = new IPipe[] { this };
	}

	public IPipe[] asPipeArray() {
		pipeArray[0] = this;
		return pipeArray;
	}

	public IFluidHandler getFluidHandler() {
		return fluidHandler;
	}

	public EnumFacing getSide() {
		return side;
	}

	public void setSide(EnumFacing side) {
		this.side = side;
	}

	@Override
	public int getCapacity() {
		IFluidTankProperties[] tankInfo = fluidHandler.getTankProperties();
		int capacity = 0;
		for (IFluidTankProperties tank : tankInfo) {
			capacity += tank.getCapacity();
		}
		return capacity;
	}

	@Override
	public int addFluid(FluidStack stack) {
		return fluidHandler.fill(stack, true);
	}

	@Override
	public int removeFluid(FluidStack like) {
		FluidStack drained = fluidHandler.drain(like, true);
		return drained == null ? 0 : drained.amount;
	}

	@Override
	public int getFluidAmount(FluidStack like) {
		IFluidTankProperties[] tankInfo = fluidHandler.getTankProperties();
		int amount = 0;
		for (IFluidTankProperties tank : tankInfo) {
			FluidStack contents = tank.getContents();
			if(contents != null && contents.isFluidEqual(like)) {
				amount += contents.amount;
			}
		}
		return amount;
	}

	@Override
	public FluidStack[] getFluids() {
		IFluidTankProperties[] tankInfo = fluidHandler.getTankProperties();
		FluidStack[] content = new FluidStack[tankInfo.length];
		for (int i = 0; i < tankInfo.length; i++) {
			content[i] = tankInfo[i].getContents();
		}
		return content;
	}

	@Override
	public int getPressure() {
		return pressure;
	}

	@Override
	public void setPressure(int pressure) {
		this.pressure = pressure;
	}

	@Override
	public void setSuction(int suction) {
		this.suction = suction;
	}

	@Override
	public int getSuction() {
		return suction;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public IPipe[] getInternalPipes(IBlockAccess world, BlockPos pos) {
		return null;
	}

	@Override
	public boolean isSideAvailable(EnumFacing side) {
		return !occluded && this.side == side;
	}
}
