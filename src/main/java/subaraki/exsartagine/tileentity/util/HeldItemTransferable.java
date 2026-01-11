package subaraki.exsartagine.tileentity.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

public interface HeldItemTransferable {

    int getTransferFromHeldItemZone(EntityPlayer player, EnumHand hand, EnumFacing face, float hitX, float hitY, float hitZ);

    boolean transferFromHeldItem(EntityPlayer player, EnumHand hand, boolean insert, int zone);

}
