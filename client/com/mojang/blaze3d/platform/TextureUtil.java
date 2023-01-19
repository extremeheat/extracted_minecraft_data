package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.DontObfuscate;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.SharedConstants;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

@DontObfuscate
public class TextureUtil {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int MIN_MIPMAP_LEVEL = 0;
   private static final int DEFAULT_IMAGE_BUFFER_SIZE = 8192;

   public TextureUtil() {
      super();
   }

   public static int generateTextureId() {
      RenderSystem.assertOnRenderThreadOrInit();
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         int[] var0 = new int[ThreadLocalRandom.current().nextInt(15) + 1];
         GlStateManager._genTextures(var0);
         int var1 = GlStateManager._genTexture();
         GlStateManager._deleteTextures(var0);
         return var1;
      } else {
         return GlStateManager._genTexture();
      }
   }

   public static void releaseTextureId(int var0) {
      RenderSystem.assertOnRenderThreadOrInit();
      GlStateManager._deleteTexture(var0);
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
      RenderSystem.assertOnRenderThreadOrInit();
      bind(var1);
      if (var2 >= 0) {
         GlStateManager._texParameter(3553, 33085, var2);
         GlStateManager._texParameter(3553, 33082, 0);
         GlStateManager._texParameter(3553, 33083, var2);
         GlStateManager._texParameter(3553, 34049, 0.0F);
      }

      for(int var5 = 0; var5 <= var2; ++var5) {
         GlStateManager._texImage2D(3553, var5, var0.glFormat(), var3 >> var5, var4 >> var5, 0, 6408, 5121, null);
      }
   }

   private static void bind(int var0) {
      RenderSystem.assertOnRenderThreadOrInit();
      GlStateManager._bindTexture(var0);
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static ByteBuffer readResource(InputStream var0) throws IOException {
      ByteBuffer var1;
      if (var0 instanceof FileInputStream var2) {
         FileChannel var3 = var2.getChannel();
         var1 = MemoryUtil.memAlloc((int)var3.size() + 1);

         while(var3.read(var1) != -1) {
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

   public static void writeAsPNG(String var0, int var1, int var2, int var3, int var4) {
      RenderSystem.assertOnRenderThread();
      bind(var1);

      for(int var5 = 0; var5 <= var2; ++var5) {
         String var6 = var0 + "_" + var5 + ".png";
         int var7 = var3 >> var5;
         int var8 = var4 >> var5;

         try (NativeImage var9 = new NativeImage(var7, var8, false)) {
            var9.downloadTexture(var5, false);
            var9.writeToFile(var6);
            LOGGER.debug("Exported png to: {}", new File(var6).getAbsolutePath());
         } catch (IOException var14) {
            LOGGER.debug("Unable to write: ", var14);
         }
      }
   }

   public static void initTexture(IntBuffer var0, int var1, int var2) {
      RenderSystem.assertOnRenderThread();
      GL11.glPixelStorei(3312, 0);
      GL11.glPixelStorei(3313, 0);
      GL11.glPixelStorei(3314, 0);
      GL11.glPixelStorei(3315, 0);
      GL11.glPixelStorei(3316, 0);
      GL11.glPixelStorei(3317, 4);
      GL11.glTexImage2D(3553, 0, 6408, var1, var2, 0, 32993, 33639, var0);
      GL11.glTexParameteri(3553, 10240, 9728);
      GL11.glTexParameteri(3553, 10241, 9729);
   }
}
