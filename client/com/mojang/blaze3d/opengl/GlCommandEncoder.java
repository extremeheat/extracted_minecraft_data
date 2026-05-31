package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.IndexType;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.GpuFence;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.CommandEncoderBackend;
import com.mojang.blaze3d.systems.GpuQueryPool;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderPassBackend;
import com.mojang.blaze3d.systems.RenderPassDescriptor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.ScissorState;
import com.mojang.blaze3d.systems.TransientMemory;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.logging.LogUtils;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.BiConsumer;
import org.joml.Vector4fc;
import org.jspecify.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.ARBBaseInstance;
import org.lwjgl.opengl.ARBDrawIndirect;
import org.lwjgl.opengl.ARBMultiDrawIndirect;
import org.lwjgl.opengl.GL33C;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

class GlCommandEncoder implements CommandEncoderBackend, AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int MAX_SUBMITS_IN_FLIGHT = 2;
   private static final long NO_FENCE = 0L;
   private final GlDevice device;
   private final GlTransientMemory transientMemory;
   private final long[] fences = new long[2];
   private long currentSubmitIndex = 2L;
   private final int readFbo;
   private final int drawFbo;
   private @Nullable RenderPipeline lastPipeline;
   private @Nullable GlProgram lastProgram;
   private VertexArrayCache.@Nullable VertexArray lastVertexArray;
   private final List<@Nullable FrameBufferAttachment> renderPassColorTextures = new ArrayList();

   protected GlCommandEncoder(final GlDevice device) {
      super();
      this.device = device;
      this.transientMemory = (GlTransientMemory)(device.getDeviceInfo().features().persistentMapping() ? new GlTransientMemory.PersistentMapping(device, this) : new GlTransientMemory.Fallback(device, this));
      this.readFbo = device.directStateAccess().createFrameBufferObject();
      this.drawFbo = device.directStateAccess().createFrameBufferObject();
   }

   public void close() {
      this.transientMemory.close();
   }

   public long currentSubmitIndex() {
      return this.currentSubmitIndex;
   }

   public int currentSubmitSlot() {
      return (int)(this.currentSubmitIndex % 2L);
   }

   public void submit() {
      this.fences[this.currentSubmitSlot()] = GL33C.glFenceSync(37143, 0);
      ++this.currentSubmitIndex;
      if (!this.awaitSubmit(this.currentSubmitIndex - 2L, 9223372036854775807L)) {
         throw new IllegalStateException("Failed to wait for frame completion");
      } else {
         this.transientMemory.rotate();
      }
   }

   public boolean awaitSubmit(final long index, final long timeoutMs) {
      if (this.currentSubmitIndex > index + 2L) {
         return true;
      } else if (index == this.currentSubmitIndex) {
         if (timeoutMs == 0L) {
            return false;
         } else {
            throw new IllegalStateException("Cannot wait on a fence for the current submit");
         }
      } else {
         int submitSlot = (int)(index % 2L);
         long fence = this.fences[submitSlot];
         if (fence == 0L) {
            return true;
         } else {
            int result = GlStateManager._glClientWaitSync(fence, 1, timeoutMs);
            if (result == 37147) {
               return false;
            } else if (result == 37149) {
               throw new IllegalStateException("Failed to complete GPU fence: " + GlStateManager._getError());
            } else {
               GL33C.glDeleteSync(this.fences[submitSlot]);
               this.fences[submitSlot] = 0L;
               return true;
            }
         }
      }
   }

   public TransientMemory transientMemory() {
      return this.transientMemory;
   }

   public RenderPassBackend createRenderPass(final RenderPassDescriptor descriptor) {
      this.device.debugLabels().pushDebugGroup(descriptor.label());
      List<RenderPassDescriptor.Attachment<Optional<Vector4fc>>> colorAttachments = descriptor.colorAttachments();
      this.renderPassColorTextures.clear();

      for(RenderPassDescriptor.Attachment<Optional<Vector4fc>> colorAttachment : colorAttachments) {
         this.renderPassColorTextures.add(colorAttachment != null ? (GlTextureView)colorAttachment.textureView() : null);
      }

      RenderPassDescriptor.Attachment<OptionalDouble> depthAttachment = descriptor.depthAttachment();
      int fbo = this.device.frameBufferCache().getFbo(this.device.directStateAccess(), this.renderPassColorTextures, depthAttachment == null ? null : (GlTextureView)depthAttachment.textureView());
      GlStateManager._glBindFramebuffer(36160, fbo);
      int clearMask = 0;
      boolean needsScissor = false;

      for(int i = 0; i < colorAttachments.size(); ++i) {
         RenderPassDescriptor.Attachment<Optional<Vector4fc>> attachment = (RenderPassDescriptor.Attachment)colorAttachments.get(i);
         if (attachment != null) {
            Optional<Vector4fc> clearValue = attachment.clearValue();
            if (clearValue.isPresent()) {
               GlStateManager._colorMask(i, 15);
               GlStateManager._clearBuffer(i, (Vector4fc)clearValue.get());
            }

            clearMask |= 16384;
            needsScissor |= descriptor.renderArea != null && !descriptor.renderArea.fillsTexture(attachment.textureView());
         }
      }

      if (depthAttachment != null) {
         OptionalDouble clearValue = depthAttachment.clearValue();
         if (clearValue.isPresent()) {
            GlStateManager._depthMask(true);
         }

         clearMask |= 256;
      }

      if (clearMask != 0) {
         if (needsScissor) {
            GlStateManager._enableScissorTest();
            GlStateManager._scissorBox(descriptor.renderArea.x(), descriptor.renderArea.y(), descriptor.renderArea.width(), descriptor.renderArea.height());
         } else {
            GlStateManager._disableScissorTest();
         }
      }

      int width = 0;
      int height = 0;
      if (!colorAttachments.isEmpty()) {
         for(RenderPassDescriptor.Attachment<Optional<Vector4fc>> colorAttachment : colorAttachments) {
            if (colorAttachment != null) {
               GpuTextureView colorTexture = colorAttachment.textureView();
               width = colorTexture.getWidth(0);
               height = colorTexture.getHeight(0);
            }
         }
      } else if (depthAttachment != null) {
         width = depthAttachment.textureView().getWidth(0);
         height = depthAttachment.textureView().getHeight(0);
      }

      GlStateManager._viewport(0, 0, width, height);
      this.lastPipeline = null;
      ScissorState scissorState = new ScissorState();
      if (needsScissor) {
         scissorState.enable(descriptor.renderArea.x(), descriptor.renderArea.y(), descriptor.renderArea.width(), descriptor.renderArea.height());
      }

      return new GlRenderPass(this, this.device, depthAttachment != null, this.renderPassColorTextures.size(), scissorState);
   }

   public void clearColorTexture(final GpuTexture colorTexture, final Vector4fc clearColor) {
      this.device.directStateAccess().bindFrameBufferTextures(this.drawFbo, ((GlTexture)colorTexture).id, 0, 0, 36160);
      GL33C.glClearColor(clearColor.x(), clearColor.y(), clearColor.z(), clearColor.w());
      GlStateManager._disableScissorTest();
      GlStateManager._colorMask(15);
      GlStateManager._clear(16384);
      GlStateManager._glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
      GlStateManager._glBindFramebuffer(36160, 0);
   }

   public void clearColorAndDepthTextures(final GpuTexture colorTexture, final Vector4fc clearColor, final GpuTexture depthTexture, final double clearDepth) {
      int fbo = this.device.frameBufferCache().getFbo(this.device.directStateAccess(), Collections.singletonList((GlTexture)colorTexture), (GlTexture)depthTexture);
      GlStateManager._glBindFramebuffer(36160, fbo);
      GlStateManager._disableScissorTest();
      GL33C.glClearDepth(clearDepth);
      GL33C.glClearColor(clearColor.x(), clearColor.y(), clearColor.z(), clearColor.w());
      GlStateManager._depthMask(true);
      GlStateManager._colorMask(15);
      GlStateManager._clear(16640);
      GlStateManager._glBindFramebuffer(36160, 0);
   }

   public void clearColorAndDepthTextures(final GpuTexture colorTexture, final Vector4fc clearColor, final GpuTexture depthTexture, final double clearDepth, final int regionX, final int regionY, final int regionWidth, final int regionHeight) {
      int fbo = this.device.frameBufferCache().getFbo(this.device.directStateAccess(), Collections.singletonList((GlTexture)colorTexture), (GlTexture)depthTexture);
      GlStateManager._glBindFramebuffer(36160, fbo);
      GlStateManager._scissorBox(regionX, regionY, regionWidth, regionHeight);
      GlStateManager._enableScissorTest();
      GL33C.glClearDepth(clearDepth);
      GL33C.glClearColor(clearColor.x(), clearColor.y(), clearColor.z(), clearColor.w());
      GlStateManager._depthMask(true);
      GlStateManager._colorMask(15);
      GlStateManager._clear(16640);
      GlStateManager._glBindFramebuffer(36160, 0);
   }

   public void clearDepthTexture(final GpuTexture depthTexture, final double clearDepth) {
      this.device.directStateAccess().bindFrameBufferTextures(this.drawFbo, 0, ((GlTexture)depthTexture).id, 0, 36160);
      GL33C.glDrawBuffer(0);
      GL33C.glClearDepth(clearDepth);
      GlStateManager._depthMask(true);
      GlStateManager._disableScissorTest();
      GlStateManager._clear(256);
      GL33C.glDrawBuffer(36064);
      GlStateManager._glFramebufferTexture2D(36160, 36096, 3553, 0, 0);
      GlStateManager._glBindFramebuffer(36160, 0);
   }

   public void writeToBuffer(final GpuBufferSlice slice, final ByteBuffer data) {
      GlBuffer buffer = (GlBuffer)slice.buffer();
      buffer.checkCanBeUsed();
      this.device.directStateAccess().bufferSubData(buffer.handle(), slice.offset(), data, buffer.usage());
   }

   public void copyToBuffer(final GpuBufferSlice source, final GpuBufferSlice target) {
      GlBuffer sourceBuffer = (GlBuffer)source.buffer();
      GlBuffer targetBuffer = (GlBuffer)target.buffer();
      sourceBuffer.checkCanBeUsed();
      targetBuffer.checkCanBeUsed();
      this.device.directStateAccess().copyBufferSubData(sourceBuffer.handle(), targetBuffer.handle(), source.offset(), target.offset(), source.length());
   }

   public void writeToTexture(final GpuTexture destination, final NativeImage source, final int mipLevel, final int depthOrLayer, final int destX, final int destY, final int width, final int height, final int sourceX, final int sourceY) {
      int target;
      if ((destination.usage() & 16) != 0) {
         target = GlConst.CUBEMAP_TARGETS[depthOrLayer % 6];
         GL33C.glBindTexture(34067, ((GlTexture)destination).id);
      } else {
         target = 3553;
         GlStateManager._bindTexture(((GlTexture)destination).id);
      }

      GlStateManager._pixelStore(3314, source.getWidth());
      GlStateManager._pixelStore(3316, sourceX);
      GlStateManager._pixelStore(3315, sourceY);
      GlStateManager._pixelStore(3317, source.format().components());
      GlStateManager._texSubImage2D(target, mipLevel, destX, destY, width, height, GlConst.toGl(source.format()), 5121, source.getPointer());
   }

   public void writeToTexture(final GpuTexture destination, final ByteBuffer source, final NativeImage.Format format, final int mipLevel, final int depthOrLayer, final int destX, final int destY, final int width, final int height) {
      int target;
      if ((destination.usage() & 16) != 0) {
         target = GlConst.CUBEMAP_TARGETS[depthOrLayer % 6];
         GL33C.glBindTexture(34067, ((GlTexture)destination).id);
      } else {
         target = 3553;
         GlStateManager._bindTexture(((GlTexture)destination).id);
      }

      GlStateManager._pixelStore(3314, width);
      GlStateManager._pixelStore(3316, 0);
      GlStateManager._pixelStore(3315, 0);
      GlStateManager._pixelStore(3317, format.components());
      GlStateManager._texSubImage2D(target, mipLevel, destX, destY, width, height, GlConst.toGl(format), 5121, source);
   }

   public void copyTextureToBuffer(final GpuTexture source, final GpuBuffer destination, final long offset, final Runnable callback, final int mipLevel) {
      this.copyTextureToBuffer(source, destination, offset, callback, mipLevel, 0, 0, source.getWidth(mipLevel), source.getHeight(mipLevel));
   }

   public void copyTextureToBuffer(final GpuTexture source, final GpuBuffer destination, final long offset, final Runnable callback, final int mipLevel, final int x, final int y, final int width, final int height) {
      ((GlBuffer)destination).checkCanBeUsed();
      GlStateManager.clearGlErrors();
      this.device.directStateAccess().bindFrameBufferTextures(this.readFbo, ((GlTexture)source).glId(), 0, mipLevel, 36008);
      GlStateManager._glBindBuffer(35051, ((GlBuffer)destination).handle());
      GlStateManager._pixelStore(3330, width);
      GlStateManager._readPixels(x, y, width, height, GlConst.toGlExternalId(source.getFormat()), GlConst.toGlType(source.getFormat()), offset);
      RenderSystem.queueFencedTask(callback);
      GlStateManager._glFramebufferTexture2D(36008, 36064, 3553, 0, mipLevel);
      GlStateManager._glBindFramebuffer(36008, 0);
      GlStateManager._glBindBuffer(35051, 0);
      int error = GlStateManager._getError();
      if (error != 0) {
         String var10002 = source.getLabel();
         throw new IllegalStateException("Couldn't perform copyTobuffer for texture " + var10002 + ": GL error " + error);
      }
   }

   public void copyTextureToTexture(final GpuTexture source, final GpuTexture destination, final int mipLevel, final int destX, final int destY, final int sourceX, final int sourceY, final int width, final int height) {
      GlStateManager.clearGlErrors();
      GlStateManager._disableScissorTest();
      boolean isDepth = source.getFormat().hasDepthAspect();
      int sourceId = ((GlTexture)source).glId();
      int destId = ((GlTexture)destination).glId();
      this.device.directStateAccess().bindFrameBufferTextures(this.readFbo, isDepth ? 0 : sourceId, isDepth ? sourceId : 0, 0, 0);
      this.device.directStateAccess().bindFrameBufferTextures(this.drawFbo, isDepth ? 0 : destId, isDepth ? destId : 0, 0, 0);
      this.device.directStateAccess().blitFrameBuffers(this.readFbo, this.drawFbo, sourceX, sourceY, width, height, destX, destY, width, height, isDepth ? 256 : 16384, 9728);
      int error = GlStateManager._getError();
      if (error != 0) {
         String var10002 = source.getLabel();
         throw new IllegalStateException("Couldn't perform copyToTexture for texture " + var10002 + " to " + destination.getLabel() + ": GL error " + error);
      }
   }

   public void presentTexture(final GpuTextureView textureView) {
      GlStateManager._disableScissorTest();
      GlStateManager._viewport(0, 0, textureView.getWidth(0), textureView.getHeight(0));
      GlStateManager._depthMask(true);
      GlStateManager._colorMask(15);
      this.device.directStateAccess().bindFrameBufferTextures(this.drawFbo, ((GlTexture)textureView.texture()).glId(), 0, 0, 0);
      this.device.directStateAccess().blitFrameBuffers(this.drawFbo, 0, 0, 0, textureView.getWidth(0), textureView.getHeight(0), 0, 0, textureView.getWidth(0), textureView.getHeight(0), 16384, 9728);
   }

   public GpuFence createFence() {
      return new GlFence(this);
   }

   protected <T> void executeDrawMultiple(final GlRenderPass renderPass, final Collection<RenderPass.Draw<T>> draws, final @Nullable GpuBuffer defaultIndexBuffer, @Nullable IndexType defaultIndexType, final Collection<String> dynamicUniforms, final T uniformArgument) {
      if (this.trySetup(renderPass, dynamicUniforms)) {
         if (defaultIndexType == null) {
            defaultIndexType = IndexType.SHORT;
         }

         for(RenderPass.Draw<T> draw : draws) {
            IndexType indexType = draw.indexType() == null ? defaultIndexType : draw.indexType();
            renderPass.setIndexBuffer(draw.indexBuffer() == null ? defaultIndexBuffer : draw.indexBuffer(), indexType);
            renderPass.setVertexBuffer(draw.slot(), draw.vertexBuffer().slice());
            if (GlRenderPass.VALIDATION) {
               if (renderPass.indexBuffer == null) {
                  throw new IllegalStateException("Missing index buffer");
               }

               ((GlBuffer)renderPass.indexBuffer).checkCanBeUsed();
               if (renderPass.indexBuffer.isClosed()) {
                  throw new IllegalStateException("Index buffer has been closed!");
               }

               if (draw.slot() < 0 || draw.slot() > 16) {
                  throw new IllegalStateException("Vertex buffer slot must be between 0 and 16");
               }

               if (renderPass.vertexBuffers[draw.slot()] != null) {
                  ((GlBuffer)renderPass.vertexBuffers[draw.slot()].buffer()).checkCanBeUsed();
               }

               if (renderPass.vertexBuffers[draw.slot()] == null) {
                  throw new IllegalStateException("Missing vertex buffer at slot " + draw.slot());
               }

               if (renderPass.vertexBuffers[draw.slot()].buffer().isClosed()) {
                  throw new IllegalStateException("Vertex buffer at slot " + draw.slot() + " has been closed!");
               }
            }

            BiConsumer<T, RenderPass.UniformUploader> uniformUploaderConsumer = draw.uniformUploaderConsumer();
            if (uniformUploaderConsumer != null) {
               uniformUploaderConsumer.accept(uniformArgument, (RenderPass.UniformUploader)(name, buffer) -> {
                  ((GlBuffer)buffer.buffer()).checkCanBeUsed();
                  Uniform patt1$temp = renderPass.pipeline.program().getUniform(name);
                  if (patt1$temp instanceof Uniform.Ubo $b$0) {
                     Uniform.Ubo var10000 = $b$0;

                     try {
                        var9 = var10000.blockBinding();
                     } catch (Throwable var8) {
                        throw new MatchException(var8.toString(), var8);
                     }

                     int patt2$temp = var9;
                     if (true) {
                        GL33C.glBindBufferRange(35345, patt2$temp, ((GlBuffer)buffer.buffer()).handle(), buffer.offset(), buffer.length());
                     }
                  }

               });
            }

            if (renderPass.vertexBufferDirty) {
               this.lastVertexArray = this.device.vertexArrayCache().bindVertexArray(renderPass.pipeline.info().getVertexFormatBindings(), renderPass.vertexBuffers, (VertexArrayCache.VertexArray)null);
               renderPass.vertexBufferDirty = false;
            }

            this.drawFromBuffers(renderPass, draw.baseVertex(), draw.firstIndex(), draw.indexCount(), indexType, renderPass.pipeline, 1, 0);
         }

      }
   }

   private void validateDraw(final GlRenderPass renderPass, final @Nullable IndexType indexType) {
      if (GlRenderPass.VALIDATION) {
         if (indexType != null) {
            if (renderPass.indexBuffer == null) {
               throw new IllegalStateException("Missing index buffer");
            }

            ((GlBuffer)renderPass.indexBuffer).checkCanBeUsed();
            if (renderPass.indexBuffer.isClosed()) {
               throw new IllegalStateException("Index buffer has been closed!");
            }

            if ((renderPass.indexBuffer.usage() & 64) == 0) {
               throw new IllegalStateException("Index buffer must have GpuBuffer.USAGE_INDEX!");
            }
         }

         GlRenderPipeline pipeline = renderPass.pipeline;

         for(int i = 0; i < 16; ++i) {
            if (renderPass.vertexBuffers[i] == null && pipeline != null && pipeline.info().getVertexFormatBindings()[i] != null) {
               throw new IllegalStateException("Vertex format contains elements but vertex buffer at slot " + i + " is null");
            }

            if (renderPass.vertexBuffers[i] != null) {
               ((GlBuffer)renderPass.vertexBuffers[i].buffer()).checkCanBeUsed();
            }
         }
      }

   }

   protected void executeDraw(final GlRenderPass renderPass, final int baseVertex, final int firstIndex, final int drawCount, final @Nullable IndexType indexType, final int instanceCount, final int firstInstance) {
      if (this.trySetup(renderPass, Collections.emptyList())) {
         this.validateDraw(renderPass, indexType);
         this.lastVertexArray = this.device.vertexArrayCache().bindVertexArray(renderPass.pipeline.info().getVertexFormatBindings(), renderPass.vertexBuffers, renderPass.vertexBufferDirty ? null : this.lastVertexArray);
         renderPass.vertexBufferDirty = false;
         this.drawFromBuffers(renderPass, baseVertex, firstIndex, drawCount, indexType, renderPass.pipeline, instanceCount, firstInstance);
      }
   }

   public void executeDraws(final GlRenderPass renderPass, final @Nullable IndexType indexType, final @Nullable PointerBuffer firstIndexOffsets, final IntBuffer indexCounts, final IntBuffer vertexOffsets, final int drawCount) {
      if (this.trySetup(renderPass, Collections.emptyList())) {
         this.validateDraw(renderPass, indexType);
         this.lastVertexArray = this.device.vertexArrayCache().bindVertexArray(renderPass.pipeline.info().getVertexFormatBindings(), renderPass.vertexBuffers, renderPass.vertexBufferDirty ? null : this.lastVertexArray);
         renderPass.vertexBufferDirty = false;
         if (indexType == null) {
            GL33C.nglMultiDrawArrays(GlConst.toGl(renderPass.pipeline.info().getPrimitiveTopology()), MemoryUtil.memAddress(vertexOffsets), MemoryUtil.memAddress(indexCounts), drawCount);
         } else {
            GlStateManager._glBindBuffer(34963, ((GlBuffer)renderPass.indexBuffer).handle());

            assert firstIndexOffsets != null;

            GL33C.nglMultiDrawElementsBaseVertex(GlConst.toGl(renderPass.pipeline.info().getPrimitiveTopology()), MemoryUtil.memAddress(indexCounts), GlConst.toGl(indexType), MemoryUtil.memAddress(firstIndexOffsets), drawCount, MemoryUtil.memAddress(vertexOffsets));
         }

      }
   }

   protected void executeDrawIndirect(final GlRenderPass renderPass, final @Nullable IndexType indexType, final GlBuffer commands, final long offset, final int drawCount) {
      if (this.trySetup(renderPass, Collections.emptyList())) {
         this.validateDraw(renderPass, indexType);
         this.lastVertexArray = this.device.vertexArrayCache().bindVertexArray(renderPass.pipeline.info().getVertexFormatBindings(), renderPass.vertexBuffers, renderPass.vertexBufferDirty ? null : this.lastVertexArray);
         renderPass.vertexBufferDirty = false;
         GlStateManager._glBindBuffer(36671, commands.handle());
         if (indexType == null) {
            if (drawCount > 1) {
               ARBMultiDrawIndirect.glMultiDrawArraysIndirect(GlConst.toGl(renderPass.pipeline.info().getPrimitiveTopology()), offset, drawCount, 0);
            } else {
               ARBDrawIndirect.glDrawArraysIndirect(GlConst.toGl(renderPass.pipeline.info().getPrimitiveTopology()), offset);
            }
         } else {
            GlStateManager._glBindBuffer(34963, ((GlBuffer)renderPass.indexBuffer).handle());
            if (drawCount > 1) {
               ARBMultiDrawIndirect.glMultiDrawElementsIndirect(GlConst.toGl(renderPass.pipeline.info().getPrimitiveTopology()), GlConst.toGl(indexType), offset, drawCount, 0);
            } else {
               ARBDrawIndirect.glDrawElementsIndirect(GlConst.toGl(renderPass.pipeline.info().getPrimitiveTopology()), GlConst.toGl(indexType), offset);
            }
         }

      }
   }

   private void drawFromBuffers(final GlRenderPass renderPass, final int baseVertex, final int firstIndex, final int drawCount, final @Nullable IndexType indexType, final GlRenderPipeline pipeline, final int instanceCount, final int firstInstance) {
      if (indexType != null) {
         GlStateManager._glBindBuffer(34963, ((GlBuffer)renderPass.indexBuffer).handle());
         if (firstInstance > 0) {
            ARBBaseInstance.glDrawElementsInstancedBaseVertexBaseInstance(GlConst.toGl(pipeline.info().getPrimitiveTopology()), drawCount, GlConst.toGl(indexType), (long)firstIndex * (long)indexType.bytes, instanceCount, baseVertex, firstInstance);
         } else {
            GL33C.glDrawElementsInstancedBaseVertex(GlConst.toGl(pipeline.info().getPrimitiveTopology()), drawCount, GlConst.toGl(indexType), (long)firstIndex * (long)indexType.bytes, instanceCount, baseVertex);
         }
      } else if (firstInstance > 0) {
         ARBBaseInstance.glDrawArraysInstancedBaseInstance(GlConst.toGl(pipeline.info().getPrimitiveTopology()), baseVertex, drawCount, instanceCount, firstInstance);
      } else {
         GL33C.glDrawArraysInstanced(GlConst.toGl(pipeline.info().getPrimitiveTopology()), baseVertex, drawCount, instanceCount);
      }

   }

   private boolean trySetup(final GlRenderPass renderPass, final Collection<String> dynamicUniforms) {
      if (!GlRenderPass.VALIDATION) {
         if (renderPass.pipeline == null || renderPass.pipeline.program() == GlProgram.INVALID_PROGRAM) {
            return false;
         }
      } else {
         if (renderPass.pipeline == null) {
            throw new IllegalStateException("Can't draw without a render pipeline");
         }

         if (renderPass.pipeline.program() == GlProgram.INVALID_PROGRAM) {
            throw new IllegalStateException("Pipeline contains invalid shader program");
         }

         for(BindGroupLayout.UniformDescription uniform : BindGroupLayout.flattenUniforms(renderPass.pipeline.info().getBindGroupLayouts())) {
            GpuBufferSlice value = (GpuBufferSlice)renderPass.uniforms.get(uniform.name());
            if (!dynamicUniforms.contains(uniform.name())) {
               if (value == null) {
                  String var10002 = uniform.name();
                  throw new IllegalStateException("Missing uniform " + var10002 + " (should be " + String.valueOf(uniform.type()) + ")");
               }

               ((GlBuffer)value.buffer()).checkCanBeUsed();
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

         for(Map.Entry<String, Uniform> entry : renderPass.pipeline.program().getUniforms().entrySet()) {
            if (entry.getValue() instanceof Uniform.Sampler) {
               String name = (String)entry.getKey();
               GlRenderPass.TextureViewAndSampler viewAndSampler = (GlRenderPass.TextureViewAndSampler)renderPass.samplers.get(name);
               if (viewAndSampler == null) {
                  throw new IllegalStateException("Missing sampler " + name);
               }

               GlTextureView textureView = viewAndSampler.view();
               if (textureView.isClosed()) {
                  throw new IllegalStateException("Texture view " + name + " (" + textureView.texture().getLabel() + ") has been closed!");
               }

               if ((textureView.texture().usage() & 4) == 0) {
                  throw new IllegalStateException("Texture view " + name + " (" + textureView.texture().getLabel() + ") must have USAGE_TEXTURE_BINDING!");
               }

               if (viewAndSampler.sampler().isClosed()) {
                  throw new IllegalStateException("Sampler for " + name + " (" + textureView.texture().getLabel() + ") has been closed!");
               }
            }
         }

         if (renderPass.pipeline.info().wantsDepthTexture() && !renderPass.hasDepthTexture()) {
            LOGGER.warn("Render pipeline {} wants a depth texture but none was provided - this is probably a bug", renderPass.pipeline.info().getLocation());
         }
      }

      RenderPipeline pipeline = renderPass.pipeline.info();
      GlProgram glProgram = renderPass.pipeline.program();
      this.applyPipelineState(pipeline);
      boolean differentProgram = this.lastProgram != glProgram;
      if (differentProgram) {
         GlStateManager._glUseProgram(glProgram.getProgramId());
         this.lastProgram = glProgram;
      }

      label216:
      for(Map.Entry<String, Uniform> entry : glProgram.getUniforms().entrySet()) {
         String name = (String)entry.getKey();
         boolean isDirty = renderPass.dirtyUniforms.contains(name);
         Uniform.Ubo var10000 = (Uniform)entry.getValue();
         Objects.requireNonNull(var10000);
         Uniform var10 = var10000;
         byte var11 = 0;

         while(true) {
            //$FF: var11->value
            //0->com/mojang/blaze3d/opengl/Uniform$Ubo
            //1->com/mojang/blaze3d/opengl/Uniform$Utb
            //2->com/mojang/blaze3d/opengl/Uniform$Sampler
            switch (var10.typeSwitch<invokedynamic>(var10, var11)) {
               case 0:
                  Uniform.Ubo var12 = (Uniform.Ubo)var10;
                  var10000 = var12;

                  try {
                     var65 = var10000.blockBinding();
                  } catch (Throwable var32) {
                     throw new MatchException(var32.toString(), var32);
                  }

                  int blockBinding = var65;
                  if (true) {
                     if (isDirty) {
                        GpuBufferSlice bufferView = (GpuBufferSlice)renderPass.uniforms.get(name);
                        GL33C.glBindBufferRange(35345, blockBinding, ((GlBuffer)bufferView.buffer()).handle(), bufferView.offset(), bufferView.length());
                     }
                     continue label216;
                  }

                  var11 = 1;
                  break;
               case 1:
                  Uniform.Utb bufferView = (Uniform.Utb)var10;
                  Uniform.Utb var56 = bufferView;

                  try {
                     var57 = var56.location();
                  } catch (Throwable var31) {
                     throw new MatchException(var31.toString(), var31);
                  }

                  int texture = var57;
                  if (true) {
                     var56 = bufferView;

                     try {
                        var59 = var56.samplerIndex();
                     } catch (Throwable var30) {
                        throw new MatchException(var30.toString(), var30);
                     }

                     texture = var59;
                     if (true) {
                        var56 = bufferView;

                        try {
                           var61 = var56.format();
                        } catch (Throwable var29) {
                           throw new MatchException(var29.toString(), var29);
                        }

                        GpuFormat texture = var61;
                        var56 = bufferView;

                        try {
                           var63 = var56.texture();
                        } catch (Throwable var28) {
                           throw new MatchException(var28.toString(), var28);
                        }

                        int texture = var63;
                        if (true) {
                           if (differentProgram || isDirty) {
                              GlStateManager._glUniform1i(texture, texture);
                           }

                           GlStateManager._activeTexture('\u84c0' + texture);
                           GL33C.glBindTexture(35882, texture);
                           if (isDirty) {
                              GpuBufferSlice bufferView = (GpuBufferSlice)renderPass.uniforms.get(name);
                              GL33C.glTexBuffer(35882, GlConst.toGlInternalId(texture), ((GlBuffer)bufferView.buffer()).handle());
                           }
                           continue label216;
                        }
                     }
                  }

                  var11 = 2;
                  break;
               case 2:
                  Uniform.Sampler bufferView = (Uniform.Sampler)var10;
                  Uniform.Sampler var52 = bufferView;

                  try {
                     var53 = var52.location();
                  } catch (Throwable var27) {
                     throw new MatchException(var27.toString(), var27);
                  }

                  int location = var53;
                  if (true) {
                     var52 = bufferView;

                     try {
                        var55 = var52.samplerIndex();
                     } catch (Throwable var26) {
                        throw new MatchException(var26.toString(), var26);
                     }

                     location = var55;
                     if (true) {
                        GlRenderPass.TextureViewAndSampler viewAndSampler = (GlRenderPass.TextureViewAndSampler)renderPass.samplers.get(name);
                        if (viewAndSampler == null) {
                           continue label216;
                        }

                        GlTextureView textureView = viewAndSampler.view();
                        if (differentProgram || isDirty) {
                           GlStateManager._glUniform1i(location, location);
                        }

                        GlStateManager._activeTexture('\u84c0' + location);
                        GlTexture texture = textureView.texture();
                        int target;
                        if ((texture.usage() & 16) != 0) {
                           target = 34067;
                           GL33C.glBindTexture(34067, texture.id);
                        } else {
                           target = 3553;
                           GlStateManager._bindTexture(texture.id);
                        }

                        GL33C.glBindSampler(location, viewAndSampler.sampler().getId());
                        GlStateManager._texParameter(target, 33084, textureView.baseMipLevel());
                        GlStateManager._texParameter(target, 33085, textureView.baseMipLevel() + textureView.mipLevels() - 1);
                        continue label216;
                     }
                  }

                  var11 = 3;
                  break;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }
         }
      }

      renderPass.dirtyUniforms.clear();
      int[] drawBuffers = new int[renderPass.colorAttachmentCount];

      for(int i = 0; i < renderPass.colorAttachmentCount; ++i) {
         drawBuffers[i] = '\u8ce0' + i;
      }

      GL33C.glDrawBuffers(drawBuffers);
      if (renderPass.isScissorEnabled()) {
         GlStateManager._enableScissorTest();
         GlStateManager._scissorBox(renderPass.getScissorX(), renderPass.getScissorY(), renderPass.getScissorWidth(), renderPass.getScissorHeight());
      } else {
         GlStateManager._disableScissorTest();
      }

      return true;
   }

   private void applyPipelineState(final RenderPipeline pipeline) {
      if (this.lastPipeline != pipeline) {
         this.lastPipeline = pipeline;
         DepthStencilState depthStencilState = pipeline.getDepthStencilState();
         if (depthStencilState != null) {
            GlStateManager._enableDepthTest();
            GlStateManager._depthFunc(GlConst.toGl(depthStencilState.depthTest()));
            GlStateManager._depthMask(depthStencilState.writeDepth());
            if (depthStencilState.depthBiasConstant() == 0.0F && depthStencilState.depthBiasScaleFactor() == 0.0F) {
               GlStateManager._disablePolygonOffset();
            } else {
               GlStateManager._polygonOffset(depthStencilState.depthBiasScaleFactor(), depthStencilState.depthBiasConstant());
               GlStateManager._enablePolygonOffset();
            }
         } else {
            GlStateManager._disableDepthTest();
            GlStateManager._depthMask(false);
            GlStateManager._disablePolygonOffset();
         }

         if (pipeline.isCull()) {
            GlStateManager._enableCull();
         } else {
            GlStateManager._disableCull();
         }

         ColorTargetState[] colorTargetStates = pipeline.getColorTargetStates();

         for(int i = 0; i < colorTargetStates.length; ++i) {
            ColorTargetState state = colorTargetStates[i];
            if (state != null) {
               if (state.blendFunction().isPresent()) {
                  GlStateManager._enableBlend(i);
                  BlendFunction blendFunction = (BlendFunction)state.blendFunction().get();
                  GlStateManager._blendFuncSeparate(GlConst.toGl(blendFunction.color().sourceFactor()), GlConst.toGl(blendFunction.color().destFactor()), GlConst.toGl(blendFunction.alpha().sourceFactor()), GlConst.toGl(blendFunction.alpha().destFactor()));
                  GlStateManager._blendEquationSeparate(GlConst.toGl(blendFunction.color().op()), GlConst.toGl(blendFunction.alpha().op()));
               } else {
                  GlStateManager._disableBlend(i);
               }
            }
         }

         GlStateManager._polygonMode(1032, GlConst.toGl(pipeline.getPolygonMode()));

         for(int i = 0; i < colorTargetStates.length; ++i) {
            ColorTargetState state = colorTargetStates[i];
            if (state != null) {
               GlStateManager._colorMask(i, state.writeMask());
            }
         }

      }
   }

   public void submitRenderPass() {
      GlStateManager._glBindFramebuffer(36160, 0);
      this.device.debugLabels().popDebugGroup();
   }

   public void writeTimestamp(final GpuQueryPool pool, final int index) {
      ((GlQueryPool)pool).writeTimestamp(index);
   }
}
