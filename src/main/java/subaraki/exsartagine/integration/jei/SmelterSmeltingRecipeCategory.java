package subaraki.exsartagine.integration.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import subaraki.exsartagine.init.RecipeTypes;
import subaraki.exsartagine.integration.jei.wrappers.SmelterSmeltingRecipeWrapper;
import subaraki.exsartagine.recipe.ModRecipes;

import java.util.List;
import java.util.stream.Collectors;

public class SmelterSmeltingRecipeCategory extends AbstractCookingRecipeCategory<SmelterSmeltingRecipeWrapper> {

    // Textures

    protected static final int inputSlot = 0;
    protected static final int outputSlot = 2;

    protected IDrawableStatic staticFlame;

    public SmelterSmeltingRecipeCategory(ItemStack catalyst, IGuiHelper help) {
        super(RecipeTypes.SMELTER, catalyst, help);
    }

    @Override
    public void setupGui() {
        background = guiHelper.createDrawable(BACKGROUNDS, 0, 0, 104, 54);
        staticFlame = guiHelper.drawableBuilder(BACKGROUNDS, 104, 44, 36, 15).build();
        cookProgress = guiHelper.createAnimatedDrawable(staticFlame, 200, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        cookProgress.draw(minecraft, 16, 0);
    }

    @Override
    public void setupRecipes(IModRegistry registry) {
        IJeiHelpers helpers = registry.getJeiHelpers();
        List<SmelterSmeltingRecipeWrapper> smelterSmeltingRecipeWrappers = ModRecipes.getRecipes(RecipeTypes.SMELTER).stream()
                .map(customRecipe -> new SmelterSmeltingRecipeWrapper(customRecipe,helpers)).collect(Collectors.toList());
        registry.addRecipes(smelterSmeltingRecipeWrappers, getUid());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, SmelterSmeltingRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(inputSlot, true, 0, 0);
        guiItemStacks.init(1, false, 60, 18);
        guiItemStacks.init(2, false, 84, 22);

        guiItemStacks.set(ingredients);
    }
}
