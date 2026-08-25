package com.mojang.renderpearl.backend.opengl;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.ScissorState;
import com.mojang.logging.LogUtils;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.buffers.TransientMemory;
import com.mojang.renderpearl.api.commands.GpuFence;
import com.mojang.renderpearl.api.commands.GpuQueryPool;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.commands.RenderPassDescriptor;
import com.mojang.renderpearl.api.pipeline.IndexType;
import com.mojang.renderpearl.api.textures.GpuTexture;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import com.mojang.renderpearl.backend.api.CommandEncoderBackend;
import com.mojang.renderpearl.backend.api.RenderPassBackend;
import com.mojang.renderpearl.util.TextureViewAndSampler;
import com.mojang.renderpearl.util.UncheckedAutoCloseable;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import org.joml.Vector4fc;
import org.jspecify.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.ARBBaseInstance;
import org.lwjgl.opengl.ARBDrawIndirect;
import org.lwjgl.opengl.ARBMultiDrawIndirect;
import org.lwjgl.opengl.GL33C;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

class GlCommandEncoder implements CommandEncoderBackend, UncheckedAutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int MAX_SUBMITS_IN_FLIGHT = 2;
   private static final long NO_FENCE = 0L;
   private final GlDevice device;
   private final GlTransientMemory transientMemory;
   private final long[] fences = new long[2];
   private long currentSubmitIndex = 2L;
   private final int readFbo;
   private final int drawFbo;
   private @Nullable GlRenderPipeline lastPipeline;
   private @Nullable GlProgram lastProgram;
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
      if (!this.awaitSubmit(this.currentSubmitIndex - 2L, -1L)) {
         throw new IllegalStateException("Failed to wait for frame completion");
      } else {
         this.transientMemory.rotate();
      }
   }

   public boolean awaitSubmit(final long index, final long timeoutNS) {
      if (this.currentSubmitIndex > index + 2L) {
         return true;
      } else if (index == this.currentSubmitIndex) {
         if (timeoutNS == 0L) {
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
            int result = GlStateManager._glClientWaitSync(fence, 1, timeoutNS);
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

      RenderPass.RenderArea renderArea = descriptor.renderArea();
      GlStateManager._enableScissorTest();
      GlStateManager._scissorBox(renderArea.x(), renderArea.y(), renderArea.width(), renderArea.height());

      for(int i = 0; i < colorAttachments.size(); ++i) {
         RenderPassDescriptor.Attachment<Optional<Vector4fc>> attachment = (RenderPassDescriptor.Attachment)colorAttachments.get(i);
         if (attachment != null) {
            Optional<Vector4fc> clearValue = attachment.clearValue();
            if (clearValue.isPresent()) {
               GlStateManager._colorMask(i, 15);
               GlStateManager._clearBuffer(i, (Vector4fc)clearValue.get());
            }
         }
      }

      if (depthAttachment != null) {
         OptionalDouble clearValue = depthAttachment.clearValue();
         if (clearValue.isPresent()) {
            GlStateManager._depthMask(true);
            GlStateManager._clearBuffer(clearValue.getAsDouble());
         }
      }

      GlStateManager._viewport(0, 0, width, height);
      int[] drawBuffers = new int[this.renderPassColorTextures.size()];

      for(int i = 0; i < this.renderPassColorTextures.size(); ++i) {
         if (colorAttachments.get(i) != null) {
            drawBuffers[i] = '\u8ce0' + i;
         } else {
            drawBuffers[i] = 0;
         }
      }

      GL33C.glDrawBuffers(drawBuffers);
      this.lastPipeline = null;
      ScissorState scissorState = new ScissorState();
      scissorState.enable(renderArea.x(), renderArea.y(), renderArea.width(), renderArea.height());
      return new GlRenderPass(this, this.device, this.renderPassColorTextures.size(), scissorState);
   }

   public void clearColorTexture(final GpuTexture colorTexture, final Vector4fc clearColor) {
      GL33C.glClearColor(clearColor.x(), clearColor.y(), clearColor.z(), clearColor.w());
      GlStateManager._disableScissorTest();
      GlStateManager._colorMask(15);

      for(int i = 0; i < colorTexture.getMipLevels(); ++i) {
         this.device.directStateAccess().bindFrameBufferTextures(this.drawFbo, ((GlTexture)colorTexture).id, 0, i, 36160);
         GlStateManager._clear(16384);
      }

      GlStateManager._glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
      GlStateManager._glBindFramebuffer(36160, 0);
   }

   public void clearColorAndDepthTextures(final GpuTexture colorTexture, final Vector4fc clearColor, final GpuTexture depthTexture, final double clearDepth) {
      GlStateManager._disableScissorTest();
      GL33C.glClearDepth(clearDepth);
      GL33C.glClearColor(clearColor.x(), clearColor.y(), clearColor.z(), clearColor.w());
      GlStateManager._depthMask(true);
      GlStateManager._colorMask(15);

      for(int i = 0; i < colorTexture.getMipLevels(); ++i) {
         int fbo = this.device.frameBufferCache().getFbo(this.device.directStateAccess(), Collections.singletonList((GlTexture)colorTexture), (GlTexture)depthTexture, i);
         GlStateManager._glBindFramebuffer(36160, fbo);
         GlStateManager._clear(16640);
      }

      GlStateManager._glBindFramebuffer(36160, 0);
   }

   public void clearColorAndDepthTextures(final GpuTexture colorTexture, final Vector4fc clearColor, final GpuTexture depthTexture, final double clearDepth, final int regionX, final int regionY, final int regionWidth, final int regionHeight, final int mipLevel) {
      GlStateManager._scissorBox(regionX, regionY, regionWidth, regionHeight);
      GlStateManager._enableScissorTest();
      GL33C.glClearDepth(clearDepth);
      GL33C.glClearColor(clearColor.x(), clearColor.y(), clearColor.z(), clearColor.w());
      GlStateManager._depthMask(true);
      GlStateManager._colorMask(15);
      int fbo = this.device.frameBufferCache().getFbo(this.device.directStateAccess(), Collections.singletonList((GlTexture)colorTexture), (GlTexture)depthTexture, mipLevel);
      GlStateManager._glBindFramebuffer(36160, fbo);
      GlStateManager._clear(16640);
      GlStateManager._glBindFramebuffer(36160, 0);
   }

   public void clearDepthTexture(final GpuTexture depthTexture, final double clearDepth) {
      GL33C.glClearDepth(clearDepth);
      GlStateManager._depthMask(true);
      GlStateManager._disableScissorTest();

      for(int i = 0; i < depthTexture.getMipLevels(); ++i) {
         this.device.directStateAccess().bindFrameBufferTextures(this.drawFbo, 0, ((GlTexture)depthTexture).id, i, 36160);
         GL33C.glDrawBuffer(0);
         GlStateManager._clear(256);
      }

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

   public void writeToTexture(final GpuTexture destination, final ByteBuffer source, final int mipLevel, final int depthOrLayer, final int destX, final int destY, final int width, final int height) {
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
      GlStateManager._pixelStore(3317, destination.getFormat().byteAlignment());
      GlStateManager._texSubImage2D(target, mipLevel, destX, destY, width, height, GlConst.toGlExternalId(destination.getFormat()), GlConst.toGlType(destination.getFormat()), source);
   }

   public void copyBufferToTexture(final GpuBufferSlice source, final int sourceX, final int sourceY, final int sourceWidth, final int sourceHeight, final GpuTexture destination, final int destinationX, final int destinationY, final int copyWidth, final int copyHeight, final int mipLevel, final int arrayLayer) {
      int target;
      if ((destination.usage() & 16) != 0) {
         target = GlConst.CUBEMAP_TARGETS[arrayLayer % 6];
         GL33C.glBindTexture(34067, ((GlTexture)destination).id);
      } else {
         target = 3553;
         GlStateManager._bindTexture(((GlTexture)destination).id);
      }

      int texelSize = destination.getFormat().blockSize();
      long skipTexels = (long)sourceX + (long)sourceY * (long)sourceWidth;
      long skipBytes = skipTexels * (long)texelSize;
      GlBuffer sourceGlBuffer = (GlBuffer)source.buffer();
      GlStateManager._glBindBuffer(35052, sourceGlBuffer.handle());
      GlStateManager._pixelStore(3314, sourceWidth);
      GlStateManager._pixelStore(32878, sourceHeight);
      GlStateManager._pixelStore(3316, 0);
      GlStateManager._pixelStore(3315, 0);
      GlStateManager._pixelStore(3317, destination.getFormat().byteAlignment());
      GlStateManager._texSubImage2D(target, mipLevel, destinationX, destinationY, copyWidth, copyHeight, GlConst.toGlExternalId(destination.getFormat()), GlConst.toGlType(destination.getFormat()), source.offset() + skipBytes);
      GlStateManager._glBindBuffer(35052, 0);
   }

   public void copyTextureToBuffer(final GpuTexture source, final GpuBuffer destination, final long offset, final Runnable callback, final int mipLevel) {
      this.copyTextureToBuffer(source, destination, offset, callback, mipLevel, 0, 0, source.getWidth(mipLevel), source.getHeight(mipLevel));
   }

   public void copyTextureToBuffer(final GpuTexture source, final GpuBuffer destination, final long offset, final Runnable callback, final int mipLevel, final int x, final int y, final int width, final int height) {
      ((GlBuffer)destination).checkCanBeUsed();
      GlStateManager.clearGlErrors();
      boolean isDepth = source.getFormat().hasDepthAspect();
      int textureId = ((GlTexture)source).glId();
      this.device.directStateAccess().bindFrameBufferTextures(this.readFbo, !isDepth ? textureId : 0, isDepth ? textureId : 0, mipLevel, 36008);
      GlStateManager._glBindBuffer(35051, ((GlBuffer)destination).handle());
      GlStateManager._pixelStore(3333, source.getFormat().byteAlignment());
      GlStateManager._pixelStore(3330, width);
      if (isDepth) {
         GlStateManager._glReadBuffer(0);
      }

      GlStateManager._readPixels(x, y, width, height, GlConst.toGlExternalId(source.getFormat()), GlConst.toGlType(source.getFormat()), offset);
      RenderSystem.queueFencedTask(callback);
      GlStateManager._glFramebufferTexture2D(36008, isDepth ? '\u8d00' : '\u8ce0', 3553, 0, mipLevel);
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
      this.device.directStateAccess().bindFrameBufferTextures(this.readFbo, isDepth ? 0 : sourceId, isDepth ? sourceId : 0, mipLevel, 0);
      this.device.directStateAccess().bindFrameBufferTextures(this.drawFbo, isDepth ? 0 : destId, isDepth ? destId : 0, mipLevel, 0);
      this.device.directStateAccess().blitFrameBuffers(this.readFbo, this.drawFbo, sourceX, sourceY, width, height, destX, destY, width, height, isDepth ? 256 : 16384, 9728);
      int error = GlStateManager._getError();
      if (error != 0) {
         String var10002 = source.getLabel();
         throw new IllegalStateException("Couldn't perform copyToTexture for texture " + var10002 + " to " + destination.getLabel() + ": GL error " + error);
      }
   }

   void presentTexture(final long windowHandle, final GpuTextureView textureView, final int swapchainWidth, final int swapchainHeight) {
      this.device.makeCurrent(windowHandle);
      int destY = Math.max(0, swapchainHeight - textureView.getHeight(0));
      int copyWidth = Math.min(swapchainWidth, textureView.getWidth(0));
      int copyHeight = Math.min(swapchainHeight, textureView.getHeight(0));
      GlStateManager._disableScissorTest();
      GlStateManager._viewport(0, 0, textureView.getWidth(0), textureView.getHeight(0));
      GlStateManager._depthMask(true);
      GlStateManager._colorMask(15);
      this.device.directStateAccess().bindFrameBufferTextures(this.drawFbo, ((GlTexture)textureView.texture()).glId(), 0, 0, 0);
      this.device.directStateAccess().blitFrameBuffers(this.drawFbo, 0, 0, 0, copyWidth, copyHeight, 0, destY, copyWidth, copyHeight + destY, 16384, 9728);
   }

   public GpuFence createFence() {
      return new GlFence(this);
   }

   protected void executeDraw(final GlRenderPass renderPass, final int baseVertex, final int firstIndex, final int drawCount, final @Nullable IndexType indexType, final int instanceCount, final int firstInstance) {
      this.setupDraw(renderPass);
      if (indexType != null) {
         if (firstInstance > 0) {
            ARBBaseInstance.glDrawElementsInstancedBaseVertexBaseInstance(renderPass.pipeline.primitiveTopology(), drawCount, GlConst.toGl(indexType), (long)firstIndex * (long)indexType.bytes, instanceCount, baseVertex, firstInstance);
         } else {
            GL33C.glDrawElementsInstancedBaseVertex(renderPass.pipeline.primitiveTopology(), drawCount, GlConst.toGl(indexType), (long)firstIndex * (long)indexType.bytes, instanceCount, baseVertex);
         }
      } else if (firstInstance > 0) {
         ARBBaseInstance.glDrawArraysInstancedBaseInstance(renderPass.pipeline.primitiveTopology(), baseVertex, drawCount, instanceCount, firstInstance);
      } else {
         GL33C.glDrawArraysInstanced(renderPass.pipeline.primitiveTopology(), baseVertex, drawCount, instanceCount);
      }

   }

   public void executeDraws(final GlRenderPass renderPass, final @Nullable IndexType indexType, final @Nullable PointerBuffer firstIndexOffsets, final IntBuffer indexCounts, final IntBuffer vertexOffsets, final int drawCount) {
      this.setupDraw(renderPass);
      if (indexType == null) {
         GL33C.nglMultiDrawArrays(renderPass.pipeline.primitiveTopology(), MemoryUtil.memAddress(vertexOffsets), MemoryUtil.memAddress(indexCounts), drawCount);
      } else {
         assert firstIndexOffsets != null;

         GL33C.nglMultiDrawElementsBaseVertex(renderPass.pipeline.primitiveTopology(), MemoryUtil.memAddress(indexCounts), GlConst.toGl(indexType), MemoryUtil.memAddress(firstIndexOffsets), drawCount, MemoryUtil.memAddress(vertexOffsets));
      }

   }

   protected void executeDrawIndirect(final GlRenderPass renderPass, final @Nullable IndexType indexType, final GlBuffer commands, final long offset, final int drawCount) {
      this.setupDraw(renderPass);
      GlStateManager._glBindBuffer(36671, commands.handle());
      if (indexType == null) {
         if (drawCount > 1) {
            ARBMultiDrawIndirect.glMultiDrawArraysIndirect(renderPass.pipeline.primitiveTopology(), offset, drawCount, 0);
         } else {
            ARBDrawIndirect.glDrawArraysIndirect(renderPass.pipeline.primitiveTopology(), offset);
         }
      } else if (drawCount > 1) {
         ARBMultiDrawIndirect.glMultiDrawElementsIndirect(renderPass.pipeline.primitiveTopology(), GlConst.toGl(indexType), offset, drawCount, 0);
      } else {
         ARBDrawIndirect.glDrawElementsIndirect(renderPass.pipeline.primitiveTopology(), GlConst.toGl(indexType), offset);
      }

   }

   private void setupDraw(final GlRenderPass renderPass) {
      if (renderPass.vertexBufferDirty) {
         renderPass.pipeline.vertexArray().bind(renderPass.vertexBuffers);
         renderPass.vertexBufferDirty = false;
      }

      if (renderPass.indexBufferDirty) {
         renderPass.indexBufferDirty = false;
         GlStateManager._glBindBuffer(34963, ((GlBuffer)renderPass.indexBuffer).handle());
      }

      if (this.lastPipeline != renderPass.pipeline) {
         renderPass.pipeline.bind();
         this.lastPipeline = renderPass.pipeline;
      }

      GlProgram glProgram = renderPass.pipeline.program();
      if (renderPass.pushConstantsDirty) {
         renderPass.pushConstantsDirty = false;
         Uniform.Ubo pushConstantUBO = glProgram.pushConstant();
         if (pushConstantUBO != null) {
            assert renderPass.pushConstants != null;

            GL33C.glBindBufferRange(35345, pushConstantUBO.blockBinding(), ((GlBuffer)renderPass.pushConstants.buffer()).handle(), renderPass.pushConstants.offset(), renderPass.pushConstants.length());
         }
      }

      if (renderPass.anyUniformDirty) {
         renderPass.anyUniformDirty = false;

         label129:
         for(int i = 0; i < renderPass.dirtyUniforms.size(); ++i) {
            if (renderPass.dirtyUniforms.getBoolean(i)) {
               renderPass.dirtyUniforms.set(i, false);
               Uniform dirtyUniform = glProgram.getUniform(i);
               if (dirtyUniform != null) {
                  Objects.requireNonNull(dirtyUniform);
                  Uniform var5 = dirtyUniform;
                  byte var6 = 0;

                  while(true) {
                     //$FF: var6->value
                     //0->com/mojang/renderpearl/backend/opengl/Uniform$Ubo
                     //1->com/mojang/renderpearl/backend/opengl/Uniform$Utb
                     //2->com/mojang/renderpearl/backend/opengl/Uniform$Sampler
                     switch (var5.typeSwitch<invokedynamic>(var5, var6)) {
                        case 0:
                           Uniform.Ubo var7 = (Uniform.Ubo)var5;
                           Uniform.Ubo var39 = var7;

                           try {
                              var40 = var39.blockBinding();
                           } catch (Throwable var19) {
                              throw new MatchException(var19.toString(), var19);
                           }

                           int blockBinding = var40;
                           if (true) {
                              GpuBufferSlice var26 = (GpuBufferSlice)renderPass.uniforms.get(i);
                              GL33C.glBindBufferRange(35345, blockBinding, ((GlBuffer)var26.buffer()).handle(), var26.offset(), var26.length());
                              continue label129;
                           }

                           var6 = 1;
                           break;
                        case 1:
                           Uniform.Utb bufferView = (Uniform.Utb)var5;
                           Uniform.Utb var33 = bufferView;

                           try {
                              var34 = var33.samplerIndex();
                           } catch (Throwable var23) {
                              throw new MatchException(var23.toString(), var23);
                           }

                           int texture = var34;
                           if (true) {
                              var33 = bufferView;

                              try {
                                 var36 = var33.format();
                              } catch (Throwable var22) {
                                 throw new MatchException(var22.toString(), var22);
                              }

                              GpuFormat texture = var36;
                              var33 = bufferView;

                              try {
                                 var38 = var33.texture();
                              } catch (Throwable var21) {
                                 throw new MatchException(var21.toString(), var21);
                              }

                              int texture = var38;
                              if (true) {
                                 GlStateManager._activeTexture('\u84c0' + texture);
                                 GL33C.glBindTexture(35882, texture);
                                 GpuBufferSlice texture = (GpuBufferSlice)renderPass.uniforms.get(i);
                                 GL33C.glTexBuffer(35882, GlConst.toGlInternalId(texture), ((GlBuffer)texture.buffer()).handle());
                                 continue label129;
                              }
                           }

                           var6 = 2;
                           break;
                        case 2:
                           Uniform.Sampler bufferView = (Uniform.Sampler)var5;
                           Uniform.Sampler var10000 = bufferView;

                           try {
                              var32 = var10000.samplerIndex();
                           } catch (Throwable var20) {
                              throw new MatchException(var20.toString(), var20);
                           }

                           int samplerIndex = var32;
                           if (true) {
                              TextureViewAndSampler viewAndSampler = (TextureViewAndSampler)renderPass.uniforms.get(i);
                              if (viewAndSampler != null) {
                                 GlTextureView textureView = (GlTextureView)viewAndSampler.view();
                                 GlStateManager._activeTexture('\u84c0' + samplerIndex);
                                 GlTexture texture = textureView.texture();
                                 int target;
                                 if ((texture.usage() & 16) != 0) {
                                    target = 34067;
                                    GL33C.glBindTexture(34067, texture.id);
                                 } else {
                                    target = 3553;
                                    GlStateManager._bindTexture(texture.id);
                                 }

                                 GL33C.glBindSampler(samplerIndex, ((GlSampler)viewAndSampler.sampler()).getId());
                                 GlStateManager._texParameter(target, 33084, textureView.baseMipLevel());
                                 GlStateManager._texParameter(target, 33085, textureView.baseMipLevel() + textureView.mipLevels() - 1);
                              }
                              continue label129;
                           }

                           var6 = 3;
                           break;
                        default:
                           throw new MatchException((String)null, (Throwable)null);
                     }
                  }
               }
            }
         }
      }

      if (renderPass.scissorStateDirty) {
         renderPass.scissorStateDirty = false;
         if (renderPass.isScissorEnabled()) {
            GlStateManager._enableScissorTest();
            GlStateManager._scissorBox(renderPass.getScissorX(), renderPass.getScissorY(), renderPass.getScissorWidth(), renderPass.getScissorHeight());
         } else {
            GlStateManager._disableScissorTest();
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
