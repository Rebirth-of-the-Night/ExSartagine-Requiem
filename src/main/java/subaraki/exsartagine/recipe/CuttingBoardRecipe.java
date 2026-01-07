package subaraki.exsartagine.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.items.IItemHandler;
import subaraki.exsartagine.init.RecipeTypes;
import subaraki.exsartagine.tileentity.TileEntityCuttingBoard;

import java.util.Collections;
import java.util.List;

public class CuttingBoardRecipe implements CustomRecipe<IItemHandler> {

    private final Ingredient ingredient;
    private final Ingredient knife;
    private final ItemStack output;
    private final int cuts;

    public CuttingBoardRecipe(Ingredient ingredient, Ingredient knife, ItemStack output, int cuts) {
        this.ingredient = ingredient;
        this.knife = knife;
        this.output = output;
        this.cuts = cuts;
    }

    public Ingredient getKnife() {
        return knife;
    }

    @Override
    public List<Ingredient> getIngredients() {
        return Collections.singletonList(ingredient);
    }

    @Override
    public boolean itemMatch(IItemHandler handler) {
        return ingredient.test(handler.getStackInSlot(TileEntityCuttingBoard.INPUT));
    }

    @Override
    public ItemStack getDisplay() {
        return output;
    }

    @Override
    public int getCookTime() {
        return cuts;
    }

    @Override
    public IRecipeType<?> getType() {
        return RecipeTypes.CUTTING_BOARD;
    }
}
