package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundDeleteChatPacket(MessageSignature.Packed b) implements Packet<ClientGamePacketListener> {
   private final MessageSignature.Packed messageSignature;
   public static final StreamCodec<FriendlyByteBuf, ClientboundDeleteChatPacket> STREAM_CODEC = Packet.codec(
      ClientboundDeleteChatPacket::write, ClientboundDeleteChatPacket::new
   );

   private ClientboundDeleteChatPacket(FriendlyByteBuf var1) {
      this(MessageSignature.Packed.read(var1));
   }

   public ClientboundDeleteChatPacket(MessageSignature.Packed var1) {
      super();
      this.messageSignature = var1;
   }

   private void write(FriendlyByteBuf var1) {
      MessageSignature.Packed.write(var1, this.messageSignature);
   }

   @Override
   public PacketType<ClientboundDeleteChatPacket> type() {
      return GamePacketTypes.CLIENTBOUND_DELETE_CHAT;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleDeleteChat(this);
   }
}
