package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

public abstract class BiomeSource implements BiomeManager.NoiseBiomeSource {
   public static final Codec<BiomeSource> CODEC;
   protected final Map<StructureFeature<?>, Boolean> supportedStructures;
   protected final Set<BlockState> surfaceBlocks;
   protected final List<Biome> possibleBiomes;

   protected BiomeSource(Stream<Supplier<Biome>> var1) {
      this((List)var1.map(Supplier::get).collect(ImmutableList.toImmutableList()));
   }

   protected BiomeSource(List<Biome> var1) {
      super();
      this.supportedStructures = Maps.newHashMap();
      this.surfaceBlocks = Sets.newHashSet();
      this.possibleBiomes = var1;
   }

   protected abstract Codec<? extends BiomeSource> codec();

   public abstract BiomeSource withSeed(long var1);

   public List<Biome> possibleBiomes() {
      return this.possibleBiomes;
   }

   public Set<Biome> getBiomesWithin(int var1, int var2, int var3, int var4) {
      int var5 = var1 - var4 >> 2;
      int var6 = var2 - var4 >> 2;
      int var7 = var3 - var4 >> 2;
      int var8 = var1 + var4 >> 2;
      int var9 = var2 + var4 >> 2;
      int var10 = var3 + var4 >> 2;
      int var11 = var8 - var5 + 1;
      int var12 = var9 - var6 + 1;
      int var13 = var10 - var7 + 1;
      HashSet var14 = Sets.newHashSet();

      for(int var15 = 0; var15 < var13; ++var15) {
         for(int var16 = 0; var16 < var11; ++var16) {
            for(int var17 = 0; var17 < var12; ++var17) {
               int var18 = var5 + var16;
               int var19 = var6 + var17;
               int var20 = var7 + var15;
               var14.add(this.getNoiseBiome(var18, var19, var20));
            }
         }
      }

      return var14;
   }

   @Nullable
   public BlockPos findBiomeHorizontal(int var1, int var2, int var3, int var4, Predicate<Biome> var5, Random var6) {
      return this.findBiomeHorizontal(var1, var2, var3, var4, 1, var5, var6, false);
   }

   @Nullable
   public BlockPos findBiomeHorizontal(int var1, int var2, int var3, int var4, int var5, Predicate<Biome> var6, Random var7, boolean var8) {
      int var9 = var1 >> 2;
      int var10 = var3 >> 2;
      int var11 = var4 >> 2;
      int var12 = var2 >> 2;
      BlockPos var13 = null;
      int var14 = 0;
      int var15 = var8 ? 0 : var11;

      for(int var16 = var15; var16 <= var11; var16 += var5) {
         for(int var17 = -var16; var17 <= var16; var17 += var5) {
            boolean var18 = Math.abs(var17) == var16;

            for(int var19 = -var16; var19 <= var16; var19 += var5) {
               if (var8) {
                  boolean var20 = Math.abs(var19) == var16;
                  if (!var20 && !var18) {
                     continue;
                  }
               }

               int var22 = var9 + var19;
               int var21 = var10 + var17;
               if (var6.test(this.getNoiseBiome(var22, var12, var21))) {
                  if (var13 == null || var7.nextInt(var14 + 1) == 0) {
                     var13 = new BlockPos(var22 << 2, var2, var21 << 2);
                     if (var8) {
                        return var13;
                     }
                  }

                  ++var14;
               }
            }
         }
      }

      return var13;
   }

   public boolean canGenerateStructure(StructureFeature<?> var1) {
      return (Boolean)this.supportedStructures.computeIfAbsent(var1, (var1x) -> {
         return this.possibleBiomes.stream().anyMatch((var1) -> {
            return var1.getGenerationSettings().isValidStart(var1x);
         });
      });
   }

   public Set<BlockState> getSurfaceBlocks() {
      if (this.surfaceBlocks.isEmpty()) {
         Iterator var1 = this.possibleBiomes.iterator();

         while(var1.hasNext()) {
            Biome var2 = (Biome)var1.next();
            this.surfaceBlocks.add(var2.getGenerationSettings().getSurfaceBuilderConfig().getTopMaterial());
         }
      }

      return this.surfaceBlocks;
   }

   static {
      Registry.register(Registry.BIOME_SOURCE, (String)"fixed", FixedBiomeSource.CODEC);
      Registry.register(Registry.BIOME_SOURCE, (String)"multi_noise", MultiNoiseBiomeSource.CODEC);
      Registry.register(Registry.BIOME_SOURCE, (String)"checkerboard", CheckerboardColumnBiomeSource.CODEC);
      Registry.register(Registry.BIOME_SOURCE, (String)"vanilla_layered", OverworldBiomeSource.CODEC);
      Registry.register(Registry.BIOME_SOURCE, (String)"the_end", TheEndBiomeSource.CODEC);
      CODEC = Registry.BIOME_SOURCE.dispatchStable(BiomeSource::codec, Function.identity());
   }
}
