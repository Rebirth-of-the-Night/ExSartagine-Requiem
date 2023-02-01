package subaraki.exsartagine.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import subaraki.exsartagine.ExSartagine;

public class HeatableGuiBlock extends KitchenwareBlock {

    protected final int guiID;


    public HeatableGuiBlock(Material materialIn, int guiID) {
        super(materialIn);
        this.guiID = guiID;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        playerIn.openGui(ExSartagine.instance, guiID, worldIn, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }
}
