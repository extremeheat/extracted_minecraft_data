package net.minecraft.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

public class PanoramaRenderer {
   private final Minecraft minecraft;
   private final CubeMap cubeMap;
   private float time;

   public PanoramaRenderer(CubeMap var1) {
      super();
      this.cubeMap = var1;
      this.minecraft = Minecraft.getInstance();
   }

   public void render(float var1, float var2) {
      this.time += var1;
      this.cubeMap.render(this.minecraft, Mth.sin(this.time * 0.001F) * 5.0F + 25.0F, -this.time * 0.1F, var2);
   }
}
