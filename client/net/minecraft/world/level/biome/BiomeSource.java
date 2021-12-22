package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.util.Graph;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.apache.commons.lang3.mutable.MutableInt;

public abstract class BiomeSource implements BiomeResolver {
   public static final Codec<BiomeSource> CODEC;
   private final Set<Biome> possibleBiomes;
   private final List<BiomeSource.StepFeatureData> featuresPerStep;

   protected BiomeSource(Stream<Supplier<Biome>> var1) {
      this((List)var1.map(Supplier::get).distinct().collect(ImmutableList.toImmutableList()));
   }

   protected BiomeSource(List<Biome> var1) {
      super();
      this.possibleBiomes = new ObjectLinkedOpenHashSet(var1);
      this.featuresPerStep = this.buildFeaturesPerStep(var1, true);
   }

   private List<BiomeSource.StepFeatureData> buildFeaturesPerStep(List<Biome> var1, boolean var2) {
      Object2IntOpenHashMap var3 = new Object2IntOpenHashMap();
      MutableInt var4 = new MutableInt(0);
      Comparator var5 = Comparator.comparingInt(InnerFeatureData::step).thenComparingInt(InnerFeatureData::featureIndex);
      TreeMap var6 = new TreeMap(var5);
      int var7 = 0;
      Iterator var8 = var1.iterator();

      ArrayList var10;
      int var12;

      record InnerFeatureData(int a, int b, PlacedFeature c) {
         private final int featureIndex;
         private final int step;
         private final PlacedFeature feature;

         InnerFeatureData(int var1, int var2, PlacedFeature var3) {
            super();
            this.featureIndex = var1;
            this.step = var2;
            this.feature = var3;
         }

         public int featureIndex() {
            return this.featureIndex;
         }

         public int step() {
            return this.step;
         }

         public PlacedFeature feature() {
            return this.feature;
         }
      }

      while(var8.hasNext()) {
         Biome var9 = (Biome)var8.next();
         var10 = Lists.newArrayList();
         List var11 = var9.getGenerationSettings().features();
         var7 = Math.max(var7, var11.size());

         for(var12 = 0; var12 < var11.size(); ++var12) {
            Iterator var13 = ((List)var11.get(var12)).iterator();

            while(var13.hasNext()) {
               Supplier var14 = (Supplier)var13.next();
               PlacedFeature var15 = (PlacedFeature)var14.get();
               var10.add(new InnerFeatureData(var3.computeIfAbsent(var15, (var1x) -> {
                  return var4.getAndIncrement();
               }), var12, var15));
            }
         }

         for(var12 = 0; var12 < var10.size(); ++var12) {
            Set var24 = (Set)var6.computeIfAbsent((InnerFeatureData)var10.get(var12), (var1x) -> {
               return new TreeSet(var5);
            });
            if (var12 < var10.size() - 1) {
               var24.add((InnerFeatureData)var10.get(var12 + 1));
            }
         }
      }

      TreeSet var19 = new TreeSet(var5);
      TreeSet var20 = new TreeSet(var5);
      var10 = Lists.newArrayList();
      Iterator var21 = var6.keySet().iterator();

      while(var21.hasNext()) {
         InnerFeatureData var23 = (InnerFeatureData)var21.next();
         if (!var20.isEmpty()) {
            throw new IllegalStateException("You somehow broke the universe; DFS bork (iteration finished with non-empty in-progress vertex set");
         }

         if (!var19.contains(var23)) {
            Objects.requireNonNull(var10);
            if (Graph.depthFirstSearch(var6, var19, var20, var10::add, var23)) {
               if (!var2) {
                  throw new IllegalStateException("Feature order cycle found");
               }

               ArrayList var25 = new ArrayList(var1);

               int var26;
               do {
                  var26 = var25.size();
                  ListIterator var28 = var25.listIterator();

                  while(var28.hasNext()) {
                     Biome var16 = (Biome)var28.next();
                     var28.remove();

                     try {
                        this.buildFeaturesPerStep(var25, false);
                     } catch (IllegalStateException var18) {
                        continue;
                     }

                     var28.add(var16);
                  }
               } while(var26 != var25.size());

               throw new IllegalStateException("Feature order cycle found, involved biomes: " + var25);
            }
         }
      }

      Collections.reverse(var10);
      Builder var22 = ImmutableList.builder();

      for(var12 = 0; var12 < var7; ++var12) {
         List var27 = (List)var10.stream().filter((var1x) -> {
            return var1x.step() == var12;
         }).map(InnerFeatureData::feature).collect(Collectors.toList());
         int var29 = var27.size();
         Object2IntOpenCustomHashMap var30 = new Object2IntOpenCustomHashMap(var29, Util.identityStrategy());

         for(int var17 = 0; var17 < var29; ++var17) {
            var30.put((PlacedFeature)var27.get(var17), var17);
         }

         var22.add(new BiomeSource.StepFeatureData(var27, var30));
      }

      return var22.build();
   }

   protected abstract Codec<? extends BiomeSource> codec();

   public abstract BiomeSource withSeed(long var1);

   public Set<Biome> possibleBiomes() {
      return this.possibleBiomes;
   }

   public Set<Biome> getBiomesWithin(int var1, int var2, int var3, int var4, Climate.Sampler var5) {
      int var6 = QuartPos.fromBlock(var1 - var4);
      int var7 = QuartPos.fromBlock(var2 - var4);
      int var8 = QuartPos.fromBlock(var3 - var4);
      int var9 = QuartPos.fromBlock(var1 + var4);
      int var10 = QuartPos.fromBlock(var2 + var4);
      int var11 = QuartPos.fromBlock(var3 + var4);
      int var12 = var9 - var6 + 1;
      int var13 = var10 - var7 + 1;
      int var14 = var11 - var8 + 1;
      HashSet var15 = Sets.newHashSet();

      for(int var16 = 0; var16 < var14; ++var16) {
         for(int var17 = 0; var17 < var12; ++var17) {
            for(int var18 = 0; var18 < var13; ++var18) {
               int var19 = var6 + var17;
               int var20 = var7 + var18;
               int var21 = var8 + var16;
               var15.add(this.getNoiseBiome(var19, var20, var21, var5));
            }
         }
      }

      return var15;
   }

   @Nullable
   public BlockPos findBiomeHorizontal(int var1, int var2, int var3, int var4, Predicate<Biome> var5, Random var6, Climate.Sampler var7) {
      return this.findBiomeHorizontal(var1, var2, var3, var4, 1, var5, var6, false, var7);
   }

   @Nullable
   public BlockPos findBiomeHorizontal(int var1, int var2, int var3, int var4, int var5, Predicate<Biome> var6, Random var7, boolean var8, Climate.Sampler var9) {
      int var10 = QuartPos.fromBlock(var1);
      int var11 = QuartPos.fromBlock(var3);
      int var12 = QuartPos.fromBlock(var4);
      int var13 = QuartPos.fromBlock(var2);
      BlockPos var14 = null;
      int var15 = 0;
      int var16 = var8 ? 0 : var12;

      for(int var17 = var16; var17 <= var12; var17 += var5) {
         for(int var18 = SharedConstants.debugGenerateSquareTerrainWithoutNoise ? 0 : -var17; var18 <= var17; var18 += var5) {
            boolean var19 = Math.abs(var18) == var17;

            for(int var20 = -var17; var20 <= var17; var20 += var5) {
               if (var8) {
                  boolean var21 = Math.abs(var20) == var17;
                  if (!var21 && !var19) {
                     continue;
                  }
               }

               int var23 = var10 + var20;
               int var22 = var11 + var18;
               if (var6.test(this.getNoiseBiome(var23, var13, var22, var9))) {
                  if (var14 == null || var7.nextInt(var15 + 1) == 0) {
                     var14 = new BlockPos(QuartPos.toBlock(var23), var2, QuartPos.toBlock(var22));
                     if (var8) {
                        return var14;
                     }
                  }

                  ++var15;
               }
            }
         }
      }

      return var14;
   }

   public abstract Biome getNoiseBiome(int var1, int var2, int var3, Climate.Sampler var4);

   public void addMultinoiseDebugInfo(List<String> var1, BlockPos var2, Climate.Sampler var3) {
   }

   public List<BiomeSource.StepFeatureData> featuresPerStep() {
      return this.featuresPerStep;
   }

   static {
      Registry.register(Registry.BIOME_SOURCE, (String)"fixed", FixedBiomeSource.CODEC);
      Registry.register(Registry.BIOME_SOURCE, (String)"multi_noise", MultiNoiseBiomeSource.CODEC);
      Registry.register(Registry.BIOME_SOURCE, (String)"checkerboard", CheckerboardColumnBiomeSource.CODEC);
      Registry.register(Registry.BIOME_SOURCE, (String)"the_end", TheEndBiomeSource.CODEC);
      CODEC = Registry.BIOME_SOURCE.byNameCodec().dispatchStable(BiomeSource::codec, Function.identity());
   }

   public static record StepFeatureData(List<PlacedFeature> a, ToIntFunction<PlacedFeature> b) {
      private final List<PlacedFeature> features;
      private final ToIntFunction<PlacedFeature> indexMapping;

      public StepFeatureData(List<PlacedFeature> var1, ToIntFunction<PlacedFeature> var2) {
         super();
         this.features = var1;
         this.indexMapping = var2;
      }

      public List<PlacedFeature> features() {
         return this.features;
      }

      public ToIntFunction<PlacedFeature> indexMapping() {
         return this.indexMapping;
      }
   }
}
