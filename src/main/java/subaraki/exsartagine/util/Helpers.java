package subaraki.exsartagine.util;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Collections;
import java.util.List;

public class Helpers {
    public static List<ItemStack> getMatchingStacks(IIngredient ingredient, List<ItemStack> result) {

        if (ingredient == null) {
            return result;
        }

        if (ingredient instanceof IOreDictEntry) {
            NonNullList<ItemStack> ores = OreDictionary.getOres(((IOreDictEntry) ingredient).getName());
            getMatchingStacks(ores, ingredient.getAmount(), result);

        } else if (ingredient instanceof IItemStack) {
            ItemStack itemStack = CraftTweakerMC.getItemStack((IItemStack) ingredient);
            itemStack.setCount(ingredient.getAmount());
            result.add(itemStack);

        } else {
            List<IItemStack> items = ingredient.getItems();

            for (IItemStack item : items) {
                ItemStack itemStack = CraftTweakerMC.getItemStack(item);
                getMatchingStacks(Collections.singletonList(itemStack), ingredient.getAmount(), result);
            }
        }

        return result;
    }

    public static List<ItemStack> getMatchingStacks(List<ItemStack> itemStackList, int amount, List<ItemStack> result) {

        NonNullList<ItemStack> internalList = NonNullList.create();

        for (ItemStack itemStack : itemStackList) {

            if (itemStack.isEmpty()) {
                continue;
            }

            if (itemStack.getMetadata() == OreDictionary.WILDCARD_VALUE) {

                itemStack.getItem().getSubItems(CreativeTabs.SEARCH, internalList);

            } else {
                internalList.add(itemStack);
            }
        }

        for (ItemStack itemStack : internalList) {
            itemStack.setCount(amount);
        }

        result.addAll(internalList);
        return result;
    }

}
