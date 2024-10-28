package net.minecraft.network.protocol.login;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundLoginDisconnectPacket implements Packet<ClientLoginPacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundLoginDisconnectPacket> STREAM_CODEC = Packet.codec(ClientboundLoginDisconnectPacket::write, ClientboundLoginDisconnectPacket::new);
   private final Component reason;

   public ClientboundLoginDisconnectPacket(Component var1) {
      super();
      this.reason = var1;
   }

   private ClientboundLoginDisconnectPacket(FriendlyByteBuf var1) {
      super();
      this.reason = Component.Serializer.fromJsonLenient(var1.readUtf(262144), RegistryAccess.EMPTY);
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeUtf(Component.Serializer.toJson(this.reason, RegistryAccess.EMPTY));
   }

   public PacketType<ClientboundLoginDisconnectPacket> type() {
      return LoginPacketTypes.CLIENTBOUND_LOGIN_DISCONNECT;
   }

   public void handle(ClientLoginPacketListener var1) {
      var1.handleDisconnect(this);
   }

   public Component getReason() {
      return this.reason;
   }
}
