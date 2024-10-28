package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundRenameItemPacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundRenameItemPacket> STREAM_CODEC = Packet.codec(ServerboundRenameItemPacket::write, ServerboundRenameItemPacket::new);
   private final String name;

   public ServerboundRenameItemPacket(String var1) {
      super();
      this.name = var1;
   }

   private ServerboundRenameItemPacket(FriendlyByteBuf var1) {
      super();
      this.name = var1.readUtf();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeUtf(this.name);
   }

   public PacketType<ServerboundRenameItemPacket> type() {
      return GamePacketTypes.SERVERBOUND_RENAME_ITEM;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleRenameItem(this);
   }

   public String getName() {
      return this.name;
   }
}
