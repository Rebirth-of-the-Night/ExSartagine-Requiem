package subaraki.exsartagine.integration.jei.wrappers;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import subaraki.exsartagine.integration.jei.category.AbstractCookingRecipeCategory;
import subaraki.exsartagine.recipe.CuttingBoardRecipe;

import java.util.ArrayList;
import java.util.List;

public class CuttingBoardRecipeWrapper implements IRecipeWrapper {

    private final CuttingBoardRecipe recipe;
    private final IJeiHelpers jeiHelpers;
    private final IDrawableAnimated progress;

    public CuttingBoardRecipeWrapper(CuttingBoardRecipe recipe, IJeiHelpers jeiHelpers) {
        this.recipe = recipe;
        this.jeiHelpers = jeiHelpers;
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        this.progress = guiHelper.createAnimatedDrawable(
                guiHelper.createDrawable(AbstractCookingRecipeCategory.BACKGROUNDS, 173, 139, 15, 9),
                Math.max(recipe.getCookTime() * 16, 4), IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        List<Ingredient> ings = new ArrayList<>(recipe.getIngredients());
        ings.add(recipe.getKnife());
        List<List<ItemStack>> inputLists = jeiHelpers.getStackHelper()
                .expandRecipeItemStackInputs(ings);
        ingredients.setInputLists(VanillaTypes.ITEM, inputLists);
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getDisplay());
    }

    @Override
    public void drawInfo(Minecraft mc, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        progress.draw(mc, 46, 22);
        String cutCount = Integer.toString(recipe.getCookTime());
        mc.fontRenderer.drawStringWithShadow(cutCount, 53.5f - mc.fontRenderer.getStringWidth(cutCount) / 2f, 33f, 0xffffff);
    }
}
