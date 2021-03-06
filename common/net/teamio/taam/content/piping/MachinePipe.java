package net.teamio.taam.content.piping;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.teamio.taam.Config;
import net.teamio.taam.Taam;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IRenderable;
import net.teamio.taam.machines.IMachine;
import net.teamio.taam.piping.IPipe;
import net.teamio.taam.piping.PipeEndFluidHandler;
import net.teamio.taam.piping.PipeInfo;
import net.teamio.taam.piping.PipeUtil;
import net.teamio.taam.util.FaceBitmap;
import net.teamio.taam.util.FluidUtils;

public class MachinePipe implements IMachine, IPipe, IRenderable {

	public static final float pipeWidth = 4/16f;
	public static final float fromBorder = (1f - pipeWidth) / 2;

	public static final float flangeWidth = 7.25f/16f;
	public static final float flangeSize = 2/16f;
	public static final float fromBorderFlange = (1f - flangeWidth) / 2;

	public static final float baseplateWidth = 15/16f;
	public static final float baseplateSize = 2/16f;
	public static final float fromBorderBaseplate = (1f - baseplateWidth) / 2;

	public static AxisAlignedBB bbCenter = new AxisAlignedBB(
			fromBorder, fromBorder, fromBorder,
			1-fromBorder, 1-fromBorder, 1-fromBorder);
	public static final AxisAlignedBB[] bbFaces = new AxisAlignedBB[6];
	public static final AxisAlignedBB[] bbFlanges = new AxisAlignedBB[6];
	public static AxisAlignedBB bbBaseplate = new AxisAlignedBB(
			fromBorderBaseplate, 0, fromBorderBaseplate,
			1-fromBorderBaseplate, baseplateSize, 1-fromBorderBaseplate);

	static {
		bbFaces[EnumFacing.EAST.ordinal()]	= new AxisAlignedBB(1-fromBorder,	fromBorder,		fromBorder,
																1,				1-fromBorder,	1-fromBorder);
		bbFaces[EnumFacing.WEST.ordinal()]	= new AxisAlignedBB(0,				fromBorder,		fromBorder,
																fromBorder,		1-fromBorder,	1-fromBorder);
		bbFaces[EnumFacing.SOUTH.ordinal()]	= new AxisAlignedBB(fromBorder,		fromBorder,		1-fromBorder,
																1-fromBorder,	1-fromBorder,	1);
		bbFaces[EnumFacing.NORTH.ordinal()]	= new AxisAlignedBB(fromBorder,		fromBorder,		0,
																1-fromBorder,	1-fromBorder,	fromBorder);
		bbFaces[EnumFacing.UP.ordinal()]	= new AxisAlignedBB(fromBorder,		1-fromBorder,	fromBorder,
																1-fromBorder,	1,				1-fromBorder);
		bbFaces[EnumFacing.DOWN.ordinal()]	= new AxisAlignedBB(fromBorder,		0,				fromBorder,
																1-fromBorder,	fromBorder,		1-fromBorder);

		bbFlanges[EnumFacing.EAST.ordinal()]	= new AxisAlignedBB(1-flangeSize,		fromBorderFlange,	fromBorderFlange,
																	1,					1-fromBorderFlange,	1-fromBorderFlange);
		bbFlanges[EnumFacing.WEST.ordinal()]	= new AxisAlignedBB(0,					fromBorderFlange,	fromBorderFlange,
																	flangeSize,			1-fromBorderFlange,	1-fromBorderFlange);
		bbFlanges[EnumFacing.SOUTH.ordinal()]	= new AxisAlignedBB(fromBorderFlange,	fromBorderFlange,	1-flangeSize,
																	1-fromBorderFlange,	1-fromBorderFlange,	1);
		bbFlanges[EnumFacing.NORTH.ordinal()]	= new AxisAlignedBB(fromBorderFlange,	fromBorderFlange,	0,
																	1-fromBorderFlange,	1-fromBorderFlange,	flangeSize);
		bbFlanges[EnumFacing.UP.ordinal()]	= new AxisAlignedBB(fromBorderFlange,		1-flangeSize,		fromBorderFlange,
																	1-fromBorderFlange,	1,					1-fromBorderFlange);
		bbFlanges[EnumFacing.DOWN.ordinal()]	= new AxisAlignedBB(fromBorderFlange,	0,					fromBorderFlange,
																	1-fromBorderFlange,	flangeSize,			1-fromBorderFlange);
	}

	private final PipeInfo info;
	/**
	 * Bitmap containing the surrounding pipes Runtime-only, required for
	 * rendering. This is updated in the {@link #renderUpdate()} method, called
	 * from
	 * {@link net.minecraft.block.Block#getActualState(net.minecraft.block.state.IBlockState, IBlockAccess, BlockPos)}
	 * just before rendering.
	 */
	private byte adjacentPipes;
	/**
	 * Cache for the occlusion bitmap provided in
	 * {@link #blockUpdate(World, BlockPos, byte)}
	 */
	private byte occludedSides;

	private PipeEndFluidHandler[] adjacentFluidHandlers;

	public MachinePipe() {
		info = new PipeInfo(Config.pl_pipe_capacity);
	}

	@Override
	public List<String> getVisibleParts() {
		List<String> visibleParts = BaseTileEntity.visibleParts.get();

		// Visible parts list is re-used to reduce object creation
		visibleParts.clear();
		visibleParts.add("Pipe_Center");
		if (isSideConnected(EnumFacing.EAST))
			visibleParts.add("Pipe_East");

		if (isSideConnected(EnumFacing.WEST))
			visibleParts.add("Pipe_West");

		if (isSideConnected(EnumFacing.NORTH))
			visibleParts.add("Pipe_North");
		if (isSideConnected(EnumFacing.SOUTH))
			visibleParts.add("Pipe_South");

		if (isSideConnected(EnumFacing.DOWN))
			visibleParts.add("Pipe_Down");
		if (isSideConnected(EnumFacing.UP))
			visibleParts.add("Pipe_Up");
		return visibleParts;
	}

	public boolean isSideConnected(EnumFacing side) {
		return FaceBitmap.isSideBitSet(adjacentPipes, side);
	}

	@Override
	public boolean renderUpdate(IBlockAccess world, BlockPos pos) {
		byte old = adjacentPipes;
		adjacentPipes = 0;
		for (EnumFacing side : EnumFacing.VALUES) {
			// Side occluded? Skip.
			if(FaceBitmap.isSideBitSet(occludedSides, side)) {
				continue;
			}
			IPipe pipeOnSide = PipeUtil.getConnectedPipe(world, pos, side);
			if (pipeOnSide != null) {
				adjacentPipes = FaceBitmap.setSideBit(adjacentPipes, side);
				continue;
			}
			if(Config.pl_pipe_wrap_ifluidhandler) {
				if (FluidUtils.getFluidHandler(world, pos.offset(side), side.getOpposite()) != null) {
					adjacentPipes = FaceBitmap.setSideBit(adjacentPipes, side);
				}
			}
		}
		return old != adjacentPipes;
	}

	@Override
	public void blockUpdate(World world, BlockPos pos, byte occlusionField) {
		occludedSides = occlusionField;
		// Check surrounding blocks for IFluidHandler implementations that don't use the pipe system
		// and create wrappers accordingly
		boolean wrappersRequired = false;
		if(Config.pl_pipe_wrap_ifluidhandler) {
			for (EnumFacing side : EnumFacing.VALUES) {
				// Side occluded? Skip.
				if(FaceBitmap.isSideBitSet(occludedSides, side)) {
					continue;
				}
				// Check for pipes
				int sideIdx = side.ordinal();
				IPipe pipeOnSide = PipeUtil.getConnectedPipe(world, pos, side);
				// if there is no pipe, check for an IFluidHandler to wrap
				if (pipeOnSide == null) {
					IFluidHandler fh = FluidUtils.getFluidHandler(world, pos.offset(side), side.getOpposite());
					if (fh != null) {
						wrappersRequired = true;
						// Fluid handler here, we need a wrapper.
						if (adjacentFluidHandlers == null) {
							adjacentFluidHandlers = new PipeEndFluidHandler[6];
							adjacentFluidHandlers[sideIdx] = new PipeEndFluidHandler(fh, side.getOpposite(), false);
						} else {
							// Not yet known or a different TileEntity, we need a new wrapper.
							if (adjacentFluidHandlers[sideIdx] == null
									|| adjacentFluidHandlers[sideIdx].getFluidHandler() != fh) {
								adjacentFluidHandlers[sideIdx] = new PipeEndFluidHandler(fh, side.getOpposite(), false);
							}
						}
					}
				} else {
					// We have a regular pipe there, no need for a wrapper
					if (adjacentFluidHandlers != null) {
						adjacentFluidHandlers[sideIdx] = null;
					}
				}
			}
		}
		// No wrappers required, delete the array
		if (!wrappersRequired) {
			adjacentFluidHandlers = null;
		}
	}

	@Override
	public void update(World world, BlockPos pos) {
		// Process "this"
		PipeUtil.processPipes(this, world, pos);
		// Process the fluid handlers for adjecent non-pipe-machines (implementing IFluidHandler)
		if (Config.pl_pipe_wrap_ifluidhandler && adjacentFluidHandlers != null) {
			for (EnumFacing side : EnumFacing.VALUES) {
				PipeEndFluidHandler handler = adjacentFluidHandlers[side.ordinal()];
				if (handler != null) {
					PipeUtil.processPipes(handler, world, pos.offset(side));
				}
			}
		}

		//TODO: updateState(false, false, false);
	}

	@Override
	public void writePropertiesToNBT(NBTTagCompound tag) {
		info.writeToNBT(tag);
		tag.setByte("occludedSides", occludedSides);
	}

	@Override
	public void readPropertiesFromNBT(NBTTagCompound tag) {
		info.readFromNBT(tag);
		occludedSides = tag.getByte("occludedSides");
	}

	@Override
	public void writeUpdatePacket(PacketBuffer buf) {
		info.writeUpdatePacket(buf);
		buf.writeByte(occludedSides);
	}

	@Override
	public void readUpdatePacket(PacketBuffer buf) {
		info.readUpdatePacket(buf);
		occludedSides = buf.readByte();
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos blockPos) {
		return state;
	}

	@Override
	public String getModelPath() {
		return "taam:machine";
	}

	@Override
	public void addCollisionBoxes(AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
		if (mask.intersectsWith(bbCenter)) {
			list.add(bbCenter);
		}
		for (EnumFacing side : EnumFacing.VALUES) {
			if (isSideConnected(side)) {
				AxisAlignedBB box = bbFaces[side.ordinal()];
				if (mask.intersectsWith(box)) {
					list.add(box);
				}
			}
		}
	}

	@Override
	public void addSelectionBoxes(List<AxisAlignedBB> list) {
		list.add(bbCenter);
		for (EnumFacing side : EnumFacing.VALUES) {
			if (isSideConnected(side)) {
				AxisAlignedBB box = bbFaces[side.ordinal()];
				list.add(box);
				box = bbFlanges[side.ordinal()];
				list.add(box);
			}
		}
	}

	@Override
	public void addOcclusionBoxes(List<AxisAlignedBB> list) {
		list.add(bbCenter);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == Taam.CAPABILITY_PIPE) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == Taam.CAPABILITY_PIPE) {
			return (T) this;
		}
		return null;
	}

	/*
	 * IPipe implementation
	 */

	@Override
	public int getPressure() {
		return info.pressure;
	}

	@Override
	public int addFluid(FluidStack stack) {
		//TODO: markDirty();
		return info.addFluid(stack);
	}

	@Override
	public FluidStack[] getFluids() {
		return info.getFluids();
	}

	@Override
	public int getCapacity() {
		return info.capacity;
	}

	@Override
	public void setPressure(int pressure) {
		info.pressure = pressure;
	}

	@Override
	public void setSuction(int suction) {
		info.suction = suction;
	}

	@Override
	public int getSuction() {
		return info.suction;
	}

	@Override
	public boolean isActive() {
		return false;
	}

	@Override
	public IPipe[] getInternalPipes(IBlockAccess world, BlockPos pos) {
		List<IPipe> pipes = new ArrayList<IPipe>(6);
		if(Config.pl_pipe_wrap_ifluidhandler && adjacentFluidHandlers != null) {
			for (EnumFacing side : EnumFacing.values()) {
				if(isSideAvailable(side)) {
					// If there is no "regular" pipe on that side
					IPipe pipeOnSide = PipeUtil.getConnectedPipe(world, pos, side);
					if (pipeOnSide == null) {
						// Check for fluid handler wrappers
						int sideIdx = side.ordinal();
						if(adjacentFluidHandlers[sideIdx] != null) {
							pipes.add(adjacentFluidHandlers[sideIdx]);
						}
					}
				}
			}
		}
		return pipes.toArray(new IPipe[pipes.size()]);
	}

	@Override
	public int removeFluid(FluidStack like) {
		return info.removeFluid(like);
	}

	@Override
	public int getFluidAmount(FluidStack like) {
		return info.getFluidAmount(like);
	}

	@Override
	public boolean isSideAvailable(EnumFacing side) {
		return !FaceBitmap.isSideBitSet(occludedSides, side);
		//TODO: Check disabled sides once available
	}

}
