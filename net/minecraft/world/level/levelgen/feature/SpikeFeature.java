package net.minecraft.world.level.levelgen.feature;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.SpikeConfiguration;
import net.minecraft.world.phys.AABB;

public class SpikeFeature extends Feature {
   private static final LoadingCache SPIKE_CACHE;

   public SpikeFeature(Function var1) {
      super(var1);
   }

   public static List getSpikesForLevel(LevelAccessor var0) {
      Random var1 = new Random(var0.getSeed());
      long var2 = var1.nextLong() & 65535L;
      return (List)SPIKE_CACHE.getUnchecked(var2);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator var2, Random var3, BlockPos var4, SpikeConfiguration var5) {
      List var6 = var5.getSpikes();
      if (var6.isEmpty()) {
         var6 = getSpikesForLevel(var1);
      }

      Iterator var7 = var6.iterator();

      while(var7.hasNext()) {
         SpikeFeature.EndSpike var8 = (SpikeFeature.EndSpike)var7.next();
         if (var8.isCenterWithinChunk(var4)) {
            this.placeSpike(var1, var3, var5, var8);
         }
      }

      return true;
   }

   private void placeSpike(LevelAccessor var1, Random var2, SpikeConfiguration var3, SpikeFeature.EndSpike var4) {
      int var5 = var4.getRadius();
      Iterator var6 = BlockPos.betweenClosed(new BlockPos(var4.getCenterX() - var5, 0, var4.getCenterZ() - var5), new BlockPos(var4.getCenterX() + var5, var4.getHeight() + 10, var4.getCenterZ() + var5)).iterator();

      while(true) {
         while(var6.hasNext()) {
            BlockPos var7 = (BlockPos)var6.next();
            if (var7.distSqr((double)var4.getCenterX(), (double)var7.getY(), (double)var4.getCenterZ(), false) <= (double)(var5 * var5 + 1) && var7.getY() < var4.getHeight()) {
               this.setBlock(var1, var7, Blocks.OBSIDIAN.defaultBlockState());
            } else if (var7.getY() > 65) {
               this.setBlock(var1, var7, Blocks.AIR.defaultBlockState());
            }
         }

         if (var4.isGuarded()) {
            boolean var19 = true;
            boolean var21 = true;
            boolean var8 = true;
            BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();

            for(int var10 = -2; var10 <= 2; ++var10) {
               for(int var11 = -2; var11 <= 2; ++var11) {
                  for(int var12 = 0; var12 <= 3; ++var12) {
                     boolean var13 = Mth.abs(var10) == 2;
                     boolean var14 = Mth.abs(var11) == 2;
                     boolean var15 = var12 == 3;
                     if (var13 || var14 || var15) {
                        boolean var16 = var10 == -2 || var10 == 2 || var15;
                        boolean var17 = var11 == -2 || var11 == 2 || var15;
                        BlockState var18 = (BlockState)((BlockState)((BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, var16 && var11 != -2)).setValue(IronBarsBlock.SOUTH, var16 && var11 != 2)).setValue(IronBarsBlock.WEST, var17 && var10 != -2)).setValue(IronBarsBlock.EAST, var17 && var10 != 2);
                        this.setBlock(var1, var9.set(var4.getCenterX() + var10, var4.getHeight() + var12, var4.getCenterZ() + var11), var18);
                     }
                  }
               }
            }
         }

         EndCrystal var20 = (EndCrystal)EntityType.END_CRYSTAL.create(var1.getLevel());
         var20.setBeamTarget(var3.getCrystalBeamTarget());
         var20.setInvulnerable(var3.isCrystalInvulnerable());
         var20.moveTo((double)((float)var4.getCenterX() + 0.5F), (double)(var4.getHeight() + 1), (double)((float)var4.getCenterZ() + 0.5F), var2.nextFloat() * 360.0F, 0.0F);
         var1.addFreshEntity(var20);
         this.setBlock(var1, new BlockPos(var4.getCenterX(), var4.getHeight(), var4.getCenterZ()), Blocks.BEDROCK.defaultBlockState());
         return;
      }
   }

   static {
      SPIKE_CACHE = CacheBuilder.newBuilder().expireAfterWrite(5L, TimeUnit.MINUTES).build(new SpikeFeature.SpikeCacheLoader());
   }

   static class SpikeCacheLoader extends CacheLoader {
      private SpikeCacheLoader() {
      }

      public List load(Long var1) {
         List var2 = (List)IntStream.range(0, 10).boxed().collect(Collectors.toList());
         Collections.shuffle(var2, new Random(var1));
         ArrayList var3 = Lists.newArrayList();

         for(int var4 = 0; var4 < 10; ++var4) {
            int var5 = Mth.floor(42.0D * Math.cos(2.0D * (-3.141592653589793D + 0.3141592653589793D * (double)var4)));
            int var6 = Mth.floor(42.0D * Math.sin(2.0D * (-3.141592653589793D + 0.3141592653589793D * (double)var4)));
            int var7 = (Integer)var2.get(var4);
            int var8 = 2 + var7 / 3;
            int var9 = 76 + var7 * 3;
            boolean var10 = var7 == 1 || var7 == 2;
            var3.add(new SpikeFeature.EndSpike(var5, var6, var8, var9, var10));
         }

         return var3;
      }

      // $FF: synthetic method
      public Object load(Object var1) throws Exception {
         return this.load((Long)var1);
      }

      // $FF: synthetic method
      SpikeCacheLoader(Object var1) {
         this();
      }
   }

   public static class EndSpike {
      private final int centerX;
      private final int centerZ;
      private final int radius;
      private final int height;
      private final boolean guarded;
      private final AABB topBoundingBox;

      public EndSpike(int var1, int var2, int var3, int var4, boolean var5) {
         this.centerX = var1;
         this.centerZ = var2;
         this.radius = var3;
         this.height = var4;
         this.guarded = var5;
         this.topBoundingBox = new AABB((double)(var1 - var3), 0.0D, (double)(var2 - var3), (double)(var1 + var3), 256.0D, (double)(var2 + var3));
      }

      public boolean isCenterWithinChunk(BlockPos var1) {
         return var1.getX() >> 4 == this.centerX >> 4 && var1.getZ() >> 4 == this.centerZ >> 4;
      }

      public int getCenterX() {
         return this.centerX;
      }

      public int getCenterZ() {
         return this.centerZ;
      }

      public int getRadius() {
         return this.radius;
      }

      public int getHeight() {
         return this.height;
      }

      public boolean isGuarded() {
         return this.guarded;
      }

      public AABB getTopBoundingBox() {
         return this.topBoundingBox;
      }

      public Dynamic serialize(DynamicOps var1) {
         Builder var2 = ImmutableMap.builder();
         var2.put(var1.createString("centerX"), var1.createInt(this.centerX));
         var2.put(var1.createString("centerZ"), var1.createInt(this.centerZ));
         var2.put(var1.createString("radius"), var1.createInt(this.radius));
         var2.put(var1.createString("height"), var1.createInt(this.height));
         var2.put(var1.createString("guarded"), var1.createBoolean(this.guarded));
         return new Dynamic(var1, var1.createMap(var2.build()));
      }

      public static SpikeFeature.EndSpike deserialize(Dynamic var0) {
         return new SpikeFeature.EndSpike(var0.get("centerX").asInt(0), var0.get("centerZ").asInt(0), var0.get("radius").asInt(0), var0.get("height").asInt(0), var0.get("guarded").asBoolean(false));
      }
   }
}
