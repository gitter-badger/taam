package founderio.taam.content.logistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.vec.BlockCoord;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import founderio.taam.TaamMain;
import founderio.taam.content.BaseTileEntity;
import founderio.taam.content.IRotatable;
import founderio.taam.logistics.Demand;
import founderio.taam.logistics.Demand.DemandCategory;
import founderio.taam.logistics.Goods;
import founderio.taam.logistics.IStation;
import founderio.taam.logistics.LogisticsConfiguration;
import founderio.taam.logistics.LogisticsManager;
import founderio.taam.logistics.PredictedInventory;
import founderio.taam.network.TPLogisticsConfiguration;
import founderio.taam.util.WorldCoord;

public class TileEntityLogisticsStation extends BaseTileEntity implements IStation, IRotatable {
	
	private String name = "";
	private ForgeDirection direction = ForgeDirection.NORTH;
	
	private BlockCoord coordsManager = null;
	
	private int stationID = -1;
	
	private List<LogisticsConfiguration> configurations;
	
	private transient boolean changed = false;
	
	public static enum StationStatus {
		Idle,
		Restocking,
		Producing,
		ManualOrder
	}
	
	public TileEntityLogisticsStation() {
		configurations = new ArrayList<LogisticsConfiguration>();
	}
	
	public StationStatus getStatus() {
		return StationStatus.Idle;
	}
	
	@Override
	public boolean isConnectedToManager() {
		return coordsManager != null;
	}

	@Override
	public int getStationID() {
		return stationID;
	}
	
	private void linkToManager(TileEntityLogisticsManager manager) {
		//TODO for later: once cross-dimensional support is a topic, update this.
		if(manager.getWorldObj() != worldObj) {
			return;
		}
		this.coordsManager = new BlockCoord(manager);
		this.stationID = manager.stationRegister(this);
		
		if(stationID % 2 == 0) {
			LogisticsConfiguration.KeepStock testConfig = new LogisticsConfiguration.KeepStock(this);
			
			testConfig.what = new ItemStack(Blocks.dirt);
			testConfig.amount = 20;
			testConfig.enabled = true;
			
			configurations.add(testConfig);
		} else {
			LogisticsConfiguration.ProvideStock testConfig = new LogisticsConfiguration.ProvideStock(this);
			
			testConfig.what = new ItemStack(Blocks.dirt);
			testConfig.enabled = true;
			
			configurations.add(testConfig);
		}
		
		updateState();
	}
	
	@Override
	public void linkToManager(WorldCoord coords) {
		if(worldObj.isRemote) {
			TPLogisticsConfiguration config = TPLogisticsConfiguration.newConnectManager(new WorldCoord(this), coords);
			TaamMain.network.sendToServer(config);
		} else {
			TileEntity te = worldObj.getTileEntity(coords.x, coords.y, coords.z);
			if(te instanceof TileEntityLogisticsManager) {
				linkToManager((TileEntityLogisticsManager) te);
			} else {
				//TODO: Log Error
			}
		}
	}
	
	public void unlinkFromManager() {
		this.coordsManager = null;
		this.stationID = -1;
	}
	
	public TileEntityLogisticsManager getManagerTE() {
		if(coordsManager == null) {
			return null;
		}
		TileEntity te = worldObj.getTileEntity(coordsManager.x, coordsManager.y, coordsManager.z);
		if(te instanceof TileEntityLogisticsManager) {
			return (TileEntityLogisticsManager) te;
		} else {
			coordsManager = null;
			stationID = -1;
			changed = true;
			return null;
		}
	}
	
	public void placeDemand(Demand demand) {
		Collection<Demand> currentDemands = getCurrentDemands();
		demand.station = stationID;
		currentDemands.add(demand);
	}
	
	public void placeDemand(ItemStack stack) {
		Collection<Demand> currentDemands = getCurrentDemands();
		Demand demand = new Demand();
		demand.goods = new Goods();
		demand.goods.amount = stack.stackSize;
		demand.goods.type = stack;
		demand.category = DemandCategory.FillStock;
		demand.station = stationID;
		System.out.println("Placing demand: " + demand);
		currentDemands.add(demand);
	}
	
	public IInventory getControlledInventory() {
		return InventoryUtils.getInventory(worldObj,
				xCoord + direction.offsetX,
				yCoord + direction.offsetY,
				zCoord + direction.offsetZ);
	}
	
	public PredictedInventory getPredictedInventory() {
		Collection<Demand> demands = getCurrentDemands();
		PredictedInventory inventory = new PredictedInventory(true, getControlledInventory());
		inventory.addDemands(demands);
		return inventory;
	}
	
	public PredictedInventory getGrantedInventory() {
		PredictedInventory inventory = new PredictedInventory(true, getControlledInventory());
		//TODO: subtract outgoing transports
		return inventory;
	}
	
	//private Collection<Demand> currentDemands;
	
	public Collection<Demand> getCurrentDemands() {
		//TODO: Chaching somehow? without causing problems when this list is loaded from outside our own update method
		TileEntityLogisticsManager teManager = getManagerTE();
		if(teManager == null) {
			return Collections.emptyList();
		}
		LogisticsManager manager = teManager.getManager();
		//TODO: Respect transports (DO NOT OVERLAP WITH DEMANDS!) [-> Manual Transports do not have demands!]

		Collection<Demand> myProgressingDemands = Collections2.filter(manager.processingDemands, new Predicate<Demand>() {
			@Override
			public boolean apply(Demand input) {
				return input.station == stationID;
			}
		});
		Collection<Demand> myPendingDemands = Collections2.filter(manager.pendingDemands, new Predicate<Demand>() {
			@Override
			public boolean apply(Demand input) {
				return input.station == stationID;
			}
		});
		ArrayList<Demand> myDemands = new ArrayList<Demand>(myProgressingDemands.size() + myPendingDemands.size());
		myDemands.addAll(myPendingDemands);
		myDemands.addAll(myProgressingDemands);
		return myDemands;
	}
	
	@Override
	public void updateEntity() {

		
		if(!worldObj.isRemote) {
			changed = false;
			
			TileEntityLogisticsManager teManager = getManagerTE();
			IInventory controlledInventory = getControlledInventory();
			if(teManager != null && controlledInventory != null) {
				//LogisticsManager manager = teManager.getManager();
						
				
				for(LogisticsConfiguration config : configurations) {
					if(config.enabled) {
						config.process();
					}
				}
			}
			
			// Content changed, send Network update.
			if(changed) {
				updateState();
			}
		}
	}
	
	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		if(name != null && !name.trim().isEmpty()) {
			tag.setString("name", name);
		}
		tag.setInteger("direction", direction.ordinal());
		if(coordsManager != null) {
			tag.setIntArray("coordsManager", coordsManager.intArray());
		}
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		name = tag.getString("name");
		direction = ForgeDirection.getOrientation(tag.getInteger("direction"));
		int[] coords = tag.getIntArray("coordsManager");
		if(coords == null || coords.length != 3) {
			coordsManager = null;
		} else {
			coordsManager = BlockCoord.fromAxes(coords);
		}
	}

	@Override
	public String getName() {
		if(name == null || name.trim().isEmpty()) {
			return "Station at x" + xCoord + " y" + yCoord + " z" + zCoord;
		} else {
			return name;
		}
	}

	
	@Override
	public ForgeDirection getFacingDirection() {
		return direction;
	}

	@Override
	public ForgeDirection getMountDirection() {
		return ForgeDirection.DOWN;
	}

	@Override
	public ForgeDirection getNextFacingDirection() {
		return direction.getRotation(ForgeDirection.UP);
	}

	@Override
	public ForgeDirection getNextMountDirection() {
		return ForgeDirection.DOWN;
	}

	@Override
	public void setFacingDirection(ForgeDirection direction) {
		this.direction = direction;
		updateState();
	}

	@Override
	public void setMountDirection(ForgeDirection direction) {
		return;
	}

	public List<LogisticsConfiguration> getConfigurations() {
		return Collections.unmodifiableList(configurations);
	}

}