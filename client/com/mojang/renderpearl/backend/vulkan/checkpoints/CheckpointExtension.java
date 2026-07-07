package com.mojang.renderpearl.backend.vulkan.checkpoints;

import com.mojang.renderpearl.backend.vulkan.VulkanDevice;
import com.mojang.renderpearl.backend.vulkan.VulkanQueue;
import java.util.List;
import java.util.function.Supplier;
import org.lwjgl.vulkan.VkCommandBuffer;

public interface CheckpointExtension extends AutoCloseable {
   CheckpointStorage createStorage(VulkanDevice device, VulkanQueue queue, int maxFramesInFlight);

   List<QueueCheckpoints> retrieveCheckpoints(boolean isDeviceLost);

   void close();

   public static enum CheckpointType {
      BEGIN_RENDER_PASS,
      END_RENDER_PASS;

      private CheckpointType() {
      }

      // $FF: synthetic method
      private static CheckpointType[] $values() {
         return new CheckpointType[]{BEGIN_RENDER_PASS, END_RENDER_PASS};
      }
   }

   public static record QueueCheckpoints(long queue, List<StageCheckpoint> checkpoints) {
      public QueueCheckpoints {
         super();
      }
   }

   public static record StageCheckpoint(long stage, CheckpointType type, String label) {
      public StageCheckpoint {
         super();
      }
   }

   public interface CheckpointStorage {
      void rotate();

      void recordCheckpoint(VkCommandBuffer commandBuffer, CheckpointType type, Supplier<String> label);
   }
}
