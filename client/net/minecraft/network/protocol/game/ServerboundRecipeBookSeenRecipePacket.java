package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

public class ServerboundRecipeBookSeenRecipePacket implements Packet<ServerGamePacketListener> {
   private ResourceLocation recipe;

   public ServerboundRecipeBookSeenRecipePacket() {
      super();
   }

   public ServerboundRecipeBookSeenRecipePacket(Recipe<?> var1) {
      super();
      this.recipe = var1.getId();
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.recipe = var1.readResourceLocation();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeResourceLocation(this.recipe);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleRecipeBookSeenRecipePacket(this);
   }

   public ResourceLocation getRecipe() {
      return this.recipe;
   }
}
