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

public record ClientboundSetScorePacket(String b, String c, int d, Optional<Component> e, Optional<NumberFormat> f) implements Packet<ClientGamePacketListener> {
   private final String owner;
   private final String objectiveName;
   private final int score;
   private final Optional<Component> display;
   private final Optional<NumberFormat> numberFormat;
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSetScorePacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.STRING_UTF8,
      ClientboundSetScorePacket::owner,
      ByteBufCodecs.STRING_UTF8,
      ClientboundSetScorePacket::objectiveName,
      ByteBufCodecs.VAR_INT,
      ClientboundSetScorePacket::score,
      ComponentSerialization.TRUSTED_OPTIONAL_STREAM_CODEC,
      ClientboundSetScorePacket::display,
      NumberFormatTypes.OPTIONAL_STREAM_CODEC,
      ClientboundSetScorePacket::numberFormat,
      ClientboundSetScorePacket::new
   );

   public ClientboundSetScorePacket(String var1, String var2, int var3, Optional<Component> var4, Optional<NumberFormat> var5) {
      super();
      this.owner = var1;
      this.objectiveName = var2;
      this.score = var3;
      this.display = var4;
      this.numberFormat = var5;
   }

   @Override
   public PacketType<ClientboundSetScorePacket> type() {
      return GamePacketTypes.CLIENTBOUND_SET_SCORE;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetScore(this);
   }
}
