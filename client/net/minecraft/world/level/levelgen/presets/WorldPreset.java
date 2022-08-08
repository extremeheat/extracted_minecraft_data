package net.minecraft.world.level.levelgen.presets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldGenSettings;

public class WorldPreset {
   public static final Codec<WorldPreset> DIRECT_CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.unboundedMap(ResourceKey.codec(Registry.LEVEL_STEM_REGISTRY), LevelStem.CODEC).fieldOf("dimensions").forGetter((var0x) -> {
         return var0x.dimensions;
      })).apply(var0, WorldPreset::new);
   }).flatXmap(WorldPreset::requireOverworld, WorldPreset::requireOverworld);
   public static final Codec<Holder<WorldPreset>> CODEC;
   private final Map<ResourceKey<LevelStem>, LevelStem> dimensions;

   public WorldPreset(Map<ResourceKey<LevelStem>, LevelStem> var1) {
      super();
      this.dimensions = var1;
   }

   private Registry<LevelStem> createRegistry() {
      MappedRegistry var1 = new MappedRegistry(Registry.LEVEL_STEM_REGISTRY, Lifecycle.experimental(), (Function)null);
      LevelStem.keysInOrder(this.dimensions.keySet().stream()).forEach((var2) -> {
         LevelStem var3 = (LevelStem)this.dimensions.get(var2);
         if (var3 != null) {
            var1.register(var2, var3, Lifecycle.stable());
         }

      });
      return var1.freeze();
   }

   public WorldGenSettings createWorldGenSettings(long var1, boolean var3, boolean var4) {
      return new WorldGenSettings(var1, var3, var4, this.createRegistry());
   }

   public WorldGenSettings recreateWorldGenSettings(WorldGenSettings var1) {
      return this.createWorldGenSettings(var1.seed(), var1.generateStructures(), var1.generateBonusChest());
   }

   public Optional<LevelStem> overworld() {
      return Optional.ofNullable((LevelStem)this.dimensions.get(LevelStem.OVERWORLD));
   }

   public LevelStem overworldOrThrow() {
      return (LevelStem)this.overworld().orElseThrow(() -> {
         return new IllegalStateException("Can't find overworld in this preset");
      });
   }

   private static DataResult<WorldPreset> requireOverworld(WorldPreset var0) {
      return var0.overworld().isEmpty() ? DataResult.error("Missing overworld dimension") : DataResult.success(var0, Lifecycle.stable());
   }

   static {
      CODEC = RegistryFileCodec.create(Registry.WORLD_PRESET_REGISTRY, DIRECT_CODEC);
   }
}
