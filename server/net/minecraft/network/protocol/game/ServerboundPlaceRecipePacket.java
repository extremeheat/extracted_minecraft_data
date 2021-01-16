package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;

public class ServerboundPlaceRecipePacket implements Packet<ServerGamePacketListener> {
   private int containerId;
   private ResourceLocation recipe;
   private boolean shiftDown;

   public ServerboundPlaceRecipePacket() {
      super();
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
