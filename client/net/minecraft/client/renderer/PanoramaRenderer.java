package net.minecraft.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;

public class PanoramaRenderer {
   public static final Identifier PANORAMA_OVERLAY = Identifier.withDefaultNamespace("textures/gui/title/background/panorama_overlay.png");
   private final Minecraft minecraft;
   private final CubeMap cubeMap;
   private float spin;

   public PanoramaRenderer(CubeMap var1) {
      super();
      this.cubeMap = var1;
      this.minecraft = Minecraft.getInstance();
   }

   public void render(GuiGraphics var1, int var2, int var3, boolean var4) {
      if (var4) {
         float var5 = this.minecraft.getDeltaTracker().getRealtimeDeltaTicks();
         float var6 = (float)((double)var5 * (Double)this.minecraft.options.panoramaSpeed().get());
         this.spin = wrap(this.spin + var6 * 0.1F, 360.0F);
      }

      this.cubeMap.render(this.minecraft, 10.0F, -this.spin);
      var1.blit(RenderPipelines.GUI_TEXTURED, PANORAMA_OVERLAY, 0, 0, 0.0F, 0.0F, var2, var3, 16, 128, 16, 128);
   }

   private static float wrap(float var0, float var1) {
      return var0 > var1 ? var0 - var1 : var0;
   }

   public void registerTextures(TextureManager var1) {
      this.cubeMap.registerTextures(var1);
   }
}
