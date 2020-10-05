package subaraki.exsartagine.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import subaraki.exsartagine.recipe.Recipes;

public abstract class BlockHeatable extends Block implements Heatable {

    public BlockHeatable(Material materialIn) {
        super(materialIn);
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        Block blockDown = world.getBlockState(pos.down()).getBlock();
        return Recipes.isPlaceable(blockDown);
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {

        if (world.getTileEntity(pos).getClass().equals(this.getTileEntity())) {
            if (fromPos.up().equals(pos)) { //if the block is beneath us
                Block down = world.getBlockState(fromPos).getBlock();
                if (!Recipes.isPlaceable(down)) {
                    dropBlockAsItem(world, pos, getDefaultState(), 0);
                    world.setBlockToAir(pos);
                } else if (Recipes.isHeatSource(down)) {
                    startHeating(world, state, pos);
                } else {
                    stopHeating(world, state, pos);
                }
            }
        }
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        if (Recipes.isHeatSource(world.getBlockState(pos.down()).getBlock())) {
            startHeating(world, state, pos);
        }
    }
}
