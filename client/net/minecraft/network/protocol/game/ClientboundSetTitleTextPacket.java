package net.minecraft.network.protocol.game;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundSetTitleTextPacket(Component text) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSetTitleTextPacket> STREAM_CODEC;

   public ClientboundSetTitleTextPacket(Component text) {
      super();
      this.text = text;
   }

   public PacketType<ClientboundSetTitleTextPacket> type() {
      return GamePacketTypes.CLIENTBOUND_SET_TITLE_TEXT;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.setTitleText(this);
   }

   public Component text() {
      return this.text;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ComponentSerialization.TRUSTED_STREAM_CODEC, ClientboundSetTitleTextPacket::text, ClientboundSetTitleTextPacket::new);
   }
}
