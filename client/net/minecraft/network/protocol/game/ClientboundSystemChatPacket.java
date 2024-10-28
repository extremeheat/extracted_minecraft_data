package net.minecraft.network.protocol.game;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundSystemChatPacket(Component content, boolean overlay) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSystemChatPacket> STREAM_CODEC;

   public ClientboundSystemChatPacket(Component content, boolean overlay) {
      super();
      this.content = content;
      this.overlay = overlay;
   }

   public PacketType<ClientboundSystemChatPacket> type() {
      return GamePacketTypes.CLIENTBOUND_SYSTEM_CHAT;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSystemChat(this);
   }

   public boolean isSkippable() {
      return true;
   }

   public Component content() {
      return this.content;
   }

   public boolean overlay() {
      return this.overlay;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ComponentSerialization.TRUSTED_STREAM_CODEC, ClientboundSystemChatPacket::content, ByteBufCodecs.BOOL, ClientboundSystemChatPacket::overlay, ClientboundSystemChatPacket::new);
   }
}
