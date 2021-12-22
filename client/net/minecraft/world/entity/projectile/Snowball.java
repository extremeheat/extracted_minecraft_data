package net.minecraft.world.entity.projectile;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class Snowball extends ThrowableItemProjectile {
   public Snowball(EntityType<? extends Snowball> var1, Level var2) {
      super(var1, var2);
   }

   public Snowball(Level var1, LivingEntity var2) {
      super(EntityType.SNOWBALL, var2, var1);
   }

   public Snowball(Level var1, double var2, double var4, double var6) {
      super(EntityType.SNOWBALL, var2, var4, var6, var1);
   }

   protected Item getDefaultItem() {
      return Items.SNOWBALL;
   }

   private ParticleOptions getParticle() {
      ItemStack var1 = this.getItemRaw();
      return (ParticleOptions)(var1.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemParticleOption(ParticleTypes.ITEM, var1));
   }

   public void handleEntityEvent(byte var1) {
      if (var1 == 3) {
         ParticleOptions var2 = this.getParticle();

         for(int var3 = 0; var3 < 8; ++var3) {
            this.level.addParticle(var2, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
         }
      }

   }

   protected void onHitEntity(EntityHitResult var1) {
      super.onHitEntity(var1);
      Entity var2 = var1.getEntity();
      int var3 = var2 instanceof Blaze ? 3 : 0;
      var2.hurt(DamageSource.thrown(this, this.getOwner()), (float)var3);
   }

   protected void onHit(HitResult var1) {
      super.onHit(var1);
      if (!this.level.isClientSide) {
         this.level.broadcastEntityEvent(this, (byte)3);
         this.discard();
      }

   }
}
