package subaraki.exsartagine.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import subaraki.exsartagine.gui.common.ContainerKettle;

// not threadsafe!
public class SwapTanksPacket implements IMessage {


  public SwapTanksPacket() {
  }

  @Override
  public void fromBytes(ByteBuf buf) {
  }

  @Override
  public void toBytes(ByteBuf buf) {
  }

  public static class Handler implements IMessageHandler<SwapTanksPacket, IMessage> {
    @Override
    public IMessage onMessage(SwapTanksPacket message, MessageContext ctx) {
      // Always use a construct like this to actually handle your message. This ensures that
      // youre 'handle' code is run on the main Minecraft thread. 'onMessage' itself
      // is called on the networking thread so it is not safe to do a lot of things
      // here.
      FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
      return null;
    }

    private void handle(SwapTanksPacket message, MessageContext ctx) {
      // This code is run on the server side. So you can do server-side calculations here
      Container container = ctx.getServerHandler().player.openContainer;
      if (container instanceof ContainerKettle) {
        ((ContainerKettle) container).swapTanks();
      }
    }
  }
}
