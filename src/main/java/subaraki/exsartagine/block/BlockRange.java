package subaraki.exsartagine.block;

import com.codetaylor.mc.athenaeum.util.Properties;
import com.codetaylor.mc.pyrotech.library.spi.block.IBlockIgnitableAdjacentIgniterBlock;
import com.codetaylor.mc.pyrotech.library.spi.block.IBlockIgnitableWithIgniterItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;
import subaraki.exsartagine.ExSartagine;
import subaraki.exsartagine.Utils;
import subaraki.exsartagine.init.ExSartagineItems;
import subaraki.exsartagine.init.ModSounds;
import subaraki.exsartagine.tileentity.TileEntityRange;
import subaraki.exsartagine.util.ConfigHandler;
import subaraki.exsartagine.util.Reference;

import java.util.List;
import java.util.Random;

@Optional.Interface(modid = "pyrotech", iface = "com.codetaylor.mc.pyrotech.library.spi.block.IBlockIgnitableWithIgniterItem")
@Optional.Interface(modid = "pyrotech", iface = "com.codetaylor.mc.pyrotech.library.spi.block.IBlockIgnitableAdjacentIgniterBlock")
public class BlockRange extends Block implements IBlockIgnitableWithIgniterItem, IBlockIgnitableAdjacentIgniterBlock {

    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyBool HEATED = PropertyBool.create("heated");

    private final Tier tier;

    public BlockRange(Tier tier) {
        super(Material.IRON);
        this.tier = tier;
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
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

        TileEntity tile = worldIn.getTileEntity(pos);

        if (!(tile instanceof TileEntityRange))
            return false;

        if (((TileEntityRange) tile).handlePlayerInteraction(player, hand, facing, hitX, hitY, hitZ))
            return true;

        player.openGui(ExSartagine.instance, Reference.RANGE, worldIn, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
    }

    @Override
    public int getLightValue(IBlockState state) {
        if (tier == Tier.HEARTH && state.getValue(HEATED)) return 14;
        return super.getLightValue(state);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof TileEntityRange) {
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
            TileEntityRange range = (TileEntityRange) tileentity;
            Utils.scatter(worldIn, pos, range.getInventory());
            Utils.scatter(worldIn, pos, range.getCooktopInventory());
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Optional.Method(modid = "pyrotech")
    @Override
    public void igniteWithIgniterItem(World world, BlockPos pos, IBlockState blockState, EnumFacing facing){
        if (blockState.getValue(Properties.FACING_HORIZONTAL) == facing) {
            TileEntity tile = world.getTileEntity(pos);
            if ((tile instanceof TileEntityRange)) {
                ((TileEntityRange) tile).ignite();
            }
        }
    }

    @Optional.Method(modid = "pyrotech")
    @Override
    public void igniteWithAdjacentIgniterBlock(World world, BlockPos pos, IBlockState blockState, EnumFacing facing) {
        TileEntity tile = world.getTileEntity(pos);
        if ((tile instanceof TileEntityRange)) {
            ((TileEntityRange) tile).ignite();
        }
    }

    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
    }

    @Override
    public void onEntityWalk(World world, BlockPos pos, Entity entity) {
        if (!entity.isImmuneToFire()
                && world.getBlockState(pos).getValue(HEATED)
                && entity instanceof EntityLivingBase
                && !EnchantmentHelper.hasFrostWalkerEnchantment((EntityLivingBase) entity)) {
            entity.attackEntityFrom(DamageSource.HOT_FLOOR, 1.0F);
        }
        super.onEntityWalk(world, pos, entity);
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

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
        return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileEntityRange) {
            TileEntityRange range = (TileEntityRange) te;
            if (range.isFueled()) {
                if (tier == Tier.HEARTH) {
                    vanillaFurnaceParticles(stateIn, worldIn, pos, rand);
                } else {
                    smokeParticles(stateIn, worldIn, pos, rand);
                }
            }
            if (range.getCooktopInventory().isWorking()) {
                worldIn.playSound(null, pos.getX() + 0.5D, pos.getY() + 1D, pos.getZ() + 0.5D, ModSounds.FRYING, SoundCategory.BLOCKS, 0.75f, 1);
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

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flags) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null && tag.hasKey("BlockEntityTag", Constants.NBT.TAG_COMPOUND)) {
            NBTTagCompound blockTag = tag.getCompoundTag("BlockEntityTag");
            if (blockTag.getBoolean("self_igniting_upgrade")) {
                tooltip.add(TextFormatting.RED + "+" + TextFormatting.GRAY + I18n.format(ExSartagine.MODID + ".gui.self_igniting_upgrade"));
            }
            if (blockTag.getBoolean("combustion_chamber_upgrade")) {
                tooltip.add(TextFormatting.RED + "+" + TextFormatting.GRAY + I18n.format(ExSartagine.MODID + ".gui.combustion_chamber_upgrade"));
            }
        }
        super.addInformation(stack, world, tooltip, flags);
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

    public Tier getTier() {
        return tier;
    }

    public enum Tier {
        HEARTH(1, 0.5f, 30) {
            @Override
            public boolean isManualIgnition() {
                return ConfigHandler.hearth_requires_ignition;
            }
        },
        RANGE(3, 1f, 50) {
            @Override
            public boolean isManualIgnition() {
                return ConfigHandler.range_requires_ignition;
            }
        };

        private final int maxExtensions;
        private final float fuelEfficiency;
        private final int cookingSpeed;

        Tier(int maxExtensions, float fuelEfficiency, int cookingSpeed) {
            this.maxExtensions = maxExtensions;
            this.fuelEfficiency = fuelEfficiency;
            this.cookingSpeed = cookingSpeed;
        }

        public int getMaxExtensions() {
            return maxExtensions;
        }

        public float getFuelEfficiency() {
            return fuelEfficiency;
        }

        public int getCookingSpeed() {
            return cookingSpeed;
        }

        public abstract boolean isManualIgnition();
    }
}
