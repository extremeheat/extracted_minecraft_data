package net.minecraft.network.protocol.game;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public record CommonPlayerSpawnInfo(
   Holder<DimensionType> dimensionType,
   ResourceKey<Level> dimension,
   long seed,
   GameType gameType,
   @Nullable GameType previousGameType,
   boolean isDebug,
   boolean isFlat,
   Optional<GlobalPos> lastDeathLocation,
   int portalCooldown
) {
   public CommonPlayerSpawnInfo(RegistryFriendlyByteBuf var1) {
      this(
         DimensionType.STREAM_CODEC.decode(var1),
         var1.readResourceKey(Registries.DIMENSION),
         var1.readLong(),
         GameType.byId(var1.readByte()),
         GameType.byNullableId(var1.readByte()),
         var1.readBoolean(),
         var1.readBoolean(),
         var1.readOptional(FriendlyByteBuf::readGlobalPos),
         var1.readVarInt()
      );
   }

   public CommonPlayerSpawnInfo(
      Holder<DimensionType> dimensionType,
      ResourceKey<Level> dimension,
      long seed,
      GameType gameType,
      @Nullable GameType previousGameType,
      boolean isDebug,
      boolean isFlat,
      Optional<GlobalPos> lastDeathLocation,
      int portalCooldown
   ) {
      super();
      this.dimensionType = dimensionType;
      this.dimension = dimension;
      this.seed = seed;
      this.gameType = gameType;
      this.previousGameType = previousGameType;
      this.isDebug = isDebug;
      this.isFlat = isFlat;
      this.lastDeathLocation = lastDeathLocation;
      this.portalCooldown = portalCooldown;
   }

   public void write(RegistryFriendlyByteBuf var1) {
      DimensionType.STREAM_CODEC.encode(var1, this.dimensionType);
      var1.writeResourceKey(this.dimension);
      var1.writeLong(this.seed);
      var1.writeByte(this.gameType.getId());
      var1.writeByte(GameType.getNullableId(this.previousGameType));
      var1.writeBoolean(this.isDebug);
      var1.writeBoolean(this.isFlat);
      var1.writeOptional(this.lastDeathLocation, FriendlyByteBuf::writeGlobalPos);
      var1.writeVarInt(this.portalCooldown);
   }
}
