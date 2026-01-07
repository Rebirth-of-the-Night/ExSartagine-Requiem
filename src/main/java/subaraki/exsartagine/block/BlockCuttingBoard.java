package subaraki.exsartagine.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import subaraki.exsartagine.ExSartagine;
import subaraki.exsartagine.Utils;
import subaraki.exsartagine.init.ExSartagineItems;
import subaraki.exsartagine.tileentity.TileEntityCuttingBoard;

import java.util.List;

public class BlockCuttingBoard extends BlockHorizontal implements ITileEntityProvider {

    public static final AxisAlignedBB BOUNDING_BOX_NS = new AxisAlignedBB(0.0625, 0, 0.1875, 0.9375, 0.0625, 0.8125);
    public static final AxisAlignedBB BOUNDING_BOX_EW = new AxisAlignedBB(0.1875, 0, 0.0625, 0.8125, 0.0625, 0.9375);

    public BlockCuttingBoard() {
        super(Material.WOOD);
        setHardness(1.5f);
        setResistance(1.5f);
        setSoundType(SoundType.WOOD);
        setCreativeTab(ExSartagineItems.pots);
        setDefaultState(getDefaultState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (state.getValue(FACING)) {
            case NORTH:
            case SOUTH:
                return BOUNDING_BOX_NS;
            case EAST:
            case WEST:
                return BOUNDING_BOX_EW;
            default:
                throw new IllegalStateException("Invalid cutting board block state: " + state + " (at " + pos + ")");
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    EnumFacing face, float hitX, float hitY, float hitZ) {
        if (face == EnumFacing.UP) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityCuttingBoard) {
                return ((TileEntityCuttingBoard) te).handlePlayerInteraction(player, hand);
            }
        }
        return super.onBlockActivated(world, pos, state, player, hand, face, hitX, hitY, hitZ);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityCuttingBoard) {
            Utils.scatter(world, pos, ((TileEntityCuttingBoard) te).getInventory());
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityCuttingBoard();
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        BlockPos below = pos.down();
        return world.getBlockState(below).isSideSolid(world, below, EnumFacing.UP);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        BlockPos below = pos.down();
        if (!world.getBlockState(below).isSideSolid(world, below, EnumFacing.UP)) {
            dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 0x3));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing face,
                                            float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flags) {
        Minecraft mc = Minecraft.getMinecraft();
        String sneakKey = mc.gameSettings.keyBindSneak.getDisplayName();
        String useKey = mc.gameSettings.keyBindUseItem.getDisplayName();
        tooltip.add(TextFormatting.BLUE + "[" + TextFormatting.WHITE + sneakKey
                + TextFormatting.BLUE + "+" + TextFormatting.WHITE
                + I18n.format(ExSartagine.MODID + ".gui.scroll") + TextFormatting.BLUE + "] "
                + TextFormatting.GRAY + I18n.format(ExSartagine.MODID + ".gui.cutting_board_info_ing"));
        tooltip.add(TextFormatting.BLUE + "[" + TextFormatting.WHITE + useKey + TextFormatting.BLUE + "] "
                + TextFormatting.GRAY + I18n.format(ExSartagine.MODID + ".gui.cutting_board_info_cut"));
        tooltip.add(TextFormatting.BLUE + "[" + TextFormatting.WHITE + sneakKey
                + TextFormatting.BLUE + "+" + TextFormatting.WHITE + useKey + TextFormatting.BLUE + "] "
                + TextFormatting.GRAY + I18n.format(ExSartagine.MODID + ".gui.cutting_board_info_knife"));
    }
}
