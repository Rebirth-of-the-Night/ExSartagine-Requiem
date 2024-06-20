package subaraki.exsartagine.integration.jei.category;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import subaraki.exsartagine.integration.jei.wrappers.PotRecipeWrapper;
import subaraki.exsartagine.recipe.IRecipeType;
import subaraki.exsartagine.recipe.PotRecipe;
import subaraki.exsartagine.recipe.ModRecipes;
import subaraki.exsartagine.tileentity.TileEntityPot;

import java.util.List;
import java.util.stream.Collectors;

public class PotRecipeCategory extends AbstractCookingRecipeCategory<PotRecipeWrapper> {

    // Textures

    protected static final int inputSlot = 0;
    protected static final int outputSlot = 2;

    private final IRecipeType<? extends PotRecipe> recipeType;
    protected IDrawableStatic staticFlame;

    public PotRecipeCategory(IRecipeType<? extends PotRecipe> recipeType, ItemStack catalyst, IGuiHelper help) {
        super(recipeType, catalyst, help);
        this.recipeType = recipeType;
    }

    @Override
    public void setupGui() {
        background = guiHelper.createDrawable(BACKGROUNDS, 0, 59, 124, 56);
        staticFlame = guiHelper.drawableBuilder(BACKGROUNDS, 147, 0, 33, 18).build();
    }

    @Override
    public void setupRecipes(IModRegistry registry) {
        List<PotRecipeWrapper> recipes = ModRecipes.getRecipes(recipeType).stream()
                .map(potRecipe -> new PotRecipeWrapper(potRecipe, registry.getJeiHelpers())).collect(Collectors.toList());
        registry.addRecipes(recipes, getUid());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, PotRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(inputSlot, true, 42, 2);
        guiItemStacks.init(outputSlot, false, 102, 20);

        guiItemStacks.set(ingredients);

        IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();
        guiFluidStacks.init(0, true, 1, 1, 5, 54, TileEntityPot.TANK_CAPACITY, false, null);
        guiFluidStacks.set(ingredients);
    }
}
