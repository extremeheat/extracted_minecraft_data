package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.inventory.RecipeBookType;

public class ServerboundRecipeBookChangeSettingsPacket implements Packet<ServerGamePacketListener> {
   private RecipeBookType bookType;
   private boolean isOpen;
   private boolean isFiltering;

   public ServerboundRecipeBookChangeSettingsPacket() {
      super();
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.bookType = (RecipeBookType)var1.readEnum(RecipeBookType.class);
      this.isOpen = var1.readBoolean();
      this.isFiltering = var1.readBoolean();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeEnum(this.bookType);
      var1.writeBoolean(this.isOpen);
      var1.writeBoolean(this.isFiltering);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleRecipeBookChangeSettingsPacket(this);
   }

   public RecipeBookType getBookType() {
      return this.bookType;
   }

   public boolean isOpen() {
      return this.isOpen;
   }

   public boolean isFiltering() {
      return this.isFiltering;
   }
}
