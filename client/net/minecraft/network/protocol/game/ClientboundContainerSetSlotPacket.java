package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.item.ItemStack;

public class ClientboundContainerSetSlotPacket implements Packet<ClientGamePacketListener> {
   public static final int CARRIED_ITEM = -1;
   public static final int PLAYER_INVENTORY = -2;
   private final int containerId;
   private final int stateId;
   private final int slot;
   private final ItemStack itemStack;

   public ClientboundContainerSetSlotPacket(int var1, int var2, int var3, ItemStack var4) {
      super();
      this.containerId = var1;
      this.stateId = var2;
      this.slot = var3;
      this.itemStack = var4.copy();
   }

   public ClientboundContainerSetSlotPacket(FriendlyByteBuf var1) {
      super();
      this.containerId = var1.readByte();
      this.stateId = var1.readVarInt();
      this.slot = var1.readShort();
      this.itemStack = var1.readItem();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeByte(this.containerId);
      var1.writeVarInt(this.stateId);
      var1.writeShort(this.slot);
      var1.writeItem(this.itemStack);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleContainerSetSlot(this);
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

   public int getStateId() {
      return this.stateId;
   }
}
