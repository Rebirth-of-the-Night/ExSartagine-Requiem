package subaraki.exsartagine.integration.jei.wrappers;

import com.google.common.collect.Lists;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import subaraki.exsartagine.integration.jei.JeiPlugin;
import subaraki.exsartagine.recipe.SmelterRecipe;
import subaraki.exsartagine.util.ConfigHandler;

import java.util.List;

public class SmelterSmeltingRecipeWrapper implements IRecipeWrapper {

    private final SmelterRecipe recipe;
    private final IJeiHelpers helpers;

    public SmelterSmeltingRecipeWrapper(SmelterRecipe recipe, IJeiHelpers helpers) {
        this.recipe = recipe;
        this.helpers = helpers;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        IStackHelper stackHelper = helpers.getStackHelper();
        List<List<ItemStack>> inputLists = stackHelper.expandRecipeItemStackInputs(Lists.newArrayList(recipe.getIngredients()));
        ingredients.setInputLists(VanillaTypes.ITEM, inputLists);
        ingredients.setOutputs(VanillaTypes.ITEM, Lists.newArrayList(recipe.getDisplay(),recipe.getDisplay()));
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        minecraft.fontRenderer.drawString("Chance: " + ConfigHandler.percent + "%",38,45,0x404040);
        JeiPlugin.drawDirtyIcon(minecraft, recipe, 29, 20);
    }

    @Override
    public List<String> getTooltipStrings(final int mouseX, final int mouseY) {
        return JeiPlugin.getDirtyTooltip(recipe, 29, 20, mouseX, mouseY);
    }

}
