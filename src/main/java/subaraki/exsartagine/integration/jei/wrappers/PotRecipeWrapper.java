package subaraki.exsartagine.integration.jei.wrappers;

import com.google.common.collect.Lists;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import subaraki.exsartagine.integration.jei.category.AbstractCookingRecipeCategory;
import subaraki.exsartagine.recipe.PotRecipe;

import java.util.List;

public class PotRecipeWrapper implements IRecipeWrapper {

    private final PotRecipe recipe;
    private final IJeiHelpers jeiHelpers;
    private final IDrawableAnimated cookProgress;

    public PotRecipeWrapper(PotRecipe recipe, IJeiHelpers jeiHelpers) {
        this.recipe = recipe;
        this.jeiHelpers = jeiHelpers;
        final IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        this.cookProgress = guiHelper.createAnimatedDrawable(
                guiHelper.createDrawable(AbstractCookingRecipeCategory.BACKGROUNDS, 147, 0, 33, 18),
                Math.max(recipe.getCookTime(), 4), IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        IStackHelper stackHelper = jeiHelpers.getStackHelper();
        List<List<ItemStack>> inputLists = stackHelper.expandRecipeItemStackInputs(Lists.newArrayList(recipe.getIngredients()));
        ingredients.setInputLists(VanillaTypes.ITEM, inputLists);
        FluidStack fluid = recipe.getInputFluid();
        if (fluid != null && fluid.amount > 0) {
            ingredients.setInput(VanillaTypes.FLUID, fluid);
        }
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getDisplay());
    }

    @Override
    public void drawInfo(final Minecraft minecraft, final int recipeWidth, final int recipeHeight, final int mouseX, final int mouseY) {
        cookProgress.draw(minecraft, 63, 20);
    }
}
