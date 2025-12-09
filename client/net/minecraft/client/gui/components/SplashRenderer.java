package net.minecraft.client.gui.components;

import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.resources.SplashManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import org.joml.Matrix3x2f;

public class SplashRenderer {
   public static final SplashRenderer CHRISTMAS;
   public static final SplashRenderer NEW_YEAR;
   public static final SplashRenderer HALLOWEEN;
   private static final int WIDTH_OFFSET = 123;
   private static final int HEIGH_OFFSET = 69;
   private static final float TEXT_ANGLE = -0.34906584F;
   private final Component splash;

   public SplashRenderer(Component var1) {
      super();
      this.splash = var1;
   }

   public void render(GuiGraphics var1, int var2, Font var3, float var4) {
      int var5 = var3.width((FormattedText)this.splash);
      ActiveTextCollector var6 = var1.textRenderer();
      float var7 = 1.8F - Mth.abs(Mth.sin((double)((float)(Util.getMillis() % 1000L) / 1000.0F * 6.2831855F)) * 0.1F);
      float var8 = var7 * 100.0F / (float)(var5 + 32);
      Matrix3x2f var9 = (new Matrix3x2f(var6.defaultParameters().pose())).translate((float)var2 / 2.0F + 123.0F, 69.0F).rotate(-0.34906584F).scale(var8);
      ActiveTextCollector.Parameters var10 = var6.defaultParameters().withOpacity(var4).withPose(var9);
      var6.accept(TextAlignment.LEFT, -var5 / 2, -8, var10, (Component)this.splash);
   }

   static {
      CHRISTMAS = new SplashRenderer(SplashManager.CHRISTMAS);
      NEW_YEAR = new SplashRenderer(SplashManager.NEW_YEAR);
      HALLOWEEN = new SplashRenderer(SplashManager.HALLOWEEN);
   }
}
