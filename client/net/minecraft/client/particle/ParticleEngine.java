package net.minecraft.client.particle;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ParticlesRenderState;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.particles.ParticleLimit;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.entity.Entity;

public class ParticleEngine {
   private static final List<ParticleRenderType> RENDER_ORDER;
   protected ClientLevel level;
   private final Map<ParticleRenderType, ParticleGroup<?>> particles = Maps.newIdentityHashMap();
   private final Queue<TrackingEmitter> trackingEmitters = Queues.newArrayDeque();
   private final Queue<Particle> particlesToAdd = Queues.newArrayDeque();
   private final Object2IntOpenHashMap<ParticleLimit> trackedParticleCounts = new Object2IntOpenHashMap();
   private final ParticleResources resourceManager;
   private final RandomSource random = RandomSource.create();

   public ParticleEngine(ClientLevel var1, ParticleResources var2) {
      super();
      this.level = var1;
      this.resourceManager = var2;
   }

   public void createTrackingEmitter(Entity var1, ParticleOptions var2) {
      this.trackingEmitters.add(new TrackingEmitter(this.level, var1, var2));
   }

   public void createTrackingEmitter(Entity var1, ParticleOptions var2, int var3) {
      this.trackingEmitters.add(new TrackingEmitter(this.level, var1, var2, var3));
   }

   @Nullable
   public Particle createParticle(ParticleOptions var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      Particle var14 = this.makeParticle(var1, var2, var4, var6, var8, var10, var12);
      if (var14 != null) {
         this.add(var14);
         return var14;
      } else {
         return null;
      }
   }

   @Nullable
   private <T extends ParticleOptions> Particle makeParticle(T var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      ParticleProvider var14 = (ParticleProvider)this.resourceManager.getProviders().get(BuiltInRegistries.PARTICLE_TYPE.getId(var1.getType()));
      return var14 == null ? null : var14.createParticle(var1, this.level, var2, var4, var6, var8, var10, var12, this.random);
   }

   public void add(Particle var1) {
      Optional var2 = var1.getParticleLimit();
      if (var2.isPresent()) {
         if (this.hasSpaceInParticleLimit((ParticleLimit)var2.get())) {
            this.particlesToAdd.add(var1);
            this.updateCount((ParticleLimit)var2.get(), 1);
         }
      } else {
         this.particlesToAdd.add(var1);
      }

   }

   public void tick() {
      this.particles.forEach((var0, var1x) -> {
         Profiler.get().push(var0.name());
         var1x.tickParticles();
         Profiler.get().pop();
      });
      if (!this.trackingEmitters.isEmpty()) {
         ArrayList var1 = Lists.newArrayList();

         for(TrackingEmitter var3 : this.trackingEmitters) {
            var3.tick();
            if (!var3.isAlive()) {
               var1.add(var3);
            }
         }

         this.trackingEmitters.removeAll(var1);
      }

      Particle var4;
      if (!this.particlesToAdd.isEmpty()) {
         while((var4 = (Particle)this.particlesToAdd.poll()) != null) {
            ((ParticleGroup)this.particles.computeIfAbsent(var4.getGroup(), this::createParticleGroup)).add(var4);
         }
      }

   }

   private ParticleGroup<?> createParticleGroup(ParticleRenderType var1) {
      if (var1 == ParticleRenderType.ITEM_PICKUP) {
         return new ItemPickupParticleGroup(this);
      } else if (var1 == ParticleRenderType.ELDER_GUARDIANS) {
         return new ElderGuardianParticleGroup(this);
      } else {
         return (ParticleGroup<?>)(var1 == ParticleRenderType.NO_RENDER ? new NoRenderParticleGroup(this) : new QuadParticleGroup(this, var1));
      }
   }

   protected void updateCount(ParticleLimit var1, int var2) {
      this.trackedParticleCounts.addTo(var1, var2);
   }

   public void extract(ParticlesRenderState var1, Frustum var2, Camera var3, float var4) {
      for(ParticleRenderType var6 : RENDER_ORDER) {
         ParticleGroup var7 = (ParticleGroup)this.particles.get(var6);
         if (var7 != null && !var7.isEmpty()) {
            var1.add(var7.extractRenderState(var2, var3, var4));
         }
      }

   }

   public void setLevel(@Nullable ClientLevel var1) {
      this.level = var1;
      this.clearParticles();
      this.trackingEmitters.clear();
   }

   public String countParticles() {
      return String.valueOf(this.particles.values().stream().mapToInt(ParticleGroup::size).sum());
   }

   private boolean hasSpaceInParticleLimit(ParticleLimit var1) {
      return this.trackedParticleCounts.getInt(var1) < var1.limit();
   }

   public void clearParticles() {
      this.particles.clear();
      this.particlesToAdd.clear();
      this.trackingEmitters.clear();
      this.trackedParticleCounts.clear();
   }

   static {
      RENDER_ORDER = List.of(ParticleRenderType.SINGLE_QUADS, ParticleRenderType.ITEM_PICKUP, ParticleRenderType.ELDER_GUARDIANS);
   }
}
