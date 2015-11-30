package net.teamio.taam.content.common;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.teamio.taam.Taam;

public class BlockOre extends Block {

	private IIcon[] iconList;
	
	public BlockOre() {
		super(Material.rock);
		this.setStepSound(Block.soundTypeStone);
		this.setHarvestLevel("pickaxe", 1);
		this.setResistance(25);
		this.setHardness(2);
		this.setBlockTextureName(Taam.MOD_ID + ":ore");
	}
	
	@Override
	public int damageDropped(int meta) {
		if (meta < 0 || meta >= Taam.BLOCK_ORE_META.values().length) {
			meta = 0;
		}
		return meta;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		if (meta < 0 || meta >= iconList.length) {
			meta = 0;
		}

		return iconList[meta];
	}

	public String getUnlocalizedName(ItemStack itemStack) {
		int i = itemStack.getItemDamage();
		Enum<?>[] values = Taam.BLOCK_ORE_META.values();

		if (i < 0 || i >= values.length) {
			i = 0;
		}

		return super.getUnlocalizedName() + "." + values[i].name();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {
		Enum<?>[] values = Taam.BLOCK_ORE_META.values();
		iconList = new IIcon[values.length];
		for (int i = 0; i < values.length; i++) {
			iconList[i] = ir.registerIcon(Taam.MOD_ID + ":ore." + values[i].name());
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs creativeTab, List list) {
		Enum<?>[] values = Taam.BLOCK_ORE_META.values();
		for (int i = 0; i < values.length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

}