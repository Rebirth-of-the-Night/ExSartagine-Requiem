package subaraki.exsartagine.recipe;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import subaraki.exsartagine.util.Helpers;

import java.util.ArrayList;
import java.util.List;

public class IIngredientWrapper extends Ingredient {

    private final IIngredient iIngredient;

    public IIngredientWrapper(IIngredient iIngredient) {
        this.iIngredient = iIngredient;
    }

    public IIngredient getiIngredient() {
        return iIngredient;
    }

    @Override
    public boolean apply(ItemStack itemStack) {
        if (this.iIngredient == null) {
            return itemStack == null || itemStack.isEmpty();
        }

        if (itemStack.isEmpty()) {
            return false;
        }
        return this.iIngredient.matches(CraftTweakerMC.getIItemStack(itemStack));
    }

    @Override
    public ItemStack[] getMatchingStacks() {

        List<ItemStack> matchingStacks = Helpers.getMatchingStacks(this.iIngredient, new ArrayList<>());
        return matchingStacks.toArray(new ItemStack[0]);
    }

    public static Ingredient createWrappedIIngredient(IIngredient iIngredient) {
        return new IIngredientWrapper(iIngredient);
    }
}
