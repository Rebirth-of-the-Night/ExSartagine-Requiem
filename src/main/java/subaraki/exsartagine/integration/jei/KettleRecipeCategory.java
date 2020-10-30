package subaraki.exsartagine.integration.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import subaraki.exsartagine.integration.jei.wrappers.KettleRecipeWrapper;
import subaraki.exsartagine.integration.jei.wrappers.PotRecipeWrapper;
import subaraki.exsartagine.recipe.KettleRecipe;
import subaraki.exsartagine.recipe.PotRecipe;
import subaraki.exsartagine.recipe.Recipes;

import java.util.List;
import java.util.stream.Collectors;

public class KettleRecipeCategory extends AbstractCookingRecipeCategory<KettleRecipeWrapper> {

    // Textures

    protected static final int inputSlot = 0;
    protected static final int outputSlot = 2;

    protected IDrawableStatic staticFlame;


    public KettleRecipeCategory(ItemStack catalyst, IGuiHelper help) {
        super(catalyst, help);
    }

    @Override
    public void setupGui() {
        background = guiHelper.createDrawable(BACKGROUNDS, 0, 169, 166, 54);
        staticFlame = guiHelper.drawableBuilder(BACKGROUNDS, 120, 0, 22, 15).build();
        cookProgress = guiHelper.createAnimatedDrawable(staticFlame, 100, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        cookProgress.draw(minecraft, 87, 19);
    }

    @Override
    public void setupRecipes(IModRegistry registry) {
        List<KettleRecipeWrapper> recipes = Recipes.getRecipes("kettle").stream()
                .map(potRecipe -> new KettleRecipeWrapper((KettleRecipe) potRecipe, registry.getJeiHelpers())).collect(Collectors.toList());
        registry.addRecipes(recipes, getUid());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, KettleRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        IGuiFluidStackGroup guiFluidStackGroup = recipeLayout.getFluidStacks();

        guiFluidStackGroup.init(0,true,1,1,7,52,10000,true,null);

        int xPos = 29;
        int yPos = 0;

        guiItemStacks.init(0, true, 6, yPos + 18);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3 ; x++) {
                guiItemStacks.init(1 + x + 3 * y, true, xPos + 18 * x, yPos + 18 * y);
            }
        }

        xPos += 83;

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3 ; x++) {
                guiItemStacks.init(10 + x + 3 * y, false, xPos + 18 * x, yPos + 18 * y);
            }
        }
        guiItemStacks.set(ingredients);
        guiFluidStackGroup.set(ingredients);
    }
}
