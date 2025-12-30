package subaraki.exsartagine.integration.jei.category;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import subaraki.exsartagine.init.RecipeTypes;
import subaraki.exsartagine.integration.jei.wrappers.CooktopRecipeWrapper;
import subaraki.exsartagine.recipe.ModRecipes;

import java.util.List;
import java.util.stream.Collectors;

public class CooktopRecipeCategory extends AbstractCookingRecipeCategory<CooktopRecipeWrapper> {

    public CooktopRecipeCategory(ItemStack catalyst, IGuiHelper helper) {
        super(RecipeTypes.COOKTOP, catalyst, helper);
    }

    @Override
    public void setupGui() {
        background = guiHelper.createDrawable(BACKGROUNDS, 0, 115, 82, 54);
    }

    @Override
    public void setupRecipes(IModRegistry reg) {
        List<CooktopRecipeWrapper> recipes = ModRecipes.getRecipes(RecipeTypes.COOKTOP).stream()
                .map(r -> new CooktopRecipeWrapper(r, reg.getJeiHelpers()))
                .collect(Collectors.toList());
        reg.addRecipes(recipes, getUid());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, CooktopRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup isg = recipeLayout.getItemStacks();
        isg.init(0, true, 0, 0);
        isg.init(1, false, 60, 18);
        isg.set(ingredients);
    }
}
