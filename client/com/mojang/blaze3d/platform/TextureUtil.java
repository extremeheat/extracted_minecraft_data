package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.DontObfuscate;
import com.mojang.blaze3d.buffers.BufferType;
import com.mojang.blaze3d.buffers.BufferUsage;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.CommandEncoder;
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

      GpuBuffer var12 = RenderSystem.getDevice().createBuffer(() -> "Texture output buffer", BufferType.PIXEL_PACK, BufferUsage.STATIC_READ, var5);
      CommandEncoder var7 = RenderSystem.getDevice().createCommandEncoder();
      Runnable var8 = () -> {
         try (GpuBuffer.ReadView var7x = var7.readBuffer(var12)) {
            int var8 = 0;

            for(int var9 = 0; var9 <= var3; ++var9) {
               int var10 = var2.getWidth(var9);
               int var11 = var2.getHeight(var9);

               try (NativeImage var12x = new NativeImage(var10, var11, false)) {
                  for(int var13 = 0; var13 < var11; ++var13) {
                     for(int var14 = 0; var14 < var10; ++var14) {
                        int var15 = var7x.data().getInt(var8 + (var14 + var13 * var10) * var2.getFormat().pixelSize());
                        var12x.setPixelABGR(var14, var13, var4.applyAsInt(var15));
                     }
                  }

                  Path var21 = var0.resolve(var1 + "_" + var9 + ".png");
                  var12x.writeToFile(var21);
                  LOGGER.debug("Exported png to: {}", var21.toAbsolutePath());
               } catch (IOException var19) {
                  LOGGER.debug("Unable to write: ", var19);
               }

               var8 += var2.getFormat().pixelSize() * var10 * var11;
            }
         }

         var12.close();
      };
      AtomicInteger var9 = new AtomicInteger();
      int var10 = 0;

      for(int var11 = 0; var11 <= var3; ++var11) {
         var7.copyTextureToBuffer(var2, var12, var10, () -> {
            if (var9.getAndIncrement() == var3) {
               var8.run();
            }

         }, var11);
         var10 += var2.getFormat().pixelSize() * var2.getWidth(var11) * var2.getHeight(var11);
      }

   }

   public static Path getDebugTexturePath(Path var0) {
      return var0.resolve("screenshots").resolve("debug");
   }

   public static Path getDebugTexturePath() {
      return getDebugTexturePath(Path.of("."));
   }
}
