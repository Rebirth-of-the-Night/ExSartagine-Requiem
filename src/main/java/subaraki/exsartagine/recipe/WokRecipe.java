package subaraki.exsartagine.recipe;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.items.IItemHandler;

import java.util.List;

public class WokRecipe implements CustomRecipe<IItemHandler> {

    private final Ingredient ingredient;
    private final ItemStack output;

    private static final int INPUT = 0;

    public WokRecipe(Ingredient input, ItemStack output) {
        ingredient = input;
        this.output = output;
    }

    @Override
    public boolean itemMatch(IItemHandler handler) {
        return ingredient.test(handler.getStackInSlot(INPUT));
    }

    @Override
    public List<Ingredient> getIngredients() {
        return Lists.newArrayList(ingredient);
    }

    @Override
    public ItemStack getDisplay() {
        return output;
    }
}
