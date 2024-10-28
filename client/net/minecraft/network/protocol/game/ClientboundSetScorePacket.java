package net.minecraft.network.protocol.game;

import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.NumberFormatTypes;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundSetScorePacket(String owner, String objectiveName, int score, Optional<Component> display, Optional<NumberFormat> numberFormat) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSetScorePacket> STREAM_CODEC;

   public ClientboundSetScorePacket(String owner, String objectiveName, int score, Optional<Component> display, Optional<NumberFormat> numberFormat) {
      super();
      this.owner = owner;
      this.objectiveName = objectiveName;
      this.score = score;
      this.display = display;
      this.numberFormat = numberFormat;
   }

   public PacketType<ClientboundSetScorePacket> type() {
      return GamePacketTypes.CLIENTBOUND_SET_SCORE;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetScore(this);
   }

   public String owner() {
      return this.owner;
   }

   public String objectiveName() {
      return this.objectiveName;
   }

   public int score() {
      return this.score;
   }

   public Optional<Component> display() {
      return this.display;
   }

   public Optional<NumberFormat> numberFormat() {
      return this.numberFormat;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, ClientboundSetScorePacket::owner, ByteBufCodecs.STRING_UTF8, ClientboundSetScorePacket::objectiveName, ByteBufCodecs.VAR_INT, ClientboundSetScorePacket::score, ComponentSerialization.TRUSTED_OPTIONAL_STREAM_CODEC, ClientboundSetScorePacket::display, NumberFormatTypes.OPTIONAL_STREAM_CODEC, ClientboundSetScorePacket::numberFormat, ClientboundSetScorePacket::new);
   }
}
