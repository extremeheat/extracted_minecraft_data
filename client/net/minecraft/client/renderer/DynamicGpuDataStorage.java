package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.device.GpuDevice;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class DynamicGpuDataStorage<T extends DynamicGpuDataStorage.DynamicGpuData> implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final List<MappableRingBuffer> oldBuffers = new ArrayList();
   private final int blockSize;
   private MappableRingBuffer ringBuffer;
   private int nextBlock;
   private int capacity;
   private @Nullable T lastData;
   private final String label;
   private final @GpuBuffer.Usage int usage;

   public DynamicGpuDataStorage(final String label, final int dataSize, final @GpuBuffer.Usage int usage, final int initialCapacity) {
      super();
      GpuDevice device = RenderSystem.getDevice();
      this.usage = usage;
      this.blockSize = usage == 128 ? Mth.roundToward(dataSize, device.getDeviceInfo().limits().minUniformOffsetAlignment()) : dataSize;
      this.capacity = Mth.smallestEncompassingPowerOfTwo(initialCapacity);
      this.nextBlock = 0;
      this.ringBuffer = new MappableRingBuffer(() -> label + " x" + this.blockSize, this.usage | 2, this.blockSize * this.capacity);
      this.label = label;
   }

   public void endFrame() {
      this.nextBlock = 0;
      this.lastData = null;
      this.ringBuffer.rotate();
      if (!this.oldBuffers.isEmpty()) {
         for(MappableRingBuffer oldBuffer : this.oldBuffers) {
            oldBuffer.close();
         }

         this.oldBuffers.clear();
      }

   }

   private void resizeBuffers(final int newCapacity) {
      this.capacity = newCapacity;
      this.nextBlock = 0;
      this.lastData = null;
      this.oldBuffers.add(this.ringBuffer);
      this.ringBuffer = new MappableRingBuffer(() -> this.label + " x" + this.blockSize, this.usage | 2, this.blockSize * this.capacity);
   }

   public GpuBufferSlice writeData(final T gpuData) {
      if (this.lastData != null && this.lastData.equals(gpuData)) {
         return this.ringBuffer.currentBuffer().slice((long)((this.nextBlock - 1) * this.blockSize), (long)this.blockSize);
      } else {
         if (this.nextBlock >= this.capacity) {
            int newCapacity = this.capacity * 2;
            LOGGER.info("Resizing {}, capacity limit of {} reached during a single frame. New capacity will be {}.", new Object[]{this.label, this.capacity, newCapacity});
            this.resizeBuffers(newCapacity);
         }

         int offset = this.nextBlock * this.blockSize;

         try (GpuBufferSlice.MappedView view = this.ringBuffer.currentBuffer().slice((long)offset, (long)this.blockSize).map(false, true)) {
            gpuData.write(view.data());
         }

         ++this.nextBlock;
         this.lastData = gpuData;
         return this.ringBuffer.currentBuffer().slice((long)offset, (long)this.blockSize);
      }
   }

   public GpuBufferSlice[] writeData(final T[] dataArray) {
      if (dataArray.length == 0) {
         return new GpuBufferSlice[0];
      } else {
         if (this.nextBlock + dataArray.length > this.capacity) {
            int newCapacity = Mth.smallestEncompassingPowerOfTwo(Math.max(this.capacity + 1, dataArray.length));
            LOGGER.info("Resizing {}, capacity limit of {} reached during a single frame. New capacity will be {}.", new Object[]{this.label, this.capacity, newCapacity});
            this.resizeBuffers(newCapacity);
         }

         int firstOffset = this.nextBlock * this.blockSize;
         GpuBufferSlice[] result = new GpuBufferSlice[dataArray.length];

         try (GpuBufferSlice.MappedView view = this.ringBuffer.currentBuffer().slice((long)firstOffset, (long)(dataArray.length * this.blockSize)).map(false, true)) {
            ByteBuffer byteBuffer = view.data();

            for(int i = 0; i < dataArray.length; ++i) {
               T data = dataArray[i];
               result[i] = this.ringBuffer.currentBuffer().slice((long)(firstOffset + i * this.blockSize), (long)this.blockSize);
               byteBuffer.position(i * this.blockSize);
               data.write(byteBuffer);
            }
         }

         this.nextBlock += dataArray.length;
         this.lastData = dataArray[dataArray.length - 1];
         return result;
      }
   }

   public GpuBufferSlice writeDataBatched(final List<T> dataList) {
      if (dataList.isEmpty()) {
         return new GpuBufferSlice((GpuBuffer)null, 0L, 0L);
      } else {
         if (this.nextBlock + dataList.size() > this.capacity) {
            int newCapacity = Mth.smallestEncompassingPowerOfTwo(Math.max(this.capacity + 1, dataList.size()));
            LOGGER.info("Resizing {}, capacity limit of {} reached during a single frame. New capacity will be {}.", new Object[]{this.label, this.capacity, newCapacity});
            this.resizeBuffers(newCapacity);
         }

         int firstOffset = this.nextBlock * this.blockSize;
         GpuBufferSlice result = this.ringBuffer.currentBuffer().slice((long)firstOffset, (long)(dataList.size() * this.blockSize));

         try (GpuBufferSlice.MappedView view = result.map(false, true)) {
            ByteBuffer byteBuffer = view.data();

            for(int i = 0; i < dataList.size(); ++i) {
               T data = (T)(dataList.get(i));
               byteBuffer.position(i * this.blockSize);
               data.write(byteBuffer);
            }
         }

         this.nextBlock += dataList.size();
         this.lastData = (T)(dataList.getLast());
         return result;
      }
   }

   public GpuBufferSlice[] writeDataBatchedMultiple(final List<List<T>> dataLists) {
      if (dataLists.isEmpty()) {
         return new GpuBufferSlice[0];
      } else {
         int totalCount = 0;

         for(List<T> data : dataLists) {
            totalCount += data.size();
         }

         if (this.nextBlock + totalCount > this.capacity) {
            int newCapacity = Mth.smallestEncompassingPowerOfTwo(Math.max(this.capacity + 1, totalCount));
            LOGGER.info("Resizing {}, capacity limit of {} reached during a single frame. New capacity will be {}.", new Object[]{this.label, this.capacity, newCapacity});
            this.resizeBuffers(newCapacity);
         }

         int offset = this.nextBlock * this.blockSize;
         GpuBufferSlice[] result = new GpuBufferSlice[dataLists.size()];
         int bufferPositionIndex = 0;

         try (GpuBufferSlice.MappedView view = this.ringBuffer.currentBuffer().slice((long)offset, (long)(totalCount * this.blockSize)).map(false, true)) {
            ByteBuffer byteBuffer = view.data();

            for(int i = 0; i < dataLists.size(); ++i) {
               List<T> dataList = (List)dataLists.get(i);
               result[i] = this.ringBuffer.currentBuffer().slice((long)offset, (long)(dataList.size() * this.blockSize));

               for(T data : dataList) {
                  byteBuffer.position(bufferPositionIndex++ * this.blockSize);
                  data.write(byteBuffer);
               }

               offset += dataList.size() * this.blockSize;
            }
         }

         this.nextBlock += totalCount;
         this.lastData = (T)(((List)dataLists.getLast()).getLast());
         return result;
      }
   }

   public @GpuBuffer.Usage int usage() {
      return this.usage;
   }

   public void close() {
      for(MappableRingBuffer oldBuffer : this.oldBuffers) {
         oldBuffer.close();
      }

      this.ringBuffer.close();
   }

   public interface DynamicGpuData {
      void write(ByteBuffer byteBuffer);
   }
}
