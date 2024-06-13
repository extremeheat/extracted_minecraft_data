package net.minecraft.network.protocol.login;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundLoginAcknowledgedPacket implements Packet<ServerLoginPacketListener> {
   public static final ServerboundLoginAcknowledgedPacket INSTANCE = new ServerboundLoginAcknowledgedPacket();
   public static final StreamCodec<ByteBuf, ServerboundLoginAcknowledgedPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

   private ServerboundLoginAcknowledgedPacket() {
      super();
   }

   @Override
   public PacketType<ServerboundLoginAcknowledgedPacket> type() {
      return LoginPacketTypes.SERVERBOUND_LOGIN_ACKNOWLEDGED;
   }

   public void handle(ServerLoginPacketListener var1) {
      var1.handleLoginAcknowledgement(this);
   }

   @Override
   public boolean isTerminal() {
      return true;
   }
}