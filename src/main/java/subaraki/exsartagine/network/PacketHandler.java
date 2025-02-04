package subaraki.exsartagine.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {

  public static SimpleNetworkWrapper INSTANCE = null;

  public PacketHandler() {
  }

  public static void registerMessages(String channelName) {
    INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);
    registerMessages();
  }
  public static void registerMessages() {
    // Register messages which are sent from the client to the server here:
    INSTANCE.registerMessage(SwapTanksPacket.Handler.class, SwapTanksPacket.class, 0, Side.SERVER);
    INSTANCE.registerMessage(ClearTankPacket.Handler.class, ClearTankPacket.class, 1, Side.SERVER);
  }
}