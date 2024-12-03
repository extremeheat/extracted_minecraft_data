package net.minecraft.network.protocol.login;

import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundHelloPacket(String name, UUID profileId) implements Packet<ServerLoginPacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundHelloPacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ServerboundHelloPacket>codec(ServerboundHelloPacket::write, ServerboundHelloPacket::new);

   private ServerboundHelloPacket(FriendlyByteBuf var1) {
      this(var1.readUtf(16), var1.readUUID());
   }

   public ServerboundHelloPacket(String var1, UUID var2) {
      super();
      this.name = var1;
      this.profileId = var2;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeUtf(this.name, 16);
      var1.writeUUID(this.profileId);
   }

   public PacketType<ServerboundHelloPacket> type() {
      return LoginPacketTypes.SERVERBOUND_HELLO;
   }

   public void handle(ServerLoginPacketListener var1) {
      var1.handleHello(this);
   }
}
