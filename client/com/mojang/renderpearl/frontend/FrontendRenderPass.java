package com.mojang.renderpearl.frontend;

import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.commands.GpuQueryPool;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.commands.RenderPassDescriptor;
import com.mojang.renderpearl.api.device.DeviceFeatures;
import com.mojang.renderpearl.api.device.DeviceLimits;
import com.mojang.renderpearl.api.pipeline.BindGroupLayout;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.CompiledRenderPipeline;
import com.mojang.renderpearl.api.pipeline.IndexType;
import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import com.mojang.renderpearl.backend.api.GpuDeviceBackend;
import com.mojang.renderpearl.backend.api.RenderPassBackend;
import com.mojang.renderpearl.backend.common.BaseGpuBuffer;
import com.mojang.renderpearl.util.TextureViewAndSampler;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import org.joml.Vector4fc;
import org.jspecify.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.VkDrawIndexedIndirectCommand;
import org.lwjgl.vulkan.VkDrawIndirectCommand;

public class FrontendRenderPass implements RenderPass, RenderPass.UniformUploader {
   private final RenderPassBackend backend;
   private final GpuDeviceBackend device;
   private final DeviceFeatures deviceFeatures;
   private final DeviceLimits deviceLimits;
   private final Runnable onFinish;
   private final RenderPass.@Nullable RenderArea renderArea;
   private final List<@Nullable RenderPassDescriptor.Attachment<Optional<Vector4fc>>> colorAttachments;
   private final boolean hasDepthAttachment;
   private boolean isClosed;
   private int pushedDebugGroups;
   private @Nullable FrontendRenderPipeline boundPipeline;
   private final @Nullable GpuBufferSlice[] vertexBuffers = new GpuBufferSlice[16];
   protected @Nullable GpuBuffer indexBuffer;
   protected final HashMap<String, Object> uniforms = new HashMap();
   private boolean constantsPushed = false;

   public FrontendRenderPass(final RenderPassBackend backend, final GpuDeviceBackend device, final List<@Nullable RenderPassDescriptor.Attachment<Optional<Vector4fc>>> colorAttachments, final boolean hasDepthAttachment, final Runnable onFinish, final RenderPass.@Nullable RenderArea renderArea) {
      super();
      this.backend = backend;
      this.device = device;
      this.deviceFeatures = device.getDeviceInfo().features();
      this.deviceLimits = device.getDeviceInfo().limits();
      this.colorAttachments = colorAttachments;
      this.hasDepthAttachment = hasDepthAttachment;
      this.onFinish = onFinish;
      this.renderArea = renderArea;
   }

   public void pushDebugGroup(final Supplier<String> label) {
      if (this.isClosed) {
         throw new IllegalStateException("Can't use a closed render pass");
      } else {
         ++this.pushedDebugGroups;
         this.backend.pushDebugGroup(label);
      }
   }

   public void popDebugGroup() {
      if (this.isClosed) {
         throw new IllegalStateException("Can't use a closed render pass");
      } else if (this.pushedDebugGroups == 0) {
         throw new IllegalStateException("Can't pop more debug groups than was pushed!");
      } else {
         --this.pushedDebugGroups;
         this.backend.popDebugGroup();
      }
   }

   public void writeTimestamp(final GpuQueryPool pool, final int index) {
      if (index >= 0 && index <= pool.size()) {
         this.backend.writeTimestamp(pool, index);
      } else {
         throw new IllegalStateException("Index " + index + " is out of range for query pool of size " + pool.size());
      }
   }

   public void setPipeline(final CompiledRenderPipeline pipeline) {
      if (!(pipeline instanceof FrontendRenderPipeline frontendPipeline)) {
         throw new IllegalArgumentException("Pipeline must be instance of FrontendCompiledRenderPipeline");
      } else {
         List<ColorTargetState> colorTargetStates = frontendPipeline.colorTargetStates();
         if (colorTargetStates.size() != this.colorAttachments.size()) {
            throw new IllegalStateException("Render pass color attachment count must match pipeline color target state count.");
         } else {
            for(int i = 0; i < this.colorAttachments.size(); ++i) {
               RenderPassDescriptor.Attachment<Optional<Vector4fc>> attachment = (RenderPassDescriptor.Attachment)this.colorAttachments.get(i);
               if (attachment != null) {
                  ColorTargetState colorTargetState = (ColorTargetState)colorTargetStates.get(i);
                  if (colorTargetState == null || colorTargetState.format() != attachment.textureView().texture().getFormat()) {
                     throw new IllegalStateException("Render pass color attachment " + i + " format doesn't match pipeline format.");
                  }
               }
            }

            this.boundPipeline = frontendPipeline;
            this.backend.setPipeline(frontendPipeline.backendRenderPipeline());
            this.uniforms.forEach(this::setUniform);
            this.constantsPushed = false;
         }
      }
   }

   public void setUniform(final String name, final @Nullable GpuTextureView textureView, final @Nullable GpuSampler sampler) {
      if (textureView != null && sampler != null) {
         TextureViewAndSampler pair = new TextureViewAndSampler(textureView, sampler);
         this.setUniform(name, (Object)pair);
      } else {
         if (textureView != null || sampler != null) {
            throw new IllegalArgumentException("textureView and sampler must both or neither be null");
         }

         this.setUniform(name, (Object)null);
      }

   }

   public void setUniform(final String name, final GpuBuffer value) {
      this.setUniform(name, value.slice());
   }

   public void setUniform(final String name, final GpuBufferSlice value) {
      int alignment = this.device.getDeviceInfo().limits().minUniformOffsetAlignment();
      if (value.offset() % (long)alignment > 0L) {
         throw new IllegalArgumentException("Uniform buffer offset must be aligned to " + alignment);
      } else {
         this.setUniform(name, (Object)value);
      }
   }

   private void setUniform(final String name, final @Nullable Object value) {
      if (value == null) {
         this.uniforms.remove(name);
      } else {
         this.uniforms.put(name, value);
      }

      if (this.boundPipeline != null) {
         int uniformIndex = this.boundPipeline.uniformIndices().getOrDefault(name, -1);
         if (uniformIndex != -1) {
            this.backend.setUniform(uniformIndex, value);
         }
      }
   }

   public void pushConstants(final ByteBuffer value) {
      if (this.boundPipeline == null) {
         throw new IllegalStateException("Must bind pipeline before pushing constants");
      } else if (value.remaining() < this.boundPipeline.pushConstantSize()) {
         throw new IllegalArgumentException("Not enough values for push constant range");
      } else {
         this.constantsPushed = true;
         this.backend.pushConstants(value);
      }
   }

   public void enableScissor(final int x, final int y, final int width, final int height) {
      if (width > 0 && height > 0) {
         if (x >= this.renderArea.x() && y >= this.renderArea.y() && x + width <= this.renderArea.x() + this.renderArea.width() && y + height <= this.renderArea.height()) {
            this.backend.enableScissor(x, y, width, height);
         } else {
            throw new IllegalArgumentException("Scissor at " + x + ", " + y + " with size " + width + "x" + height + " is out of bounds for render area " + String.valueOf(this.renderArea));
         }
      } else {
         throw new IllegalArgumentException("Scissor size must be >0, was " + width + "x" + height);
      }
   }

   public void disableScissor() {
      this.backend.disableScissor();
   }

   public void setVertexBuffer(final int slot, final @Nullable GpuBufferSlice vertexBuffer) {
      if (slot >= 0 && slot < 16) {
         if (vertexBuffer != null && vertexBuffer.buffer().isClosed()) {
            throw new IllegalStateException("Vertex buffer at slot " + slot + " has been closed!");
         } else if (vertexBuffer != null && (vertexBuffer.buffer().usage() & 32) == 0) {
            throw new IllegalStateException("Vertex buffer at slot " + slot + " doesn't have GpuBuffer.USAGE_VERTEX flag!");
         } else {
            this.vertexBuffers[slot] = vertexBuffer;
            this.backend.setVertexBuffer(slot, vertexBuffer);
         }
      } else {
         throw new IllegalArgumentException("Vertex buffer slot is out of range: " + slot);
      }
   }

   public void setIndexBuffer(final GpuBuffer indexBuffer, final IndexType indexType) {
      this.indexBuffer = indexBuffer;
      this.backend.setIndexBuffer(indexBuffer, indexType);
   }

   public void drawIndexed(final int indexCount, final int instanceCount, final int firstIndex, final int vertexOffset, final int firstInstance) {
      if (this.isClosed) {
         throw new IllegalStateException("Can't use a closed render pass");
      } else if (firstInstance != 0 && !this.deviceFeatures.nonZeroFirstInstance()) {
         throw new UnsupportedOperationException("firstInstance must be zero on when device does not support nonZeroFirstInstance");
      } else {
         this.validateDraw(List.of(), false, true);
         this.backend.drawIndexed(indexCount, instanceCount, firstIndex, vertexOffset, firstInstance);
      }
   }

   public void multiDrawIndexed(final IntBuffer drawParameters, final int instanceCount, final int firstInstance, final int drawCount) {
      if (this.isClosed) {
         throw new IllegalStateException("Can't use a closed render pass");
      } else if (!this.deviceFeatures.multiDrawDirectInterleaved()) {
         throw new UnsupportedOperationException("device does not support multiDrawDirectInterleaved");
      } else if (firstInstance != 0 && !this.deviceFeatures.nonZeroFirstInstance()) {
         throw new UnsupportedOperationException("firstInstance must be zero on when device does not support nonZeroFirstInstance");
      } else if (drawCount > this.deviceLimits.maxMultiDrawDirectInterleavedDrawCount()) {
         throw new IllegalArgumentException("May not exceed maxMultiDrawDirectInterleavedDrawCount draws in a single multiDrawDirectInterleaved call");
      } else if (drawParameters.remaining() < drawCount * 3) {
         throw new IllegalArgumentException("Not enough elements in drawParameters for drawCount draws");
      } else {
         this.validateDraw(List.of(), false, true);
         this.backend.multiDrawIndexed(drawParameters, instanceCount, firstInstance, drawCount);
      }
   }

   public void multiDrawIndexed(final PointerBuffer firstIndexOffsets, final IntBuffer indexCounts, final IntBuffer vertexOffsets, final int drawCount) {
      if (this.isClosed) {
         throw new IllegalStateException("Can't use a closed render pass");
      } else if (!this.deviceFeatures.multiDrawDirectSeparate()) {
         throw new UnsupportedOperationException("device does not support multiDrawDirectSeparate");
      } else if (firstIndexOffsets.remaining() < drawCount) {
         throw new IllegalArgumentException("firstIndexOffsets does not contain enough elements for drawCount draws");
      } else if (indexCounts.remaining() < drawCount) {
         throw new IllegalArgumentException("indexCounts does not contain enough elements for drawCount draws");
      } else if (vertexOffsets.remaining() < drawCount) {
         throw new IllegalArgumentException("vertexOffsets does not contain enough elements for drawCount draws");
      } else {
         this.validateDraw(List.of(), false, true);
         this.backend.multiDrawIndexed(firstIndexOffsets, indexCounts, vertexOffsets, drawCount);
      }
   }

   public void drawIndexedIndirect(final GpuBufferSlice commands, final int drawCount) {
      if (this.isClosed) {
         throw new IllegalStateException("Can't use a closed render pass");
      } else if (!this.deviceFeatures.drawIndirect()) {
         throw new UnsupportedOperationException("device does not support drawIndirect");
      } else if (drawCount > 1 && !this.deviceFeatures.multiDrawIndirect()) {
         throw new UnsupportedOperationException("drawCount must be one when device does not support multiDrawIndirect");
      } else if ((commands.buffer().usage() & 512) == 0) {
         throw new IllegalArgumentException("Indirect commands buffer must have GpuBuffer.USAGE_INDIRECT_PARAMETERS flag");
      } else if (commands.length() < (long)drawCount * (long)VkDrawIndexedIndirectCommand.SIZEOF) {
         throw new IllegalArgumentException("Commands buffer is not large enough to hold requested draw count at the given offset");
      } else if (commands.offset() % 4L != 0L) {
         throw new IllegalArgumentException("Commands offset must be multiple of 4");
      } else {
         this.validateDraw(List.of(), false, true);
         this.backend.drawIndexedIndirect(commands, drawCount);
      }
   }

   public <T> void drawMultipleIndexed(final Collection<RenderPass.Draw<T>> draws, final @Nullable GpuBuffer defaultIndexBuffer, final @Nullable IndexType defaultIndexType, final Collection<String> dynamicUniforms, final T uniformArgument) {
      if (this.isClosed) {
         throw new IllegalStateException("Can't use a closed render pass");
      } else {
         this.validateDraw(dynamicUniforms, true, false);
         GpuBuffer lastIndexBuffer = null;
         IndexType lastIndexType = null;

         for(RenderPass.Draw<T> draw : draws) {
            BiConsumer<T, RenderPass.UniformUploader> uniformUploaderConsumer = draw.uniformUploaderConsumer();
            if (uniformUploaderConsumer != null) {
               uniformUploaderConsumer.accept(uniformArgument, this);
            }

            IndexType indexType = draw.indexType() == null ? defaultIndexType : draw.indexType();
            GpuBuffer indexBuffer = draw.indexBuffer() == null ? defaultIndexBuffer : draw.indexBuffer();

            assert indexBuffer != null;

            assert indexType != null;

            this.setVertexBuffer(draw.slot(), draw.vertexBuffer().slice());
            if (FrontendGpuDevice.STRICT_VALIDATION) {
               if (indexBuffer == null) {
                  throw new IllegalStateException("Missing index buffer");
               }

               ((BaseGpuBuffer)indexBuffer).checkCanBeUsed();
               if (indexBuffer.isClosed()) {
                  throw new IllegalStateException("Index buffer has been closed!");
               }

               if (draw.slot() < 0 || draw.slot() >= 16) {
                  throw new IllegalStateException("Vertex buffer slot must be between 0 and 16");
               }

               if (this.vertexBuffers[draw.slot()] != null) {
                  ((BaseGpuBuffer)this.vertexBuffers[draw.slot()].buffer()).checkCanBeUsed();
               }

               if (this.vertexBuffers[draw.slot()] == null) {
                  throw new IllegalStateException("Missing vertex buffer at slot " + draw.slot());
               }

               if (this.vertexBuffers[draw.slot()].buffer().isClosed()) {
                  throw new IllegalStateException("Vertex buffer at slot " + draw.slot() + " has been closed!");
               }
            }

            if (indexBuffer != lastIndexBuffer || indexType != lastIndexType) {
               this.setIndexBuffer(indexBuffer, indexType);
               lastIndexBuffer = indexBuffer;
               lastIndexType = indexType;
            }

            assert this.boundPipeline != null;

            if (this.boundPipeline.pushConstantSize() > 0 && !this.constantsPushed) {
               throw new IllegalStateException("Missing push constants");
            }

            this.backend.drawIndexed(draw.indexCount(), 1, draw.firstIndex(), draw.baseVertex(), 0);
         }

      }
   }

   public void draw(final int vertexCount, final int instanceCount, final int firstVertex, final int firstInstance) {
      if (this.isClosed) {
         throw new IllegalStateException("Can't use a closed render pass");
      } else if (firstInstance != 0 && !this.deviceFeatures.nonZeroFirstInstance()) {
         throw new UnsupportedOperationException("firstInstance must be zero on when device does not support nonZeroFirstInstance");
      } else {
         this.validateDraw(List.of(), false, false);
         this.backend.draw(vertexCount, instanceCount, firstVertex, firstInstance);
      }
   }

   public void multiDraw(final IntBuffer drawParameters, final int instanceCount, final int firstInstance, final int drawCount) {
      if (this.isClosed) {
         throw new IllegalStateException("Can't use a closed render pass");
      } else if (!this.deviceFeatures.multiDrawDirectInterleaved()) {
         throw new UnsupportedOperationException("device does not support multiDrawDirectInterleaved");
      } else if (firstInstance != 0 && !this.deviceFeatures.nonZeroFirstInstance()) {
         throw new UnsupportedOperationException("firstInstance must be zero on when device does not support nonZeroFirstInstance");
      } else if (drawCount > this.deviceLimits.maxMultiDrawDirectInterleavedDrawCount()) {
         throw new IllegalArgumentException("May not exceed maxMultiDrawDirectInterleavedDrawCount draws in a single multiDrawDirectInterleaved call");
      } else if (drawParameters.remaining() < drawCount * 2) {
         throw new IllegalArgumentException("Not enough elements in drawParameters for drawCount draws");
      } else {
         this.validateDraw(List.of(), false, false);
         this.backend.multiDraw(drawParameters, instanceCount, firstInstance, drawCount);
      }
   }

   public void multiDraw(final IntBuffer firstVertices, final IntBuffer vertexCounts, final int drawCount) {
      if (this.isClosed) {
         throw new IllegalStateException("Can't use a closed render pass");
      } else if (!this.deviceFeatures.multiDrawDirectSeparate()) {
         throw new UnsupportedOperationException("device does not support multiDrawDirectSeparate");
      } else if (firstVertices.remaining() < drawCount) {
         throw new IllegalArgumentException("firstVertices does not contain enough elements for drawCount draws");
      } else if (vertexCounts.remaining() < drawCount) {
         throw new IllegalArgumentException("vertexCounts does not contain enough elements for drawCount draws");
      } else {
         this.validateDraw(List.of(), false, false);
         this.backend.multiDraw(firstVertices, vertexCounts, drawCount);
      }
   }

   public void drawIndirect(final GpuBufferSlice commands, final int drawCount) {
      if (this.isClosed) {
         throw new IllegalStateException("Can't use a closed render pass");
      } else if (!this.deviceFeatures.drawIndirect()) {
         throw new UnsupportedOperationException("device does not support drawIndirect");
      } else if (drawCount > 1 && !this.deviceFeatures.multiDrawIndirect()) {
         throw new UnsupportedOperationException("drawCount must be one when device does not support multiDrawIndirect");
      } else if ((commands.buffer().usage() & 512) == 0) {
         throw new IllegalArgumentException("Indirect commands buffer must have GpuBuffer.USAGE_INDIRECT_PARAMETERS flag");
      } else if (commands.length() < (long)drawCount * (long)VkDrawIndirectCommand.SIZEOF) {
         throw new IllegalArgumentException("Commands buffer is not large enough to hold requested draw count at the given offset");
      } else if (commands.offset() % 4L != 0L) {
         throw new IllegalArgumentException("Commands offset must be multiple of 4");
      } else {
         this.validateDraw(List.of(), false, false);
         this.backend.drawIndirect(commands, drawCount);
      }
   }

   public void close() {
      if (!this.isClosed) {
         this.isClosed = true;
         if (this.pushedDebugGroups > 0) {
            throw new IllegalStateException("Render pass had debug groups left open!");
         }

         this.onFinish.run();
      }

   }

   private void validateDraw(final Collection<String> dynamicUniforms, final boolean dynamicVertexBuffer, final boolean indexed) {
      if (this.boundPipeline == null) {
         throw new IllegalStateException("Can't draw without a render pipeline");
      } else if (FrontendGpuDevice.STRICT_VALIDATION) {
         if (this.boundPipeline.pushConstantSize() > 0 && !this.constantsPushed) {
            throw new IllegalStateException("Missing push constants");
         } else {
            if (indexed) {
               if (this.indexBuffer == null) {
                  throw new IllegalStateException("Missing index buffer");
               }

               ((BaseGpuBuffer)this.indexBuffer).checkCanBeUsed();
               if (this.indexBuffer.isClosed()) {
                  throw new IllegalStateException("Index buffer has been closed!");
               }

               if ((this.indexBuffer.usage() & 64) == 0) {
                  throw new IllegalStateException("Index buffer must have GpuBuffer.USAGE_INDEX!");
               }
            }

            if (!dynamicVertexBuffer) {
               for(int i = 0; i < 16; ++i) {
                  if (this.vertexBuffers[i] == null && this.boundPipeline.vertexFormats().get(i) != null) {
                     throw new IllegalStateException("Vertex format contains elements but vertex buffer at slot " + i + " is null");
                  }

                  if (this.vertexBuffers[i] != null) {
                     ((BaseGpuBuffer)this.vertexBuffers[i].buffer()).checkCanBeUsed();
                  }
               }
            }

            for(BindGroupLayout.UniformDescription uniform : this.boundPipeline.uniforms()) {
               Object value = this.uniforms.get(uniform.name());
               if (!dynamicUniforms.contains(uniform.name())) {
                  if (value == null) {
                     String var39 = uniform.name();
                     throw new IllegalStateException("Missing uniform " + var39 + " (should be " + String.valueOf(uniform.type()) + ")");
                  }

                  switch (uniform.type()) {
                     case UNIFORM_BUFFER:
                        if (!(value instanceof GpuBufferSlice)) {
                           throw new IllegalArgumentException("UBO value must be GpuBufferSlice");
                        }

                        GpuBufferSlice valueSlice = (GpuBufferSlice)value;
                        ((BaseGpuBuffer)valueSlice.buffer()).checkCanBeUsed();
                        if (valueSlice.buffer().isClosed()) {
                           throw new IllegalStateException("Uniform buffer " + uniform.name() + " is already closed");
                        }

                        if ((valueSlice.buffer().usage() & 128) == 0) {
                           throw new IllegalStateException("Uniform buffer " + uniform.name() + " must have GpuBuffer.USAGE_UNIFORM");
                        }
                        break;
                     case TEXEL_BUFFER:
                        if (value instanceof GpuBufferSlice) {
                           GpuBufferSlice var12 = (GpuBufferSlice)value;
                           GpuBufferSlice var31 = var12;

                           try {
                              var32 = var31.buffer();
                           } catch (Throwable var21) {
                              throw new MatchException(var21.toString(), var21);
                           }

                           GpuBuffer length = var32;
                           var31 = var12;

                           try {
                              var34 = var31.offset();
                           } catch (Throwable var20) {
                              throw new MatchException(var20.toString(), var20);
                           }

                           long length = var34;
                           if (true) {
                              var31 = var12;

                              try {
                                 var36 = var31.length();
                              } catch (Throwable var19) {
                                 throw new MatchException(var19.toString(), var19);
                              }

                              length = var36;
                              if (true) {
                                 if (length == 0L && length == length.size()) {
                                    if ((length.usage() & 256) == 0) {
                                       throw new IllegalStateException("Uniform texel buffer " + uniform.name() + " must have GpuBuffer.USAGE_UNIFORM_TEXEL_BUFFER");
                                    }

                                    if (uniform.gpuFormat() == null) {
                                       throw new IllegalStateException("Invalid uniform texel buffer " + uniform.name() + " (missing a texture format)");
                                    }
                                    break;
                                 }

                                 throw new IllegalStateException("Uniform texel buffers do not support a slice of a buffer, must be entire buffer");
                              }
                           }
                        }

                        throw new IllegalArgumentException("UTB value must be GpuBufferSlice");
                     case COMBINED_IMAGE_SAMPLER:
                        if (!(value instanceof TextureViewAndSampler)) {
                           throw new IllegalArgumentException("Sampler value must be TextureViewAndSampler");
                        }

                        TextureViewAndSampler var9 = (TextureViewAndSampler)value;
                        TextureViewAndSampler var10000 = var9;

                        try {
                           var28 = var10000.view();
                        } catch (Throwable var18) {
                           throw new MatchException(var18.toString(), var18);
                        }

                        GpuTextureView length = var28;
                        var10000 = var9;

                        try {
                           var30 = var10000.sampler();
                        } catch (Throwable var17) {
                           throw new MatchException(var17.toString(), var17);
                        }

                        GpuSampler length = var30;
                        if (length.isClosed()) {
                           String var38 = uniform.name();
                           throw new IllegalStateException("Texture view " + var38 + " (" + length.texture().getLabel() + ") has been closed!");
                        }

                        if ((length.texture().usage() & 4) == 0) {
                           String var37 = uniform.name();
                           throw new IllegalStateException("Texture view " + var37 + " (" + length.texture().getLabel() + ") must have USAGE_TEXTURE_BINDING!");
                        }

                        if (length.isClosed()) {
                           String var10002 = uniform.name();
                           throw new IllegalStateException("Sampler for " + var10002 + " (" + length.texture().getLabel() + ") has been closed!");
                        }
                  }
               }
            }

            if (this.boundPipeline.wantsDepthTexture() && !this.hasDepthAttachment) {
               throw new IllegalStateException(String.format(Locale.ROOT, "Render pipeline %s wants a depth texture but none was provided", this.boundPipeline.name()));
            }
         }
      }
   }
}
