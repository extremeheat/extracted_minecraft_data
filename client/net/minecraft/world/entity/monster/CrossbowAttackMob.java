package net.minecraft.world.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public interface CrossbowAttackMob extends RangedAttackMob {
   void setChargingCrossbow(boolean var1);

   void shootCrossbowProjectile(LivingEntity var1, ItemStack var2, Projectile var3, float var4);

   @Nullable
   LivingEntity getTarget();

   void onCrossbowAttackPerformed();

   default void performCrossbowAttack(LivingEntity var1, float var2) {
      InteractionHand var3 = ProjectileUtil.getWeaponHoldingHand(var1, Items.CROSSBOW);
      ItemStack var4 = var1.getItemInHand(var3);
      if (var1.isHolding(Items.CROSSBOW)) {
         CrossbowItem.performShooting(var1.level(), var1, var3, var4, var2, (float)(14 - var1.level().getDifficulty().getId() * 4));
      }

      this.onCrossbowAttackPerformed();
   }

   default void shootCrossbowProjectile(LivingEntity var1, LivingEntity var2, Projectile var3, float var4, float var5) {
      double var6 = var2.getX() - var1.getX();
      double var8 = var2.getZ() - var1.getZ();
      double var10 = Math.sqrt(var6 * var6 + var8 * var8);
      double var12 = var2.getY(0.3333333333333333) - var3.getY() + var10 * 0.20000000298023224;
      Vector3f var14 = this.getProjectileShotVector(var1, new Vec3(var6, var12, var8), var4);
      var3.shoot((double)var14.x(), (double)var14.y(), (double)var14.z(), var5, (float)(14 - var1.level().getDifficulty().getId() * 4));
      var1.playSound(SoundEvents.CROSSBOW_SHOOT, 1.0F, 1.0F / (var1.getRandom().nextFloat() * 0.4F + 0.8F));
   }

   default Vector3f getProjectileShotVector(LivingEntity var1, Vec3 var2, float var3) {
      Vector3f var4 = var2.toVector3f().normalize();
      Vector3f var5 = new Vector3f(var4).cross(new Vector3f(0.0F, 1.0F, 0.0F));
      if ((double)var5.lengthSquared() <= 1.0E-7) {
         Vec3 var6 = var1.getUpVector(1.0F);
         var5 = new Vector3f(var4).cross(var6.toVector3f());
      }

      Vector3f var7 = new Vector3f(var4).rotateAxis(1.5707964F, var5.x, var5.y, var5.z);
      return new Vector3f(var4).rotateAxis(var3 * 0.017453292F, var7.x, var7.y, var7.z);
   }
}
