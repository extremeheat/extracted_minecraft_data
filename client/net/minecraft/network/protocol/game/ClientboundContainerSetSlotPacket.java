package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.item.ItemStack;

public class ClientboundContainerSetSlotPacket implements Packet<ClientGamePacketListener> {
   private int containerId;
   private int slot;
   private ItemStack itemStack;

   public ClientboundContainerSetSlotPacket() {
      super();
      this.itemStack = ItemStack.EMPTY;
   }

   public ClientboundContainerSetSlotPacket(int var1, int var2, ItemStack var3) {
      super();
      this.itemStack = ItemStack.EMPTY;
      this.containerId = var1;
      this.slot = var2;
      this.itemStack = var3.copy();
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleContainerSetSlot(this);
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.containerId = var1.readByte();
      this.slot = var1.readShort();
      this.itemStack = var1.readItem();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeByte(this.containerId);
      var1.writeShort(this.slot);
      var1.writeItem(this.itemStack);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public int getSlot() {
      return this.slot;
   }

   public ItemStack getItem() {
      return this.itemStack;
   }
}
