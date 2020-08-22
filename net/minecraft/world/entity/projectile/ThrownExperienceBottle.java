package net.minecraft.world.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class ThrownExperienceBottle extends ThrowableItemProjectile {
   public ThrownExperienceBottle(EntityType var1, Level var2) {
      super(var1, var2);
   }

   public ThrownExperienceBottle(Level var1, LivingEntity var2) {
      super(EntityType.EXPERIENCE_BOTTLE, var2, var1);
   }

   public ThrownExperienceBottle(Level var1, double var2, double var4, double var6) {
      super(EntityType.EXPERIENCE_BOTTLE, var2, var4, var6, var1);
   }

   protected Item getDefaultItem() {
      return Items.EXPERIENCE_BOTTLE;
   }

   protected float getGravity() {
      return 0.07F;
   }

   protected void onHit(HitResult var1) {
      if (!this.level.isClientSide) {
         this.level.levelEvent(2002, new BlockPos(this), PotionUtils.getColor(Potions.WATER));
         int var2 = 3 + this.level.random.nextInt(5) + this.level.random.nextInt(5);

         while(var2 > 0) {
            int var3 = ExperienceOrb.getExperienceValue(var2);
            var2 -= var3;
            this.level.addFreshEntity(new ExperienceOrb(this.level, this.getX(), this.getY(), this.getZ(), var3));
         }

         this.remove();
      }

   }
}
