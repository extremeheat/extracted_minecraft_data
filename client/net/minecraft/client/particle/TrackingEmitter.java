package net.minecraft.client.particle;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class TrackingEmitter extends NoRenderParticle {
   private final Entity entity;
   private int life;
   private final int lifeTime;
   private final ParticleOptions particleType;

   public TrackingEmitter(Level var1, Entity var2, ParticleOptions var3) {
      this(var1, var2, var3, 3);
   }

   public TrackingEmitter(Level var1, Entity var2, ParticleOptions var3, int var4) {
      this(var1, var2, var3, var4, var2.getDeltaMovement());
   }

   private TrackingEmitter(Level var1, Entity var2, ParticleOptions var3, int var4, Vec3 var5) {
      super(var1, var2.x, var2.getBoundingBox().minY + (double)(var2.getBbHeight() / 2.0F), var2.z, var5.x, var5.y, var5.z);
      this.entity = var2;
      this.lifeTime = var4;
      this.particleType = var3;
      this.tick();
   }

   public void tick() {
      for(int var1 = 0; var1 < 16; ++var1) {
         double var2 = (double)(this.random.nextFloat() * 2.0F - 1.0F);
         double var4 = (double)(this.random.nextFloat() * 2.0F - 1.0F);
         double var6 = (double)(this.random.nextFloat() * 2.0F - 1.0F);
         if (var2 * var2 + var4 * var4 + var6 * var6 <= 1.0D) {
            double var8 = this.entity.x + var2 * (double)this.entity.getBbWidth() / 4.0D;
            double var10 = this.entity.getBoundingBox().minY + (double)(this.entity.getBbHeight() / 2.0F) + var4 * (double)this.entity.getBbHeight() / 4.0D;
            double var12 = this.entity.z + var6 * (double)this.entity.getBbWidth() / 4.0D;
            this.level.addParticle(this.particleType, false, var8, var10, var12, var2, var4 + 0.2D, var6);
         }
      }

      ++this.life;
      if (this.life >= this.lifeTime) {
         this.remove();
      }

   }
}
