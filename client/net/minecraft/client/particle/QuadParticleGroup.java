package net.minecraft.client.particle;

import java.util.Objects;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.ParticleGroupRenderState;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;

public class QuadParticleGroup extends ParticleGroup<SingleQuadParticle> {
   private final ParticleRenderType particleType;
   final QuadParticleRenderState particleTypeRenderState = new QuadParticleRenderState();

   public QuadParticleGroup(final ParticleEngine engine, final ParticleRenderType particleType) {
      super(engine);
      this.particleType = particleType;
   }

   public ParticleGroupRenderState extractRenderState(final Frustum frustum, final Camera camera, final float partialTickTime) {
      for(SingleQuadParticle particle : this.particles) {
         if (frustum.pointInFrustum(particle.x, particle.y, particle.z)) {
            try {
               particle.extract(this.particleTypeRenderState, camera, partialTickTime);
            } catch (Throwable throwable) {
               CrashReport report = CrashReport.forThrowable(throwable, "Rendering Particle");
               CrashReportCategory category = report.addCategory("Particle being rendered");
               Objects.requireNonNull(particle);
               category.setDetail("Particle", particle::toString);
               ParticleRenderType var10002 = this.particleType;
               Objects.requireNonNull(var10002);
               category.setDetail("Particle Type", var10002::toString);
               throw new ReportedException(report);
            }
         }
      }

      return this.particleTypeRenderState;
   }
}
