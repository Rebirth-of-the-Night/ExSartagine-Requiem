package subaraki.exsartagine.gui.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import subaraki.exsartagine.gui.common.slot.SlotInput;
import subaraki.exsartagine.gui.common.slot.SlotOutput;
import subaraki.exsartagine.init.RecipeTypes;
import subaraki.exsartagine.recipe.Recipes;
import subaraki.exsartagine.tileentity.TileEntitySmelter;

public class ContainerSmelter extends Container {

    @SuppressWarnings("unused")
    private final TileEntitySmelter smelter;

    public ContainerSmelter(InventoryPlayer playerInventory, TileEntitySmelter smelter) {
        this.smelter = smelter;

        this.addSlotToContainer(new SlotInput<>(smelter.getInventory(), 0, 56, 17, RecipeTypes.SMELTER));
        this.addSlotToContainer(new SlotOutput(playerInventory.player, smelter.getInventory(), 1, 116, 35));
        this.addSlotToContainer(new SlotOutput(playerInventory.player, smelter.getInventory(), 2, 140, 39));

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
        ItemStack bufferStack = ItemStack.EMPTY; //no idea... should have made documentation when i was coding this. TODO
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            bufferStack = slotStack.copy();

            if (index == 1 || index == 2) //output slots
            {
                if (!this.mergeItemStack(slotStack, 3, 39, true)) //merge to player inventory
                {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(slotStack, bufferStack);
            } else if (index != 0)// player inventory
            {
                if (Recipes.hasResult(slotStack, RecipeTypes.SMELTER)) //if the item clicked can be smolten
                {
                    if (!this.mergeItemStack(slotStack, 0, 1, false)) //mergo to input slot
                    {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 3 && index < 30) //if player inventory (no hotbar) is clicked and the item cannot be smolten
                {
                    if (!this.mergeItemStack(slotStack, 30, 39, false)) //merge into player hotbar
                    {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 30 && index < 39 && !this.mergeItemStack(slotStack, 3, 30, false)) //vice versa...
                {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(slotStack, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (slotStack.getCount() == bufferStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, slotStack);
        }

        return bufferStack;
    }
}