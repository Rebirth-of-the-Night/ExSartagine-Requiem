package subaraki.exsartagine.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.items.IItemHandler;
import subaraki.exsartagine.init.RecipeTypes;

import java.util.Collections;
import java.util.List;

public class SmelterRecipe implements CustomRecipe<IItemHandler> {

    private final Ingredient ingredient;
    private final ItemStack output;

    private static final int INPUT = 0;

    public SmelterRecipe(Ingredient input, ItemStack output) {
        ingredient = input;
        this.output = output;
    }

    @Override
    public boolean itemMatch(IItemHandler handler) {
        return ingredient.test(handler.getStackInSlot(INPUT));
    }

    @Override
    public ItemStack getResult(IItemHandler handler) {
        return output.copy();
    }

    @Override
    public List<Ingredient> getIngredients() {
        return Collections.singletonList(ingredient);
    }

    @Override
    public ItemStack getDisplay() {
        return output;
    }

    @Override
    public IRecipeType<IItemHandler> getType() {
        return RecipeTypes.SMELTER;
    }
}
