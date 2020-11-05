package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.item.ItemStack;

public class ServerboundSetCreativeModeSlotPacket implements Packet<ServerGamePacketListener> {
   private int slotNum;
   private ItemStack itemStack;

   public ServerboundSetCreativeModeSlotPacket() {
      super();
      this.itemStack = ItemStack.EMPTY;
   }

   public ServerboundSetCreativeModeSlotPacket(int var1, ItemStack var2) {
      super();
      this.itemStack = ItemStack.EMPTY;
      this.slotNum = var1;
      this.itemStack = var2.copy();
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleSetCreativeModeSlot(this);
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.slotNum = var1.readShort();
      this.itemStack = var1.readItem();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeShort(this.slotNum);
      var1.writeItem(this.itemStack);
   }

   public int getSlotNum() {
      return this.slotNum;
   }

   public ItemStack getItem() {
      return this.itemStack;
   }
}
