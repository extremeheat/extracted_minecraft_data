package com.mojang.blaze3d.vulkan;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.GpuFence;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.CommandEncoderBackend;
import com.mojang.blaze3d.systems.GpuQueryPool;
import com.mojang.blaze3d.systems.RenderPassBackend;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MathUtil;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.KHRDynamicRendering;
import org.lwjgl.vulkan.KHRSynchronization2;
import org.lwjgl.vulkan.VK12;
import org.lwjgl.vulkan.VkAllocationCallbacks;
import org.lwjgl.vulkan.VkBufferCopy;
import org.lwjgl.vulkan.VkBufferImageCopy;
import org.lwjgl.vulkan.VkClearAttachment;
import org.lwjgl.vulkan.VkClearColorValue;
import org.lwjgl.vulkan.VkClearDepthStencilValue;
import org.lwjgl.vulkan.VkClearRect;
import org.lwjgl.vulkan.VkClearValue;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferAllocateInfo;
import org.lwjgl.vulkan.VkCommandBufferBeginInfo;
import org.lwjgl.vulkan.VkCommandBufferInheritanceInfo;
import org.lwjgl.vulkan.VkCommandBufferInheritanceRenderingInfo;
import org.lwjgl.vulkan.VkCommandPoolCreateInfo;
import org.lwjgl.vulkan.VkDependencyInfo;
import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkImageCopy;
import org.lwjgl.vulkan.VkImageSubresourceLayers;
import org.lwjgl.vulkan.VkImageSubresourceRange;
import org.lwjgl.vulkan.VkMemoryBarrier2;
import org.lwjgl.vulkan.VkOffset2D;
import org.lwjgl.vulkan.VkRect2D;
import org.lwjgl.vulkan.VkRenderingAttachmentInfo;
import org.lwjgl.vulkan.VkRenderingInfo;
import org.lwjgl.vulkan.VkSemaphoreCreateInfo;
import org.lwjgl.vulkan.VkSemaphoreTypeCreateInfo;
import org.lwjgl.vulkan.VkSemaphoreWaitInfo;

public class VulkanCommandEncoder implements CommandEncoderBackend, Destroyable {
   public static final int MAX_SUBMITS_IN_FLIGHT = 2;
   private final VulkanDevice device;
   private final long submitSemaphore;
   private long currentSubmitIndex = 2L;
   private long completedSubmitIndex = 0L;
   private VulkanQueue.Submission submissionBuilder;
   private final DestructionQueue<Destroyable> destroyQueue = new DestructionQueue<Destroyable>(2, Destroyable::destroy);
   private final DestructionQueue<VkCommandBuffer> commandBufferDestroyQueue;
   private final long commandPool;
   private @Nullable VkCommandBuffer currentCommandBuffer;
   private @Nullable VulkanRenderPass currentRenderPass;

   public VulkanCommandEncoder(final VulkanDevice device) {
      super();
      this.device = device;
      MemoryStack baseStack = MemoryStack.stackGet();
      MemoryStack stack = baseStack.push();

      try {
         VkSemaphoreTypeCreateInfo semaphoreTypeCreateInfo = VkSemaphoreTypeCreateInfo.calloc(stack).sType$Default();
         semaphoreTypeCreateInfo.semaphoreType(1);
         semaphoreTypeCreateInfo.initialValue(this.currentSubmitIndex - 1L);
         VkSemaphoreCreateInfo semaphoreCreateInfo = VkSemaphoreCreateInfo.calloc(stack).sType$Default();
         semaphoreCreateInfo.pNext(semaphoreTypeCreateInfo);
         LongBuffer semaphoreHandlePtr = stack.callocLong(1);
         VulkanUtils.crashIfFailure(VK12.vkCreateSemaphore(device.vkDevice(), semaphoreCreateInfo, (VkAllocationCallbacks)null, semaphoreHandlePtr), "Failed to create submit VkSemaphore");
         this.submitSemaphore = semaphoreHandlePtr.get(0);
      } catch (Throwable var10) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var8) {
               var10.addSuppressed(var8);
            }
         }

         throw var10;
      }

      if (stack != null) {
         stack.close();
      }

      stack = baseStack.push();

      try {
         VkCommandPoolCreateInfo commandPoolCreateInfo = VkCommandPoolCreateInfo.calloc(stack).sType$Default();
         commandPoolCreateInfo.flags(1);
         commandPoolCreateInfo.queueFamilyIndex(device.graphicsQueue().queueFamilyIndex());
         LongBuffer commandPoolHandlePtr = stack.callocLong(1);
         VulkanUtils.crashIfFailure(VK12.vkCreateCommandPool(device.vkDevice(), commandPoolCreateInfo, (VkAllocationCallbacks)null, commandPoolHandlePtr), "Failed to create VkCommandPool");
         this.commandPool = commandPoolHandlePtr.get(0);
      } catch (Throwable var9) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var7) {
               var9.addSuppressed(var7);
            }
         }

         throw var9;
      }

      if (stack != null) {
         stack.close();
      }

      this.submissionBuilder = device.graphicsQueue().beginSubmit();
      this.commandBufferDestroyQueue = new DestructionQueue<VkCommandBuffer>(2, new DestructionQueue.Destroyer<VkCommandBuffer>() {
         PointerBuffer commandBuffers;

         {
            Objects.requireNonNull(VulkanCommandEncoder.this);
            this.commandBuffers = PointerBuffer.allocateDirect(64);
         }

         public void begin(final int count) {
            if (this.commandBuffers.capacity() < count) {
               this.commandBuffers = PointerBuffer.allocateDirect(MathUtil.mathRoundPoT(count));
            }

            this.commandBuffers.clear();
         }

         public void destroy(final VkCommandBuffer commandBuffer) {
            this.commandBuffers.put(commandBuffer);
         }

         public void end() {
            this.commandBuffers.flip();
            if (this.commandBuffers.limit() != 0) {
               VK12.vkFreeCommandBuffers(device.vkDevice(), VulkanCommandEncoder.this.commandPool, this.commandBuffers);
            }

         }
      });
   }

   public void destroy() {
      this.submissionBuilder.close();
      this.device.graphicsQueue().waitIdle();
      this.commandBufferDestroyQueue.close();
      this.destroyQueue.close();
      VK12.vkDestroyCommandPool(this.device.vkDevice(), this.commandPool, (VkAllocationCallbacks)null);
      VK12.vkDestroySemaphore(this.device.vkDevice(), this.submitSemaphore, (VkAllocationCallbacks)null);
   }

   public void queueForDestroy(final Destroyable destroyable) {
      this.destroyQueue.add(destroyable);
   }

   public void queueForDestroy(final VkCommandBuffer commandBuffer) {
      this.commandBufferDestroyQueue.add(commandBuffer);
   }

   private VulkanGpuBuffer createStagingBuffer(final ByteBuffer data) {
      int size = data.remaining();
      VulkanGpuBuffer stagingBuffer = new VulkanGpuBuffer(this.device, () -> "Staging buffer", 22, (long)size, false);

      try (GpuBuffer.MappedView mappedMemory = stagingBuffer.map(0L, (long)data.remaining())) {
         MemoryUtil.memCopy(data, mappedMemory.data());
      }

      return stagingBuffer;
   }

   public VkCommandBuffer allocateTransientCommandBuffer(final boolean primary) {
      MemoryStack stack = MemoryStack.stackPush();

      VkCommandBuffer var6;
      try {
         VkCommandBufferAllocateInfo allocateInfo = VkCommandBufferAllocateInfo.calloc(stack).sType$Default();
         allocateInfo.commandPool(this.commandPool);
         allocateInfo.level(primary ? 0 : 1);
         allocateInfo.commandBufferCount(1);
         PointerBuffer bufferPtr = stack.callocPointer(1);
         VulkanUtils.crashIfFailure(VK12.vkAllocateCommandBuffers(this.device.vkDevice(), allocateInfo, bufferPtr), "Failed to allocated VkCommandBuffer");
         VkCommandBuffer commandBuffer = new VkCommandBuffer(bufferPtr.get(0), this.device.vkDevice());
         this.queueForDestroy(commandBuffer);
         var6 = commandBuffer;
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

      return var6;
   }

   private VkCommandBuffer commandBuffer() {
      if (this.currentCommandBuffer != null) {
         return this.currentCommandBuffer;
      } else {
         MemoryStack stack = MemoryStack.stackPush();

         VkCommandBuffer var3;
         try {
            this.currentCommandBuffer = this.allocateTransientCommandBuffer(true);
            VkCommandBufferBeginInfo beginInfo = VkCommandBufferBeginInfo.calloc(stack).sType$Default();
            beginInfo.flags(1);
            VulkanUtils.crashIfFailure(VK12.vkBeginCommandBuffer(this.currentCommandBuffer, beginInfo), "Failed to begin VkCommandBuffer");
            this.submissionBuilder.executeCommands(this.currentCommandBuffer);
            var3 = this.currentCommandBuffer;
         } catch (Throwable var5) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (stack != null) {
            stack.close();
         }

         return var3;
      }
   }

   private VkCommandBuffer renderpassCommandBuffer(final IntBuffer colorAttachmentFormats, final int depthFormat) {
      MemoryStack stack = MemoryStack.stackPush();

      VkCommandBuffer var8;
      try {
         VkCommandBuffer renderpassCommandBuffer = this.allocateTransientCommandBuffer(false);
         VkCommandBufferInheritanceRenderingInfo dynamicRenderinginheritanceInfo = VkCommandBufferInheritanceRenderingInfo.calloc(stack).sType$Default();
         dynamicRenderinginheritanceInfo.pColorAttachmentFormats(colorAttachmentFormats);
         dynamicRenderinginheritanceInfo.depthAttachmentFormat(depthFormat);
         dynamicRenderinginheritanceInfo.rasterizationSamples(1);
         VkCommandBufferInheritanceInfo inheritanceInfo = VkCommandBufferInheritanceInfo.calloc(stack).sType$Default();
         inheritanceInfo.pNext(dynamicRenderinginheritanceInfo);
         VkCommandBufferBeginInfo beginInfo = VkCommandBufferBeginInfo.calloc(stack).sType$Default();
         beginInfo.flags(3);
         beginInfo.pInheritanceInfo(inheritanceInfo);
         VulkanUtils.crashIfFailure(VK12.vkBeginCommandBuffer(renderpassCommandBuffer, beginInfo), "Failed to begin VkCommandBuffer");
         var8 = renderpassCommandBuffer;
      } catch (Throwable var10) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var9) {
               var10.addSuppressed(var9);
            }
         }

         throw var10;
      }

      if (stack != null) {
         stack.close();
      }

      return var8;
   }

   VkCommandBuffer textureInitCommandBuffer() {
      return this.commandBuffer();
   }

   private void endCommandBuffer() {
      if (this.currentCommandBuffer != null) {
         VulkanUtils.crashIfFailure(VK12.vkEndCommandBuffer(this.currentCommandBuffer), "Failed to end VkCommandBuffer");
         this.currentCommandBuffer = null;
      }
   }

   public void waitSemaphore(final long vkSemaphore, final long value, final long stageMask) {
      this.endCommandBuffer();
      this.submissionBuilder.waitSemaphore(vkSemaphore, value, stageMask);
   }

   public void execute(final VkCommandBuffer commandBuffer) {
      this.endCommandBuffer();
      this.submissionBuilder.executeCommands(commandBuffer);
   }

   public void signalSemaphore(final long vkSemaphore, final long value, final long stageMask) {
      this.endCommandBuffer();
      this.submissionBuilder.signalSemaphore(vkSemaphore, value, stageMask);
   }

   private void memoryBarrier(final MemoryStack stack) {
      VkMemoryBarrier2.Buffer memoryBarrier = VkMemoryBarrier2.calloc(1, stack).sType$Default();
      memoryBarrier.srcStageMask(65536L);
      memoryBarrier.srcAccessMask(98304L);
      memoryBarrier.dstStageMask(65536L);
      memoryBarrier.dstAccessMask(98304L);
      VkDependencyInfo depInfo = VkDependencyInfo.calloc(stack).sType$Default();
      depInfo.pMemoryBarriers(memoryBarrier);
      KHRSynchronization2.vkCmdPipelineBarrier2KHR(this.commandBuffer(), depInfo);
   }

   public void submit() {
      this.endCommandBuffer();
      this.signalSemaphore(this.submitSemaphore, this.currentSubmitIndex, 65536L);
      this.submissionBuilder.close();
      this.submissionBuilder = this.device.graphicsQueue().beginSubmit();
      ++this.currentSubmitIndex;
      if (!this.awaitSubmitCompletion(this.currentSubmitIndex - 2L, 5000L)) {
         throw new IllegalStateException("5s timeout reached when waiting for VK semaphore");
      } else {
         this.commandBufferDestroyQueue.rotate();
         this.destroyQueue.rotate();
      }
   }

   public RenderPassBackend createRenderPass(final Supplier<String> label, final GpuTextureView colorTexture, final OptionalInt clearColor) {
      return this.createRenderPass(label, colorTexture, clearColor, (GpuTextureView)null, OptionalDouble.empty());
   }

   public RenderPassBackend createRenderPass(final Supplier<String> label, final GpuTextureView colorTexture, final OptionalInt clearColor, final @Nullable GpuTextureView depthTexture, final OptionalDouble clearDepth) {
      VulkanGpuTextureView vkColor = (VulkanGpuTextureView)colorTexture;
      VulkanGpuTextureView vulkanDepthAttachment = (VulkanGpuTextureView)depthTexture;
      this.device.instance().debug().beginDebugGroup(this.commandBuffer(), label);
      MemoryStack stack = MemoryStack.stackPush();

      try {
         VkExtent2D extent = VkExtent2D.calloc(stack).width(colorTexture.getWidth(0)).height(colorTexture.getHeight(0));
         VkOffset2D offset = VkOffset2D.calloc(stack).set(0, 0);
         VkRect2D.Buffer renderArea = VkRect2D.calloc(1, stack).extent(extent).offset(offset);
         VkRenderingAttachmentInfo.Buffer colorAttachmentInfo = VkRenderingAttachmentInfo.calloc(1, stack);
         colorAttachmentInfo.sType$Default();
         colorAttachmentInfo.imageView(vkColor.vkImageView());
         colorAttachmentInfo.imageLayout(1);
         colorAttachmentInfo.storeOp(0);
         if (clearColor.isPresent()) {
            int color = clearColor.getAsInt();
            VkClearColorValue vkClearColor = VulkanUtils.putArgb(VkClearColorValue.calloc(stack), color);
            colorAttachmentInfo.loadOp(1);
            colorAttachmentInfo.clearValue(VkClearValue.calloc(stack).color(vkClearColor));
         } else {
            colorAttachmentInfo.loadOp(0);
         }

         VkRenderingInfo renderingInfo = VkRenderingInfo.calloc(stack).sType$Default();
         renderingInfo.renderArea((VkRect2D)renderArea.get(0));
         renderingInfo.flags(1);
         renderingInfo.layerCount(1);
         renderingInfo.viewMask(0);
         renderingInfo.pColorAttachments(colorAttachmentInfo);
         if (vulkanDepthAttachment != null) {
            VkRenderingAttachmentInfo depthAttachmentInfo = VkRenderingAttachmentInfo.calloc(stack).sType$Default();
            depthAttachmentInfo.imageView(vulkanDepthAttachment.vkImageView());
            depthAttachmentInfo.imageLayout(1);
            depthAttachmentInfo.storeOp(0);
            if (clearDepth.isPresent()) {
               double color = clearDepth.getAsDouble();
               VkClearDepthStencilValue vkClearColor = VkClearDepthStencilValue.calloc(stack).depth((float)color);
               depthAttachmentInfo.loadOp(1);
               depthAttachmentInfo.clearValue(VkClearValue.calloc(stack).depthStencil(vkClearColor));
            } else {
               depthAttachmentInfo.loadOp(0);
            }

            renderingInfo.pDepthAttachment(depthAttachmentInfo);
         }

         KHRDynamicRendering.vkCmdBeginRenderingKHR(this.commandBuffer(), renderingInfo);
         Supplier<VkCommandBuffer> secondaryCommandBufferSupplier = () -> {
            MemoryStack memStack = MemoryStack.stackPush();

            VkCommandBuffer var4;
            try {
               var4 = this.renderpassCommandBuffer(memStack.ints(VulkanConst.toVk(colorTexture.texture().getFormat())), depthTexture == null ? 0 : VulkanConst.toVk(depthTexture.texture().getFormat()));
            } catch (Throwable var7) {
               if (memStack != null) {
                  try {
                     memStack.close();
                  } catch (Throwable x2) {
                     var7.addSuppressed(x2);
                  }
               }

               throw var7;
            }

            if (memStack != null) {
               memStack.close();
            }

            return var4;
         };
         this.currentRenderPass = new VulkanRenderPass(this.device, this::queueForDestroy, this.commandBuffer(), secondaryCommandBufferSupplier, colorTexture.getWidth(0), colorTexture.getHeight(0), depthTexture != null);
      } catch (Throwable var19) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var18) {
               var19.addSuppressed(var18);
            }
         }

         throw var19;
      }

      if (stack != null) {
         stack.close();
      }

      return this.currentRenderPass;
   }

   public void submitRenderPass() {
      if (this.currentRenderPass == null) {
         throw new IllegalStateException("Cannot submit a renderpass if one hasn't been started!");
      } else {
         this.currentRenderPass.end();
         KHRDynamicRendering.vkCmdEndRenderingKHR(this.commandBuffer());
         this.device.instance().debug().endDebugGroup(this.commandBuffer());
         MemoryStack stack = MemoryStack.stackPush();

         try {
            this.memoryBarrier(stack);
         } catch (Throwable var5) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (stack != null) {
            stack.close();
         }

      }
   }

   private void clearColorTextureUnsynced(final MemoryStack stack, final GpuTexture colorTexture, final int clearColor) {
      VkClearColorValue vkClearColor = VulkanUtils.putArgb(VkClearColorValue.calloc(stack), clearColor);
      VkImageSubresourceRange subresourceRange = VkImageSubresourceRange.calloc(stack);
      subresourceRange.baseMipLevel(0);
      subresourceRange.levelCount(colorTexture.getMipLevels());
      subresourceRange.baseArrayLayer(0);
      subresourceRange.layerCount(1);
      subresourceRange.aspectMask(1);
      VK12.vkCmdClearColorImage(this.commandBuffer(), ((VulkanGpuTexture)colorTexture).vkImage(), 1, vkClearColor, subresourceRange);
   }

   public void clearDepthTextureUnsynced(final MemoryStack stack, final GpuTexture depthTexture, final double clearDepth) {
      VkClearDepthStencilValue vkClearDepth = VkClearDepthStencilValue.calloc(stack).depth((float)clearDepth);
      VkImageSubresourceRange subresourceRange = VkImageSubresourceRange.calloc(stack);
      subresourceRange.baseMipLevel(0);
      subresourceRange.levelCount(depthTexture.getMipLevels());
      subresourceRange.baseArrayLayer(0);
      subresourceRange.layerCount(1);
      subresourceRange.aspectMask(2);
      VK12.vkCmdClearDepthStencilImage(this.commandBuffer(), ((VulkanGpuTexture)depthTexture).vkImage(), 1, vkClearDepth, subresourceRange);
   }

   public void clearColorTexture(final GpuTexture colorTexture, final int clearColor) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         this.clearColorTextureUnsynced(stack, colorTexture, clearColor);
         this.memoryBarrier(stack);
      } catch (Throwable var7) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (stack != null) {
         stack.close();
      }

   }

   public void clearColorAndDepthTextures(final GpuTexture colorTexture, final int clearColor, final GpuTexture depthTexture, final double clearDepth) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         this.clearColorTextureUnsynced(stack, colorTexture, clearColor);
         this.clearDepthTextureUnsynced(stack, depthTexture, clearDepth);
         this.memoryBarrier(stack);
      } catch (Throwable var10) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var9) {
               var10.addSuppressed(var9);
            }
         }

         throw var10;
      }

      if (stack != null) {
         stack.close();
      }

   }

   public void clearColorAndDepthTextures(final GpuTexture colorTexture, final int clearColor, final GpuTexture depthTexture, final double clearDepth, final int regionX, final int regionY, final int regionWidth, final int regionHeight) {
      try (
         GpuTextureView colorTextureView = this.device.createTextureView(colorTexture);
         GpuTextureView depthTextureView = this.device.createTextureView(depthTexture);
      ) {
         MemoryStack stack = MemoryStack.stackPush();

         try {
            this.createRenderPass(() -> "ClearColorDepthTextures", colorTextureView, OptionalInt.empty(), depthTextureView, OptionalDouble.empty());

            assert this.currentRenderPass != null;

            VkClearRect.Buffer rects = VkClearRect.calloc(1, stack);
            rects.baseArrayLayer(0);
            rects.layerCount(1);
            rects.rect().offset().set(regionX, regionY);
            rects.rect().extent().set(regionWidth, regionHeight);
            VkClearAttachment.Buffer attachments = VkClearAttachment.calloc(2, stack);
            VkClearValue colorClearValue = VkClearValue.calloc(stack);
            VulkanUtils.putArgb(colorClearValue.color(), clearColor);
            attachments.aspectMask(1);
            attachments.clearValue(colorClearValue);
            VkClearValue depthClearValue = VkClearValue.calloc(stack);
            VkClearDepthStencilValue clearValue = depthClearValue.depthStencil();
            clearValue.depth((float)clearDepth);
            attachments.position(1);
            attachments.aspectMask(2);
            attachments.clearValue(depthClearValue);
            attachments.position(0);
            VkCommandBuffer secondaryCommandBuffer = this.currentRenderPass.allocateTransientRenderpassCommandBuffer();
            VK12.vkCmdClearAttachments(secondaryCommandBuffer, attachments, rects);
            VulkanUtils.crashIfFailure(VK12.vkEndCommandBuffer(secondaryCommandBuffer), "Failed to end VkCommandBuffer");
            this.currentRenderPass.executeCommandBuffer(secondaryCommandBuffer);
            this.submitRenderPass();
         } catch (Throwable var22) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var21) {
                  var22.addSuppressed(var21);
               }
            }

            throw var22;
         }

         if (stack != null) {
            stack.close();
         }
      }

   }

   public void clearDepthTexture(final GpuTexture depthTexture, final double clearDepth) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         this.clearDepthTextureUnsynced(stack, depthTexture, clearDepth);
         this.memoryBarrier(stack);
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

   }

   public void writeToBuffer(final GpuBufferSlice destination, final ByteBuffer data) {
      VulkanGpuBuffer destBuffer = (VulkanGpuBuffer)destination.buffer();

      try (VulkanGpuBuffer stagingBuffer = this.createStagingBuffer(data)) {
         MemoryStack stack = MemoryStack.stackPush();

         try {
            VkBufferCopy.Buffer regions = VkBufferCopy.calloc(1, stack).srcOffset(0L).dstOffset(destination.offset()).size((long)data.remaining());
            VK12.vkCmdCopyBuffer(this.commandBuffer(), stagingBuffer.vkBuffer(), destBuffer.vkBuffer(), regions);
            this.memoryBarrier(stack);
         } catch (Throwable var10) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }
            }

            throw var10;
         }

         if (stack != null) {
            stack.close();
         }
      }

   }

   public GpuBuffer.MappedView mapBuffer(final GpuBufferSlice buffer, final boolean read, final boolean write) {
      VulkanGpuBuffer vulkanBuffer = (VulkanGpuBuffer)buffer.buffer();
      return vulkanBuffer.map(buffer.offset(), buffer.length());
   }

   public void copyToBuffer(final GpuBufferSlice source, final GpuBufferSlice target) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         VkBufferCopy.Buffer copyInfo = VkBufferCopy.calloc(1, stack);
         copyInfo.srcOffset(source.offset());
         copyInfo.dstOffset(target.offset());
         copyInfo.size(source.length());
         VK12.vkCmdCopyBuffer(this.commandBuffer(), ((VulkanGpuBuffer)source.buffer()).vkBuffer(), ((VulkanGpuBuffer)target.buffer()).vkBuffer(), copyInfo);
         this.memoryBarrier(stack);
      } catch (Throwable var7) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (stack != null) {
         stack.close();
      }

   }

   public void writeToTexture(final GpuTexture destination, final NativeImage source, final int mipLevel, final int depthOrLayer, final int destX, final int destY, final int width, final int height, final int sourceX, final int sourceY) {
      int stagingBufferSize = source.getWidth() * source.getHeight() * destination.getFormat().pixelSize();
      int texelSize = destination.getFormat().pixelSize();
      int skipTexels = sourceX + sourceY * source.getWidth();
      long skipBytes = (long)skipTexels * (long)texelSize;

      try (VulkanGpuBuffer stagingBuffer = this.createStagingBuffer(MemoryUtil.memByteBuffer(source.getPointer(), stagingBufferSize))) {
         this.writeToTexture((VulkanGpuTexture)destination, stagingBuffer, skipBytes, mipLevel, depthOrLayer, destX, destY, width, height, source.getWidth(), source.getHeight());
      }

   }

   public void writeToTexture(final GpuTexture destination, final ByteBuffer source, final NativeImage.Format format, final int mipLevel, final int depthOrLayer, final int destX, final int destY, final int width, final int height) {
      try (VulkanGpuBuffer stagingBuffer = this.createStagingBuffer(source)) {
         this.writeToTexture((VulkanGpuTexture)destination, stagingBuffer, 0L, mipLevel, depthOrLayer, destX, destY, width, height, width, height);
      }

   }

   private void writeToTexture(final VulkanGpuTexture destination, final VulkanGpuBuffer stagingBuffer, final long bufferOffset, final int mipLevel, final int depthOrLayer, final int destX, final int destY, final int width, final int height, final int srcWidth, final int srcHeight) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         VkBufferImageCopy.Buffer region = VkBufferImageCopy.calloc(1, stack);
         region.bufferOffset(bufferOffset);
         region.bufferRowLength(srcWidth);
         region.bufferImageHeight(srcHeight);
         VkImageSubresourceLayers imageSubresource = region.imageSubresource();
         imageSubresource.aspectMask(1);
         imageSubresource.mipLevel(mipLevel);
         imageSubresource.baseArrayLayer(depthOrLayer);
         imageSubresource.layerCount(1);
         region.imageOffset().set(destX, destY, 0);
         region.imageExtent().set(width, height, 1);
         VK12.vkCmdCopyBufferToImage(this.commandBuffer(), stagingBuffer.vkBuffer(), destination.vkImage(), 1, region);
         this.memoryBarrier(stack);
      } catch (Throwable var17) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var16) {
               var17.addSuppressed(var16);
            }
         }

         throw var17;
      }

      if (stack != null) {
         stack.close();
      }

   }

   public void copyTextureToBuffer(final GpuTexture source, final GpuBuffer destination, final long offset, final Runnable callback, final int mipLevel) {
      this.copyTextureToBuffer(source, destination, offset, callback, mipLevel, 0, 0, source.getWidth(mipLevel), source.getHeight(mipLevel));
   }

   public void copyTextureToBuffer(final GpuTexture source, final GpuBuffer destination, final long offset, final Runnable callback, final int mipLevel, final int x, final int y, final int width, final int height) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         VkBufferImageCopy.Buffer copy = VkBufferImageCopy.calloc(1, stack);
         copy.bufferOffset(offset);
         VkImageSubresourceLayers subresource = copy.imageSubresource();
         subresource.aspectMask(VulkanConst.formatAspectMask(source.getFormat()));
         subresource.mipLevel(mipLevel);
         subresource.baseArrayLayer(0);
         subresource.layerCount(1);
         copy.imageOffset().set(x, y, 0);
         copy.imageExtent().set(width, height, 1);
         copy.bufferRowLength(width);
         copy.bufferImageHeight(height);
         VK12.vkCmdCopyImageToBuffer(this.commandBuffer(), ((VulkanGpuTexture)source).vkImage(), 1, ((VulkanGpuBuffer)destination).vkBuffer(), copy);
         this.memoryBarrier(stack);
      } catch (Throwable var15) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var14) {
               var15.addSuppressed(var14);
            }
         }

         throw var15;
      }

      if (stack != null) {
         stack.close();
      }

      Objects.requireNonNull(callback);
      this.queueForDestroy(callback::run);
   }

   public void copyTextureToTexture(final GpuTexture source, final GpuTexture destination, final int mipLevel, final int destX, final int destY, final int sourceX, final int sourceY, final int width, final int height) {
      VulkanGpuTexture vulkanSrc = (VulkanGpuTexture)source;
      VulkanGpuTexture vulkanDst = (VulkanGpuTexture)destination;
      MemoryStack stack = MemoryStack.stackPush();

      try {
         VkImageSubresourceLayers subresourceLayers = VkImageSubresourceLayers.calloc(stack);
         subresourceLayers.mipLevel(mipLevel);
         subresourceLayers.baseArrayLayer(0);
         subresourceLayers.layerCount(1);
         subresourceLayers.aspectMask(VulkanConst.formatAspectMask(source.getFormat()));
         VkImageCopy.Buffer regions = VkImageCopy.calloc(1, stack);
         regions.srcOffset().set(sourceX, sourceY, 0);
         regions.dstOffset().set(destX, destY, 0);
         regions.extent().set(width, height, 1);
         regions.srcSubresource(subresourceLayers);
         regions.dstSubresource(subresourceLayers);
         VK12.vkCmdCopyImage(this.commandBuffer(), vulkanSrc.vkImage(), 1, vulkanDst.vkImage(), 1, regions);
         this.memoryBarrier(stack);
      } catch (Throwable var16) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var15) {
               var16.addSuppressed(var15);
            }
         }

         throw var16;
      }

      if (stack != null) {
         stack.close();
      }

   }

   private boolean awaitSubmitCompletion(final long submitIndex, final long timeoutMs) {
      if (this.completedSubmitIndex >= submitIndex) {
         return true;
      } else if (submitIndex == this.currentSubmitIndex) {
         throw new IllegalStateException("Cannot wait on a fence for the current submit");
      } else {
         MemoryStack stack = MemoryStack.stackPush();

         boolean var9;
         try {
            VkSemaphoreWaitInfo waitInfo = VkSemaphoreWaitInfo.calloc(stack).sType$Default();
            waitInfo.pSemaphores(stack.longs(this.submitSemaphore));
            waitInfo.pValues(stack.longs(submitIndex));
            waitInfo.semaphoreCount(1);
            int result = VK12.vkWaitSemaphores(this.device.vkDevice(), waitInfo, timeoutMs * 1000000L);
            VulkanUtils.crashIfFailure(result, "Failed to wait for semaphore");
            boolean completed = result == 0;
            if (completed) {
               this.completedSubmitIndex = submitIndex;
            }

            var9 = completed;
         } catch (Throwable var11) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var10) {
                  var11.addSuppressed(var10);
               }
            }

            throw var11;
         }

         if (stack != null) {
            stack.close();
         }

         return var9;
      }
   }

   public GpuFence createFence() {
      return new GpuFence() {
         private final long submitIndex;
         private boolean completed;

         {
            Objects.requireNonNull(VulkanCommandEncoder.this);
            this.submitIndex = VulkanCommandEncoder.this.currentSubmitIndex;
            this.completed = false;
         }

         public boolean awaitCompletion(final long timeoutMs) {
            if (!this.completed) {
               this.completed = VulkanCommandEncoder.this.awaitSubmitCompletion(this.submitIndex, timeoutMs);
            }

            return this.completed;
         }

         public void close() {
            this.completed = true;
         }
      };
   }

   public void writeTimestamp(final GpuQueryPool pool, final int index) {
      long queryPool = ((VulkanQueryPool)pool).vkQueryPool();
      VK12.vkResetQueryPool(this.device.vkDevice(), queryPool, index, 1);
      KHRSynchronization2.vkCmdWriteTimestamp2KHR(this.commandBuffer(), 65536L, queryPool, index);
   }

   public long getTimestampNow() {
      MemoryStack stack = MemoryStack.stackPush();

      try (VulkanQueryPool queryPool = (VulkanQueryPool)this.device.createTimestampQueryPool(1)) {
         VkCommandBuffer commandBuffer = this.allocateTransientCommandBuffer(true);
         VkCommandBufferBeginInfo beginInfo = VkCommandBufferBeginInfo.calloc(stack).sType$Default();
         beginInfo.flags(1);
         VulkanUtils.crashIfFailure(VK12.vkBeginCommandBuffer(commandBuffer, beginInfo), "Failed to begin VkCommandBuffer");
         KHRSynchronization2.vkCmdWriteTimestamp2KHR(commandBuffer, 0L, queryPool.vkQueryPool(), 0);
         VulkanUtils.crashIfFailure(VK12.vkEndCommandBuffer(commandBuffer), "Failed to end VkCommandBuffer");

         try (VulkanQueue.Submission submit = this.device.graphicsQueue().beginSubmit()) {
            submit.executeCommands(commandBuffer);
         }

         LongBuffer timestampPtr = stack.callocLong(1);
         VulkanUtils.crashIfFailure(VK12.vkGetQueryPoolResults(this.device.vkDevice(), queryPool.vkQueryPool(), 0, 1, timestampPtr, 0L, 3), "Cannot fetch current timestamp");
         return timestampPtr.get(0);
      } catch (Throwable var13) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var8) {
               var13.addSuppressed(var8);
            }
         }

         throw var13;
      }

      if (stack != null) {
         stack.close();
      }
   }
}
