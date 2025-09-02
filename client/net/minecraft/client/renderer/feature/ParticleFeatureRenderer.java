package net.minecraft.client.renderer.feature;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeCollector;

public class ParticleFeatureRenderer {
   public ParticleFeatureRenderer() {
      super();
   }

   public void render(SubmitNodeCollection var1, MultiBufferSource.BufferSource var2) {
      for(SubmitNodeCollector.ParticleGroupRenderer var4 : var1.getParticleGroupRenderers()) {
         var4.render(var2);
      }

   }
}
