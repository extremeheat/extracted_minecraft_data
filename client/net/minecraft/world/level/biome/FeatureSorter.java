package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.Graph;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.apache.commons.lang3.mutable.MutableInt;

public class FeatureSorter {
   public FeatureSorter() {
      super();
   }

   public static <T> List<StepFeatureData> buildFeaturesPerStep(List<T> var0, Function<T, List<HolderSet<PlacedFeature>>> var1, boolean var2) {
      Object2IntOpenHashMap var3 = new Object2IntOpenHashMap();
      MutableInt var4 = new MutableInt(0);
      Comparator var5 = Comparator.comparingInt(1FeatureData::step).thenComparingInt(1FeatureData::featureIndex);
      TreeMap var6 = new TreeMap(var5);
      int var7 = 0;
      Iterator var8 = var0.iterator();

      ArrayList var10;
      int var12;

      record 1FeatureData(int featureIndex, int step, PlacedFeature feature) {
         _FeatureData/* $FF was: 1FeatureData*/(int var1, int var2, PlacedFeature var3) {
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
         Object var9 = var8.next();
         var10 = Lists.newArrayList();
         List var11 = (List)var1.apply(var9);
         var7 = Math.max(var7, var11.size());

         for(var12 = 0; var12 < var11.size(); ++var12) {
            Iterator var13 = ((HolderSet)var11.get(var12)).iterator();

            while(var13.hasNext()) {
               Holder var14 = (Holder)var13.next();
               PlacedFeature var15 = (PlacedFeature)var14.value();
               var10.add(new 1FeatureData(var3.computeIfAbsent(var15, (var1x) -> {
                  return var4.getAndIncrement();
               }), var12, var15));
            }
         }

         for(var12 = 0; var12 < var10.size(); ++var12) {
            Set var24 = (Set)var6.computeIfAbsent((1FeatureData)var10.get(var12), (var1x) -> {
               return new TreeSet(var5);
            });
            if (var12 < var10.size() - 1) {
               var24.add((1FeatureData)var10.get(var12 + 1));
            }
         }
      }

      TreeSet var19 = new TreeSet(var5);
      TreeSet var20 = new TreeSet(var5);
      var10 = Lists.newArrayList();
      Iterator var21 = var6.keySet().iterator();

      while(var21.hasNext()) {
         1FeatureData var23 = (1FeatureData)var21.next();
         if (!var20.isEmpty()) {
            throw new IllegalStateException("You somehow broke the universe; DFS bork (iteration finished with non-empty in-progress vertex set");
         }

         if (!var19.contains(var23)) {
            Objects.requireNonNull(var10);
            if (Graph.depthFirstSearch(var6, var19, var20, var10::add, var23)) {
               if (!var2) {
                  throw new IllegalStateException("Feature order cycle found");
               }

               ArrayList var25 = new ArrayList(var0);

               int var26;
               do {
                  var26 = var25.size();
                  ListIterator var28 = var25.listIterator();

                  while(var28.hasNext()) {
                     Object var16 = var28.next();
                     var28.remove();

                     try {
                        buildFeaturesPerStep(var25, var1, false);
                     } catch (IllegalStateException var18) {
                        continue;
                     }

                     var28.add(var16);
                  }
               } while(var26 != var25.size());

               throw new IllegalStateException("Feature order cycle found, involved sources: " + String.valueOf(var25));
            }
         }
      }

      Collections.reverse(var10);
      ImmutableList.Builder var22 = ImmutableList.builder();

      for(var12 = 0; var12 < var7; ++var12) {
         List var27 = (List)var10.stream().filter((var1x) -> {
            return var1x.step() == var12;
         }).map(1FeatureData::feature).collect(Collectors.toList());
         var22.add(new StepFeatureData(var27));
      }

      return var22.build();
   }

   public static record StepFeatureData(List<PlacedFeature> features, ToIntFunction<PlacedFeature> indexMapping) {
      StepFeatureData(List<PlacedFeature> var1) {
         this(var1, Util.createIndexIdentityLookup(var1));
      }

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
