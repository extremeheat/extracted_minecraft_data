package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

public class ServerboundPlaceRecipePacket implements Packet {
   private int containerId;
   private ResourceLocation recipe;
   private boolean shiftDown;

   public ServerboundPlaceRecipePacket() {
   }

   public ServerboundPlaceRecipePacket(int var1, Recipe var2, boolean var3) {
      this.containerId = var1;
      this.recipe = var2.getId();
      this.shiftDown = var3;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.containerId = var1.readByte();
      this.recipe = var1.readResourceLocation();
      this.shiftDown = var1.readBoolean();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
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
