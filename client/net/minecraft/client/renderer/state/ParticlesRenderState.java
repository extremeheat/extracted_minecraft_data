package net.minecraft.client.renderer.state;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.SubmitNodeStorage;

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

   public void submit(SubmitNodeStorage var1, CameraRenderState var2) {
      for(ParticleGroupRenderState var4 : this.particles) {
         var4.submit(var1, var2);
      }

   }
}
