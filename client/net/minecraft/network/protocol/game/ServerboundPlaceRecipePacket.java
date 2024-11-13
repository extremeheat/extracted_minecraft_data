package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;

public record ServerboundPlaceRecipePacket(int containerId, RecipeDisplayId recipe, boolean useMaxItems) implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundPlaceRecipePacket> STREAM_CODEC;

   public ServerboundPlaceRecipePacket(int var1, RecipeDisplayId var2, boolean var3) {
      super();
      this.containerId = var1;
      this.recipe = var2;
      this.useMaxItems = var3;
   }

   public PacketType<ServerboundPlaceRecipePacket> type() {
      return GamePacketTypes.SERVERBOUND_PLACE_RECIPE;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handlePlaceRecipe(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.CONTAINER_ID, ServerboundPlaceRecipePacket::containerId, RecipeDisplayId.STREAM_CODEC, ServerboundPlaceRecipePacket::recipe, ByteBufCodecs.BOOL, ServerboundPlaceRecipePacket::useMaxItems, ServerboundPlaceRecipePacket::new);
   }
}
