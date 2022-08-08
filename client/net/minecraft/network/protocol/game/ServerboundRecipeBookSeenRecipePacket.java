package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

public class ServerboundRecipeBookSeenRecipePacket implements Packet<ServerGamePacketListener> {
   private final ResourceLocation recipe;

   public ServerboundRecipeBookSeenRecipePacket(Recipe<?> var1) {
      super();
      this.recipe = var1.getId();
   }

   public ServerboundRecipeBookSeenRecipePacket(FriendlyByteBuf var1) {
      super();
      this.recipe = var1.readResourceLocation();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeResourceLocation(this.recipe);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleRecipeBookSeenRecipePacket(this);
   }

   public ResourceLocation getRecipe() {
      return this.recipe;
   }
}
