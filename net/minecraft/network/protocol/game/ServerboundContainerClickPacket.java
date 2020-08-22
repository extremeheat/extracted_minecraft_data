package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

public class ServerboundContainerClickPacket implements Packet {
   private int containerId;
   private int slotNum;
   private int buttonNum;
   private short uid;
   private ItemStack itemStack;
   private ClickType clickType;

   public ServerboundContainerClickPacket() {
      this.itemStack = ItemStack.EMPTY;
   }

   public ServerboundContainerClickPacket(int var1, int var2, int var3, ClickType var4, ItemStack var5, short var6) {
      this.itemStack = ItemStack.EMPTY;
      this.containerId = var1;
      this.slotNum = var2;
      this.buttonNum = var3;
      this.itemStack = var5.copy();
      this.uid = var6;
      this.clickType = var4;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleContainerClick(this);
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.containerId = var1.readByte();
      this.slotNum = var1.readShort();
      this.buttonNum = var1.readByte();
      this.uid = var1.readShort();
      this.clickType = (ClickType)var1.readEnum(ClickType.class);
      this.itemStack = var1.readItem();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeByte(this.containerId);
      var1.writeShort(this.slotNum);
      var1.writeByte(this.buttonNum);
      var1.writeShort(this.uid);
      var1.writeEnum(this.clickType);
      var1.writeItem(this.itemStack);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public int getSlotNum() {
      return this.slotNum;
   }

   public int getButtonNum() {
      return this.buttonNum;
   }

   public short getUid() {
      return this.uid;
   }

   public ItemStack getItem() {
      return this.itemStack;
   }

   public ClickType getClickType() {
      return this.clickType;
   }
}
