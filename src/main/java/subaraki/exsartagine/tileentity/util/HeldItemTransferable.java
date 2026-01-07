package subaraki.exsartagine.tileentity.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

public interface HeldItemTransferable {
    boolean transferFromHeldItem(EntityPlayer player, EnumHand hand, boolean insert);
}
