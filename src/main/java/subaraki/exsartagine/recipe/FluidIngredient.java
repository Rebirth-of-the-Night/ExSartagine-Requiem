package subaraki.exsartagine.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FluidIngredient extends Ingredient {

    protected FluidStack fluid;

    public ItemStack[] cachedStacks;

    public FluidIngredient(FluidStack fluidStack) {
        this.fluid = fluidStack;
    }

    @Nonnull
    @Override
    public ItemStack[] getMatchingStacks() {
        if (cachedStacks == null) {
            List<ItemStack> stacks = new ArrayList<>();
                stacks.add(FluidUtil.getFilledBucket(new FluidStack(fluid,1000)));
            cachedStacks = stacks.toArray(new ItemStack[0]);
        }
        return this.cachedStacks;
    }

    @Override
    public boolean apply(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        } else {
            FluidStack fs = FluidUtil.getFluidContained(stack);
            return fs != null && fs.containsFluid(fluid);
        }
    }

}
