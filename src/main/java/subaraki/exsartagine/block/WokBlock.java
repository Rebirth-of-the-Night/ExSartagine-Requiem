package subaraki.exsartagine.block;

import java.util.Random;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.oredict.OreDictionary;
import subaraki.exsartagine.Oredict;
import subaraki.exsartagine.Utils;
import subaraki.exsartagine.init.ExSartagineItems;
import subaraki.exsartagine.init.ModSounds;
import subaraki.exsartagine.recipe.ModRecipes;
import subaraki.exsartagine.tileentity.TileEntityKettle;
import subaraki.exsartagine.tileentity.WokBlockEntity;
import subaraki.exsartagine.tileentity.util.KitchenwareBlockEntity;

public class WokBlock extends KitchenwareBlock {

    protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D);
    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    public WokBlock() {
        super(Material.IRON);
        setSoundType(SoundType.METAL);
        setCreativeTab(ExSartagineItems.pots);
        setHarvestLevel("pickaxe", 1);
        setHardness(3.5f);
    }

    /////////////////rendering//////////////
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABB;
    }

    ///////////////TE Stuff//////////////////////

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new WokBlockEntity();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = playerIn.getHeldItem(hand);
        TileEntity tileEntity = worldIn.getTileEntity(pos);

        if (tileEntity instanceof WokBlockEntity) {
            WokBlockEntity wokBlockEntity = (WokBlockEntity) tileEntity;
            if (!worldIn.isRemote) {

                if (!stack.isEmpty()) {
                    if (Oredict.checkMatch(Oredict.SPATULA, stack)) {
                        wokBlockEntity.flip(playerIn, stack);
                    } else {

                        FluidActionResult fluidActionResult = FluidUtil.tryEmptyContainerAndStow(stack, wokBlockEntity.getFluidInventoryInput(),
                                new InvWrapper(playerIn.inventory), Integer.MAX_VALUE, playerIn, true);
                        if (fluidActionResult.isSuccess()) {
                            playerIn.setHeldItem(hand, fluidActionResult.getResult());
                        } else if (ModRecipes.validItems.contains(stack.getItem())){
                            ItemStack single = stack.copy();
                            single.setCount(1);
                            ItemStack returns = wokBlockEntity.addSingleItem(single);
                            if (returns.isEmpty() && !playerIn.capabilities.isCreativeMode) {
                                stack.shrink(1);
                            }
                        }
                    }
                } else {
                    wokBlockEntity.giveItems(playerIn);
                }
            }
        }
        return true;
    }

    //this is called when attempting to break the block on both sides
    @Override
    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
        if (!worldIn.isRemote && playerIn.isSneaking()) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof KitchenwareBlockEntity) {
                KitchenwareBlockEntity te = (KitchenwareBlockEntity) tileentity;
                Utils.scatter(worldIn, pos, te.getEntireItemInventory());
                te.markDirty();
            }
        }
    }

/////////////// MISC //////////////////////

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

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        double d0 = (double) pos.getX() + 0.5D;
        double d1 = (double) pos.getY() + 0.15D;
        double d2 = (double) pos.getZ() + 0.5D;

        TileEntity tileEntity = worldIn.getTileEntity(pos);

        if (tileEntity instanceof WokBlockEntity) {
			WokBlockEntity wokBlockEntity = (WokBlockEntity) tileEntity;

            if (wokBlockEntity.isCooking()) {
                worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + (RANDOM.nextDouble() / 1.5 - 0.35), d1, d2 + (RANDOM.nextDouble() / 1.5 - 0.35), 0.0D, 0.0D, 0.0D);
                worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + (RANDOM.nextDouble() / 1.5 - 0.35), d1, d2 + (RANDOM.nextDouble() / 1.5 - 0.35), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    /////// TURNING STUFF ////////////////

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING,LEGS);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex() + super.getMetaFromState(state);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.byHorizontalIndex(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH;
        }

        boolean legs = (meta & LEGS_BIT) != 0;

        return this.getDefaultState().withProperty(FACING, enumfacing).withProperty(LEGS,legs);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }
}
