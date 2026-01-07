package subaraki.exsartagine.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import subaraki.exsartagine.tileentity.util.HeldItemTransferable;

public class TransferHeldItemPacket implements IMessage {

    BlockPos target;
    EnumHand hand;
    boolean insert;

    public TransferHeldItemPacket() {
    }

    public TransferHeldItemPacket(BlockPos target, EnumHand hand, boolean insert) {
        this.target = target;
        this.hand = hand;
        this.insert = insert;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        target = BlockPos.fromLong(buf.readLong());
        byte mask = buf.readByte();
        hand = (mask & 0x1) == 0 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
        insert = (mask & 0x2) != 0;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(target.toLong());
        buf.writeByte(hand.ordinal() | (insert ? 0x2 : 0)); // hopefully nobody's adding a third hand to the game
    }

    public static class Handler implements IMessageHandler<TransferHeldItemPacket, IMessage> {
        @Override
        public IMessage onMessage(TransferHeldItemPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                double reach = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue() + 1;
                BlockPos pos = message.target;
                if (player.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= reach) {
                    TileEntity te = player.world.getTileEntity(pos);
                    if (te instanceof HeldItemTransferable) {
                        ((HeldItemTransferable) te).transferFromHeldItem(player, message.hand, message.insert);
                    }
                }
            });
            return null;
        }
    }
}
