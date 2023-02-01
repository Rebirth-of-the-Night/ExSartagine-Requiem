package subaraki.exsartagine.recipe;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

public interface CustomFluidRecipe<T extends IItemHandler,F extends IFluidHandler> extends CustomRecipe<T> {

    boolean fluidMatch(F handler);
    FluidStack getInputFluid();
    default FluidStack getOutputFluid() {
        return null;
    }

    default boolean match(T itemhandler,F fluidHandler) {
        return itemMatch(itemhandler) && fluidMatch(fluidHandler);
    }
}
