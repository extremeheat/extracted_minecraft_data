package net.minecraft.world.level.levelgen.blending;

import com.google.common.primitives.Doubles;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.DoubleStream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction8;
import net.minecraft.core.QuartPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;

public class BlendingData {
   private static final double BLENDING_DENSITY_FACTOR = 0.1D;
   protected static final LevelHeightAccessor AREA_WITH_OLD_GENERATION = new LevelHeightAccessor() {
      public int getHeight() {
         return 256;
      }

      public int getMinBuildHeight() {
         return 0;
      }
   };
   protected static final int CELL_WIDTH = 4;
   protected static final int CELL_HEIGHT = 8;
   protected static final int CELL_RATIO = 2;
   private static final int CELLS_PER_SECTION_Y = 2;
   private static final int QUARTS_PER_SECTION = QuartPos.fromBlock(16);
   private static final int CELL_HORIZONTAL_MAX_INDEX_INSIDE;
   private static final int CELL_HORIZONTAL_MAX_INDEX_OUTSIDE;
   private static final int CELL_COLUMN_INSIDE_COUNT;
   private static final int CELL_COLUMN_OUTSIDE_COUNT;
   private static final int CELL_COLUMN_COUNT;
   private static final int CELL_HORIZONTAL_FLOOR_COUNT;
   private static final List<Block> SURFACE_BLOCKS;
   protected static final double NO_VALUE = 1.7976931348623157E308D;
   private final boolean oldNoise;
   private boolean hasCalculatedData;
   private final double[] heights;
   private final transient double[][] densities;
   private final transient double[] floorDensities;
   private static final Codec<double[]> DOUBLE_ARRAY_CODEC;
   public static final Codec<BlendingData> CODEC;

   private static DataResult<BlendingData> validateArraySize(BlendingData var0) {
      return var0.heights.length != CELL_COLUMN_COUNT ? DataResult.error("heights has to be of length " + CELL_COLUMN_COUNT) : DataResult.success(var0);
   }

   private BlendingData(boolean var1, Optional<double[]> var2) {
      super();
      this.oldNoise = var1;
      this.heights = (double[])var2.orElse((double[])Util.make(new double[CELL_COLUMN_COUNT], (var0) -> {
         Arrays.fill(var0, 1.7976931348623157E308D);
      }));
      this.densities = new double[CELL_COLUMN_COUNT][];
      this.floorDensities = new double[CELL_HORIZONTAL_FLOOR_COUNT * CELL_HORIZONTAL_FLOOR_COUNT];
   }

   public boolean oldNoise() {
      return this.oldNoise;
   }

   @Nullable
   public static BlendingData getOrUpdateBlendingData(WorldGenRegion var0, int var1, int var2) {
      ChunkAccess var3 = var0.getChunk(var1, var2);
      BlendingData var4 = var3.getBlendingData();
      if (var4 != null && var4.oldNoise()) {
         var4.calculateData(var3, sideByGenerationAge(var0, var1, var2, false));
         return var4;
      } else {
         return null;
      }
   }

   public static Set<Direction8> sideByGenerationAge(WorldGenLevel var0, int var1, int var2, boolean var3) {
      EnumSet var4 = EnumSet.noneOf(Direction8.class);
      Direction8[] var5 = Direction8.values();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Direction8 var8 = var5[var7];
         int var9 = var1;
         int var10 = var2;

         Direction var12;
         for(Iterator var11 = var8.getDirections().iterator(); var11.hasNext(); var10 += var12.getStepZ()) {
            var12 = (Direction)var11.next();
            var9 += var12.getStepX();
         }

         if (var0.getChunk(var9, var10).isOldNoiseGeneration() == var3) {
            var4.add(var8);
         }
      }

      return var4;
   }

   private void calculateData(ChunkAccess var1, Set<Direction8> var2) {
      if (!this.hasCalculatedData) {
         Arrays.fill(this.floorDensities, 1.0D);
         if (var2.contains(Direction8.NORTH) || var2.contains(Direction8.WEST) || var2.contains(Direction8.NORTH_WEST)) {
            this.addValuesForColumn(getInsideIndex(0, 0), var1, 0, 0);
         }

         int var3;
         if (var2.contains(Direction8.NORTH)) {
            for(var3 = 1; var3 < QUARTS_PER_SECTION; ++var3) {
               this.addValuesForColumn(getInsideIndex(var3, 0), var1, 4 * var3, 0);
            }
         }

         if (var2.contains(Direction8.WEST)) {
            for(var3 = 1; var3 < QUARTS_PER_SECTION; ++var3) {
               this.addValuesForColumn(getInsideIndex(0, var3), var1, 0, 4 * var3);
            }
         }

         if (var2.contains(Direction8.EAST)) {
            for(var3 = 1; var3 < QUARTS_PER_SECTION; ++var3) {
               this.addValuesForColumn(getOutsideIndex(CELL_HORIZONTAL_MAX_INDEX_OUTSIDE, var3), var1, 15, 4 * var3);
            }
         }

         if (var2.contains(Direction8.SOUTH)) {
            for(var3 = 0; var3 < QUARTS_PER_SECTION; ++var3) {
               this.addValuesForColumn(getOutsideIndex(var3, CELL_HORIZONTAL_MAX_INDEX_OUTSIDE), var1, 4 * var3, 15);
            }
         }

         if (var2.contains(Direction8.EAST) && var2.contains(Direction8.NORTH_EAST)) {
            this.addValuesForColumn(getOutsideIndex(CELL_HORIZONTAL_MAX_INDEX_OUTSIDE, 0), var1, 15, 0);
         }

         if (var2.contains(Direction8.EAST) && var2.contains(Direction8.SOUTH) && var2.contains(Direction8.SOUTH_EAST)) {
            this.addValuesForColumn(getOutsideIndex(CELL_HORIZONTAL_MAX_INDEX_OUTSIDE, CELL_HORIZONTAL_MAX_INDEX_OUTSIDE), var1, 15, 15);
         }

         this.hasCalculatedData = true;
      }
   }

   private void addValuesForColumn(int var1, ChunkAccess var2, int var3, int var4) {
      if (this.heights[var1] == 1.7976931348623157E308D) {
         this.heights[var1] = (double)getHeightAtXZ(var2, var3, var4);
      }

      this.densities[var1] = getDensityColumn(var2, var3, var4, Mth.floor(this.heights[var1]));
   }

   private static int getHeightAtXZ(ChunkAccess var0, int var1, int var2) {
      int var3;
      if (var0.hasPrimedHeightmap(Heightmap.Types.WORLD_SURFACE_WG)) {
         var3 = Math.min(var0.getHeight(Heightmap.Types.WORLD_SURFACE_WG, var1, var2) + 1, AREA_WITH_OLD_GENERATION.getMaxBuildHeight());
      } else {
         var3 = AREA_WITH_OLD_GENERATION.getMaxBuildHeight();
      }

      int var4 = AREA_WITH_OLD_GENERATION.getMinBuildHeight();
      BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos(var1, var3, var2);

      do {
         if (var5.getY() <= var4) {
            return var4;
         }

         var5.move(Direction.DOWN);
      } while(!SURFACE_BLOCKS.contains(var0.getBlockState(var5).getBlock()));

      return var5.getY();
   }

   private static double read1(ChunkAccess var0, BlockPos.MutableBlockPos var1) {
      return isGround(var0, var1.move(Direction.DOWN)) ? 1.0D : -1.0D;
   }

   private static double read7(ChunkAccess var0, BlockPos.MutableBlockPos var1) {
      double var2 = 0.0D;

      for(int var4 = 0; var4 < 7; ++var4) {
         var2 += read1(var0, var1);
      }

      return var2;
   }

   private static double[] getDensityColumn(ChunkAccess var0, int var1, int var2, int var3) {
      double[] var4 = new double[cellCountPerColumn()];
      Arrays.fill(var4, -1.0D);
      BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos(var1, AREA_WITH_OLD_GENERATION.getMaxBuildHeight(), var2);
      double var6 = read7(var0, var5);

      int var8;
      double var9;
      double var11;
      for(var8 = var4.length - 2; var8 >= 0; --var8) {
         var9 = read1(var0, var5);
         var11 = read7(var0, var5);
         var4[var8] = (var6 + var9 + var11) / 15.0D;
         var6 = var11;
      }

      var8 = Mth.intFloorDiv(var3, 8);
      if (var8 >= 1 && var8 < var4.length) {
         var9 = ((double)var3 + 0.5D) % 8.0D / 8.0D;
         var11 = (1.0D - var9) / var9;
         double var13 = Math.max(var11, 1.0D) * 0.25D;
         var4[var8] = -var11 / var13;
         var4[var8 - 1] = 1.0D / var13;
      }

      return var4;
   }

   private static boolean isGround(ChunkAccess var0, BlockPos var1) {
      BlockState var2 = var0.getBlockState(var1);
      if (var2.isAir()) {
         return false;
      } else if (var2.is(BlockTags.LEAVES)) {
         return false;
      } else if (var2.is(BlockTags.LOGS)) {
         return false;
      } else if (!var2.is(Blocks.BROWN_MUSHROOM_BLOCK) && !var2.is(Blocks.RED_MUSHROOM_BLOCK)) {
         return !var2.getCollisionShape(var0, var1).isEmpty();
      } else {
         return false;
      }
   }

   protected double getHeight(int var1, int var2, int var3) {
      if (var1 != CELL_HORIZONTAL_MAX_INDEX_OUTSIDE && var3 != CELL_HORIZONTAL_MAX_INDEX_OUTSIDE) {
         return var1 != 0 && var3 != 0 ? 1.7976931348623157E308D : this.heights[getInsideIndex(var1, var3)];
      } else {
         return this.heights[getOutsideIndex(var1, var3)];
      }
   }

   private static double getDensity(@Nullable double[] var0, int var1) {
      if (var0 == null) {
         return 1.7976931348623157E308D;
      } else {
         int var2 = var1 - getColumnMinY();
         return var2 >= 0 && var2 < var0.length ? var0[var2] * 0.1D : 1.7976931348623157E308D;
      }
   }

   protected double getDensity(int var1, int var2, int var3) {
      if (var2 == getMinY()) {
         return this.floorDensities[this.getFloorIndex(var1, var3)] * 0.1D;
      } else if (var1 != CELL_HORIZONTAL_MAX_INDEX_OUTSIDE && var3 != CELL_HORIZONTAL_MAX_INDEX_OUTSIDE) {
         return var1 != 0 && var3 != 0 ? 1.7976931348623157E308D : getDensity(this.densities[getInsideIndex(var1, var3)], var2);
      } else {
         return getDensity(this.densities[getOutsideIndex(var1, var3)], var2);
      }
   }

   protected void iterateHeights(int var1, int var2, BlendingData.HeightConsumer var3) {
      for(int var4 = 0; var4 < this.densities.length; ++var4) {
         double var5 = this.heights[var4];
         if (var5 != 1.7976931348623157E308D) {
            var3.consume(var1 + getX(var4), var2 + getZ(var4), var5);
         }
      }

   }

   protected void iterateDensities(int var1, int var2, int var3, int var4, BlendingData.DensityConsumer var5) {
      int var6 = getColumnMinY();
      int var7 = Math.max(0, var3 - var6);
      int var8 = Math.min(cellCountPerColumn(), var4 - var6);

      int var9;
      int var11;
      for(var9 = 0; var9 < this.densities.length; ++var9) {
         double[] var10 = this.densities[var9];
         if (var10 != null) {
            var11 = var1 + getX(var9);
            int var12 = var2 + getZ(var9);

            for(int var13 = var7; var13 < var8; ++var13) {
               var5.consume(var11, var13 + var6, var12, var10[var13] * 0.1D);
            }
         }
      }

      if (var6 >= var3 && var6 <= var4) {
         for(var9 = 0; var9 < this.floorDensities.length; ++var9) {
            int var14 = this.getFloorX(var9);
            var11 = this.getFloorZ(var9);
            var5.consume(var14, var6, var11, this.floorDensities[var9] * 0.1D);
         }
      }

   }

   private int getFloorIndex(int var1, int var2) {
      return var1 * CELL_HORIZONTAL_FLOOR_COUNT + var2;
   }

   private int getFloorX(int var1) {
      return var1 / CELL_HORIZONTAL_FLOOR_COUNT;
   }

   private int getFloorZ(int var1) {
      return var1 % CELL_HORIZONTAL_FLOOR_COUNT;
   }

   private static int cellCountPerColumn() {
      return AREA_WITH_OLD_GENERATION.getSectionsCount() * 2;
   }

   private static int getColumnMinY() {
      return getMinY() + 1;
   }

   private static int getMinY() {
      return AREA_WITH_OLD_GENERATION.getMinSection() * 2;
   }

   private static int getInsideIndex(int var0, int var1) {
      return CELL_HORIZONTAL_MAX_INDEX_INSIDE - var0 + var1;
   }

   private static int getOutsideIndex(int var0, int var1) {
      return CELL_COLUMN_INSIDE_COUNT + var0 + CELL_HORIZONTAL_MAX_INDEX_OUTSIDE - var1;
   }

   private static int getX(int var0) {
      if (var0 < CELL_COLUMN_INSIDE_COUNT) {
         return zeroIfNegative(CELL_HORIZONTAL_MAX_INDEX_INSIDE - var0);
      } else {
         int var1 = var0 - CELL_COLUMN_INSIDE_COUNT;
         return CELL_HORIZONTAL_MAX_INDEX_OUTSIDE - zeroIfNegative(CELL_HORIZONTAL_MAX_INDEX_OUTSIDE - var1);
      }
   }

   private static int getZ(int var0) {
      if (var0 < CELL_COLUMN_INSIDE_COUNT) {
         return zeroIfNegative(var0 - CELL_HORIZONTAL_MAX_INDEX_INSIDE);
      } else {
         int var1 = var0 - CELL_COLUMN_INSIDE_COUNT;
         return CELL_HORIZONTAL_MAX_INDEX_OUTSIDE - zeroIfNegative(var1 - CELL_HORIZONTAL_MAX_INDEX_OUTSIDE);
      }
   }

   private static int zeroIfNegative(int var0) {
      return var0 & ~(var0 >> 31);
   }

   static {
      CELL_HORIZONTAL_MAX_INDEX_INSIDE = QUARTS_PER_SECTION - 1;
      CELL_HORIZONTAL_MAX_INDEX_OUTSIDE = QUARTS_PER_SECTION;
      CELL_COLUMN_INSIDE_COUNT = 2 * CELL_HORIZONTAL_MAX_INDEX_INSIDE + 1;
      CELL_COLUMN_OUTSIDE_COUNT = 2 * CELL_HORIZONTAL_MAX_INDEX_OUTSIDE + 1;
      CELL_COLUMN_COUNT = CELL_COLUMN_INSIDE_COUNT + CELL_COLUMN_OUTSIDE_COUNT;
      CELL_HORIZONTAL_FLOOR_COUNT = QUARTS_PER_SECTION + 1;
      SURFACE_BLOCKS = List.of(Blocks.PODZOL, Blocks.GRAVEL, Blocks.GRASS_BLOCK, Blocks.STONE, Blocks.COARSE_DIRT, Blocks.SAND, Blocks.RED_SAND, Blocks.MYCELIUM, Blocks.SNOW_BLOCK, Blocks.TERRACOTTA, Blocks.DIRT);
      DOUBLE_ARRAY_CODEC = Codec.DOUBLE.listOf().xmap(Doubles::toArray, Doubles::asList);
      CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(Codec.BOOL.fieldOf("old_noise").forGetter(BlendingData::oldNoise), DOUBLE_ARRAY_CODEC.optionalFieldOf("heights").forGetter((var0x) -> {
            return DoubleStream.of(var0x.heights).anyMatch((var0) -> {
               return var0 != 1.7976931348623157E308D;
            }) ? Optional.of(var0x.heights) : Optional.empty();
         })).apply(var0, BlendingData::new);
      }).comapFlatMap(BlendingData::validateArraySize, Function.identity());
   }

   protected interface HeightConsumer {
      void consume(int var1, int var2, double var3);
   }

   protected interface DensityConsumer {
      void consume(int var1, int var2, int var3, double var4);
   }
}
