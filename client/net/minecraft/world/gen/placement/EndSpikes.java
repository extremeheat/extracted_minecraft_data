package net.minecraft.world.gen.placement;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.EndCrystalTowerFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class EndSpikes extends BasePlacement<NoPlacementConfig> {
   private static final LoadingCache<Long, EndCrystalTowerFeature.EndSpike[]> field_202467_a;

   public EndSpikes() {
      super();
   }

   public <C extends IFeatureConfig> boolean func_201491_a_(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, NoPlacementConfig var5, Feature<C> var6, C var7) {
      EndCrystalTowerFeature.EndSpike[] var8 = func_202466_a(var1);
      boolean var9 = false;
      EndCrystalTowerFeature.EndSpike[] var10 = var8;
      int var11 = var8.length;

      for(int var12 = 0; var12 < var11; ++var12) {
         EndCrystalTowerFeature.EndSpike var13 = var10[var12];
         if (var13.func_186154_a(var4)) {
            ((EndCrystalTowerFeature)var6).func_186143_a(var13);
            var9 |= ((EndCrystalTowerFeature)var6).func_212245_a(var1, var2, var3, new BlockPos(var13.func_186151_a(), 45, var13.func_186152_b()), IFeatureConfig.field_202429_e);
         }
      }

      return var9;
   }

   public static EndCrystalTowerFeature.EndSpike[] func_202466_a(IWorld var0) {
      Random var1 = new Random(var0.func_72905_C());
      long var2 = var1.nextLong() & 65535L;
      return (EndCrystalTowerFeature.EndSpike[])field_202467_a.getUnchecked(var2);
   }

   static {
      field_202467_a = CacheBuilder.newBuilder().expireAfterWrite(5L, TimeUnit.MINUTES).build(new EndSpikes.CacheLoader());
   }

   static class CacheLoader extends com.google.common.cache.CacheLoader<Long, EndCrystalTowerFeature.EndSpike[]> {
      private CacheLoader() {
         super();
      }

      public EndCrystalTowerFeature.EndSpike[] load(Long var1) throws Exception {
         ArrayList var2 = Lists.newArrayList(ContiguousSet.create(Range.closedOpen(0, 10), DiscreteDomain.integers()));
         Collections.shuffle(var2, new Random(var1));
         EndCrystalTowerFeature.EndSpike[] var3 = new EndCrystalTowerFeature.EndSpike[10];

         for(int var4 = 0; var4 < 10; ++var4) {
            int var5 = (int)(42.0D * Math.cos(2.0D * (-3.141592653589793D + 0.3141592653589793D * (double)var4)));
            int var6 = (int)(42.0D * Math.sin(2.0D * (-3.141592653589793D + 0.3141592653589793D * (double)var4)));
            int var7 = (Integer)var2.get(var4);
            int var8 = 2 + var7 / 3;
            int var9 = 76 + var7 * 3;
            boolean var10 = var7 == 1 || var7 == 2;
            var3[var4] = new EndCrystalTowerFeature.EndSpike(var5, var6, var8, var9, var10);
         }

         return var3;
      }

      // $FF: synthetic method
      public Object load(Object var1) throws Exception {
         return this.load((Long)var1);
      }

      // $FF: synthetic method
      CacheLoader(Object var1) {
         this();
      }
   }
}
