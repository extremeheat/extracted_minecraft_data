package net.minecraft.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.packs.resources.SimpleResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Screenshot {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

   public static void grab(File var0, int var1, int var2, RenderTarget var3, Consumer<Component> var4) {
      grab(var0, (String)null, var1, var2, var3, var4);
   }

   public static void grab(File var0, @Nullable String var1, int var2, int var3, RenderTarget var4, Consumer<Component> var5) {
      NativeImage var6 = takeScreenshot(var2, var3, var4);
      File var7 = new File(var0, "screenshots");
      var7.mkdir();
      File var8;
      if (var1 == null) {
         var8 = getFile(var7);
      } else {
         var8 = new File(var7, var1);
      }

      SimpleResource.IO_EXECUTOR.execute(() -> {
         try {
            var6.writeToFile(var8);
            Component var3 = (new TextComponent(var8.getName())).withStyle(ChatFormatting.UNDERLINE).withStyle((var1) -> {
               var1.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, var8.getAbsolutePath()));
            });
            var5.accept(new TranslatableComponent("screenshot.success", new Object[]{var3}));
         } catch (Exception var7) {
            LOGGER.warn("Couldn't save screenshot", var7);
            var5.accept(new TranslatableComponent("screenshot.failure", new Object[]{var7.getMessage()}));
         } finally {
            var6.close();
         }

      });
   }

   public static NativeImage takeScreenshot(int var0, int var1, RenderTarget var2) {
      if (GLX.isUsingFBOs()) {
         var0 = var2.width;
         var1 = var2.height;
      }

      NativeImage var3 = new NativeImage(var0, var1, false);
      if (GLX.isUsingFBOs()) {
         GlStateManager.bindTexture(var2.colorTextureId);
         var3.downloadTexture(0, true);
      } else {
         var3.downloadFrameBuffer(true);
      }

      var3.flipY();
      return var3;
   }

   private static File getFile(File var0) {
      String var1 = DATE_FORMAT.format(new Date());
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
