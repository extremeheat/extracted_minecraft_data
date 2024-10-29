package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.stats.RecipeBookSettings;

public record ClientboundRecipeBookSettingsPacket(RecipeBookSettings bookSettings) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundRecipeBookSettingsPacket> STREAM_CODEC;

   public ClientboundRecipeBookSettingsPacket(RecipeBookSettings var1) {
      super();
      this.bookSettings = var1;
   }

   public PacketType<ClientboundRecipeBookSettingsPacket> type() {
      return GamePacketTypes.CLIENTBOUND_RECIPE_BOOK_SETTINGS;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleRecipeBookSettings(this);
   }

   public RecipeBookSettings bookSettings() {
      return this.bookSettings;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(RecipeBookSettings.STREAM_CODEC, ClientboundRecipeBookSettingsPacket::bookSettings, ClientboundRecipeBookSettingsPacket::new);
   }
}
