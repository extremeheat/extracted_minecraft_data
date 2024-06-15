package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class TrackingEmitter extends NoRenderParticle {
   private final Entity entity;
   private int life;
   private final int lifeTime;
   private final ParticleOptions particleType;

   public TrackingEmitter(ClientLevel var1, Entity var2, ParticleOptions var3) {
      this(var1, var2, var3, 3);
   }

   public TrackingEmitter(ClientLevel var1, Entity var2, ParticleOptions var3, int var4) {
      this(var1, var2, var3, var4, var2.getDeltaMovement());
   }

   private TrackingEmitter(ClientLevel var1, Entity var2, ParticleOptions var3, int var4, Vec3 var5) {
      super(var1, var2.getX(), var2.getY(0.5), var2.getZ(), var5.x, var5.y, var5.z);
      this.entity = var2;
      this.lifeTime = var4;
      this.particleType = var3;
      this.tick();
   }

   @Override
   public void tick() {
      for (int var1 = 0; var1 < 16; var1++) {
         double var2 = (double)(this.random.nextFloat() * 2.0F - 1.0F);
         double var4 = (double)(this.random.nextFloat() * 2.0F - 1.0F);
         double var6 = (double)(this.random.nextFloat() * 2.0F - 1.0F);
         if (!(var2 * var2 + var4 * var4 + var6 * var6 > 1.0)) {
            double var8 = this.entity.getX(var2 / 4.0);
            double var10 = this.entity.getY(0.5 + var4 / 4.0);
            double var12 = this.entity.getZ(var6 / 4.0);
            this.level.addParticle(this.particleType, false, var8, var10, var12, var2, var4 + 0.2, var6);
         }
      }

      this.life++;
      if (this.life >= this.lifeTime) {
         this.remove();
      }
   }
}
