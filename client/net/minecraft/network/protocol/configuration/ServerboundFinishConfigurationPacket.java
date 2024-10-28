package net.minecraft.network.protocol.configuration;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundFinishConfigurationPacket implements Packet<ServerConfigurationPacketListener> {
   public static final ServerboundFinishConfigurationPacket INSTANCE = new ServerboundFinishConfigurationPacket();
   public static final StreamCodec<ByteBuf, ServerboundFinishConfigurationPacket> STREAM_CODEC;

   private ServerboundFinishConfigurationPacket() {
      super();
   }

   public PacketType<ServerboundFinishConfigurationPacket> type() {
      return ConfigurationPacketTypes.SERVERBOUND_FINISH_CONFIGURATION;
   }

   public void handle(ServerConfigurationPacketListener var1) {
      var1.handleConfigurationFinished(this);
   }

   public boolean isTerminal() {
      return true;
   }

   static {
      STREAM_CODEC = StreamCodec.unit(INSTANCE);
   }
}
