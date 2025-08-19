package net.minecraft.network.protocol.configuration;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundAcceptCodeOfConductPacket() implements Packet<ServerConfigurationPacketListener> {
   public static final ServerboundAcceptCodeOfConductPacket INSTANCE = new ServerboundAcceptCodeOfConductPacket();
   public static final StreamCodec<ByteBuf, ServerboundAcceptCodeOfConductPacket> STREAM_CODEC;

   public ServerboundAcceptCodeOfConductPacket() {
      super();
   }

   public PacketType<ServerboundAcceptCodeOfConductPacket> type() {
      return ConfigurationPacketTypes.SERVERBOUND_ACCEPT_CODE_OF_CONDUCT;
   }

   public void handle(ServerConfigurationPacketListener var1) {
      var1.handleAcceptCodeOfConduct(this);
   }

   static {
      STREAM_CODEC = StreamCodec.<ByteBuf, ServerboundAcceptCodeOfConductPacket>unit(INSTANCE);
   }
}
