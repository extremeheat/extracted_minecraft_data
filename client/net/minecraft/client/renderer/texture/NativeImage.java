package net.minecraft.client.renderer.texture;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.util.LWJGLMemoryUntracker;
import org.apache.commons.io.IOUtils;
import org.lwjgl.stb.STBIWriteCallback;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageResize;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public final class NativeImage implements AutoCloseable {
   private static final Set<StandardOpenOption> field_209272_a;
   private final NativeImage.PixelFormat field_211680_b;
   private final int field_195719_a;
   private final int field_195720_b;
   private final boolean field_195721_c;
   private long field_195722_d;
   private final int field_195723_e;

   public NativeImage(int var1, int var2, boolean var3) {
      this(NativeImage.PixelFormat.RGBA, var1, var2, var3);
   }

   public NativeImage(NativeImage.PixelFormat var1, int var2, int var3, boolean var4) {
      super();
      this.field_211680_b = var1;
      this.field_195719_a = var2;
      this.field_195720_b = var3;
      this.field_195723_e = var2 * var3 * var1.func_211651_a();
      this.field_195721_c = false;
      if (var4) {
         this.field_195722_d = MemoryUtil.nmemCalloc(1L, (long)this.field_195723_e);
      } else {
         this.field_195722_d = MemoryUtil.nmemAlloc((long)this.field_195723_e);
      }

   }

   private NativeImage(NativeImage.PixelFormat var1, int var2, int var3, boolean var4, long var5) {
      super();
      this.field_211680_b = var1;
      this.field_195719_a = var2;
      this.field_195720_b = var3;
      this.field_195721_c = var4;
      this.field_195722_d = var5;
      this.field_195723_e = var2 * var3 * var1.func_211651_a();
   }

   public String toString() {
      return "NativeImage[" + this.field_211680_b + " " + this.field_195719_a + "x" + this.field_195720_b + "@" + this.field_195722_d + (this.field_195721_c ? "S" : "N") + "]";
   }

   public static NativeImage func_195713_a(InputStream var0) throws IOException {
      return func_211679_a(NativeImage.PixelFormat.RGBA, var0);
   }

   public static NativeImage func_211679_a(@Nullable NativeImage.PixelFormat var0, InputStream var1) throws IOException {
      ByteBuffer var2 = null;

      NativeImage var3;
      try {
         var2 = TextureUtil.func_195724_a(var1);
         var2.rewind();
         var3 = func_211677_a(var0, var2);
      } finally {
         MemoryUtil.memFree(var2);
         IOUtils.closeQuietly(var1);
      }

      return var3;
   }

   public static NativeImage func_195704_a(ByteBuffer var0) throws IOException {
      return func_211677_a(NativeImage.PixelFormat.RGBA, var0);
   }

   public static NativeImage func_211677_a(@Nullable NativeImage.PixelFormat var0, ByteBuffer var1) throws IOException {
      if (var0 != null && !var0.func_211654_w()) {
         throw new UnsupportedOperationException("Don't know how to read format " + var0);
      } else if (MemoryUtil.memAddress(var1) == 0L) {
         throw new IllegalArgumentException("Invalid buffer");
      } else {
         MemoryStack var2 = MemoryStack.stackPush();
         Throwable var3 = null;

         NativeImage var8;
         try {
            IntBuffer var4 = var2.mallocInt(1);
            IntBuffer var5 = var2.mallocInt(1);
            IntBuffer var6 = var2.mallocInt(1);
            ByteBuffer var7 = STBImage.stbi_load_from_memory(var1, var4, var5, var6, var0 == null ? 0 : var0.field_211659_e);
            if (var7 == null) {
               throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
            }

            var8 = new NativeImage(var0 == null ? NativeImage.PixelFormat.func_211646_b(var6.get(0)) : var0, var4.get(0), var5.get(0), true, MemoryUtil.memAddress(var7));
         } catch (Throwable var17) {
            var3 = var17;
            throw var17;
         } finally {
            if (var2 != null) {
               if (var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var16) {
                     var3.addSuppressed(var16);
                  }
               } else {
                  var2.close();
               }
            }

         }

         return var8;
      }
   }

   private static void func_195707_b(boolean var0) {
      if (var0) {
         GlStateManager.func_187421_b(3553, 10242, 10496);
         GlStateManager.func_187421_b(3553, 10243, 10496);
      } else {
         GlStateManager.func_187421_b(3553, 10242, 10497);
         GlStateManager.func_187421_b(3553, 10243, 10497);
      }

   }

   private static void func_195705_a(boolean var0, boolean var1) {
      if (var0) {
         GlStateManager.func_187421_b(3553, 10241, var1 ? 9987 : 9729);
         GlStateManager.func_187421_b(3553, 10240, 9729);
      } else {
         GlStateManager.func_187421_b(3553, 10241, var1 ? 9986 : 9728);
         GlStateManager.func_187421_b(3553, 10240, 9728);
      }

   }

   private void func_195696_g() {
      if (this.field_195722_d == 0L) {
         throw new IllegalStateException("Image is not allocated.");
      }
   }

   public void close() {
      if (this.field_195722_d != 0L) {
         if (this.field_195721_c) {
            STBImage.nstbi_image_free(this.field_195722_d);
         } else {
            MemoryUtil.nmemFree(this.field_195722_d);
         }
      }

      this.field_195722_d = 0L;
   }

   public int func_195702_a() {
      return this.field_195719_a;
   }

   public int func_195714_b() {
      return this.field_195720_b;
   }

   public NativeImage.PixelFormat func_211678_c() {
      return this.field_211680_b;
   }

   public int func_195709_a(int var1, int var2) {
      if (this.field_211680_b != NativeImage.PixelFormat.RGBA) {
         throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", this.field_211680_b));
      } else if (var1 <= this.field_195719_a && var2 <= this.field_195720_b) {
         this.func_195696_g();
         return MemoryUtil.memIntBuffer(this.field_195722_d, this.field_195723_e).get(var1 + var2 * this.field_195719_a);
      } else {
         throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", var1, var2, this.field_195719_a, this.field_195720_b));
      }
   }

   public void func_195700_a(int var1, int var2, int var3) {
      if (this.field_211680_b != NativeImage.PixelFormat.RGBA) {
         throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", this.field_211680_b));
      } else if (var1 <= this.field_195719_a && var2 <= this.field_195720_b) {
         this.func_195696_g();
         MemoryUtil.memIntBuffer(this.field_195722_d, this.field_195723_e).put(var1 + var2 * this.field_195719_a, var3);
      } else {
         throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", var1, var2, this.field_195719_a, this.field_195720_b));
      }
   }

   public byte func_211675_e(int var1, int var2) {
      if (!this.field_211680_b.func_211653_r()) {
         throw new IllegalArgumentException(String.format("no luminance or alpha in %s", this.field_211680_b));
      } else if (var1 <= this.field_195719_a && var2 <= this.field_195720_b) {
         return MemoryUtil.memByteBuffer(this.field_195722_d, this.field_195723_e).get((var1 + var2 * this.field_195719_a) * this.field_211680_b.func_211651_a() + this.field_211680_b.func_211647_v() / 8);
      } else {
         throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", var1, var2, this.field_195719_a, this.field_195720_b));
      }
   }

   public void func_195718_b(int var1, int var2, int var3) {
      if (this.field_211680_b != NativeImage.PixelFormat.RGBA) {
         throw new UnsupportedOperationException("Can only call blendPixel with RGBA format");
      } else {
         int var4 = this.func_195709_a(var1, var2);
         float var5 = (float)(var3 >> 24 & 255) / 255.0F;
         float var6 = (float)(var3 >> 16 & 255) / 255.0F;
         float var7 = (float)(var3 >> 8 & 255) / 255.0F;
         float var8 = (float)(var3 >> 0 & 255) / 255.0F;
         float var9 = (float)(var4 >> 24 & 255) / 255.0F;
         float var10 = (float)(var4 >> 16 & 255) / 255.0F;
         float var11 = (float)(var4 >> 8 & 255) / 255.0F;
         float var12 = (float)(var4 >> 0 & 255) / 255.0F;
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
         this.func_195700_a(var1, var2, var19 << 24 | var20 << 16 | var21 << 8 | var22 << 0);
      }
   }

   @Deprecated
   public int[] func_195716_c() {
      if (this.field_211680_b != NativeImage.PixelFormat.RGBA) {
         throw new UnsupportedOperationException("can only call makePixelArray for RGBA images.");
      } else {
         this.func_195696_g();
         int[] var1 = new int[this.func_195702_a() * this.func_195714_b()];

         for(int var2 = 0; var2 < this.func_195714_b(); ++var2) {
            for(int var3 = 0; var3 < this.func_195702_a(); ++var3) {
               int var4 = this.func_195709_a(var3, var2);
               int var5 = var4 >> 24 & 255;
               int var6 = var4 >> 16 & 255;
               int var7 = var4 >> 8 & 255;
               int var8 = var4 >> 0 & 255;
               int var9 = var5 << 24 | var8 << 16 | var7 << 8 | var6;
               var1[var3 + var2 * this.func_195702_a()] = var9;
            }
         }

         return var1;
      }
   }

   public void func_195697_a(int var1, int var2, int var3, boolean var4) {
      this.func_195706_a(var1, var2, var3, 0, 0, this.field_195719_a, this.field_195720_b, var4);
   }

   public void func_195706_a(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8) {
      this.func_195712_a(var1, var2, var3, var4, var5, var6, var7, false, false, var8);
   }

   public void func_195712_a(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, boolean var9, boolean var10) {
      this.func_195696_g();
      func_195705_a(var8, var10);
      func_195707_b(var9);
      if (var6 == this.func_195702_a()) {
         GlStateManager.func_187425_g(3314, 0);
      } else {
         GlStateManager.func_187425_g(3314, this.func_195702_a());
      }

      GlStateManager.func_187425_g(3316, var4);
      GlStateManager.func_187425_g(3315, var5);
      this.field_211680_b.func_211658_c();
      GlStateManager.func_199298_a(3553, var1, var2, var3, var6, var7, this.field_211680_b.func_211650_d(), 5121, this.field_195722_d);
   }

   public void func_195717_a(int var1, boolean var2) {
      this.func_195696_g();
      this.field_211680_b.func_211656_b();
      GlStateManager.func_199295_a(3553, var1, this.field_211680_b.func_211650_d(), 5121, this.field_195722_d);
      if (var2 && this.field_211680_b.func_211645_i()) {
         for(int var3 = 0; var3 < this.func_195714_b(); ++var3) {
            for(int var4 = 0; var4 < this.func_195702_a(); ++var4) {
               this.func_195700_a(var4, var3, this.func_195709_a(var4, var3) | 255 << this.field_211680_b.func_211648_n());
            }
         }
      }

   }

   public void func_195701_a(boolean var1) {
      this.func_195696_g();
      this.field_211680_b.func_211656_b();
      if (var1) {
         GlStateManager.func_199297_b(3357, 3.4028235E38F);
      }

      GlStateManager.func_199296_a(0, 0, this.field_195719_a, this.field_195720_b, this.field_211680_b.func_211650_d(), 5121, this.field_195722_d);
      if (var1) {
         GlStateManager.func_199297_b(3357, 0.0F);
      }

   }

   public void func_209271_a(File var1) throws IOException {
      this.func_209270_a(var1.toPath());
   }

   public void func_211676_a(STBTTFontinfo var1, int var2, int var3, int var4, float var5, float var6, float var7, float var8, int var9, int var10) {
      if (var9 >= 0 && var9 + var3 <= this.func_195702_a() && var10 >= 0 && var10 + var4 <= this.func_195714_b()) {
         if (this.field_211680_b.func_211651_a() != 1) {
            throw new IllegalArgumentException("Can only write fonts into 1-component images.");
         } else {
            STBTruetype.nstbtt_MakeGlyphBitmapSubpixel(var1.address(), this.field_195722_d + (long)var9 + (long)(var10 * this.func_195702_a()), var3, var4, this.func_195702_a(), var5, var6, var7, var8, var2);
         }
      } else {
         throw new IllegalArgumentException(String.format("Out of bounds: start: (%s, %s) (size: %sx%s); size: %sx%s", var9, var10, var3, var4, this.func_195702_a(), this.func_195714_b()));
      }
   }

   public void func_209270_a(Path var1) throws IOException {
      if (!this.field_211680_b.func_211654_w()) {
         throw new UnsupportedOperationException("Don't know how to write format " + this.field_211680_b);
      } else {
         this.func_195696_g();
         SeekableByteChannel var2 = Files.newByteChannel(var1, field_209272_a);
         Throwable var3 = null;

         try {
            NativeImage.WriteCallback var4 = new NativeImage.WriteCallback(var2);

            try {
               if (!STBImageWrite.stbi_write_png_to_func(var4, 0L, this.func_195702_a(), this.func_195714_b(), this.field_211680_b.func_211651_a(), MemoryUtil.memByteBuffer(this.field_195722_d, this.field_195723_e), 0)) {
                  throw new IOException("Could not write image to the PNG file \"" + var1.toAbsolutePath() + "\": " + STBImage.stbi_failure_reason());
               }
            } finally {
               var4.free();
            }

            var4.func_209267_a();
         } catch (Throwable var19) {
            var3 = var19;
            throw var19;
         } finally {
            if (var2 != null) {
               if (var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var17) {
                     var3.addSuppressed(var17);
                  }
               } else {
                  var2.close();
               }
            }

         }

      }
   }

   public void func_195703_a(NativeImage var1) {
      if (var1.func_211678_c() != this.field_211680_b) {
         throw new UnsupportedOperationException("Image formats don't match.");
      } else {
         int var2 = this.field_211680_b.func_211651_a();
         this.func_195696_g();
         var1.func_195696_g();
         if (this.field_195719_a == var1.field_195719_a) {
            MemoryUtil.memCopy(var1.field_195722_d, this.field_195722_d, (long)Math.min(this.field_195723_e, var1.field_195723_e));
         } else {
            int var3 = Math.min(this.func_195702_a(), var1.func_195702_a());
            int var4 = Math.min(this.func_195714_b(), var1.func_195714_b());

            for(int var5 = 0; var5 < var4; ++var5) {
               int var6 = var5 * var1.func_195702_a() * var2;
               int var7 = var5 * this.func_195702_a() * var2;
               MemoryUtil.memCopy(var1.field_195722_d + (long)var6, this.field_195722_d + (long)var7, (long)var3);
            }
         }

      }
   }

   public void func_195715_a(int var1, int var2, int var3, int var4, int var5) {
      for(int var6 = var2; var6 < var2 + var4; ++var6) {
         for(int var7 = var1; var7 < var1 + var3; ++var7) {
            this.func_195700_a(var7, var6, var5);
         }
      }

   }

   public void func_195699_a(int var1, int var2, int var3, int var4, int var5, int var6, boolean var7, boolean var8) {
      for(int var9 = 0; var9 < var6; ++var9) {
         for(int var10 = 0; var10 < var5; ++var10) {
            int var11 = var7 ? var5 - 1 - var10 : var10;
            int var12 = var8 ? var6 - 1 - var9 : var9;
            int var13 = this.func_195709_a(var1 + var10, var2 + var9);
            this.func_195700_a(var1 + var3 + var11, var2 + var4 + var12, var13);
         }
      }

   }

   public void func_195710_e() {
      this.func_195696_g();
      MemoryStack var1 = MemoryStack.stackPush();
      Throwable var2 = null;

      try {
         int var3 = this.field_211680_b.func_211651_a();
         int var4 = this.func_195702_a() * var3;
         long var5 = var1.nmalloc(var4);

         for(int var7 = 0; var7 < this.func_195714_b() / 2; ++var7) {
            int var8 = var7 * this.func_195702_a() * var3;
            int var9 = (this.func_195714_b() - 1 - var7) * this.func_195702_a() * var3;
            MemoryUtil.memCopy(this.field_195722_d + (long)var8, var5, (long)var4);
            MemoryUtil.memCopy(this.field_195722_d + (long)var9, this.field_195722_d + (long)var8, (long)var4);
            MemoryUtil.memCopy(var5, this.field_195722_d + (long)var9, (long)var4);
         }
      } catch (Throwable var17) {
         var2 = var17;
         throw var17;
      } finally {
         if (var1 != null) {
            if (var2 != null) {
               try {
                  var1.close();
               } catch (Throwable var16) {
                  var2.addSuppressed(var16);
               }
            } else {
               var1.close();
            }
         }

      }

   }

   public void func_195708_a(int var1, int var2, int var3, int var4, NativeImage var5) {
      this.func_195696_g();
      if (var5.func_211678_c() != this.field_211680_b) {
         throw new UnsupportedOperationException("resizeSubRectTo only works for images of the same format.");
      } else {
         int var6 = this.field_211680_b.func_211651_a();
         STBImageResize.nstbir_resize_uint8(this.field_195722_d + (long)((var1 + var2 * this.func_195702_a()) * var6), var3, var4, this.func_195702_a() * var6, var5.field_195722_d, var5.func_195702_a(), var5.func_195714_b(), 0, var6);
      }
   }

   public void func_195711_f() {
      LWJGLMemoryUntracker.func_197933_a(this.field_195722_d);
   }

   static {
      field_209272_a = EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
   }

   public static enum PixelFormat {
      RGBA(4, 6408, true, true, true, false, true, 0, 8, 16, 255, 24, true),
      RGB(3, 6407, true, true, true, false, false, 0, 8, 16, 255, 255, true),
      LUMINANCE_ALPHA(2, 6410, false, false, false, true, true, 255, 255, 255, 0, 8, true),
      LUMINANCE(1, 6409, false, false, false, true, false, 0, 0, 0, 0, 255, true);

      private final int field_211659_e;
      private final int field_211660_f;
      private final boolean field_211661_g;
      private final boolean field_211662_h;
      private final boolean field_211663_i;
      private final boolean field_211664_j;
      private final boolean field_211665_k;
      private final int field_211666_l;
      private final int field_211667_m;
      private final int field_211668_n;
      private final int field_211669_o;
      private final int field_211670_p;
      private final boolean field_211671_q;

      private PixelFormat(int var3, int var4, boolean var5, boolean var6, boolean var7, boolean var8, boolean var9, int var10, int var11, int var12, int var13, int var14, boolean var15) {
         this.field_211659_e = var3;
         this.field_211660_f = var4;
         this.field_211661_g = var5;
         this.field_211662_h = var6;
         this.field_211663_i = var7;
         this.field_211664_j = var8;
         this.field_211665_k = var9;
         this.field_211666_l = var10;
         this.field_211667_m = var11;
         this.field_211668_n = var12;
         this.field_211669_o = var13;
         this.field_211670_p = var14;
         this.field_211671_q = var15;
      }

      public int func_211651_a() {
         return this.field_211659_e;
      }

      public void func_211656_b() {
         GlStateManager.func_187425_g(3333, this.func_211651_a());
      }

      public void func_211658_c() {
         GlStateManager.func_187425_g(3317, this.func_211651_a());
      }

      public int func_211650_d() {
         return this.field_211660_f;
      }

      public boolean func_211645_i() {
         return this.field_211665_k;
      }

      public int func_211648_n() {
         return this.field_211670_p;
      }

      public boolean func_211653_r() {
         return this.field_211664_j || this.field_211665_k;
      }

      public int func_211647_v() {
         return this.field_211664_j ? this.field_211669_o : this.field_211670_p;
      }

      public boolean func_211654_w() {
         return this.field_211671_q;
      }

      private static NativeImage.PixelFormat func_211646_b(int var0) {
         switch(var0) {
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
   }

   public static enum PixelFormatGLCode {
      RGBA(6408),
      RGB(6407),
      LUMINANCE_ALPHA(6410),
      LUMINANCE(6409),
      INTENSITY(32841);

      private final int field_211673_f;

      private PixelFormatGLCode(int var3) {
         this.field_211673_f = var3;
      }

      int func_211672_a() {
         return this.field_211673_f;
      }
   }

   static class WriteCallback extends STBIWriteCallback {
      private final WritableByteChannel field_209268_a;
      private IOException field_209269_b;

      private WriteCallback(WritableByteChannel var1) {
         super();
         this.field_209268_a = var1;
      }

      public void invoke(long var1, long var3, int var5) {
         ByteBuffer var6 = getData(var3, var5);

         try {
            this.field_209268_a.write(var6);
         } catch (IOException var8) {
            this.field_209269_b = var8;
         }

      }

      public void func_209267_a() throws IOException {
         if (this.field_209269_b != null) {
            throw this.field_209269_b;
         }
      }

      // $FF: synthetic method
      WriteCallback(WritableByteChannel var1, Object var2) {
         this(var1);
      }
   }
}
