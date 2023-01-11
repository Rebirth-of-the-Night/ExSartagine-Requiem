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

public abstract class KitchenwareBlock extends Block implements Heatable {


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

        if (world.getTileEntity(pos).getClass().equals(this.getTileEntity())) {
            if (fromPos.up().equals(pos)) { //if the block is beneath us
                IBlockState down = world.getBlockState(fromPos);
                if (!Recipes.isPlaceable(down)) {
                    dropBlockAsItem(world, pos, getDefaultState(), 0);
                    world.setBlockToAir(pos);
                } else
                    setHeating(world, state, pos,Recipes.isHeatSource(down));
            }
        }
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        if (Recipes.isHeatSource(world.getBlockState(pos.down()))) {
            setHeating(world, state, pos,true);
        }
    }

    @Override
    public void setHeating(World world, IBlockState state, BlockPos pos,boolean hot) {
        ((KitchenwareBlockEntity)world.getTileEntity(pos)).setHeated(hot);
        world.notifyBlockUpdate(pos, state, getDefaultState(), 3);
    }
}
