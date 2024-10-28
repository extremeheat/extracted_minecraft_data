package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.Difficulty;

public class ServerboundChangeDifficultyPacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundChangeDifficultyPacket> STREAM_CODEC = Packet.codec(ServerboundChangeDifficultyPacket::write, ServerboundChangeDifficultyPacket::new);
   private final Difficulty difficulty;

   public ServerboundChangeDifficultyPacket(Difficulty var1) {
      super();
      this.difficulty = var1;
   }

   private ServerboundChangeDifficultyPacket(FriendlyByteBuf var1) {
      super();
      this.difficulty = Difficulty.byId(var1.readUnsignedByte());
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeByte(this.difficulty.getId());
   }

   public PacketType<ServerboundChangeDifficultyPacket> type() {
      return GamePacketTypes.SERVERBOUND_CHANGE_DIFFICULTY;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleChangeDifficulty(this);
   }

   public Difficulty getDifficulty() {
      return this.difficulty;
   }
}
