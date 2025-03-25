package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.buffers.BufferType;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntList;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Collection;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ARGB;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;

public class GlCommandEncoder implements CommandEncoder {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final GlDevice device;
   private final int readFbo;
   private final int drawFbo;
   @Nullable
   private RenderPipeline lastPipeline;
   private boolean inRenderPass;
   @Nullable
   private GlProgram lastProgram;

   protected GlCommandEncoder(GlDevice var1) {
      super();
      this.device = var1;
      this.readFbo = var1.directStateAccess().createFrameBufferObject();
      this.drawFbo = var1.directStateAccess().createFrameBufferObject();
   }

   public RenderPass createRenderPass(GpuTexture var1, OptionalInt var2) {
      return this.createRenderPass(var1, var2, (GpuTexture)null, OptionalDouble.empty());
   }

   public RenderPass createRenderPass(GpuTexture var1, OptionalInt var2, @Nullable GpuTexture var3, OptionalDouble var4) {
      if (this.inRenderPass) {
         throw new IllegalStateException("Close the existing render pass before creating a new one!");
      } else {
         if (var4.isPresent() && var3 == null) {
            LOGGER.warn("Depth clear value was provided but no depth texture is being used");
         }

         if (var1.isClosed()) {
            throw new IllegalStateException("Color texture is closed");
         } else if (var3 != null && var3.isClosed()) {
            throw new IllegalStateException("Depth texture is closed");
         } else {
            this.inRenderPass = true;
            int var5 = ((GlTexture)var1).getFbo(this.device.directStateAccess(), var3);
            GlStateManager._glBindFramebuffer(36160, var5);
            int var6 = 0;
            if (var2.isPresent()) {
               int var7 = var2.getAsInt();
               GL11.glClearColor(ARGB.redFloat(var7), ARGB.greenFloat(var7), ARGB.blueFloat(var7), ARGB.alphaFloat(var7));
               var6 |= 16384;
            }

            if (var3 != null && var4.isPresent()) {
               GL11.glClearDepth(var4.getAsDouble());
               var6 |= 256;
            }

            if (var6 != 0) {
               GlStateManager._disableScissorTest();
               GlStateManager._depthMask(true);
               GlStateManager._colorMask(true, true, true, true);
               GlStateManager._clear(var6);
            }

            GlStateManager._viewport(0, 0, var1.getWidth(0), var1.getHeight(0));
            this.lastPipeline = null;
            return new GlRenderPass(this, var3 != null);
         }
      }
   }

   public void clearColorTexture(GpuTexture var1, int var2) {
      if (this.inRenderPass) {
         throw new IllegalStateException("Close the existing render pass before creating a new one!");
      } else if (!var1.getFormat().hasColorAspect()) {
         throw new IllegalStateException("Trying to clear a non-color texture as color");
      } else if (var1.isClosed()) {
         throw new IllegalStateException("Color texture is closed");
      } else {
         this.device.directStateAccess().bindFrameBufferTextures(this.drawFbo, ((GlTexture)var1).id, 0, 0, 36160);
         GL11.glClearColor(ARGB.redFloat(var2), ARGB.greenFloat(var2), ARGB.blueFloat(var2), ARGB.alphaFloat(var2));
         GlStateManager._disableScissorTest();
         GlStateManager._colorMask(true, true, true, true);
         GlStateManager._clear(16384);
         GlStateManager._glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
         GlStateManager._glBindFramebuffer(36160, 0);
      }
   }

   public void clearColorAndDepthTextures(GpuTexture var1, int var2, GpuTexture var3, double var4) {
      if (this.inRenderPass) {
         throw new IllegalStateException("Close the existing render pass before creating a new one!");
      } else if (!var1.getFormat().hasColorAspect()) {
         throw new IllegalStateException("Trying to clear a non-color texture as color");
      } else if (!var3.getFormat().hasDepthAspect()) {
         throw new IllegalStateException("Trying to clear a non-depth texture as depth");
      } else if (var1.isClosed()) {
         throw new IllegalStateException("Color texture is closed");
      } else if (var3.isClosed()) {
         throw new IllegalStateException("Depth texture is closed");
      } else {
         int var6 = ((GlTexture)var1).getFbo(this.device.directStateAccess(), var3);
         GlStateManager._glBindFramebuffer(36160, var6);
         GlStateManager._disableScissorTest();
         GL11.glClearDepth(var4);
         GL11.glClearColor(ARGB.redFloat(var2), ARGB.greenFloat(var2), ARGB.blueFloat(var2), ARGB.alphaFloat(var2));
         GlStateManager._depthMask(true);
         GlStateManager._colorMask(true, true, true, true);
         GlStateManager._clear(16640);
         GlStateManager._glBindFramebuffer(36160, 0);
      }
   }

   public void clearDepthTexture(GpuTexture var1, double var2) {
      if (this.inRenderPass) {
         throw new IllegalStateException("Close the existing render pass before creating a new one!");
      } else if (!var1.getFormat().hasDepthAspect()) {
         throw new IllegalStateException("Trying to clear a non-depth texture as depth");
      } else if (var1.isClosed()) {
         throw new IllegalStateException("Depth texture is closed");
      } else {
         this.device.directStateAccess().bindFrameBufferTextures(this.drawFbo, 0, ((GlTexture)var1).id, 0, 36160);
         GL11.glDrawBuffer(0);
         GL11.glClearDepth(var2);
         GlStateManager._depthMask(true);
         GlStateManager._disableScissorTest();
         GlStateManager._clear(256);
         GL11.glDrawBuffer(36064);
         GlStateManager._glFramebufferTexture2D(36160, 36096, 3553, 0, 0);
         GlStateManager._glBindFramebuffer(36160, 0);
      }
   }

   public void writeToBuffer(GpuBuffer var1, ByteBuffer var2, int var3) {
      if (this.inRenderPass) {
         throw new IllegalStateException("Close the existing render pass before performing additional commands");
      } else {
         GlBuffer var4 = (GlBuffer)var1;
         if (var4.closed) {
            throw new IllegalStateException("Buffer already closed");
         } else if (!var4.usage().isWritable()) {
            throw new IllegalStateException("Buffer is not writable");
         } else {
            int var5 = var2.remaining();
            if (var5 + var3 > var4.size) {
               throw new IllegalArgumentException("Cannot write more data than this buffer can hold (attempting to write " + var5 + " bytes at offset " + var3 + " to " + var4.size + " size buffer)");
            } else {
               GlStateManager._glBindBuffer(GlConst.toGl(var4.type()), var4.handle);
               if (var4.initialized) {
                  GlStateManager._glBufferSubData(GlConst.toGl(var4.type()), var3, var2);
               } else if (var3 == 0 && var5 == var4.size) {
                  GlStateManager._glBufferData(GlConst.toGl(var4.type()), var2, GlConst.toGl(var4.usage()));
                  GlBuffer.MEMORY_POOl.malloc((long)var4.handle, var4.size);
                  var4.initialized = true;
                  this.device.debugLabels().applyLabel(var4);
               } else {
                  GlStateManager._glBufferData(GlConst.toGl(var4.type()), (long)var4.size, GlConst.toGl(var4.usage()));
                  GlStateManager._glBufferSubData(GlConst.toGl(var4.type()), var3, var2);
                  GlBuffer.MEMORY_POOl.malloc((long)var4.handle, var4.size);
                  var4.initialized = true;
                  this.device.debugLabels().applyLabel(var4);
               }

            }
         }
      }
   }

   public GpuBuffer.ReadView readBuffer(GpuBuffer var1) {
      return this.readBuffer(var1, 0, var1.size());
   }

   public GpuBuffer.ReadView readBuffer(GpuBuffer var1, int var2, int var3) {
      if (this.inRenderPass) {
         throw new IllegalStateException("Close the existing render pass before performing additional commands");
      } else {
         GlBuffer var4 = (GlBuffer)var1;
         if (var4.closed) {
            throw new IllegalStateException("Buffer already closed");
         } else if (!var4.usage().isReadable()) {
            throw new IllegalStateException("Buffer is not readable");
         } else if (var2 + var3 > var4.size) {
            throw new IllegalArgumentException("Cannot read more data than this buffer can hold (attempting to read " + var3 + " bytes at offset " + var2 + " from " + var4.size + " size buffer)");
         } else {
            GlStateManager.clearGlErrors();
            GlStateManager._glBindBuffer(GlConst.toGl(var4.type()), var4.handle);
            ByteBuffer var5 = GlStateManager._glMapBufferRange(GlConst.toGl(var4.type()), var2, var3, 1);
            if (var5 == null) {
               throw new IllegalStateException("Can't read buffer, opengl error " + GlStateManager._getError());
            } else {
               return new GlBuffer.ReadView(GlConst.toGl(var4.type()), var5);
            }
         }
      }
   }

   public void writeToTexture(GpuTexture var1, NativeImage var2) {
      int var3 = var1.getWidth(0);
      int var4 = var1.getHeight(0);
      if (var2.getWidth() == var3 && var2.getHeight() == var4) {
         if (var1.isClosed()) {
            throw new IllegalStateException("Destination texture is closed");
         } else {
            this.writeToTexture(var1, var2, 0, 0, 0, var3, var4, 0, 0);
         }
      } else {
         throw new IllegalArgumentException("Cannot replace texture of size " + var3 + "x" + var4 + " with image of size " + var2.getWidth() + "x" + var2.getHeight());
      }
   }

   public void writeToTexture(GpuTexture var1, NativeImage var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      if (this.inRenderPass) {
         throw new IllegalStateException("Close the existing render pass before performing additional commands");
      } else if (var3 >= 0 && var3 < var1.getMipLevels()) {
         if (var8 + var6 <= var2.getWidth() && var9 + var7 <= var2.getHeight()) {
            if (var4 + var6 <= var1.getWidth(var3) && var5 + var7 <= var1.getHeight(var3)) {
               if (var1.isClosed()) {
                  throw new IllegalStateException("Destination texture is closed");
               } else {
                  GlStateManager._bindTexture(((GlTexture)var1).id);
                  GlStateManager._pixelStore(3314, var2.getWidth());
                  GlStateManager._pixelStore(3316, var8);
                  GlStateManager._pixelStore(3315, var9);
                  GlStateManager._pixelStore(3317, var2.format().components());
                  GlStateManager._texSubImage2D(3553, var3, var4, var5, var6, var7, GlConst.toGl(var2.format()), 5121, var2.getPointer());
               }
            } else {
               throw new IllegalArgumentException("Dest texture (" + var6 + "x" + var7 + ") is not large enough to write a rectangle of " + var6 + "x" + var7 + " at " + var4 + "x" + var5 + " (at mip level " + var3 + ")");
            }
         } else {
            int var10002 = var2.getWidth();
            throw new IllegalArgumentException("Copy source (" + var10002 + "x" + var2.getHeight() + ") is not large enough to read a rectangle of " + var6 + "x" + var7 + " from " + var8 + "x" + var9);
         }
      } else {
         throw new IllegalArgumentException("Invalid mipLevel " + var3 + ", must be >= 0 and < " + var1.getMipLevels());
      }
   }

   public void writeToTexture(GpuTexture var1, IntBuffer var2, NativeImage.Format var3, int var4, int var5, int var6, int var7, int var8) {
      if (this.inRenderPass) {
         throw new IllegalStateException("Close the existing render pass before performing additional commands");
      } else if (var4 >= 0 && var4 < var1.getMipLevels()) {
         if (var7 * var8 > var2.remaining()) {
            throw new IllegalArgumentException("Copy would overrun the source buffer (remaining length of " + var2.remaining() + ", but copy is " + var7 + "x" + var8 + ")");
         } else if (var5 + var7 <= var1.getWidth(var4) && var6 + var8 <= var1.getHeight(var4)) {
            if (var1.isClosed()) {
               throw new IllegalStateException("Destination texture is closed");
            } else {
               GlStateManager._bindTexture(((GlTexture)var1).id);
               GlStateManager._pixelStore(3314, var7);
               GlStateManager._pixelStore(3316, 0);
               GlStateManager._pixelStore(3315, 0);
               GlStateManager._pixelStore(3317, var3.components());
               GlStateManager._texSubImage2D(3553, var4, var5, var6, var7, var8, GlConst.toGl(var3), 5121, var2);
            }
         } else {
            throw new IllegalArgumentException("Dest texture (" + var1.getWidth(var4) + "x" + var1.getHeight(var4) + ") is not large enough to write a rectangle of " + var7 + "x" + var8 + " at " + var5 + "x" + var6);
         }
      } else {
         throw new IllegalArgumentException("Invalid mipLevel, must be >= 0 and < " + var1.getMipLevels());
      }
   }

   public void copyTextureToBuffer(GpuTexture var1, GpuBuffer var2, int var3, Runnable var4, int var5) {
      if (this.inRenderPass) {
         throw new IllegalStateException("Close the existing render pass before performing additional commands");
      } else {
         this.copyTextureToBuffer(var1, var2, var3, var4, var5, 0, 0, var1.getWidth(var5), var1.getHeight(var5));
      }
   }

   public void copyTextureToBuffer(GpuTexture var1, GpuBuffer var2, int var3, Runnable var4, int var5, int var6, int var7, int var8, int var9) {
      if (this.inRenderPass) {
         throw new IllegalStateException("Close the existing render pass before performing additional commands");
      } else if (var5 >= 0 && var5 < var1.getMipLevels()) {
         if (var1.getWidth(var5) * var1.getHeight(var5) * var1.getFormat().pixelSize() + var3 > var2.size()) {
            int var11 = var2.size();
            throw new IllegalArgumentException("Buffer of size " + var11 + " is not large enough to hold " + var8 + "x" + var9 + " pixels (" + var1.getFormat().pixelSize() + " bytes each) starting from offset " + var3);
         } else if (var2.type() != BufferType.PIXEL_PACK) {
            throw new IllegalArgumentException("Buffer of type " + String.valueOf(var2.type()) + " cannot be used to retrieve a texture");
         } else if (var6 + var8 <= var1.getWidth(var5) && var7 + var9 <= var1.getHeight(var5)) {
            if (var1.isClosed()) {
               throw new IllegalStateException("Source texture is closed");
            } else if (var2.isClosed()) {
               throw new IllegalStateException("Destination buffer is closed");
            } else {
               GlStateManager.clearGlErrors();
               this.device.directStateAccess().bindFrameBufferTextures(this.readFbo, ((GlTexture)var1).glId(), 0, var5, 36008);
               GlStateManager._glBindBuffer(GlConst.toGl(var2.type()), ((GlBuffer)var2).handle);
               GlStateManager._pixelStore(3330, var8);
               GlStateManager._readPixels(var6, var7, var8, var9, GlConst.toGlExternalId(var1.getFormat()), GlConst.toGlType(var1.getFormat()), (long)var3);
               RenderSystem.queueFencedTask(var4);
               GlStateManager._glFramebufferTexture2D(36008, 36064, 3553, 0, var5);
               GlStateManager._glBindFramebuffer(36008, 0);
               GlStateManager._glBindBuffer(GlConst.toGl(var2.type()), 0);
               int var10 = GlStateManager._getError();
               if (var10 != 0) {
                  String var10002 = var1.getLabel();
                  throw new IllegalStateException("Couldn't perform copyTobuffer for texture " + var10002 + ": GL error " + var10);
               }
            }
         } else {
            throw new IllegalArgumentException("Copy source texture (" + var1.getWidth(var5) + "x" + var1.getHeight(var5) + ") is not large enough to read a rectangle of " + var8 + "x" + var9 + " from " + var6 + "," + var7);
         }
      } else {
         throw new IllegalArgumentException("Invalid mipLevel " + var5 + ", must be >= 0 and < " + var1.getMipLevels());
      }
   }

   public void copyTextureToTexture(GpuTexture var1, GpuTexture var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      if (this.inRenderPass) {
         throw new IllegalStateException("Close the existing render pass before performing additional commands");
      } else if (var3 >= 0 && var3 < var1.getMipLevels() && var3 < var2.getMipLevels()) {
         if (var4 + var8 <= var2.getWidth(var3) && var5 + var9 <= var2.getHeight(var3)) {
            if (var6 + var8 <= var1.getWidth(var3) && var7 + var9 <= var1.getHeight(var3)) {
               if (var1.isClosed()) {
                  throw new IllegalStateException("Source texture is closed");
               } else if (var2.isClosed()) {
                  throw new IllegalStateException("Destination texture is closed");
               } else {
                  GlStateManager.clearGlErrors();
                  GlStateManager._disableScissorTest();
                  boolean var10 = var1.getFormat().hasDepthAspect();
                  int var11 = ((GlTexture)var1).glId();
                  int var12 = ((GlTexture)var2).glId();
                  this.device.directStateAccess().bindFrameBufferTextures(this.readFbo, var10 ? 0 : var11, var10 ? var11 : 0, 0, 0);
                  this.device.directStateAccess().bindFrameBufferTextures(this.drawFbo, var10 ? 0 : var12, var10 ? var12 : 0, 0, 0);
                  this.device.directStateAccess().blitFrameBuffers(this.readFbo, this.drawFbo, var6, var7, var8, var9, var4, var5, var8, var9, var10 ? 256 : 16384, 9728);
                  int var13 = GlStateManager._getError();
                  if (var13 != 0) {
                     String var10002 = var1.getLabel();
                     throw new IllegalStateException("Couldn't perform copyToTexture for texture " + var10002 + " to " + var2.getLabel() + ": GL error " + var13);
                  }
               }
            } else {
               throw new IllegalArgumentException("Source texture (" + var1.getWidth(var3) + "x" + var1.getHeight(var3) + ") is not large enough to read a rectangle of " + var8 + "x" + var9 + " at " + var6 + "x" + var7);
            }
         } else {
            throw new IllegalArgumentException("Dest texture (" + var2.getWidth(var3) + "x" + var2.getHeight(var3) + ") is not large enough to write a rectangle of " + var8 + "x" + var9 + " at " + var4 + "x" + var5);
         }
      } else {
         throw new IllegalArgumentException("Invalid mipLevel " + var3 + ", must be >= 0 and < " + var1.getMipLevels() + " and < " + var2.getMipLevels());
      }
   }

   public void presentTexture(GpuTexture var1) {
      if (this.inRenderPass) {
         throw new IllegalStateException("Close the existing render pass before performing additional commands");
      } else if (!var1.getFormat().hasColorAspect()) {
         throw new IllegalStateException("Cannot present a non-color texture!");
      } else {
         GlStateManager._disableScissorTest();
         GlStateManager._viewport(0, 0, var1.getWidth(0), var1.getHeight(0));
         GlStateManager._depthMask(true);
         GlStateManager._colorMask(true, true, true, true);
         this.device.directStateAccess().bindFrameBufferTextures(this.drawFbo, ((GlTexture)var1).glId(), 0, 0, 0);
         this.device.directStateAccess().blitFrameBuffers(this.drawFbo, 0, 0, 0, var1.getWidth(0), var1.getHeight(0), 0, 0, var1.getWidth(0), var1.getHeight(0), 16384, 9728);
      }
   }

   protected void executeDrawMultiple(GlRenderPass var1, Collection<RenderPass.Draw> var2, @Nullable GpuBuffer var3, @Nullable VertexFormat.IndexType var4) {
      if (this.trySetup(var1)) {
         if (var4 == null) {
            var4 = VertexFormat.IndexType.SHORT;
         }

         for(RenderPass.Draw var6 : var2) {
            VertexFormat.IndexType var7 = var6.indexType() == null ? var4 : var6.indexType();
            var1.setIndexBuffer(var6.indexBuffer() == null ? var3 : var6.indexBuffer(), var7);
            var1.setVertexBuffer(var6.slot(), var6.vertexBuffer());
            if (GlRenderPass.VALIDATION) {
               if (var1.indexBuffer == null) {
                  throw new IllegalStateException("Missing index buffer");
               }

               if (var1.indexBuffer.isClosed()) {
                  throw new IllegalStateException("Index buffer has been closed!");
               }

               if (var1.vertexBuffers[0] == null) {
                  throw new IllegalStateException("Missing vertex buffer at slot 0");
               }

               if (var1.vertexBuffers[0].isClosed()) {
                  throw new IllegalStateException("Vertex buffer at slot 0 has been closed!");
               }
            }

            Consumer var8 = var6.uniformUploaderConsumer();
            if (var8 != null) {
               var8.accept((RenderPass.UniformUploader)(var1x, var2x) -> {
                  Uniform var3 = var1.pipeline.program().getUniform(var1x);
                  if (var3 != null) {
                     var3.set(var2x);
                     var3.upload();
                  }

               });
            }

            this.drawFromBuffers(var1, var6.firstIndex(), var6.indexCount(), var7, var1.pipeline);
         }

      }
   }

   protected void executeDraw(GlRenderPass var1, int var2, int var3, @Nullable VertexFormat.IndexType var4) {
      if (this.trySetup(var1)) {
         if (GlRenderPass.VALIDATION) {
            if (var4 != null) {
               if (var1.indexBuffer == null) {
                  throw new IllegalStateException("Missing index buffer");
               }

               if (var1.indexBuffer.isClosed()) {
                  throw new IllegalStateException("Index buffer has been closed!");
               }
            }

            if (var1.vertexBuffers[0] == null) {
               throw new IllegalStateException("Missing vertex buffer at slot 0");
            }

            if (var1.vertexBuffers[0].isClosed()) {
               throw new IllegalStateException("Vertex buffer at slot 0 has been closed!");
            }
         }

         this.drawFromBuffers(var1, var2, var3, var4, var1.pipeline);
      }
   }

   private void drawFromBuffers(GlRenderPass var1, int var2, int var3, @Nullable VertexFormat.IndexType var4, GlRenderPipeline var5) {
      this.device.vertexArrayCache().bindVertexArray(var5.info().getVertexFormat(), (GlBuffer)var1.vertexBuffers[0]);
      if (var4 != null) {
         GlStateManager._glBindBuffer(34963, ((GlBuffer)var1.indexBuffer).handle);
         GlStateManager._drawElements(GlConst.toGl(var5.info().getVertexFormatMode()), var3, GlConst.toGl(var4), (long)var2 * (long)var4.bytes);
      } else {
         GlStateManager._drawArrays(GlConst.toGl(var5.info().getVertexFormatMode()), var2, var3);
      }

   }

   private boolean trySetup(GlRenderPass var1) {
      if (GlRenderPass.VALIDATION) {
         if (var1.pipeline == null) {
            throw new IllegalStateException("Can't draw without a render pipeline");
         }

         if (var1.pipeline.program() == GlProgram.INVALID_PROGRAM) {
            throw new IllegalStateException("Pipeline contains invalid shader program");
         }

         for(RenderPipeline.UniformDescription var3 : var1.pipeline.info().getUniforms()) {
            Object var4 = var1.uniforms.get(var3.name());
            if (var4 == null && !GlProgram.BUILT_IN_UNIFORMS.contains(var3.name())) {
               String var10002 = var3.name();
               throw new IllegalStateException("Missing uniform " + var10002 + " (should be " + String.valueOf(var3.type()) + ")");
            }
         }

         for(String var12 : var1.pipeline.program().getSamplers()) {
            if (!var1.samplers.containsKey(var12)) {
               throw new IllegalStateException("Missing sampler " + var12);
            }

            if (((GpuTexture)var1.samplers.get(var12)).isClosed()) {
               throw new IllegalStateException("Sampler " + var12 + " has been closed!");
            }
         }

         if (var1.pipeline.info().wantsDepthTexture() && !var1.hasDepthTexture()) {
            LOGGER.warn("Render pipeline {} wants a depth texture but none was provided - this is probably a bug", var1.pipeline.info().getLocation());
         }
      } else if (var1.pipeline == null || var1.pipeline.program() == GlProgram.INVALID_PROGRAM) {
         return false;
      }

      RenderPipeline var11 = var1.pipeline.info();
      GlProgram var13 = var1.pipeline.program();

      for(Uniform var5 : var13.getUniforms()) {
         if (var1.dirtyUniforms.contains(var5.getName())) {
            Object var6 = var1.uniforms.get(var5.getName());
            if (var6 instanceof int[]) {
               var13.safeGetUniform(var5.getName()).set((int[])var6);
            } else if (var6 instanceof float[]) {
               var13.safeGetUniform(var5.getName()).set((float[])var6);
            } else if (var6 != null) {
               String var21 = String.valueOf(var5.getType());
               throw new IllegalStateException("Unknown uniform type - expected " + var21 + ", found " + String.valueOf(var6));
            }
         }
      }

      var1.dirtyUniforms.clear();
      this.applyPipelineState(var11);
      boolean var15 = this.lastProgram != var13;
      if (var15) {
         GlStateManager._glUseProgram(var13.getProgramId());
         this.lastProgram = var13;
      }

      IntList var16 = var13.getSamplerLocations();

      for(int var17 = 0; var17 < var13.getSamplers().size(); ++var17) {
         String var7 = (String)var13.getSamplers().get(var17);
         GlTexture var8 = (GlTexture)var1.samplers.get(var7);
         if (var8 != null) {
            if (var15 || var1.dirtySamplers.contains(var7)) {
               int var9 = var16.getInt(var17);
               Uniform.uploadInteger(var9, var17);
               GlStateManager._activeTexture('\u84c0' + var17);
            }

            GlStateManager._bindTexture(var8.glId());
            var8.flushModeChanges();
         }
      }

      Window var18 = Minecraft.getInstance() == null ? null : Minecraft.getInstance().getWindow();
      var13.setDefaultUniforms(var11.getVertexFormatMode(), RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), var18 == null ? 0.0F : (float)var18.getWidth(), var18 == null ? 0.0F : (float)var18.getHeight());

      for(Uniform var20 : var13.getUniforms()) {
         var20.upload();
      }

      if (var1.scissorState.isEnabled()) {
         GlStateManager._enableScissorTest();
         GlStateManager._scissorBox(var1.scissorState.getX(), var1.scissorState.getY(), var1.scissorState.getWidth(), var1.scissorState.getHeight());
      } else {
         GlStateManager._disableScissorTest();
      }

      return true;
   }

   private void applyPipelineState(RenderPipeline var1) {
      if (this.lastPipeline != var1) {
         this.lastPipeline = var1;
         if (var1.getDepthTestFunction() != DepthTestFunction.NO_DEPTH_TEST) {
            GlStateManager._enableDepthTest();
            GlStateManager._depthFunc(GlConst.toGl(var1.getDepthTestFunction()));
         } else {
            GlStateManager._disableDepthTest();
         }

         if (var1.isCull()) {
            GlStateManager._enableCull();
         } else {
            GlStateManager._disableCull();
         }

         if (var1.getBlendFunction().isPresent()) {
            GlStateManager._enableBlend();
            BlendFunction var2 = (BlendFunction)var1.getBlendFunction().get();
            GlStateManager._blendFuncSeparate(GlConst.toGl(var2.sourceColor()), GlConst.toGl(var2.destColor()), GlConst.toGl(var2.sourceAlpha()), GlConst.toGl(var2.destAlpha()));
         } else {
            GlStateManager._disableBlend();
         }

         GlStateManager._polygonMode(1032, GlConst.toGl(var1.getPolygonMode()));
         GlStateManager._depthMask(var1.isWriteDepth());
         GlStateManager._colorMask(var1.isWriteColor(), var1.isWriteColor(), var1.isWriteColor(), var1.isWriteAlpha());
         if (var1.getDepthBiasConstant() == 0.0F && var1.getDepthBiasScaleFactor() == 0.0F) {
            GlStateManager._disablePolygonOffset();
         } else {
            GlStateManager._polygonOffset(var1.getDepthBiasScaleFactor(), var1.getDepthBiasConstant());
            GlStateManager._enablePolygonOffset();
         }

         switch (var1.getColorLogic()) {
            case NONE:
               GlStateManager._disableColorLogicOp();
               break;
            case OR_REVERSE:
               GlStateManager._enableColorLogicOp();
               GlStateManager._logicOp(5387);
         }

      }
   }

   public void finishRenderPass() {
      this.inRenderPass = false;
      GlStateManager._glBindFramebuffer(36160, 0);
   }

   protected GlDevice getDevice() {
      return this.device;
   }
}
