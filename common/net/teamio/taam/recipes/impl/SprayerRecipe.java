package net.teamio.taam.recipes.impl;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.teamio.taam.recipes.BaseProcessingRecipeFluidBased;
import net.teamio.taam.recipes.ChancedOutput;

public class SprayerRecipe extends BaseProcessingRecipeFluidBased {

	private ChancedOutput[] output;

	public SprayerRecipe(String inputOreDict, FluidStack inputFluid, ItemStack output) {
		this.inputOreDict = inputOreDict;
		this.output = new ChancedOutput[] { new ChancedOutput(output, 1) };
		this.inputFluid = inputFluid;
	}

	public SprayerRecipe(ItemStack input, FluidStack inputFluid, ItemStack output) {
		this.input = input;
		this.output = new ChancedOutput[] { new ChancedOutput(output, 1) };
		this.inputFluid = inputFluid;
	}

	@Override
	public ChancedOutput[] getOutput() {
		return output;
	}

	@Override
	public ItemStack[] getOutput(ItemStack input) {
		return new ItemStack[] { output[0].output.copy() };
	}

	@Override
	public FluidStack getOutputFluid(ItemStack input, FluidStack inputFluid) {
		return null;
	}

	@Override
	public FluidStack getOutputFluid() {
		return null;
	}

}
