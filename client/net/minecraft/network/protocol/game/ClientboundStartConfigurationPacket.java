package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundStartConfigurationPacket implements Packet<ClientGamePacketListener> {
   public static final ClientboundStartConfigurationPacket INSTANCE = new ClientboundStartConfigurationPacket();
   public static final StreamCodec<ByteBuf, ClientboundStartConfigurationPacket> STREAM_CODEC;

   private ClientboundStartConfigurationPacket() {
      super();
   }

   public PacketType<ClientboundStartConfigurationPacket> type() {
      return GamePacketTypes.CLIENTBOUND_START_CONFIGURATION;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleConfigurationStart(this);
   }

   public boolean isTerminal() {
      return true;
   }

   static {
      STREAM_CODEC = StreamCodec.unit(INSTANCE);
   }
}
