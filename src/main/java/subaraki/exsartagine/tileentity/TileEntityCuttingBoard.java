package subaraki.exsartagine.tileentity;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.WorldServer;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import subaraki.exsartagine.Oredict;
import subaraki.exsartagine.init.RecipeTypes;
import subaraki.exsartagine.recipe.CuttingBoardRecipe;
import subaraki.exsartagine.recipe.ModRecipes;
import subaraki.exsartagine.tileentity.util.HeldItemTransferable;
import subaraki.exsartagine.tileentity.util.RecipeHandler;
import subaraki.exsartagine.tileentity.util.RecipeHost;
import subaraki.exsartagine.util.Helpers;

public class TileEntityCuttingBoard extends TileEntity implements HeldItemTransferable, RecipeHost<CuttingBoardRecipe> {

    public static final int INPUT = 0;
    public static final int KNIFE = 1;

    private final RecipeHandler<CuttingBoardRecipe> recipeHandler = new RecipeHandler<>(this);
    private final CuttingBoardInventory inventory = new CuttingBoardInventory();

    private EntityPlayer currentUser;
    private ItemStack currentUserKnife = ItemStack.EMPTY;

    public IItemHandler getInventory() {
        return inventory;
    }

    @Override
    public boolean isRemote() {
        return world.isRemote;
    }

    @Override
    public int getRecipeTimeDecay() {
        return 0;
    }

    public boolean handlePlayerInteraction(EntityPlayer player, EnumHand hand) {
        if (player.isSneaking()) {
            if (Helpers.handleHeldItemInteraction(player, hand, inventory, KNIFE)) {
                if (!inventory.getStackInSlot(KNIFE).isEmpty()) {
                    world.playSound(player, pos, SoundEvents.BLOCK_WOOD_BREAK, SoundCategory.BLOCKS, 0.8f, 0.7f + 0.1f * world.rand.nextFloat());
                }
                return true;
            }
            return false;
        }

        ItemStack held = player.getHeldItem(hand);
        if (inventory.getStackInSlot(INPUT).isEmpty()) {
            if (!held.isEmpty()) {
                inventory.setStackInSlot(INPUT, held);
                player.setHeldItem(hand, ItemStack.EMPTY);
                return true;
            }
            return false;
        } else if (held.isEmpty()) {
            ItemStack stack = inventory.extractItem(INPUT, Integer.MAX_VALUE, false);
            if (!stack.isEmpty()) {
                player.setHeldItem(hand, stack);
            }
            return true;
        } else {
            currentUser = player;
            currentUserKnife = held;
            if (recipeHandler.tick()) {
                markDirty();
            }
            return recipeHandler.isWorking();
        }
    }

    @Override
    public int getTransferFromHeldItemZone(EntityPlayer player, EnumHand hand, EnumFacing face, float hitX, float hitY, float hitZ) {
        return face == EnumFacing.UP ? 0 : -1;
    }

    @Override
    public boolean transferFromHeldItem(EntityPlayer player, EnumHand hand, boolean insert, int zone) {
        return Helpers.transferHeldItemToHandler(player, hand, inventory, INPUT, insert);
    }

    @Nullable
    @Override
    public CuttingBoardRecipe findRecipe() {
        return ModRecipes.findRecipe(inventory, CuttingBoardRecipe.class, RecipeTypes.CUTTING_BOARD);
    }

    @Override
    public boolean doesRecipeMatch(CuttingBoardRecipe recipe) {
        return recipe.itemMatch(inventory);
    }

    @Override
    public boolean canDoWork(CuttingBoardRecipe recipe) {
        return recipe.getKnife().test(currentUserKnife);
    }

    @Override
    public boolean canFitOutputs(CuttingBoardRecipe recipe) {
        return true;
    }

    @Override
    public void onWorkTick(CuttingBoardRecipe recipe, int work) {
        Helpers.damageOrConsumeItem(currentUser, currentUserKnife);
        world.playSound(null, pos, SoundEvents.BLOCK_WOOD_BREAK, SoundCategory.BLOCKS, 0.75f, 0.9f + 0.2f * world.rand.nextFloat());

        if (!(world instanceof WorldServer)) { // shouldn't happen, but better safe than sorry
            return;
        }
        WorldServer ws = (WorldServer) world;
        int ingId = Item.getIdFromItem(inventory.getStackInSlot(INPUT).getItem());
        double bx = pos.getX() + 0.5, by = pos.getY() + 0.2, bz = pos.getZ() + 0.5;
        for (int i = 2 + ws.rand.nextInt(2); i > 0; i--) {
            float ay = ws.rand.nextFloat() * 6.28f, ap = 0.3f + ws.rand.nextFloat();
            float gr = 0.2f * MathHelper.cos(ap);
            ws.spawnParticle(EnumParticleTypes.ITEM_CRACK, false, bx, by, bz, 2 + ws.rand.nextInt(2),
                    gr * MathHelper.cos(ay), 0.2f * MathHelper.sin(ap), gr * MathHelper.sin(ay), 0.05, ingId);
        }
    }

    @Override
    public void processRecipe(CuttingBoardRecipe recipe) {
        // produce output
        EntityItem item = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5, recipe.getResult(inventory));
        EnumFacing outFace = world.getBlockState(pos).getValue(BlockHorizontal.FACING).rotateYCCW();
        BlockPos adj = pos.offset(outFace);
        if (!world.getBlockState(adj).isSideSolid(world, adj, outFace.getOpposite())) {
            Vec3i vec = outFace.getDirectionVec();
            item.setVelocity(vec.getX() * 0.125, 0.08, vec.getZ() * 0.125);
            item.setPickupDelay(24);
        } else {
            item.setVelocity(-0.03 + world.rand.nextDouble() * 0.06, 0.08, -0.03 + world.rand.nextDouble() * 0.06);
            item.setPickupDelay(12);
        }
        world.spawnEntity(item);

        // consume inputs
        inventory.extractItem(INPUT, 1, false);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 0, writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(super.getUpdateTag());
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        recipeHandler.readFromNBT(tag);
        inventory.deserializeNBT(tag.getCompoundTag("inv"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        recipeHandler.writeToNBT(tag);
        tag.setTag("inv", inventory.serializeNBT());
        return tag;
    }

    private class CuttingBoardInventory extends ItemStackHandler {
        private CuttingBoardInventory() {
            super(2);
        }

        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }

        @Override
        public int getSlotLimit(int slot) {
            return slot == KNIFE ? 1 : 64;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return slot != KNIFE || Oredict.checkMatch(Oredict.KNIFE, stack);
        }
    }
}
