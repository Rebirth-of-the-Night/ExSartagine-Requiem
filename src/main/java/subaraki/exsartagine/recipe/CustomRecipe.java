package subaraki.exsartagine.recipe;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.IItemHandler;
import subaraki.exsartagine.Utils;

import java.util.List;

public interface CustomRecipe<T extends IItemHandler> {

    ItemStack getDisplay();

    List<Ingredient> getIngredients();

    boolean itemMatch(T handler);

    default ItemStack getResult(T handler) {
        return getDisplay().copy();
    }

    default List<ItemStack> getResults(T handler) {
        return Lists.newArrayList(getResult(handler));
    }

    default NonNullList<ItemStack> getRemainingItems(T handler) {
        return Utils.defaultRecipeGetRemainingItems(handler);
    }
}
