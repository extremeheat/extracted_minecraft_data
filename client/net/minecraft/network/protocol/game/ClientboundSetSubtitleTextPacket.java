package net.minecraft.network.protocol.game;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundSetSubtitleTextPacket(Component text) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSetSubtitleTextPacket> STREAM_CODEC;

   public ClientboundSetSubtitleTextPacket(Component var1) {
      super();
      this.text = var1;
   }

   public PacketType<ClientboundSetSubtitleTextPacket> type() {
      return GamePacketTypes.CLIENTBOUND_SET_SUBTITLE_TEXT;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.setSubtitleText(this);
   }

   public Component text() {
      return this.text;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ComponentSerialization.TRUSTED_STREAM_CODEC, ClientboundSetSubtitleTextPacket::text, ClientboundSetSubtitleTextPacket::new);
   }
}
