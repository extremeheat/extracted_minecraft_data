package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;

public record WorldGenSettings(WorldOptions options, WorldDimensions dimensions) {
   public static final Codec<WorldGenSettings> CODEC = RecordCodecBuilder.create((i) -> i.group(WorldOptions.CODEC.forGetter(WorldGenSettings::options), WorldDimensions.CODEC.forGetter(WorldGenSettings::dimensions)).apply(i, i.stable(WorldGenSettings::new)));

   public WorldGenSettings {
      super();
   }

   public static <T> DataResult<T> encode(final DynamicOps<T> ops, final WorldOptions options, final WorldDimensions dimensions) {
      return CODEC.encodeStart(ops, new WorldGenSettings(options, dimensions));
   }

   public static <T> DataResult<T> encode(final DynamicOps<T> ops, final WorldOptions options, final RegistryAccess registryAccess) {
      return encode(ops, options, new WorldDimensions(registryAccess.lookupOrThrow(Registries.LEVEL_STEM)));
   }
}
