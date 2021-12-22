package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.item.ItemStack;

public class ServerboundSetCreativeModeSlotPacket implements Packet<ServerGamePacketListener> {
   private final int slotNum;
   private final ItemStack itemStack;

   public ServerboundSetCreativeModeSlotPacket(int var1, ItemStack var2) {
      super();
      this.slotNum = var1;
      this.itemStack = var2.copy();
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleSetCreativeModeSlot(this);
   }

   public ServerboundSetCreativeModeSlotPacket(FriendlyByteBuf var1) {
      super();
      this.slotNum = var1.readShort();
      this.itemStack = var1.readItem();
   }

   public void write(FriendlyByteBuf var1) {
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
