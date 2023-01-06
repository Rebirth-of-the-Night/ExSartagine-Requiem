package subaraki.exsartagine.gui.common;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import subaraki.exsartagine.recipe.CustomRecipe;
import subaraki.exsartagine.recipe.IRecipeType;
import subaraki.exsartagine.recipe.Recipes;

public class SlotInput<T extends IItemHandler,U extends CustomRecipe<T>> extends SlotItemHandler {


    protected final IRecipeType<T> type;

    public SlotInput(IItemHandler itemHandler, int index, int xPosition, int yPosition, IRecipeType<T> type) {
        super(itemHandler, index, xPosition, yPosition);
        this.type = type;
    }

    @Override
    public T getItemHandler() {
        return (T)super.getItemHandler();
    }

    @Override
    public boolean isItemValid(ItemStack input) {
            return Recipes.hasResult(getItemHandler(),type);
    }
}