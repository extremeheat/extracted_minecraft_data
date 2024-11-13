package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundSelectBundleItemPacket(int slotId, int selectedItemIndex) implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundSelectBundleItemPacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ServerboundSelectBundleItemPacket>codec(ServerboundSelectBundleItemPacket::write, ServerboundSelectBundleItemPacket::new);

   private ServerboundSelectBundleItemPacket(FriendlyByteBuf var1) {
      this(var1.readVarInt(), var1.readVarInt());
   }

   public ServerboundSelectBundleItemPacket(int var1, int var2) {
      super();
      this.slotId = var1;
      this.selectedItemIndex = var2;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.slotId);
      var1.writeVarInt(this.selectedItemIndex);
   }

   public PacketType<ServerboundSelectBundleItemPacket> type() {
      return GamePacketTypes.SERVERBOUND_BUNDLE_ITEM_SELECTED;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleBundleItemSelectedPacket(this);
   }
}
