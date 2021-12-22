package net.minecraft.world.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class LargeFireball extends Fireball {
   private int explosionPower = 1;

   public LargeFireball(EntityType<? extends LargeFireball> var1, Level var2) {
      super(var1, var2);
   }

   public LargeFireball(Level var1, LivingEntity var2, double var3, double var5, double var7, int var9) {
      super(EntityType.FIREBALL, var2, var3, var5, var7, var1);
      this.explosionPower = var9;
   }

   protected void onHit(HitResult var1) {
      super.onHit(var1);
      if (!this.level.isClientSide) {
         boolean var2 = this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
         this.level.explode((Entity)null, this.getX(), this.getY(), this.getZ(), (float)this.explosionPower, var2, var2 ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE);
         this.discard();
      }

   }

   protected void onHitEntity(EntityHitResult var1) {
      super.onHitEntity(var1);
      if (!this.level.isClientSide) {
         Entity var2 = var1.getEntity();
         Entity var3 = this.getOwner();
         var2.hurt(DamageSource.fireball(this, var3), 6.0F);
         if (var3 instanceof LivingEntity) {
            this.doEnchantDamageEffects((LivingEntity)var3, var2);
         }

      }
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putByte("ExplosionPower", (byte)this.explosionPower);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.contains("ExplosionPower", 99)) {
         this.explosionPower = var1.getByte("ExplosionPower");
      }

   }
}
