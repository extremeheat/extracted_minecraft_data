package net.minecraft.world.level.levelgen;

import java.util.Arrays;
import net.minecraft.SharedConstants;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.Zone;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.densityfunction.DensityBufferPool;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensitySamplerSet;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;
import net.minecraft.world.level.levelgen.densityfunction.ScopedDensityBuffer;
import org.jspecify.annotations.Nullable;

public class NoiseChunk implements AutoCloseable {
   private final RandomState randomState;
   private final DensityVolume volume;
   private final Aquifer aquifer;
   private final DensityBufferPool bufferPool;
   private final SamplerContext samplerContext;
   private final DensitySamplerSet cachingSamplers;
   private final ScopedDensityBuffer densityBuffer;
   private final int[] columnMaxYs;
   private final NoiseColumn mutableColumn;

   public NoiseChunk(final RandomState randomState, final DensityVolume volume, final ContextMap samplerFields, final DensityFunction finalDensity, final Aquifer.@Nullable Config aquifer, final Aquifer.FluidPicker globalFluidPicker) {
      super();
      this.randomState = randomState;
      this.volume = volume;
      this.bufferPool = randomState.acquireDensityBufferPool();
      this.samplerContext = SamplerContext.builder().setUserFields(samplerFields).useBufferArena(this.bufferPool).enableCaches().build();
      this.cachingSamplers = randomState.samplersWithContext(this.samplerContext);
      ProfilerFiller profiler = Profiler.get();
      if (aquifer != null && !SharedConstants.DEBUG_DISABLE_AQUIFERS) {
         try (Zone var8 = profiler.zone("initializeAquifer")) {
            this.aquifer = aquifer.create(this.cachingSamplers, randomState.getOrCreateRandomFactory(Identifier.withDefaultNamespace("aquifer")), volume, globalFluidPicker);
         }
      } else {
         this.aquifer = Aquifer.createDisabled(globalFluidPicker);
      }

      try (Zone var17 = profiler.zone("sampleDensityBuffer")) {
         this.densityBuffer = this.cachingSamplers.get(finalDensity).sampleVolume(volume);
      }

      try (Zone var18 = profiler.zone("findColumnMaxYs")) {
         this.columnMaxYs = new int[volume.sizeX() * volume.sizeZ()];

         for(int z = 0; z < volume.sizeZ(); ++z) {
            for(int x = 0; x < volume.sizeX(); ++x) {
               this.columnMaxYs[x + z * volume.sizeX()] = findColumnMaxY(volume, x, z, this.densityBuffer, this.aquifer);
            }
         }
      }

      this.mutableColumn = new NoiseColumn(volume.minBlockY(), volume.sizeY());
   }

   private static int findColumnMaxY(final DensityVolume volume, final int x, final int z, final ScopedDensityBuffer densityBuffer, final Aquifer aquifer) {
      int blockX = volume.blockX(x);
      int blockZ = volume.blockZ(z);
      int index = volume.indexUnchecked(x, volume.sizeY() - 1, z);

      for(int y = volume.sizeY() - 1; y >= 0; --y) {
         int blockY = volume.blockY(y);
         float density = densityBuffer.get(index--);
         BlockState noiseBlock = aquifer.computeSubstance(blockX, blockY, blockZ, (double)density);
         if (noiseBlock != NoiseColumn.AIR) {
            return y;
         }
      }

      return -1;
   }

   public DensityVolume volume() {
      return this.volume;
   }

   public Aquifer aquifer() {
      return this.aquifer;
   }

   public DensitySamplerSet cachingSamplers() {
      return this.cachingSamplers;
   }

   private int getColumnMaxY(final int x, final int z) {
      return this.columnMaxYs[x + z * this.volume.sizeX()];
   }

   public NoiseColumn prepareColumn(final int x, final int z) {
      int maxY = this.getColumnMaxY(x, z);
      NoiseColumn column = this.mutableColumn;
      Arrays.fill(column.blocks, NoiseColumn.AIR);
      column.fluidUpdates.clear();
      column.topIndexY = maxY;
      column.surfaceGradientX = this.getColumnMaxY(Math.min(x + 1, this.volume.sizeX() - 1), z) - this.getColumnMaxY(Math.max(x - 1, 0), z);
      column.surfaceGradientZ = this.getColumnMaxY(x, Math.min(z + 1, this.volume.sizeZ() - 1)) - this.getColumnMaxY(x, Math.max(z - 1, 0));
      int blockX = this.volume.blockX(x);
      int blockZ = this.volume.blockZ(z);
      int densityIndex = this.volume.indexUnchecked(x, 0, z);

      for(int y = 0; y <= maxY; ++y) {
         int blockY = this.volume.blockY(y);
         float density = this.densityBuffer.get(densityIndex++);
         BlockState noiseBlock = this.aquifer.computeSubstance(blockX, blockY, blockZ, (double)density);
         if (noiseBlock != NoiseColumn.AIR) {
            column.blocks[y] = noiseBlock;
            if (noiseBlock != NoiseColumn.SOLID && this.aquifer.shouldScheduleFluidUpdate()) {
               column.fluidUpdates.set(y);
            }
         }
      }

      return column;
   }

   public DensityVolume volumeWithBlocks() {
      return new DensityVolume(this.volume.sizeX(), Math.max(this.maxBlockY() - this.volume.minBlockY() + 1, 1), this.volume.sizeZ(), this.volume.minBlockX(), this.volume.minBlockY(), this.volume.minBlockZ());
   }

   private int maxBlockY() {
      int maxY = -2147483648;

      for(int columnMaxY : this.columnMaxYs) {
         if (columnMaxY > maxY) {
            maxY = columnMaxY;
         }
      }

      return this.volume.blockY(maxY);
   }

   public void close() {
      this.densityBuffer.close();
      this.samplerContext.clearCaches();
      this.randomState.releaseDensityBufferPool(this.bufferPool);
   }
}
