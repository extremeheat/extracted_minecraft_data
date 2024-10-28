package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.Difficulty;

public class ClientboundChangeDifficultyPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundChangeDifficultyPacket> STREAM_CODEC = Packet.codec(ClientboundChangeDifficultyPacket::write, ClientboundChangeDifficultyPacket::new);
   private final Difficulty difficulty;
   private final boolean locked;

   public ClientboundChangeDifficultyPacket(Difficulty var1, boolean var2) {
      super();
      this.difficulty = var1;
      this.locked = var2;
   }

   private ClientboundChangeDifficultyPacket(FriendlyByteBuf var1) {
      super();
      this.difficulty = Difficulty.byId(var1.readUnsignedByte());
      this.locked = var1.readBoolean();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeByte(this.difficulty.getId());
      var1.writeBoolean(this.locked);
   }

   public PacketType<ClientboundChangeDifficultyPacket> type() {
      return GamePacketTypes.CLIENTBOUND_CHANGE_DIFFICULTY;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleChangeDifficulty(this);
   }

   public boolean isLocked() {
      return this.locked;
   }

   public Difficulty getDifficulty() {
      return this.difficulty;
   }
}
