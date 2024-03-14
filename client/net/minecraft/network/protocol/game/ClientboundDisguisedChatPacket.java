package net.minecraft.network.protocol.game;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundDisguisedChatPacket(Component b, ChatType.Bound c) implements Packet<ClientGamePacketListener> {
   private final Component message;
   private final ChatType.Bound chatType;
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDisguisedChatPacket> STREAM_CODEC = StreamCodec.composite(
      ComponentSerialization.TRUSTED_STREAM_CODEC,
      ClientboundDisguisedChatPacket::message,
      ChatType.Bound.STREAM_CODEC,
      ClientboundDisguisedChatPacket::chatType,
      ClientboundDisguisedChatPacket::new
   );

   public ClientboundDisguisedChatPacket(Component var1, ChatType.Bound var2) {
      super();
      this.message = var1;
      this.chatType = var2;
   }

   @Override
   public PacketType<ClientboundDisguisedChatPacket> type() {
      return GamePacketTypes.CLIENTBOUND_DISGUISED_CHAT;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleDisguisedChat(this);
   }

   @Override
   public boolean isSkippable() {
      return true;
   }
}
