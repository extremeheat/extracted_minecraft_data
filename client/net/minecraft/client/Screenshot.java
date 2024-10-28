package net.minecraft.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.function.Consumer;
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
   private int rowHeight;
   private final DataOutputStream outputStream;
   private final byte[] bytes;
   private final int width;
   private final int height;
   private File file;

   public static void grab(File var0, RenderTarget var1, Consumer<Component> var2) {
      grab(var0, (String)null, var1, var2);
   }

   public static void grab(File var0, @Nullable String var1, RenderTarget var2, Consumer<Component> var3) {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            _grab(var0, var1, var2, var3);
         });
      } else {
         _grab(var0, var1, var2, var3);
      }

   }

   private static void _grab(File var0, @Nullable String var1, RenderTarget var2, Consumer<Component> var3) {
      NativeImage var4 = takeScreenshot(var2);
      File var5 = new File(var0, "screenshots");
      var5.mkdir();
      File var6;
      if (var1 == null) {
         var6 = getFile(var5);
      } else {
         var6 = new File(var5, var1);
      }

      Util.ioPool().execute(() -> {
         try {
            var4.writeToFile(var6);
            MutableComponent var3x = Component.literal(var6.getName()).withStyle(ChatFormatting.UNDERLINE).withStyle((var1) -> {
               return var1.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, var6.getAbsolutePath()));
            });
            var3.accept(Component.translatable("screenshot.success", var3x));
         } catch (Exception var7) {
            LOGGER.warn("Couldn't save screenshot", var7);
            var3.accept(Component.translatable("screenshot.failure", var7.getMessage()));
         } finally {
            var4.close();
         }

      });
   }

   public static NativeImage takeScreenshot(RenderTarget var0) {
      int var1 = var0.width;
      int var2 = var0.height;
      NativeImage var3 = new NativeImage(var1, var2, false);
      RenderSystem.bindTexture(var0.getColorTextureId());
      var3.downloadTexture(0, true);
      var3.flipY();
      return var3;
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

   public Screenshot(File var1, int var2, int var3, int var4) throws IOException {
      super();
      this.width = var2;
      this.height = var3;
      this.rowHeight = var4;
      File var5 = new File(var1, "screenshots");
      var5.mkdir();
      String var6 = "huge_" + Util.getFilenameFormattedDateTime();

      for(int var7 = 1; (this.file = new File(var5, var6 + (var7 == 1 ? "" : "_" + var7) + ".tga")).exists(); ++var7) {
      }

      byte[] var8 = new byte[18];
      var8[2] = 2;
      var8[12] = (byte)(var2 % 256);
      var8[13] = (byte)(var2 / 256);
      var8[14] = (byte)(var3 % 256);
      var8[15] = (byte)(var3 / 256);
      var8[16] = 24;
      this.bytes = new byte[var2 * var4 * 3];
      this.outputStream = new DataOutputStream(new FileOutputStream(this.file));
      this.outputStream.write(var8);
   }

   public void addRegion(ByteBuffer var1, int var2, int var3, int var4, int var5) {
      int var6 = var4;
      int var7 = var5;
      if (var4 > this.width - var2) {
         var6 = this.width - var2;
      }

      if (var5 > this.height - var3) {
         var7 = this.height - var3;
      }

      this.rowHeight = var7;

      for(int var8 = 0; var8 < var7; ++var8) {
         var1.position((var5 - var7) * var4 * 3 + var8 * var4 * 3);
         int var9 = (var2 + var8 * this.width) * 3;
         var1.get(this.bytes, var9, var6 * 3);
      }

   }

   public void saveRow() throws IOException {
      this.outputStream.write(this.bytes, 0, this.width * 3 * this.rowHeight);
   }

   public File close() throws IOException {
      this.outputStream.close();
      return this.file;
   }
}
