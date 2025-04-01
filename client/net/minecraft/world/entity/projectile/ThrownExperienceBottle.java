package net.minecraft.world.entity.projectile;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ThrownExperienceBottle extends ThrowableItemProjectile {
   public ThrownExperienceBottle(EntityType<? extends ThrownExperienceBottle> var1, Level var2) {
      super(var1, var2);
   }

   public ThrownExperienceBottle(Level var1, LivingEntity var2, ItemStack var3) {
      super(EntityType.EXPERIENCE_BOTTLE, var2, var1, var3);
   }

   public ThrownExperienceBottle(Level var1, double var2, double var4, double var6, ItemStack var8) {
      super(EntityType.EXPERIENCE_BOTTLE, var2, var4, var6, var1, var8);
   }

   protected Item getDefaultItem() {
      return Items.EXPERIENCE_BOTTLE;
   }

   protected double getDefaultGravity() {
      return 0.07;
   }

   protected void onHit(HitResult var1) {
      super.onHit(var1);
      if (this.level() instanceof ServerLevel) {
         this.level().levelEvent(2002, this.blockPosition(), -13083194);
         int var2 = 3 + this.level().random.nextInt(5) + this.level().random.nextInt(5);
         if (var1 instanceof BlockHitResult) {
            BlockHitResult var3 = (BlockHitResult)var1;
            Vec3 var4 = var3.getDirection().getUnitVec3();
            ExperienceOrb.awardWithDirection((ServerLevel)this.level(), var1.getLocation().add(var4.scale(0.1)), var4, var2);
         } else {
            ExperienceOrb.awardWithDirection((ServerLevel)this.level(), var1.getLocation(), this.getDeltaMovement().scale(-1.0), var2);
         }

         this.discard();
      }

   }
}
