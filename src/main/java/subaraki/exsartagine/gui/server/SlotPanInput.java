package subaraki.exsartagine.gui.server;

import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import subaraki.exsartagine.recipe.FryingPanRecipes;

public class SlotPanInput extends SlotItemHandler {


    public SlotPanInput(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack input) {
        if (!input.isEmpty()) {
            //Prioritise PanRecipe inputs
            if (!FryingPanRecipes.hasResult(input)) {
                return true;
            }
            //if no pan input was found, check if the input is food
            else {
                return input.getItem() instanceof ItemFood;
            }
        }
        return false;
    }
}