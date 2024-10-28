package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;

public record WorldGenSettings(WorldOptions options, WorldDimensions dimensions) {
   public static final Codec<WorldGenSettings> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(WorldOptions.CODEC.forGetter(WorldGenSettings::options), WorldDimensions.CODEC.forGetter(WorldGenSettings::dimensions)).apply(var0, var0.stable(WorldGenSettings::new));
   });

   public WorldGenSettings(WorldOptions var1, WorldDimensions var2) {
      super();
      this.options = var1;
      this.dimensions = var2;
   }

   public static <T> DataResult<T> encode(DynamicOps<T> var0, WorldOptions var1, WorldDimensions var2) {
      return CODEC.encodeStart(var0, new WorldGenSettings(var1, var2));
   }

   public static <T> DataResult<T> encode(DynamicOps<T> var0, WorldOptions var1, RegistryAccess var2) {
      return encode(var0, var1, new WorldDimensions(var2.registryOrThrow(Registries.LEVEL_STEM)));
   }

   public WorldOptions options() {
      return this.options;
   }

   public WorldDimensions dimensions() {
      return this.dimensions;
   }
}
