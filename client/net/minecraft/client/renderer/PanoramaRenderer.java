package net.minecraft.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;

public class PanoramaRenderer {
   public static final ResourceLocation PANORAMA_OVERLAY = ResourceLocation.withDefaultNamespace("textures/gui/title/background/panorama_overlay.png");
   private final Minecraft minecraft;
   private final CubeMap cubeMap;
   private float spin;

   public PanoramaRenderer(CubeMap var1) {
      super();
      this.cubeMap = var1;
      this.minecraft = Minecraft.getInstance();
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4, float var5) {
      float var6 = this.minecraft.getDeltaTracker().getRealtimeDeltaTicks();
      float var7 = (float)((double)var6 * (Double)this.minecraft.options.panoramaSpeed().get());
      this.spin = wrap(this.spin + var7 * 0.1F, 360.0F);
      var1.flush();
      this.cubeMap.render(this.minecraft, 10.0F, -this.spin, var4);
      var1.flush();
      var1.blit(RenderType::guiTextured, PANORAMA_OVERLAY, 0, 0, 0.0F, 0.0F, var2, var3, 16, 128, 16, 128, ARGB.white(var4));
   }

   private static float wrap(float var0, float var1) {
      return var0 > var1 ? var0 - var1 : var0;
   }
}
