package net.minecraft.client;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.ARGB;
import org.slf4j.Logger;

public class Screenshot {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String SCREENSHOT_DIR = "screenshots";

   public Screenshot() {
      super();
   }

   public static void grab(File var0, RenderTarget var1, Consumer<Component> var2) {
      grab(var0, (String)null, var1, 1, var2);
   }

   public static void grab(File var0, @Nullable String var1, RenderTarget var2, int var3, Consumer<Component> var4) {
      takeScreenshot(var2, var3, (var3x) -> {
         File var4x = new File(var0, "screenshots");
         var4x.mkdir();
         File var5;
         if (var1 == null) {
            var5 = getFile(var4x);
         } else {
            var5 = new File(var4x, var1);
         }

         Util.ioPool().execute(() -> {
            try {
               NativeImage var3 = var3x;

               try {
                  var3x.writeToFile(var5);
                  MutableComponent var4x = Component.literal(var5.getName()).withStyle(ChatFormatting.UNDERLINE).withStyle((UnaryOperator)((var1) -> var1.withClickEvent(new ClickEvent.OpenFile(var5.getAbsoluteFile()))));
                  var4.accept(Component.translatable("screenshot.success", var4x));
               } catch (Throwable var7) {
                  if (var3x != null) {
                     try {
                        var3.close();
                     } catch (Throwable var6) {
                        var7.addSuppressed(var6);
                     }
                  }

                  throw var7;
               }

               if (var3x != null) {
                  var3x.close();
               }
            } catch (Exception var8) {
               LOGGER.warn("Couldn't save screenshot", var8);
               var4.accept(Component.translatable("screenshot.failure", var8.getMessage()));
            }

         });
      });
   }

   public static void takeScreenshot(RenderTarget var0, Consumer<NativeImage> var1) {
      takeScreenshot(var0, 1, var1);
   }

   public static void takeScreenshot(RenderTarget var0, int var1, Consumer<NativeImage> var2) {
      int var3 = var0.width;
      int var4 = var0.height;
      GpuTexture var5 = var0.getColorTexture();
      if (var5 == null) {
         throw new IllegalStateException("Tried to capture screenshot of an incomplete framebuffer");
      } else if (var3 % var1 == 0 && var4 % var1 == 0) {
         GpuBuffer var6 = RenderSystem.getDevice().createBuffer(() -> "Screenshot buffer", 9, var3 * var4 * var5.getFormat().pixelSize());
         CommandEncoder var7 = RenderSystem.getDevice().createCommandEncoder();
         RenderSystem.getDevice().createCommandEncoder().copyTextureToBuffer(var5, var6, 0, () -> {
            try (GpuBuffer.MappedView var7x = var7.mapBuffer(var6, true, false)) {
               int var8 = var4 / var1;
               int var9 = var3 / var1;
               NativeImage var10 = new NativeImage(var9, var8, false);

               for(int var11 = 0; var11 < var8; ++var11) {
                  for(int var12 = 0; var12 < var9; ++var12) {
                     if (var1 == 1) {
                        int var21 = var7x.data().getInt((var12 + var11 * var3) * var5.getFormat().pixelSize());
                        var10.setPixelABGR(var12, var4 - var11 - 1, var21 | -16777216);
                     } else {
                        int var13 = 0;
                        int var14 = 0;
                        int var15 = 0;

                        for(int var16 = 0; var16 < var1; ++var16) {
                           for(int var17 = 0; var17 < var1; ++var17) {
                              int var18 = var7x.data().getInt((var12 * var1 + var16 + (var11 * var1 + var17) * var3) * var5.getFormat().pixelSize());
                              var13 += ARGB.red(var18);
                              var14 += ARGB.green(var18);
                              var15 += ARGB.blue(var18);
                           }
                        }

                        int var22 = var1 * var1;
                        var10.setPixelABGR(var12, var8 - var11 - 1, ARGB.color(255, var13 / var22, var14 / var22, var15 / var22));
                     }
                  }
               }

               var2.accept(var10);
            }

            var6.close();
         }, 0);
      } else {
         throw new IllegalArgumentException("Image size is not divisible by downscale factor");
      }
   }

   private static File getFile(File var0) {
      String var1 = Util.getFilenameFormattedDateTime();
      int var2 = 1;

      while(true) {
         File var3 = new File(var0, var1 + (var2 == 1 ? "" : "_" + var2) + ".png");
         if (!var3.exists()) {
            return var3;
         }

         ++var2;
      }
   }
}
