package subaraki.exsartagine.integration.jei.wrappers;

import com.google.common.collect.Lists;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.ItemStack;
import subaraki.exsartagine.recipe.FryingPanRecipe;

import java.util.List;

public class FryingPanRecipeWrapper implements IRecipeWrapper {


    private final FryingPanRecipe recipe;
    private final IJeiHelpers jeiHelpers;

    public FryingPanRecipeWrapper(FryingPanRecipe recipe, IJeiHelpers jeiHelpers) {
        this.recipe = recipe;
        this.jeiHelpers = jeiHelpers;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        IStackHelper stackHelper = jeiHelpers.getStackHelper();
        List<List<ItemStack>> inputLists = stackHelper.expandRecipeItemStackInputs(Lists.newArrayList(recipe.getIngredients()));
        ingredients.setInputLists(VanillaTypes.ITEM, inputLists);
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getDisplay());
    }
}
