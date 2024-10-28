package net.minecraft.network.protocol.game;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.item.ItemStack;

public record ServerboundSetCreativeModeSlotPacket(short slotNum, ItemStack itemStack) implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundSetCreativeModeSlotPacket> STREAM_CODEC;

   public ServerboundSetCreativeModeSlotPacket(int var1, ItemStack var2) {
      this((short)var1, var2);
   }

   public ServerboundSetCreativeModeSlotPacket(short slotNum, ItemStack itemStack) {
      super();
      this.slotNum = slotNum;
      this.itemStack = itemStack;
   }

   public PacketType<ServerboundSetCreativeModeSlotPacket> type() {
      return GamePacketTypes.SERVERBOUND_SET_CREATIVE_MODE_SLOT;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleSetCreativeModeSlot(this);
   }

   public short slotNum() {
      return this.slotNum;
   }

   public ItemStack itemStack() {
      return this.itemStack;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.SHORT, ServerboundSetCreativeModeSlotPacket::slotNum, ItemStack.validatedStreamCodec(ItemStack.OPTIONAL_STREAM_CODEC), ServerboundSetCreativeModeSlotPacket::itemStack, ServerboundSetCreativeModeSlotPacket::new);
   }
}
