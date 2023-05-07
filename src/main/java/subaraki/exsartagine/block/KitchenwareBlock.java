package subaraki.exsartagine.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import subaraki.exsartagine.Utils;
import subaraki.exsartagine.recipe.ModRecipes;
import subaraki.exsartagine.tileentity.WokBlockEntity;
import subaraki.exsartagine.tileentity.util.KitchenwareBlockEntity;

public abstract class KitchenwareBlock extends Block {

    public static final PropertyBool LEGS = PropertyBool.create("legs");

    public KitchenwareBlock(Material materialIn) {
        super(materialIn);
        setDefaultState(this.getDefaultState().withProperty(LEGS,false));
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return ModRecipes.isPlaceable(world.getBlockState(pos.down()));
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof WokBlockEntity) {
            WokBlockEntity te = (WokBlockEntity) tileentity;
            Utils.scatter(worldIn, pos, te.getEntireItemInventory());
            te.clearInput();
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this,LEGS);
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
            if (!ModRecipes.isPlaceable(down)) {
                dropBlockAsItem(world, pos, getDefaultState(), 0);
                world.setBlockToAir(pos);
            }
        }
    }
}
