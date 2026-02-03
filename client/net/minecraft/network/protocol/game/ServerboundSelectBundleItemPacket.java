package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundSelectBundleItemPacket(int slotId, int selectedItemIndex) implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundSelectBundleItemPacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ServerboundSelectBundleItemPacket>codec(ServerboundSelectBundleItemPacket::write, ServerboundSelectBundleItemPacket::new);

   private ServerboundSelectBundleItemPacket(final FriendlyByteBuf input) {
      this(input.readVarInt(), input.readVarInt());
      if (this.selectedItemIndex < 0 && this.selectedItemIndex != -1) {
         throw new IllegalArgumentException("Invalid selectedItemIndex: " + this.selectedItemIndex);
      }
   }

   public ServerboundSelectBundleItemPacket {
      super();
   }

   private void write(final FriendlyByteBuf output) {
      output.writeVarInt(this.slotId);
      output.writeVarInt(this.selectedItemIndex);
   }

   public PacketType<ServerboundSelectBundleItemPacket> type() {
      return GamePacketTypes.SERVERBOUND_BUNDLE_ITEM_SELECTED;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handleBundleItemSelectedPacket(this);
   }
}
