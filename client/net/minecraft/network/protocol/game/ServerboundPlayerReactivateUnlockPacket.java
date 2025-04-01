package net.minecraft.network.protocol.game;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.server.players.PlayerUnlock;

public record ServerboundPlayerReactivateUnlockPacket(Holder<PlayerUnlock> unlock) implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundPlayerReactivateUnlockPacket> STREAM_CODEC;

   public ServerboundPlayerReactivateUnlockPacket(Holder<PlayerUnlock> var1) {
      super();
      this.unlock = var1;
   }

   public PacketType<ServerboundPlayerReactivateUnlockPacket> type() {
      return GamePacketTypes.SERVERBOUND_PLAYER_REACTIVATE_UNLOCK;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleReactivateUnlock(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.holderRegistry(Registries.PLAYER_UNLOCK), ServerboundPlayerReactivateUnlockPacket::unlock, ServerboundPlayerReactivateUnlockPacket::new);
   }
}
