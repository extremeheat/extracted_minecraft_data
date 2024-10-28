package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.IntUnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.providers.FreeTypeUtil;
import net.minecraft.util.FastColor;
import net.minecraft.util.PngInfo;
import org.apache.commons.io.IOUtils;
import org.lwjgl.stb.STBIWriteCallback;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageResize;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.freetype.FT_Bitmap;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FT_GlyphSlot;
import org.lwjgl.util.freetype.FreeType;
import org.slf4j.Logger;

public final class NativeImage implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Set<StandardOpenOption> OPEN_OPTIONS;
   private final Format format;
   private final int width;
   private final int height;
   private final boolean useStbFree;
   private long pixels;
   private final long size;

   public NativeImage(int var1, int var2, boolean var3) {
      this(NativeImage.Format.RGBA, var1, var2, var3);
   }

   public NativeImage(Format var1, int var2, int var3, boolean var4) {
      super();
      if (var2 > 0 && var3 > 0) {
         this.format = var1;
         this.width = var2;
         this.height = var3;
         this.size = (long)var2 * (long)var3 * (long)var1.components();
         this.useStbFree = false;
         if (var4) {
            this.pixels = MemoryUtil.nmemCalloc(1L, this.size);
         } else {
            this.pixels = MemoryUtil.nmemAlloc(this.size);
         }

         if (this.pixels == 0L) {
            throw new IllegalStateException("Unable to allocate texture of size " + var2 + "x" + var3 + " (" + var1.components() + " channels)");
         }
      } else {
         throw new IllegalArgumentException("Invalid texture size: " + var2 + "x" + var3);
      }
   }

   private NativeImage(Format var1, int var2, int var3, boolean var4, long var5) {
      super();
      if (var2 > 0 && var3 > 0) {
         this.format = var1;
         this.width = var2;
         this.height = var3;
         this.useStbFree = var4;
         this.pixels = var5;
         this.size = (long)var2 * (long)var3 * (long)var1.components();
      } else {
         throw new IllegalArgumentException("Invalid texture size: " + var2 + "x" + var3);
      }
   }

   public String toString() {
      String var10000 = String.valueOf(this.format);
      return "NativeImage[" + var10000 + " " + this.width + "x" + this.height + "@" + this.pixels + (this.useStbFree ? "S" : "N") + "]";
   }

   private boolean isOutsideBounds(int var1, int var2) {
      return var1 < 0 || var1 >= this.width || var2 < 0 || var2 >= this.height;
   }

   public static NativeImage read(InputStream var0) throws IOException {
      return read(NativeImage.Format.RGBA, var0);
   }

   public static NativeImage read(@Nullable Format var0, InputStream var1) throws IOException {
      ByteBuffer var2 = null;

      NativeImage var3;
      try {
         var2 = TextureUtil.readResource(var1);
         var2.rewind();
         var3 = read(var0, var2);
      } finally {
         MemoryUtil.memFree(var2);
         IOUtils.closeQuietly(var1);
      }

      return var3;
   }

   public static NativeImage read(ByteBuffer var0) throws IOException {
      return read(NativeImage.Format.RGBA, var0);
   }

   public static NativeImage read(byte[] var0) throws IOException {
      MemoryStack var1 = MemoryStack.stackPush();

      NativeImage var3;
      try {
         ByteBuffer var2 = var1.malloc(var0.length);
         var2.put(var0);
         var2.rewind();
         var3 = read(var2);
      } catch (Throwable var5) {
         if (var1 != null) {
            try {
               var1.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (var1 != null) {
         var1.close();
      }

      return var3;
   }

   public static NativeImage read(@Nullable Format var0, ByteBuffer var1) throws IOException {
      if (var0 != null && !var0.supportedByStb()) {
         throw new UnsupportedOperationException("Don't know how to read format " + String.valueOf(var0));
      } else if (MemoryUtil.memAddress(var1) == 0L) {
         throw new IllegalArgumentException("Invalid buffer");
      } else {
         PngInfo.validateHeader(var1);
         MemoryStack var2 = MemoryStack.stackPush();

         NativeImage var7;
         try {
            IntBuffer var3 = var2.mallocInt(1);
            IntBuffer var4 = var2.mallocInt(1);
            IntBuffer var5 = var2.mallocInt(1);
            ByteBuffer var6 = STBImage.stbi_load_from_memory(var1, var3, var4, var5, var0 == null ? 0 : var0.components);
            if (var6 == null) {
               throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
            }

            var7 = new NativeImage(var0 == null ? NativeImage.Format.getStbFormat(var5.get(0)) : var0, var3.get(0), var4.get(0), true, MemoryUtil.memAddress(var6));
         } catch (Throwable var9) {
            if (var2 != null) {
               try {
                  var2.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (var2 != null) {
            var2.close();
         }

         return var7;
      }
   }

   private static void setFilter(boolean var0, boolean var1) {
      RenderSystem.assertOnRenderThreadOrInit();
      if (var0) {
         GlStateManager._texParameter(3553, 10241, var1 ? 9987 : 9729);
         GlStateManager._texParameter(3553, 10240, 9729);
      } else {
         GlStateManager._texParameter(3553, 10241, var1 ? 9986 : 9728);
         GlStateManager._texParameter(3553, 10240, 9728);
      }

   }

   private void checkAllocated() {
      if (this.pixels == 0L) {
         throw new IllegalStateException("Image is not allocated.");
      }
   }

   public void close() {
      if (this.pixels != 0L) {
         if (this.useStbFree) {
            STBImage.nstbi_image_free(this.pixels);
         } else {
            MemoryUtil.nmemFree(this.pixels);
         }
      }

      this.pixels = 0L;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public Format format() {
      return this.format;
   }

   public int getPixelRGBA(int var1, int var2) {
      if (this.format != NativeImage.Format.RGBA) {
         throw new IllegalArgumentException(String.format(Locale.ROOT, "getPixelRGBA only works on RGBA images; have %s", this.format));
      } else if (this.isOutsideBounds(var1, var2)) {
         throw new IllegalArgumentException(String.format(Locale.ROOT, "(%s, %s) outside of image bounds (%s, %s)", var1, var2, this.width, this.height));
      } else {
         this.checkAllocated();
         long var3 = ((long)var1 + (long)var2 * (long)this.width) * 4L;
         return MemoryUtil.memGetInt(this.pixels + var3);
      }
   }

   public void setPixelRGBA(int var1, int var2, int var3) {
      if (this.format != NativeImage.Format.RGBA) {
         throw new IllegalArgumentException(String.format(Locale.ROOT, "setPixelRGBA only works on RGBA images; have %s", this.format));
      } else if (this.isOutsideBounds(var1, var2)) {
         throw new IllegalArgumentException(String.format(Locale.ROOT, "(%s, %s) outside of image bounds (%s, %s)", var1, var2, this.width, this.height));
      } else {
         this.checkAllocated();
         long var4 = ((long)var1 + (long)var2 * (long)this.width) * 4L;
         MemoryUtil.memPutInt(this.pixels + var4, var3);
      }
   }

   public NativeImage mappedCopy(IntUnaryOperator var1) {
      if (this.format != NativeImage.Format.RGBA) {
         throw new IllegalArgumentException(String.format(Locale.ROOT, "function application only works on RGBA images; have %s", this.format));
      } else {
         this.checkAllocated();
         NativeImage var2 = new NativeImage(this.width, this.height, false);
         int var3 = this.width * this.height;
         IntBuffer var4 = MemoryUtil.memIntBuffer(this.pixels, var3);
         IntBuffer var5 = MemoryUtil.memIntBuffer(var2.pixels, var3);

         for(int var6 = 0; var6 < var3; ++var6) {
            var5.put(var6, var1.applyAsInt(var4.get(var6)));
         }

         return var2;
      }
   }

   public void applyToAllPixels(IntUnaryOperator var1) {
      if (this.format != NativeImage.Format.RGBA) {
         throw new IllegalArgumentException(String.format(Locale.ROOT, "function application only works on RGBA images; have %s", this.format));
      } else {
         this.checkAllocated();
         int var2 = this.width * this.height;
         IntBuffer var3 = MemoryUtil.memIntBuffer(this.pixels, var2);

         for(int var4 = 0; var4 < var2; ++var4) {
            var3.put(var4, var1.applyAsInt(var3.get(var4)));
         }

      }
   }

   public int[] getPixelsRGBA() {
      if (this.format != NativeImage.Format.RGBA) {
         throw new IllegalArgumentException(String.format(Locale.ROOT, "getPixelsRGBA only works on RGBA images; have %s", this.format));
      } else {
         this.checkAllocated();
         int[] var1 = new int[this.width * this.height];
         MemoryUtil.memIntBuffer(this.pixels, this.width * this.height).get(var1);
         return var1;
      }
   }

   public void setPixelLuminance(int var1, int var2, byte var3) {
      RenderSystem.assertOnRenderThread();
      if (!this.format.hasLuminance()) {
         throw new IllegalArgumentException(String.format(Locale.ROOT, "setPixelLuminance only works on image with luminance; have %s", this.format));
      } else if (this.isOutsideBounds(var1, var2)) {
         throw new IllegalArgumentException(String.format(Locale.ROOT, "(%s, %s) outside of image bounds (%s, %s)", var1, var2, this.width, this.height));
      } else {
         this.checkAllocated();
         long var4 = ((long)var1 + (long)var2 * (long)this.width) * (long)this.format.components() + (long)(this.format.luminanceOffset() / 8);
         MemoryUtil.memPutByte(this.pixels + var4, var3);
      }
   }

   public byte getRedOrLuminance(int var1, int var2) {
      RenderSystem.assertOnRenderThread();
      if (!this.format.hasLuminanceOrRed()) {
         throw new IllegalArgumentException(String.format(Locale.ROOT, "no red or luminance in %s", this.format));
      } else if (this.isOutsideBounds(var1, var2)) {
         throw new IllegalArgumentException(String.format(Locale.ROOT, "(%s, %s) outside of image bounds (%s, %s)", var1, var2, this.width, this.height));
      } else {
         int var3 = (var1 + var2 * this.width) * this.format.components() + this.format.luminanceOrRedOffset() / 8;
         return MemoryUtil.memGetByte(this.pixels + (long)var3);
      }
   }

   public byte getGreenOrLuminance(int var1, int var2) {
      RenderSystem.assertOnRenderThread();
      if (!this.format.hasLuminanceOrGreen()) {
         throw new IllegalArgumentException(String.format(Locale.ROOT, "no green or luminance in %s", this.format));
      } else if (this.isOutsideBounds(var1, var2)) {
         throw new IllegalArgumentException(String.format(Locale.ROOT, "(%s, %s) outside of image bounds (%s, %s)", var1, var2, this.width, this.height));
      } else {
         int var3 = (var1 + var2 * this.width) * this.format.components() + this.format.luminanceOrGreenOffset() / 8;
         return MemoryUtil.memGetByte(this.pixels + (long)var3);
      }
   }

   public byte getBlueOrLuminance(int var1, int var2) {
      RenderSystem.assertOnRenderThread();
      if (!this.format.hasLuminanceOrBlue()) {
         throw new IllegalArgumentException(String.format(Locale.ROOT, "no blue or luminance in %s", this.format));
      } else if (this.isOutsideBounds(var1, var2)) {
         throw new IllegalArgumentException(String.format(Locale.ROOT, "(%s, %s) outside of image bounds (%s, %s)", var1, var2, this.width, this.height));
      } else {
         int var3 = (var1 + var2 * this.width) * this.format.components() + this.format.luminanceOrBlueOffset() / 8;
         return MemoryUtil.memGetByte(this.pixels + (long)var3);
      }
   }

   public byte getLuminanceOrAlpha(int var1, int var2) {
      if (!this.format.hasLuminanceOrAlpha()) {
         throw new IllegalArgumentException(String.format(Locale.ROOT, "no luminance or alpha in %s", this.format));
      } else if (this.isOutsideBounds(var1, var2)) {
         throw new IllegalArgumentException(String.format(Locale.ROOT, "(%s, %s) outside of image bounds (%s, %s)", var1, var2, this.width, this.height));
      } else {
         int var3 = (var1 + var2 * this.width) * this.format.components() + this.format.luminanceOrAlphaOffset() / 8;
         return MemoryUtil.memGetByte(this.pixels + (long)var3);
      }
   }

   public void blendPixel(int var1, int var2, int var3) {
      if (this.format != NativeImage.Format.RGBA) {
         throw new UnsupportedOperationException("Can only call blendPixel with RGBA format");
      } else {
         int var4 = this.getPixelRGBA(var1, var2);
         float var5 = (float)FastColor.ABGR32.alpha(var3) / 255.0F;
         float var6 = (float)FastColor.ABGR32.blue(var3) / 255.0F;
         float var7 = (float)FastColor.ABGR32.green(var3) / 255.0F;
         float var8 = (float)FastColor.ABGR32.red(var3) / 255.0F;
         float var9 = (float)FastColor.ABGR32.alpha(var4) / 255.0F;
         float var10 = (float)FastColor.ABGR32.blue(var4) / 255.0F;
         float var11 = (float)FastColor.ABGR32.green(var4) / 255.0F;
         float var12 = (float)FastColor.ABGR32.red(var4) / 255.0F;
         float var14 = 1.0F - var5;
         float var15 = var5 * var5 + var9 * var14;
         float var16 = var6 * var5 + var10 * var14;
         float var17 = var7 * var5 + var11 * var14;
         float var18 = var8 * var5 + var12 * var14;
         if (var15 > 1.0F) {
            var15 = 1.0F;
         }

         if (var16 > 1.0F) {
            var16 = 1.0F;
         }

         if (var17 > 1.0F) {
            var17 = 1.0F;
         }

         if (var18 > 1.0F) {
            var18 = 1.0F;
         }

         int var19 = (int)(var15 * 255.0F);
         int var20 = (int)(var16 * 255.0F);
         int var21 = (int)(var17 * 255.0F);
         int var22 = (int)(var18 * 255.0F);
         this.setPixelRGBA(var1, var2, FastColor.ABGR32.color(var19, var20, var21, var22));
      }
   }

   /** @deprecated */
   @Deprecated
   public int[] makePixelArray() {
      if (this.format != NativeImage.Format.RGBA) {
         throw new UnsupportedOperationException("can only call makePixelArray for RGBA images.");
      } else {
         this.checkAllocated();
         int[] var1 = new int[this.getWidth() * this.getHeight()];

         for(int var2 = 0; var2 < this.getHeight(); ++var2) {
            for(int var3 = 0; var3 < this.getWidth(); ++var3) {
               int var4 = this.getPixelRGBA(var3, var2);
               var1[var3 + var2 * this.getWidth()] = FastColor.ARGB32.color(FastColor.ABGR32.alpha(var4), FastColor.ABGR32.red(var4), FastColor.ABGR32.green(var4), FastColor.ABGR32.blue(var4));
            }
         }

         return var1;
      }
   }

   public void upload(int var1, int var2, int var3, boolean var4) {
      this.upload(var1, var2, var3, 0, 0, this.width, this.height, false, var4);
   }

   public void upload(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, boolean var9) {
      this.upload(var1, var2, var3, var4, var5, var6, var7, false, false, var8, var9);
   }

   public void upload(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, boolean var9, boolean var10, boolean var11) {
      if (!RenderSystem.isOnRenderThreadOrInit()) {
         RenderSystem.recordRenderCall(() -> {
            this._upload(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
         });
      } else {
         this._upload(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
      }

   }

   private void _upload(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, boolean var9, boolean var10, boolean var11) {
      try {
         RenderSystem.assertOnRenderThreadOrInit();
         this.checkAllocated();
         setFilter(var8, var10);
         if (var6 == this.getWidth()) {
            GlStateManager._pixelStore(3314, 0);
         } else {
            GlStateManager._pixelStore(3314, this.getWidth());
         }

         GlStateManager._pixelStore(3316, var4);
         GlStateManager._pixelStore(3315, var5);
         this.format.setUnpackPixelStoreState();
         GlStateManager._texSubImage2D(3553, var1, var2, var3, var6, var7, this.format.glFormat(), 5121, this.pixels);
         if (var9) {
            GlStateManager._texParameter(3553, 10242, 33071);
            GlStateManager._texParameter(3553, 10243, 33071);
         }
      } finally {
         if (var11) {
            this.close();
         }

      }

   }

   public void downloadTexture(int var1, boolean var2) {
      RenderSystem.assertOnRenderThread();
      this.checkAllocated();
      this.format.setPackPixelStoreState();
      GlStateManager._getTexImage(3553, var1, this.format.glFormat(), 5121, this.pixels);
      if (var2 && this.format.hasAlpha()) {
         for(int var3 = 0; var3 < this.getHeight(); ++var3) {
            for(int var4 = 0; var4 < this.getWidth(); ++var4) {
               this.setPixelRGBA(var4, var3, this.getPixelRGBA(var4, var3) | 255 << this.format.alphaOffset());
            }
         }
      }

   }

   public void downloadDepthBuffer(float var1) {
      RenderSystem.assertOnRenderThread();
      if (this.format.components() != 1) {
         throw new IllegalStateException("Depth buffer must be stored in NativeImage with 1 component.");
      } else {
         this.checkAllocated();
         this.format.setPackPixelStoreState();
         GlStateManager._readPixels(0, 0, this.width, this.height, 6402, 5121, this.pixels);
      }
   }

   public void drawPixels() {
      RenderSystem.assertOnRenderThread();
      this.format.setUnpackPixelStoreState();
      GlStateManager._glDrawPixels(this.width, this.height, this.format.glFormat(), 5121, this.pixels);
   }

   public void writeToFile(File var1) throws IOException {
      this.writeToFile(var1.toPath());
   }

   public void copyFromFont(FT_Face var1, int var2) {
      if (this.format.components() != 1) {
         throw new IllegalArgumentException("Can only write fonts into 1-component images.");
      } else {
         FreeTypeUtil.checkError(FreeType.FT_Load_Glyph(var1, var2, 4), "Loading glyph");
         FT_GlyphSlot var3 = (FT_GlyphSlot)Objects.requireNonNull(var1.glyph(), "Glyph not initialized");
         FT_Bitmap var4 = var3.bitmap();
         if (var4.pixel_mode() != 2) {
            throw new IllegalStateException("Rendered glyph was not 8-bit grayscale");
         } else if (var4.width() == this.getWidth() && var4.rows() == this.getHeight()) {
            int var5 = var4.width() * var4.rows();
            ByteBuffer var6 = (ByteBuffer)Objects.requireNonNull(var4.buffer(var5), "Glyph has no bitmap");
            MemoryUtil.memCopy(MemoryUtil.memAddress(var6), this.pixels, (long)var5);
         } else {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "Glyph bitmap of size %sx%s does not match image of size: %sx%s", var4.width(), var4.rows(), this.getWidth(), this.getHeight()));
         }
      }
   }

   public void writeToFile(Path var1) throws IOException {
      if (!this.format.supportedByStb()) {
         throw new UnsupportedOperationException("Don't know how to write format " + String.valueOf(this.format));
      } else {
         this.checkAllocated();
         SeekableByteChannel var2 = Files.newByteChannel(var1, OPEN_OPTIONS);

         try {
            if (!this.writeToChannel(var2)) {
               String var10002 = String.valueOf(var1.toAbsolutePath());
               throw new IOException("Could not write image to the PNG file \"" + var10002 + "\": " + STBImage.stbi_failure_reason());
            }
         } catch (Throwable var6) {
            if (var2 != null) {
               try {
                  var2.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (var2 != null) {
            var2.close();
         }

      }
   }

   public byte[] asByteArray() throws IOException {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();

      byte[] var3;
      try {
         WritableByteChannel var2 = Channels.newChannel(var1);

         try {
            if (!this.writeToChannel(var2)) {
               throw new IOException("Could not write image to byte array: " + STBImage.stbi_failure_reason());
            }

            var3 = var1.toByteArray();
         } catch (Throwable var7) {
            if (var2 != null) {
               try {
                  var2.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (var2 != null) {
            var2.close();
         }
      } catch (Throwable var8) {
         try {
            var1.close();
         } catch (Throwable var5) {
            var8.addSuppressed(var5);
         }

         throw var8;
      }

      var1.close();
      return var3;
   }

   private boolean writeToChannel(WritableByteChannel var1) throws IOException {
      WriteCallback var2 = new WriteCallback(var1);

      boolean var4;
      try {
         int var3 = Math.min(this.getHeight(), 2147483647 / this.getWidth() / this.format.components());
         if (var3 < this.getHeight()) {
            LOGGER.warn("Dropping image height from {} to {} to fit the size into 32-bit signed int", this.getHeight(), var3);
         }

         if (STBImageWrite.nstbi_write_png_to_func(var2.address(), 0L, this.getWidth(), var3, this.format.components(), this.pixels, 0) != 0) {
            var2.throwIfException();
            var4 = true;
            return var4;
         }

         var4 = false;
      } finally {
         var2.free();
      }

      return var4;
   }

   public void copyFrom(NativeImage var1) {
      if (var1.format() != this.format) {
         throw new UnsupportedOperationException("Image formats don't match.");
      } else {
         int var2 = this.format.components();
         this.checkAllocated();
         var1.checkAllocated();
         if (this.width == var1.width) {
            MemoryUtil.memCopy(var1.pixels, this.pixels, Math.min(this.size, var1.size));
         } else {
            int var3 = Math.min(this.getWidth(), var1.getWidth());
            int var4 = Math.min(this.getHeight(), var1.getHeight());

            for(int var5 = 0; var5 < var4; ++var5) {
               int var6 = var5 * var1.getWidth() * var2;
               int var7 = var5 * this.getWidth() * var2;
               MemoryUtil.memCopy(var1.pixels + (long)var6, this.pixels + (long)var7, (long)var3);
            }
         }

      }
   }

   public void fillRect(int var1, int var2, int var3, int var4, int var5) {
      for(int var6 = var2; var6 < var2 + var4; ++var6) {
         for(int var7 = var1; var7 < var1 + var3; ++var7) {
            this.setPixelRGBA(var7, var6, var5);
         }
      }

   }

   public void copyRect(int var1, int var2, int var3, int var4, int var5, int var6, boolean var7, boolean var8) {
      this.copyRect(this, var1, var2, var1 + var3, var2 + var4, var5, var6, var7, var8);
   }

   public void copyRect(NativeImage var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, boolean var9) {
      for(int var10 = 0; var10 < var7; ++var10) {
         for(int var11 = 0; var11 < var6; ++var11) {
            int var12 = var8 ? var6 - 1 - var11 : var11;
            int var13 = var9 ? var7 - 1 - var10 : var10;
            int var14 = this.getPixelRGBA(var2 + var11, var3 + var10);
            var1.setPixelRGBA(var4 + var12, var5 + var13, var14);
         }
      }

   }

   public void flipY() {
      this.checkAllocated();
      int var1 = this.format.components();
      int var2 = this.getWidth() * var1;
      long var3 = MemoryUtil.nmemAlloc((long)var2);

      try {
         for(int var5 = 0; var5 < this.getHeight() / 2; ++var5) {
            int var6 = var5 * this.getWidth() * var1;
            int var7 = (this.getHeight() - 1 - var5) * this.getWidth() * var1;
            MemoryUtil.memCopy(this.pixels + (long)var6, var3, (long)var2);
            MemoryUtil.memCopy(this.pixels + (long)var7, this.pixels + (long)var6, (long)var2);
            MemoryUtil.memCopy(var3, this.pixels + (long)var7, (long)var2);
         }
      } finally {
         MemoryUtil.nmemFree(var3);
      }

   }

   public void resizeSubRectTo(int var1, int var2, int var3, int var4, NativeImage var5) {
      this.checkAllocated();
      if (var5.format() != this.format) {
         throw new UnsupportedOperationException("resizeSubRectTo only works for images of the same format.");
      } else {
         int var6 = this.format.components();
         STBImageResize.nstbir_resize_uint8(this.pixels + (long)((var1 + var2 * this.getWidth()) * var6), var3, var4, this.getWidth() * var6, var5.pixels, var5.getWidth(), var5.getHeight(), 0, var6);
      }
   }

   public void untrack() {
      DebugMemoryUntracker.untrack(this.pixels);
   }

   static {
      OPEN_OPTIONS = EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
   }

   public static enum Format {
      RGBA(4, 6408, true, true, true, false, true, 0, 8, 16, 255, 24, true),
      RGB(3, 6407, true, true, true, false, false, 0, 8, 16, 255, 255, true),
      LUMINANCE_ALPHA(2, 33319, false, false, false, true, true, 255, 255, 255, 0, 8, true),
      LUMINANCE(1, 6403, false, false, false, true, false, 0, 0, 0, 0, 255, true);

      final int components;
      private final int glFormat;
      private final boolean hasRed;
      private final boolean hasGreen;
      private final boolean hasBlue;
      private final boolean hasLuminance;
      private final boolean hasAlpha;
      private final int redOffset;
      private final int greenOffset;
      private final int blueOffset;
      private final int luminanceOffset;
      private final int alphaOffset;
      private final boolean supportedByStb;

      private Format(int var3, int var4, boolean var5, boolean var6, boolean var7, boolean var8, boolean var9, int var10, int var11, int var12, int var13, int var14, boolean var15) {
         this.components = var3;
         this.glFormat = var4;
         this.hasRed = var5;
         this.hasGreen = var6;
         this.hasBlue = var7;
         this.hasLuminance = var8;
         this.hasAlpha = var9;
         this.redOffset = var10;
         this.greenOffset = var11;
         this.blueOffset = var12;
         this.luminanceOffset = var13;
         this.alphaOffset = var14;
         this.supportedByStb = var15;
      }

      public int components() {
         return this.components;
      }

      public void setPackPixelStoreState() {
         RenderSystem.assertOnRenderThread();
         GlStateManager._pixelStore(3333, this.components());
      }

      public void setUnpackPixelStoreState() {
         RenderSystem.assertOnRenderThreadOrInit();
         GlStateManager._pixelStore(3317, this.components());
      }

      public int glFormat() {
         return this.glFormat;
      }

      public boolean hasRed() {
         return this.hasRed;
      }

      public boolean hasGreen() {
         return this.hasGreen;
      }

      public boolean hasBlue() {
         return this.hasBlue;
      }

      public boolean hasLuminance() {
         return this.hasLuminance;
      }

      public boolean hasAlpha() {
         return this.hasAlpha;
      }

      public int redOffset() {
         return this.redOffset;
      }

      public int greenOffset() {
         return this.greenOffset;
      }

      public int blueOffset() {
         return this.blueOffset;
      }

      public int luminanceOffset() {
         return this.luminanceOffset;
      }

      public int alphaOffset() {
         return this.alphaOffset;
      }

      public boolean hasLuminanceOrRed() {
         return this.hasLuminance || this.hasRed;
      }

      public boolean hasLuminanceOrGreen() {
         return this.hasLuminance || this.hasGreen;
      }

      public boolean hasLuminanceOrBlue() {
         return this.hasLuminance || this.hasBlue;
      }

      public boolean hasLuminanceOrAlpha() {
         return this.hasLuminance || this.hasAlpha;
      }

      public int luminanceOrRedOffset() {
         return this.hasLuminance ? this.luminanceOffset : this.redOffset;
      }

      public int luminanceOrGreenOffset() {
         return this.hasLuminance ? this.luminanceOffset : this.greenOffset;
      }

      public int luminanceOrBlueOffset() {
         return this.hasLuminance ? this.luminanceOffset : this.blueOffset;
      }

      public int luminanceOrAlphaOffset() {
         return this.hasLuminance ? this.luminanceOffset : this.alphaOffset;
      }

      public boolean supportedByStb() {
         return this.supportedByStb;
      }

      static Format getStbFormat(int var0) {
         switch (var0) {
            case 1:
               return LUMINANCE;
            case 2:
               return LUMINANCE_ALPHA;
            case 3:
               return RGB;
            case 4:
            default:
               return RGBA;
         }
      }

      // $FF: synthetic method
      private static Format[] $values() {
         return new Format[]{RGBA, RGB, LUMINANCE_ALPHA, LUMINANCE};
      }
   }

   private static class WriteCallback extends STBIWriteCallback {
      private final WritableByteChannel output;
      @Nullable
      private IOException exception;

      WriteCallback(WritableByteChannel var1) {
         super();
         this.output = var1;
      }

      public void invoke(long var1, long var3, int var5) {
         ByteBuffer var6 = getData(var3, var5);

         try {
            this.output.write(var6);
         } catch (IOException var8) {
            this.exception = var8;
         }

      }

      public void throwIfException() throws IOException {
         if (this.exception != null) {
            throw this.exception;
         }
      }
   }

   public static enum InternalGlFormat {
      RGBA(6408),
      RGB(6407),
      RG(33319),
      RED(6403);

      private final int glFormat;

      private InternalGlFormat(int var3) {
         this.glFormat = var3;
      }

      public int glFormat() {
         return this.glFormat;
      }

      // $FF: synthetic method
      private static InternalGlFormat[] $values() {
         return new InternalGlFormat[]{RGBA, RGB, RG, RED};
      }
   }
}
