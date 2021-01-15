package subaraki.exsartagine.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import subaraki.exsartagine.ExSartagine;
import subaraki.exsartagine.Utils;
import subaraki.exsartagine.recipe.Recipes;
import subaraki.exsartagine.tileentity.Cooker;
import subaraki.exsartagine.tileentity.TileEntityCooker;
import subaraki.exsartagine.tileentity.TileEntityPot;

public abstract class BlockHeatable extends Block implements Heatable {

    protected final int guiID;

    public BlockHeatable(Material materialIn, int guiID) {
        super(materialIn);
        this.guiID = guiID;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        playerIn.openGui(ExSartagine.instance, guiID, worldIn, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        Block blockDown = world.getBlockState(pos.down()).getBlock();
        return Recipes.isPlaceable(blockDown);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof Cooker) {
            Cooker te = (Cooker)tileentity;
            if(te.getInventory() instanceof ItemStackHandler)
            {
                ItemStackHandler inventory = (ItemStackHandler) te.getInventory();
                Utils.scatter(worldIn, pos, inventory);
            }
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

    @Override
    public void startHeating(World world, IBlockState state, BlockPos pos) {
        ((Cooker)world.getTileEntity(pos)).setCooking();
        world.notifyBlockUpdate(pos, state, getDefaultState(), 3);
    }

    @Override
    public void stopHeating(World world, IBlockState state, BlockPos pos) {
        ((Cooker)world.getTileEntity(pos)).stopCooking();
        world.notifyBlockUpdate(pos, state, getDefaultState(), 3);
    }
}
