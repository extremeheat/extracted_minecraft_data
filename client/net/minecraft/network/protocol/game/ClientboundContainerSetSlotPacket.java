package net.minecraft.network.protocol.game;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.item.ItemStack;

public class ClientboundContainerSetSlotPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundContainerSetSlotPacket> STREAM_CODEC = Packet.codec(ClientboundContainerSetSlotPacket::write, ClientboundContainerSetSlotPacket::new);
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

   private ClientboundContainerSetSlotPacket(RegistryFriendlyByteBuf var1) {
      super();
      this.containerId = var1.readByte();
      this.stateId = var1.readVarInt();
      this.slot = var1.readShort();
      this.itemStack = (ItemStack)ItemStack.OPTIONAL_STREAM_CODEC.decode(var1);
   }

   private void write(RegistryFriendlyByteBuf var1) {
      var1.writeByte(this.containerId);
      var1.writeVarInt(this.stateId);
      var1.writeShort(this.slot);
      ItemStack.OPTIONAL_STREAM_CODEC.encode(var1, this.itemStack);
   }

   public PacketType<ClientboundContainerSetSlotPacket> type() {
      return GamePacketTypes.CLIENTBOUND_CONTAINER_SET_SLOT;
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
