package net.minecraft.client.particle;

import com.google.common.collect.EvictingQueue;
import java.util.Iterator;
import java.util.Objects;
import java.util.Queue;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.ParticleGroupRenderState;
import net.minecraft.client.renderer.culling.Frustum;

public abstract class ParticleGroup<P extends Particle> {
   private static final int MAX_PARTICLES = 16384;
   protected final ParticleEngine engine;
   protected final Queue<P> particles = EvictingQueue.create(16384);

   public ParticleGroup(ParticleEngine var1) {
      super();
      this.engine = var1;
   }

   public boolean isEmpty() {
      return this.particles.isEmpty();
   }

   public void tickParticles() {
      if (!this.particles.isEmpty()) {
         Iterator var1 = this.particles.iterator();

         while(var1.hasNext()) {
            Particle var2 = (Particle)var1.next();
            this.tickParticle(var2);
            if (!var2.isAlive()) {
               var2.getParticleLimit().ifPresent((var1x) -> this.engine.updateCount(var1x, -1));
               var1.remove();
            }
         }
      }

   }

   private void tickParticle(Particle var1) {
      try {
         var1.tick();
      } catch (Throwable var5) {
         CrashReport var3 = CrashReport.forThrowable(var5, "Ticking Particle");
         CrashReportCategory var4 = var3.addCategory("Particle being ticked");
         Objects.requireNonNull(var1);
         var4.setDetail("Particle", var1::toString);
         ParticleRenderType var10002 = var1.getGroup();
         Objects.requireNonNull(var10002);
         var4.setDetail("Particle Type", var10002::toString);
         throw new ReportedException(var3);
      }
   }

   public void add(Particle var1) {
      this.particles.add(var1);
   }

   public int size() {
      return this.particles.size();
   }

   public abstract ParticleGroupRenderState extractRenderState(Frustum var1, Camera var2, float var3);

   public Queue<P> getAll() {
      return this.particles;
   }
}
