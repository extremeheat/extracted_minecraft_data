package net.minecraft.world.level.levelgen.densityfunction;

import java.util.Arrays;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextMap;
import org.jspecify.annotations.Nullable;

public class SamplerContext {
   public static final SamplerContext EMPTY_UNCACHED;
   private static final int CACHE_SIZE_STEP = 16;
   private final ContextMap userFields;
   private final DensityBufferArena bufferArena;
   private CacheCell @Nullable [] cacheCells;

   private SamplerContext(final ContextMap userFields, final DensityBufferArena bufferArena, final boolean enableCaches) {
      super();
      this.userFields = userFields;
      this.bufferArena = bufferArena;
      this.cacheCells = enableCaches ? new CacheCell[0] : null;
   }

   public static Builder builder() {
      return new Builder();
   }

   public <T> @Nullable T getField(final ContextKey<T> key) {
      return (T)this.userFields.get(key);
   }

   public <T> T getFieldOrDefault(final ContextKey<? extends T> key, final T defaultValue) {
      return (T)Objects.requireNonNullElse(this.getField(key), defaultValue);
   }

   public ScopedDensityBuffer acquireBuffer(final DensityVolume volume) {
      return this.bufferArena.acquire(volume.size());
   }

   private @Nullable CacheCell getCacheCell(final int cacheId) {
      if (this.cacheCells == null) {
         return null;
      } else {
         int oldSize = this.cacheCells.length;
         if (cacheId >= oldSize) {
            int newSize = Mth.roundToward(cacheId + 1, 16);
            this.cacheCells = (CacheCell[])Arrays.copyOf(this.cacheCells, newSize);

            for(int i = oldSize; i < newSize; ++i) {
               this.cacheCells[i] = new CacheCell();
            }
         }

         return this.cacheCells[cacheId];
      }
   }

   void sampleVolumeCached(final int cacheId, final DensitySampler input, final DensityBuffer outputBuffer, final DensityVolume volume) {
      CacheCell cell = this.getCacheCell(cacheId);
      if (cell == null) {
         input.sampleVolume(this, outputBuffer, volume);
      } else {
         if (cell.buffer == null || !volume.equals(cell.volume)) {
            if (cell.buffer != null) {
               cell.buffer.close();
            }

            cell.volume = volume;
            cell.buffer = this.acquireBuffer(volume);
            input.sampleVolume(this, cell.buffer, volume);
         }

         outputBuffer.copyFrom(cell.buffer);
      }
   }

   float sampleValueCached(final int cacheId, final DensitySampler input, final int blockX, final int blockY, final int blockZ) {
      CacheCell cell = this.getCacheCell(cacheId);
      if (cell == null) {
         return input.sampleValue(this, blockX, blockY, blockZ);
      } else {
         long cacheKey = BlockPos.asLong(blockX, blockY, blockZ);
         if (cell.valueKey == cacheKey && !Float.isNaN(cell.value)) {
            return cell.value;
         } else {
            if (cell.buffer != null && cell.volume != null) {
               int index = cell.volume.indexOfBlock(blockX, blockY, blockZ);
               if (index != -1) {
                  return cell.buffer.get(index);
               }
            }

            float value = input.sampleValue(this, blockX, blockY, blockZ);
            cell.valueKey = cacheKey;
            cell.value = value;
            return value;
         }
      }
   }

   static {
      EMPTY_UNCACHED = new SamplerContext(ContextMap.EMPTY, DensityBufferArena.GLOBAL, false);
   }

   private static class CacheCell {
      private @Nullable DensityVolume volume;
      private @Nullable ScopedDensityBuffer buffer;
      private long valueKey;
      private float value = 0.0F / 0.0F;

      private CacheCell() {
         super();
      }
   }

   public static class Builder {
      private ContextMap userFields;
      private DensityBufferArena bufferArena;
      private boolean enableCaches;

      private Builder() {
         super();
         this.userFields = ContextMap.EMPTY;
         this.bufferArena = DensityBufferArena.GLOBAL;
      }

      public Builder setUserFields(final ContextMap userFields) {
         this.userFields = userFields;
         return this;
      }

      public Builder useBufferArena(final DensityBufferArena bufferArena) {
         this.bufferArena = bufferArena;
         return this;
      }

      public Builder enableCaches() {
         this.enableCaches = true;
         return this;
      }

      public SamplerContext build() {
         return new SamplerContext(this.userFields, this.bufferArena, this.enableCaches);
      }
   }
}
