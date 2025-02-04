package subaraki.exsartagine.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.Nullable;
import subaraki.exsartagine.ExSartagine;
import subaraki.exsartagine.Utils;
import subaraki.exsartagine.recipe.ModRecipes;
import subaraki.exsartagine.tileentity.util.KitchenwareBlockEntity;
import subaraki.exsartagine.util.Helpers;

import java.util.List;

public abstract class KitchenwareBlock extends Block {

    public static final PropertyBool LEGS = PropertyBool.create("legs");
    public static final PropertyBool DIRTY = PropertyBool.create("dirty");

    public KitchenwareBlock(Material materialIn) {
        super(materialIn);
        setDefaultState(this.getDefaultState().withProperty(LEGS,false));
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return ModRecipes.isPlaceable(world.getBlockState(pos.down()).getActualState(world, pos.down()));
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.hasKey("dirtytime", Constants.NBT.TAG_INT)) {
            return;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof KitchenwareBlockEntity)) {
            return;
        }
        ((KitchenwareBlockEntity<?>) tile).setSoiledTime(tag.getInteger("dirtytime"));
    }

    @Override
    public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof KitchenwareBlockEntity && ((KitchenwareBlockEntity<?>) tile).getSoiledTime() > 0) {
            return;
        }
        super.dropBlockAsItemWithChance(world, pos, state, chance, fortune);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof KitchenwareBlockEntity) {
            KitchenwareBlockEntity<?> te = (KitchenwareBlockEntity<?>) tile;
            Utils.scatter(world, pos, te.getEntireItemInventory());
            int dirtyTime = te.getSoiledTime();
            if (dirtyTime > 0) {
                ItemStack stack = new ItemStack(this);
                NBTTagCompound tag = new NBTTagCompound();
                tag.setInteger("dirtytime", dirtyTime);
                stack.setTagCompound(tag);
                InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
            }
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flags) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.hasKey("dirtytime", Constants.NBT.TAG_INT)) {
            return;
        }
        tooltip.add(TextFormatting.RED + I18n.format(ExSartagine.MODID + ".gui.dirty"));
        tooltip.add(TextFormatting.GRAY + "(" + Helpers.formatTime(tag.getInteger("dirtytime")) + ")");
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, LEGS, DIRTY);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof KitchenwareBlockEntity)) {
            return state;
        }
        return state.withProperty(DIRTY, ((KitchenwareBlockEntity<?>) tile).isSoiled());
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    public static final int LEGS_BIT = 0b1000;

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(LEGS) ? LEGS_BIT : 0;
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState state = worldIn.getBlockState(pos.down());
        boolean legs = ModRecipes.hasLegs(state);
        return this.getDefaultState().withProperty(LEGS,legs);
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
