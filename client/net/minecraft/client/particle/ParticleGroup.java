package net.minecraft.client.particle;

import com.google.common.collect.EvictingQueue;
import java.util.Iterator;
import java.util.Objects;
import java.util.Queue;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.ParticleGroupRenderState;

public abstract class ParticleGroup<P extends Particle> {
   private static final int MAX_PARTICLES = 16384;
   protected final ParticleEngine engine;
   protected final Queue<P> particles = EvictingQueue.create(16384);

   public ParticleGroup(final ParticleEngine engine) {
      super();
      this.engine = engine;
   }

   public boolean isEmpty() {
      return this.particles.isEmpty();
   }

   public void tickParticles() {
      if (!this.particles.isEmpty()) {
         Iterator<P> iterator = this.particles.iterator();

         while(iterator.hasNext()) {
            P particle = (P)(iterator.next());
            this.tickParticle(particle);
            if (!particle.isAlive()) {
               particle.getParticleLimit().ifPresent((options) -> this.engine.updateCount(options, -1));
               iterator.remove();
            }
         }
      }

   }

   private void tickParticle(final Particle particle) {
      try {
         particle.tick();
      } catch (Throwable t) {
         CrashReport report = CrashReport.forThrowable(t, "Ticking Particle");
         CrashReportCategory category = report.addCategory("Particle being ticked");
         Objects.requireNonNull(particle);
         category.setDetail("Particle", particle::toString);
         ParticleRenderType var10002 = particle.getGroup();
         Objects.requireNonNull(var10002);
         category.setDetail("Particle Type", var10002::toString);
         throw new ReportedException(report);
      }
   }

   public void add(final Particle particle) {
      this.particles.add(particle);
   }

   public int size() {
      return this.particles.size();
   }

   public abstract ParticleGroupRenderState extractRenderState(Frustum frustum, Camera camera, float partialTickTime);

   public Queue<P> getAll() {
      return this.particles;
   }
}
