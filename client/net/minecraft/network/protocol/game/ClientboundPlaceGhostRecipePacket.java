package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

public class ClientboundPlaceGhostRecipePacket implements Packet<ClientGamePacketListener> {
   private int containerId;
   private ResourceLocation recipe;

   public ClientboundPlaceGhostRecipePacket() {
      super();
   }

   public ClientboundPlaceGhostRecipePacket(int var1, Recipe<?> var2) {
      super();
      this.containerId = var1;
      this.recipe = var2.getId();
   }

   public ResourceLocation getRecipe() {
      return this.recipe;
   }

   public int getContainerId() {
      return this.containerId;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.containerId = var1.readByte();
      this.recipe = var1.readResourceLocation();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeByte(this.containerId);
      var1.writeResourceLocation(this.recipe);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handlePlaceRecipe(this);
   }
}
