package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.GpuFence;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.CommandEncoderBackend;
import com.mojang.blaze3d.systems.GpuQueryPool;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderPassBackend;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL33C;
import org.slf4j.Logger;

class GlCommandEncoder implements CommandEncoderBackend {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final GlDevice device;
   private final int readFbo;
   private final int drawFbo;
   private @Nullable RenderPipeline lastPipeline;
   private @Nullable GlProgram lastProgram;

   protected GlCommandEncoder(final GlDevice device) {
      super();
      this.device = device;
      this.readFbo = device.directStateAccess().createFrameBufferObject();
      this.drawFbo = device.directStateAccess().createFrameBufferObject();
   }

   public void submit() {
   }

   public RenderPassBackend createRenderPass(final Supplier<String> label, final GpuTextureView colorTexture, final OptionalInt clearColor) {
      return this.createRenderPass(label, colorTexture, clearColor, (GpuTextureView)null, OptionalDouble.empty());
   }

   public RenderPassBackend createRenderPass(final Supplier<String> label, final GpuTextureView colorTexture, final OptionalInt clearColor, final @Nullable GpuTextureView depthTexture, final OptionalDouble clearDepth) {
      this.device.debugLabels().pushDebugGroup(label);
      int fbo = ((GlTextureView)colorTexture).getFbo(this.device.directStateAccess(), depthTexture == null ? null : depthTexture.texture());
      GlStateManager._glBindFramebuffer(36160, fbo);
      int clearMask = 0;
      if (clearColor.isPresent()) {
         int argb = clearColor.getAsInt();
         GL11.glClearColor(ARGB.redFloat(argb), ARGB.greenFloat(argb), ARGB.blueFloat(argb), ARGB.alphaFloat(argb));
         clearMask |= 16384;
      }

      if (depthTexture != null && clearDepth.isPresent()) {
         GL11.glClearDepth(clearDepth.getAsDouble());
         clearMask |= 256;
      }

      if (clearMask != 0) {
         GlStateManager._disableScissorTest();
         GlStateManager._depthMask(true);
         GlStateManager._colorMask(15);
         GlStateManager._clear(clearMask);
      }

      GlStateManager._viewport(0, 0, colorTexture.getWidth(0), colorTexture.getHeight(0));
      this.lastPipeline = null;
      return new GlRenderPass(this, this.device, depthTexture != null);
   }

   public void clearColorTexture(final GpuTexture colorTexture, final int clearColor) {
      this.device.directStateAccess().bindFrameBufferTextures(this.drawFbo, ((GlTexture)colorTexture).id, 0, 0, 36160);
      GL11.glClearColor(ARGB.redFloat(clearColor), ARGB.greenFloat(clearColor), ARGB.blueFloat(clearColor), ARGB.alphaFloat(clearColor));
      GlStateManager._disableScissorTest();
      GlStateManager._colorMask(15);
      GlStateManager._clear(16384);
      GlStateManager._glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
      GlStateManager._glBindFramebuffer(36160, 0);
   }

   public void clearColorAndDepthTextures(final GpuTexture colorTexture, final int clearColor, final GpuTexture depthTexture, final double clearDepth) {
      int fbo = ((GlTexture)colorTexture).getFbo(this.device.directStateAccess(), depthTexture);
      GlStateManager._glBindFramebuffer(36160, fbo);
      GlStateManager._disableScissorTest();
      GL11.glClearDepth(clearDepth);
      GL11.glClearColor(ARGB.redFloat(clearColor), ARGB.greenFloat(clearColor), ARGB.blueFloat(clearColor), ARGB.alphaFloat(clearColor));
      GlStateManager._depthMask(true);
      GlStateManager._colorMask(15);
      GlStateManager._clear(16640);
      GlStateManager._glBindFramebuffer(36160, 0);
   }

   public void clearColorAndDepthTextures(final GpuTexture colorTexture, final int clearColor, final GpuTexture depthTexture, final double clearDepth, final int regionX, final int regionY, final int regionWidth, final int regionHeight) {
      int fbo = ((GlTexture)colorTexture).getFbo(this.device.directStateAccess(), depthTexture);
      GlStateManager._glBindFramebuffer(36160, fbo);
      GlStateManager._scissorBox(regionX, regionY, regionWidth, regionHeight);
      GlStateManager._enableScissorTest();
      GL11.glClearDepth(clearDepth);
      GL11.glClearColor(ARGB.redFloat(clearColor), ARGB.greenFloat(clearColor), ARGB.blueFloat(clearColor), ARGB.alphaFloat(clearColor));
      GlStateManager._depthMask(true);
      GlStateManager._colorMask(15);
      GlStateManager._clear(16640);
      GlStateManager._glBindFramebuffer(36160, 0);
   }

   public void clearDepthTexture(final GpuTexture depthTexture, final double clearDepth) {
      this.device.directStateAccess().bindFrameBufferTextures(this.drawFbo, 0, ((GlTexture)depthTexture).id, 0, 36160);
      GL11.glDrawBuffer(0);
      GL11.glClearDepth(clearDepth);
      GlStateManager._depthMask(true);
      GlStateManager._disableScissorTest();
      GlStateManager._clear(256);
      GL11.glDrawBuffer(36064);
      GlStateManager._glFramebufferTexture2D(36160, 36096, 3553, 0, 0);
      GlStateManager._glBindFramebuffer(36160, 0);
   }

   public void writeToBuffer(final GpuBufferSlice slice, final ByteBuffer data) {
      GlBuffer buffer = (GlBuffer)slice.buffer();
      if (buffer.closed) {
         throw new IllegalStateException("Buffer already closed");
      } else if ((buffer.usage() & 8) == 0) {
         throw new IllegalStateException("Buffer needs USAGE_COPY_DST to be a destination for a copy");
      } else {
         int length = data.remaining();
         if ((long)length > slice.length()) {
            throw new IllegalArgumentException("Cannot write more data than the slice allows (attempting to write " + length + " bytes into a slice of length " + slice.length() + ")");
         } else if (slice.length() + slice.offset() > buffer.size()) {
            throw new IllegalArgumentException("Cannot write more data than this buffer can hold (attempting to write " + length + " bytes at offset " + slice.offset() + " to " + buffer.size() + " size buffer)");
         } else {
            this.device.directStateAccess().bufferSubData(buffer.handle, slice.offset(), data, buffer.usage());
         }
      }
   }

   public GpuBuffer.MappedView mapBuffer(final GpuBufferSlice slice, final boolean read, final boolean write) {
      GlBuffer buffer = (GlBuffer)slice.buffer();
      int flags = 0;
      if (read) {
         flags |= 1;
      }

      if (write) {
         flags |= 34;
      }

      return this.device.getBufferStorage().mapBuffer(this.device.directStateAccess(), buffer, slice.offset(), slice.length(), flags);
   }

   public void copyToBuffer(final GpuBufferSlice source, final GpuBufferSlice target) {
      GlBuffer sourceBuffer = (GlBuffer)source.buffer();
      GlBuffer targetBuffer = (GlBuffer)target.buffer();
      this.device.directStateAccess().copyBufferSubData(sourceBuffer.handle, targetBuffer.handle, source.offset(), target.offset(), source.length());
   }

   public void writeToTexture(final GpuTexture destination, final NativeImage source, final int mipLevel, final int depthOrLayer, final int destX, final int destY, final int width, final int height, final int sourceX, final int sourceY) {
      int target;
      if ((destination.usage() & 16) != 0) {
         target = GlConst.CUBEMAP_TARGETS[depthOrLayer % 6];
         GL11.glBindTexture(34067, ((GlTexture)destination).id);
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
         GL11.glBindTexture(34067, ((GlTexture)destination).id);
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
      GlStateManager.clearGlErrors();
      this.device.directStateAccess().bindFrameBufferTextures(this.readFbo, ((GlTexture)source).glId(), 0, mipLevel, 36008);
      GlStateManager._glBindBuffer(35051, ((GlBuffer)destination).handle);
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
      return new GlFence();
   }

   protected <T> void executeDrawMultiple(final GlRenderPass renderPass, final Collection<RenderPass.Draw<T>> draws, final @Nullable GpuBuffer defaultIndexBuffer, VertexFormat.@Nullable IndexType defaultIndexType, final Collection<String> dynamicUniforms, final T uniformArgument) {
      if (this.trySetup(renderPass, dynamicUniforms)) {
         if (defaultIndexType == null) {
            defaultIndexType = VertexFormat.IndexType.SHORT;
         }

         for(RenderPass.Draw<T> draw : draws) {
            VertexFormat.IndexType indexType = draw.indexType() == null ? defaultIndexType : draw.indexType();
            renderPass.setIndexBuffer(draw.indexBuffer() == null ? defaultIndexBuffer : draw.indexBuffer(), indexType);
            renderPass.setVertexBuffer(draw.slot(), draw.vertexBuffer());
            if (GlRenderPass.VALIDATION) {
               if (renderPass.indexBuffer == null) {
                  throw new IllegalStateException("Missing index buffer");
               }

               if (renderPass.indexBuffer.isClosed()) {
                  throw new IllegalStateException("Index buffer has been closed!");
               }

               if (renderPass.vertexBuffers[0] == null) {
                  throw new IllegalStateException("Missing vertex buffer at slot 0");
               }

               if (renderPass.vertexBuffers[0].isClosed()) {
                  throw new IllegalStateException("Vertex buffer at slot 0 has been closed!");
               }
            }

            BiConsumer<T, RenderPass.UniformUploader> uniformUploaderConsumer = draw.uniformUploaderConsumer();
            if (uniformUploaderConsumer != null) {
               uniformUploaderConsumer.accept(uniformArgument, (RenderPass.UniformUploader)(name, buffer) -> {
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
                        GL32.glBindBufferRange(35345, patt2$temp, ((GlBuffer)buffer.buffer()).handle, buffer.offset(), buffer.length());
                     }
                  }

               });
            }

            this.drawFromBuffers(renderPass, draw.baseVertex(), draw.firstIndex(), draw.indexCount(), indexType, renderPass.pipeline, 1);
         }

      }
   }

   protected void executeDraw(final GlRenderPass renderPass, final int baseVertex, final int firstIndex, final int drawCount, final VertexFormat.@Nullable IndexType indexType, final int instanceCount) {
      if (this.trySetup(renderPass, Collections.emptyList())) {
         if (GlRenderPass.VALIDATION) {
            if (indexType != null) {
               if (renderPass.indexBuffer == null) {
                  throw new IllegalStateException("Missing index buffer");
               }

               if (renderPass.indexBuffer.isClosed()) {
                  throw new IllegalStateException("Index buffer has been closed!");
               }

               if ((renderPass.indexBuffer.usage() & 64) == 0) {
                  throw new IllegalStateException("Index buffer must have GpuBuffer.USAGE_INDEX!");
               }
            }

            GlRenderPipeline pipeline = renderPass.pipeline;
            if (renderPass.vertexBuffers[0] == null && pipeline != null && !pipeline.info().getVertexFormat().getElements().isEmpty()) {
               throw new IllegalStateException("Vertex format contains elements but vertex buffer at slot 0 is null");
            }

            if (renderPass.vertexBuffers[0] != null && renderPass.vertexBuffers[0].isClosed()) {
               throw new IllegalStateException("Vertex buffer at slot 0 has been closed!");
            }

            if (renderPass.vertexBuffers[0] != null && (renderPass.vertexBuffers[0].usage() & 32) == 0) {
               throw new IllegalStateException("Vertex buffer must have GpuBuffer.USAGE_VERTEX!");
            }
         }

         this.drawFromBuffers(renderPass, baseVertex, firstIndex, drawCount, indexType, renderPass.pipeline, instanceCount);
      }
   }

   private void drawFromBuffers(final GlRenderPass renderPass, final int baseVertex, final int firstIndex, final int drawCount, final VertexFormat.@Nullable IndexType indexType, final GlRenderPipeline pipeline, final int instanceCount) {
      this.device.vertexArrayCache().bindVertexArray(pipeline.info().getVertexFormat(), (GlBuffer)renderPass.vertexBuffers[0]);
      if (indexType != null) {
         GlStateManager._glBindBuffer(34963, ((GlBuffer)renderPass.indexBuffer).handle);
         if (instanceCount > 1) {
            if (baseVertex > 0) {
               GL32.glDrawElementsInstancedBaseVertex(GlConst.toGl(pipeline.info().getVertexFormatMode()), drawCount, GlConst.toGl(indexType), (long)firstIndex * (long)indexType.bytes, instanceCount, baseVertex);
            } else {
               GL31.glDrawElementsInstanced(GlConst.toGl(pipeline.info().getVertexFormatMode()), drawCount, GlConst.toGl(indexType), (long)firstIndex * (long)indexType.bytes, instanceCount);
            }
         } else if (baseVertex > 0) {
            GL32.glDrawElementsBaseVertex(GlConst.toGl(pipeline.info().getVertexFormatMode()), drawCount, GlConst.toGl(indexType), (long)firstIndex * (long)indexType.bytes, baseVertex);
         } else {
            GlStateManager._drawElements(GlConst.toGl(pipeline.info().getVertexFormatMode()), drawCount, GlConst.toGl(indexType), (long)firstIndex * (long)indexType.bytes);
         }
      } else if (instanceCount > 1) {
         GL31.glDrawArraysInstanced(GlConst.toGl(pipeline.info().getVertexFormatMode()), baseVertex, drawCount, instanceCount);
      } else {
         GlStateManager._drawArrays(GlConst.toGl(pipeline.info().getVertexFormatMode()), baseVertex, drawCount);
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

         for(RenderPipeline.UniformDescription uniform : renderPass.pipeline.info().getUniforms()) {
            GpuBufferSlice value = (GpuBufferSlice)renderPass.uniforms.get(uniform.name());
            if (!dynamicUniforms.contains(uniform.name())) {
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

      label203:
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
                     var63 = var10000.blockBinding();
                  } catch (Throwable var32) {
                     throw new MatchException(var32.toString(), var32);
                  }

                  int var41 = var63;
                  if (true) {
                     int blockBinding = var41;
                     if (isDirty) {
                        GpuBufferSlice bufferView = (GpuBufferSlice)renderPass.uniforms.get(name);
                        GL32.glBindBufferRange(35345, blockBinding, ((GlBuffer)bufferView.buffer()).handle, bufferView.offset(), bufferView.length());
                     }
                     continue label203;
                  }

                  var11 = 1;
                  break;
               case 1:
                  Uniform.Utb bufferView = (Uniform.Utb)var10;
                  Uniform.Utb var54 = bufferView;

                  try {
                     var55 = var54.location();
                  } catch (Throwable var31) {
                     throw new MatchException(var31.toString(), var31);
                  }

                  int texture = var55;
                  if (true) {
                     int location = texture;
                     var54 = bufferView;

                     try {
                        var57 = var54.samplerIndex();
                     } catch (Throwable var30) {
                        throw new MatchException(var30.toString(), var30);
                     }

                     texture = var57;
                     if (true) {
                        int samplerIndex = texture;
                        var54 = bufferView;

                        try {
                           var59 = var54.format();
                        } catch (Throwable var29) {
                           throw new MatchException(var29.toString(), var29);
                        }

                        GpuFormat texture = var59;
                        GpuFormat format = texture;
                        var54 = bufferView;

                        try {
                           var61 = var54.texture();
                        } catch (Throwable var28) {
                           throw new MatchException(var28.toString(), var28);
                        }

                        int texture = var61;
                        if (true) {
                           if (differentProgram || isDirty) {
                              GlStateManager._glUniform1i(location, samplerIndex);
                           }

                           GlStateManager._activeTexture('\u84c0' + samplerIndex);
                           GL11C.glBindTexture(35882, texture);
                           if (isDirty) {
                              GpuBufferSlice bufferView = (GpuBufferSlice)renderPass.uniforms.get(name);
                              GL31.glTexBuffer(35882, GlConst.toGlInternalId(format), ((GlBuffer)bufferView.buffer()).handle);
                           }
                           continue label203;
                        }
                     }
                  }

                  var11 = 2;
                  break;
               case 2:
                  Uniform.Sampler bufferView = (Uniform.Sampler)var10;
                  Uniform.Sampler var50 = bufferView;

                  try {
                     var51 = var50.location();
                  } catch (Throwable var27) {
                     throw new MatchException(var27.toString(), var27);
                  }

                  int var22 = var51;
                  if (true) {
                     int location = var22;
                     var50 = bufferView;

                     try {
                        var53 = var50.samplerIndex();
                     } catch (Throwable var26) {
                        throw new MatchException(var26.toString(), var26);
                     }

                     var22 = var53;
                     if (true) {
                        int samplerIndex = var22;
                        GlRenderPass.TextureViewAndSampler viewAndSampler = (GlRenderPass.TextureViewAndSampler)renderPass.samplers.get(name);
                        if (viewAndSampler == null) {
                           continue label203;
                        }

                        GlTextureView textureView = viewAndSampler.view();
                        if (differentProgram || isDirty) {
                           GlStateManager._glUniform1i(location, samplerIndex);
                        }

                        GlStateManager._activeTexture('\u84c0' + samplerIndex);
                        GlTexture texture = textureView.texture();
                        int target;
                        if ((texture.usage() & 16) != 0) {
                           target = 34067;
                           GL11.glBindTexture(34067, texture.id);
                        } else {
                           target = 3553;
                           GlStateManager._bindTexture(texture.id);
                        }

                        GL33C.glBindSampler(samplerIndex, viewAndSampler.sampler().getId());
                        GlStateManager._texParameter(target, 33084, textureView.baseMipLevel());
                        GlStateManager._texParameter(target, 33085, textureView.baseMipLevel() + textureView.mipLevels() - 1);
                        continue label203;
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

         if (pipeline.getColorTargetState().blendFunction().isPresent()) {
            GlStateManager._enableBlend();
            BlendFunction blendFunction = (BlendFunction)pipeline.getColorTargetState().blendFunction().get();
            GlStateManager._blendFuncSeparate(GlConst.toGl(blendFunction.color().sourceFactor()), GlConst.toGl(blendFunction.color().destFactor()), GlConst.toGl(blendFunction.alpha().sourceFactor()), GlConst.toGl(blendFunction.alpha().destFactor()));
            GlStateManager._blendEquationSeparate(GlConst.toGl(blendFunction.color().op()), GlConst.toGl(blendFunction.alpha().op()));
         } else {
            GlStateManager._disableBlend();
         }

         GlStateManager._polygonMode(1032, GlConst.toGl(pipeline.getPolygonMode()));
         GlStateManager._colorMask(pipeline.getColorTargetState().writeMask());
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
