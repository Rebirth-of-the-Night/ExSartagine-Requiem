package subaraki.exsartagine.integration.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import subaraki.exsartagine.ExSartagine;
import subaraki.exsartagine.gui.client.GuiHelpers;
import subaraki.exsartagine.gui.client.screen.GuiCauldron;
import subaraki.exsartagine.gui.client.screen.GuiPot;
import subaraki.exsartagine.gui.client.screen.GuiSmelter;
import subaraki.exsartagine.gui.client.screen.KettleScreen;
import subaraki.exsartagine.gui.common.ContainerKettle;
import subaraki.exsartagine.init.ExSartagineBlocks;
import subaraki.exsartagine.init.RecipeTypes;
import subaraki.exsartagine.integration.jei.category.*;
import subaraki.exsartagine.recipe.DirtyingRecipe;
import subaraki.exsartagine.util.Helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JEIPlugin
public class JeiPlugin implements IModPlugin {

    private List<AbstractCookingRecipeCategory<?>> categories;

    private KettleRecipeCategory kettleRecipeCategory;

    @Override
    public void registerCategories(IRecipeCategoryRegistration reg) {
        categories = new ArrayList<>();
        IGuiHelper helper = reg.getJeiHelpers().getGuiHelper();
        categories.add(new SmelterSmeltingRecipeCategory(new ItemStack(ExSartagineBlocks.smelter),helper));
        categories.add(new PotRecipeCategory(RecipeTypes.POT, new ItemStack(ExSartagineBlocks.pot),helper));
        if (ExSartagineBlocks.cauldron != null) {
            categories.add(new PotRecipeCategory(RecipeTypes.CAULDRON, new ItemStack(ExSartagineBlocks.cauldron), helper));
        }
        kettleRecipeCategory = new KettleRecipeCategory(new ItemStack(ExSartagineBlocks.kettle),helper);
        categories.add(kettleRecipeCategory);
        categories.add(new WokRecipeCategory(new ItemStack(ExSartagineBlocks.wok),helper));

        for (AbstractCookingRecipeCategory<?> category : categories) {
            reg.addRecipeCategories(category);
        }
    }

    @Override
    public void register(IModRegistry registry) {
        for (AbstractCookingRecipeCategory<?> category : categories)
            category.setup(registry);

        if (ExSartagineBlocks.cauldron != null) {
            registry.addRecipeCatalyst(new ItemStack(ExSartagineBlocks.cauldron), ExSartagine.MODID + "." + RecipeTypes.POT.name());
        }

        //xpos, ypos,width,height
        registry.addRecipeClickArea(GuiPot.class, 80, 32, 26, 23, ExSartagine.MODID+"."+ RecipeTypes.POT.name());
        registry.addRecipeClickArea(GuiCauldron.class, 80, 32, 26, 23, ExSartagine.MODID+"."+ RecipeTypes.POT.name(), ExSartagine.MODID+"."+ RecipeTypes.CAULDRON.name());
        registry.addRecipeClickArea(GuiSmelter.class, 78, 20, 28, 23, ExSartagine.MODID+"."+RecipeTypes.SMELTER.name());
        registry.addRecipeClickArea(KettleScreen.class, 85, 34, 21, 17, ExSartagine.MODID+"."+RecipeTypes.KETTLE.name());

        IRecipeTransferRegistry recipeTransferRegistry = registry.getRecipeTransferRegistry();

        recipeTransferRegistry.addRecipeTransferHandler(ContainerKettle.class,kettleRecipeCategory.getUid(),0,10,10+10,36);

        //registry.addGuiScreenHandler(KettleScreen.class, KettleGuiProperties::new);
    }

    private static final ResourceLocation CLEAN_TEXTURE = new ResourceLocation(ExSartagine.MODID, "textures/gui/clean.png");

    public static void drawDirtyIcon(Minecraft mc, DirtyingRecipe recipe, int x, int y) {
        int dirtyTime = recipe.getDirtyTime();
        if (dirtyTime > 0) {
            mc.getTextureManager().bindTexture(GuiHelpers.DIRTY_TEXTURE);
            GlStateManager.enableBlend();
            GlStateManager.color(1F, 1F, 1F, 1F);
            Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, 16, 16, 16, 16);
        } else if (dirtyTime < 0) {
            mc.getTextureManager().bindTexture(CLEAN_TEXTURE);
            GlStateManager.enableBlend();
            GlStateManager.color(1F, 1F, 1F, 1F);
            Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, 16, 16, 16, 16);
        }
    }

    public static List<String> getDirtyTooltip(DirtyingRecipe recipe, int x, int y, int mx, int my) {
        int dirtyTime = recipe.getDirtyTime();
        if (dirtyTime != 0 && GuiHelpers.isPointInRect(mx - 1, my - 1, x, y, 17, 17)) {
            if (dirtyTime > 0) {
                return Collections.singletonList(TextFormatting.RED + I18n.format(ExSartagine.MODID + ".gui.dirty_recipe", Helpers.formatTime(dirtyTime)));
            } else {
                return Collections.singletonList(I18n.format(ExSartagine.MODID + ".gui.clean_recipe"));
            }
        }
        return Collections.emptyList();
    }

}
