package subaraki.exsartagine.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.items.IItemHandler;

public interface CustomRecipe<T extends IItemHandler> {

    boolean match(T handler);

    ItemStack getResult(T handler);

    Ingredient getIngredient();

    ItemStack getDisplay();
}
