package net.minecraft.world.entity.monster;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
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
         CrossbowItem.performShooting(var1.level, var1, var3, var4, var2, (float)(14 - var1.level.getDifficulty().getId() * 4));
      }

      this.onCrossbowAttackPerformed();
   }

   default void shootCrossbowProjectile(LivingEntity var1, LivingEntity var2, Projectile var3, float var4, float var5) {
      double var7 = var2.getX() - var1.getX();
      double var9 = var2.getZ() - var1.getZ();
      double var11 = Math.sqrt(var7 * var7 + var9 * var9);
      double var13 = var2.getY(0.3333333333333333) - var3.getY() + var11 * 0.20000000298023224;
      Vector3f var15 = this.getProjectileShotVector(var1, new Vec3(var7, var13, var9), var4);
      var3.shoot((double)var15.x(), (double)var15.y(), (double)var15.z(), var5, (float)(14 - var1.level.getDifficulty().getId() * 4));
      var1.playSound(SoundEvents.CROSSBOW_SHOOT, 1.0F, 1.0F / (var1.getRandom().nextFloat() * 0.4F + 0.8F));
   }

   default Vector3f getProjectileShotVector(LivingEntity var1, Vec3 var2, float var3) {
      Vec3 var4 = var2.normalize();
      Vec3 var5 = var4.cross(new Vec3(0.0, 1.0, 0.0));
      if (var5.lengthSqr() <= 1.0E-7) {
         var5 = var4.cross(var1.getUpVector(1.0F));
      }

      Quaternion var6 = new Quaternion(new Vector3f(var5), 90.0F, true);
      Vector3f var7 = new Vector3f(var4);
      var7.transform(var6);
      Quaternion var8 = new Quaternion(var7, var3, true);
      Vector3f var9 = new Vector3f(var4);
      var9.transform(var8);
      return var9;
   }
}
