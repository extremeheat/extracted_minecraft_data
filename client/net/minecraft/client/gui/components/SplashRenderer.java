package net.minecraft.client.gui.components;

import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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

   public SplashRenderer(final Component splash) {
      super();
      this.splash = splash;
   }

   public void extractRenderState(final GuiGraphicsExtractor graphics, final int screenWidth, final Font font, final float alpha) {
      int textWidth = font.width((FormattedText)this.splash);
      ActiveTextCollector textRenderer = graphics.textRenderer();
      float textPhase = 1.8F - Mth.abs(Mth.sin((double)((float)(Util.getMillis() % 1000L) / 1000.0F * 6.2831855F)) * 0.1F);
      float textScale = textPhase * 100.0F / (float)(textWidth + 32);
      Matrix3x2f transform = (new Matrix3x2f(textRenderer.defaultParameters().pose())).translate((float)screenWidth / 2.0F + 123.0F, 69.0F).rotate(-0.34906584F).scale(textScale);
      ActiveTextCollector.Parameters renderParameters = textRenderer.defaultParameters().withOpacity(alpha).withPose(transform);
      textRenderer.accept(TextAlignment.LEFT, -textWidth / 2, -8, renderParameters, (Component)this.splash);
   }

   static {
      CHRISTMAS = new SplashRenderer(SplashManager.CHRISTMAS);
      NEW_YEAR = new SplashRenderer(SplashManager.NEW_YEAR);
      HALLOWEEN = new SplashRenderer(SplashManager.HALLOWEEN);
   }
}
