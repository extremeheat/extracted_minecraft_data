package net.minecraft.world.level.levelgen.blending;

import com.google.common.primitives.Doubles;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Arrays;
import java.util.EnumSet;
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
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;

public class BlendingData {
   private static final double BLENDING_DENSITY_FACTOR = 0.1;
   protected static final int CELL_WIDTH = 4;
   protected static final int CELL_HEIGHT = 8;
   protected static final int CELL_RATIO = 2;
   private static final double SOLID_DENSITY = 1.0;
   private static final double AIR_DENSITY = -1.0;
   private static final int CELLS_PER_SECTION_Y = 2;
   private static final int QUARTS_PER_SECTION = QuartPos.fromBlock(16);
   private static final int CELL_HORIZONTAL_MAX_INDEX_INSIDE = QUARTS_PER_SECTION - 1;
   private static final int CELL_HORIZONTAL_MAX_INDEX_OUTSIDE = QUARTS_PER_SECTION;
   private static final int CELL_COLUMN_INSIDE_COUNT = 2 * CELL_HORIZONTAL_MAX_INDEX_INSIDE + 1;
   private static final int CELL_COLUMN_OUTSIDE_COUNT = 2 * CELL_HORIZONTAL_MAX_INDEX_OUTSIDE + 1;
   private static final int CELL_COLUMN_COUNT = CELL_COLUMN_INSIDE_COUNT + CELL_COLUMN_OUTSIDE_COUNT;
   private final LevelHeightAccessor areaWithOldGeneration;
   private static final List<Block> SURFACE_BLOCKS = List.of(
      Blocks.PODZOL,
      Blocks.GRAVEL,
      Blocks.GRASS_BLOCK,
      Blocks.PEELGRASS_BLOCK,
      Blocks.CORRUPTED_PEELGRASS_BLOCK,
      Blocks.STONE,
      Blocks.POTONE,
      Blocks.COARSE_DIRT,
      Blocks.SAND,
      Blocks.GRAVTATER,
      Blocks.RED_SAND,
      Blocks.MYCELIUM,
      Blocks.SNOW_BLOCK,
      Blocks.TERRACOTTA,
      Blocks.DIRT,
      Blocks.TERREDEPOMME
   );
   protected static final double NO_VALUE = 1.7976931348623157E308;
   private boolean hasCalculatedData;
   private final double[] heights;
   private final List<List<Holder<Biome>>> biomes;
   private final transient double[][] densities;
   private static final Codec<double[]> DOUBLE_ARRAY_CODEC = Codec.DOUBLE.listOf().xmap(Doubles::toArray, Doubles::asList);
   public static final Codec<BlendingData> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  Codec.INT.fieldOf("min_section").forGetter(var0x -> var0x.areaWithOldGeneration.getMinSection()),
                  Codec.INT.fieldOf("max_section").forGetter(var0x -> var0x.areaWithOldGeneration.getMaxSection()),
                  DOUBLE_ARRAY_CODEC.optionalFieldOf("heights")
                     .forGetter(
                        var0x -> DoubleStream.of(var0x.heights).anyMatch(var0xx -> var0xx != 1.7976931348623157E308)
                              ? Optional.of(var0x.heights)
                              : Optional.empty()
                     )
               )
               .apply(var0, BlendingData::new)
      )
      .comapFlatMap(BlendingData::validateArraySize, Function.identity());

   private static DataResult<BlendingData> validateArraySize(BlendingData var0) {
      return var0.heights.length != CELL_COLUMN_COUNT ? DataResult.error(() -> "heights has to be of length " + CELL_COLUMN_COUNT) : DataResult.success(var0);
   }

   private BlendingData(int var1, int var2, Optional<double[]> var3) {
      super();
      this.heights = var3.orElse(Util.make(new double[CELL_COLUMN_COUNT], var0 -> Arrays.fill(var0, 1.7976931348623157E308)));
      this.densities = new double[CELL_COLUMN_COUNT][];
      ObjectArrayList var4 = new ObjectArrayList(CELL_COLUMN_COUNT);
      var4.size(CELL_COLUMN_COUNT);
      this.biomes = var4;
      int var5 = SectionPos.sectionToBlockCoord(var1);
      int var6 = SectionPos.sectionToBlockCoord(var2) - var5;
      this.areaWithOldGeneration = LevelHeightAccessor.create(var5, var6);
   }

   @Nullable
   public static BlendingData getOrUpdateBlendingData(WorldGenRegion var0, int var1, int var2) {
      ChunkAccess var3 = var0.getChunk(var1, var2);
      BlendingData var4 = var3.getBlendingData();
      if (var4 != null && var3.getHighestGeneratedStatus().isOrAfter(ChunkStatus.BIOMES)) {
         var4.calculateData(var3, sideByGenerationAge(var0, var1, var2, false));
         return var4;
      } else {
         return null;
      }
   }

   public static Set<Direction8> sideByGenerationAge(WorldGenLevel var0, int var1, int var2, boolean var3) {
      EnumSet var4 = EnumSet.noneOf(Direction8.class);

      for(Direction8 var8 : Direction8.values()) {
         int var9 = var1 + var8.getStepX();
         int var10 = var2 + var8.getStepZ();
         if (var0.getChunk(var9, var10).isOldNoiseGeneration() == var3) {
            var4.add(var8);
         }
      }

      return var4;
   }

   private void calculateData(ChunkAccess var1, Set<Direction8> var2) {
      if (!this.hasCalculatedData) {
         if (var2.contains(Direction8.NORTH) || var2.contains(Direction8.WEST) || var2.contains(Direction8.NORTH_WEST)) {
            this.addValuesForColumn(getInsideIndex(0, 0), var1, 0, 0);
         }

         if (var2.contains(Direction8.NORTH)) {
            for(int var3 = 1; var3 < QUARTS_PER_SECTION; ++var3) {
               this.addValuesForColumn(getInsideIndex(var3, 0), var1, 4 * var3, 0);
            }
         }

         if (var2.contains(Direction8.WEST)) {
            for(int var4 = 1; var4 < QUARTS_PER_SECTION; ++var4) {
               this.addValuesForColumn(getInsideIndex(0, var4), var1, 0, 4 * var4);
            }
         }

         if (var2.contains(Direction8.EAST)) {
            for(int var5 = 1; var5 < QUARTS_PER_SECTION; ++var5) {
               this.addValuesForColumn(getOutsideIndex(CELL_HORIZONTAL_MAX_INDEX_OUTSIDE, var5), var1, 15, 4 * var5);
            }
         }

         if (var2.contains(Direction8.SOUTH)) {
            for(int var6 = 0; var6 < QUARTS_PER_SECTION; ++var6) {
               this.addValuesForColumn(getOutsideIndex(var6, CELL_HORIZONTAL_MAX_INDEX_OUTSIDE), var1, 4 * var6, 15);
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
      if (this.heights[var1] == 1.7976931348623157E308) {
         this.heights[var1] = (double)this.getHeightAtXZ(var2, var3, var4);
      }

      this.densities[var1] = this.getDensityColumn(var2, var3, var4, Mth.floor(this.heights[var1]));
      this.biomes.set(var1, this.getBiomeColumn(var2, var3, var4));
   }

   private int getHeightAtXZ(ChunkAccess var1, int var2, int var3) {
      int var4;
      if (var1.hasPrimedHeightmap(Heightmap.Types.WORLD_SURFACE_WG)) {
         var4 = Math.min(var1.getHeight(Heightmap.Types.WORLD_SURFACE_WG, var2, var3) + 1, this.areaWithOldGeneration.getMaxBuildHeight());
      } else {
         var4 = this.areaWithOldGeneration.getMaxBuildHeight();
      }

      int var5 = this.areaWithOldGeneration.getMinBuildHeight();
      BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos(var2, var4, var3);

      while(var6.getY() > var5) {
         var6.move(Direction.DOWN);
         if (SURFACE_BLOCKS.contains(var1.getBlockState(var6).getBlock())) {
            return var6.getY();
         }
      }

      return var5;
   }

   private static double read1(ChunkAccess var0, BlockPos.MutableBlockPos var1) {
      return isGround(var0, var1.move(Direction.DOWN)) ? 1.0 : -1.0;
   }

   private static double read7(ChunkAccess var0, BlockPos.MutableBlockPos var1) {
      double var2 = 0.0;

      for(int var4 = 0; var4 < 7; ++var4) {
         var2 += read1(var0, var1);
      }

      return var2;
   }

   private double[] getDensityColumn(ChunkAccess var1, int var2, int var3, int var4) {
      double[] var5 = new double[this.cellCountPerColumn()];
      Arrays.fill(var5, -1.0);
      BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos(var2, this.areaWithOldGeneration.getMaxBuildHeight(), var3);
      double var7 = read7(var1, var6);

      for(int var9 = var5.length - 2; var9 >= 0; --var9) {
         double var10 = read1(var1, var6);
         double var12 = read7(var1, var6);
         var5[var9] = (var7 + var10 + var12) / 15.0;
         var7 = var12;
      }

      int var16 = this.getCellYIndex(Mth.floorDiv(var4, 8));
      if (var16 >= 0 && var16 < var5.length - 1) {
         double var17 = ((double)var4 + 0.5) % 8.0 / 8.0;
         double var18 = (1.0 - var17) / var17;
         double var14 = Math.max(var18, 1.0) * 0.25;
         var5[var16 + 1] = -var18 / var14;
         var5[var16] = 1.0 / var14;
      }

      return var5;
   }

   private List<Holder<Biome>> getBiomeColumn(ChunkAccess var1, int var2, int var3) {
      ObjectArrayList var4 = new ObjectArrayList(this.quartCountPerColumn());
      var4.size(this.quartCountPerColumn());

      for(int var5 = 0; var5 < var4.size(); ++var5) {
         int var6 = var5 + QuartPos.fromBlock(this.areaWithOldGeneration.getMinBuildHeight());
         var4.set(var5, var1.getNoiseBiome(QuartPos.fromBlock(var2), var6, QuartPos.fromBlock(var3)));
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
      } else if (var2.is(Blocks.BROWN_MUSHROOM_BLOCK) || var2.is(Blocks.RED_MUSHROOM_BLOCK)) {
         return false;
      } else {
         return !var2.getCollisionShape(var0, var1).isEmpty();
      }
   }

   protected double getHeight(int var1, int var2, int var3) {
      if (var1 == CELL_HORIZONTAL_MAX_INDEX_OUTSIDE || var3 == CELL_HORIZONTAL_MAX_INDEX_OUTSIDE) {
         return this.heights[getOutsideIndex(var1, var3)];
      } else {
         return var1 != 0 && var3 != 0 ? 1.7976931348623157E308 : this.heights[getInsideIndex(var1, var3)];
      }
   }

   private double getDensity(@Nullable double[] var1, int var2) {
      if (var1 == null) {
         return 1.7976931348623157E308;
      } else {
         int var3 = this.getCellYIndex(var2);
         return var3 >= 0 && var3 < var1.length ? var1[var3] * 0.1 : 1.7976931348623157E308;
      }
   }

   protected double getDensity(int var1, int var2, int var3) {
      if (var2 == this.getMinY()) {
         return 0.1;
      } else if (var1 == CELL_HORIZONTAL_MAX_INDEX_OUTSIDE || var3 == CELL_HORIZONTAL_MAX_INDEX_OUTSIDE) {
         return this.getDensity(this.densities[getOutsideIndex(var1, var3)], var2);
      } else {
         return var1 != 0 && var3 != 0 ? 1.7976931348623157E308 : this.getDensity(this.densities[getInsideIndex(var1, var3)], var2);
      }
   }

   protected void iterateBiomes(int var1, int var2, int var3, BlendingData.BiomeConsumer var4) {
      if (var2 >= QuartPos.fromBlock(this.areaWithOldGeneration.getMinBuildHeight())
         && var2 < QuartPos.fromBlock(this.areaWithOldGeneration.getMaxBuildHeight())) {
         int var5 = var2 - QuartPos.fromBlock(this.areaWithOldGeneration.getMinBuildHeight());

         for(int var6 = 0; var6 < this.biomes.size(); ++var6) {
            if (this.biomes.get(var6) != null) {
               Holder var7 = this.biomes.get(var6).get(var5);
               if (var7 != null) {
                  var4.consume(var1 + getX(var6), var3 + getZ(var6), var7);
               }
            }
         }
      }
   }

   protected void iterateHeights(int var1, int var2, BlendingData.HeightConsumer var3) {
      for(int var4 = 0; var4 < this.heights.length; ++var4) {
         double var5 = this.heights[var4];
         if (var5 != 1.7976931348623157E308) {
            var3.consume(var1 + getX(var4), var2 + getZ(var4), var5);
         }
      }
   }

   protected void iterateDensities(int var1, int var2, int var3, int var4, BlendingData.DensityConsumer var5) {
      int var6 = this.getColumnMinY();
      int var7 = Math.max(0, var3 - var6);
      int var8 = Math.min(this.cellCountPerColumn(), var4 - var6);

      for(int var9 = 0; var9 < this.densities.length; ++var9) {
         double[] var10 = this.densities[var9];
         if (var10 != null) {
            int var11 = var1 + getX(var9);
            int var12 = var2 + getZ(var9);

            for(int var13 = var7; var13 < var8; ++var13) {
               var5.consume(var11, var13 + var6, var12, var10[var13] * 0.1);
            }
         }
      }
   }

   private int cellCountPerColumn() {
      return this.areaWithOldGeneration.getSectionsCount() * 2;
   }

   private int quartCountPerColumn() {
      return QuartPos.fromSection(this.areaWithOldGeneration.getSectionsCount());
   }

   private int getColumnMinY() {
      return this.getMinY() + 1;
   }

   private int getMinY() {
      return this.areaWithOldGeneration.getMinSection() * 2;
   }

   private int getCellYIndex(int var1) {
      return var1 - this.getColumnMinY();
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

   public LevelHeightAccessor getAreaWithOldGeneration() {
      return this.areaWithOldGeneration;
   }

   protected interface BiomeConsumer {
      void consume(int var1, int var2, Holder<Biome> var3);
   }

   protected interface DensityConsumer {
      void consume(int var1, int var2, int var3, double var4);
   }

   protected interface HeightConsumer {
      void consume(int var1, int var2, double var3);
   }
}
