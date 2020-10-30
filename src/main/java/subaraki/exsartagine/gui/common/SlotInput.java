package subaraki.exsartagine.gui.common;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import subaraki.exsartagine.recipe.Recipes;

public class SlotInput extends SlotItemHandler {


    protected final String type;

    public SlotInput(IItemHandler itemHandler, int index, int xPosition, int yPosition, String type) {
        super(itemHandler, index, xPosition, yPosition);
        this.type = type;
    }

    @Override
    public boolean isItemValid(ItemStack input) {
            return Recipes.hasResult(new ItemStackHandler(NonNullList.from(ItemStack.EMPTY,input)),type);
    }
}