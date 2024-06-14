package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

public class LogoRenderer {
   public static final ResourceLocation MINECRAFT_LOGO = ResourceLocation.withDefaultNamespace("textures/gui/title/minecraft.png");
   public static final ResourceLocation EASTER_EGG_LOGO = ResourceLocation.withDefaultNamespace("textures/gui/title/minceraft.png");
   public static final ResourceLocation MINECRAFT_EDITION = ResourceLocation.withDefaultNamespace("textures/gui/title/edition.png");
   public static final int LOGO_WIDTH = 256;
   public static final int LOGO_HEIGHT = 44;
   private static final int LOGO_TEXTURE_WIDTH = 256;
   private static final int LOGO_TEXTURE_HEIGHT = 64;
   private static final int EDITION_WIDTH = 128;
   private static final int EDITION_HEIGHT = 14;
   private static final int EDITION_TEXTURE_WIDTH = 128;
   private static final int EDITION_TEXTURE_HEIGHT = 16;
   public static final int DEFAULT_HEIGHT_OFFSET = 30;
   private static final int EDITION_LOGO_OVERLAP = 7;
   private final boolean showEasterEgg = (double)RandomSource.create().nextFloat() < 1.0E-4;
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
      var1.blit(this.showEasterEgg ? EASTER_EGG_LOGO : MINECRAFT_LOGO, var5, var4, 0.0F, 0.0F, 256, 44, 256, 64);
      int var6 = var2 / 2 - 64;
      int var7 = var4 + 44 - 7;
      var1.blit(MINECRAFT_EDITION, var6, var7, 0.0F, 0.0F, 128, 14, 128, 16);
      var1.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.disableBlend();
   }
}
