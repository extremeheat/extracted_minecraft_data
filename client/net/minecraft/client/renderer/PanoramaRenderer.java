package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class PanoramaRenderer {
   public static final ResourceLocation PANORAMA_OVERLAY = ResourceLocation.withDefaultNamespace("textures/gui/title/background/panorama_overlay.png");
   private final Minecraft minecraft;
   private final CubeMap cubeMap;
   private float spin;
   private float bob;

   public PanoramaRenderer(CubeMap var1) {
      super();
      this.cubeMap = var1;
      this.minecraft = Minecraft.getInstance();
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4, float var5) {
      float var6 = (float)((double)var5 * (Double)this.minecraft.options.panoramaSpeed().get());
      this.spin = wrap(this.spin + var6 * 0.1F, 360.0F);
      this.bob = wrap(this.bob + var6 * 0.001F, 6.2831855F);
      this.cubeMap.render(this.minecraft, 10.0F, -this.spin, var4);
      RenderSystem.enableBlend();
      var1.setColor(1.0F, 1.0F, 1.0F, var4);
      var1.blit(PANORAMA_OVERLAY, 0, 0, var2, var3, 0.0F, 0.0F, 16, 128, 16, 128);
      var1.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.disableBlend();
   }

   private static float wrap(float var0, float var1) {
      return var0 > var1 ? var0 - var1 : var0;
   }
}
