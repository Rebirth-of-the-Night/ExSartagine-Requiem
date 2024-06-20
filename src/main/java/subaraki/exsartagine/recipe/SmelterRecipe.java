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
    private final int dirtyTime;

    private static final int INPUT = 0;

    public SmelterRecipe(Ingredient input, ItemStack output, int dirtyTime) {
        this.ingredient = input;
        this.output = output;
        this.dirtyTime = dirtyTime;
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
    public int getCookTime() {
        return 199;
    }

    public int getDirtyTime() {
        return dirtyTime;
    }

    @Override
    public IRecipeType<?> getType() {
        return RecipeTypes.SMELTER;
    }
}
