package subaraki.exsartagine.gui.common;

import net.minecraftforge.fluids.FluidTank;
import subaraki.exsartagine.tileentity.KettleBlockEntity;

public class KettleFSH extends FluidTank {
    private final KettleBlockEntity kettleBlockEntity;

    public KettleFSH(KettleBlockEntity kettleBlockEntity, int capacity) {
        super(capacity);
        this.kettleBlockEntity = kettleBlockEntity;
    }

    @Override
    protected void onContentsChanged() {
        super.onContentsChanged();
        kettleBlockEntity.markDirty();
    }
}
