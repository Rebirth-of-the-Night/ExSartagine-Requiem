package subaraki.exsartagine.integration.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import subaraki.exsartagine.ExSartagine;
import subaraki.exsartagine.recipe.IRecipeType;
import subaraki.exsartagine.util.Reference;

public abstract class AbstractCookingRecipeCategory<T extends IRecipeWrapper> implements IRecipeCategory<T> {
    private final String uid;
    protected IGuiHelper guiHelper;
    private final ItemStack catalyst;
    protected IDrawable background;
    protected IDrawableAnimated cookProgress;

    public static final String BACKGROUND = "textures/gui/jei/backgrounds.png";
    public static final ResourceLocation BACKGROUNDS = new ResourceLocation(ExSartagine.MODID, BACKGROUND);

    public AbstractCookingRecipeCategory(IRecipeType<?> type, ItemStack catalyst, IGuiHelper helper) {
        this.uid = ExSartagine.MODID+"."+type.name();
        this.catalyst = catalyst;
        this.guiHelper = helper;
        setupGui();
    }

    public abstract void setupGui();

    public final void setup(IModRegistry reg) {
        reg.addRecipeCatalyst(catalyst, getUid());
        setupRecipes(reg);
    }

    public abstract void setupRecipes(IModRegistry reg);

    @Override
    public String getUid() {
        return uid;
    }

    @Override
    public String getTitle() {
        return I18n.format(catalyst.getTranslationKey() + ".name");
    }

    @Override
    public String getModName() {
        return Reference.NAME;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }
}