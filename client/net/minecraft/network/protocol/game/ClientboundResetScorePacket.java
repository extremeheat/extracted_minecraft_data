package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundResetScorePacket(String owner, @Nullable String objectiveName) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundResetScorePacket> STREAM_CODEC = Packet.codec(
      ClientboundResetScorePacket::write, ClientboundResetScorePacket::new
   );

   private ClientboundResetScorePacket(FriendlyByteBuf var1) {
      this(var1.readUtf(), var1.readNullable(FriendlyByteBuf::readUtf));
   }

   public ClientboundResetScorePacket(String owner, @Nullable String objectiveName) {
      super();
      this.owner = owner;
      this.objectiveName = objectiveName;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeUtf(this.owner);
      var1.writeNullable(this.objectiveName, FriendlyByteBuf::writeUtf);
   }

   @Override
   public PacketType<ClientboundResetScorePacket> type() {
      return GamePacketTypes.CLIENTBOUND_RESET_SCORE;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleResetScore(this);
   }
}
