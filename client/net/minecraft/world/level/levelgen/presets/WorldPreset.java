package net.minecraft.world.level.levelgen.presets;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;

public class WorldPreset {
   public static final Codec<WorldPreset> DIRECT_CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.unboundedMap(ResourceKey.codec(Registries.LEVEL_STEM), LevelStem.CODEC).fieldOf("dimensions").forGetter((var0x) -> {
         return var0x.dimensions;
      })).apply(var0, WorldPreset::new);
   }).validate(WorldPreset::requireOverworld);
   public static final Codec<Holder<WorldPreset>> CODEC;
   private final Map<ResourceKey<LevelStem>, LevelStem> dimensions;

   public WorldPreset(Map<ResourceKey<LevelStem>, LevelStem> var1) {
      super();
      this.dimensions = var1;
   }

   private ImmutableMap<ResourceKey<LevelStem>, LevelStem> dimensionsInOrder() {
      ImmutableMap.Builder var1 = ImmutableMap.builder();
      WorldDimensions.keysInOrder(this.dimensions.keySet().stream()).forEach((var2) -> {
         LevelStem var3 = (LevelStem)this.dimensions.get(var2);
         if (var3 != null) {
            var1.put(var2, var3);
         }

      });
      return var1.build();
   }

   public WorldDimensions createWorldDimensions() {
      return new WorldDimensions(this.dimensionsInOrder());
   }

   public Optional<LevelStem> overworld() {
      return Optional.ofNullable((LevelStem)this.dimensions.get(LevelStem.OVERWORLD));
   }

   private static DataResult<WorldPreset> requireOverworld(WorldPreset var0) {
      return var0.overworld().isEmpty() ? DataResult.error(() -> {
         return "Missing overworld dimension";
      }) : DataResult.success(var0, Lifecycle.stable());
   }

   static {
      CODEC = RegistryFileCodec.create(Registries.WORLD_PRESET, DIRECT_CODEC);
   }
}
