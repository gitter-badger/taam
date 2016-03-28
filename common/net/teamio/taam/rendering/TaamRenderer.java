package net.teamio.taam.rendering;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.content.conveyors.TileEntityConveyorProcessor;
import net.teamio.taam.content.conveyors.TileEntityConveyorSieve;
import net.teamio.taam.conveyors.ConveyorUtil;
import net.teamio.taam.conveyors.ItemWrapper;
import net.teamio.taam.conveyors.api.IConveyorAwareTE;
import net.teamio.taam.piping.IPipe;

public class TaamRenderer extends TileEntitySpecialRenderer<TileEntity> {

	private RenderItem ri;
	private float rot = 0;
	private float rot_sensor = 0;
	public static double rotSin = 0;
	
	public TaamRenderer() {
		ri = Minecraft.getMinecraft().getRenderItem();
	}
	
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
	    if (event.phase == TickEvent.Phase.END) {
	    	rot++;
	    	rot_sensor++;
	    	if(rot_sensor > 360) {
	    		rot_sensor -= 360;
	    	}
			rotSin = Math.sin(Math.toRadians(rot*32));
	    }
	}
	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks, int destroyStage) {
		if(tileEntity instanceof IConveyorAwareTE) {
			renderConveyorItems((IConveyorAwareTE) tileEntity, x, y, z);
		}
		if(tileEntity instanceof IPipe) {

			IPipe pipe = (IPipe)tileEntity;
			
			GL11.glPushMatrix();
			GL11.glTranslated(x, y, z);
			
			GL11.glPushMatrix();
			GL11.glTranslated(0.4f, 0.4f, 0.25f);
			
			GL11.glScalef(.02f, .02f, .02f);
			GL11.glRotatef(180, 0, 0, 1);

			Minecraft.getMinecraft().fontRendererObj.drawString(pipe.getPressure() + "-" + pipe.getSuction(), 0, 0, 0xFFFFFF);
			//GL11.glTranslated(0, y+0.5f, z-0.1f);
			Minecraft.getMinecraft().fontRendererObj.drawString("E: " + (pipe.getPressure() - pipe.getSuction()), 0, 8, 0xFFFF00);

			GL11.glPopMatrix();

			GL11.glTranslated(.5f, .5f, .5f);
			GL11.glRotatef(180, 0, 1, 0);
			GL11.glTranslated(-.5f, -.5f, -.5f);
			
			GL11.glPushMatrix();
			GL11.glTranslated(0.4f, 0.4f, 0.25f);
			
			GL11.glScalef(.02f, .02f, .02f);
			GL11.glRotatef(180, 0, 0, 1);
			
//			GL11.glTranslated(0, 0, -25f);
			
			Minecraft.getMinecraft().fontRendererObj.drawString(pipe.getPressure() + "-" + pipe.getSuction(), 0, 0, 0xFFFFFF);
			//GL11.glTranslated(0, y+0.5f, z-0.1f);
			Minecraft.getMinecraft().fontRendererObj.drawString("E: " + (pipe.getPressure() - pipe.getSuction()), 0, 8, 0xFFFF00);

			GL11.glPopMatrix();
			
			GL11.glPopMatrix();
		}
	}
	
	private EnumFacing conveyorGetDirection(IConveyorAwareTE tileEntity) {
		EnumFacing direction;
		if(tileEntity instanceof IRotatable) {
			direction = ((IRotatable) tileEntity).getFacingDirection();
		} else {
			direction = EnumFacing.SOUTH;
		}
		return direction;
	}
	
	public void renderConveyorItems(IConveyorAwareTE tileEntity, double x, double y, double z) {

		
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		
		if(tileEntity != null) {

	        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
			if(tileEntity instanceof TileEntityConveyorProcessor) {
				
				/*
				 * Get Rotation
				 */
				
				EnumFacing direction = conveyorGetDirection(tileEntity);
				
				float rotationDegrees = 0;
				if(direction == EnumFacing.WEST) {
					rotationDegrees = 270;
				} else if(direction == EnumFacing.NORTH) {
					rotationDegrees = 180;
				} else if(direction == EnumFacing.EAST) {
					rotationDegrees = 90;
				}
				
				TileEntityConveyorProcessor processor = (TileEntityConveyorProcessor) tileEntity;
				ItemStack processingStack = processor.getStackInSlot(0);
				if(processingStack != null) {
					GL11.glPushMatrix();
					Random rand = processor.getWorld().rand;
					GL11.glTranslatef(0.5f, 0.4f, 0.5f);
					
					/*
					 * Rotate the items
					 */
					
					GL11.glRotatef(rotationDegrees, 0, 1, 0);

					/*
					 * Shaking inside the processor
					 */
					if(!processor.isShutdown) {
						GL11.glTranslatef(
								0.015f * (1-rand.nextFloat()),
								0.025f * (1-rand.nextFloat()),
								0.015f * (1-rand.nextFloat()));
					}
					GL11.glScalef(0.4f, 0.4f, 0.4f);


					IBakedModel model = ri.getItemModelMesher().getItemModel(processingStack);
					ri.renderItem(processingStack, model);
					
					GL11.glPopMatrix();
				}
			}
			/*
			 * Regular rendering, meaning conveyors & similar
			 */
			if(tileEntity.shouldRenderItemsDefault()) {
				float posY = 0.1f;
				if(tileEntity instanceof TileEntityConveyorSieve) {
					//TODO extract into separate method getItemRenderPosY() in IConveyorAwareTE
					if(((TileEntityConveyorSieve) tileEntity).isShutdown) {
						//posY = 0;
					} else {
						posY += (float)(rotSin*0.04);
					}
				}
				for(int slot = 0; slot < 9; slot++) {
					ItemWrapper wrapper = tileEntity.getSlot(slot);
					
					ItemStack itemStack;
					if(wrapper == null || wrapper.isEmpty()
							|| (itemStack = wrapper.itemStack) == null) {
						continue;
					}
					
					int movementProgress = tileEntity.getMovementProgress(slot);
					if(movementProgress < 0) {
						movementProgress = 0;
					}
					float speedsteps = tileEntity.getSpeedsteps();
					
					EnumFacing renderDirection = tileEntity.getNextSlot(slot);
					
					float posX = (float)ConveyorUtil.getItemPositionX(slot, movementProgress / speedsteps, renderDirection);
					float posZ = (float)ConveyorUtil.getItemPositionZ(slot, movementProgress / speedsteps, renderDirection);
					
					GL11.glPushMatrix();
					GL11.glTranslatef(posX, posY + 0.51f, posZ);
					GL11.glScalef(0.4f, 0.4f, 0.4f);
	
					IBakedModel model = ri.getItemModelMesher().getItemModel(itemStack);
					ri.renderItem(itemStack, model);
					
					GL11.glPopMatrix();
				}
			}
		}
		
		GL11.glPopMatrix();
	}
}
