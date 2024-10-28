package net.minecraft.client.gui.components;

import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

public class SplashRenderer {
   public static final SplashRenderer CHRISTMAS = new SplashRenderer("Merry X-mas!");
   public static final SplashRenderer NEW_YEAR = new SplashRenderer("Happy new year!");
   public static final SplashRenderer HALLOWEEN = new SplashRenderer("OOoooOOOoooo! Spooky!");
   private static final int WIDTH_OFFSET = 123;
   private static final int HEIGH_OFFSET = 69;
   private final String splash;

   public SplashRenderer(String var1) {
      super();
      this.splash = var1;
   }

   public void render(GuiGraphics var1, int var2, Font var3, int var4) {
      var1.pose().pushPose();
      var1.pose().translate((float)var2 / 2.0F + 123.0F, 69.0F, 0.0F);
      var1.pose().mulPose(Axis.ZP.rotationDegrees(-20.0F));
      float var5 = 1.8F - Mth.abs(Mth.sin((float)(Util.getMillis() % 1000L) / 1000.0F * 6.2831855F) * 0.1F);
      var5 = var5 * 100.0F / (float)(var3.width(this.splash) + 32);
      var1.pose().scale(var5, var5, var5);
      var1.drawCenteredString(var3, (String)this.splash, 0, -8, 16776960 | var4);
      var1.pose().popPose();
   }
}
