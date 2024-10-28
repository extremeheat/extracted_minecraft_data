package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

public class ServerboundRecipeBookSeenRecipePacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundRecipeBookSeenRecipePacket> STREAM_CODEC = Packet.codec(ServerboundRecipeBookSeenRecipePacket::write, ServerboundRecipeBookSeenRecipePacket::new);
   private final ResourceLocation recipe;

   public ServerboundRecipeBookSeenRecipePacket(RecipeHolder<?> var1) {
      super();
      this.recipe = var1.id();
   }

   private ServerboundRecipeBookSeenRecipePacket(FriendlyByteBuf var1) {
      super();
      this.recipe = var1.readResourceLocation();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeResourceLocation(this.recipe);
   }

   public PacketType<ServerboundRecipeBookSeenRecipePacket> type() {
      return GamePacketTypes.SERVERBOUND_RECIPE_BOOK_SEEN_RECIPE;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleRecipeBookSeenRecipePacket(this);
   }

   public ResourceLocation getRecipe() {
      return this.recipe;
   }
}
