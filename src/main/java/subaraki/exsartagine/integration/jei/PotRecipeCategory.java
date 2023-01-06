package subaraki.exsartagine.integration.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import subaraki.exsartagine.init.RecipeTypes;
import subaraki.exsartagine.integration.jei.wrappers.PotRecipeWrapper;
import subaraki.exsartagine.recipe.PotRecipe;
import subaraki.exsartagine.recipe.Recipes;

import java.util.List;
import java.util.stream.Collectors;

public class PotRecipeCategory extends AbstractCookingRecipeCategory<PotRecipeWrapper> {

    // Textures

    protected static final int inputSlot = 0;
    protected static final int outputSlot = 2;

    protected IDrawableStatic staticFlame;


    public PotRecipeCategory(ItemStack catalyst, IGuiHelper help) {
        super(catalyst, help);
    }

    @Override
    public void setupGui() {
        background = guiHelper.createDrawable(BACKGROUNDS, 0, 59, 124, 56);
        staticFlame = guiHelper.drawableBuilder(BACKGROUNDS, 147, 0, 33, 18).build();
        cookProgress = guiHelper.createAnimatedDrawable(staticFlame, 200, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        cookProgress.draw(minecraft, 63, 20);
    }

    @Override
    public void setupRecipes(IModRegistry registry) {
        List<PotRecipeWrapper> recipes = Recipes.getRecipes(RecipeTypes.POT).stream()
                .map(potRecipe -> new PotRecipeWrapper((PotRecipe) potRecipe, registry.getJeiHelpers())).collect(Collectors.toList());
        registry.addRecipes(recipes, getUid());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, PotRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(inputSlot, true, 42, 2);
        guiItemStacks.init(outputSlot, false, 102, 20);

        guiItemStacks.set(ingredients);
    }
}
