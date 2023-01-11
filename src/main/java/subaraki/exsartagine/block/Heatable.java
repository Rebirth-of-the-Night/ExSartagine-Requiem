package subaraki.exsartagine.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface Heatable {
    void setHeating(World world, IBlockState state, BlockPos pos,boolean hot);

    Class<?> getTileEntity();
}
