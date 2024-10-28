package net.minecraft.network.protocol.game;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public record CommonPlayerSpawnInfo(Holder<DimensionType> dimensionType, ResourceKey<Level> dimension, long seed, GameType gameType, @Nullable GameType previousGameType, boolean isDebug, boolean isFlat, Optional<GlobalPos> lastDeathLocation, int portalCooldown) {
   private static final StreamCodec<RegistryFriendlyByteBuf, Holder<DimensionType>> DIMENSION_TYPE_ID_STREAM_CODEC;

   public CommonPlayerSpawnInfo(RegistryFriendlyByteBuf var1) {
      this((Holder)DIMENSION_TYPE_ID_STREAM_CODEC.decode(var1), var1.readResourceKey(Registries.DIMENSION), var1.readLong(), GameType.byId(var1.readByte()), GameType.byNullableId(var1.readByte()), var1.readBoolean(), var1.readBoolean(), var1.readOptional(FriendlyByteBuf::readGlobalPos), var1.readVarInt());
   }

   public CommonPlayerSpawnInfo(Holder<DimensionType> var1, ResourceKey<Level> var2, long var3, GameType var5, @Nullable GameType var6, boolean var7, boolean var8, Optional<GlobalPos> var9, int var10) {
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

   public void write(RegistryFriendlyByteBuf var1) {
      DIMENSION_TYPE_ID_STREAM_CODEC.encode(var1, this.dimensionType);
      var1.writeResourceKey(this.dimension);
      var1.writeLong(this.seed);
      var1.writeByte(this.gameType.getId());
      var1.writeByte(GameType.getNullableId(this.previousGameType));
      var1.writeBoolean(this.isDebug);
      var1.writeBoolean(this.isFlat);
      var1.writeOptional(this.lastDeathLocation, FriendlyByteBuf::writeGlobalPos);
      var1.writeVarInt(this.portalCooldown);
   }

   public Holder<DimensionType> dimensionType() {
      return this.dimensionType;
   }

   public ResourceKey<Level> dimension() {
      return this.dimension;
   }

   public long seed() {
      return this.seed;
   }

   public GameType gameType() {
      return this.gameType;
   }

   @Nullable
   public GameType previousGameType() {
      return this.previousGameType;
   }

   public boolean isDebug() {
      return this.isDebug;
   }

   public boolean isFlat() {
      return this.isFlat;
   }

   public Optional<GlobalPos> lastDeathLocation() {
      return this.lastDeathLocation;
   }

   public int portalCooldown() {
      return this.portalCooldown;
   }

   static {
      DIMENSION_TYPE_ID_STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.DIMENSION_TYPE);
   }
}
