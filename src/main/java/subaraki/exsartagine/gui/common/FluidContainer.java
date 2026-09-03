package subaraki.exsartagine.gui.common;

import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;

public interface FluidContainer {
    @Nullable
    IFluidHandler getFluidContainerTank(int tankIndex);
}
