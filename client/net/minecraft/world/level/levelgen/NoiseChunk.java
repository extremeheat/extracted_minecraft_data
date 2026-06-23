package net.minecraft.world.level.levelgen;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.core.Holder;
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
import org.jspecify.annotations.Nullable;

public class NoiseChunk implements DensityFunction.FunctionContext, DensityFunction.ContextProvider {
   private final int cellCountXZ;
   private final int cellCountY;
   private final int cellNoiseMinY;
   private final int firstCellX;
   private final int firstCellZ;
   private final int firstNoiseX;
   private final int firstNoiseZ;
   private final List<NoiseInterpolator> interpolators;
   private final List<CacheAllInCell> cellCaches;
   private final Map<DensityFunction, DensityFunction> wrapped = new HashMap();
   private final Long2IntMap preliminarySurfaceLevelCache = new Long2IntOpenHashMap();
   private final Aquifer aquifer;
   private final DensityFunction preliminarySurfaceLevel;
   private final DensityFunction fullNoiseDensity;
   private final BlockStateFiller blockStateRule;
   private final Blender blender;
   private final FlatCache blendAlpha;
   private final FlatCache blendOffset;
   private final DensityFunctions.BeardifierOrMarker beardifier;
   private long lastBlendingDataPos;
   private Blender.BlendingOutput lastBlendingOutput;
   private final int noiseSizeXZ;
   private final int cellWidth;
   private final int cellHeight;
   private boolean interpolating;
   private boolean fillingCell;
   private int cellStartBlockX;
   private int cellStartBlockY;
   private int cellStartBlockZ;
   private int inCellX;
   private int inCellY;
   private int inCellZ;
   private long interpolationCounter;
   private long arrayInterpolationCounter;
   private int arrayIndex;
   private final DensityFunction.ContextProvider sliceFillingContextProvider;

   public static NoiseChunk forChunk(final ChunkAccess chunk, final RandomState randomState, final DensityFunctions.BeardifierOrMarker beardifier, final NoiseGeneratorSettings settings, final Aquifer.FluidPicker globalFluidPicker, final Blender blender) {
      NoiseSettings noiseSettings = settings.noiseSettings().clampToHeightAccessor(chunk);
      ChunkPos pos = chunk.getPos();
      int cellCountXZ = 16 / noiseSettings.getCellWidth();
      return new NoiseChunk(cellCountXZ, randomState, pos.getMinBlockX(), pos.getMinBlockZ(), noiseSettings, beardifier, settings, globalFluidPicker, blender);
   }

   public NoiseChunk(final int cellCountXZ, final RandomState randomState, final int chunkMinBlockX, final int chunkMinBlockZ, final NoiseSettings noiseSettings, final DensityFunctions.BeardifierOrMarker beardifier, final NoiseGeneratorSettings settings, final Aquifer.FluidPicker globalFluidPicker, final Blender blender) {
      super();
      this.lastBlendingDataPos = ChunkPos.INVALID_CHUNK_POS;
      this.lastBlendingOutput = new Blender.BlendingOutput(1.0, 0.0);
      this.sliceFillingContextProvider = new DensityFunction.ContextProvider() {
         {
            Objects.requireNonNull(NoiseChunk.this);
         }

         public DensityFunction.FunctionContext forIndex(final int cellYIndex) {
            NoiseChunk.this.cellStartBlockY = (cellYIndex + NoiseChunk.this.cellNoiseMinY) * NoiseChunk.this.cellHeight;
            ++NoiseChunk.this.interpolationCounter;
            NoiseChunk.this.inCellY = 0;
            NoiseChunk.this.arrayIndex = cellYIndex;
            return NoiseChunk.this;
         }

         public void fillAllDirectly(final double[] output, final DensityFunction function) {
            for(int cellYIndex = 0; cellYIndex < NoiseChunk.this.cellCountY + 1; ++cellYIndex) {
               NoiseChunk.this.cellStartBlockY = (cellYIndex + NoiseChunk.this.cellNoiseMinY) * NoiseChunk.this.cellHeight;
               ++NoiseChunk.this.interpolationCounter;
               NoiseChunk.this.inCellY = 0;
               NoiseChunk.this.arrayIndex = cellYIndex;
               output[cellYIndex] = function.compute(NoiseChunk.this);
            }

         }
      };
      this.cellWidth = noiseSettings.getCellWidth();
      this.cellHeight = noiseSettings.getCellHeight();
      this.cellCountXZ = cellCountXZ;
      this.cellCountY = Mth.floorDiv(noiseSettings.height(), this.cellHeight);
      this.cellNoiseMinY = Mth.floorDiv(noiseSettings.minY(), this.cellHeight);
      this.firstCellX = Math.floorDiv(chunkMinBlockX, this.cellWidth);
      this.firstCellZ = Math.floorDiv(chunkMinBlockZ, this.cellWidth);
      this.interpolators = Lists.newArrayList();
      this.cellCaches = Lists.newArrayList();
      this.firstNoiseX = QuartPos.fromBlock(chunkMinBlockX);
      this.firstNoiseZ = QuartPos.fromBlock(chunkMinBlockZ);
      this.noiseSizeXZ = QuartPos.fromBlock(cellCountXZ * this.cellWidth);
      this.blender = blender;
      this.beardifier = beardifier;
      if (!blender.isEmpty()) {
         this.blendAlpha = new FlatCache(new BlendAlpha(), false);
         this.blendOffset = new FlatCache(new BlendOffset(), false);

         for(int x = 0; x <= this.noiseSizeXZ; ++x) {
            int quartX = this.firstNoiseX + x;
            int blockX = QuartPos.toBlock(quartX);

            for(int z = 0; z <= this.noiseSizeXZ; ++z) {
               int quartZ = this.firstNoiseZ + z;
               int blockZ = QuartPos.toBlock(quartZ);
               Blender.BlendingOutput blendingOutput = blender.blendOffsetAndFactor(blockX, blockZ);
               this.blendAlpha.values[x + z * this.blendAlpha.sizeXZ] = blendingOutput.alpha();
               this.blendOffset.values[x + z * this.blendOffset.sizeXZ] = blendingOutput.blendingOffset();
            }
         }
      } else {
         this.blendAlpha = null;
         this.blendOffset = null;
      }

      NoiseRouter router = randomState.router();
      NoiseRouter wrappedRouter = router.mapAll(this::wrap);
      this.preliminarySurfaceLevel = wrappedRouter.preliminarySurfaceLevel();
      if (!settings.isAquifersEnabled()) {
         this.aquifer = Aquifer.createDisabled(globalFluidPicker);
      } else {
         int chunkX = SectionPos.blockToSectionCoord(chunkMinBlockX);
         int chunkZ = SectionPos.blockToSectionCoord(chunkMinBlockZ);
         this.aquifer = Aquifer.create(this, new ChunkPos(chunkX, chunkZ), wrappedRouter, randomState.aquiferRandom(), noiseSettings.minY(), noiseSettings.height(), globalFluidPicker);
      }

      List<BlockStateFiller> builder = new ArrayList();
      DensityFunction fullNoiseValue = DensityFunctions.cacheAllInCell(DensityFunctions.add(wrappedRouter.finalDensity(), DensityFunctions.BeardifierMarker.INSTANCE)).mapAll(this::wrap);
      this.fullNoiseDensity = fullNoiseValue;
      builder.add((BlockStateFiller)(context) -> this.aquifer.computeSubstance(context, fullNoiseValue.compute(context)));
      if (settings.oreVeinsEnabled()) {
         builder.add(OreVeinifier.create(wrappedRouter.veinToggle(), wrappedRouter.veinRidged(), wrappedRouter.veinGap(), randomState.oreRandom()));
      }

      this.blockStateRule = new MaterialRuleList((BlockStateFiller[])builder.toArray(new BlockStateFiller[0]));
   }

   protected Climate.Sampler cachedClimateSampler(final NoiseRouter noises) {
      return new Climate.Sampler(noises.temperature().mapAll(this::wrap), noises.vegetation().mapAll(this::wrap), noises.continents().mapAll(this::wrap), noises.erosion().mapAll(this::wrap), noises.depth().mapAll(this::wrap), noises.ridges().mapAll(this::wrap));
   }

   protected @Nullable BlockState getInterpolatedState() {
      return this.blockStateRule.calculate(this);
   }

   protected double getInterpolatedDensity() {
      return this.fullNoiseDensity.compute(this);
   }

   public int blockX() {
      return this.cellStartBlockX + this.inCellX;
   }

   public int blockY() {
      return this.cellStartBlockY + this.inCellY;
   }

   public int blockZ() {
      return this.cellStartBlockZ + this.inCellZ;
   }

   public int maxPreliminarySurfaceLevel(final int minBlockX, final int minBlockZ, final int maxBlockX, final int maxBlockZ) {
      int maxY = -2147483648;

      for(int blockZ = minBlockZ; blockZ <= maxBlockZ; blockZ += 4) {
         for(int blockX = minBlockX; blockX <= maxBlockX; blockX += 4) {
            int surfaceLevel = this.preliminarySurfaceLevel(blockX, blockZ);
            if (surfaceLevel > maxY) {
               maxY = surfaceLevel;
            }
         }
      }

      return maxY;
   }

   public int preliminarySurfaceLevel(final int sampleX, final int sampleZ) {
      int quantizedX = QuartPos.toBlock(QuartPos.fromBlock(sampleX));
      int quantizedZ = QuartPos.toBlock(QuartPos.fromBlock(sampleZ));
      return this.preliminarySurfaceLevelCache.computeIfAbsent(ColumnPos.asLong(quantizedX, quantizedZ), this::computePreliminarySurfaceLevel);
   }

   private int computePreliminarySurfaceLevel(final long key) {
      int blockX = ColumnPos.getX(key);
      int blockZ = ColumnPos.getZ(key);
      return Mth.floor(this.preliminarySurfaceLevel.compute(new DensityFunction.SinglePointContext(blockX, 0, blockZ)));
   }

   private void fillSlice(final boolean slice0, final int cellX) {
      this.cellStartBlockX = cellX * this.cellWidth;
      this.inCellX = 0;

      for(int cellZIndex = 0; cellZIndex < this.cellCountXZ + 1; ++cellZIndex) {
         int cellZ = this.firstCellZ + cellZIndex;
         this.cellStartBlockZ = cellZ * this.cellWidth;
         this.inCellZ = 0;
         ++this.arrayInterpolationCounter;

         for(NoiseInterpolator noiseInterpolator : this.interpolators) {
            double[] slice = (slice0 ? noiseInterpolator.slice0 : noiseInterpolator.slice1)[cellZIndex];
            noiseInterpolator.fillArray(slice, this.sliceFillingContextProvider);
         }
      }

      ++this.arrayInterpolationCounter;
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

   public void advanceCellX(final int cellXIndex) {
      this.fillSlice(false, this.firstCellX + cellXIndex + 1);
      this.cellStartBlockX = (this.firstCellX + cellXIndex) * this.cellWidth;
   }

   public NoiseChunk forIndex(final int cellIndex) {
      int zInCell = Math.floorMod(cellIndex, this.cellWidth);
      int xyIndex = Math.floorDiv(cellIndex, this.cellWidth);
      int xInCell = Math.floorMod(xyIndex, this.cellWidth);
      int yInCell = this.cellHeight - 1 - Math.floorDiv(xyIndex, this.cellWidth);
      this.inCellX = xInCell;
      this.inCellY = yInCell;
      this.inCellZ = zInCell;
      this.arrayIndex = cellIndex;
      return this;
   }

   public void fillAllDirectly(final double[] output, final DensityFunction function) {
      this.arrayIndex = 0;

      for(int yInCell = this.cellHeight - 1; yInCell >= 0; --yInCell) {
         this.inCellY = yInCell;

         for(int xInCell = 0; xInCell < this.cellWidth; ++xInCell) {
            this.inCellX = xInCell;

            for(int zInCell = 0; zInCell < this.cellWidth; ++zInCell) {
               this.inCellZ = zInCell;
               output[this.arrayIndex++] = function.compute(this);
            }
         }
      }

   }

   public void selectCellYZ(final int cellYIndex, final int cellZIndex) {
      for(NoiseInterpolator i : this.interpolators) {
         i.selectCellYZ(cellYIndex, cellZIndex);
      }

      this.fillingCell = true;
      this.cellStartBlockY = (cellYIndex + this.cellNoiseMinY) * this.cellHeight;
      this.cellStartBlockZ = (this.firstCellZ + cellZIndex) * this.cellWidth;
      ++this.arrayInterpolationCounter;

      for(CacheAllInCell cellCache : this.cellCaches) {
         cellCache.noiseFiller.fillArray(cellCache.values, this);
      }

      ++this.arrayInterpolationCounter;
      this.fillingCell = false;
   }

   public void updateForY(final int posY, final double factorY) {
      this.inCellY = posY - this.cellStartBlockY;

      for(NoiseInterpolator i : this.interpolators) {
         i.updateForY(factorY);
      }

   }

   public void updateForX(final int posX, final double factorX) {
      this.inCellX = posX - this.cellStartBlockX;

      for(NoiseInterpolator i : this.interpolators) {
         i.updateForX(factorX);
      }

   }

   public void updateForZ(final int posZ, final double factorZ) {
      this.inCellZ = posZ - this.cellStartBlockZ;
      ++this.interpolationCounter;

      for(NoiseInterpolator i : this.interpolators) {
         i.updateForZ(factorZ);
      }

   }

   public void stopInterpolation() {
      if (!this.interpolating) {
         throw new IllegalStateException("Staring interpolation twice");
      } else {
         this.interpolating = false;
      }
   }

   public void swapSlices() {
      this.interpolators.forEach(NoiseInterpolator::swapSlices);
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

   private Blender.BlendingOutput getOrComputeBlendingOutput(final int blockX, final int blockZ) {
      long pos2D = ChunkPos.pack(blockX, blockZ);
      if (this.lastBlendingDataPos == pos2D) {
         return this.lastBlendingOutput;
      } else {
         this.lastBlendingDataPos = pos2D;
         Blender.BlendingOutput output = this.blender.blendOffsetAndFactor(blockX, blockZ);
         this.lastBlendingOutput = output;
         return output;
      }
   }

   protected DensityFunction wrap(final DensityFunction function) {
      return (DensityFunction)this.wrapped.computeIfAbsent(function, this::wrapNew);
   }

   private DensityFunction wrapNew(final DensityFunction function) {
      Objects.requireNonNull(function);
      byte var3 = 0;
      Object var16;
      //$FF: var3->value
      //0->net/minecraft/world/level/levelgen/DensityFunctions$Marker
      //1->net/minecraft/world/level/levelgen/DensityFunctions$HolderHolder
      switch (function.typeSwitch<invokedynamic>(function, var3)) {
         case 0:
            DensityFunctions.Marker var4 = (DensityFunctions.Marker)function;
            var16 = var4;

            try {
               var18 = var16.type();
            } catch (Throwable var12) {
               throw new MatchException(var12.toString(), var12);
            }

            DensityFunctions.Marker.Type wrapped = var18;
            DensityFunctions.Marker.Type type = wrapped;
            var16 = var4;

            try {
               var16 = var16.wrapped();
            } catch (Throwable var11) {
               throw new MatchException(var11.toString(), var11);
            }

            DensityFunction wrapped = var16;
            switch (type) {
               case Interpolated:
                  var16 = new NoiseInterpolator(wrapped);
                  return (DensityFunction)var16;
               case FlatCache:
                  var16 = new FlatCache(wrapped, true);
                  return (DensityFunction)var16;
               case Cache2D:
                  var16 = new Cache2D(wrapped);
                  return (DensityFunction)var16;
               case CacheOnce:
                  var16 = new CacheOnce(wrapped);
                  return (DensityFunction)var16;
               case CacheAllInCell:
                  var16 = new CacheAllInCell(wrapped);
                  return (DensityFunction)var16;
               case BlendDensity:
                  var16 = (DensityFunction)(!this.blender.isEmpty() ? new BlendDensity(wrapped) : wrapped);
                  return (DensityFunction)var16;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }
         case 1:
            DensityFunctions.HolderHolder wrapped = (DensityFunctions.HolderHolder)function;
            var16 = wrapped;

            try {
               var15 = var16.function();
            } catch (Throwable var10) {
               throw new MatchException(var10.toString(), var10);
            }

            Holder holder = var15;
            var16 = (DensityFunction)holder.value();
            break;
         default:
            var16 = (DensityFunction)(function == DensityFunctions.BlendAlpha.INSTANCE && this.blendAlpha != null ? this.blendAlpha : (function == DensityFunctions.BlendOffset.INSTANCE && this.blendOffset != null ? this.blendOffset : (function == DensityFunctions.BeardifierMarker.INSTANCE ? this.beardifier : function)));
      }

      return var16;
   }

   private interface NoiseChunkDensityFunction extends DensityFunction {
      DensityFunction wrapped();

      default double minValue() {
         return this.wrapped().minValue();
      }

      default double maxValue() {
         return this.wrapped().maxValue();
      }
   }

   private class FlatCache implements NoiseChunkDensityFunction, DensityFunctions.MarkerOrMarked {
      private final DensityFunction noiseFiller;
      private final double[] values;
      private final int sizeXZ;

      private FlatCache(final DensityFunction noiseFiller, final boolean fill) {
         Objects.requireNonNull(NoiseChunk.this);
         super();
         this.noiseFiller = noiseFiller;
         this.sizeXZ = NoiseChunk.this.noiseSizeXZ + 1;
         this.values = new double[this.sizeXZ * this.sizeXZ];
         if (fill) {
            for(int x = 0; x <= NoiseChunk.this.noiseSizeXZ; ++x) {
               int quartX = NoiseChunk.this.firstNoiseX + x;
               int blockX = QuartPos.toBlock(quartX);

               for(int z = 0; z <= NoiseChunk.this.noiseSizeXZ; ++z) {
                  int quartZ = NoiseChunk.this.firstNoiseZ + z;
                  int blockZ = QuartPos.toBlock(quartZ);
                  this.values[x + z * this.sizeXZ] = noiseFiller.compute(new DensityFunction.SinglePointContext(blockX, 0, blockZ));
               }
            }
         }

      }

      public double compute(final DensityFunction.FunctionContext context) {
         int quartX = QuartPos.fromBlock(context.blockX());
         int quartZ = QuartPos.fromBlock(context.blockZ());
         int x = quartX - NoiseChunk.this.firstNoiseX;
         int z = quartZ - NoiseChunk.this.firstNoiseZ;
         return x >= 0 && z >= 0 && x < this.sizeXZ && z < this.sizeXZ ? this.values[x + z * this.sizeXZ] : this.noiseFiller.compute(context);
      }

      public void fillArray(final double[] output, final DensityFunction.ContextProvider contextProvider) {
         contextProvider.fillAllDirectly(output, this);
      }

      public DensityFunction wrapped() {
         return this.noiseFiller;
      }

      public DensityFunctions.Marker.Type type() {
         return DensityFunctions.Marker.Type.FlatCache;
      }
   }

   private class CacheAllInCell implements NoiseChunkDensityFunction, DensityFunctions.MarkerOrMarked {
      private final DensityFunction noiseFiller;
      private final double[] values;

      private CacheAllInCell(final DensityFunction noiseFiller) {
         Objects.requireNonNull(NoiseChunk.this);
         super();
         this.noiseFiller = noiseFiller;
         this.values = new double[NoiseChunk.this.cellWidth * NoiseChunk.this.cellWidth * NoiseChunk.this.cellHeight];
         NoiseChunk.this.cellCaches.add(this);
      }

      public double compute(final DensityFunction.FunctionContext context) {
         if (context != NoiseChunk.this) {
            return this.noiseFiller.compute(context);
         } else if (!NoiseChunk.this.interpolating) {
            throw new IllegalStateException("Trying to sample interpolator outside the interpolation loop");
         } else {
            int x = NoiseChunk.this.inCellX;
            int y = NoiseChunk.this.inCellY;
            int z = NoiseChunk.this.inCellZ;
            return x >= 0 && y >= 0 && z >= 0 && x < NoiseChunk.this.cellWidth && y < NoiseChunk.this.cellHeight && z < NoiseChunk.this.cellWidth ? this.values[((NoiseChunk.this.cellHeight - 1 - y) * NoiseChunk.this.cellWidth + x) * NoiseChunk.this.cellWidth + z] : this.noiseFiller.compute(context);
         }
      }

      public void fillArray(final double[] output, final DensityFunction.ContextProvider contextProvider) {
         contextProvider.fillAllDirectly(output, this);
      }

      public DensityFunction wrapped() {
         return this.noiseFiller;
      }

      public DensityFunctions.Marker.Type type() {
         return DensityFunctions.Marker.Type.CacheAllInCell;
      }
   }

   public class NoiseInterpolator implements NoiseChunkDensityFunction, DensityFunctions.MarkerOrMarked {
      private double[][] slice0;
      private double[][] slice1;
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

      private NoiseInterpolator(final DensityFunction noiseFiller) {
         Objects.requireNonNull(NoiseChunk.this);
         super();
         this.noiseFiller = noiseFiller;
         this.slice0 = this.allocateSlice(NoiseChunk.this.cellCountY, NoiseChunk.this.cellCountXZ);
         this.slice1 = this.allocateSlice(NoiseChunk.this.cellCountY, NoiseChunk.this.cellCountXZ);
         NoiseChunk.this.interpolators.add(this);
      }

      private double[][] allocateSlice(final int cellCountY, final int cellCountZ) {
         int sizeZ = cellCountZ + 1;
         int sizeY = cellCountY + 1;
         double[][] result = new double[sizeZ][sizeY];

         for(int cellZIndex = 0; cellZIndex < sizeZ; ++cellZIndex) {
            result[cellZIndex] = new double[sizeY];
         }

         return result;
      }

      private void selectCellYZ(final int cellYIndex, final int cellZIndex) {
         this.noise000 = this.slice0[cellZIndex][cellYIndex];
         this.noise001 = this.slice0[cellZIndex + 1][cellYIndex];
         this.noise100 = this.slice1[cellZIndex][cellYIndex];
         this.noise101 = this.slice1[cellZIndex + 1][cellYIndex];
         this.noise010 = this.slice0[cellZIndex][cellYIndex + 1];
         this.noise011 = this.slice0[cellZIndex + 1][cellYIndex + 1];
         this.noise110 = this.slice1[cellZIndex][cellYIndex + 1];
         this.noise111 = this.slice1[cellZIndex + 1][cellYIndex + 1];
      }

      private void updateForY(final double factorY) {
         this.valueXZ00 = Mth.lerp(factorY, this.noise000, this.noise010);
         this.valueXZ10 = Mth.lerp(factorY, this.noise100, this.noise110);
         this.valueXZ01 = Mth.lerp(factorY, this.noise001, this.noise011);
         this.valueXZ11 = Mth.lerp(factorY, this.noise101, this.noise111);
      }

      private void updateForX(final double factorX) {
         this.valueZ0 = Mth.lerp(factorX, this.valueXZ00, this.valueXZ10);
         this.valueZ1 = Mth.lerp(factorX, this.valueXZ01, this.valueXZ11);
      }

      private void updateForZ(final double factorZ) {
         this.value = Mth.lerp(factorZ, this.valueZ0, this.valueZ1);
      }

      public double compute(final DensityFunction.FunctionContext context) {
         if (context != NoiseChunk.this) {
            return this.noiseFiller.compute(context);
         } else if (!NoiseChunk.this.interpolating) {
            throw new IllegalStateException("Trying to sample interpolator outside the interpolation loop");
         } else {
            return NoiseChunk.this.fillingCell ? Mth.lerp3((double)NoiseChunk.this.inCellX / (double)NoiseChunk.this.cellWidth, (double)NoiseChunk.this.inCellY / (double)NoiseChunk.this.cellHeight, (double)NoiseChunk.this.inCellZ / (double)NoiseChunk.this.cellWidth, this.noise000, this.noise100, this.noise010, this.noise110, this.noise001, this.noise101, this.noise011, this.noise111) : this.value;
         }
      }

      public void fillArray(final double[] output, final DensityFunction.ContextProvider contextProvider) {
         if (NoiseChunk.this.fillingCell) {
            contextProvider.fillAllDirectly(output, this);
         } else {
            this.wrapped().fillArray(output, contextProvider);
         }
      }

      public DensityFunction wrapped() {
         return this.noiseFiller;
      }

      private void swapSlices() {
         double[][] tmp = this.slice0;
         this.slice0 = this.slice1;
         this.slice1 = tmp;
      }

      public DensityFunctions.Marker.Type type() {
         return DensityFunctions.Marker.Type.Interpolated;
      }
   }

   private class CacheOnce implements NoiseChunkDensityFunction, DensityFunctions.MarkerOrMarked {
      private final DensityFunction function;
      private long lastCounter;
      private long lastArrayCounter;
      private double lastValue;
      private double @Nullable [] lastArray;

      private CacheOnce(final DensityFunction function) {
         Objects.requireNonNull(NoiseChunk.this);
         super();
         this.function = function;
      }

      public double compute(final DensityFunction.FunctionContext context) {
         if (context != NoiseChunk.this) {
            return this.function.compute(context);
         } else if (this.lastArray != null && this.lastArrayCounter == NoiseChunk.this.arrayInterpolationCounter) {
            return this.lastArray[NoiseChunk.this.arrayIndex];
         } else if (this.lastCounter == NoiseChunk.this.interpolationCounter) {
            return this.lastValue;
         } else {
            this.lastCounter = NoiseChunk.this.interpolationCounter;
            double value = this.function.compute(context);
            this.lastValue = value;
            return value;
         }
      }

      public void fillArray(final double[] output, final DensityFunction.ContextProvider contextProvider) {
         if (this.lastArray != null && this.lastArrayCounter == NoiseChunk.this.arrayInterpolationCounter) {
            System.arraycopy(this.lastArray, 0, output, 0, output.length);
         } else {
            this.wrapped().fillArray(output, contextProvider);
            if (this.lastArray != null && this.lastArray.length == output.length) {
               System.arraycopy(output, 0, this.lastArray, 0, output.length);
            } else {
               this.lastArray = (double[])(([D)output).clone();
            }

            this.lastArrayCounter = NoiseChunk.this.arrayInterpolationCounter;
         }
      }

      public DensityFunction wrapped() {
         return this.function;
      }

      public DensityFunctions.Marker.Type type() {
         return DensityFunctions.Marker.Type.CacheOnce;
      }
   }

   private static class Cache2D implements NoiseChunkDensityFunction, DensityFunctions.MarkerOrMarked {
      private final DensityFunction function;
      private long lastPos2D;
      private double lastValue;

      private Cache2D(final DensityFunction function) {
         super();
         this.lastPos2D = ChunkPos.INVALID_CHUNK_POS;
         this.function = function;
      }

      public double compute(final DensityFunction.FunctionContext context) {
         int blockX = context.blockX();
         int blockZ = context.blockZ();
         long pos2D = ChunkPos.pack(blockX, blockZ);
         if (this.lastPos2D == pos2D) {
            return this.lastValue;
         } else {
            this.lastPos2D = pos2D;
            double value = this.function.compute(context);
            this.lastValue = value;
            return value;
         }
      }

      public void fillArray(final double[] output, final DensityFunction.ContextProvider contextProvider) {
         this.function.fillArray(output, contextProvider);
      }

      public DensityFunction wrapped() {
         return this.function;
      }

      public DensityFunctions.Marker.Type type() {
         return DensityFunctions.Marker.Type.Cache2D;
      }
   }

   private class BlendAlpha implements NoiseChunkDensityFunction {
      private BlendAlpha() {
         Objects.requireNonNull(NoiseChunk.this);
         super();
      }

      public DensityFunction wrapped() {
         return DensityFunctions.BlendAlpha.INSTANCE;
      }

      public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
         return visitor.apply(this.wrapped());
      }

      public double compute(final DensityFunction.FunctionContext context) {
         return NoiseChunk.this.getOrComputeBlendingOutput(context.blockX(), context.blockZ()).alpha();
      }

      public void fillArray(final double[] output, final DensityFunction.ContextProvider contextProvider) {
         contextProvider.fillAllDirectly(output, this);
      }

      public double minValue() {
         return 0.0;
      }

      public double maxValue() {
         return 1.0;
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return DensityFunctions.BlendAlpha.CODEC;
      }
   }

   private class BlendOffset implements NoiseChunkDensityFunction {
      private BlendOffset() {
         Objects.requireNonNull(NoiseChunk.this);
         super();
      }

      public DensityFunction wrapped() {
         return DensityFunctions.BlendOffset.INSTANCE;
      }

      public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
         return visitor.apply(this.wrapped());
      }

      public double compute(final DensityFunction.FunctionContext context) {
         return NoiseChunk.this.getOrComputeBlendingOutput(context.blockX(), context.blockZ()).blendingOffset();
      }

      public void fillArray(final double[] output, final DensityFunction.ContextProvider contextProvider) {
         contextProvider.fillAllDirectly(output, this);
      }

      public double minValue() {
         return -1.0 / 0.0;
      }

      public double maxValue() {
         return 1.0 / 0.0;
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return DensityFunctions.BlendOffset.CODEC;
      }
   }

   private class BlendDensity implements NoiseChunkDensityFunction, DensityFunctions.MarkerOrMarked {
      private final DensityFunction input;

      private BlendDensity(final DensityFunction input) {
         Objects.requireNonNull(NoiseChunk.this);
         super();
         this.input = input;
      }

      public double compute(final DensityFunction.FunctionContext context) {
         return NoiseChunk.this.blender.blendDensity(context, this.input.compute(context));
      }

      public void fillArray(final double[] output, final DensityFunction.ContextProvider contextProvider) {
         this.input.fillArray(output, contextProvider);

         for(int i = 0; i < output.length; ++i) {
            DensityFunction.FunctionContext context = contextProvider.forIndex(i);
            output[i] = NoiseChunk.this.blender.blendDensity(context, output[i]);
         }

      }

      public double minValue() {
         return -1.0 / 0.0;
      }

      public double maxValue() {
         return 1.0 / 0.0;
      }

      public DensityFunction wrapped() {
         return this.input;
      }

      public DensityFunctions.Marker.Type type() {
         return DensityFunctions.Marker.Type.BlendDensity;
      }
   }

   @FunctionalInterface
   public interface BlockStateFiller {
      @Nullable BlockState calculate(final DensityFunction.FunctionContext context);
   }
}
