package net.minecraft.world.entity.projectile;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

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

   @Override
   protected Item getDefaultItem() {
      return Items.EXPERIENCE_BOTTLE;
   }

   @Override
   protected double getDefaultGravity() {
      return 0.07;
   }

   @Override
   protected void onHit(HitResult var1) {
      super.onHit(var1);
      if (this.level() instanceof ServerLevel) {
         this.level().levelEvent(2002, this.blockPosition(), PotionContents.getColor(Potions.WATER));
         int var2 = 3 + this.level().random.nextInt(5) + this.level().random.nextInt(5);
         ExperienceOrb.award((ServerLevel)this.level(), this.position(), var2);
         this.discard();
      }
   }
}
