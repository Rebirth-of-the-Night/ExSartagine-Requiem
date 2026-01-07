package subaraki.exsartagine.integration.jei.category;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import subaraki.exsartagine.init.RecipeTypes;
import subaraki.exsartagine.integration.jei.wrappers.CuttingBoardRecipeWrapper;
import subaraki.exsartagine.recipe.ModRecipes;

import java.util.List;
import java.util.stream.Collectors;

public class CuttingBoardRecipeCategory extends AbstractCookingRecipeCategory<CuttingBoardRecipeWrapper> {

    public CuttingBoardRecipeCategory(ItemStack catalyst, IGuiHelper helper) {
        super(RecipeTypes.CUTTING_BOARD, catalyst, helper);
    }

    @Override
    public void setupGui() {
        background = guiHelper.createDrawable(BACKGROUNDS, 83, 117, 90, 49);
    }

    @Override
    public void setupRecipes(IModRegistry reg) {
        List<CuttingBoardRecipeWrapper> recipes = ModRecipes.getRecipes(RecipeTypes.CUTTING_BOARD).stream()
                .map(r -> new CuttingBoardRecipeWrapper(r, reg.getJeiHelpers()))
                .collect(Collectors.toList());
        reg.addRecipes(recipes, getUid());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, CuttingBoardRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup isg = recipeLayout.getItemStacks();
        isg.init(0, true, 19, 17);
        isg.init(1, true, 44, 5);
        isg.init(2, false, 70, 17);
        isg.set(ingredients);
    }
}
