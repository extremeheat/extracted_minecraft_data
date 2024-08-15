package net.minecraft.world.level.levelgen.feature;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.feature.configurations.SpikeConfiguration;
import net.minecraft.world.phys.AABB;

public class SpikeFeature extends Feature<SpikeConfiguration> {
   public static final int NUMBER_OF_SPIKES = 10;
   private static final int SPIKE_DISTANCE = 42;
   private static final LoadingCache<Long, List<SpikeFeature.EndSpike>> SPIKE_CACHE = CacheBuilder.newBuilder()
      .expireAfterWrite(5L, TimeUnit.MINUTES)
      .build(new SpikeFeature.SpikeCacheLoader());

   public SpikeFeature(Codec<SpikeConfiguration> var1) {
      super(var1);
   }

   public static List<SpikeFeature.EndSpike> getSpikesForLevel(WorldGenLevel var0) {
      RandomSource var1 = RandomSource.create(var0.getSeed());
      long var2 = var1.nextLong() & 65535L;
      return (List<SpikeFeature.EndSpike>)SPIKE_CACHE.getUnchecked(var2);
   }

   @Override
   public boolean place(FeaturePlaceContext<SpikeConfiguration> var1) {
      SpikeConfiguration var2 = (SpikeConfiguration)var1.config();
      WorldGenLevel var3 = var1.level();
      RandomSource var4 = var1.random();
      BlockPos var5 = var1.origin();
      List var6 = var2.getSpikes();
      if (var6.isEmpty()) {
         var6 = getSpikesForLevel(var3);
      }

      for (SpikeFeature.EndSpike var8 : var6) {
         if (var8.isCenterWithinChunk(var5)) {
            this.placeSpike(var3, var4, var2, var8);
         }
      }

      return true;
   }

   private void placeSpike(ServerLevelAccessor var1, RandomSource var2, SpikeConfiguration var3, SpikeFeature.EndSpike var4) {
      int var5 = var4.getRadius();

      for (BlockPos var7 : BlockPos.betweenClosed(
         new BlockPos(var4.getCenterX() - var5, var1.getMinBuildHeight(), var4.getCenterZ() - var5),
         new BlockPos(var4.getCenterX() + var5, var4.getHeight() + 10, var4.getCenterZ() + var5)
      )) {
         if (var7.distToLowCornerSqr((double)var4.getCenterX(), (double)var7.getY(), (double)var4.getCenterZ()) <= (double)(var5 * var5 + 1)
            && var7.getY() < var4.getHeight()) {
            this.setBlock(var1, var7, Blocks.OBSIDIAN.defaultBlockState());
         } else if (var7.getY() > 65) {
            this.setBlock(var1, var7, Blocks.AIR.defaultBlockState());
         }
      }

      if (var4.isGuarded()) {
         byte var19 = -2;
         byte var21 = 2;
         byte var8 = 3;
         BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();

         for (int var10 = -2; var10 <= 2; var10++) {
            for (int var11 = -2; var11 <= 2; var11++) {
               for (int var12 = 0; var12 <= 3; var12++) {
                  boolean var13 = Mth.abs(var10) == 2;
                  boolean var14 = Mth.abs(var11) == 2;
                  boolean var15 = var12 == 3;
                  if (var13 || var14 || var15) {
                     boolean var16 = var10 == -2 || var10 == 2 || var15;
                     boolean var17 = var11 == -2 || var11 == 2 || var15;
                     BlockState var18 = Blocks.IRON_BARS
                        .defaultBlockState()
                        .setValue(IronBarsBlock.NORTH, Boolean.valueOf(var16 && var11 != -2))
                        .setValue(IronBarsBlock.SOUTH, Boolean.valueOf(var16 && var11 != 2))
                        .setValue(IronBarsBlock.WEST, Boolean.valueOf(var17 && var10 != -2))
                        .setValue(IronBarsBlock.EAST, Boolean.valueOf(var17 && var10 != 2));
                     this.setBlock(var1, var9.set(var4.getCenterX() + var10, var4.getHeight() + var12, var4.getCenterZ() + var11), var18);
                  }
               }
            }
         }
      }

      EndCrystal var20 = EntityType.END_CRYSTAL.create(var1.getLevel(), EntitySpawnReason.STRUCTURE);
      if (var20 != null) {
         var20.setBeamTarget(var3.getCrystalBeamTarget());
         var20.setInvulnerable(var3.isCrystalInvulnerable());
         var20.moveTo((double)var4.getCenterX() + 0.5, (double)(var4.getHeight() + 1), (double)var4.getCenterZ() + 0.5, var2.nextFloat() * 360.0F, 0.0F);
         var1.addFreshEntity(var20);
         BlockPos var22 = var20.blockPosition();
         this.setBlock(var1, var22.below(), Blocks.BEDROCK.defaultBlockState());
         this.setBlock(var1, var22, FireBlock.getState(var1, var22));
      }
   }

   public static class EndSpike {
      public static final Codec<SpikeFeature.EndSpike> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  Codec.INT.fieldOf("centerX").orElse(0).forGetter(var0x -> var0x.centerX),
                  Codec.INT.fieldOf("centerZ").orElse(0).forGetter(var0x -> var0x.centerZ),
                  Codec.INT.fieldOf("radius").orElse(0).forGetter(var0x -> var0x.radius),
                  Codec.INT.fieldOf("height").orElse(0).forGetter(var0x -> var0x.height),
                  Codec.BOOL.fieldOf("guarded").orElse(false).forGetter(var0x -> var0x.guarded)
               )
               .apply(var0, SpikeFeature.EndSpike::new)
      );
      private final int centerX;
      private final int centerZ;
      private final int radius;
      private final int height;
      private final boolean guarded;
      private final AABB topBoundingBox;

      public EndSpike(int var1, int var2, int var3, int var4, boolean var5) {
         super();
         this.centerX = var1;
         this.centerZ = var2;
         this.radius = var3;
         this.height = var4;
         this.guarded = var5;
         this.topBoundingBox = new AABB(
            (double)(var1 - var3),
            (double)DimensionType.MIN_Y,
            (double)(var2 - var3),
            (double)(var1 + var3),
            (double)DimensionType.MAX_Y,
            (double)(var2 + var3)
         );
      }

      public boolean isCenterWithinChunk(BlockPos var1) {
         return SectionPos.blockToSectionCoord(var1.getX()) == SectionPos.blockToSectionCoord(this.centerX)
            && SectionPos.blockToSectionCoord(var1.getZ()) == SectionPos.blockToSectionCoord(this.centerZ);
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
   }

   static class SpikeCacheLoader extends CacheLoader<Long, List<SpikeFeature.EndSpike>> {
      SpikeCacheLoader() {
         super();
      }

      public List<SpikeFeature.EndSpike> load(Long var1) {
         IntArrayList var2 = Util.toShuffledList(IntStream.range(0, 10), RandomSource.create(var1));
         ArrayList var3 = Lists.newArrayList();

         for (int var4 = 0; var4 < 10; var4++) {
            int var5 = Mth.floor(42.0 * Math.cos(2.0 * (-3.141592653589793 + 0.3141592653589793 * (double)var4)));
            int var6 = Mth.floor(42.0 * Math.sin(2.0 * (-3.141592653589793 + 0.3141592653589793 * (double)var4)));
            int var7 = var2.get(var4);
            int var8 = 2 + var7 / 3;
            int var9 = 76 + var7 * 3;
            boolean var10 = var7 == 1 || var7 == 2;
            var3.add(new SpikeFeature.EndSpike(var5, var6, var8, var9, var10));
         }

         return var3;
      }
   }
}
