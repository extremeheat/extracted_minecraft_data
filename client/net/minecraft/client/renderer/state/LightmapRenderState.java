package net.minecraft.client.renderer.state;

import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import org.joml.Vector3fc;

public class LightmapRenderState {
   public boolean needsUpdate = false;
   public float blockFactor;
   public Vector3fc blockLightTint;
   public float skyFactor;
   public Vector3fc skyLightColor;
   public Vector3fc ambientColor;
   public float brightness;
   public float darknessEffectScale;
   public float nightVisionEffectIntensity;
   public Vector3fc nightVisionColor;
   public float bossOverlayWorldDarkening;

   public LightmapRenderState() {
      super();
      this.blockLightTint = LightmapRenderStateExtractor.WHITE;
      this.skyLightColor = LightmapRenderStateExtractor.WHITE;
      this.ambientColor = LightmapRenderStateExtractor.WHITE;
      this.nightVisionColor = LightmapRenderStateExtractor.WHITE;
   }
}
