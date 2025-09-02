package net.minecraft.client.particle;

import java.util.Objects;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.ParticleGroupRenderState;
import net.minecraft.client.renderer.QuadParticleRenderState;
import net.minecraft.client.renderer.culling.Frustum;

public class QuadParticleGroup extends ParticleGroup<SingleQuadParticle> {
   private final ParticleRenderType particleType;
   final QuadParticleRenderState particleTypeRenderState = new QuadParticleRenderState();

   public QuadParticleGroup(ParticleEngine var1, ParticleRenderType var2) {
      super(var1);
      this.particleType = var2;
   }

   public ParticleGroupRenderState extractRenderState(Frustum var1, Camera var2, float var3) {
      for(SingleQuadParticle var5 : this.particles) {
         if (var1.pointInFrustum(var5.x, var5.y, var5.z)) {
            try {
               var5.extract(this.particleTypeRenderState, var2, var3);
            } catch (Throwable var9) {
               CrashReport var7 = CrashReport.forThrowable(var9, "Rendering Particle");
               CrashReportCategory var8 = var7.addCategory("Particle being rendered");
               Objects.requireNonNull(var5);
               var8.setDetail("Particle", var5::toString);
               ParticleRenderType var10002 = this.particleType;
               Objects.requireNonNull(var10002);
               var8.setDetail("Particle Type", var10002::toString);
               throw new ReportedException(var7);
            }
         }
      }

      return this.particleTypeRenderState;
   }
}
