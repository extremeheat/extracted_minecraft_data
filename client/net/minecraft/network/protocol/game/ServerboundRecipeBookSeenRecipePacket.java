package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;

public record ServerboundRecipeBookSeenRecipePacket(RecipeDisplayId recipe) implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundRecipeBookSeenRecipePacket> STREAM_CODEC;

   public ServerboundRecipeBookSeenRecipePacket(RecipeDisplayId var1) {
      super();
      this.recipe = var1;
   }

   public PacketType<ServerboundRecipeBookSeenRecipePacket> type() {
      return GamePacketTypes.SERVERBOUND_RECIPE_BOOK_SEEN_RECIPE;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleRecipeBookSeenRecipePacket(this);
   }

   public RecipeDisplayId recipe() {
      return this.recipe;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(RecipeDisplayId.STREAM_CODEC, ServerboundRecipeBookSeenRecipePacket::recipe, ServerboundRecipeBookSeenRecipePacket::new);
   }
}
