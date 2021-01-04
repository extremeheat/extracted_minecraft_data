package com.mojang.blaze3d.platform;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

public class TextureUtil {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final int MIN_MIPMAP_LEVEL = 0;
   private static final int DEFAULT_IMAGE_BUFFER_SIZE = 8192;

   public TextureUtil() {
      super();
   }

   public static int generateTextureId() {
      return GlStateManager.genTexture();
   }

   public static void releaseTextureId(int var0) {
      GlStateManager.deleteTexture(var0);
   }

   public static void prepareImage(int var0, int var1, int var2) {
      prepareImage(NativeImage.InternalGlFormat.RGBA, var0, 0, var1, var2);
   }

   public static void prepareImage(NativeImage.InternalGlFormat var0, int var1, int var2, int var3) {
      prepareImage(var0, var1, 0, var2, var3);
   }

   public static void prepareImage(int var0, int var1, int var2, int var3) {
      prepareImage(NativeImage.InternalGlFormat.RGBA, var0, var1, var2, var3);
   }

   public static void prepareImage(NativeImage.InternalGlFormat var0, int var1, int var2, int var3, int var4) {
      bind(var1);
      if (var2 >= 0) {
         GlStateManager.texParameter(3553, 33085, var2);
         GlStateManager.texParameter(3553, 33082, 0);
         GlStateManager.texParameter(3553, 33083, var2);
         GlStateManager.texParameter(3553, 34049, 0.0F);
      }

      for(int var5 = 0; var5 <= var2; ++var5) {
         GlStateManager.texImage2D(3553, var5, var0.glFormat(), var3 >> var5, var4 >> var5, 0, 6408, 5121, (IntBuffer)null);
      }

   }

   private static void bind(int var0) {
      GlStateManager.bindTexture(var0);
   }

   public static ByteBuffer readResource(InputStream var0) throws IOException {
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

   public static String readResourceAsString(InputStream var0) {
      ByteBuffer var1 = null;

      try {
         var1 = readResource(var0);
         int var2 = var1.position();
         var1.rewind();
         String var3 = MemoryUtil.memASCII(var1, var2);
         return var3;
      } catch (IOException var7) {
      } finally {
         if (var1 != null) {
            MemoryUtil.memFree(var1);
         }

      }

      return null;
   }

   public static void writeAsPNG(String var0, int var1, int var2, int var3, int var4) {
      bind(var1);

      for(int var5 = 0; var5 <= var2; ++var5) {
         String var6 = var0 + "_" + var5 + ".png";
         int var7 = var3 >> var5;
         int var8 = var4 >> var5;

         try {
            NativeImage var9 = new NativeImage(var7, var8, false);
            Throwable var10 = null;

            try {
               var9.downloadTexture(var5, false);
               var9.writeToFile(var6);
               LOGGER.debug("Exported png to: {}", (new File(var6)).getAbsolutePath());
            } catch (Throwable var20) {
               var10 = var20;
               throw var20;
            } finally {
               if (var9 != null) {
                  if (var10 != null) {
                     try {
                        var9.close();
                     } catch (Throwable var19) {
                        var10.addSuppressed(var19);
                     }
                  } else {
                     var9.close();
                  }
               }

            }
         } catch (IOException var22) {
            LOGGER.debug("Unable to write: ", var22);
         }
      }

   }

   public static void initTexture(IntBuffer var0, int var1, int var2) {
      GL11.glPixelStorei(3312, 0);
      GL11.glPixelStorei(3313, 0);
      GL11.glPixelStorei(3314, 0);
      GL11.glPixelStorei(3315, 0);
      GL11.glPixelStorei(3316, 0);
      GL11.glPixelStorei(3317, 4);
      GL11.glTexImage2D(3553, 0, 6408, var1, var2, 0, 32993, 33639, var0);
      GL11.glTexParameteri(3553, 10242, 10497);
      GL11.glTexParameteri(3553, 10243, 10497);
      GL11.glTexParameteri(3553, 10240, 9728);
      GL11.glTexParameteri(3553, 10241, 9729);
   }
}
