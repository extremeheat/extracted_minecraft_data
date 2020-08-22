package net.minecraft.world.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class CaveSpider extends Spider {
   public CaveSpider(EntityType var1, Level var2) {
      super(var1, var2);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(12.0D);
   }

   public boolean doHurtTarget(Entity var1) {
      if (super.doHurtTarget(var1)) {
         if (var1 instanceof LivingEntity) {
            byte var2 = 0;
            if (this.level.getDifficulty() == Difficulty.NORMAL) {
               var2 = 7;
            } else if (this.level.getDifficulty() == Difficulty.HARD) {
               var2 = 15;
            }

            if (var2 > 0) {
               ((LivingEntity)var1).addEffect(new MobEffectInstance(MobEffects.POISON, var2 * 20, 0));
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(LevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5) {
      return var4;
   }

   protected float getStandingEyeHeight(Pose var1, EntityDimensions var2) {
      return 0.45F;
   }
}
