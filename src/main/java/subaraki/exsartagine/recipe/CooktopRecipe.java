package subaraki.exsartagine.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.items.IItemHandler;
import subaraki.exsartagine.init.RecipeTypes;

import java.util.Collections;
import java.util.List;

public class CooktopRecipe implements CustomRecipe<IItemHandler> {

    private final Ingredient ingredient;
    private final ItemStack output;
    private final int cookTime;

    public CooktopRecipe(Ingredient ingredient, ItemStack output, int cookTime) {
        this.ingredient = ingredient;
        this.output = output;
        this.cookTime = cookTime;
    }

    public boolean itemMatch(ItemStack stack) {
        return ingredient.test(stack);
    }

    @Override
    public boolean itemMatch(IItemHandler handler) {
        return itemMatch(handler.getStackInSlot(0));
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
        return cookTime;
    }

    @Override
    public IRecipeType<?> getType() {
        return RecipeTypes.COOKTOP;
    }
}
