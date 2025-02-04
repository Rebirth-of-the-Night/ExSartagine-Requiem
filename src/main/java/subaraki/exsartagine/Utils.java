package subaraki.exsartagine;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class Utils {

    public static void scatter(World worldIn, BlockPos pos, IItemHandler inventory) {
        dropInventoryItems(worldIn, pos, inventory);
    }

    public static void dropInventoryItems(World worldIn, BlockPos pos, IItemHandler inventory) {
        dropInventoryItems(worldIn, pos.getX(), pos.getY(), pos.getZ(), inventory);
    }

    public static void dropInventoryItems(World worldIn, Entity entityAt, IItemHandler inventory) {
        dropInventoryItems(worldIn, entityAt.posX, entityAt.posY, entityAt.posZ, inventory);
    }

    private static void dropInventoryItems(World worldIn, double x, double y, double z, IItemHandler inventory) {
        for (int i = 0; i < inventory.getSlots(); ++i) {
            ItemStack itemstack = inventory.getStackInSlot(i);

            if (!itemstack.isEmpty()) {
                InventoryHelper.spawnItemStack(worldIn, x, y, z, itemstack);
            }
        }
    }

    public static boolean doesStackMatchOre(@Nonnull ItemStack stack, String name) {
        if (!OreDictionary.doesOreNameExist(name)) {
            ExSartagine.instance.getLogger().warn("doesStackMatchOre called with non-existing name. stack: {} name: {}", stack, name);
            return false;
        } else if (stack.isEmpty()) {
            return false;
        } else {
            int needle = OreDictionary.getOreID(name);
            int[] var3 = OreDictionary.getOreIDs(stack);

            return Arrays.stream(var3).anyMatch(id -> id == needle);
        }
    }

    /**
     * Default implementation of IRecipe.getRemainingItems {getRemainingItems} because
     * this is just copy pasted over a lot of recipes.
     *
     * @param inv Crafting inventory
     * @return Crafting inventory contents after the recipe.
     */
    public static NonNullList<ItemStack> defaultRecipeGetRemainingItems(IItemHandler inv) {
        NonNullList<ItemStack> ret = NonNullList.withSize(inv.getSlots(), ItemStack.EMPTY);
        for (int i = 0; i < ret.size(); i++)
        {
            ret.set(i, ForgeHooks.getContainerItem(inv.getStackInSlot(i)));
        }
        return ret;
    }

}
