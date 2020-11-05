package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.item.ItemStack;

public class ServerboundEditBookPacket implements Packet<ServerGamePacketListener> {
   private ItemStack book;
   private boolean signing;
   private int slot;

   public ServerboundEditBookPacket() {
      super();
   }

   public ServerboundEditBookPacket(ItemStack var1, boolean var2, int var3) {
      super();
      this.book = var1.copy();
      this.signing = var2;
      this.slot = var3;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.book = var1.readItem();
      this.signing = var1.readBoolean();
      this.slot = var1.readVarInt();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeItem(this.book);
      var1.writeBoolean(this.signing);
      var1.writeVarInt(this.slot);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleEditBook(this);
   }

   public ItemStack getBook() {
      return this.book;
   }

   public boolean isSigning() {
      return this.signing;
   }

   public int getSlot() {
      return this.slot;
   }
}
