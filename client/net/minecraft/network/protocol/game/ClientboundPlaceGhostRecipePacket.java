package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

public class ClientboundPlaceGhostRecipePacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundPlaceGhostRecipePacket> STREAM_CODEC = Packet.codec(ClientboundPlaceGhostRecipePacket::write, ClientboundPlaceGhostRecipePacket::new);
   private final int containerId;
   private final ResourceLocation recipe;

   public ClientboundPlaceGhostRecipePacket(int var1, RecipeHolder<?> var2) {
      super();
      this.containerId = var1;
      this.recipe = var2.id();
   }

   private ClientboundPlaceGhostRecipePacket(FriendlyByteBuf var1) {
      super();
      this.containerId = var1.readByte();
      this.recipe = var1.readResourceLocation();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeByte(this.containerId);
      var1.writeResourceLocation(this.recipe);
   }

   public PacketType<ClientboundPlaceGhostRecipePacket> type() {
      return GamePacketTypes.CLIENTBOUND_PLACE_GHOST_RECIPE;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handlePlaceRecipe(this);
   }

   public ResourceLocation getRecipe() {
      return this.recipe;
   }

   public int getContainerId() {
      return this.containerId;
   }
}
