package subaraki.exsartagine.gui.common;

import net.minecraftforge.items.ItemStackHandler;
import subaraki.exsartagine.tileentity.KettleBlockEntity;

public class KettleISH extends ItemStackHandler {

    protected final KettleBlockEntity blockEntity;

    public KettleISH(KettleBlockEntity blockEntity, int slots) {
        super(slots);
        this.blockEntity = blockEntity;
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        blockEntity.markDirty();
    }
}
