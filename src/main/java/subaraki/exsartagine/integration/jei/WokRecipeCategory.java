package subaraki.exsartagine.integration.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import subaraki.exsartagine.integration.jei.wrappers.WokRecipeWrapper;
import subaraki.exsartagine.recipe.WokRecipe;
import subaraki.exsartagine.recipe.Recipes;
import subaraki.exsartagine.util.Reference;

import java.util.List;
import java.util.stream.Collectors;

public class WokRecipeCategory extends AbstractCookingRecipeCategory<WokRecipeWrapper> {

    // Textures
    protected IDrawableStatic staticFlame;

    public static final ResourceLocation WOK_BACKGROUND = new ResourceLocation(Reference.MODID, "textures/gui/jei/wok.png");


    public WokRecipeCategory(ItemStack catalyst, IGuiHelper help) {
        super(catalyst, help);
    }

    @Override
    public void setupGui() {
        background = guiHelper.createDrawable(WOK_BACKGROUND, 0, 0, 148, 54);
        staticFlame = guiHelper.drawableBuilder(WOK_BACKGROUND, 148, 0, 22, 15).build();
        cookProgress = guiHelper.createAnimatedDrawable(staticFlame, 100, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        cookProgress.draw(minecraft, 69, 19);
    }

    @Override
    public void setupRecipes(IModRegistry registry) {
        List<WokRecipeWrapper> recipes = Recipes.getWokRecipes().stream()
                .map(wokRecipe -> new WokRecipeWrapper(wokRecipe, registry.getJeiHelpers())).collect(Collectors.toList());
        registry.addRecipes(recipes, getUid());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, WokRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        int xPos = 11;
        int yPos = 0;

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3 ; x++) {
                guiItemStacks.init(x + 3 * y, true, xPos + 18 * x, yPos + 18 * y);
            }
        }

        xPos += 83;

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3 ; x++) {
                guiItemStacks.init(9 + x + 3 * y, false, xPos + 18 * x, yPos + 18 * y);
            }
        }
        guiItemStacks.set(ingredients);

        IGuiFluidStackGroup guiFluidStackGroup = recipeLayout.getFluidStacks();
        guiFluidStackGroup.init(0,true,1,1,7,52,1000,true,null);
        guiFluidStackGroup.set(ingredients);
    }
}
