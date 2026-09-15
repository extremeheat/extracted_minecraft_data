package net.minecraft.client.renderer.oit;

public enum OitStage {
   DEPTH_BOUNDS,
   TRANSMITTANCE,
   ACCUMULATE;

   private OitStage() {
   }

   // $FF: synthetic method
   private static OitStage[] $values() {
      return new OitStage[]{DEPTH_BOUNDS, TRANSMITTANCE, ACCUMULATE};
   }
}
