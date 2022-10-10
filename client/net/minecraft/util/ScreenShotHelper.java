package net.minecraft.util;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.resources.SimpleResource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScreenShotHelper {
   private static final Logger field_148261_a = LogManager.getLogger();
   private static final DateFormat field_74295_a = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

   public static void func_148260_a(File var0, int var1, int var2, Framebuffer var3, Consumer<ITextComponent> var4) {
      func_148259_a(var0, (String)null, var1, var2, var3, var4);
   }

   public static void func_148259_a(File var0, @Nullable String var1, int var2, int var3, Framebuffer var4, Consumer<ITextComponent> var5) {
      NativeImage var6 = func_198052_a(var2, var3, var4);
      File var7 = new File(var0, "screenshots");
      var7.mkdir();
      File var8;
      if (var1 == null) {
         var8 = func_74290_a(var7);
      } else {
         var8 = new File(var7, var1);
      }

      SimpleResource.field_199031_a.execute(() -> {
         try {
            var6.func_209271_a(var8);
            ITextComponent var3 = (new TextComponentString(var8.getName())).func_211708_a(TextFormatting.UNDERLINE).func_211710_a((var1) -> {
               var1.func_150241_a(new ClickEvent(ClickEvent.Action.OPEN_FILE, var8.getAbsolutePath()));
            });
            var5.accept(new TextComponentTranslation("screenshot.success", new Object[]{var3}));
         } catch (Exception var7) {
            field_148261_a.warn("Couldn't save screenshot", var7);
            var5.accept(new TextComponentTranslation("screenshot.failure", new Object[]{var7.getMessage()}));
         } finally {
            var6.close();
         }

      });
   }

   public static NativeImage func_198052_a(int var0, int var1, Framebuffer var2) {
      if (OpenGlHelper.func_148822_b()) {
         var0 = var2.field_147622_a;
         var1 = var2.field_147620_b;
      }

      NativeImage var3 = new NativeImage(var0, var1, false);
      if (OpenGlHelper.func_148822_b()) {
         GlStateManager.func_179144_i(var2.field_147617_g);
         var3.func_195717_a(0, true);
      } else {
         var3.func_195701_a(true);
      }

      var3.func_195710_e();
      return var3;
   }

   private static File func_74290_a(File var0) {
      String var1 = field_74295_a.format(new Date());
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
