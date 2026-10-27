package com.mojang.renderpearl.frontend;

import com.mojang.logging.LogUtils;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.commands.GpuQueryPool;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.device.DeviceFeatures;
import com.mojang.renderpearl.api.device.DeviceLimits;
import com.mojang.renderpearl.api.pipeline.CompiledRenderPipeline;
import com.mojang.renderpearl.api.pipeline.IndexType;
import com.mojang.renderpearl.api.pipeline.RenderPassLayout;
import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.textures.GpuTexture;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import com.mojang.renderpearl.backend.api.RenderPassBackend;
import com.mojang.renderpearl.backend.common.BaseGpuBuffer;
import com.mojang.renderpearl.util.TextureViewAndSampler;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.VkDrawIndexedIndirectCommand;
import org.lwjgl.vulkan.VkDrawIndirectCommand;
import org.slf4j.Logger;

public class FrontendRenderPass implements RenderPass, RenderPass.UniformUploader {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final RenderPassBackend backend;
   private final FrontendGpuDevice device;
   private final DeviceFeatures deviceFeatures;
   private final DeviceLimits deviceLimits;
   private final Runnable onFinish;
   private final RenderPass.@Nullable RenderArea renderArea;
   private final RenderPassLayout layout;
   private final List<GpuTextureView> attachmentTextureViews;
   private final List<GpuTexture> attachmentTextures;
   private boolean isClosed;
   private int pushedDebugGroups;
   private int currentSubpassIndex = 0;
   private @Nullable FrontendRenderPipeline boundPipeline;
   private final @Nullable GpuBufferSlice[] vertexBuffers;
   protected @Nullable GpuBuffer indexBuffer;
   protected final HashMap<String, Object> uniforms;
   private boolean constantsPushed;

   public FrontendRenderPass(final RenderPassBackend backend, final FrontendGpuDevice device, final RenderPassLayout layout, final Runnable onFinish, final RenderPass.@Nullable RenderArea renderArea, final List<GpuTextureView> attachmentTextureViews) {
      super();
      this.vertexBuffers = new GpuBufferSlice[CompiledRenderPipeline.CreateInfo.MAX_VERTEX_BUFFERS];
      this.uniforms = new HashMap();
      this.constantsPushed = false;
      this.backend = backend;
      this.device = device;
      this.deviceFeatures = device.getDeviceInfo().features();
      this.deviceLimits = device.getDeviceInfo().limits();
      this.layout = layout;
      this.onFinish = onFinish;
      this.renderArea = renderArea;
      this.attachmentTextureViews = attachmentTextureViews;
      this.attachmentTextures = attachmentTextureViews.stream().map(GpuTextureView::texture).toList();
      if (this.layout().subpasses().size() != 1) {
         this.pushDebugGroup(() -> this.layout().subpass(0).name());
      }

   }

   public void close() {
      while(this.currentSubpassIndex < this.layout.subpasses().size() - 1) {
         this.nextSubpass();
         LOGGER.warn("Subpass '{}' from RenderPassLayout '{}' skipped on renderpass close, this is likely a bug!", this.layout.subpass(this.currentSubpassIndex).name(), this.layout.name());
      }

      if (this.layout().subpasses().size() != 1) {
         this.popDebugGroup();
      }

      if (!this.isClosed) {
         this.isClosed = true;
         if (this.pushedDebugGroups > 0) {
            throw new IllegalStateException("Render pass had debug groups left open!");
         }

         this.onFinish.run();
      }

   }

   public RenderPassLayout layout() {
      return this.layout;
   }

   public RenderPassLayout.Subpass activeSubpass() {
      return this.layout.subpass(this.currentSubpassIndex);
   }

   public void nextSubpass() {
      ++this.currentSubpassIndex;
      this.boundPipeline = null;
      this.popDebugGroup();
      this.backend.nextSubpass();
      this.pushDebugGroup(() -> this.layout().subpass(this.currentSubpassIndex).name());
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
      if (pipeline instanceof FrontendRenderPipeline frontendPipeline) {
         if (!frontendPipeline.subpass().equals(this.activeSubpass())) {
            RenderPassLayout.Subpass expectedSubpass = frontendPipeline.subpass();
            RenderPassLayout.Subpass activeSubpass = this.activeSubpass();
            if (expectedSubpass.index() != activeSubpass.index()) {
               int var10002 = expectedSubpass.index();
               throw new IllegalStateException("Pipeline's subpass index " + var10002 + " does not match the active subpass's index " + activeSubpass.index());
            } else {
               throw new IllegalArgumentException(String.format(Locale.ROOT, "Pipeline was compiled for Subpass '%s' from RenderPassLayout '%s' which is not compatible with active Subpass '%s' from RenderPassLayout %s'", frontendPipeline.subpass().name(), frontendPipeline.subpass().renderPassLayout().name(), activeSubpass.name(), activeSubpass.renderPassLayout().name()));
            }
         } else {
            this.boundPipeline = frontendPipeline;
            this.backend.setPipeline(frontendPipeline.backendRenderPipeline());
            this.uniforms.forEach(this::setUniform);
            this.constantsPushed = false;
         }
      } else {
         throw new IllegalArgumentException("Pipeline must be instance of FrontendCompiledRenderPipeline");
      }
   }

   public void setUniform(final String name, final @Nullable GpuTextureView textureView) {
      if (textureView != null && !this.attachmentTextureViews.contains(textureView)) {
         throw new IllegalArgumentException("TextureView that is not a RenderPass attachment cannot be bound as an input attachment");
      } else {
         this.setUniform(name, (Object)textureView);
      }
   }

   public void setUniform(final String name, final @Nullable GpuTextureView textureView, final @Nullable GpuSampler sampler) {
      if (textureView != null && sampler != null) {
         if (this.attachmentTextures.contains(textureView.texture())) {
            throw new IllegalArgumentException("Texture that is a RenderPass attachment cannot be bound as a sampled texture");
         }

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
         if (x >= this.renderArea.x() && y >= this.renderArea.y() && x + width <= this.renderArea.x() + this.renderArea.width() && y + height <= this.renderArea.y() + this.renderArea.height()) {
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
      if (slot >= 0 && slot < CompiledRenderPipeline.CreateInfo.MAX_VERTEX_BUFFERS) {
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
            if (this.device.strictValidation) {
               if (indexBuffer == null) {
                  throw new IllegalStateException("Missing index buffer");
               }

               ((BaseGpuBuffer)indexBuffer).checkCanBeUsed();
               if (indexBuffer.isClosed()) {
                  throw new IllegalStateException("Index buffer has been closed!");
               }

               if (draw.slot() < 0 || draw.slot() >= CompiledRenderPipeline.CreateInfo.MAX_VERTEX_BUFFERS) {
                  throw new IllegalStateException("Vertex buffer slot must be between 0 and " + CompiledRenderPipeline.CreateInfo.MAX_VERTEX_BUFFERS);
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

   private void validateDraw(final Collection<String> dynamicUniforms, final boolean dynamicVertexBuffer, final boolean indexed) {
      if (this.boundPipeline == null) {
         throw new IllegalStateException("Can't draw without a render pipeline");
      } else if (this.device.strictValidation) {
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
               for(int i = 0; i < this.boundPipeline.usedVertexBufferSlots().size(); ++i) {
                  int bufferSlot = this.boundPipeline.usedVertexBufferSlots().getInt(i);
                  GpuBufferSlice buffer = this.vertexBuffers[bufferSlot];
                  if (buffer == null) {
                     throw new IllegalStateException("Vertex format contains elements but vertex buffer at slot " + bufferSlot + " is null");
                  }

                  ((BaseGpuBuffer)buffer.buffer()).checkCanBeUsed();
               }
            }

            for(CompiledRenderPipeline.CreateInfo.Uniform uniform : this.boundPipeline.uniforms()) {
               Object value = this.uniforms.get(uniform.name());
               if (!dynamicUniforms.contains(uniform.name())) {
                  if (value == null) {
                     String var45 = uniform.name();
                     throw new IllegalStateException("Missing uniform " + var45 + " (should be " + String.valueOf(uniform.type()) + ")");
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
                           GpuBufferSlice var35 = var12;

                           try {
                              var36 = var35.buffer();
                           } catch (Throwable var21) {
                              throw new MatchException(var21.toString(), var21);
                           }

                           GpuBuffer length = var36;
                           var35 = var12;

                           try {
                              var38 = var35.offset();
                           } catch (Throwable var20) {
                              throw new MatchException(var20.toString(), var20);
                           }

                           long length = var38;
                           if (true) {
                              var35 = var12;

                              try {
                                 var40 = var35.length();
                              } catch (Throwable var19) {
                                 throw new MatchException(var19.toString(), var19);
                              }

                              length = var40;
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
                           var32 = var10000.view();
                        } catch (Throwable var18) {
                           throw new MatchException(var18.toString(), var18);
                        }

                        GpuTextureView length = var32;
                        var10000 = var9;

                        try {
                           var34 = var10000.sampler();
                        } catch (Throwable var17) {
                           throw new MatchException(var17.toString(), var17);
                        }

                        GpuSampler length = var34;
                        if (length.isClosed()) {
                           String var44 = uniform.name();
                           throw new IllegalStateException("Texture view " + var44 + " (" + length.texture().getLabel() + ") has been closed!");
                        }

                        if ((length.texture().usage() & 4) == 0) {
                           String var43 = uniform.name();
                           throw new IllegalStateException("Texture view " + var43 + " (" + length.texture().getLabel() + ") must have USAGE_TEXTURE_BINDING!");
                        }

                        if (length.isClosed()) {
                           String var42 = uniform.name();
                           throw new IllegalStateException("Sampler for " + var42 + " (" + length.texture().getLabel() + ") has been closed!");
                        }
                        break;
                     case INPUT_ATTACHMENT:
                        if (!(value instanceof GpuTextureView)) {
                           throw new IllegalStateException("InputAttachment must be GpuTextureView");
                        }

                        GpuTextureView textureView = (GpuTextureView)value;
                        if (textureView.isClosed()) {
                           String var41 = uniform.name();
                           throw new IllegalStateException("Texture view " + var41 + " (" + textureView.texture().getLabel() + ") has been closed!");
                        }

                        if ((textureView.texture().usage() & 4) == 0) {
                           String var10002 = uniform.name();
                           throw new IllegalStateException("Texture view " + var10002 + " (" + textureView.texture().getLabel() + ") must have USAGE_TEXTURE_BINDING!");
                        }

                        int attachmentIndex = this.activeSubpass().inputAttachments().getByte(uniform.inputAttachmentIndex());
                        if (this.attachmentTextures.get(attachmentIndex) != textureView.texture()) {
                           throw new IllegalStateException("Input attachment uniform must match input attachment binding from RenderPass and shader");
                        }
                        break;
                     default:
                        throw new IllegalStateException("Unhandled uniform type");
                  }
               }
            }

         }
      }
   }
}
