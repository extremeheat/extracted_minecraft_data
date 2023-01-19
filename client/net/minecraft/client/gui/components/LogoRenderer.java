package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

public class LogoRenderer extends GuiComponent {
   public static final ResourceLocation MINECRAFT_LOGO = new ResourceLocation("textures/gui/title/minecraft.png");
   public static final ResourceLocation MINECRAFT_EDITION = new ResourceLocation("textures/gui/title/edition.png");
   public static final int LOGO_WIDTH = 274;
   public static final int LOGO_HEIGHT = 44;
   public static final int DEFAULT_HEIGHT_OFFSET = 30;
   private final boolean showEasterEgg = (double)RandomSource.create().nextFloat() < 1.0E-4;
   private final boolean keepLogoThroughFade;

   public LogoRenderer(boolean var1) {
      super();
      this.keepLogoThroughFade = var1;
   }

   public void renderLogo(PoseStack var1, int var2, float var3) {
      this.renderLogo(var1, var2, var3, 30);
   }

   public void renderLogo(PoseStack var1, int var2, float var3, int var4) {
      RenderSystem.setShaderTexture(0, MINECRAFT_LOGO);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.keepLogoThroughFade ? 1.0F : var3);
      int var5 = var2 / 2 - 137;
      if (this.showEasterEgg) {
         this.blitOutlineBlack(var5, var4, (var2x, var3x) -> {
            this.blit(var1, var2x, var3x, 0, 0, 99, 44);
            this.blit(var1, var2x + 99, var3x, 129, 0, 27, 44);
            this.blit(var1, var2x + 99 + 26, var3x, 126, 0, 3, 44);
            this.blit(var1, var2x + 99 + 26 + 3, var3x, 99, 0, 26, 44);
            this.blit(var1, var2x + 155, var3x, 0, 45, 155, 44);
         });
      } else {
         this.blitOutlineBlack(var5, var4, (var2x, var3x) -> {
            this.blit(var1, var2x, var3x, 0, 0, 155, 44);
            this.blit(var1, var2x + 155, var3x, 0, 45, 155, 44);
         });
      }

      RenderSystem.setShaderTexture(0, MINECRAFT_EDITION);
      blit(var1, var5 + 88, var4 + 37, 0.0F, 0.0F, 98, 14, 128, 16);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }
}
