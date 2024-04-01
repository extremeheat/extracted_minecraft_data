package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class LogoRenderer {
   public static final ResourceLocation POISONOUS_POTATO_LOGO = new ResourceLocation(
      "nothingtoseeheremovealong", "textures/gui/title/poisonous_potato_logo.png"
   );
   public static final int LOGO_WIDTH = 256;
   public static final int LOGO_HEIGHT = 44;
   private static final int LOGO_TEXTURE_WIDTH = 256;
   private static final int LOGO_TEXTURE_HEIGHT = 128;
   public static final int DEFAULT_HEIGHT_OFFSET = 30;
   private final boolean keepLogoThroughFade;

   public LogoRenderer(boolean var1) {
      super();
      this.keepLogoThroughFade = var1;
   }

   public void renderLogo(GuiGraphics var1, int var2, float var3) {
      this.renderLogo(var1, var2, var3, 30);
   }

   public void renderLogo(GuiGraphics var1, int var2, float var3, int var4) {
      var1.setColor(1.0F, 1.0F, 1.0F, this.keepLogoThroughFade ? 1.0F : var3);
      RenderSystem.enableBlend();
      int var5 = var2 / 2 - 128;
      var1.blit(POISONOUS_POTATO_LOGO, var5, var4, 0.0F, 0.0F, 256, 128, 256, 128);
      var1.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.disableBlend();
   }
}
