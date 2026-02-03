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

   public PanoramaRenderer(final CubeMap cubeMap) {
      super();
      this.cubeMap = cubeMap;
      this.minecraft = Minecraft.getInstance();
   }

   public void render(final GuiGraphics graphics, final int width, final int height, final boolean shouldSpin) {
      if (shouldSpin) {
         float a = this.minecraft.getDeltaTracker().getRealtimeDeltaTicks();
         float delta = (float)((double)a * (Double)this.minecraft.options.panoramaSpeed().get());
         this.spin = wrap(this.spin + delta * 0.1F, 360.0F);
      }

      this.cubeMap.render(this.minecraft, 10.0F, -this.spin);
      graphics.blit(RenderPipelines.GUI_TEXTURED, PANORAMA_OVERLAY, 0, 0, 0.0F, 0.0F, width, height, 16, 128, 16, 128);
   }

   private static float wrap(final float value, final float limit) {
      return value > limit ? value - limit : value;
   }

   public void registerTextures(final TextureManager textureManager) {
      this.cubeMap.registerTextures(textureManager);
   }
}
