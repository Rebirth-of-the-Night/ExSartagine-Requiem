package subaraki.exsartagine.gui.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import subaraki.exsartagine.gui.common.slot.SlotInput;
import subaraki.exsartagine.gui.common.slot.SlotOutput;
import subaraki.exsartagine.recipe.ModRecipes;
import subaraki.exsartagine.tileentity.TileEntityPot;

public class ContainerPot extends Container {

    private final TileEntityPot pot;

    public ContainerPot(InventoryPlayer playerInventory, TileEntityPot pot) {
        this.pot = pot;
        this.addSlotToContainer(new SlotInput<>(pot.getInventory(), 0, 56, 17, pot.getRecipeType()));
        this.addSlotToContainer(new SlotOutput(playerInventory.player, pot.getInventory(), 1, 116, 35));

        for (int i = 0; i < 3; ++i)
            for (int j = 0; j < 9; ++j)
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

        for (int k = 0; k < 9; ++k)
            this.addSlotToContainer(new Slot(playerInventory, k, 8 + k * 18, 142));
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    /**
     * Take a stack from the specified inventory slot.
     */
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index == 1) {
                if (!this.mergeItemStack(itemstack1, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            } else if (index != 0) {
                if (ModRecipes.hasResult(itemstack, pot.getRecipeType())) {
                    if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 29) {
                    if (!this.mergeItemStack(itemstack1, 29, 38, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 38 && !this.mergeItemStack(itemstack1, 2, 29, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 2, 38, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }
}
