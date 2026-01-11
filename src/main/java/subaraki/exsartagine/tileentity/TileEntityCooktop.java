package subaraki.exsartagine.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import subaraki.exsartagine.ExSartagine;
import subaraki.exsartagine.block.BlockRange;
import subaraki.exsartagine.block.KitchenwareBlock;
import subaraki.exsartagine.init.RecipeTypes;
import subaraki.exsartagine.recipe.CooktopRecipe;
import subaraki.exsartagine.recipe.ModRecipes;
import subaraki.exsartagine.tileentity.util.HeldItemTransferable;
import subaraki.exsartagine.tileentity.util.RecipeHandler;
import subaraki.exsartagine.tileentity.util.RecipeHost;
import subaraki.exsartagine.util.Helpers;

public abstract class TileEntityCooktop extends TileEntity implements HeldItemTransferable {
    public static int getHitSlot(float hitX, float hitZ) {
        return hitZ < 0.5f ? (hitX < 0.5f ? 0 : 1) : (hitX < 0.5f ? 2 : 3);
    }

    private final CooktopInventory cooktopInventory = new CooktopInventory(this);

    public CooktopInventory getCooktopInventory() {
        return cooktopInventory;
    }

    public abstract BlockRange.Tier getEffectiveTier();

    public boolean handlePlayerCooktopInteraction(EntityPlayer player, EnumHand hand, float hitX, float hitZ) {
        return Helpers.handleHeldItemInteraction(player, hand, cooktopInventory, getHitSlot(hitX, hitZ));
    }

    @Override
    public int getTransferFromHeldItemZone(EntityPlayer player, EnumHand hand, EnumFacing face, float hitX, float hitY, float hitZ) {
        return face == EnumFacing.UP ? getHitSlot(hitX, hitZ) : -1;
    }

    @Override
    public boolean transferFromHeldItem(EntityPlayer player, EnumHand hand, boolean insert, int zone) {
        if (zone < 0 || zone >= cooktopInventory.slots.length) {
            return false;
        }
        return Helpers.transferHeldItemToHandler(player, hand, cooktopInventory, zone, insert);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag) {
        super.readFromNBT(tag);
        cooktopInventory.readFromNbt(tag.getTagList("cooktop", Constants.NBT.TAG_COMPOUND));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag("cooktop", cooktopInventory.writeToNbt());
        return tag;
    }

    public static class CooktopInventory implements IItemHandler {
        private final TileEntityCooktop cooktop;
        private final Slot[] slots = new Slot[4];

        private CooktopInventory(TileEntityCooktop cooktop) {
            this.cooktop = cooktop;
            for (int i = 0; i < slots.length; i++) {
                slots[i] = new Slot();
            }
        }

        public boolean isNonEmpty() {
            for (Slot slot : slots) {
                if (!slot.stackInSlot.isEmpty()) {
                    return true;
                }
            }
            return false;
        }

        public boolean isWorking() {
            for (Slot slot : slots) {
                if (slot.isWorking()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int getSlots() {
            return slots.length;
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return slots[slot].stackInSlot;
        }

        @NotNull
        @Override
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            World world = cooktop.getWorld();
            BlockPos above = cooktop.getPos().up();
            IBlockState state = world.getBlockState(above);
            if (state.getBlock() instanceof KitchenwareBlock || state.isSideSolid(world, above, EnumFacing.DOWN)) {
                return stack;
            }
            return slots[slot].insertItem(stack, simulate);
        }

        @NotNull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return slots[slot].extractItem(amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        protected void tick() {
            for (Slot slot : slots) {
                slot.tick();
            }
        }

        private void readFromNbt(NBTTagList tag) {
            if (tag.tagCount() != slots.length) {
                ExSartagine.instance.getLogger().warn("Serialized slot count {} differs from actual count {}!", tag.tagCount(), slots.length);
                ExSartagine.instance.getLogger().warn("At range block in dimension {} at {}", cooktop.getWorld().provider.getDimension(), cooktop.getPos());
                return;
            }
            for (int i = 0; i < slots.length; i++) {
                slots[i].readFromNbt(tag.getCompoundTagAt(i));
            }
        }

        private NBTTagList writeToNbt() {
            NBTTagList tag = new NBTTagList();
            for (Slot slot : slots) {
                tag.appendTag(slot.writeToNbt());
            }
            return tag;
        }

        private class Slot implements RecipeHost<CooktopRecipe> {
            private final RecipeHandler<CooktopRecipe> recipeHandler = new RecipeHandler<>(this);
            private ItemStack stackInSlot = ItemStack.EMPTY;

            ItemStack insertItem(@NotNull ItemStack stack, boolean simulate) { // assumes max insertion stack size is 1!
                if (stackInSlot.isEmpty() && !stack.isEmpty()) {
                    if (!simulate) {
                        stackInSlot = ItemHandlerHelper.copyStackWithSize(stack, 1);
                        cooktop.markDirty();
                    }
                    return ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - 1);
                }
                return stack;
            }

            ItemStack extractItem(int amount, boolean simulate) {
                if (amount <= 0 || stackInSlot.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                if (amount >= stackInSlot.getCount()) {
                    if (simulate) {
                        return stackInSlot.copy();
                    } else {
                        ItemStack stack = stackInSlot;
                        stackInSlot = ItemStack.EMPTY;
                        cooktop.markDirty();
                        return stack;
                    }
                } else if (simulate) {
                    return ItemHandlerHelper.copyStackWithSize(stackInSlot, amount);
                } else {
                    ItemStack stack = stackInSlot.splitStack(amount);
                    cooktop.markDirty();
                    return stack;
                }
            }

            void tick() {
                if (recipeHandler.tick()) {
                    cooktop.markDirty();
                }
            }

            boolean isWorking() {
                return recipeHandler.isWorking();
            }

            @Override
            public boolean isRemote() {
                return cooktop.getWorld().isRemote;
            }

            @Override
            public int getRecipeTimeScale() {
                return 100;
            }

            @Override
            public int getRecipeTimeIncrement() {
                return cooktop.getEffectiveTier().getCookingSpeed();
            }

            @Nullable
            @Override
            public CooktopRecipe findRecipe() {
                for (CooktopRecipe recipe : ModRecipes.getRecipes(RecipeTypes.COOKTOP)) {
                    if (recipe.itemMatch(stackInSlot)) {
                        return recipe;
                    }
                }
                return null;
            }

            @Override
            public boolean doesRecipeMatch(CooktopRecipe recipe) {
                return recipe.itemMatch(stackInSlot);
            }

            @Override
            public boolean canDoWork(CooktopRecipe recipe) {
                // can be relevant if a recipe outputs more than one of an item that has a second-order product
                return stackInSlot.getCount() == 1;
            }

            @Override
            public boolean canFitOutputs(CooktopRecipe recipe) {
                return true;
            }

            @Override
            public void processRecipe(CooktopRecipe recipe) {
                stackInSlot = recipe.getDisplay().copy();
                cooktop.markDirty();
            }

            void readFromNbt(NBTTagCompound tag) {
                stackInSlot = tag.hasKey("stack", Constants.NBT.TAG_COMPOUND) ? new ItemStack(tag.getCompoundTag("stack")) : ItemStack.EMPTY;
                recipeHandler.readFromNBT(tag);
            }

            NBTTagCompound writeToNbt() {
                NBTTagCompound tag = new NBTTagCompound();
                if (!stackInSlot.isEmpty()) {
                    tag.setTag("stack", stackInSlot.serializeNBT());
                }
                recipeHandler.writeToNBT(tag);
                return tag;
            }
        }
    }
}
