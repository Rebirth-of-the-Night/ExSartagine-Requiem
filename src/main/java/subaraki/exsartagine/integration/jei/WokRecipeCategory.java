package subaraki.exsartagine.integration.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import subaraki.exsartagine.ExSartagine;
import subaraki.exsartagine.init.RecipeTypes;
import subaraki.exsartagine.integration.jei.wrappers.WokRecipeWrapper;
import subaraki.exsartagine.recipe.ModRecipes;

import java.util.List;
import java.util.stream.Collectors;

public class WokRecipeCategory extends AbstractCookingRecipeCategory<WokRecipeWrapper> {

    // Textures
    protected IDrawableStatic staticFlame;

    public static final ResourceLocation WOK_BACKGROUND = new ResourceLocation(ExSartagine.MODID, "textures/gui/jei/wok.png");


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
        List<WokRecipeWrapper> recipes = ModRecipes.getRecipeMap(RecipeTypes.WOK).entrySet().stream()
                .map(resourceLocationCustomRecipeEntry ->
                        new WokRecipeWrapper(resourceLocationCustomRecipeEntry.getValue(), registry.getJeiHelpers(), resourceLocationCustomRecipeEntry.getKey())).collect(Collectors.toList());
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

        ResourceLocation registryName = recipeWrapper.getName();
        if (registryName != null) {
            guiItemStacks.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
                if (slotIndex > 8) {
                //    String recipeModId = registryName.getNamespace();

                 //   boolean modIdDifferent = false;
                //    ResourceLocation itemRegistryName = ingredient.getItem().getRegistryName();
                //    if (itemRegistryName != null) {
                //        String itemModId = itemRegistryName.getNamespace();
                //        modIdDifferent = !recipeModId.equals(itemModId);
                //    }

                //    if (modIdDifferent) {
                //        String modName = ForgeModIdHelper.getInstance().getFormattedModNameForModId(recipeModId);
                //        if (modName != null) {
                //            tooltip.add(TextFormatting.GRAY + Translator.translateToLocalFormatted("jei.tooltip.recipe.by", modName));
                //        }
              //      }

                    boolean showAdvanced = Minecraft.getMinecraft().gameSettings.advancedItemTooltips || GuiScreen.isShiftKeyDown();
                    if (showAdvanced) {
                        tooltip.add(TextFormatting.DARK_GRAY + Translator.translateToLocalFormatted("jei.tooltip.recipe.id", registryName.toString()));
                    }
                }
            });
        }
    }
}
