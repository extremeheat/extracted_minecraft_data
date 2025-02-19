package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.DontObfuscate;
import com.mojang.blaze3d.buffers.BufferType;
import com.mojang.blaze3d.buffers.BufferUsage;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;
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

   public static ByteBuffer readResource(InputStream var0) throws IOException {
      ReadableByteChannel var1 = Channels.newChannel(var0);
      if (var1 instanceof SeekableByteChannel var2) {
         return readResource(var1, (int)var2.size() + 1);
      } else {
         return readResource(var1, 8192);
      }
   }

   private static ByteBuffer readResource(ReadableByteChannel var0, int var1) throws IOException {
      ByteBuffer var2 = MemoryUtil.memAlloc(var1);

      try {
         while(var0.read(var2) != -1) {
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

   public static void writeAsPNG(Path var0, String var1, GpuTexture var2, int var3, IntUnaryOperator var4) {
      RenderSystem.assertOnRenderThread();
      int var5 = 0;

      for(int var6 = 0; var6 <= var3; ++var6) {
         var5 += var2.getFormat().pixelSize() * var2.getWidth(var6) * var2.getHeight(var6);
      }

      GpuBuffer var11 = new GpuBuffer(BufferType.PIXEL_PACK, BufferUsage.STATIC_READ, var5);
      Runnable var7 = () -> {
         try (GpuBuffer.ReadView var6 = var11.read()) {
            int var7 = 0;

            for(int var8 = 0; var8 <= var3; ++var8) {
               int var9 = var2.getWidth(var8);
               int var10 = var2.getHeight(var8);

               try (NativeImage var11x = new NativeImage(var9, var10, false)) {
                  for(int var12 = 0; var12 < var10; ++var12) {
                     for(int var13 = 0; var13 < var9; ++var13) {
                        int var14 = var6.data().getInt(var7 + (var13 + var12 * var9) * var2.getFormat().pixelSize());
                        var11x.setPixelABGR(var13, var12, var4.applyAsInt(var14));
                     }
                  }

                  Path var20 = var0.resolve(var1 + "_" + var8 + ".png");
                  var11x.writeToFile(var20);
                  LOGGER.debug("Exported png to: {}", var20.toAbsolutePath());
               } catch (IOException var18) {
                  LOGGER.debug("Unable to write: ", var18);
               }

               var7 += var2.getFormat().pixelSize() * var9 * var10;
            }
         }

         var11.close();
      };
      AtomicInteger var8 = new AtomicInteger();
      int var9 = 0;

      for(int var10 = 0; var10 <= var3; ++var10) {
         var2.copyToBuffer(var11, var9, () -> {
            if (var8.getAndIncrement() == var3) {
               var7.run();
            }

         }, var10);
         var9 += var2.getFormat().pixelSize() * var2.getWidth(var10) * var2.getHeight(var10);
      }

   }

   public static Path getDebugTexturePath(Path var0) {
      return var0.resolve("screenshots").resolve("debug");
   }

   public static Path getDebugTexturePath() {
      return getDebugTexturePath(Path.of("."));
   }
}
