package net.minecraft.client.gui.components;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

public class LogoRenderer {
   public static final Identifier HERDCRAFT_LOGO = Identifier.withDefaultNamespace("textures/gui/title/herdcraft.png");
   public static final int LOGO_WIDTH = 358;
   public static final int LOGO_HEIGHT = 105;
   public static final int DEFAULT_HEIGHT_OFFSET = 1;
   private final boolean keepLogoThroughFade;

   public LogoRenderer(final boolean keepLogoThroughFade) {
      super();
      this.keepLogoThroughFade = keepLogoThroughFade;
   }

   public void extractRenderState(final GuiGraphicsExtractor graphics, final int width, final float alpha) {
      this.extractRenderState(graphics, width, alpha, 1);
   }

   public void extractRenderState(final GuiGraphicsExtractor graphics, final int width, final float alpha, final int heightOffset) {
      int logoX = width / 2 - 179;
      float effectiveAlpha = this.keepLogoThroughFade ? 1.0F : alpha;
      int color = ARGB.white(effectiveAlpha);
      graphics.blit(RenderPipelines.GUI_TEXTURED, HERDCRAFT_LOGO, logoX, heightOffset, 0.0F, 0.0F, 358, 105, 358, 105, color);
   }

   public boolean keepLogoThroughFade() {
      return this.keepLogoThroughFade;
   }
}
