package net.minecraft.network.protocol.game;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public record CommonPlayerSpawnInfo(
   ResourceKey<DimensionType> a, ResourceKey<Level> b, long c, GameType d, @Nullable GameType e, boolean f, boolean g, Optional<GlobalPos> h, int i
) {
   private final ResourceKey<DimensionType> dimensionType;
   private final ResourceKey<Level> dimension;
   private final long seed;
   private final GameType gameType;
   @Nullable
   private final GameType previousGameType;
   private final boolean isDebug;
   private final boolean isFlat;
   private final Optional<GlobalPos> lastDeathLocation;
   private final int portalCooldown;

   public CommonPlayerSpawnInfo(FriendlyByteBuf var1) {
      this(
         var1.readResourceKey(Registries.DIMENSION_TYPE),
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
      ResourceKey<DimensionType> var1,
      ResourceKey<Level> var2,
      long var3,
      GameType var5,
      @Nullable GameType var6,
      boolean var7,
      boolean var8,
      Optional<GlobalPos> var9,
      int var10
   ) {
      super();
      this.dimensionType = var1;
      this.dimension = var2;
      this.seed = var3;
      this.gameType = var5;
      this.previousGameType = var6;
      this.isDebug = var7;
      this.isFlat = var8;
      this.lastDeathLocation = var9;
      this.portalCooldown = var10;
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeResourceKey(this.dimensionType);
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
