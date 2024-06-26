package subaraki.exsartagine.integration.jei.wrappers;

import com.google.common.collect.Lists;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import subaraki.exsartagine.integration.jei.JeiPlugin;
import subaraki.exsartagine.recipe.KettleRecipe;

import java.util.List;

public class KettleRecipeWrapper implements IRecipeWrapper {

    private final KettleRecipe recipe;
    private final IJeiHelpers jeiHelpers;

    public KettleRecipeWrapper(KettleRecipe recipe, IJeiHelpers jeiHelpers) {
        this.recipe = recipe;
        this.jeiHelpers = jeiHelpers;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        IStackHelper stackHelper = jeiHelpers.getStackHelper();
        List<Ingredient> ingredientList = Lists.newArrayList(recipe.getCatalyst());
        ingredientList.addAll(recipe.getIngredients());
        List<List<ItemStack>> inputLists = stackHelper.expandRecipeItemStackInputs(ingredientList);
        ingredients.setInputLists(VanillaTypes.ITEM, inputLists);
        ingredients.setOutputs(VanillaTypes.ITEM, recipe.getResults(null));

        ingredients.setInput(VanillaTypes.FLUID,recipe.getInputFluid());
        ingredients.setOutput(VanillaTypes.FLUID,recipe.getOutputFluid());
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        JeiPlugin.drawDirtyIcon(minecraft, recipe, 90, 1);
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return JeiPlugin.getDirtyTooltip(recipe, 90, 1, mouseX, mouseY);
    }

}
