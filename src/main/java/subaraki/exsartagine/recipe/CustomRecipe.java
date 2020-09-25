package subaraki.exsartagine.recipe;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public interface CustomRecipe<T extends IItemHandler> {

    boolean match(T handler);

    ItemStack getResult(T handler);

}
