package net.minecraft.client.particle;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.ParticleGroupRenderState;
import net.minecraft.client.renderer.culling.Frustum;

public class NoRenderParticleGroup extends ParticleGroup<NoRenderParticle> {
   private static final ParticleGroupRenderState EMPTY_RENDER_STATE = (var0) -> {
   };

   public NoRenderParticleGroup(ParticleEngine var1) {
      super(var1);
   }

   public ParticleGroupRenderState extractRenderState(Frustum var1, Camera var2, float var3) {
      return EMPTY_RENDER_STATE;
   }
}
