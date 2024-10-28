package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.damagesource.CombatTracker;

public class ClientboundPlayerCombatEndPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundPlayerCombatEndPacket> STREAM_CODEC = Packet.codec(ClientboundPlayerCombatEndPacket::write, ClientboundPlayerCombatEndPacket::new);
   private final int duration;

   public ClientboundPlayerCombatEndPacket(CombatTracker var1) {
      this(var1.getCombatDuration());
   }

   public ClientboundPlayerCombatEndPacket(int var1) {
      super();
      this.duration = var1;
   }

   private ClientboundPlayerCombatEndPacket(FriendlyByteBuf var1) {
      super();
      this.duration = var1.readVarInt();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.duration);
   }

   public PacketType<ClientboundPlayerCombatEndPacket> type() {
      return GamePacketTypes.CLIENTBOUND_PLAYER_COMBAT_END;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handlePlayerCombatEnd(this);
   }
}
