package subaraki.exsartagine.integration.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;
import subaraki.exsartagine.ExSartagine;
import subaraki.exsartagine.init.ExSartagineBlocks;
import subaraki.exsartagine.gui.client.screen.GuiPot;
import subaraki.exsartagine.gui.client.screen.GuiSmelter;
import subaraki.exsartagine.gui.client.screen.KettleScreen;
import subaraki.exsartagine.init.RecipeTypes;

import java.util.ArrayList;
import java.util.List;

@JEIPlugin
public class JeiPlugin implements IModPlugin {

    private List<AbstractCookingRecipeCategory<?>> categories;

    @Override
    public void registerCategories(IRecipeCategoryRegistration reg) {
        categories = new ArrayList<>();
        IGuiHelper helper = reg.getJeiHelpers().getGuiHelper();
        categories.add(new SmelterSmeltingRecipeCategory(new ItemStack(ExSartagineBlocks.smelter),helper));
        categories.add(new PotRecipeCategory(new ItemStack(ExSartagineBlocks.pot),helper));
        categories.add(new KettleRecipeCategory(new ItemStack(ExSartagineBlocks.kettle),helper));
        categories.add(new WokRecipeCategory(new ItemStack(ExSartagineBlocks.wok),helper));

        for (AbstractCookingRecipeCategory<?> category : categories) {
            reg.addRecipeCategories(category);
        }
    }

    @Override
    public void register(IModRegistry registry) {
        for (AbstractCookingRecipeCategory<?> category : categories)
            category.setup(registry);

        //xpos, ypos,width,height
        registry.addRecipeClickArea(GuiPot.class, 80, 32, 26, 23, ExSartagine.MODID+"."+ RecipeTypes.POT);
        registry.addRecipeClickArea(GuiSmelter.class, 78, 20, 28, 23, ExSartagine.MODID+"."+RecipeTypes.SMELTER);
        registry.addRecipeClickArea(KettleScreen.class, 88, 32, 24, 23, ExSartagine.MODID+"."+RecipeTypes.KETTLE);
    }
}
