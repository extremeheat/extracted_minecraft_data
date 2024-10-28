package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundConfigurationAcknowledgedPacket implements Packet<ServerGamePacketListener> {
   public static final ServerboundConfigurationAcknowledgedPacket INSTANCE = new ServerboundConfigurationAcknowledgedPacket();
   public static final StreamCodec<ByteBuf, ServerboundConfigurationAcknowledgedPacket> STREAM_CODEC;

   private ServerboundConfigurationAcknowledgedPacket() {
      super();
   }

   public PacketType<ServerboundConfigurationAcknowledgedPacket> type() {
      return GamePacketTypes.SERVERBOUND_CONFIGURATION_ACKNOWLEDGED;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleConfigurationAcknowledged(this);
   }

   public boolean isTerminal() {
      return true;
   }

   static {
      STREAM_CODEC = StreamCodec.unit(INSTANCE);
   }
}
