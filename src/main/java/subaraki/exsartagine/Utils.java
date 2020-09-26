package subaraki.exsartagine;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class Utils {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void scatter(World worldIn, BlockPos pos, ItemStackHandler inventory) {
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
            LOGGER.warn("doesStackMatchOre called with non-existing name. stack: {} name: {}", stack, name);
            return false;
        } else if (stack.isEmpty()) {
            return false;
        } else {
            int needle = OreDictionary.getOreID(name);
            int[] var3 = OreDictionary.getOreIDs(stack);

            return Arrays.stream(var3).anyMatch(id -> id == needle);
        }
    }
}
