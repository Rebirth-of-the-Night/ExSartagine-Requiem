package subaraki.exsartagine.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface Heatable {
    void startHeating(World world, IBlockState state, BlockPos pos);

    void stopHeating(World world, IBlockState state, BlockPos pos);

    Class<?> getTileEntity();
}
