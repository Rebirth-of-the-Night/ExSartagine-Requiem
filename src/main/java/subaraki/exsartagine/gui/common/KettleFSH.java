package subaraki.exsartagine.gui.common;

import net.minecraftforge.fluids.FluidTank;
import subaraki.exsartagine.tileentity.TileEntityKettle;

public class KettleFSH extends FluidTank {
    private final TileEntityKettle kettleBlockEntity;

    public KettleFSH(TileEntityKettle kettleBlockEntity, int capacity) {
        super(capacity);
        this.kettleBlockEntity = kettleBlockEntity;
    }

    @Override
    protected void onContentsChanged() {
        super.onContentsChanged();
        kettleBlockEntity.markDirty();
    }
}
