package subaraki.exsartagine.gui.common;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import subaraki.exsartagine.recipe.Recipes;

public class SlotPanInput extends SlotItemHandler {


    public SlotPanInput(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack input) {
        if (!input.isEmpty()) {
            //Prioritise PanRecipe inputs
            return !Recipes.hasResult(input,getItemHandler(),"pan");
        }
        return false;
    }
}