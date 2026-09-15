package com.mojang.renderpearl.backend.vulkan.checkpoints;

import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.backend.vulkan.VulkanDevice;
import com.mojang.renderpearl.backend.vulkan.VulkanGpuBuffer;
import com.mojang.renderpearl.backend.vulkan.VulkanQueue;
import com.mojang.renderpearl.util.UncheckedAutoCloseable;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.vulkan.AMDBufferMarker;
import org.lwjgl.vulkan.VkCommandBuffer;

public class AmdCheckpointExtension implements CheckpointExtension {
   private static final long[] STAGES = new long[]{1L, 8192L};
   private final List<AmdCheckpointStorage> storages = new ArrayList();

   public AmdCheckpointExtension() {
      super();
   }

   public CheckpointExtension.CheckpointStorage createStorage(final VulkanDevice device, final VulkanQueue queue, final int maxFramesInFlight) {
      AmdCheckpointStorage storage = new AmdCheckpointStorage(device, queue, maxFramesInFlight);
      this.storages.add(storage);
      return storage;
   }

   public List<CheckpointExtension.QueueCheckpoints> retrieveCheckpoints(final boolean isDeviceLost) {
      List<CheckpointExtension.QueueCheckpoints> result = new ArrayList(this.storages.size());

      for(AmdCheckpointStorage storage : this.storages) {
         result.add(storage.retrieveCheckpoints());
      }

      return result;
   }

   public void close() {
      for(AmdCheckpointStorage storage : this.storages) {
         storage.close();
      }

   }

   private static class AmdCheckpointStorage extends AbstractCheckpointStorage implements UncheckedAutoCloseable {
      private final VulkanGpuBuffer buffer;
      private final GpuBufferSlice.MappedView mappedView;

      protected AmdCheckpointStorage(final VulkanDevice device, final VulkanQueue queue, final int maxFramesInFlight) {
         super(queue, maxFramesInFlight);
         this.buffer = device.createBuffer(() -> "Internal marker storage", 9, (long)(AmdCheckpointExtension.STAGES.length * 4));
         this.mappedView = this.buffer.map(true, false);
      }

      protected void recordCheckpoint(final VkCommandBuffer commandBuffer, final int id) {
         for(int i = 0; i < AmdCheckpointExtension.STAGES.length; ++i) {
            AMDBufferMarker.vkCmdWriteBufferMarker2AMD(commandBuffer, AmdCheckpointExtension.STAGES[i], this.buffer.vkBuffer(), (long)(i * 4), id);
         }

      }

      public CheckpointExtension.QueueCheckpoints retrieveCheckpoints() {
         List<CheckpointExtension.StageCheckpoint> stageCheckpoints = new ArrayList();

         for(int i = 0; i < AmdCheckpointExtension.STAGES.length; ++i) {
            AbstractCheckpointStorage.Checkpoint checkpoint = this.findCheckpoint(this.mappedView.data().getInt(i * 4));
            if (checkpoint != null) {
               stageCheckpoints.add(new CheckpointExtension.StageCheckpoint(AmdCheckpointExtension.STAGES[i], checkpoint.type(), checkpoint.label()));
            }
         }

         return new CheckpointExtension.QueueCheckpoints(this.queue.address(), stageCheckpoints);
      }

      public void close() {
         this.mappedView.close();
         this.buffer.close();
      }
   }
}
