package net.minecraft.network.protocol.game;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;

public record ClientboundPlaceGhostRecipePacket(int containerId, RecipeDisplay recipeDisplay) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundPlaceGhostRecipePacket> STREAM_CODEC;

   public ClientboundPlaceGhostRecipePacket(int var1, RecipeDisplay var2) {
      super();
      this.containerId = var1;
      this.recipeDisplay = var2;
   }

   public PacketType<ClientboundPlaceGhostRecipePacket> type() {
      return GamePacketTypes.CLIENTBOUND_PLACE_GHOST_RECIPE;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handlePlaceRecipe(this);
   }

   public int containerId() {
      return this.containerId;
   }

   public RecipeDisplay recipeDisplay() {
      return this.recipeDisplay;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.CONTAINER_ID, ClientboundPlaceGhostRecipePacket::containerId, RecipeDisplay.STREAM_CODEC, ClientboundPlaceGhostRecipePacket::recipeDisplay, ClientboundPlaceGhostRecipePacket::new);
   }
}
