package subaraki.exsartagine.util;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

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

    public static IItemHandlerModifiable copyInventory(IItemHandler original, int fromIncl, int toExcl) {
        ItemStackHandler copy = new ItemStackHandler(toExcl - fromIncl);
        int limit = Math.min(copy.getSlots(), original.getSlots() - fromIncl);
        for (int i = 0; i < limit; i++) {
            ItemStack stack = original.getStackInSlot(fromIncl + i);
            if (!stack.isEmpty()) {
                copy.setStackInSlot(i, stack.copy());
            }
        }
        return copy;
    }

    public static IItemHandlerModifiable copyInventory(IItemHandler original) {
        return copyInventory(original, 0, original.getSlots());
    }

    public static boolean bernoulli(Random rand, int odds) {
        return odds >= 0 && (odds >= 100 || rand.nextInt(100) < odds);
    }

    public static String formatTime(int ticks) {
        if (ticks == 0) {
            return "0:00";
        }
        int seconds = (ticks + 19) / 20;
        if (seconds < 60) {
            return String.format("0:%02d", seconds);
        }
        int minutes = seconds / 60;
        if (minutes < 60) {
            return String.format("%d:%02d", minutes, seconds % 60);
        }
        return String.format("%d:%02d:%02d", minutes / 60, minutes % 60, seconds % 60);
    }

    public static boolean areItemStackListsEqual(List<ItemStack> a, List<ItemStack> b) {
        if (a.size() != b.size()) {
            return false;
        }
        Iterator<ItemStack> iterA = a.iterator(), iterB = b.iterator();
        while (iterA.hasNext()) {
            if (!ItemStack.areItemStacksEqual(iterA.next(), iterB.next())) {
                return false;
            }
        }
        return true;
    }

    public static boolean handleHeldItemInteraction(EntityPlayer player, EnumHand hand, IItemHandler inv, int slot) {
        ItemStack held = player.getHeldItem(hand);
        ItemStack inSlot = inv.getStackInSlot(slot);
        if (inSlot.isEmpty()) {
            if (!held.isEmpty()) {
                ItemStack rem = inv.insertItem(slot, held, false);
                if (rem.getCount() < held.getCount()) {
                    player.setHeldItem(hand, rem);
                    return true;
                }
            }
        } else if (held.isEmpty()) {
            ItemStack ext = inv.extractItem(slot, Integer.MAX_VALUE, false);
            if (!ext.isEmpty()) {
                player.setHeldItem(hand, ext);
                return true;
            }
        } else {
            int amount = held.getMaxStackSize() - held.getCount();
            if (amount > 0 && ItemHandlerHelper.canItemStacksStack(held, inv.extractItem(slot, amount, true))) {
                ItemStack ext = inv.extractItem(slot, amount, false);
                if (!ext.isEmpty()) {
                    held.grow(ext.getCount());
                    return true;
                }
            }
        }
        return false;
    }

    public static void damageOrConsumeItem(EntityLivingBase user, ItemStack stack) {
        if (stack.isItemStackDamageable()) {
            stack.damageItem(1, user);
        } else {
            stack.shrink(1);
        }
    }

    public static boolean transferHeldItemToHandler(EntityLivingBase player, EnumHand hand, IItemHandler inv, int slot, boolean insert) {
        ItemStack held = player.getHeldItem(hand);
        if (insert) {
            if (!held.isEmpty() && inv.insertItem(slot, ItemHandlerHelper.copyStackWithSize(held, 1), false).isEmpty()) {
                held.shrink(1);
                return true;
            }
        } else if (held.isEmpty()) {
            player.setHeldItem(hand, inv.extractItem(slot, 1, false));
            return true;
        } else if (held.getCount() < held.getMaxStackSize()) {
            ItemStack stack = inv.extractItem(slot, 1, true);
            if (!stack.isEmpty() && ItemHandlerHelper.canItemStacksStack(held, stack)) {
                ItemStack ext = inv.extractItem(slot, 1, false);
                if (!ext.isEmpty()) {
                    held.grow(ext.getCount());
                    return true;
                }
            }
        }
        return false;
    }
}
