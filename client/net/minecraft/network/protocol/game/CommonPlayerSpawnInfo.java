package net.minecraft.network.protocol.game;

import java.util.Optional;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public record CommonPlayerSpawnInfo(Holder<DimensionType> dimensionType, ResourceKey<Level> dimension, long seed, GameType gameType, Optional<GameType> previousGameType, boolean isDebug, boolean isFlat, Optional<GlobalPos> lastDeathLocation, int portalCooldown, int seaLevel) {
   public static final StreamCodec<RegistryFriendlyByteBuf, CommonPlayerSpawnInfo> STREAM_CODEC;

   public CommonPlayerSpawnInfo {
      super();
   }

   static {
      STREAM_CODEC = StreamCodec.composite(DimensionType.STREAM_CODEC, CommonPlayerSpawnInfo::dimensionType, ResourceKey.streamCodec(Registries.DIMENSION), CommonPlayerSpawnInfo::dimension, ByteBufCodecs.LONG, CommonPlayerSpawnInfo::seed, GameType.STREAM_CODEC, CommonPlayerSpawnInfo::gameType, GameType.OPTIONAL_STREAM_CODEC, CommonPlayerSpawnInfo::previousGameType, ByteBufCodecs.BOOL, CommonPlayerSpawnInfo::isDebug, ByteBufCodecs.BOOL, CommonPlayerSpawnInfo::isFlat, GlobalPos.STREAM_CODEC.apply(ByteBufCodecs::optional), CommonPlayerSpawnInfo::lastDeathLocation, ByteBufCodecs.VAR_INT, CommonPlayerSpawnInfo::portalCooldown, ByteBufCodecs.VAR_INT, CommonPlayerSpawnInfo::seaLevel, CommonPlayerSpawnInfo::new);
   }
}
