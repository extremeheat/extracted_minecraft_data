package net.minecraft.client.renderer;

import java.util.ArrayList;
import java.util.List;

public class ParticlesRenderState {
   public final List<ParticleGroupRenderState> particles = new ArrayList();

   public ParticlesRenderState() {
      super();
   }

   public void reset() {
      this.particles.forEach(ParticleGroupRenderState::clear);
      this.particles.clear();
   }

   public void add(ParticleGroupRenderState var1) {
      this.particles.add(var1);
   }

   public void submit(SubmitNodeStorage var1) {
      for(ParticleGroupRenderState var3 : this.particles) {
         var3.submit(var1);
      }

   }
}
