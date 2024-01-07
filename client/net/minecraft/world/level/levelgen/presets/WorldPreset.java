package net.minecraft.world.level.levelgen.presets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;

public class WorldPreset {
   public static final Codec<WorldPreset> DIRECT_CODEC = ExtraCodecs.validate(
      RecordCodecBuilder.create(
         var0 -> var0.group(
                  Codec.unboundedMap(ResourceKey.codec(Registries.LEVEL_STEM), LevelStem.CODEC).fieldOf("dimensions").forGetter(var0x -> var0x.dimensions)
               )
               .apply(var0, WorldPreset::new)
      ),
      WorldPreset::requireOverworld
   );
   public static final Codec<Holder<WorldPreset>> CODEC = RegistryFileCodec.create(Registries.WORLD_PRESET, DIRECT_CODEC);
   private final Map<ResourceKey<LevelStem>, LevelStem> dimensions;

   public WorldPreset(Map<ResourceKey<LevelStem>, LevelStem> var1) {
      super();
      this.dimensions = var1;
   }

   private Registry<LevelStem> createRegistry() {
      MappedRegistry var1 = new MappedRegistry<>(Registries.LEVEL_STEM, Lifecycle.experimental());
      WorldDimensions.keysInOrder(this.dimensions.keySet().stream()).forEach(var2 -> {
         LevelStem var3 = this.dimensions.get(var2);
         if (var3 != null) {
            var1.register(var2, var3, Lifecycle.stable());
         }
      });
      return var1.freeze();
   }

   public WorldDimensions createWorldDimensions() {
      return new WorldDimensions(this.createRegistry());
   }

   public Optional<LevelStem> overworld() {
      return Optional.ofNullable(this.dimensions.get(LevelStem.OVERWORLD));
   }

   private static DataResult<WorldPreset> requireOverworld(WorldPreset var0) {
      return var0.overworld().isEmpty() ? DataResult.error(() -> "Missing overworld dimension") : DataResult.success(var0, Lifecycle.stable());
   }
}
