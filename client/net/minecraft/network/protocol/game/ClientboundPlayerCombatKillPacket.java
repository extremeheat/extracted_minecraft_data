package net.minecraft.network.protocol.game;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundPlayerCombatKillPacket(int playerId, Component message) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundPlayerCombatKillPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.VAR_INT,
      ClientboundPlayerCombatKillPacket::playerId,
      ComponentSerialization.TRUSTED_STREAM_CODEC,
      ClientboundPlayerCombatKillPacket::message,
      ClientboundPlayerCombatKillPacket::new
   );

   public ClientboundPlayerCombatKillPacket(int playerId, Component message) {
      super();
      this.playerId = playerId;
      this.message = message;
   }

   @Override
   public PacketType<ClientboundPlayerCombatKillPacket> type() {
      return GamePacketTypes.CLIENTBOUND_PLAYER_COMBAT_KILL;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handlePlayerCombatKill(this);
   }

   @Override
   public boolean isSkippable() {
      return true;
   }
}
