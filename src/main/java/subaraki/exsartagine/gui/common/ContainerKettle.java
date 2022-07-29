package subaraki.exsartagine.gui.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import subaraki.exsartagine.tileentity.TileEntityKettle;

public class ContainerKettle extends Container {

    private final TileEntityKettle pot;

    public ContainerKettle(InventoryPlayer playerInventory, TileEntityKettle pot) {
        this.pot = pot;

        int x1 = 22;
        int y1 = 15;

        this.addSlotToContainer(new SlotItemHandler(pot.handler, 0, x1, 18 + y1));
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                this.addSlotToContainer(new SlotItemHandler(pot.handler, x + 3 * y + 1, x1 + 18 + 18 * x, y1 + 18 * y));
            }
        }

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                this.addSlotToContainer(new SlotOutput(playerInventory.player, pot.handler, x + 3 * y + 10, 116 + 18 * x, y1 + 18 * y));
            }
        }

        this.addSlotToContainer(new SlotItemHandler(pot.handler, 19, 167, 74));

        int x2 = 16;
        int y2 = 100;

        for (int i = 0; i < 3; ++i)
            for (int j = 0; j < 9; ++j)
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9,  x2 + j * 18, y2 + i * 18));

        for (int k = 0; k < 9; ++k)
            this.addSlotToContainer(new Slot(playerInventory, k, x2 + k * 18, y2 + 58));
    }

    public void swapTanks() {
        pot.swapTanks();
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

            if (index == 0) {
                if (!this.mergeItemStack(itemstack1, 19, this.inventorySlots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < 19) {
                if (!this.mergeItemStack(itemstack1, 19, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(itemstack1, itemstack);
            } else {
                if (!this.mergeItemStack(itemstack1, 1, 10, false)) {
                    return ItemStack.EMPTY;
                }
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