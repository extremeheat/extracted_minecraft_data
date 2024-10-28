package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundClearTitlesPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundClearTitlesPacket> STREAM_CODEC = Packet.codec(ClientboundClearTitlesPacket::write, ClientboundClearTitlesPacket::new);
   private final boolean resetTimes;

   public ClientboundClearTitlesPacket(boolean var1) {
      super();
      this.resetTimes = var1;
   }

   private ClientboundClearTitlesPacket(FriendlyByteBuf var1) {
      super();
      this.resetTimes = var1.readBoolean();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeBoolean(this.resetTimes);
   }

   public PacketType<ClientboundClearTitlesPacket> type() {
      return GamePacketTypes.CLIENTBOUND_CLEAR_TITLES;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleTitlesClear(this);
   }

   public boolean shouldResetTimes() {
      return this.resetTimes;
   }
}
