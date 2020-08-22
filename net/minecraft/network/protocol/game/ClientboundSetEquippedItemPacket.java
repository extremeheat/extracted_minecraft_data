package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class ClientboundSetEquippedItemPacket implements Packet {
   private int entity;
   private EquipmentSlot slot;
   private ItemStack itemStack;

   public ClientboundSetEquippedItemPacket() {
      this.itemStack = ItemStack.EMPTY;
   }

   public ClientboundSetEquippedItemPacket(int var1, EquipmentSlot var2, ItemStack var3) {
      this.itemStack = ItemStack.EMPTY;
      this.entity = var1;
      this.slot = var2;
      this.itemStack = var3.copy();
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.entity = var1.readVarInt();
      this.slot = (EquipmentSlot)var1.readEnum(EquipmentSlot.class);
      this.itemStack = var1.readItem();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(this.entity);
      var1.writeEnum(this.slot);
      var1.writeItem(this.itemStack);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetEquippedItem(this);
   }

   public ItemStack getItem() {
      return this.itemStack;
   }

   public int getEntity() {
      return this.entity;
   }

   public EquipmentSlot getSlot() {
      return this.slot;
   }
}
