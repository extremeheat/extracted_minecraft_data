package net.minecraft.world.level.dimension;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public final class LevelStem {
   public static final Codec<LevelStem> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               DimensionType.CODEC.fieldOf("type").forGetter(LevelStem::typeHolder), ChunkGenerator.CODEC.fieldOf("generator").forGetter(LevelStem::generator)
            )
            .apply(var0, var0.stable(LevelStem::new))
   );
   public static final ResourceKey<LevelStem> OVERWORLD = ResourceKey.create(Registry.LEVEL_STEM_REGISTRY, new ResourceLocation("overworld"));
   public static final ResourceKey<LevelStem> NETHER = ResourceKey.create(Registry.LEVEL_STEM_REGISTRY, new ResourceLocation("the_nether"));
   public static final ResourceKey<LevelStem> END = ResourceKey.create(Registry.LEVEL_STEM_REGISTRY, new ResourceLocation("the_end"));
   private static final Set<ResourceKey<LevelStem>> BUILTIN_ORDER = ImmutableSet.of(OVERWORLD, NETHER, END);
   private final Holder<DimensionType> type;
   private final ChunkGenerator generator;

   public LevelStem(Holder<DimensionType> var1, ChunkGenerator var2) {
      super();
      this.type = var1;
      this.generator = var2;
   }

   public Holder<DimensionType> typeHolder() {
      return this.type;
   }

   public ChunkGenerator generator() {
      return this.generator;
   }

   public static Stream<ResourceKey<LevelStem>> keysInOrder(Stream<ResourceKey<LevelStem>> var0) {
      return Stream.concat(BUILTIN_ORDER.stream(), var0.filter(var0x -> !BUILTIN_ORDER.contains(var0x)));
   }

   public static Registry<LevelStem> sortMap(Registry<LevelStem> var0) {
      MappedRegistry var1 = new MappedRegistry<>(Registry.LEVEL_STEM_REGISTRY, Lifecycle.experimental(), null);
      keysInOrder(var0.registryKeySet().stream()).forEach(var2 -> {
         LevelStem var3 = var0.get(var2);
         if (var3 != null) {
            var1.register(var2, var3, var0.lifecycle(var3));
         }
      });
      return var1;
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static boolean stable(Registry<LevelStem> var0) {
      if (var0.size() != BUILTIN_ORDER.size()) {
         return false;
      } else {
         Optional var1 = var0.getOptional(OVERWORLD);
         Optional var2 = var0.getOptional(NETHER);
         Optional var3 = var0.getOptional(END);
         if (!var1.isEmpty() && !var2.isEmpty() && !var3.isEmpty()) {
            if (!((LevelStem)var1.get()).typeHolder().is(BuiltinDimensionTypes.OVERWORLD)
               && !((LevelStem)var1.get()).typeHolder().is(BuiltinDimensionTypes.OVERWORLD_CAVES)) {
               return false;
            } else if (!((LevelStem)var2.get()).typeHolder().is(BuiltinDimensionTypes.NETHER)) {
               return false;
            } else if (!((LevelStem)var3.get()).typeHolder().is(BuiltinDimensionTypes.END)) {
               return false;
            } else if (((LevelStem)var2.get()).generator() instanceof NoiseBasedChunkGenerator var4
               && ((LevelStem)var3.get()).generator() instanceof NoiseBasedChunkGenerator var5) {
               if (!var4.stable(NoiseGeneratorSettings.NETHER)) {
                  return false;
               } else if (!var5.stable(NoiseGeneratorSettings.END)) {
                  return false;
               } else if (!(var4.getBiomeSource() instanceof MultiNoiseBiomeSource)) {
                  return false;
               } else {
                  MultiNoiseBiomeSource var6 = (MultiNoiseBiomeSource)var4.getBiomeSource();
                  if (!var6.stable(MultiNoiseBiomeSource.Preset.NETHER)) {
                     return false;
                  } else {
                     BiomeSource var7 = ((LevelStem)var1.get()).generator().getBiomeSource();
                     if (var7 instanceof MultiNoiseBiomeSource && !((MultiNoiseBiomeSource)var7).stable(MultiNoiseBiomeSource.Preset.OVERWORLD)) {
                        return false;
                     } else {
                        return var5.getBiomeSource() instanceof TheEndBiomeSource;
                     }
                  }
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }
}
