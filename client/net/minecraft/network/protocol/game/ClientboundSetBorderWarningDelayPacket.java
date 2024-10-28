package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.border.WorldBorder;

public class ClientboundSetBorderWarningDelayPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundSetBorderWarningDelayPacket> STREAM_CODEC = Packet.codec(ClientboundSetBorderWarningDelayPacket::write, ClientboundSetBorderWarningDelayPacket::new);
   private final int warningDelay;

   public ClientboundSetBorderWarningDelayPacket(WorldBorder var1) {
      super();
      this.warningDelay = var1.getWarningTime();
   }

   private ClientboundSetBorderWarningDelayPacket(FriendlyByteBuf var1) {
      super();
      this.warningDelay = var1.readVarInt();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.warningDelay);
   }

   public PacketType<ClientboundSetBorderWarningDelayPacket> type() {
      return GamePacketTypes.CLIENTBOUND_SET_BORDER_WARNING_DELAY;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetBorderWarningDelay(this);
   }

   public int getWarningDelay() {
      return this.warningDelay;
   }
}
