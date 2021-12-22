package net.minecraft.world.level.dimension;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public final class LevelStem {
   public static final Codec<LevelStem> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(DimensionType.CODEC.fieldOf("type").flatXmap(ExtraCodecs.nonNullSupplierCheck(), ExtraCodecs.nonNullSupplierCheck()).forGetter(LevelStem::typeSupplier), ChunkGenerator.CODEC.fieldOf("generator").forGetter(LevelStem::generator)).apply(var0, var0.stable(LevelStem::new));
   });
   public static final ResourceKey<LevelStem> OVERWORLD;
   public static final ResourceKey<LevelStem> NETHER;
   public static final ResourceKey<LevelStem> END;
   private static final Set<ResourceKey<LevelStem>> BUILTIN_ORDER;
   private final Supplier<DimensionType> type;
   private final ChunkGenerator generator;

   public LevelStem(Supplier<DimensionType> var1, ChunkGenerator var2) {
      super();
      this.type = var1;
      this.generator = var2;
   }

   public Supplier<DimensionType> typeSupplier() {
      return this.type;
   }

   public DimensionType type() {
      return (DimensionType)this.type.get();
   }

   public ChunkGenerator generator() {
      return this.generator;
   }

   public static MappedRegistry<LevelStem> sortMap(MappedRegistry<LevelStem> var0) {
      MappedRegistry var1 = new MappedRegistry(Registry.LEVEL_STEM_REGISTRY, Lifecycle.experimental());
      Iterator var2 = BUILTIN_ORDER.iterator();

      while(var2.hasNext()) {
         ResourceKey var3 = (ResourceKey)var2.next();
         LevelStem var4 = (LevelStem)var0.get(var3);
         if (var4 != null) {
            var1.register(var3, var4, var0.lifecycle(var4));
         }
      }

      var2 = var0.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var5 = (Entry)var2.next();
         ResourceKey var6 = (ResourceKey)var5.getKey();
         if (!BUILTIN_ORDER.contains(var6)) {
            var1.register(var6, (LevelStem)var5.getValue(), var0.lifecycle((LevelStem)var5.getValue()));
         }
      }

      return var1;
   }

   public static boolean stable(long var0, MappedRegistry<LevelStem> var2) {
      ArrayList var3 = Lists.newArrayList(var2.entrySet());
      if (var3.size() != BUILTIN_ORDER.size()) {
         return false;
      } else {
         Entry var4 = (Entry)var3.get(0);
         Entry var5 = (Entry)var3.get(1);
         Entry var6 = (Entry)var3.get(2);
         if (var4.getKey() == OVERWORLD && var5.getKey() == NETHER && var6.getKey() == END) {
            if (!((LevelStem)var4.getValue()).type().equalTo(DimensionType.DEFAULT_OVERWORLD) && ((LevelStem)var4.getValue()).type() != DimensionType.DEFAULT_OVERWORLD_CAVES) {
               return false;
            } else if (!((LevelStem)var5.getValue()).type().equalTo(DimensionType.DEFAULT_NETHER)) {
               return false;
            } else if (!((LevelStem)var6.getValue()).type().equalTo(DimensionType.DEFAULT_END)) {
               return false;
            } else if (((LevelStem)var5.getValue()).generator() instanceof NoiseBasedChunkGenerator && ((LevelStem)var6.getValue()).generator() instanceof NoiseBasedChunkGenerator) {
               NoiseBasedChunkGenerator var7 = (NoiseBasedChunkGenerator)((LevelStem)var5.getValue()).generator();
               NoiseBasedChunkGenerator var8 = (NoiseBasedChunkGenerator)((LevelStem)var6.getValue()).generator();
               if (!var7.stable(var0, NoiseGeneratorSettings.NETHER)) {
                  return false;
               } else if (!var8.stable(var0, NoiseGeneratorSettings.END)) {
                  return false;
               } else if (!(var7.getBiomeSource() instanceof MultiNoiseBiomeSource)) {
                  return false;
               } else {
                  MultiNoiseBiomeSource var9 = (MultiNoiseBiomeSource)var7.getBiomeSource();
                  if (!var9.stable(MultiNoiseBiomeSource.Preset.NETHER)) {
                     return false;
                  } else {
                     BiomeSource var10 = ((LevelStem)var4.getValue()).generator().getBiomeSource();
                     if (var10 instanceof MultiNoiseBiomeSource && !((MultiNoiseBiomeSource)var10).stable(MultiNoiseBiomeSource.Preset.OVERWORLD)) {
                        return false;
                     } else if (!(var8.getBiomeSource() instanceof TheEndBiomeSource)) {
                        return false;
                     } else {
                        TheEndBiomeSource var11 = (TheEndBiomeSource)var8.getBiomeSource();
                        return var11.stable(var0);
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

   static {
      OVERWORLD = ResourceKey.create(Registry.LEVEL_STEM_REGISTRY, new ResourceLocation("overworld"));
      NETHER = ResourceKey.create(Registry.LEVEL_STEM_REGISTRY, new ResourceLocation("the_nether"));
      END = ResourceKey.create(Registry.LEVEL_STEM_REGISTRY, new ResourceLocation("the_end"));
      BUILTIN_ORDER = Sets.newLinkedHashSet(ImmutableList.of(OVERWORLD, NETHER, END));
   }
}
