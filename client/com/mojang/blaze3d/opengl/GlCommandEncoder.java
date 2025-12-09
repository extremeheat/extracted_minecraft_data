package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.GpuFence;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuQuery;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
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
import org.lwjgl.opengl.GL32C;
import org.lwjgl.opengl.GL33C;
import org.slf4j.Logger;

public class GlCommandEncoder implements CommandEncoder {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final GlDevice device;
   private final int readFbo;
   private final int drawFbo;
   private @Nullable RenderPipeline lastPipeline;
   private boolean inRenderPass;
   private @Nullable GlProgram lastProgram;
   private @Nullable GlTimerQuery activeTimerQuery;

   protected GlCommandEncoder(GlDevice var1) {
      super();
      this.device = var1;
      this.readFbo = var1.directStateAccess().createFrameBufferObject();
      this.drawFbo = var1.directStateAccess().createFrameBufferObject();
   }

   public RenderPass createRenderPass(Supplier<String> var1, GpuTextureView var2, OptionalInt var3) {
      return this.createRenderPass(var1, var2, var3, (GpuTextureView)null, OptionalDouble.empty());
   }

   public RenderPass createRenderPass(Supplier<String> var1, GpuTextureView var2, OptionalInt var3, @Nullable GpuTextureView var4, OptionalDouble var5) {
      if (this.inRenderPass) {
         throw new IllegalStateException("Close the existing render pass before creating a new one!");
      } else {
         if (var5.isPresent() && var4 == null) {
            LOGGER.warn("Depth clear value was provided but no depth texture is being used");
         }

         if (var2.isClosed()) {
            throw new IllegalStateException("Color texture is closed");
         } else if ((var2.texture().usage() & 8) == 0) {
            throw new IllegalStateException("Color texture must have USAGE_RENDER_ATTACHMENT");
         } else if (var2.texture().getDepthOrLayers() > 1) {
            throw new UnsupportedOperationException("Textures with multiple depths or layers are not yet supported as an attachment");
         } else {
            if (var4 != null) {
               if (var4.isClosed()) {
                  throw new IllegalStateException("Depth texture is closed");
               }

               if ((var4.texture().usage() & 8) == 0) {
                  throw new IllegalStateException("Depth texture must have USAGE_RENDER_ATTACHMENT");
               }

               if (var4.texture().getDepthOrLayers() > 1) {
                  throw new UnsupportedOperationException("Textures with multiple depths or layers are not yet supported as an attachment");
               }
            }

            this.inRenderPass = true;
            this.device.debugLabels().pushDebugGroup(var1);
            int var6 = ((GlTextureView)var2).getFbo(this.device.directStateAccess(), var4 == null ? null : var4.texture());
            GlStateManager._glBindFramebuffer(36160, var6);
            int var7 = 0;
            if (var3.isPresent()) {
               int var8 = var3.getAsInt();
               GL11.glClearColor(ARGB.redFloat(var8), ARGB.greenFloat(var8), ARGB.blueFloat(var8), ARGB.alphaFloat(var8));
               var7 |= 16384;
            }

            if (var4 != null && var5.isPresent()) {
               GL11.glClearDepth(var5.getAsDouble());
               var7 |= 256;
            }

            if (var7 != 0) {
               GlStateManager._disableScissorTest();
               GlStateManager._depthMask(true);
               GlStateManager._colorMask(true, true, true, true);
               GlStateManager._clear(var7);
            }

            GlStateManager._viewport(0, 0, var2.getWidth(0), var2.getHeight(0));
            this.lastPipeline = null;
            return new GlRenderPass(this, var4 != null);
         }
      }
   }

   public void clearColorTexture(GpuTexture var1, int var2) {
      if (this.inRenderPass) {
         throw new IllegalStateException("Close the existing render pass before creating a new one!");
      } else {
         this.verifyColorTexture(var1);
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
      } else {
         this.verifyColorTexture(var1);
         this.verifyDepthTexture(var3);
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

   public void clearColorAndDepthTextures(GpuTexture var1, int var2, GpuTexture var3, double var4, int var6, int var7, int var8, int var9) {
      if (this.inRenderPass) {
         throw new IllegalStateException("Close the existing render pass before creating a new one!");
      } else {
         this.verifyColorTexture(var1);
         this.verifyDepthTexture(var3);
         this.verifyRegion(var1, var6, var7, var8, var9);
         int var10 = ((GlTexture)var1).getFbo(this.device.directStateAccess(), var3);
         GlStateManager._glBindFramebuffer(36160, var10);
         GlStateManager._scissorBox(var6, var7, var8, var9);
         GlStateManager._enableScissorTest();
         GL11.glClearDepth(var4);
         GL11.glClearColor(ARGB.redFloat(var2), ARGB.greenFloat(var2), ARGB.blueFloat(var2), ARGB.alphaFloat(var2));
         GlStateManager._depthMask(true);
         GlStateManager._colorMask(true, true, true, true);
         GlStateManager._clear(16640);
         GlStateManager._glBindFramebuffer(36160, 0);
      }
   }

   private void verifyRegion(GpuTexture var1, int var2, int var3, int var4, int var5) {
      if (var2 >= 0 && var2 < var1.getWidth(0)) {
         if (var3 >= 0 && var3 < var1.getHeight(0)) {
            if (var4 <= 0) {
               throw new IllegalArgumentException("regionWidth should be greater than 0");
            } else if (var2 + var4 > var1.getWidth(0)) {
               throw new IllegalArgumentException("regionWidth + regionX should be less than the texture width");
            } else if (var5 <= 0) {
               throw new IllegalArgumentException("regionHeight should be greater than 0");
            } else if (var3 + var5 > var1.getHeight(0)) {
               throw new IllegalArgumentException("regionWidth + regionX should be less than the texture height");
            }
         } else {
            throw new IllegalArgumentException("regionY should not be outside of the texture");
         }
      } else {
         throw new IllegalArgumentException("regionX should not be outside of the texture");
      }
   }

   public void clearDepthTexture(GpuTexture var1, double var2) {
      if (this.inRenderPass) {
         throw new IllegalStateException("Close the existing render pass before creating a new one!");
      } else {
         this.verifyDepthTexture(var1);
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

   private void verifyColorTexture(GpuTexture var1) {
      if (!var1.getFormat().hasColorAspect()) {
         throw new IllegalStateException("Trying to clear a non-color texture as color");
      } else if (var1.isClosed()) {
         throw new IllegalStateException("Color texture is closed");
      } else if ((var1.usage() & 8) == 0) {
         throw new IllegalStateException("Color texture must have USAGE_RENDER_ATTACHMENT");
      } else if (var1.getDepthOrLayers() > 1) {
         throw new UnsupportedOperationException("Clearing a texture with multiple layers or depths is not yet supported");
      }
   }

   private void verifyDepthTexture(GpuTexture var1) {
      if (!var1.getFormat().hasDepthAspect()) {
         throw new IllegalStateException("Trying to clear a non-depth texture as depth");
      } else if (var1.isClosed()) {
         throw new IllegalStateException("Depth texture is closed");
      } else if ((var1.usage() & 8) == 0) {
         throw new IllegalStateException("Depth texture must have USAGE_RENDER_ATTACHMENT");
      } else if (var1.getDepthOrLayers() > 1) {
         throw new UnsupportedOperationException("Clearing a texture with multiple layers or depths is not yet supported");
      }
   }

   public void writeToBuffer(GpuBufferSlice var1, ByteBuffer var2) {
      if (this.inRenderPass) {
         throw new IllegalStateException("Close the existing render pass before performing additional commands");
      } else {
         GlBuffer var3 = (GlBuffer)var1.buffer();
         if (var3.closed) {
            throw new IllegalStateException("Buffer already closed");
         } else if ((var3.usage() & 8) == 0) {
            throw new IllegalStateException("Buffer needs USAGE_COPY_DST to be a destination for a copy");
         } else {
            int var4 = var2.remaining();
            if ((long)var4 > var1.length()) {
               throw new IllegalArgumentException("Cannot write more data than the slice allows (attempting to write " + var4 + " bytes into a slice of length " + var1.length() + ")");
            } else if (var1.length() + var1.offset() > var3.size()) {
               throw new IllegalArgumentException("Cannot write more data than this buffer can hold (attempting to write " + var4 + " bytes at offset " + var1.offset() + " to " + var3.size() + " size buffer)");
            } else {
               this.device.directStateAccess().bufferSubData(var3.handle, var1.offset(), var2, var3.usage());
            }
         }
      }
   }

   public GpuBuffer.MappedView mapBuffer(GpuBuffer var1, boolean var2, boolean var3) {
      return this.mapBuffer(var1.slice(), var2, var3);
   }

   public GpuBuffer.MappedView mapBuffer(GpuBufferSlice var1, boolean var2, boolean var3) {
      if (this.inRenderPass) {
         throw new IllegalStateException("Close the existing render pass before performing additional commands");
      } else {
         GlBuffer var4 = (GlBuffer)var1.buffer();
         if (var4.closed) {
            throw new IllegalStateException("Buffer already closed");
         } else if (!var2 && !var3) {
            throw new IllegalArgumentException("At least read or write must be true");
         } else if (var2 && (var4.usage() & 1) == 0) {
            throw new IllegalStateException("Buffer is not readable");
         } else if (var3 && (var4.usage() & 2) == 0) {
            throw new IllegalStateException("Buffer is not writable");
         } else if (var1.offset() + var1.length() > var4.size()) {
            long var10002 = var1.length();
            throw new IllegalArgumentException("Cannot map more data than this buffer can hold (attempting to map " + var10002 + " bytes at offset " + var1.offset() + " from " + var4.size() + " size buffer)");
         } else {
            int var5 = 0;
            if (var2) {
               var5 |= 1;
            }

            if (var3) {
               var5 |= 34;
            }

            return this.device.getBufferStorage().mapBuffer(this.device.directStateAccess(), var4, var1.offset(), var1.length(), var5);
         }
      }
   }

   public void copyToBuffer(GpuBufferSlice var1, GpuBufferSlice var2) {
      if (this.inRenderPass) {
         throw new IllegalStateException("Close the existing render pass before performing additional commands");
      } else {
         GlBuffer var3 = (GlBuffer)var1.buffer();
         if (var3.closed) {
            throw new IllegalStateException("Source buffer already closed");
         } else if ((var3.usage() & 16) == 0) {
            throw new IllegalStateException("Source buffer needs USAGE_COPY_SRC to be a source for a copy");
         } else {
            GlBuffer var4 = (GlBuffer)var2.buffer();
            if (var4.closed) {
               throw new IllegalStateException("Target buffer already closed");
            } else if ((var4.usage() & 8) == 0) {
               throw new IllegalStateException("Target buffer needs USAGE_COPY_DST to be a destination for a copy");
            } else if (var1.length() != var2.length()) {
               long var6 = var1.length();
               throw new IllegalArgumentException("Cannot copy from slice of size " + var6 + " to slice of size " + var2.length() + ", they must be equal");
            } else if (var1.offset() + var1.length() > var3.size()) {
               long var5 = var1.length();
               throw new IllegalArgumentException("Cannot copy more data than the source buffer holds (attempting to copy " + var5 + " bytes at offset " + var1.offset() + " from " + var3.size() + " size buffer)");
            } else if (var2.offset() + var2.length() > var4.size()) {
               long var10002 = var2.length();
               throw new IllegalArgumentException("Cannot copy more data than the target buffer can hold (attempting to copy " + var10002 + " bytes at offset " + var2.offset() + " to " + var4.size() + " size buffer)");
            } else {
               this.device.directStateAccess().copyBufferSubData(var3.handle, var4.handle, var1.offset(), var2.offset(), var1.length());
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
         } else if ((var1.usage() & 1) == 0) {
            throw new IllegalStateException("Color texture must have USAGE_COPY_DST to be a destination for a write");
         } else {
            this.writeToTexture(var1, var2, 0, 0, 0, 0, var3, var4, 0, 0);
         }
      } else {
         throw new IllegalArgumentException("Cannot replace texture of size " + var3 + "x" + var4 + " with image of size " + var2.getWidth() + "x" + var2.getHeight());
      }
   }

   public void writeToTexture(GpuTexture var1, NativeImage var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      if (this.inRenderPass) {
         throw new IllegalStateException("Close the existing render pass before performing additional commands");
      } else if (var3 >= 0 && var3 < var1.getMipLevels()) {
         if (var9 + var7 <= var2.getWidth() && var10 + var8 <= var2.getHeight()) {
            if (var5 + var7 <= var1.getWidth(var3) && var6 + var8 <= var1.getHeight(var3)) {
               if (var1.isClosed()) {
                  throw new IllegalStateException("Destination texture is closed");
               } else if ((var1.usage() & 1) == 0) {
                  throw new IllegalStateException("Color texture must have USAGE_COPY_DST to be a destination for a write");
               } else if (var4 >= var1.getDepthOrLayers()) {
                  throw new UnsupportedOperationException("Depth or layer is out of range, must be >= 0 and < " + var1.getDepthOrLayers());
               } else {
                  int var11;
                  if ((var1.usage() & 16) != 0) {
                     var11 = GlConst.CUBEMAP_TARGETS[var4 % 6];
                     GL11.glBindTexture(34067, ((GlTexture)var1).id);
                  } else {
                     var11 = 3553;
                     GlStateManager._bindTexture(((GlTexture)var1).id);
                  }

                  GlStateManager._pixelStore(3314, var2.getWidth());
                  GlStateManager._pixelStore(3316, var9);
                  GlStateManager._pixelStore(3315, var10);
                  GlStateManager._pixelStore(3317, var2.format().components());
                  GlStateManager._texSubImage2D(var11, var3, var5, var6, var7, var8, GlConst.toGl(var2.format()), 5121, var2.getPointer());
               }
            } else {
               throw new IllegalArgumentException("Dest texture (" + var7 + "x" + var8 + ") is not large enough to write a rectangle of " + var7 + "x" + var8 + " at " + var5 + "x" + var6 + " (at mip level " + var3 + ")");
            }
         } else {
            int var10002 = var2.getWidth();
            throw new IllegalArgumentException("Copy source (" + var10002 + "x" + var2.getHeight() + ") is not large enough to read a rectangle of " + var7 + "x" + var8 + " from " + var9 + "x" + var10);
         }
      } else {
         throw new IllegalArgumentException("Invalid mipLevel " + var3 + ", must be >= 0 and < " + var1.getMipLevels());
      }
   }

   public void writeToTexture(GpuTexture var1, ByteBuffer var2, NativeImage.Format var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      if (this.inRenderPass) {
         throw new IllegalStateException("Close the existing render pass before performing additional commands");
      } else if (var4 >= 0 && var4 < var1.getMipLevels()) {
         if (var8 * var9 * var3.components() > var2.remaining()) {
            throw new IllegalArgumentException("Copy would overrun the source buffer (remaining length of " + var2.remaining() + ", but copy is " + var8 + "x" + var9 + " of format " + String.valueOf(var3) + ")");
         } else if (var6 + var8 <= var1.getWidth(var4) && var7 + var9 <= var1.getHeight(var4)) {
            if (var1.isClosed()) {
               throw new IllegalStateException("Destination texture is closed");
            } else if ((var1.usage() & 1) == 0) {
               throw new IllegalStateException("Color texture must have USAGE_COPY_DST to be a destination for a write");
            } else if (var5 >= var1.getDepthOrLayers()) {
               throw new UnsupportedOperationException("Depth or layer is out of range, must be >= 0 and < " + var1.getDepthOrLayers());
            } else {
               int var10;
               if ((var1.usage() & 16) != 0) {
                  var10 = GlConst.CUBEMAP_TARGETS[var5 % 6];
                  GL11.glBindTexture(34067, ((GlTexture)var1).id);
               } else {
                  var10 = 3553;
                  GlStateManager._bindTexture(((GlTexture)var1).id);
               }

               GlStateManager._pixelStore(3314, var8);
               GlStateManager._pixelStore(3316, 0);
               GlStateManager._pixelStore(3315, 0);
               GlStateManager._pixelStore(3317, var3.components());
               GlStateManager._texSubImage2D(var10, var4, var6, var7, var8, var9, GlConst.toGl(var3), 5121, var2);
            }
         } else {
            throw new IllegalArgumentException("Dest texture (" + var1.getWidth(var4) + "x" + var1.getHeight(var4) + ") is not large enough to write a rectangle of " + var8 + "x" + var9 + " at " + var6 + "x" + var7);
         }
      } else {
         throw new IllegalArgumentException("Invalid mipLevel, must be >= 0 and < " + var1.getMipLevels());
      }
   }

   public void copyTextureToBuffer(GpuTexture var1, GpuBuffer var2, long var3, Runnable var5, int var6) {
      if (this.inRenderPass) {
         throw new IllegalStateException("Close the existing render pass before performing additional commands");
      } else {
         this.copyTextureToBuffer(var1, var2, var3, var5, var6, 0, 0, var1.getWidth(var6), var1.getHeight(var6));
      }
   }

   public void copyTextureToBuffer(GpuTexture var1, GpuBuffer var2, long var3, Runnable var5, int var6, int var7, int var8, int var9, int var10) {
      if (this.inRenderPass) {
         throw new IllegalStateException("Close the existing render pass before performing additional commands");
      } else if (var6 >= 0 && var6 < var1.getMipLevels()) {
         if ((long)(var1.getWidth(var6) * var1.getHeight(var6) * var1.getFormat().pixelSize()) + var3 > var2.size()) {
            long var12 = var2.size();
            throw new IllegalArgumentException("Buffer of size " + var12 + " is not large enough to hold " + var9 + "x" + var10 + " pixels (" + var1.getFormat().pixelSize() + " bytes each) starting from offset " + var3);
         } else if ((var1.usage() & 2) == 0) {
            throw new IllegalArgumentException("Texture needs USAGE_COPY_SRC to be a source for a copy");
         } else if ((var2.usage() & 8) == 0) {
            throw new IllegalArgumentException("Buffer needs USAGE_COPY_DST to be a destination for a copy");
         } else if (var7 + var9 <= var1.getWidth(var6) && var8 + var10 <= var1.getHeight(var6)) {
            if (var1.isClosed()) {
               throw new IllegalStateException("Source texture is closed");
            } else if (var2.isClosed()) {
               throw new IllegalStateException("Destination buffer is closed");
            } else if (var1.getDepthOrLayers() > 1) {
               throw new UnsupportedOperationException("Textures with multiple depths or layers are not yet supported for copying");
            } else {
               GlStateManager.clearGlErrors();
               this.device.directStateAccess().bindFrameBufferTextures(this.readFbo, ((GlTexture)var1).glId(), 0, var6, 36008);
               GlStateManager._glBindBuffer(35051, ((GlBuffer)var2).handle);
               GlStateManager._pixelStore(3330, var9);
               GlStateManager._readPixels(var7, var8, var9, var10, GlConst.toGlExternalId(var1.getFormat()), GlConst.toGlType(var1.getFormat()), var3);
               RenderSystem.queueFencedTask(var5);
               GlStateManager._glFramebufferTexture2D(36008, 36064, 3553, 0, var6);
               GlStateManager._glBindFramebuffer(36008, 0);
               GlStateManager._glBindBuffer(35051, 0);
               int var11 = GlStateManager._getError();
               if (var11 != 0) {
                  String var10002 = var1.getLabel();
                  throw new IllegalStateException("Couldn't perform copyTobuffer for texture " + var10002 + ": GL error " + var11);
               }
            }
         } else {
            throw new IllegalArgumentException("Copy source texture (" + var1.getWidth(var6) + "x" + var1.getHeight(var6) + ") is not large enough to read a rectangle of " + var9 + "x" + var10 + " from " + var7 + "," + var8);
         }
      } else {
         throw new IllegalArgumentException("Invalid mipLevel " + var6 + ", must be >= 0 and < " + var1.getMipLevels());
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
               } else if ((var1.usage() & 2) == 0) {
                  throw new IllegalArgumentException("Texture needs USAGE_COPY_SRC to be a source for a copy");
               } else if ((var2.usage() & 1) == 0) {
                  throw new IllegalArgumentException("Texture needs USAGE_COPY_DST to be a destination for a copy");
               } else if (var1.getDepthOrLayers() > 1) {
                  throw new UnsupportedOperationException("Textures with multiple depths or layers are not yet supported for copying");
               } else if (var2.getDepthOrLayers() > 1) {
                  throw new UnsupportedOperationException("Textures with multiple depths or layers are not yet supported for copying");
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

   public void presentTexture(GpuTextureView var1) {
      if (this.inRenderPass) {
         throw new IllegalStateException("Close the existing render pass before performing additional commands");
      } else if (!var1.texture().getFormat().hasColorAspect()) {
         throw new IllegalStateException("Cannot present a non-color texture!");
      } else if ((var1.texture().usage() & 8) == 0) {
         throw new IllegalStateException("Color texture must have USAGE_RENDER_ATTACHMENT to presented to the screen");
      } else if (var1.texture().getDepthOrLayers() > 1) {
         throw new UnsupportedOperationException("Textures with multiple depths or layers are not yet supported for presentation");
      } else {
         GlStateManager._disableScissorTest();
         GlStateManager._viewport(0, 0, var1.getWidth(0), var1.getHeight(0));
         GlStateManager._depthMask(true);
         GlStateManager._colorMask(true, true, true, true);
         this.device.directStateAccess().bindFrameBufferTextures(this.drawFbo, ((GlTexture)var1.texture()).glId(), 0, 0, 0);
         this.device.directStateAccess().blitFrameBuffers(this.drawFbo, 0, 0, 0, var1.getWidth(0), var1.getHeight(0), 0, 0, var1.getWidth(0), var1.getHeight(0), 16384, 9728);
      }
   }

   public GpuFence createFence() {
      if (this.inRenderPass) {
         throw new IllegalStateException("Close the existing render pass before performing additional commands");
      } else {
         return new GlFence();
      }
   }

   protected <T> void executeDrawMultiple(GlRenderPass var1, Collection<RenderPass.Draw<T>> var2, @Nullable GpuBuffer var3, VertexFormat.@Nullable IndexType var4, Collection<String> var5, T var6) {
      if (this.trySetup(var1, var5)) {
         if (var4 == null) {
            var4 = VertexFormat.IndexType.SHORT;
         }

         for(RenderPass.Draw var8 : var2) {
            VertexFormat.IndexType var9 = var8.indexType() == null ? var4 : var8.indexType();
            var1.setIndexBuffer(var8.indexBuffer() == null ? var3 : var8.indexBuffer(), var9);
            var1.setVertexBuffer(var8.slot(), var8.vertexBuffer());
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

            BiConsumer var10 = var8.uniformUploaderConsumer();
            if (var10 != null) {
               var10.accept(var6, (RenderPass.UniformUploader)(var1x, var2x) -> {
                  Uniform var5 = var1.pipeline.program().getUniform(var1x);
                  if (var5 instanceof Uniform.Ubo var3) {
                     Uniform.Ubo var10000 = var3;

                     try {
                        var8 = var10000.blockBinding();
                     } catch (Throwable var7) {
                        throw new MatchException(var7.toString(), var7);
                     }

                     int var6 = var8;
                     GL32.glBindBufferRange(35345, var6, ((GlBuffer)var2x.buffer()).handle, var2x.offset(), var2x.length());
                  }

               });
            }

            this.drawFromBuffers(var1, 0, var8.firstIndex(), var8.indexCount(), var9, var1.pipeline, 1);
         }

      }
   }

   protected void executeDraw(GlRenderPass var1, int var2, int var3, int var4, VertexFormat.@Nullable IndexType var5, int var6) {
      if (this.trySetup(var1, Collections.emptyList())) {
         if (GlRenderPass.VALIDATION) {
            if (var5 != null) {
               if (var1.indexBuffer == null) {
                  throw new IllegalStateException("Missing index buffer");
               }

               if (var1.indexBuffer.isClosed()) {
                  throw new IllegalStateException("Index buffer has been closed!");
               }

               if ((var1.indexBuffer.usage() & 64) == 0) {
                  throw new IllegalStateException("Index buffer must have GpuBuffer.USAGE_INDEX!");
               }
            }

            GlRenderPipeline var7 = var1.pipeline;
            if (var1.vertexBuffers[0] == null && var7 != null && !var7.info().getVertexFormat().getElements().isEmpty()) {
               throw new IllegalStateException("Vertex format contains elements but vertex buffer at slot 0 is null");
            }

            if (var1.vertexBuffers[0] != null && var1.vertexBuffers[0].isClosed()) {
               throw new IllegalStateException("Vertex buffer at slot 0 has been closed!");
            }

            if (var1.vertexBuffers[0] != null && (var1.vertexBuffers[0].usage() & 32) == 0) {
               throw new IllegalStateException("Vertex buffer must have GpuBuffer.USAGE_VERTEX!");
            }
         }

         this.drawFromBuffers(var1, var2, var3, var4, var5, var1.pipeline, var6);
      }
   }

   private void drawFromBuffers(GlRenderPass var1, int var2, int var3, int var4, VertexFormat.@Nullable IndexType var5, GlRenderPipeline var6, int var7) {
      this.device.vertexArrayCache().bindVertexArray(var6.info().getVertexFormat(), (GlBuffer)var1.vertexBuffers[0]);
      if (var5 != null) {
         GlStateManager._glBindBuffer(34963, ((GlBuffer)var1.indexBuffer).handle);
         if (var7 > 1) {
            if (var2 > 0) {
               GL32.glDrawElementsInstancedBaseVertex(GlConst.toGl(var6.info().getVertexFormatMode()), var4, GlConst.toGl(var5), (long)var3 * (long)var5.bytes, var7, var2);
            } else {
               GL31.glDrawElementsInstanced(GlConst.toGl(var6.info().getVertexFormatMode()), var4, GlConst.toGl(var5), (long)var3 * (long)var5.bytes, var7);
            }
         } else if (var2 > 0) {
            GL32.glDrawElementsBaseVertex(GlConst.toGl(var6.info().getVertexFormatMode()), var4, GlConst.toGl(var5), (long)var3 * (long)var5.bytes, var2);
         } else {
            GlStateManager._drawElements(GlConst.toGl(var6.info().getVertexFormatMode()), var4, GlConst.toGl(var5), (long)var3 * (long)var5.bytes);
         }
      } else if (var7 > 1) {
         GL31.glDrawArraysInstanced(GlConst.toGl(var6.info().getVertexFormatMode()), var2, var4, var7);
      } else {
         GlStateManager._drawArrays(GlConst.toGl(var6.info().getVertexFormatMode()), var2, var4);
      }

   }

   private boolean trySetup(GlRenderPass var1, Collection<String> var2) {
      if (!GlRenderPass.VALIDATION) {
         if (var1.pipeline == null || var1.pipeline.program() == GlProgram.INVALID_PROGRAM) {
            return false;
         }
      } else {
         if (var1.pipeline == null) {
            throw new IllegalStateException("Can't draw without a render pipeline");
         }

         if (var1.pipeline.program() == GlProgram.INVALID_PROGRAM) {
            throw new IllegalStateException("Pipeline contains invalid shader program");
         }

         for(RenderPipeline.UniformDescription var4 : var1.pipeline.info().getUniforms()) {
            GpuBufferSlice var5 = (GpuBufferSlice)var1.uniforms.get(var4.name());
            if (!var2.contains(var4.name())) {
               if (var5 == null) {
                  String var10002 = var4.name();
                  throw new IllegalStateException("Missing uniform " + var10002 + " (should be " + String.valueOf(var4.type()) + ")");
               }

               if (var4.type() == UniformType.UNIFORM_BUFFER) {
                  if (var5.buffer().isClosed()) {
                     throw new IllegalStateException("Uniform buffer " + var4.name() + " is already closed");
                  }

                  if ((var5.buffer().usage() & 128) == 0) {
                     throw new IllegalStateException("Uniform buffer " + var4.name() + " must have GpuBuffer.USAGE_UNIFORM");
                  }
               }

               if (var4.type() == UniformType.TEXEL_BUFFER) {
                  if (var5.offset() != 0L || var5.length() != var5.buffer().size()) {
                     throw new IllegalStateException("Uniform texel buffers do not support a slice of a buffer, must be entire buffer");
                  }

                  if (var4.textureFormat() == null) {
                     throw new IllegalStateException("Invalid uniform texel buffer " + var4.name() + " (missing a texture format)");
                  }
               }
            }
         }

         for(Map.Entry var35 : var1.pipeline.program().getUniforms().entrySet()) {
            if (var35.getValue() instanceof Uniform.Sampler) {
               String var37 = (String)var35.getKey();
               GlRenderPass.TextureViewAndSampler var6 = (GlRenderPass.TextureViewAndSampler)var1.samplers.get(var37);
               if (var6 == null) {
                  throw new IllegalStateException("Missing sampler " + var37);
               }

               GlTextureView var7 = var6.view();
               if (var7.isClosed()) {
                  throw new IllegalStateException("Texture view " + var37 + " (" + var7.texture().getLabel() + ") has been closed!");
               }

               if ((var7.texture().usage() & 4) == 0) {
                  throw new IllegalStateException("Texture view " + var37 + " (" + var7.texture().getLabel() + ") must have USAGE_TEXTURE_BINDING!");
               }

               if (var6.sampler().isClosed()) {
                  throw new IllegalStateException("Sampler for " + var37 + " (" + var7.texture().getLabel() + ") has been closed!");
               }
            }
         }

         if (var1.pipeline.info().wantsDepthTexture() && !var1.hasDepthTexture()) {
            LOGGER.warn("Render pipeline {} wants a depth texture but none was provided - this is probably a bug", var1.pipeline.info().getLocation());
         }
      }

      RenderPipeline var34 = var1.pipeline.info();
      GlProgram var36 = var1.pipeline.program();
      this.applyPipelineState(var34);
      boolean var38 = this.lastProgram != var36;
      if (var38) {
         GlStateManager._glUseProgram(var36.getProgramId());
         this.lastProgram = var36;
      }

      for(Map.Entry var40 : var36.getUniforms().entrySet()) {
         String var8 = (String)var40.getKey();
         boolean var9 = var1.dirtyUniforms.contains(var8);
         Uniform.Ubo var10000 = (Uniform)var40.getValue();
         Objects.requireNonNull(var10000);
         Uniform var10 = var10000;
         byte var11 = 0;
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
               int var13 = var41;
               if (var9) {
                  GpuBufferSlice var42 = (GpuBufferSlice)var1.uniforms.get(var8);
                  GL32.glBindBufferRange(35345, var13, ((GlBuffer)var42.buffer()).handle, var42.offset(), var42.length());
               }
               break;
            case 1:
               Uniform.Utb var14 = (Uniform.Utb)var10;
               Uniform.Utb var54 = var14;

               try {
                  var55 = var54.location();
               } catch (Throwable var31) {
                  throw new MatchException(var31.toString(), var31);
               }

               int var43 = var55;
               int var15 = var43;
               var54 = var14;

               try {
                  var57 = var54.samplerIndex();
               } catch (Throwable var30) {
                  throw new MatchException(var30.toString(), var30);
               }

               var43 = var57;
               int var16 = var43;
               var54 = var14;

               try {
                  var59 = var54.format();
               } catch (Throwable var29) {
                  throw new MatchException(var29.toString(), var29);
               }

               TextureFormat var45 = var59;
               TextureFormat var17 = var45;
               var54 = var14;

               try {
                  var61 = var54.texture();
               } catch (Throwable var28) {
                  throw new MatchException(var28.toString(), var28);
               }

               int var46 = var61;
               if (var38 || var9) {
                  GlStateManager._glUniform1i(var15, var16);
               }

               GlStateManager._activeTexture('\u84c0' + var16);
               GL11C.glBindTexture(35882, var46);
               if (var9) {
                  GpuBufferSlice var47 = (GpuBufferSlice)var1.uniforms.get(var8);
                  GL31.glTexBuffer(35882, GlConst.toGlInternalId(var17), ((GlBuffer)var47.buffer()).handle);
               }
               break;
            case 2:
               Uniform.Sampler var19 = (Uniform.Sampler)var10;
               Uniform.Sampler var50 = var19;

               try {
                  var51 = var50.location();
               } catch (Throwable var27) {
                  throw new MatchException(var27.toString(), var27);
               }

               int var22 = var51;
               int var20 = var22;
               var50 = var19;

               try {
                  var53 = var50.samplerIndex();
               } catch (Throwable var26) {
                  throw new MatchException(var26.toString(), var26);
               }

               var22 = var53;
               int var21 = var22;
               GlRenderPass.TextureViewAndSampler var49 = (GlRenderPass.TextureViewAndSampler)var1.samplers.get(var8);
               if (var49 == null) {
                  break;
               }

               GlTextureView var23 = var49.view();
               if (var38 || var9) {
                  GlStateManager._glUniform1i(var20, var21);
               }

               GlStateManager._activeTexture('\u84c0' + var21);
               GlTexture var24 = var23.texture();
               char var25;
               if ((var24.usage() & 16) != 0) {
                  var25 = '\u8513';
                  GL11.glBindTexture(34067, var24.id);
               } else {
                  var25 = 3553;
                  GlStateManager._bindTexture(var24.id);
               }

               GL33C.glBindSampler(var21, var49.sampler().getId());
               GlStateManager._texParameter(var25, 33084, var23.baseMipLevel());
               GlStateManager._texParameter(var25, 33085, var23.baseMipLevel() + var23.mipLevels() - 1);
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }
      }

      var1.dirtyUniforms.clear();
      if (var1.isScissorEnabled()) {
         GlStateManager._enableScissorTest();
         GlStateManager._scissorBox(var1.getScissorX(), var1.getScissorY(), var1.getScissorWidth(), var1.getScissorHeight());
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
      this.device.debugLabels().popDebugGroup();
   }

   protected GlDevice getDevice() {
      return this.device;
   }

   public GpuQuery timerQueryBegin() {
      RenderSystem.assertOnRenderThread();
      if (this.activeTimerQuery != null) {
         throw new IllegalStateException("A GL_TIME_ELAPSED query is already active");
      } else {
         int var1 = GL32C.glGenQueries();
         GL32C.glBeginQuery(35007, var1);
         this.activeTimerQuery = new GlTimerQuery(var1);
         return this.activeTimerQuery;
      }
   }

   public void timerQueryEnd(GpuQuery var1) {
      RenderSystem.assertOnRenderThread();
      if (var1 != this.activeTimerQuery) {
         throw new IllegalStateException("Mismatched or duplicate GpuQuery when ending timerQuery");
      } else {
         GL32C.glEndQuery(35007);
         this.activeTimerQuery = null;
      }
   }
}
