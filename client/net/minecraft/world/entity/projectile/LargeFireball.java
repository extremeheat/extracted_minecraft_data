package net.minecraft.world.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class LargeFireball extends Fireball {
   private int explosionPower = 1;

   public LargeFireball(EntityType<? extends LargeFireball> var1, Level var2) {
      super(var1, var2);
   }

   public LargeFireball(Level var1, LivingEntity var2, Vec3 var3, int var4) {
      super(EntityType.FIREBALL, var2, var3, var1);
      this.explosionPower = var4;
   }

   protected void onHit(HitResult var1) {
      super.onHit(var1);
      Level var3 = this.level();
      if (var3 instanceof ServerLevel var2) {
         boolean var4 = var2.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
         this.level().explode(this, this.getX(), this.getY(), this.getZ(), (float)this.explosionPower, var4, Level.ExplosionInteraction.MOB);
         this.discard();
      }

   }

   protected void onHitEntity(EntityHitResult var1) {
      super.onHitEntity(var1);
      Level var3 = this.level();
      if (var3 instanceof ServerLevel var2) {
         Entity var6 = var1.getEntity();
         Entity var4 = this.getOwner();
         DamageSource var5 = this.damageSources().fireball(this, var4);
         var6.hurtServer(var2, var5, 6.0F);
         EnchantmentHelper.doPostAttackEffects(var2, var6, var5);
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
