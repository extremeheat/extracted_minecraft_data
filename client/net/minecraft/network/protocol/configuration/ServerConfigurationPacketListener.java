package net.minecraft.network.protocol.configuration;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;

public interface ServerConfigurationPacketListener extends ServerCommonPacketListener {
   default ConnectionProtocol protocol() {
      return ConnectionProtocol.CONFIGURATION;
   }

   void handleConfigurationFinished(ServerboundFinishConfigurationPacket var1);

   void handleSelectKnownPacks(ServerboundSelectKnownPacks var1);
}
