package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundLockDifficultyPacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundLockDifficultyPacket> STREAM_CODEC = Packet.codec(ServerboundLockDifficultyPacket::write, ServerboundLockDifficultyPacket::new);
   private final boolean locked;

   public ServerboundLockDifficultyPacket(boolean var1) {
      super();
      this.locked = var1;
   }

   private ServerboundLockDifficultyPacket(FriendlyByteBuf var1) {
      super();
      this.locked = var1.readBoolean();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeBoolean(this.locked);
   }

   public PacketType<ServerboundLockDifficultyPacket> type() {
      return GamePacketTypes.SERVERBOUND_LOCK_DIFFICULTY;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleLockDifficulty(this);
   }

   public boolean isLocked() {
      return this.locked;
   }
}
