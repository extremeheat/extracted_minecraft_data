package net.minecraft.client;

import com.mojang.blaze3d.buffers.BufferType;
import com.mojang.blaze3d.buffers.BufferUsage;
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
import org.slf4j.Logger;

public class Screenshot {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String SCREENSHOT_DIR = "screenshots";

   public Screenshot() {
      super();
   }

   public static void grab(File var0, RenderTarget var1, Consumer<Component> var2) {
      grab(var0, (String)null, var1, var2);
   }

   public static void grab(File var0, @Nullable String var1, RenderTarget var2, Consumer<Component> var3) {
      takeScreenshot(var2, (var3x) -> {
         File var4 = new File(var0, "screenshots");
         var4.mkdir();
         File var5;
         if (var1 == null) {
            var5 = getFile(var4);
         } else {
            var5 = new File(var4, var1);
         }

         Util.ioPool().execute(() -> {
            try {
               NativeImage var3xx = var3x;

               try {
                  var3x.writeToFile(var5);
                  MutableComponent var4 = Component.literal(var5.getName()).withStyle(ChatFormatting.UNDERLINE).withStyle((UnaryOperator)((var1) -> var1.withClickEvent(new ClickEvent.OpenFile(var5.getAbsoluteFile()))));
                  var3.accept(Component.translatable("screenshot.success", var4));
               } catch (Throwable var7) {
                  if (var3x != null) {
                     try {
                        var3xx.close();
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
               var3.accept(Component.translatable("screenshot.failure", var8.getMessage()));
            }

         });
      });
   }

   public static void takeScreenshot(RenderTarget var0, Consumer<NativeImage> var1) {
      int var2 = var0.width;
      int var3 = var0.height;
      GpuTexture var4 = var0.getColorTexture();
      if (var4 == null) {
         throw new IllegalStateException("Tried to capture screenshot of an incomplete framebuffer");
      } else {
         GpuBuffer var5 = RenderSystem.getDevice().createBuffer(() -> "Screenshot buffer", BufferType.PIXEL_PACK, BufferUsage.STATIC_READ, var2 * var3 * var4.getFormat().pixelSize());
         CommandEncoder var6 = RenderSystem.getDevice().createCommandEncoder();
         RenderSystem.getDevice().createCommandEncoder().copyTextureToBuffer(var4, var5, 0, () -> {
            try (GpuBuffer.ReadView var6x = var6.readBuffer(var5)) {
               NativeImage var7 = new NativeImage(var2, var3, false);

               for(int var8 = 0; var8 < var3; ++var8) {
                  for(int var9 = 0; var9 < var2; ++var9) {
                     int var10 = var6x.data().getInt((var9 + var8 * var2) * var4.getFormat().pixelSize());
                     var7.setPixelABGR(var9, var3 - var8 - 1, var10 | -16777216);
                  }
               }

               var1.accept(var7);
            }

            var5.close();
         }, 0);
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
