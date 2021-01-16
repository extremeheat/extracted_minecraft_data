package net.minecraft.world.entity.projectile;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.item.Item;
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
         this.remove();
      }

   }
}
