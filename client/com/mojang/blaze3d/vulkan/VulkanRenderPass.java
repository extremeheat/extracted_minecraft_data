package com.mojang.blaze3d.vulkan;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.GpuQueryPool;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderPassBackend;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.LongBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.KHRPushDescriptor;
import org.lwjgl.vulkan.KHRSynchronization2;
import org.lwjgl.vulkan.VK12;
import org.lwjgl.vulkan.VkAllocationCallbacks;
import org.lwjgl.vulkan.VkBufferViewCreateInfo;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkDescriptorBufferInfo;
import org.lwjgl.vulkan.VkDescriptorImageInfo;
import org.lwjgl.vulkan.VkRect2D;
import org.lwjgl.vulkan.VkViewport;
import org.lwjgl.vulkan.VkWriteDescriptorSet;

public class VulkanRenderPass implements RenderPassBackend {
   public static final boolean VALIDATION;
   private final VulkanDevice device;
   private final Consumer<Destroyable> garbageQueue;
   private final VkCommandBuffer primaryCommandBuffer;
   final Supplier<VkCommandBuffer> secondaryCommandBufferSupplier;
   private final RenderPass.RenderArea renderArea;
   private final int outputWidth;
   private final int outputHeight;
   private final boolean hasDepth;
   protected int pushedDebugGroups = 0;
   private @Nullable VkCommandBuffer currentSecondaryCommandBuffer;
   protected @Nullable VulkanRenderPipeline pipeline;
   private boolean anyDescriptorDirty = false;
   protected final HashMap<String, GpuBufferSlice> uniforms = new HashMap();
   protected final HashMap<String, TextureViewAndSampler> textures = new HashMap();

   public VulkanRenderPass(final VulkanDevice device, final Consumer<Destroyable> garbageQueue, final VkCommandBuffer primaryCommandBuffer, final Supplier<VkCommandBuffer> secondaryCommandBufferSupplier, final RenderPass.RenderArea renderArea, final int outputWidth, final int outputHeight, final boolean hasDepth) {
      super();
      this.device = device;
      this.garbageQueue = garbageQueue;
      this.primaryCommandBuffer = primaryCommandBuffer;
      this.secondaryCommandBufferSupplier = secondaryCommandBufferSupplier;
      this.renderArea = renderArea;
      this.outputWidth = outputWidth;
      this.outputHeight = outputHeight;
      this.hasDepth = hasDepth;
   }

   public void end() {
      this.endSecondaryCommandBuffer();
   }

   public VkCommandBuffer allocateTransientRenderpassCommandBuffer() {
      return (VkCommandBuffer)this.secondaryCommandBufferSupplier.get();
   }

   private VkCommandBuffer secondaryCommandBuffer() {
      if (this.currentSecondaryCommandBuffer == null) {
         this.currentSecondaryCommandBuffer = this.allocateTransientRenderpassCommandBuffer();
         MemoryStack stack = MemoryStack.stackPush();

         try {
            VkViewport.Buffer viewport = VkViewport.calloc(1, stack);
            viewport.x(0.0F);
            viewport.y(0.0F);
            viewport.width((float)this.outputWidth);
            viewport.height((float)this.outputHeight);
            viewport.minDepth(0.0F);
            viewport.maxDepth(1.0F);
            VK12.vkCmdSetViewport(this.currentSecondaryCommandBuffer, 0, viewport);
            setScissor(stack, this.currentSecondaryCommandBuffer, this.renderArea.x(), this.renderArea.y(), this.renderArea.width(), this.renderArea.height());
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

      return this.currentSecondaryCommandBuffer;
   }

   private void endSecondaryCommandBuffer() {
      if (this.currentSecondaryCommandBuffer != null) {
         VulkanUtils.crashIfFailure(VK12.vkEndCommandBuffer(this.currentSecondaryCommandBuffer), "Failed to end VkCommandBuffer");
         VK12.vkCmdExecuteCommands(this.primaryCommandBuffer, this.currentSecondaryCommandBuffer);
         this.currentSecondaryCommandBuffer = null;
      }
   }

   public void executeCommandBuffer(final VkCommandBuffer commandBuffer) {
      this.endSecondaryCommandBuffer();
      VK12.vkCmdExecuteCommands(this.primaryCommandBuffer, commandBuffer);
   }

   public void pushDebugGroup(final Supplier<String> label) {
      ++this.pushedDebugGroups;
      this.device.instance().debug().beginDebugGroup(this.secondaryCommandBuffer(), label);
   }

   public void popDebugGroup() {
      if (this.pushedDebugGroups == 0) {
         throw new IllegalStateException("Can't pop more debug groups than was pushed!");
      } else {
         --this.pushedDebugGroups;
         this.device.instance().debug().endDebugGroup(this.secondaryCommandBuffer());
      }
   }

   public void setPipeline(final RenderPipeline pipeline) {
      this.pipeline = this.device.getOrCompilePipeline(pipeline);
      if (!this.pipeline.isValid()) {
         throw new IllegalStateException("Pipeline is not valid (may contain invalid shaders?)");
      } else {
         this.anyDescriptorDirty = true;
         VK12.vkCmdBindPipeline(this.secondaryCommandBuffer(), 0, this.hasDepth ? this.pipeline.withDepthPipeline() : this.pipeline.withoutDepthPipeline());
      }
   }

   public void bindTexture(final String name, final @Nullable GpuTextureView textureView, final @Nullable GpuSampler sampler) {
      if (textureView != null && sampler != null) {
         this.textures.put(name, new TextureViewAndSampler((VulkanGpuTextureView)textureView, (VulkanGpuSampler)sampler));
         this.anyDescriptorDirty = true;
      } else if (textureView == null && sampler == null) {
         this.textures.remove(name);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void setUniform(final String name, final GpuBuffer value) {
      this.uniforms.put(name, value.slice());
      this.anyDescriptorDirty = true;
   }

   public void setUniform(final String name, final GpuBufferSlice value) {
      this.uniforms.put(name, value);
      this.anyDescriptorDirty = true;
   }

   public void enableScissor(final int x, final int y, final int width, final int height) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         setScissor(stack, this.secondaryCommandBuffer(), x, y, width, height);
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

   private static void setScissor(final MemoryStack stack, final VkCommandBuffer commandBuffer, final int x, final int y, final int width, final int height) {
      VkRect2D.Buffer scissor = VkRect2D.calloc(1, stack);
      scissor.offset().set(x, y);
      scissor.extent().set(width, height);
      VK12.vkCmdSetScissor(commandBuffer, 0, scissor);
   }

   public void disableScissor() {
      this.enableScissor(this.renderArea.x(), this.renderArea.y(), this.renderArea.width(), this.renderArea.height());
   }

   public void setVertexBuffer(final int slot, final GpuBuffer vertexBuffer) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         VK12.vkCmdBindVertexBuffers(this.secondaryCommandBuffer(), slot, stack.longs(((VulkanGpuBuffer)vertexBuffer).vkBuffer()), stack.longs(0L));
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

   public void setIndexBuffer(final GpuBuffer indexBuffer, final VertexFormat.IndexType indexType) {
      byte var10000;
      switch (indexType) {
         case SHORT -> var10000 = 0;
         case INT -> var10000 = 1;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      int type = var10000;
      VK12.vkCmdBindIndexBuffer(this.secondaryCommandBuffer(), ((VulkanGpuBuffer)indexBuffer).vkBuffer(), 0L, type);
   }

   public void drawIndexed(final int baseVertex, final int firstIndex, final int indexCount, final int instanceCount) {
      if (this.pipeline != null && this.pipeline.isValid()) {
         this.pushDescriptors();
         VK12.vkCmdDrawIndexed(this.secondaryCommandBuffer(), indexCount, instanceCount, firstIndex, baseVertex, 0);
      } else {
         throw new IllegalStateException("Pipeline is missing or not valid");
      }
   }

   public <T> void drawMultipleIndexed(final Collection<RenderPass.Draw<T>> draws, final @Nullable GpuBuffer defaultIndexBuffer, final VertexFormat.@Nullable IndexType defaultIndexType, final Collection<String> dynamicUniforms, final T uniformArgument) {
      for(RenderPass.Draw<T> draw : draws) {
         BiConsumer<T, RenderPass.UniformUploader> uniformUploaderConsumer = draw.uniformUploaderConsumer();
         if (uniformUploaderConsumer != null) {
            uniformUploaderConsumer.accept(uniformArgument, this::setUniform);
         }

         assert draw.indexBuffer() != null || defaultIndexBuffer != null;

         assert draw.indexType() != null || defaultIndexType != null;

         this.setIndexBuffer(draw.indexBuffer() == null ? defaultIndexBuffer : draw.indexBuffer(), draw.indexType() == null ? defaultIndexType : draw.indexType());
         this.setVertexBuffer(0, draw.vertexBuffer());
         this.drawIndexed(draw.baseVertex(), draw.firstIndex(), draw.indexCount(), 1);
      }

   }

   public void draw(final int firstVertex, final int vertexCount) {
      if (this.pipeline != null && this.pipeline.isValid()) {
         this.pushDescriptors();
         VK12.vkCmdDraw(this.secondaryCommandBuffer(), vertexCount, 1, firstVertex, vertexCount);
      }
   }

   private void pushDescriptors() {
      if (this.anyDescriptorDirty) {
         if (VALIDATION) {
            for(BindGroupLayout.UniformDescription uniform : BindGroupLayout.flattenUniforms(this.pipeline.info().getBindGroupLayouts())) {
               GpuBufferSlice value = (GpuBufferSlice)this.uniforms.get(uniform.name());
               if (value == null) {
                  String var10002 = uniform.name();
                  throw new IllegalStateException("Missing uniform " + var10002 + " (should be " + String.valueOf(uniform.type()) + ")");
               }

               if (uniform.type() == UniformType.UNIFORM_BUFFER) {
                  if (value.buffer().isClosed()) {
                     throw new IllegalStateException("Uniform buffer " + uniform.name() + " is already closed");
                  }

                  if ((value.buffer().usage() & 128) == 0) {
                     throw new IllegalStateException("Uniform buffer " + uniform.name() + " must have GpuBuffer.USAGE_UNIFORM");
                  }
               }

               if (uniform.type() == UniformType.TEXEL_BUFFER) {
                  if (value.offset() != 0L || value.length() != value.buffer().size()) {
                     throw new IllegalStateException("Uniform texel buffers do not support a slice of a buffer, must be entire buffer");
                  }

                  if ((value.buffer().usage() & 256) == 0) {
                     throw new IllegalStateException("Uniform texel buffer " + uniform.name() + " must have GpuBuffer.USAGE_UNIFORM_TEXEL_BUFFER");
                  }

                  if (uniform.gpuFormat() == null) {
                     throw new IllegalStateException("Invalid uniform texel buffer " + uniform.name() + " (missing a texture format)");
                  }
               }
            }
         }

         assert this.pipeline != null;

         VulkanBindGroupLayout layout = this.pipeline.layout();
         MemoryStack stack = MemoryStack.stackPush();

         try {
            VkWriteDescriptorSet.Buffer writes = VkWriteDescriptorSet.calloc(layout.entries().size(), stack);

            for(int i = 0; i < layout.entries().size(); ++i) {
               VulkanBindGroupLayout.Entry entry = (VulkanBindGroupLayout.Entry)layout.entries().get(i);
               VkWriteDescriptorSet set = ((VkWriteDescriptorSet)writes.get()).sType$Default();
               set.dstBinding(i);
               set.dstArrayElement(0);
               set.descriptorCount(1);
               if (entry.type() == VulkanBindGroupLayout.VulkanBindGroupEntryType.UNIFORM_BUFFER) {
                  GpuBufferSlice buffer = (GpuBufferSlice)this.uniforms.get(entry.name());
                  if (buffer == null) {
                     String var24 = entry.name();
                     throw new IllegalStateException("Missing uniform " + var24 + " (should be " + String.valueOf(entry.type()) + ")");
                  }

                  VkDescriptorBufferInfo.Buffer bufferInfo = VkDescriptorBufferInfo.calloc(1, stack);
                  bufferInfo.buffer(((VulkanGpuBuffer)buffer.buffer()).vkBuffer());
                  bufferInfo.offset(buffer.offset());
                  bufferInfo.range(buffer.length());
                  set.descriptorType(6);
                  set.pBufferInfo(bufferInfo);
               } else if (entry.type() == VulkanBindGroupLayout.VulkanBindGroupEntryType.SAMPLED_IMAGE) {
                  TextureViewAndSampler value = (TextureViewAndSampler)this.textures.get(entry.name());
                  if (value == null) {
                     throw new IllegalStateException("Missing sampler " + entry.name());
                  }

                  VkDescriptorImageInfo.Buffer imageInfo = VkDescriptorImageInfo.calloc(1, stack);
                  imageInfo.sampler(value.sampler.vkSampler());
                  imageInfo.imageView(value.view.vkImageView());
                  imageInfo.imageLayout(1);
                  set.descriptorType(1);
                  set.pImageInfo(imageInfo);
               } else if (entry.type() == VulkanBindGroupLayout.VulkanBindGroupEntryType.TEXEL_BUFFER) {
                  GpuBufferSlice value = (GpuBufferSlice)this.uniforms.get(entry.name());
                  if (value == null) {
                     String var25 = entry.name();
                     throw new IllegalStateException("Missing uniform " + var25 + " (should be " + String.valueOf(entry.type()) + ")");
                  }

                  LongBuffer bufferViewPtr = stack.callocLong(1);
                  MemoryStack var9 = stack.push();

                  try {
                     assert entry.texelBufferFormat() != null;

                     VkBufferViewCreateInfo viewCreateInfo = VkBufferViewCreateInfo.calloc(stack).sType$Default();
                     viewCreateInfo.buffer(((VulkanGpuBuffer)value.buffer()).vkBuffer());
                     viewCreateInfo.offset(value.offset());
                     viewCreateInfo.range(value.length());
                     viewCreateInfo.format(VulkanConst.toVk(entry.texelBufferFormat()));
                     VulkanUtils.crashIfFailure(VK12.vkCreateBufferView(this.device.vkDevice(), viewCreateInfo, (VkAllocationCallbacks)null, bufferViewPtr), "Couldn't create buffer view for texel buffer");
                     long bufferViewHandle = bufferViewPtr.get(0);
                     this.garbageQueue.accept((Destroyable)() -> VK12.vkDestroyBufferView(this.device.vkDevice(), bufferViewHandle, (VkAllocationCallbacks)null));
                  } catch (Throwable var15) {
                     if (var9 != null) {
                        try {
                           var9.close();
                        } catch (Throwable var14) {
                           var15.addSuppressed(var14);
                        }
                     }

                     throw var15;
                  }

                  if (var9 != null) {
                     var9.close();
                  }

                  set.descriptorType(4);
                  set.pTexelBufferView(bufferViewPtr);
               }
            }

            KHRPushDescriptor.vkCmdPushDescriptorSetKHR(this.secondaryCommandBuffer(), 0, this.pipeline.pipelineLayout(), 0, (VkWriteDescriptorSet.Buffer)writes.flip());
         } catch (Throwable var16) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var13) {
                  var16.addSuppressed(var13);
               }
            }

            throw var16;
         }

         if (stack != null) {
            stack.close();
         }

         this.anyDescriptorDirty = false;
      }
   }

   public void writeTimestamp(final GpuQueryPool pool, final int index) {
      long queryPool = ((VulkanQueryPool)pool).vkQueryPool();
      VK12.vkResetQueryPool(this.device.vkDevice(), queryPool, index, 1);
      KHRSynchronization2.vkCmdWriteTimestamp2KHR(this.secondaryCommandBuffer(), 65536L, queryPool, index);
   }

   static {
      VALIDATION = SharedConstants.IS_RUNNING_IN_IDE;
   }

   protected static record TextureViewAndSampler(VulkanGpuTextureView view, VulkanGpuSampler sampler) {
      protected TextureViewAndSampler {
         super();
      }
   }
}
