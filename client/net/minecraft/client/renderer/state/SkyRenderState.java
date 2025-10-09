package net.minecraft.client.renderer.state;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.level.MoonPhase;

public class SkyRenderState {
   public DimensionSpecialEffects.SkyType skyType;
   public boolean isSunriseOrSunset;
   public boolean shouldRenderDarkDisc;
   public float sunAngle;
   public float timeOfDay;
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
      this.skyType = DimensionSpecialEffects.SkyType.NONE;
      this.moonPhase = MoonPhase.FULL_MOON;
   }

   public void reset() {
      this.skyType = DimensionSpecialEffects.SkyType.NONE;
   }
}
