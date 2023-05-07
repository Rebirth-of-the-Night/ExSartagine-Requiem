package subaraki.exsartagine.block;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import subaraki.exsartagine.ExSartagine;
import subaraki.exsartagine.Oredict;
import subaraki.exsartagine.Utils;
import subaraki.exsartagine.init.ExSartagineBlocks;
import subaraki.exsartagine.init.ExSartagineItems;
import subaraki.exsartagine.tileentity.TileEntityRange;
import subaraki.exsartagine.util.Reference;

public class BlockRange extends Block {

    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyBool HEATED = PropertyBool.create("heated");
    private final Supplier<Boolean> manualIgnition;
    private final int maxExtensions;
    private final boolean hearth;

    public BlockRange(Supplier<Boolean> manualIgnition, int maxExtensions, boolean hearth) {
        super(Material.IRON);
        this.manualIgnition = manualIgnition;
        this.maxExtensions = maxExtensions;
        this.hearth = hearth;
        setSoundType(SoundType.METAL);
        setCreativeTab(ExSartagineItems.pots);
        setHarvestLevel("pickaxe", 1);
        setHardness(3.5f);
        setDefaultState(getDefaultState().withProperty(HEATED, false));
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

        TileEntity tile = worldIn.getTileEntity(pos);

        if (!(tile instanceof TileEntityRange))
            return false;


        if (manualIgnition.get()) {

            if (!((TileEntityRange) tile).isSelfIgnitingUpgrade()) {

                ItemStack stack = playerIn.getHeldItem(hand);
                boolean matches = Oredict.checkMatch(Oredict.IGNITER, stack);
                if (matches) {
                    if (!worldIn.isRemote) {
                        ((TileEntityRange) tile).createSparks();
                        stack.damageItem(1, playerIn);
                    }
                    return true;
                }
                matches = Oredict.checkMatch(Oredict.SELF_IGNITER, stack);
                if (matches) {
                    if (!worldIn.isRemote) {
                        ((TileEntityRange) tile).setSelfIgnitingUpgrade(true);
                        stack.shrink(1);
                    }
                    return true;
                }
            }
        }

        playerIn.openGui(ExSartagine.instance, Reference.RANGE, worldIn, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
    }

    @Override
    public int getLightValue(IBlockState state) {
        if (hearth && state.getValue(HEATED)) return 14;
        return super.getLightValue(state);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof TileEntityRange) {
            {
                TileEntityRange entityRange = (TileEntityRange) tileentity;

                ItemStack itemstack = new ItemStack(Item.getItemFromBlock(this));
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound.setTag("BlockEntityTag", entityRange.saveToItemNbt(nbttagcompound1));
                itemstack.setTagCompound(nbttagcompound);

                      /*  if (entityRange.hasCustomName())
                        {
                            itemstack.setStackDisplayName(entityRange.getName());
                            entityRange.setCustomName("");
                        }*/

                spawnAsEntity(worldIn, pos, itemstack);

                worldIn.updateComparatorOutputLevel(pos, state.getBlock());
            }

            TileEntityRange range = (TileEntityRange) tileentity;
            Utils.scatter(worldIn, pos, range.getInventory());
        }
        super.breakBlock(worldIn, pos, state);
    }

    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
    }

    ///////////////TE Stuff//////////////////////
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityRange();
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
        if (worldIn.getTileEntity(pos) instanceof TileEntityRange) {
            if (((TileEntityRange) worldIn.getTileEntity(pos)).isFueled()) {
                if (hearth) {
                    vanillaFurnaceParticles(stateIn, worldIn, pos, rand);
                } else {
                    smokeParticles(stateIn, worldIn, pos, rand);
                }
            }
        }
    }

    public static void vanillaFurnaceParticles(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        EnumFacing enumfacing = stateIn.getValue(FACING);
        double x0 = pos.getX() + 0.5D;
        double y0 = pos.getY() + rand.nextDouble() * 6 / 16d + 1/16d;
        double z0 = pos.getZ() + 0.5D;
        double d3 = 0.52D;
        double d4 = rand.nextDouble() * 0.6D - 0.3D;

        if (rand.nextDouble() < 0.1D) {
            worldIn.playSound(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
        }

        switch (enumfacing) {
            case WEST:
                worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x0 - d3, y0, z0 + d4, 0, 0, 0);
                worldIn.spawnParticle(EnumParticleTypes.FLAME, x0 - d3, y0, z0 + d4, 0, 0, 0);
                break;
            case EAST:
                worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x0 + d3, y0, z0 + d4, 0, 0, 0);
                worldIn.spawnParticle(EnumParticleTypes.FLAME, x0 + d3, y0, z0 + d4, 0, 0, 0);
                break;
            case NORTH:
                worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x0 + d4, y0, z0 - d3, 0, 0, 0);
                worldIn.spawnParticle(EnumParticleTypes.FLAME, x0 + d4, y0, z0 - d3, 0, 0, 0);
                break;
            case SOUTH:
                worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x0 + d4, y0, z0 + d3, 0, 0, 0);
                worldIn.spawnParticle(EnumParticleTypes.FLAME, x0 + d4, y0, z0 + d3, 0, 0, 0);
        }
    }

    public static void smokeParticles(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        EnumFacing enumfacing = stateIn.getValue(FACING);
        double x0 = pos.getX() + 0.5D;
        double y0 = pos.getY() + rand.nextDouble() * 2/16d + 10/16d;
        double z0 = pos.getZ() + 0.5D;
        double d3 = 0.52D;
        double d4 = rand.nextDouble() * 4/16d - 2/16d;

        switch (enumfacing) {
            case WEST:
                worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x0 + d3, y0, z0 + d4, 0, 0, 0);
                break;
            case EAST:
                worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x0 - d3, y0, z0 + d4, 0, 0, 0);
                break;
            case NORTH:
                worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x0 + d4, y0, z0 + d3, 0, 0, 0);
                break;
            case SOUTH:
                worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x0 + d4, y0, z0 - d3, 0, 0, 0);
        }
    }

    /////// TURNING STUFF ////////////////

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, HEATED);
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex() | (state.getValue(HEATED) ? 0b100 : 0);
    }

    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.byHorizontalIndex(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH;
        }

        boolean heated = (meta & 0b0100) != 0;

        return this.getDefaultState().withProperty(FACING, enumfacing).withProperty(HEATED, heated);
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
    }

    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    public int getMaxExtensions() {
        return maxExtensions;
    }

    public Supplier<Boolean> isManualIgnition() {
        return manualIgnition;
    }
}
