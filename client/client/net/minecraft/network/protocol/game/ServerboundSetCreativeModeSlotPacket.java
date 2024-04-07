package net.minecraft.network.protocol.game;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.item.ItemStack;

public class ServerboundSetCreativeModeSlotPacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundSetCreativeModeSlotPacket> STREAM_CODEC = Packet.codec(
      ServerboundSetCreativeModeSlotPacket::write, ServerboundSetCreativeModeSlotPacket::new
   );
   private final int slotNum;
   private final ItemStack itemStack;

   public ServerboundSetCreativeModeSlotPacket(int var1, ItemStack var2) {
      super();
      this.slotNum = var1;
      this.itemStack = var2.copy();
   }

   private ServerboundSetCreativeModeSlotPacket(RegistryFriendlyByteBuf var1) {
      super();
      this.slotNum = var1.readShort();
      this.itemStack = ItemStack.OPTIONAL_STREAM_CODEC.decode(var1);
   }

   private void write(RegistryFriendlyByteBuf var1) {
      var1.writeShort(this.slotNum);
      ItemStack.OPTIONAL_STREAM_CODEC.encode(var1, this.itemStack);
   }

   @Override
   public PacketType<ServerboundSetCreativeModeSlotPacket> type() {
      return GamePacketTypes.SERVERBOUND_SET_CREATIVE_MODE_SLOT;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleSetCreativeModeSlot(this);
   }

   public int getSlotNum() {
      return this.slotNum;
   }

   public ItemStack getItem() {
      return this.itemStack;
   }
}
