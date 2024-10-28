package net.minecraft.network.protocol.game;

import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.item.ItemStack;

public class ClientboundContainerSetContentPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundContainerSetContentPacket> STREAM_CODEC = Packet.codec(ClientboundContainerSetContentPacket::write, ClientboundContainerSetContentPacket::new);
   private final int containerId;
   private final int stateId;
   private final List<ItemStack> items;
   private final ItemStack carriedItem;

   public ClientboundContainerSetContentPacket(int var1, int var2, NonNullList<ItemStack> var3, ItemStack var4) {
      super();
      this.containerId = var1;
      this.stateId = var2;
      this.items = NonNullList.withSize(var3.size(), ItemStack.EMPTY);

      for(int var5 = 0; var5 < var3.size(); ++var5) {
         this.items.set(var5, ((ItemStack)var3.get(var5)).copy());
      }

      this.carriedItem = var4.copy();
   }

   private ClientboundContainerSetContentPacket(RegistryFriendlyByteBuf var1) {
      super();
      this.containerId = var1.readUnsignedByte();
      this.stateId = var1.readVarInt();
      this.items = (List)ItemStack.OPTIONAL_LIST_STREAM_CODEC.decode(var1);
      this.carriedItem = (ItemStack)ItemStack.OPTIONAL_STREAM_CODEC.decode(var1);
   }

   private void write(RegistryFriendlyByteBuf var1) {
      var1.writeByte(this.containerId);
      var1.writeVarInt(this.stateId);
      ItemStack.OPTIONAL_LIST_STREAM_CODEC.encode(var1, this.items);
      ItemStack.OPTIONAL_STREAM_CODEC.encode(var1, this.carriedItem);
   }

   public PacketType<ClientboundContainerSetContentPacket> type() {
      return GamePacketTypes.CLIENTBOUND_CONTAINER_SET_CONTENT;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleContainerContent(this);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public List<ItemStack> getItems() {
      return this.items;
   }

   public ItemStack getCarriedItem() {
      return this.carriedItem;
   }

   public int getStateId() {
      return this.stateId;
   }
}
