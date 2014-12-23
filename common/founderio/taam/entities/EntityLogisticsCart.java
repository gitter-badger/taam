package founderio.taam.entities;

import java.util.List;

import codechicken.lib.vec.BlockCoord;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerHorseInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import founderio.taam.TaamMain;
import founderio.taam.blocks.TileEntityLogisticsManager;
import founderio.taam.client.gui.ContainerLogisticsCart;
import founderio.taam.client.gui.GuiLogisticsCart;
import founderio.taam.multinet.logistics.IVehicle;
import founderio.taam.multinet.logistics.InBlockRoute;
import founderio.taam.multinet.logistics.LogisticsUtil;
import founderio.taam.multinet.logistics.PredictedInventory;
import founderio.taam.multinet.logistics.Route;
import founderio.taam.network.TPLogisticsConfiguration;

public class EntityLogisticsCart extends Entity implements IVehicle {

	
	private int vehicleID = -1;
	
	
	public boolean isOnRail = false;
	
	private int currentRailX;
	private int currentRailY;
	private int currentRailZ;
	
	private InBlockRoute ibr;
	private float ibrProgress;
	private Route route;
	
	private float currentSpeed = 0.1f;
	//TODO: World Coords
	private BlockCoord coordsManager;
    private String name;
    
	public EntityLogisticsCart(World world) {
		super(world);
	}

	@Override
	protected void entityInit() {
		if(coordsManager == null) {
			dataWatcher.addObject(20, 0);
			dataWatcher.addObject(21, 0);
			dataWatcher.addObject(22, 0);
		} else {
			dataWatcher.addObject(20, coordsManager.x);
			dataWatcher.addObject(21, coordsManager.y);
			dataWatcher.addObject(22, coordsManager.z);
		}
	}
	
	private BlockCoord getCoordsManager() {
		if(coordsManager == null) {
			coordsManager = new BlockCoord();
		}
		coordsManager.x = dataWatcher.getWatchableObjectInt(20);
		coordsManager.y = dataWatcher.getWatchableObjectInt(21);
		coordsManager.z = dataWatcher.getWatchableObjectInt(22);
		return coordsManager;
	}
	
	private void setCoordsManager(BlockCoord coords) {
		coordsManager = coords;
		dataWatcher.updateObject(20, coordsManager.x);
		dataWatcher.updateObject(21, coordsManager.y);
		dataWatcher.updateObject(22, coordsManager.z);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		currentRailX = tag.getInteger("currentRailX");
		currentRailY = tag.getInteger("currentRailY");
		currentRailZ = tag.getInteger("currentRailZ");
		
		vehicleID = tag.getInteger("vehicleID");
		

		ibrProgress = tag.getFloat("ibrProgress");
		currentSpeed = tag.getFloat("currentSpeed");
		
		name = tag.getString("name");
		int[] coords = tag.getIntArray("coordsManager");
		if(coords == null || coords.length != 3) {
			coordsManager = null;
		} else {
			coordsManager = BlockCoord.fromAxes(coords);
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		// TODO Auto-generated method stub
		tag.setInteger("currentRailX", currentRailX);
		tag.setInteger("currentRailY", currentRailY);
		tag.setInteger("currentRailZ", currentRailZ);
		
		tag.setInteger("vehicleID", vehicleID);
		
		//TODO: Somehow store ibr (unique id?)
		
		tag.setFloat("ibrProgress", ibrProgress);
		//TODO: Somehow store root (link to manager's ID for the route)
		
		tag.setFloat("currentSpeed", currentSpeed);
		
		if(coordsManager != null) {
			tag.setIntArray("coordsManager", coordsManager.intArray());
		}
		if(name != null && !name.trim().isEmpty()) {
			tag.setString("name", name);
		}
	}
	
	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity other) {
		
		if (other == null) {
			return null;
		}
		return null;
		//return other.canBePushed() ? other.getBoundingBox() : null;
	}

	@Override
	public AxisAlignedBB getBoundingBox() {
		return boundingBox;
	}
	
	@Override
	public boolean interactFirst(EntityPlayer player) {
		player.openGui(TaamMain.instance, 1, worldObj, this.getEntityId(), 0, 0);
		return true;
	}
	
	@Override
	public boolean canBeCollidedWith() {
		return !isDead;
	}
	
	@Override
	public boolean canBePushed() {
		return !isOnRail;
	}
	
	@Override
	public double getMountedYOffset() {
		// TODO Auto-generated method stub
		return super.getMountedYOffset();
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource damageSource, float damage) {
		// TODO Auto-generated method stub
		return super.attackEntityFrom(damageSource, damage);
	}
	
	public int getVehicleID() {
		return vehicleID;
	}
	
	private void linkToManager(TileEntityLogisticsManager manager) {
		//TODO for later: once cross-dimensional support is a topic, update this.
		if(manager.getWorldObj() != worldObj) {
			return;
		}
		setCoordsManager(new BlockCoord(manager));
		this.vehicleID = manager.vehicleRegister(this);
	}

	@Override
	public void linkToManager(BlockCoord coords) {
		if(worldObj.isRemote) {
			//TODO: send packet
			TPLogisticsConfiguration config = TPLogisticsConfiguration.newConnectManagerVehicle(worldObj.provider.dimensionId, this.getEntityId(), coords);
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

	@Override
	public boolean isConnectedToManager() {
		return getCoordsManager() != null;
	}

	@Override
	public String getName() {
		return this.name != null ? this.name : "Vehicle";
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	
	
	@Override
	public void onEntityUpdate() {
		if(worldObj.isRemote) {
			
		} else {
			this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;

            int x = MathHelper.floor_double(this.posX);
            int y = MathHelper.floor_double(this.posY);
            int z = MathHelper.floor_double(this.posZ);
            
            boolean isActuallyOnRail = LogisticsUtil.isMagnetRail(worldObj, x, y, z);
            if(!isOnRail && isActuallyOnRail) {
            	currentRailX = x;
            	currentRailY = y;
            	currentRailZ = z;
            	//TODO: Snap into place
            	isOnRail = true;
            }
            
            if(isOnRail) {
            	//TODO: Check where we need to go, depending on route
            	
            	if(ibr == null) {
            		//TODO: Find route
            	} else {
            		ibrProgress += currentSpeed;
            		while(ibrProgress > ibr.totalLength) {
            			//TODO: get next ibr
            			ibrProgress -= ibr.totalLength;
            		}
            		// Find the point in the ibr we are at and do a linear interpolation between the closes points on the ibr.
            		float calcOffset = ibrProgress;
            		int coordinateCount = ibr.getCoordinateCount();
            		for(int i = 1; i < coordinateCount; i++) {
            			// Are we past that point yet?
            			if(calcOffset <= ibr.lengths[i-1]) {
            				// We are not past it -> interpolate.
            				
            				// Calculate the single deltas for the next and the last point
            				float dX = Math.abs(ibr.xyzCoordinates[i*3] - ibr.xyzCoordinates[(i-1)*3]);
            				float dY = Math.abs(ibr.xyzCoordinates[i*3+1] - ibr.xyzCoordinates[(i-1)*3+1]);
            				float dZ = Math.abs(ibr.xyzCoordinates[i*3+2] - ibr.xyzCoordinates[(i-1)*3+2]);
            				// Calculate the percentage of the whole distance that we have completed.
            				float percentage = (calcOffset / ibr.lengths[i-1]);
            				// Set our postion to the interpolated value
            				float posX = currentRailX + ibr.xyzCoordinates[(i-1)*3] + dX * percentage;
            				float posY = currentRailY + ibr.xyzCoordinates[(i-1)*3+1] + dY * percentage;
            				float posZ = currentRailZ + ibr.xyzCoordinates[(i-1)*3+2] + dZ * percentage;
            		        this.prevPosX = this.posX;
            		        this.prevPosY = this.posY;
            		        this.prevPosZ = this.posZ;
            				setPosition(posX, posY, posZ);
            				// And don't check the next points anymore
            				break;
            			} else {
            				// We are past it -> move to next point.
            				calcOffset -= ibr.lengths[i-1];
            			}
            		}
            	}
            } else {

				AxisAlignedBB box = boundingBox.expand(0.2D, 0.0D, 0.2D);

				List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(
						this, box);

				if (list != null && !list.isEmpty()) {
					for (int k = 0; k < list.size(); ++k) {
						Entity entity = (Entity) list.get(k);

						if (entity != this.riddenByEntity
								&& entity.canBePushed()) {
							entity.applyEntityCollision(this);
						}
					}
				}
				
            	//TODO: Grounded/Air distinction.
            	this.motionX *= 0.5D;
                this.motionY *= 0.5D;
                this.motionZ *= 0.5D;
            }
            
		}
	}

	@Override
	public PredictedInventory getPredictedInventory() {
		// TODO Auto-generated method stub
		return null;
	}

}
