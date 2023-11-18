package net.minecraft.network;

import io.netty.util.Attribute;
import net.minecraft.network.protocol.Packet;

public interface ProtocolSwapHandler {
   static void swapProtocolIfNeeded(Attribute<ConnectionProtocol.CodecData<?>> var0, Packet<?> var1) {
      ConnectionProtocol var2 = var1.nextProtocol();
      if (var2 != null) {
         ConnectionProtocol.CodecData var3 = (ConnectionProtocol.CodecData)var0.get();
         ConnectionProtocol var4 = var3.protocol();
         if (var2 != var4) {
            ConnectionProtocol.CodecData var5 = var2.codec(var3.flow());
            var0.set(var5);
         }
      }
   }
}
