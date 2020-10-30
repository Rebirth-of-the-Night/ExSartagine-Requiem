package subaraki.exsartagine.tileentity;

import net.minecraftforge.items.IItemHandler;

public interface Cooker {

    void setCooking();
    void stopCooking();
    IItemHandler getInventory();
    int getProgress();
    int getCookTime();

}
