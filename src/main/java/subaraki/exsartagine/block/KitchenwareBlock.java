package subaraki.exsartagine.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import subaraki.exsartagine.Utils;
import subaraki.exsartagine.recipe.Recipes;
import subaraki.exsartagine.tileentity.util.KitchenwareBlockEntity;

public abstract class KitchenwareBlock extends Block {


    public KitchenwareBlock(Material materialIn) {
        super(materialIn);
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return Recipes.isPlaceable(world.getBlockState(pos.down()));
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof KitchenwareBlockEntity) {
            KitchenwareBlockEntity te = (KitchenwareBlockEntity) tileentity;
            Utils.scatter(worldIn, pos, te.getEntireItemInventory());
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (fromPos.up().equals(pos)) { //if the block is beneath us
            IBlockState down = world.getBlockState(fromPos);
            if (!Recipes.isPlaceable(down)) {
                dropBlockAsItem(world, pos, getDefaultState(), 0);
                world.setBlockToAir(pos);
            }
        }
    }
}
