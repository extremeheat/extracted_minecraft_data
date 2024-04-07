package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.DontObfuscate;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Path;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntUnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
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

      for (int var5 = 0; var5 <= var2; var5++) {
         GlStateManager._texImage2D(3553, var5, var0.glFormat(), var3 >> var5, var4 >> var5, 0, 6408, 5121, null);
      }
   }

   private static void bind(int var0) {
      RenderSystem.assertOnRenderThreadOrInit();
      GlStateManager._bindTexture(var0);
   }

   public static ByteBuffer readResource(InputStream var0) throws IOException {
      ReadableByteChannel var1 = Channels.newChannel(var0);
      return var1 instanceof SeekableByteChannel var2 ? readResource(var1, (int)var2.size() + 1) : readResource(var1, 8192);
   }

   private static ByteBuffer readResource(ReadableByteChannel var0, int var1) throws IOException {
      ByteBuffer var2 = MemoryUtil.memAlloc(var1);

      try {
         while (var0.read(var2) != -1) {
            if (!var2.hasRemaining()) {
               var2 = MemoryUtil.memRealloc(var2, var2.capacity() * 2);
            }
         }

         return var2;
      } catch (IOException var4) {
         MemoryUtil.memFree(var2);
         throw var4;
      }
   }

   public static void writeAsPNG(Path var0, String var1, int var2, int var3, int var4, int var5) {
      writeAsPNG(var0, var1, var2, var3, var4, var5, null);
   }

   public static void writeAsPNG(Path var0, String var1, int var2, int var3, int var4, int var5, @Nullable IntUnaryOperator var6) {
      RenderSystem.assertOnRenderThread();
      bind(var2);

      for (int var7 = 0; var7 <= var3; var7++) {
         int var8 = var4 >> var7;
         int var9 = var5 >> var7;

         try (NativeImage var10 = new NativeImage(var8, var9, false)) {
            var10.downloadTexture(var7, false);
            if (var6 != null) {
               var10.applyToAllPixels(var6);
            }

            Path var11 = var0.resolve(var1 + "_" + var7 + ".png");
            var10.writeToFile(var11);
            LOGGER.debug("Exported png to: {}", var11.toAbsolutePath());
         } catch (IOException var15) {
            LOGGER.debug("Unable to write: ", var15);
         }
      }
   }

   public static Path getDebugTexturePath(Path var0) {
      return var0.resolve("screenshots").resolve("debug");
   }

   public static Path getDebugTexturePath() {
      return getDebugTexturePath(Path.of("."));
   }
}
