package subaraki.exsartagine.integration.jei.wrappers;

import com.google.common.collect.Lists;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import subaraki.exsartagine.integration.jei.category.AbstractCookingRecipeCategory;
import subaraki.exsartagine.recipe.CooktopRecipe;

import java.util.List;

public class CooktopRecipeWrapper implements IRecipeWrapper {

    private final CooktopRecipe recipe;
    private final IJeiHelpers jeiHelpers;
    private final IDrawableAnimated cookProgress;

    public CooktopRecipeWrapper(CooktopRecipe recipe, IJeiHelpers jeiHelpers) {
        this.recipe = recipe;
        this.jeiHelpers = jeiHelpers;
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        this.cookProgress = guiHelper.createAnimatedDrawable(
                guiHelper.createDrawable(AbstractCookingRecipeCategory.BACKGROUNDS, 120, 0, 22, 15),
                Math.max(recipe.getCookTime(), 4), IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        List<List<ItemStack>> inputLists = jeiHelpers.getStackHelper()
                .expandRecipeItemStackInputs(Lists.newArrayList(recipe.getIngredients()));
        ingredients.setInputLists(VanillaTypes.ITEM, inputLists);
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getDisplay());
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        cookProgress.draw(minecraft, 25, 19);
    }
}
