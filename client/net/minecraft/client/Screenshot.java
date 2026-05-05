package net.minecraft.client;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class Screenshot {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String SCREENSHOT_DIR = "screenshots";

   public Screenshot() {
      super();
   }

   public static void grab(final File workDir, final RenderTarget target, final Consumer<Component> callback) {
      grab(workDir, (String)null, target, 1, callback);
   }

   public static void grab(final File workDir, final @Nullable String forceName, final RenderTarget target, final int downscaleFactor, final Consumer<Component> callback) {
      takeScreenshot(target, downscaleFactor, (image) -> {
         File picDir = new File(workDir, "screenshots");
         picDir.mkdir();
         File file;
         if (forceName == null) {
            file = getFile(picDir);
         } else {
            file = new File(picDir, forceName);
         }

         Util.ioPool().execute(() -> {
            try {
               NativeImage twrVar0$ = image;

               try {
                  image.writeToFile(file);
                  Component component = Component.literal(file.getName()).withStyle(ChatFormatting.UNDERLINE).withStyle((UnaryOperator)((s) -> s.withClickEvent(new ClickEvent.OpenFile(file.getAbsoluteFile()))));
                  callback.accept(Component.translatable("screenshot.success", component));
               } catch (Throwable var7) {
                  if (image != null) {
                     try {
                        twrVar0$.close();
                     } catch (Throwable x2) {
                        var7.addSuppressed(x2);
                     }
                  }

                  throw var7;
               }

               if (image != null) {
                  image.close();
               }
            } catch (Exception e) {
               LOGGER.warn("Couldn't save screenshot", e);
               callback.accept(Component.translatable("screenshot.failure", e.getMessage()));
            }

         });
      });
   }

   public static void takeScreenshot(final RenderTarget target, final Consumer<NativeImage> callback) {
      takeScreenshot(target, 1, callback);
   }

   public static void takeScreenshot(final RenderTarget target, final int downscaleFactor, final Consumer<NativeImage> callback) {
      int width = target.width;
      int height = target.height;
      GpuTexture sourceTexture = target.getColorTexture();
      if (sourceTexture == null) {
         throw new IllegalStateException("Tried to capture screenshot of an incomplete framebuffer");
      } else if (width % downscaleFactor == 0 && height % downscaleFactor == 0) {
         GpuBuffer buffer = RenderSystem.getDevice().createBuffer(() -> "Screenshot buffer", 9, (long)width * (long)height * (long)sourceTexture.getFormat().pixelSize());
         RenderSystem.getDevice().createCommandEncoder().copyTextureToBuffer(sourceTexture, buffer, 0L, () -> {
            try (GpuBufferSlice.MappedView read = buffer.map(true, false)) {
               int outputHeight = height / downscaleFactor;
               int outputWidth = width / downscaleFactor;
               NativeImage image = new NativeImage(outputWidth, outputHeight, false);

               for(int y = 0; y < outputHeight; ++y) {
                  for(int x = 0; x < outputWidth; ++x) {
                     if (downscaleFactor == 1) {
                        int argb = read.data().getInt((x + y * width) * sourceTexture.getFormat().pixelSize());
                        image.setPixelABGR(x, height - y - 1, argb | -16777216);
                     } else {
                        int red = 0;
                        int green = 0;
                        int blue = 0;

                        for(int i = 0; i < downscaleFactor; ++i) {
                           for(int j = 0; j < downscaleFactor; ++j) {
                              int argb = read.data().getInt((x * downscaleFactor + i + (y * downscaleFactor + j) * width) * sourceTexture.getFormat().pixelSize());
                              red += ARGB.red(argb);
                              green += ARGB.green(argb);
                              blue += ARGB.blue(argb);
                           }
                        }

                        int sampleCount = downscaleFactor * downscaleFactor;
                        image.setPixelABGR(x, outputHeight - y - 1, ARGB.color(255, red / sampleCount, green / sampleCount, blue / sampleCount));
                     }
                  }
               }

               callback.accept(image);
            }

            buffer.close();
         }, 0);
      } else {
         throw new IllegalArgumentException("Image size is not divisible by downscale factor");
      }
   }

   private static File getFile(final File picDir) {
      String name = Util.getFilenameFormattedDateTime();
      int count = 1;

      while(true) {
         File file = new File(picDir, name + (count == 1 ? "" : "_" + count) + ".png");
         if (!file.exists()) {
            return file;
         }

         ++count;
      }
   }
}
