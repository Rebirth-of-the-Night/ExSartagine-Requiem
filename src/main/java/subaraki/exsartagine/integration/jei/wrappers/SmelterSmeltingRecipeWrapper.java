package subaraki.exsartagine.integration.jei.wrappers;

import com.google.common.collect.Lists;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import subaraki.exsartagine.recipe.SmelterRecipe;
import subaraki.exsartagine.util.ConfigHandler;

import java.util.List;

public class SmelterSmeltingRecipeWrapper implements IRecipeWrapper {

    private final SmelterRecipe recipe;
    private final IJeiHelpers helpers;
    private final double bonusChance;

    public SmelterSmeltingRecipeWrapper(SmelterRecipe recipe, IJeiHelpers helpers) {
        this.recipe = recipe;
        this.helpers = helpers;
        bonusChance = ConfigHandler.percent;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        IStackHelper stackHelper = helpers.getStackHelper();
        List<List<ItemStack>> inputLists = stackHelper.expandRecipeItemStackInputs(Lists.newArrayList(recipe.getIngredient()));
        ingredients.setInputLists(VanillaTypes.ITEM, inputLists);
        ingredients.setOutputs(VanillaTypes.ITEM, Lists.newArrayList(recipe.getDisplay(),recipe.getDisplay()));
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        minecraft.fontRenderer.drawString("Chance: " + bonusChance + "%",38,45,0x404040);
    }
}
