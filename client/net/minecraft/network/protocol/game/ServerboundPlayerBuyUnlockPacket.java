package net.minecraft.network.protocol.game;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.server.players.PlayerUnlock;

public record ServerboundPlayerBuyUnlockPacket(Holder<PlayerUnlock> unlock) implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundPlayerBuyUnlockPacket> STREAM_CODEC;

   public ServerboundPlayerBuyUnlockPacket(Holder<PlayerUnlock> var1) {
      super();
      this.unlock = var1;
   }

   public PacketType<ServerboundPlayerBuyUnlockPacket> type() {
      return GamePacketTypes.SERVERBOUND_PLAYER_BUY_UNLOCK;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleBuyUnlock(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.holderRegistry(Registries.PLAYER_UNLOCK), ServerboundPlayerBuyUnlockPacket::unlock, ServerboundPlayerBuyUnlockPacket::new);
   }
}
