package net.teamio.taam.integration.jei;

import javax.annotation.Nonnull;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.util.Translator;
import net.minecraft.util.ResourceLocation;
import net.teamio.taam.Taam;

public class CrusherCategory extends ProcessingCategory {

	private final String localizedName = Translator.translateToLocal(Taam.INTEGRATION_JEI_CATNAME_CRUSHER);

	@Nonnull
	protected final IDrawable background;

	public CrusherCategory(IGuiHelper guiHelper) {
		ResourceLocation bgLocation = new ResourceLocation("taam", "textures/gui/processors.png");
		background = guiHelper.createDrawable(bgLocation, 0, 0, 162, 62);
	}

	@Override
	public String getTitle() {
		return localizedName;
	}

	@Override
	public String getUid() {
		return Taam.INTEGRATION_JEI_CAT_CRUSHER;
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

}
