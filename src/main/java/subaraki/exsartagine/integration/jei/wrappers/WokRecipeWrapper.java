package subaraki.exsartagine.integration.jei.wrappers;

import com.google.common.collect.Lists;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import subaraki.exsartagine.ExSartagine;
import subaraki.exsartagine.recipe.WokRecipe;

import java.awt.*;
import java.util.List;

public class WokRecipeWrapper implements IRecipeWrapper {


    private final WokRecipe recipe;
    private final IJeiHelpers jeiHelpers;
    private final ResourceLocation name;

    public WokRecipeWrapper(WokRecipe recipe, IJeiHelpers jeiHelpers, ResourceLocation name) {
        this.recipe = recipe;
        this.jeiHelpers = jeiHelpers;
        this.name = name;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        IStackHelper stackHelper = jeiHelpers.getStackHelper();
        List<List<ItemStack>> inputLists = stackHelper.expandRecipeItemStackInputs(Lists.newArrayList(recipe.getIngredients()));
        ingredients.setInputLists(VanillaTypes.ITEM, inputLists);
        ingredients.setOutputs(VanillaTypes.ITEM, recipe.getResults(null));

        ingredients.setInput(VanillaTypes.FLUID,recipe.getInputFluid());
    }

    public ResourceLocation getName() {
        return name;
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        int flips = recipe.getFlips();
        if (flips > 0) {
            String flipString = ""+flips;
            FontRenderer fontRenderer = minecraft.fontRenderer;
            int stringWidth = fontRenderer.getStringWidth(flipString);
            fontRenderer.drawString(flipString, recipeWidth - stringWidth - 66, 47, Color.gray.getRGB());
            Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(ExSartagine.MODID,"textures/gui/jei/spatula.png"));

            Gui.drawModalRectWithCustomSizedTexture(recipeWidth/2 - 3, 31, 0, 0,16,16,16,16);

        }
    }
}
