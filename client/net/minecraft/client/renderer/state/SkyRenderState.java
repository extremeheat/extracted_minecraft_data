package net.minecraft.client.renderer.state;

import net.minecraft.world.level.MoonPhase;
import net.minecraft.world.level.dimension.DimensionType;

public class SkyRenderState {
   public DimensionType.Skybox skybox;
   public boolean shouldRenderDarkDisc;
   public float sunAngle;
   public float moonAngle;
   public float starAngle;
   public float rainBrightness;
   public float starBrightness;
   public int sunriseAndSunsetColor;
   public MoonPhase moonPhase;
   public int skyColor;
   public float endFlashIntensity;
   public float endFlashXAngle;
   public float endFlashYAngle;

   public SkyRenderState() {
      super();
      this.skybox = DimensionType.Skybox.NONE;
      this.moonPhase = MoonPhase.FULL_MOON;
   }

   public void reset() {
      this.skybox = DimensionType.Skybox.NONE;
   }
}
