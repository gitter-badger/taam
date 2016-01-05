package net.teamio.taam.content.common;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.Taam;
import net.teamio.taam.Taam.BLOCK_ORE_META;

public class BlockOre extends Block {

	public static final PropertyEnum VARIANT = PropertyEnum.create("variant", Taam.BLOCK_ORE_META.class);
	
	public BlockOre() {
		super(Material.rock);
		this.setStepSound(Block.soundTypeStone);
		this.setHarvestLevel("pickaxe", 1);
		this.setResistance(3.14159265359f);
		this.setHardness(2);
	}
	
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, VARIANT);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		Taam.BLOCK_ORE_META meta = (Taam.BLOCK_ORE_META)state.getValue(VARIANT);
		return meta.ordinal();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		Taam.BLOCK_ORE_META[] values = Taam.BLOCK_ORE_META.values();
		if(meta < 0 || meta > values.length) {
			return getDefaultState();
		}
		return getDefaultState().withProperty(VARIANT, values[meta]);
	}

	public String getUnlocalizedName(ItemStack itemStack) {
		int i = itemStack.getItemDamage();
		Enum<?>[] values = Taam.BLOCK_ORE_META.values();

		if (i < 0 || i >= values.length) {
			i = 0;
		}

		return super.getUnlocalizedName() + "." + values[i].name();
	}
	
//	@Override
//	@SideOnly(Side.CLIENT)
//	public void registerBlockIcons(IIconRegister ir) {
//		BLOCK_ORE_META[] values = Taam.BLOCK_ORE_META.values();
//		iconList = new IIcon[values.length];
//		for (int i = 0; i < values.length; i++) {
//			if(values[i].ore) {
//				iconList[i] = ir.registerIcon(Taam.MOD_ID + ":ore." + values[i].name());
//			} else {
//				iconList[i] = ir.registerIcon(Taam.MOD_ID + ":ore.impossible");
//			}
//		}
//	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs creativeTab, List list) {
		BLOCK_ORE_META[] values = Taam.BLOCK_ORE_META.values();
		for (int i = 0; i < values.length; i++) {
			if(values[i].ore) {
				list.add(new ItemStack(item, 1, i));
			}
		}
	}

}