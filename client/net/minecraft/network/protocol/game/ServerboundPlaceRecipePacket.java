package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

public class ServerboundPlaceRecipePacket implements Packet<ServerGamePacketListener> {
   private final int containerId;
   private final ResourceLocation recipe;
   private final boolean shiftDown;

   public ServerboundPlaceRecipePacket(int var1, RecipeHolder<?> var2, boolean var3) {
      super();
      this.containerId = var1;
      this.recipe = var2.id();
      this.shiftDown = var3;
   }

   public ServerboundPlaceRecipePacket(FriendlyByteBuf var1) {
      super();
      this.containerId = var1.readByte();
      this.recipe = var1.readResourceLocation();
      this.shiftDown = var1.readBoolean();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeByte(this.containerId);
      var1.writeResourceLocation(this.recipe);
      var1.writeBoolean(this.shiftDown);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handlePlaceRecipe(this);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public ResourceLocation getRecipe() {
      return this.recipe;
   }

   public boolean isShiftDown() {
      return this.shiftDown;
   }
}
