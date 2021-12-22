package net.minecraft.world.entity.projectile;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ThrownEgg extends ThrowableItemProjectile {
   public ThrownEgg(EntityType<? extends ThrownEgg> var1, Level var2) {
      super(var1, var2);
   }

   public ThrownEgg(Level var1, LivingEntity var2) {
      super(EntityType.EGG, var2, var1);
   }

   public ThrownEgg(Level var1, double var2, double var4, double var6) {
      super(EntityType.EGG, var2, var4, var6, var1);
   }

   public void handleEntityEvent(byte var1) {
      if (var1 == 3) {
         double var2 = 0.08D;

         for(int var4 = 0; var4 < 8; ++var4) {
            this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItem()), this.getX(), this.getY(), this.getZ(), ((double)this.random.nextFloat() - 0.5D) * 0.08D, ((double)this.random.nextFloat() - 0.5D) * 0.08D, ((double)this.random.nextFloat() - 0.5D) * 0.08D);
         }
      }

   }

   protected void onHitEntity(EntityHitResult var1) {
      super.onHitEntity(var1);
      var1.getEntity().hurt(DamageSource.thrown(this, this.getOwner()), 0.0F);
   }

   protected void onHit(HitResult var1) {
      super.onHit(var1);
      if (!this.level.isClientSide) {
         if (this.random.nextInt(8) == 0) {
            byte var2 = 1;
            if (this.random.nextInt(32) == 0) {
               var2 = 4;
            }

            for(int var3 = 0; var3 < var2; ++var3) {
               Chicken var4 = (Chicken)EntityType.CHICKEN.create(this.level);
               var4.setAge(-24000);
               var4.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
               this.level.addFreshEntity(var4);
            }
         }

         this.level.broadcastEntityEvent(this, (byte)3);
         this.discard();
      }

   }

   protected Item getDefaultItem() {
      return Items.EGG;
   }
}
