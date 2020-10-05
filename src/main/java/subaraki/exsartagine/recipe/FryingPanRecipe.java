package subaraki.exsartagine.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.items.IItemHandler;

public class FryingPanRecipe implements CustomRecipe<IItemHandler> {

    private final Ingredient ingredient;
    private final ItemStack output;

    private static final int INPUT = 0;

    public FryingPanRecipe(Ingredient input, ItemStack output) {
        ingredient = input;
        this.output = output;
    }

    @Override
    public boolean match(IItemHandler handler) {
        return ingredient.test(handler.getStackInSlot(INPUT));
    }

    @Override
    public ItemStack getResult(IItemHandler handler) {
        return output.copy();
    }

    @Override
    public Ingredient getIngredient() {
        return ingredient;
    }

    @Override
    public ItemStack getDisplay() {
        return output;
    }
}
