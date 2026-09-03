package subaraki.exsartagine.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import subaraki.exsartagine.gui.common.FluidContainer;

public class ClickGuiTankPacket implements IMessage {
    private int index;

    public ClickGuiTankPacket() {
    }

    public ClickGuiTankPacket(int index) {
        this.index = index;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        index = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(index);
    }

    public static class Handler implements IMessageHandler<ClickGuiTankPacket, IMessage> {
        @Override
        public IMessage onMessage(ClickGuiTankPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                if (!(player.openContainer instanceof FluidContainer)) return;
                ItemStack held = player.inventory.getItemStack();
                if (held.isEmpty()) return;
                IFluidHandler tank = ((FluidContainer) player.openContainer).getFluidContainerTank(message.index);
                if (tank == null) return;
                IItemHandler inventory = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                if (inventory == null) return;

                FluidActionResult fillResult = FluidUtil.tryFillContainerAndStow(
                        held, tank, inventory, Integer.MAX_VALUE, player, true);
                if (fillResult.isSuccess()) {
                    player.inventory.setItemStack(fillResult.getResult());
                    player.updateHeldItem();
                    return;
                }

                FluidActionResult emptyResult = FluidUtil.tryEmptyContainerAndStow(
                        held, tank, inventory, Integer.MAX_VALUE, player, true);
                if (emptyResult.isSuccess()) {
                    player.inventory.setItemStack(emptyResult.getResult());
                    player.updateHeldItem();
                }
            });
            return null;
        }
    }
}
