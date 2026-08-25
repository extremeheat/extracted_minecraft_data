package net.minecraft.client.renderer;

import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import java.nio.ByteBuffer;
import java.util.List;

public interface DynamicGpuDataStorage<T extends DynamicGpuDataStorage.DynamicGpuData> extends AutoCloseable {
   void endFrame();

   GpuBufferSlice writeData(final T gpuData);

   GpuBufferSlice[] writeData(final T[] dataArray);

   GpuBufferSlice writeDataBatched(final List<T> dataList);

   GpuBufferSlice[] writeDataBatchedMultiple(final List<List<T>> dataLists);

   @GpuBuffer.Usage int usage();

   void close();

   public interface DynamicGpuData {
      void write(ByteBuffer byteBuffer);
   }
}
