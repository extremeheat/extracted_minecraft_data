package net.minecraft.world.level.levelgen.blending;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction8;
import net.minecraft.core.QuartPos;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.TerrainInfo;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.material.FluidState;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableObject;

public class Blender {
   private static final Blender EMPTY = new Blender((WorldGenRegion)null, List.of(), List.of()) {
      public TerrainInfo blendOffsetAndFactor(int var1, int var2, TerrainInfo var3) {
         return var3;
      }

      public double blendDensity(int var1, int var2, int var3, double var4) {
         return var4;
      }

      public BiomeResolver getBiomeResolver(BiomeResolver var1) {
         return var1;
      }
   };
   private static final NormalNoise SHIFT_NOISE;
   private static final int HEIGHT_BLENDING_RANGE_CELLS;
   private static final int HEIGHT_BLENDING_RANGE_CHUNKS;
   private static final int DENSITY_BLENDING_RANGE_CELLS = 2;
   private static final int DENSITY_BLENDING_RANGE_CHUNKS;
   private static final double BLENDING_FACTOR = 10.0D;
   private static final double BLENDING_JAGGEDNESS = 0.0D;
   private static final double OLD_CHUNK_Y_RADIUS;
   private static final double OLD_CHUNK_CENTER_Y;
   private static final double OLD_CHUNK_XZ_RADIUS = 8.0D;
   private final WorldGenRegion region;
   private final List<Blender.PositionedBlendingData> heightData;
   private final List<Blender.PositionedBlendingData> densityData;

   public static Blender empty() {
      return EMPTY;
   }

   // $FF: renamed from: of (net.minecraft.server.level.WorldGenRegion) net.minecraft.world.level.levelgen.blending.Blender
   public static Blender method_124(@Nullable WorldGenRegion var0) {
      if (var0 == null) {
         return EMPTY;
      } else {
         ArrayList var1 = Lists.newArrayList();
         ArrayList var2 = Lists.newArrayList();
         ChunkPos var3 = var0.getCenter();

         for(int var4 = -HEIGHT_BLENDING_RANGE_CHUNKS; var4 <= HEIGHT_BLENDING_RANGE_CHUNKS; ++var4) {
            for(int var5 = -HEIGHT_BLENDING_RANGE_CHUNKS; var5 <= HEIGHT_BLENDING_RANGE_CHUNKS; ++var5) {
               int var6 = var3.field_504 + var4;
               int var7 = var3.field_505 + var5;
               BlendingData var8 = BlendingData.getOrUpdateBlendingData(var0, var6, var7);
               if (var8 != null) {
                  Blender.PositionedBlendingData var9 = new Blender.PositionedBlendingData(var6, var7, var8);
                  var1.add(var9);
                  if (var4 >= -DENSITY_BLENDING_RANGE_CHUNKS && var4 <= DENSITY_BLENDING_RANGE_CHUNKS && var5 >= -DENSITY_BLENDING_RANGE_CHUNKS && var5 <= DENSITY_BLENDING_RANGE_CHUNKS) {
                     var2.add(var9);
                  }
               }
            }
         }

         if (var1.isEmpty() && var2.isEmpty()) {
            return EMPTY;
         } else {
            return new Blender(var0, var1, var2);
         }
      }
   }

   Blender(WorldGenRegion var1, List<Blender.PositionedBlendingData> var2, List<Blender.PositionedBlendingData> var3) {
      super();
      this.region = var1;
      this.heightData = var2;
      this.densityData = var3;
   }

   public TerrainInfo blendOffsetAndFactor(int var1, int var2, TerrainInfo var3) {
      int var4 = QuartPos.fromBlock(var1);
      int var5 = QuartPos.fromBlock(var2);
      double var6 = this.getBlendingDataValue(var4, 0, var5, BlendingData::getHeight);
      if (var6 != 1.7976931348623157E308D) {
         return new TerrainInfo(heightToOffset(var6), 10.0D, 0.0D);
      } else {
         MutableDouble var8 = new MutableDouble(0.0D);
         MutableDouble var9 = new MutableDouble(0.0D);
         MutableDouble var10 = new MutableDouble(1.0D / 0.0);
         Iterator var11 = this.heightData.iterator();

         while(var11.hasNext()) {
            Blender.PositionedBlendingData var12 = (Blender.PositionedBlendingData)var11.next();
            var12.blendingData.iterateHeights(QuartPos.fromSection(var12.chunkX), QuartPos.fromSection(var12.chunkZ), (var5x, var6x, var7) -> {
               double var9x = Mth.length((double)(var4 - var5x), (double)(var5 - var6x));
               if (!(var9x > (double)HEIGHT_BLENDING_RANGE_CELLS)) {
                  if (var9x < var10.doubleValue()) {
                     var10.setValue(var9x);
                  }

                  double var11 = 1.0D / (var9x * var9x * var9x * var9x);
                  var9.add(var7 * var11);
                  var8.add(var11);
               }
            });
         }

         if (var10.doubleValue() == 1.0D / 0.0) {
            return var3;
         } else {
            double var21 = var9.doubleValue() / var8.doubleValue();
            double var13 = Mth.clamp(var10.doubleValue() / (double)(HEIGHT_BLENDING_RANGE_CELLS + 1), 0.0D, 1.0D);
            var13 = 3.0D * var13 * var13 - 2.0D * var13 * var13 * var13;
            double var15 = Mth.lerp(var13, heightToOffset(var21), var3.offset());
            double var17 = Mth.lerp(var13, 10.0D, var3.factor());
            double var19 = Mth.lerp(var13, 0.0D, var3.jaggedness());
            return new TerrainInfo(var15, var17, var19);
         }
      }
   }

   private static double heightToOffset(double var0) {
      double var2 = 1.0D;
      double var4 = var0 + 0.5D;
      double var6 = Mth.positiveModulo(var4, 8.0D);
      return 1.0D * (32.0D * (var4 - 128.0D) - 3.0D * (var4 - 120.0D) * var6 + 3.0D * var6 * var6) / (128.0D * (32.0D - 3.0D * var6));
   }

   public double blendDensity(int var1, int var2, int var3, double var4) {
      int var6 = QuartPos.fromBlock(var1);
      int var7 = var2 / 8;
      int var8 = QuartPos.fromBlock(var3);
      double var9 = this.getBlendingDataValue(var6, var7, var8, BlendingData::getDensity);
      if (var9 != 1.7976931348623157E308D) {
         return var9;
      } else {
         MutableDouble var11 = new MutableDouble(0.0D);
         MutableDouble var12 = new MutableDouble(0.0D);
         MutableDouble var13 = new MutableDouble(1.0D / 0.0);
         Iterator var14 = this.densityData.iterator();

         while(var14.hasNext()) {
            Blender.PositionedBlendingData var15 = (Blender.PositionedBlendingData)var14.next();
            var15.blendingData.iterateDensities(QuartPos.fromSection(var15.chunkX), QuartPos.fromSection(var15.chunkZ), var7 - 1, var7 + 1, (var6x, var7x, var8x, var9x) -> {
               double var11x = Mth.length((double)(var6 - var6x), (double)((var7 - var7x) * 2), (double)(var8 - var8x));
               if (!(var11x > 2.0D)) {
                  if (var11x < var13.doubleValue()) {
                     var13.setValue(var11x);
                  }

                  double var13x = 1.0D / (var11x * var11x * var11x * var11x);
                  var12.add(var9x * var13x);
                  var11.add(var13x);
               }
            });
         }

         if (var13.doubleValue() == 1.0D / 0.0) {
            return var4;
         } else {
            double var18 = var12.doubleValue() / var11.doubleValue();
            double var16 = Mth.clamp(var13.doubleValue() / 3.0D, 0.0D, 1.0D);
            return Mth.lerp(var16, var18, var4);
         }
      }
   }

   private double getBlendingDataValue(int var1, int var2, int var3, Blender.CellValueGetter var4) {
      int var5 = QuartPos.toSection(var1);
      int var6 = QuartPos.toSection(var3);
      boolean var7 = (var1 & 3) == 0;
      boolean var8 = (var3 & 3) == 0;
      double var9 = this.getBlendingDataValue(var4, var5, var6, var1, var2, var3);
      if (var9 == 1.7976931348623157E308D) {
         if (var7 && var8) {
            var9 = this.getBlendingDataValue(var4, var5 - 1, var6 - 1, var1, var2, var3);
         }

         if (var9 == 1.7976931348623157E308D) {
            if (var7) {
               var9 = this.getBlendingDataValue(var4, var5 - 1, var6, var1, var2, var3);
            }

            if (var9 == 1.7976931348623157E308D && var8) {
               var9 = this.getBlendingDataValue(var4, var5, var6 - 1, var1, var2, var3);
            }
         }
      }

      return var9;
   }

   private double getBlendingDataValue(Blender.CellValueGetter var1, int var2, int var3, int var4, int var5, int var6) {
      BlendingData var7 = BlendingData.getOrUpdateBlendingData(this.region, var2, var3);
      return var7 != null ? var1.get(var7, var4 - QuartPos.fromSection(var2), var5, var6 - QuartPos.fromSection(var3)) : 1.7976931348623157E308D;
   }

   public BiomeResolver getBiomeResolver(BiomeResolver var1) {
      return (var2, var3, var4, var5) -> {
         Biome var6 = this.blendBiome(var2, var3, var4);
         return var6 == null ? var1.getNoiseBiome(var2, var3, var4, var5) : var6;
      };
   }

   @Nullable
   private Biome blendBiome(int var1, int var2, int var3) {
      double var4 = (double)var1 + SHIFT_NOISE.getValue((double)var1, 0.0D, (double)var3) * 12.0D;
      double var6 = (double)var3 + SHIFT_NOISE.getValue((double)var3, (double)var1, 0.0D) * 12.0D;
      MutableDouble var8 = new MutableDouble(1.0D / 0.0);
      BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();
      MutableObject var10 = new MutableObject();
      Iterator var11 = this.heightData.iterator();

      while(var11.hasNext()) {
         Blender.PositionedBlendingData var12 = (Blender.PositionedBlendingData)var11.next();
         var12.blendingData.iterateHeights(QuartPos.fromSection(var12.chunkX), QuartPos.fromSection(var12.chunkZ), (var8x, var9x, var10x) -> {
            double var12x = Mth.length(var4 - (double)var8x, var6 - (double)var9x);
            if (!(var12x > (double)HEIGHT_BLENDING_RANGE_CELLS)) {
               if (var12x < var8.doubleValue()) {
                  var10.setValue(new ChunkPos(var12.chunkX, var12.chunkZ));
                  var9.set(var8x, QuartPos.fromBlock(Mth.floor(var10x)), var9x);
                  var8.setValue(var12x);
               }

            }
         });
      }

      if (var8.doubleValue() == 1.0D / 0.0) {
         return null;
      } else {
         double var14 = Mth.clamp(var8.doubleValue() / (double)(HEIGHT_BLENDING_RANGE_CELLS + 1), 0.0D, 1.0D);
         if (var14 > 0.5D) {
            return null;
         } else {
            ChunkAccess var13 = this.region.getChunk(((ChunkPos)var10.getValue()).field_504, ((ChunkPos)var10.getValue()).field_505);
            return var13.getNoiseBiome(Math.min(var9.getX() & 3, 3), var9.getY(), Math.min(var9.getZ() & 3, 3));
         }
      }
   }

   public static void generateBorderTicks(WorldGenRegion var0, ChunkAccess var1) {
      ChunkPos var2 = var1.getPos();
      boolean var3 = var1.isOldNoiseGeneration();
      BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos();
      BlockPos var5 = new BlockPos(var2.getMinBlockX(), 0, var2.getMinBlockZ());
      int var6 = BlendingData.AREA_WITH_OLD_GENERATION.getMinBuildHeight();
      int var7 = BlendingData.AREA_WITH_OLD_GENERATION.getMaxBuildHeight() - 1;
      if (var3) {
         for(int var8 = 0; var8 < 16; ++var8) {
            for(int var9 = 0; var9 < 16; ++var9) {
               generateBorderTick(var1, var4.setWithOffset(var5, var8, var6 - 1, var9));
               generateBorderTick(var1, var4.setWithOffset(var5, var8, var6, var9));
               generateBorderTick(var1, var4.setWithOffset(var5, var8, var7, var9));
               generateBorderTick(var1, var4.setWithOffset(var5, var8, var7 + 1, var9));
            }
         }
      }

      Iterator var18 = Direction.Plane.HORIZONTAL.iterator();

      while(true) {
         Direction var19;
         do {
            if (!var18.hasNext()) {
               return;
            }

            var19 = (Direction)var18.next();
         } while(var0.getChunk(var2.field_504 + var19.getStepX(), var2.field_505 + var19.getStepZ()).isOldNoiseGeneration() == var3);

         int var10 = var19 == Direction.EAST ? 15 : 0;
         int var11 = var19 == Direction.WEST ? 0 : 15;
         int var12 = var19 == Direction.SOUTH ? 15 : 0;
         int var13 = var19 == Direction.NORTH ? 0 : 15;

         for(int var14 = var10; var14 <= var11; ++var14) {
            for(int var15 = var12; var15 <= var13; ++var15) {
               int var16 = Math.min(var7, var1.getHeight(Heightmap.Types.MOTION_BLOCKING, var14, var15)) + 1;

               for(int var17 = var6; var17 < var16; ++var17) {
                  generateBorderTick(var1, var4.setWithOffset(var5, var14, var17, var15));
               }
            }
         }
      }
   }

   private static void generateBorderTick(ChunkAccess var0, BlockPos var1) {
      BlockState var2 = var0.getBlockState(var1);
      if (var2.is(BlockTags.LEAVES)) {
         var0.markPosForPostprocessing(var1);
      }

      FluidState var3 = var0.getFluidState(var1);
      if (!var3.isEmpty()) {
         var0.markPosForPostprocessing(var1);
      }

   }

   public static void addAroundOldChunksCarvingMaskFilter(WorldGenLevel var0, ProtoChunk var1) {
      ChunkPos var2 = var1.getPos();
      Blender.DistanceGetter var3 = makeOldChunkDistanceGetter(var1.isOldNoiseGeneration(), BlendingData.sideByGenerationAge(var0, var2.field_504, var2.field_505, true));
      if (var3 != null) {
         CarvingMask.Mask var4 = (var1x, var2x, var3x) -> {
            double var4 = (double)var1x + 0.5D + SHIFT_NOISE.getValue((double)var1x, (double)var2x, (double)var3x) * 4.0D;
            double var6 = (double)var2x + 0.5D + SHIFT_NOISE.getValue((double)var2x, (double)var3x, (double)var1x) * 4.0D;
            double var8 = (double)var3x + 0.5D + SHIFT_NOISE.getValue((double)var3x, (double)var1x, (double)var2x) * 4.0D;
            return var3.getDistance(var4, var6, var8) < 4.0D;
         };
         Stream var10000 = Stream.of(GenerationStep.Carving.values());
         Objects.requireNonNull(var1);
         var10000.map(var1::getOrCreateCarvingMask).forEach((var1x) -> {
            var1x.setAdditionalMask(var4);
         });
      }
   }

   @Nullable
   public static Blender.DistanceGetter makeOldChunkDistanceGetter(boolean var0, Set<Direction8> var1) {
      if (!var0 && var1.isEmpty()) {
         return null;
      } else {
         ArrayList var2 = Lists.newArrayList();
         if (var0) {
            var2.add(makeOffsetOldChunkDistanceGetter((Direction8)null));
         }

         var1.forEach((var1x) -> {
            var2.add(makeOffsetOldChunkDistanceGetter(var1x));
         });
         return (var1x, var3, var5) -> {
            double var7 = 1.0D / 0.0;
            Iterator var9 = var2.iterator();

            while(var9.hasNext()) {
               Blender.DistanceGetter var10 = (Blender.DistanceGetter)var9.next();
               double var11 = var10.getDistance(var1x, var3, var5);
               if (var11 < var7) {
                  var7 = var11;
               }
            }

            return var7;
         };
      }
   }

   private static Blender.DistanceGetter makeOffsetOldChunkDistanceGetter(@Nullable Direction8 var0) {
      double var1 = 0.0D;
      double var3 = 0.0D;
      Direction var6;
      if (var0 != null) {
         for(Iterator var5 = var0.getDirections().iterator(); var5.hasNext(); var3 += (double)(var6.getStepZ() * 16)) {
            var6 = (Direction)var5.next();
            var1 += (double)(var6.getStepX() * 16);
         }
      }

      return (var4, var6x, var8) -> {
         return distanceToCube(var4 - 8.0D - var1, var6x - OLD_CHUNK_CENTER_Y, var8 - 8.0D - var3, 8.0D, OLD_CHUNK_Y_RADIUS, 8.0D);
      };
   }

   private static double distanceToCube(double var0, double var2, double var4, double var6, double var8, double var10) {
      double var12 = Math.abs(var0) - var6;
      double var14 = Math.abs(var2) - var8;
      double var16 = Math.abs(var4) - var10;
      return Mth.length(Math.max(0.0D, var12), Math.max(0.0D, var14), Math.max(0.0D, var16));
   }

   static {
      SHIFT_NOISE = NormalNoise.create(new XoroshiroRandomSource(42L), (NormalNoise.NoiseParameters)BuiltinRegistries.NOISE.getOrThrow(Noises.SHIFT));
      HEIGHT_BLENDING_RANGE_CELLS = QuartPos.fromSection(7) - 1;
      HEIGHT_BLENDING_RANGE_CHUNKS = QuartPos.toSection(HEIGHT_BLENDING_RANGE_CELLS + 3);
      DENSITY_BLENDING_RANGE_CHUNKS = QuartPos.toSection(5);
      OLD_CHUNK_Y_RADIUS = (double)BlendingData.AREA_WITH_OLD_GENERATION.getHeight() / 2.0D;
      OLD_CHUNK_CENTER_Y = (double)BlendingData.AREA_WITH_OLD_GENERATION.getMinBuildHeight() + OLD_CHUNK_Y_RADIUS;
   }

   static record PositionedBlendingData(int a, int b, BlendingData c) {
      final int chunkX;
      final int chunkZ;
      final BlendingData blendingData;

      PositionedBlendingData(int var1, int var2, BlendingData var3) {
         super();
         this.chunkX = var1;
         this.chunkZ = var2;
         this.blendingData = var3;
      }

      public int chunkX() {
         return this.chunkX;
      }

      public int chunkZ() {
         return this.chunkZ;
      }

      public BlendingData blendingData() {
         return this.blendingData;
      }
   }

   interface CellValueGetter {
      double get(BlendingData var1, int var2, int var3, int var4);
   }

   public interface DistanceGetter {
      double getDistance(double var1, double var3, double var5);
   }
}
