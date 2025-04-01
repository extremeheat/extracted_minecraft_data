package net.minecraft.network.protocol.game;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.server.players.PlayerUnlock;

public record ClientboundUpdatePlayerUnlocksPacket(int id, boolean reset, Map<Holder<PlayerUnlock>, Boolean> isActiveExclusive, Map<Holder<PlayerUnlock>, PlayerUnlock.UnlockVisibility> visibility, Map<Holder<PlayerUnlock>, Boolean> obtained) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdatePlayerUnlocksPacket> STREAM_CODEC;

   public ClientboundUpdatePlayerUnlocksPacket(int var1, boolean var2, Map<Holder<PlayerUnlock>, Boolean> var3, Map<Holder<PlayerUnlock>, PlayerUnlock.UnlockVisibility> var4, Map<Holder<PlayerUnlock>, Boolean> var5) {
      super();
      this.id = var1;
      this.reset = var2;
      this.isActiveExclusive = var3;
      this.visibility = var4;
      this.obtained = var5;
   }

   public PacketType<ClientboundUpdatePlayerUnlocksPacket> type() {
      return GamePacketTypes.CLIENTBOUND_UPDATE_PLAYER_UNLOCKS;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleUpdatePlayerUnlocksPacket(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, ClientboundUpdatePlayerUnlocksPacket::id, ByteBufCodecs.BOOL, ClientboundUpdatePlayerUnlocksPacket::reset, ByteBufCodecs.map(HashMap::new, ByteBufCodecs.holderRegistry(Registries.PLAYER_UNLOCK), ByteBufCodecs.BOOL), ClientboundUpdatePlayerUnlocksPacket::isActiveExclusive, ByteBufCodecs.map(HashMap::new, ByteBufCodecs.holderRegistry(Registries.PLAYER_UNLOCK), PlayerUnlock.UnlockVisibility.STREAM_CODEC), ClientboundUpdatePlayerUnlocksPacket::visibility, ByteBufCodecs.map(HashMap::new, ByteBufCodecs.holderRegistry(Registries.PLAYER_UNLOCK), ByteBufCodecs.BOOL), ClientboundUpdatePlayerUnlocksPacket::obtained, ClientboundUpdatePlayerUnlocksPacket::new);
   }
}
