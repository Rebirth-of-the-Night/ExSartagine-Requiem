package subaraki.exsartagine.gui.common;

import net.minecraftforge.items.ItemStackHandler;
import subaraki.exsartagine.tileentity.KettleBlockEntity;

public class KettleISH extends ItemStackHandler {

    public static final int CONTAINER_SLOT = 19;

    protected final KettleBlockEntity blockEntity;

    public KettleISH(KettleBlockEntity blockEntity, int slots) {
        super(slots);
        this.blockEntity = blockEntity;
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        if (slot == CONTAINER_SLOT) {
            blockEntity.tryOutputFluid();
        }
        blockEntity.markDirty();
    }

    @Override
    public int getSlotLimit(int slot) {
        return slot == CONTAINER_SLOT ? 1 : super.getSlotLimit(slot);
    }
}
