package com.mojang.renderpearl.backend.vulkan;

import com.mojang.renderpearl.util.UncheckedAutoCloseable;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.util.List;
import java.util.Objects;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.KHRSynchronization2;
import org.lwjgl.vulkan.VK12;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferSubmitInfo;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkSemaphoreSubmitInfo;
import org.lwjgl.vulkan.VkSubmitInfo2;

public record VulkanQueue(VkQueue vkQueue, int queueFamilyIndex) {
   public VulkanQueue(final VulkanDevice device, final int queueFamilyIndex, final int queueIndex) {
      this(new VkQueue(fetchVkQueue(device, queueFamilyIndex, queueIndex), device.vkDevice()), queueFamilyIndex);
   }

   public VulkanQueue {
      super();
   }

   private static long fetchVkQueue(final VulkanDevice device, final int queueFamilyIndex, final int queueIndex) {
      MemoryStack stack = MemoryStack.stackPush();

      long var5;
      try {
         PointerBuffer queueHandlePtr = stack.callocPointer(1);
         VK12.vkGetDeviceQueue(device.vkDevice(), queueFamilyIndex, queueIndex, queueHandlePtr);
         var5 = queueHandlePtr.get(0);
      } catch (Throwable var8) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (stack != null) {
         stack.close();
      }

      return var5;
   }

   public Submission beginSubmit() {
      return new Submission();
   }

   public void waitIdle() {
      VK12.vkQueueWaitIdle(this.vkQueue);
   }

   public class Submission implements UncheckedAutoCloseable {
      private boolean closed;
      private final ReferenceArrayList<SubmitStage> stages;

      private Submission() {
         Objects.requireNonNull(VulkanQueue.this);
         super();
         this.closed = false;
         this.stages = new ReferenceArrayList();
         this.stages.add(new SubmitStage());
      }

      public void waitSemaphore(final long semaphore, final long value, final long stageMask) {
         if (this.closed) {
            throw new IllegalStateException("Attempt to use closed Submission");
         } else {
            if (!((SubmitStage)this.stages.top()).commandBuffers.isEmpty() || !((SubmitStage)this.stages.top()).signals.isEmpty()) {
               this.stages.add(new SubmitStage());
            }

            ((SubmitStage)this.stages.top()).waits.add(new SemaphoreOp(semaphore, value, stageMask));
         }
      }

      public void executeCommands(final VkCommandBuffer commandBuffer) {
         if (this.closed) {
            throw new IllegalStateException("Attempt to use closed Submission");
         } else {
            if (!((SubmitStage)this.stages.top()).signals.isEmpty()) {
               this.stages.add(new SubmitStage());
            }

            ((SubmitStage)this.stages.top()).commandBuffers.add(commandBuffer);
         }
      }

      public void signalSemaphore(final long semaphore, final long value, final long stageMask) {
         if (this.closed) {
            throw new IllegalStateException("Attempt to use closed Submission");
         } else {
            ((SubmitStage)this.stages.top()).signals.add(new SemaphoreOp(semaphore, value, stageMask));
         }
      }

      public void close() {
         MemoryStack stack = MemoryStack.stackPush();

         try {
            VkSubmitInfo2.Buffer submits = VkSubmitInfo2.calloc(this.stages.size(), stack);

            for(int i = 0; i < this.stages.size(); ++i) {
               SubmitStage stage = (SubmitStage)this.stages.get(i);
               ((VkSubmitInfo2.Buffer)submits.position(i)).sType$Default();
               if (!stage.waits.isEmpty()) {
                  VkSemaphoreSubmitInfo.Buffer waits = VkSemaphoreSubmitInfo.calloc(stage.waits.size(), stack);
                  submits.pWaitSemaphoreInfos(waits);

                  for(int j = 0; j < stage.waits.size(); ++j) {
                     SemaphoreOp wait = (SemaphoreOp)stage.waits.get(j);
                     ((VkSemaphoreSubmitInfo.Buffer)waits.position(j)).sType$Default();
                     waits.semaphore(wait.vkSemaphore);
                     waits.value(wait.value);
                     waits.stageMask(wait.stageMask);
                  }
               }

               if (!stage.commandBuffers.isEmpty()) {
                  VkCommandBufferSubmitInfo.Buffer buffers = VkCommandBufferSubmitInfo.calloc(stage.commandBuffers.size(), stack);
                  submits.pCommandBufferInfos(buffers);

                  for(int j = 0; j < stage.commandBuffers.size(); ++j) {
                     VkCommandBuffer vkCommandBuffer = (VkCommandBuffer)stage.commandBuffers.get(j);
                     ((VkCommandBufferSubmitInfo.Buffer)buffers.position(j)).sType$Default();
                     buffers.commandBuffer(vkCommandBuffer);
                  }
               }

               if (!stage.signals.isEmpty()) {
                  VkSemaphoreSubmitInfo.Buffer signals = VkSemaphoreSubmitInfo.calloc(stage.signals.size(), stack);
                  submits.pSignalSemaphoreInfos(signals);

                  for(int j = 0; j < stage.signals.size(); ++j) {
                     SemaphoreOp signal = (SemaphoreOp)stage.signals.get(j);
                     ((VkSemaphoreSubmitInfo.Buffer)signals.position(j)).sType$Default();
                     signals.semaphore(signal.vkSemaphore);
                     signals.value(signal.value);
                     signals.stageMask(signal.stageMask);
                  }
               }
            }

            submits.position(0);
            KHRSynchronization2.vkQueueSubmit2KHR(VulkanQueue.this.vkQueue, submits, 0L);
            this.closed = true;
         } catch (Throwable var9) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (stack != null) {
            stack.close();
         }

      }

      private static record SemaphoreOp(long vkSemaphore, long value, long stageMask) {
         private SemaphoreOp {
            super();
         }
      }

      private static record SubmitStage(List<SemaphoreOp> waits, List<VkCommandBuffer> commandBuffers, List<SemaphoreOp> signals) {
         public SubmitStage() {
            this(new ReferenceArrayList(), new ReferenceArrayList(), new ReferenceArrayList());
         }

         private SubmitStage {
            super();
         }
      }
   }
}
