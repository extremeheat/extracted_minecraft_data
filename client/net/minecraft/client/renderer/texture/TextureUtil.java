package net.minecraft.client.renderer.texture;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryUtil;

public class TextureUtil {
   private static final Logger field_147959_c = LogManager.getLogger();

   public static int func_110996_a() {
      return GlStateManager.func_179146_y();
   }

   public static void func_147942_a(int var0) {
      GlStateManager.func_179150_h(var0);
   }

   public static void func_110991_a(int var0, int var1, int var2) {
      func_211682_a(NativeImage.PixelFormatGLCode.RGBA, var0, 0, var1, var2);
   }

   public static void func_211681_a(NativeImage.PixelFormatGLCode var0, int var1, int var2, int var3) {
      func_211682_a(var0, var1, 0, var2, var3);
   }

   public static void func_180600_a(int var0, int var1, int var2, int var3) {
      func_211682_a(NativeImage.PixelFormatGLCode.RGBA, var0, var1, var2, var3);
   }

   public static void func_211682_a(NativeImage.PixelFormatGLCode var0, int var1, int var2, int var3, int var4) {
      func_94277_a(var1);
      if (var2 >= 0) {
         GlStateManager.func_187421_b(3553, 33085, var2);
         GlStateManager.func_187421_b(3553, 33082, 0);
         GlStateManager.func_187421_b(3553, 33083, var2);
         GlStateManager.func_187403_b(3553, 34049, 0.0F);
      }

      for(int var5 = 0; var5 <= var2; ++var5) {
         GlStateManager.func_187419_a(3553, var5, var0.func_211672_a(), var3 >> var5, var4 >> var5, 0, 6408, 5121, (IntBuffer)null);
      }

   }

   private static void func_94277_a(int var0) {
      GlStateManager.func_179144_i(var0);
   }

   @Deprecated
   public static int[] func_195725_a(IResourceManager var0, ResourceLocation var1) throws IOException {
      IResource var2 = var0.func_199002_a(var1);
      Throwable var3 = null;

      Object var6;
      try {
         NativeImage var4 = NativeImage.func_195713_a(var2.func_199027_b());
         Throwable var5 = null;

         try {
            var6 = var4.func_195716_c();
         } catch (Throwable var29) {
            var6 = var29;
            var5 = var29;
            throw var29;
         } finally {
            if (var4 != null) {
               if (var5 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var28) {
                     var5.addSuppressed(var28);
                  }
               } else {
                  var4.close();
               }
            }

         }
      } catch (Throwable var31) {
         var3 = var31;
         throw var31;
      } finally {
         if (var2 != null) {
            if (var3 != null) {
               try {
                  var2.close();
               } catch (Throwable var27) {
                  var3.addSuppressed(var27);
               }
            } else {
               var2.close();
            }
         }

      }

      return (int[])var6;
   }

   public static ByteBuffer func_195724_a(InputStream var0) throws IOException {
      ByteBuffer var1;
      if (var0 instanceof FileInputStream) {
         FileInputStream var2 = (FileInputStream)var0;
         FileChannel var3 = var2.getChannel();
         var1 = MemoryUtil.memAlloc((int)var3.size() + 1);

         while(true) {
            if (var3.read(var1) != -1) {
               continue;
            }
         }
      } else {
         var1 = MemoryUtil.memAlloc(8192);
         ReadableByteChannel var4 = Channels.newChannel(var0);

         while(var4.read(var1) != -1) {
            if (var1.remaining() == 0) {
               var1 = MemoryUtil.memRealloc(var1, var1.capacity() * 2);
            }
         }
      }

      return var1;
   }
}
