package net.teamio.taam.integration.jei;

import javax.annotation.Nonnull;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.util.Translator;
import net.minecraft.util.ResourceLocation;
import net.teamio.taam.Taam;

public class GrinderCategory extends ProcessingCategory {


	private final String localizedName = Translator.translateToLocal(Taam.INTEGRATION_JEI_CATNAME_GRINDER);

	@Nonnull
	protected final IDrawable background;

	public GrinderCategory(IGuiHelper guiHelper) {
		ResourceLocation bgLocation = new ResourceLocation("taam", "textures/gui/processors.png");
		background = guiHelper.createDrawable(bgLocation, 0, 63, 162, 62);
	}

	@Override
	public String getTitle() {
		return localizedName;
	}

	@Override
	public String getUid() {
		return Taam.INTEGRATION_JEI_CAT_GRINDER;
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

}
