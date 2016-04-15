package net.teamio.taam.machines;

import java.util.List;

import mcmultipart.item.ItemMultiPart;
import mcmultipart.multipart.IMultipart;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MachineItemMultipart extends ItemMultiPart {

	private final IMachineMetaInfo[] values;

	public MachineItemMultipart(IMachineMetaInfo[] values) {
		super();
		if (values == null || values.length == 0) {
			throw new IllegalArgumentException("Specified meta values were null or empty");
		}
		this.values = values;
	}

	public IMachineMetaInfo getInfo(int meta) {
		int ordinal = MathHelper.clamp_int(meta, 0, values.length);
		return values[ordinal];
	}

	@Override
	public IMultipart createPart(World world, BlockPos pos, EnumFacing side, Vec3 hit, ItemStack stack, EntityPlayer player) {
		int meta = stack.getMetadata();
		IMachineMetaInfo info = getInfo(meta);

		IMachine machine = info.createMachine();

		// TODO: distinguish between IMachine and IMachineWithSpecialRenderer later

		return new MachineMultipart(machine);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		int meta = stack.getMetadata();
		IMachineMetaInfo info = getInfo(meta);
		info.addInformation(stack, playerIn, tooltip, advanced);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int meta = stack.getMetadata();
		IMachineMetaInfo info = getInfo(meta);

		return this.getUnlocalizedName() + "." + info.unlocalizedName();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs creativeTab, List<ItemStack> list) {
		for (int i = 0; i < values.length; i++) {
			list.add(new ItemStack(item, 1, values[i].metaData()));
		}
	}

}