package subaraki.exsartagine.gui.common.slot;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import subaraki.exsartagine.recipe.CustomRecipe;
import subaraki.exsartagine.recipe.IRecipeType;
import subaraki.exsartagine.recipe.Recipes;

public class SlotInput<T extends IItemHandler,U extends CustomRecipe<T>> extends SlotItemHandler {


    protected final IRecipeType<U> type;

    public SlotInput(IItemHandler itemHandler, int index, int xPosition, int yPosition, IRecipeType<U> type) {
        super(itemHandler, index, xPosition, yPosition);
        this.type = type;
    }

    @Override
    public T getItemHandler() {
        return (T)super.getItemHandler();
    }

    private static final ItemStackHandler DUMMY = new ItemStackHandler();

    @Override
    public boolean isItemValid(ItemStack input) {
        DUMMY.setStackInSlot(0,input);
        return Recipes.hasResult((T)DUMMY,type);
    }

}