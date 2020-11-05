package subaraki.exsartagine.integration.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.item.ItemStack;
import subaraki.exsartagine.ExSartagine;
import subaraki.exsartagine.block.ExSartagineBlocks;
import subaraki.exsartagine.gui.client.GuiPan;
import subaraki.exsartagine.gui.client.GuiPot;
import subaraki.exsartagine.gui.client.GuiSmelter;
import subaraki.exsartagine.gui.client.KettleScreen;
import subaraki.exsartagine.util.Reference;

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
        categories.add(new FryingPanRecipeCategory(new ItemStack(ExSartagineBlocks.pan),helper));

        for (AbstractCookingRecipeCategory<?> category : categories) {
            reg.addRecipeCategories(category);
        }
    }

    @Override
    public void register(IModRegistry registry) {
        for (AbstractCookingRecipeCategory<?> category : categories)
            category.setup(registry);

        //xpos, ypos,width,height
        registry.addRecipeClickArea(GuiPan.class, 78, 32, 26, 23, Reference.MODID+".pan");
        registry.addRecipeClickArea(GuiPot.class, 80, 32, 26, 23, Reference.MODID+".pot");
        registry.addRecipeClickArea(GuiSmelter.class, 78, 20, 28, 23, Reference.MODID+".smelter");
        registry.addRecipeClickArea(KettleScreen.class, 88, 32, 24, 23, Reference.MODID+".kettle");
    }
}
