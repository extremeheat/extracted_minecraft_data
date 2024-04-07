package net.minecraft.world.level.levelgen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList.Builder;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.material.MaterialRuleList;

public class NoiseChunk implements DensityFunction.ContextProvider, DensityFunction.FunctionContext {
   private final NoiseSettings noiseSettings;
   final int cellCountXZ;
   final int cellCountY;
   final int cellNoiseMinY;
   private final int firstCellX;
   private final int firstCellZ;
   final int firstNoiseX;
   final int firstNoiseZ;
   final List<NoiseChunk.NoiseInterpolator> interpolators;
   final List<NoiseChunk.CacheAllInCell> cellCaches;
   private final Map<DensityFunction, DensityFunction> wrapped = new HashMap<>();
   private final Long2IntMap preliminarySurfaceLevel = new Long2IntOpenHashMap();
   private final Aquifer aquifer;
   private final DensityFunction initialDensityNoJaggedness;
   private final NoiseChunk.BlockStateFiller blockStateRule;
   private final Blender blender;
   private final NoiseChunk.FlatCache blendAlpha;
   private final NoiseChunk.FlatCache blendOffset;
   private final DensityFunctions.BeardifierOrMarker beardifier;
   private long lastBlendingDataPos = ChunkPos.INVALID_CHUNK_POS;
   private Blender.BlendingOutput lastBlendingOutput = new Blender.BlendingOutput(1.0, 0.0);
   final int noiseSizeXZ;
   final int cellWidth;
   final int cellHeight;
   boolean interpolating;
   boolean fillingCell;
   private int cellStartBlockX;
   int cellStartBlockY;
   private int cellStartBlockZ;
   int inCellX;
   int inCellY;
   int inCellZ;
   long interpolationCounter;
   long arrayInterpolationCounter;
   int arrayIndex;
   private final DensityFunction.ContextProvider sliceFillingContextProvider = new DensityFunction.ContextProvider() {
      @Override
      public DensityFunction.FunctionContext forIndex(int var1) {
         NoiseChunk.this.cellStartBlockY = (var1 + NoiseChunk.this.cellNoiseMinY) * NoiseChunk.this.cellHeight;
         NoiseChunk.this.interpolationCounter++;
         NoiseChunk.this.inCellY = 0;
         NoiseChunk.this.arrayIndex = var1;
         return NoiseChunk.this;
      }

      @Override
      public void fillAllDirectly(double[] var1, DensityFunction var2) {
         for (int var3 = 0; var3 < NoiseChunk.this.cellCountY + 1; var3++) {
            NoiseChunk.this.cellStartBlockY = (var3 + NoiseChunk.this.cellNoiseMinY) * NoiseChunk.this.cellHeight;
            NoiseChunk.this.interpolationCounter++;
            NoiseChunk.this.inCellY = 0;
            NoiseChunk.this.arrayIndex = var3;
            var1[var3] = var2.compute(NoiseChunk.this);
         }
      }
   };

   public static NoiseChunk forChunk(
      ChunkAccess var0, RandomState var1, DensityFunctions.BeardifierOrMarker var2, NoiseGeneratorSettings var3, Aquifer.FluidPicker var4, Blender var5
   ) {
      NoiseSettings var6 = var3.noiseSettings().clampToHeightAccessor(var0);
      ChunkPos var7 = var0.getPos();
      int var8 = 16 / var6.getCellWidth();
      return new NoiseChunk(var8, var1, var7.getMinBlockX(), var7.getMinBlockZ(), var6, var2, var3, var4, var5);
   }

   public NoiseChunk(
      int var1,
      RandomState var2,
      int var3,
      int var4,
      NoiseSettings var5,
      DensityFunctions.BeardifierOrMarker var6,
      NoiseGeneratorSettings var7,
      Aquifer.FluidPicker var8,
      Blender var9
   ) {
      super();
      this.noiseSettings = var5;
      this.cellWidth = var5.getCellWidth();
      this.cellHeight = var5.getCellHeight();
      this.cellCountXZ = var1;
      this.cellCountY = Mth.floorDiv(var5.height(), this.cellHeight);
      this.cellNoiseMinY = Mth.floorDiv(var5.minY(), this.cellHeight);
      this.firstCellX = Math.floorDiv(var3, this.cellWidth);
      this.firstCellZ = Math.floorDiv(var4, this.cellWidth);
      this.interpolators = Lists.newArrayList();
      this.cellCaches = Lists.newArrayList();
      this.firstNoiseX = QuartPos.fromBlock(var3);
      this.firstNoiseZ = QuartPos.fromBlock(var4);
      this.noiseSizeXZ = QuartPos.fromBlock(var1 * this.cellWidth);
      this.blender = var9;
      this.beardifier = var6;
      this.blendAlpha = new NoiseChunk.FlatCache(new NoiseChunk.BlendAlpha(), false);
      this.blendOffset = new NoiseChunk.FlatCache(new NoiseChunk.BlendOffset(), false);

      for (int var10 = 0; var10 <= this.noiseSizeXZ; var10++) {
         int var11 = this.firstNoiseX + var10;
         int var12 = QuartPos.toBlock(var11);

         for (int var13 = 0; var13 <= this.noiseSizeXZ; var13++) {
            int var14 = this.firstNoiseZ + var13;
            int var15 = QuartPos.toBlock(var14);
            Blender.BlendingOutput var16 = var9.blendOffsetAndFactor(var12, var15);
            this.blendAlpha.values[var10][var13] = var16.alpha();
            this.blendOffset.values[var10][var13] = var16.blendingOffset();
         }
      }

      NoiseRouter var17 = var2.router();
      NoiseRouter var18 = var17.mapAll(this::wrap);
      if (!var7.isAquifersEnabled()) {
         this.aquifer = Aquifer.createDisabled(var8);
      } else {
         int var19 = SectionPos.blockToSectionCoord(var3);
         int var21 = SectionPos.blockToSectionCoord(var4);
         this.aquifer = Aquifer.create(this, new ChunkPos(var19, var21), var18, var2.aquiferRandom(), var5.minY(), var5.height(), var8);
      }

      Builder var20 = ImmutableList.builder();
      DensityFunction var22 = DensityFunctions.cacheAllInCell(DensityFunctions.add(var18.finalDensity(), DensityFunctions.BeardifierMarker.INSTANCE))
         .mapAll(this::wrap);
      var20.add((NoiseChunk.BlockStateFiller)var2x -> this.aquifer.computeSubstance(var2x, var22.compute(var2x)));
      if (var7.oreVeinsEnabled()) {
         var20.add(OreVeinifier.create(var18.veinToggle(), var18.veinRidged(), var18.veinGap(), var2.oreRandom()));
      }

      this.blockStateRule = new MaterialRuleList(var20.build());
      this.initialDensityNoJaggedness = var18.initialDensityWithoutJaggedness();
   }

   protected Climate.Sampler cachedClimateSampler(NoiseRouter var1, List<Climate.ParameterPoint> var2) {
      return new Climate.Sampler(
         var1.temperature().mapAll(this::wrap),
         var1.vegetation().mapAll(this::wrap),
         var1.continents().mapAll(this::wrap),
         var1.erosion().mapAll(this::wrap),
         var1.depth().mapAll(this::wrap),
         var1.ridges().mapAll(this::wrap),
         var2
      );
   }

   @Nullable
   protected BlockState getInterpolatedState() {
      return this.blockStateRule.calculate(this);
   }

   @Override
   public int blockX() {
      return this.cellStartBlockX + this.inCellX;
   }

   @Override
   public int blockY() {
      return this.cellStartBlockY + this.inCellY;
   }

   @Override
   public int blockZ() {
      return this.cellStartBlockZ + this.inCellZ;
   }

   public int preliminarySurfaceLevel(int var1, int var2) {
      int var3 = QuartPos.toBlock(QuartPos.fromBlock(var1));
      int var4 = QuartPos.toBlock(QuartPos.fromBlock(var2));
      return this.preliminarySurfaceLevel.computeIfAbsent(ColumnPos.asLong(var3, var4), this::computePreliminarySurfaceLevel);
   }

   private int computePreliminarySurfaceLevel(long var1) {
      int var3 = ColumnPos.getX(var1);
      int var4 = ColumnPos.getZ(var1);
      int var5 = this.noiseSettings.minY();

      for (int var6 = var5 + this.noiseSettings.height(); var6 >= var5; var6 -= this.cellHeight) {
         if (this.initialDensityNoJaggedness.compute(new DensityFunction.SinglePointContext(var3, var6, var4)) > 0.390625) {
            return var6;
         }
      }

      return 2147483647;
   }

   @Override
   public Blender getBlender() {
      return this.blender;
   }

   private void fillSlice(boolean var1, int var2) {
      this.cellStartBlockX = var2 * this.cellWidth;
      this.inCellX = 0;

      for (int var3 = 0; var3 < this.cellCountXZ + 1; var3++) {
         int var4 = this.firstCellZ + var3;
         this.cellStartBlockZ = var4 * this.cellWidth;
         this.inCellZ = 0;
         this.arrayInterpolationCounter++;

         for (NoiseChunk.NoiseInterpolator var6 : this.interpolators) {
            double[] var7 = (var1 ? var6.slice0 : var6.slice1)[var3];
            var6.fillArray(var7, this.sliceFillingContextProvider);
         }
      }

      this.arrayInterpolationCounter++;
   }

   public void initializeForFirstCellX() {
      if (this.interpolating) {
         throw new IllegalStateException("Staring interpolation twice");
      } else {
         this.interpolating = true;
         this.interpolationCounter = 0L;
         this.fillSlice(true, this.firstCellX);
      }
   }

   public void advanceCellX(int var1) {
      this.fillSlice(false, this.firstCellX + var1 + 1);
      this.cellStartBlockX = (this.firstCellX + var1) * this.cellWidth;
   }

   public NoiseChunk forIndex(int var1) {
      int var2 = Math.floorMod(var1, this.cellWidth);
      int var3 = Math.floorDiv(var1, this.cellWidth);
      int var4 = Math.floorMod(var3, this.cellWidth);
      int var5 = this.cellHeight - 1 - Math.floorDiv(var3, this.cellWidth);
      this.inCellX = var4;
      this.inCellY = var5;
      this.inCellZ = var2;
      this.arrayIndex = var1;
      return this;
   }

   @Override
   public void fillAllDirectly(double[] var1, DensityFunction var2) {
      this.arrayIndex = 0;

      for (int var3 = this.cellHeight - 1; var3 >= 0; var3--) {
         this.inCellY = var3;

         for (int var4 = 0; var4 < this.cellWidth; var4++) {
            this.inCellX = var4;

            for (int var5 = 0; var5 < this.cellWidth; var5++) {
               this.inCellZ = var5;
               var1[this.arrayIndex++] = var2.compute(this);
            }
         }
      }
   }

   public void selectCellYZ(int var1, int var2) {
      this.interpolators.forEach(var2x -> var2x.selectCellYZ(var1, var2));
      this.fillingCell = true;
      this.cellStartBlockY = (var1 + this.cellNoiseMinY) * this.cellHeight;
      this.cellStartBlockZ = (this.firstCellZ + var2) * this.cellWidth;
      this.arrayInterpolationCounter++;

      for (NoiseChunk.CacheAllInCell var4 : this.cellCaches) {
         var4.noiseFiller.fillArray(var4.values, this);
      }

      this.arrayInterpolationCounter++;
      this.fillingCell = false;
   }

   public void updateForY(int var1, double var2) {
      this.inCellY = var1 - this.cellStartBlockY;
      this.interpolators.forEach(var2x -> var2x.updateForY(var2));
   }

   public void updateForX(int var1, double var2) {
      this.inCellX = var1 - this.cellStartBlockX;
      this.interpolators.forEach(var2x -> var2x.updateForX(var2));
   }

   public void updateForZ(int var1, double var2) {
      this.inCellZ = var1 - this.cellStartBlockZ;
      this.interpolationCounter++;
      this.interpolators.forEach(var2x -> var2x.updateForZ(var2));
   }

   public void stopInterpolation() {
      if (!this.interpolating) {
         throw new IllegalStateException("Staring interpolation twice");
      } else {
         this.interpolating = false;
      }
   }

   public void swapSlices() {
      this.interpolators.forEach(NoiseChunk.NoiseInterpolator::swapSlices);
   }

   public Aquifer aquifer() {
      return this.aquifer;
   }

   protected int cellWidth() {
      return this.cellWidth;
   }

   protected int cellHeight() {
      return this.cellHeight;
   }

   Blender.BlendingOutput getOrComputeBlendingOutput(int var1, int var2) {
      long var3 = ChunkPos.asLong(var1, var2);
      if (this.lastBlendingDataPos == var3) {
         return this.lastBlendingOutput;
      } else {
         this.lastBlendingDataPos = var3;
         Blender.BlendingOutput var5 = this.blender.blendOffsetAndFactor(var1, var2);
         this.lastBlendingOutput = var5;
         return var5;
      }
   }

   protected DensityFunction wrap(DensityFunction var1) {
      return this.wrapped.computeIfAbsent(var1, this::wrapNew);
   }

   private DensityFunction wrapNew(DensityFunction var1) {
      if (var1 instanceof DensityFunctions.Marker var3) {
         return (DensityFunction)(switch (var3.type()) {
            case Interpolated -> new NoiseChunk.NoiseInterpolator(var3.wrapped());
            case FlatCache -> new NoiseChunk.FlatCache(var3.wrapped(), true);
            case Cache2D -> new NoiseChunk.Cache2D(var3.wrapped());
            case CacheOnce -> new NoiseChunk.CacheOnce(var3.wrapped());
            case CacheAllInCell -> new NoiseChunk.CacheAllInCell(var3.wrapped());
            default -> throw new MatchException(null, null);
         });
      } else {
         if (this.blender != Blender.empty()) {
            if (var1 == DensityFunctions.BlendAlpha.INSTANCE) {
               return this.blendAlpha;
            }

            if (var1 == DensityFunctions.BlendOffset.INSTANCE) {
               return this.blendOffset;
            }
         }

         if (var1 == DensityFunctions.BeardifierMarker.INSTANCE) {
            return this.beardifier;
         } else {
            return var1 instanceof DensityFunctions.HolderHolder var2 ? var2.function().value() : var1;
         }
      }
   }

   class BlendAlpha implements NoiseChunk.NoiseChunkDensityFunction {
      BlendAlpha() {
         super();
      }

      @Override
      public DensityFunction wrapped() {
         return DensityFunctions.BlendAlpha.INSTANCE;
      }

      @Override
      public DensityFunction mapAll(DensityFunction.Visitor var1) {
         return this.wrapped().mapAll(var1);
      }

      @Override
      public double compute(DensityFunction.FunctionContext var1) {
         return NoiseChunk.this.getOrComputeBlendingOutput(var1.blockX(), var1.blockZ()).alpha();
      }

      @Override
      public void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
         var2.fillAllDirectly(var1, this);
      }

      @Override
      public double minValue() {
         return 0.0;
      }

      @Override
      public double maxValue() {
         return 1.0;
      }

      @Override
      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return DensityFunctions.BlendAlpha.CODEC;
      }
   }

   class BlendOffset implements NoiseChunk.NoiseChunkDensityFunction {
      BlendOffset() {
         super();
      }

      @Override
      public DensityFunction wrapped() {
         return DensityFunctions.BlendOffset.INSTANCE;
      }

      @Override
      public DensityFunction mapAll(DensityFunction.Visitor var1) {
         return this.wrapped().mapAll(var1);
      }

      @Override
      public double compute(DensityFunction.FunctionContext var1) {
         return NoiseChunk.this.getOrComputeBlendingOutput(var1.blockX(), var1.blockZ()).blendingOffset();
      }

      @Override
      public void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
         var2.fillAllDirectly(var1, this);
      }

      @Override
      public double minValue() {
         return -1.0 / 0.0;
      }

      @Override
      public double maxValue() {
         return 1.0 / 0.0;
      }

      @Override
      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return DensityFunctions.BlendOffset.CODEC;
      }
   }

   @FunctionalInterface
   public interface BlockStateFiller {
      @Nullable
      BlockState calculate(DensityFunction.FunctionContext var1);
   }

   static class Cache2D implements DensityFunctions.MarkerOrMarked, NoiseChunk.NoiseChunkDensityFunction {
      private final DensityFunction function;
      private long lastPos2D = ChunkPos.INVALID_CHUNK_POS;
      private double lastValue;

      Cache2D(DensityFunction var1) {
         super();
         this.function = var1;
      }

      @Override
      public double compute(DensityFunction.FunctionContext var1) {
         int var2 = var1.blockX();
         int var3 = var1.blockZ();
         long var4 = ChunkPos.asLong(var2, var3);
         if (this.lastPos2D == var4) {
            return this.lastValue;
         } else {
            this.lastPos2D = var4;
            double var6 = this.function.compute(var1);
            this.lastValue = var6;
            return var6;
         }
      }

      @Override
      public void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
         this.function.fillArray(var1, var2);
      }

      @Override
      public DensityFunction wrapped() {
         return this.function;
      }

      @Override
      public DensityFunctions.Marker.Type type() {
         return DensityFunctions.Marker.Type.Cache2D;
      }
   }

   class CacheAllInCell implements DensityFunctions.MarkerOrMarked, NoiseChunk.NoiseChunkDensityFunction {
      final DensityFunction noiseFiller;
      final double[] values;

      CacheAllInCell(DensityFunction var2) {
         super();
         this.noiseFiller = var2;
         this.values = new double[NoiseChunk.this.cellWidth * NoiseChunk.this.cellWidth * NoiseChunk.this.cellHeight];
         NoiseChunk.this.cellCaches.add(this);
      }

      @Override
      public double compute(DensityFunction.FunctionContext var1) {
         if (var1 != NoiseChunk.this) {
            return this.noiseFiller.compute(var1);
         } else if (!NoiseChunk.this.interpolating) {
            throw new IllegalStateException("Trying to sample interpolator outside the interpolation loop");
         } else {
            int var2 = NoiseChunk.this.inCellX;
            int var3 = NoiseChunk.this.inCellY;
            int var4 = NoiseChunk.this.inCellZ;
            return var2 >= 0
                  && var3 >= 0
                  && var4 >= 0
                  && var2 < NoiseChunk.this.cellWidth
                  && var3 < NoiseChunk.this.cellHeight
                  && var4 < NoiseChunk.this.cellWidth
               ? this.values[((NoiseChunk.this.cellHeight - 1 - var3) * NoiseChunk.this.cellWidth + var2) * NoiseChunk.this.cellWidth + var4]
               : this.noiseFiller.compute(var1);
         }
      }

      @Override
      public void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
         var2.fillAllDirectly(var1, this);
      }

      @Override
      public DensityFunction wrapped() {
         return this.noiseFiller;
      }

      @Override
      public DensityFunctions.Marker.Type type() {
         return DensityFunctions.Marker.Type.CacheAllInCell;
      }
   }

   class CacheOnce implements DensityFunctions.MarkerOrMarked, NoiseChunk.NoiseChunkDensityFunction {
      private final DensityFunction function;
      private long lastCounter;
      private long lastArrayCounter;
      private double lastValue;
      @Nullable
      private double[] lastArray;

      CacheOnce(DensityFunction var2) {
         super();
         this.function = var2;
      }

      @Override
      public double compute(DensityFunction.FunctionContext var1) {
         if (var1 != NoiseChunk.this) {
            return this.function.compute(var1);
         } else if (this.lastArray != null && this.lastArrayCounter == NoiseChunk.this.arrayInterpolationCounter) {
            return this.lastArray[NoiseChunk.this.arrayIndex];
         } else if (this.lastCounter == NoiseChunk.this.interpolationCounter) {
            return this.lastValue;
         } else {
            this.lastCounter = NoiseChunk.this.interpolationCounter;
            double var2 = this.function.compute(var1);
            this.lastValue = var2;
            return var2;
         }
      }

      @Override
      public void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
         if (this.lastArray != null && this.lastArrayCounter == NoiseChunk.this.arrayInterpolationCounter) {
            System.arraycopy(this.lastArray, 0, var1, 0, var1.length);
         } else {
            this.wrapped().fillArray(var1, var2);
            if (this.lastArray != null && this.lastArray.length == var1.length) {
               System.arraycopy(var1, 0, this.lastArray, 0, var1.length);
            } else {
               this.lastArray = (double[])var1.clone();
            }

            this.lastArrayCounter = NoiseChunk.this.arrayInterpolationCounter;
         }
      }

      @Override
      public DensityFunction wrapped() {
         return this.function;
      }

      @Override
      public DensityFunctions.Marker.Type type() {
         return DensityFunctions.Marker.Type.CacheOnce;
      }
   }

   class FlatCache implements DensityFunctions.MarkerOrMarked, NoiseChunk.NoiseChunkDensityFunction {
      private final DensityFunction noiseFiller;
      final double[][] values;

      FlatCache(DensityFunction var2, boolean var3) {
         super();
         this.noiseFiller = var2;
         this.values = new double[NoiseChunk.this.noiseSizeXZ + 1][NoiseChunk.this.noiseSizeXZ + 1];
         if (var3) {
            for (int var4 = 0; var4 <= NoiseChunk.this.noiseSizeXZ; var4++) {
               int var5 = NoiseChunk.this.firstNoiseX + var4;
               int var6 = QuartPos.toBlock(var5);

               for (int var7 = 0; var7 <= NoiseChunk.this.noiseSizeXZ; var7++) {
                  int var8 = NoiseChunk.this.firstNoiseZ + var7;
                  int var9 = QuartPos.toBlock(var8);
                  this.values[var4][var7] = var2.compute(new DensityFunction.SinglePointContext(var6, 0, var9));
               }
            }
         }
      }

      @Override
      public double compute(DensityFunction.FunctionContext var1) {
         int var2 = QuartPos.fromBlock(var1.blockX());
         int var3 = QuartPos.fromBlock(var1.blockZ());
         int var4 = var2 - NoiseChunk.this.firstNoiseX;
         int var5 = var3 - NoiseChunk.this.firstNoiseZ;
         int var6 = this.values.length;
         return var4 >= 0 && var5 >= 0 && var4 < var6 && var5 < var6 ? this.values[var4][var5] : this.noiseFiller.compute(var1);
      }

      @Override
      public void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
         var2.fillAllDirectly(var1, this);
      }

      @Override
      public DensityFunction wrapped() {
         return this.noiseFiller;
      }

      @Override
      public DensityFunctions.Marker.Type type() {
         return DensityFunctions.Marker.Type.FlatCache;
      }
   }

   interface NoiseChunkDensityFunction extends DensityFunction {
      DensityFunction wrapped();

      @Override
      default double minValue() {
         return this.wrapped().minValue();
      }

      @Override
      default double maxValue() {
         return this.wrapped().maxValue();
      }
   }

   public class NoiseInterpolator implements DensityFunctions.MarkerOrMarked, NoiseChunk.NoiseChunkDensityFunction {
      double[][] slice0;
      double[][] slice1;
      private final DensityFunction noiseFiller;
      private double noise000;
      private double noise001;
      private double noise100;
      private double noise101;
      private double noise010;
      private double noise011;
      private double noise110;
      private double noise111;
      private double valueXZ00;
      private double valueXZ10;
      private double valueXZ01;
      private double valueXZ11;
      private double valueZ0;
      private double valueZ1;
      private double value;

      NoiseInterpolator(DensityFunction var2) {
         super();
         this.noiseFiller = var2;
         this.slice0 = this.allocateSlice(NoiseChunk.this.cellCountY, NoiseChunk.this.cellCountXZ);
         this.slice1 = this.allocateSlice(NoiseChunk.this.cellCountY, NoiseChunk.this.cellCountXZ);
         NoiseChunk.this.interpolators.add(this);
      }

      private double[][] allocateSlice(int var1, int var2) {
         int var3 = var2 + 1;
         int var4 = var1 + 1;
         double[][] var5 = new double[var3][var4];

         for (int var6 = 0; var6 < var3; var6++) {
            var5[var6] = new double[var4];
         }

         return var5;
      }

      void selectCellYZ(int var1, int var2) {
         this.noise000 = this.slice0[var2][var1];
         this.noise001 = this.slice0[var2 + 1][var1];
         this.noise100 = this.slice1[var2][var1];
         this.noise101 = this.slice1[var2 + 1][var1];
         this.noise010 = this.slice0[var2][var1 + 1];
         this.noise011 = this.slice0[var2 + 1][var1 + 1];
         this.noise110 = this.slice1[var2][var1 + 1];
         this.noise111 = this.slice1[var2 + 1][var1 + 1];
      }

      void updateForY(double var1) {
         this.valueXZ00 = Mth.lerp(var1, this.noise000, this.noise010);
         this.valueXZ10 = Mth.lerp(var1, this.noise100, this.noise110);
         this.valueXZ01 = Mth.lerp(var1, this.noise001, this.noise011);
         this.valueXZ11 = Mth.lerp(var1, this.noise101, this.noise111);
      }

      void updateForX(double var1) {
         this.valueZ0 = Mth.lerp(var1, this.valueXZ00, this.valueXZ10);
         this.valueZ1 = Mth.lerp(var1, this.valueXZ01, this.valueXZ11);
      }

      void updateForZ(double var1) {
         this.value = Mth.lerp(var1, this.valueZ0, this.valueZ1);
      }

      @Override
      public double compute(DensityFunction.FunctionContext var1) {
         if (var1 != NoiseChunk.this) {
            return this.noiseFiller.compute(var1);
         } else if (!NoiseChunk.this.interpolating) {
            throw new IllegalStateException("Trying to sample interpolator outside the interpolation loop");
         } else {
            return NoiseChunk.this.fillingCell
               ? Mth.lerp3(
                  (double)NoiseChunk.this.inCellX / (double)NoiseChunk.this.cellWidth,
                  (double)NoiseChunk.this.inCellY / (double)NoiseChunk.this.cellHeight,
                  (double)NoiseChunk.this.inCellZ / (double)NoiseChunk.this.cellWidth,
                  this.noise000,
                  this.noise100,
                  this.noise010,
                  this.noise110,
                  this.noise001,
                  this.noise101,
                  this.noise011,
                  this.noise111
               )
               : this.value;
         }
      }

      @Override
      public void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
         if (NoiseChunk.this.fillingCell) {
            var2.fillAllDirectly(var1, this);
         } else {
            this.wrapped().fillArray(var1, var2);
         }
      }

      @Override
      public DensityFunction wrapped() {
         return this.noiseFiller;
      }

      private void swapSlices() {
         double[][] var1 = this.slice0;
         this.slice0 = this.slice1;
         this.slice1 = var1;
      }

      @Override
      public DensityFunctions.Marker.Type type() {
         return DensityFunctions.Marker.Type.Interpolated;
      }
   }
}
