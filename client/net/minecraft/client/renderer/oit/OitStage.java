package net.minecraft.client.renderer.oit;

import com.mojang.renderpearl.api.pipeline.RenderPassLayout;
import net.minecraft.client.renderer.RenderPassLayouts;

public enum OitStage {
   DEPTH_BOUNDS,
   TRANSMITTANCE,
   ACCUMULATE;

   private OitStage() {
   }

   public RenderPassLayout.Subpass subpass() {
      RenderPassLayout.Subpass var10000;
      switch (this.ordinal()) {
         case 0 -> var10000 = RenderPassLayouts.OIT_DEPTH_BOUNDS.subpass(0);
         case 1 -> var10000 = RenderPassLayouts.OIT_TRANSMITTANCE.subpass(0);
         case 2 -> var10000 = RenderPassLayouts.OIT_ACCUMULATE.subpass(0);
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   // $FF: synthetic method
   private static OitStage[] $values() {
      return new OitStage[]{DEPTH_BOUNDS, TRANSMITTANCE, ACCUMULATE};
   }
}
