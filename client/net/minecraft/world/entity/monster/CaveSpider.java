package net.minecraft.world.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

public class CaveSpider extends Spider {
   public CaveSpider(EntityType<? extends CaveSpider> var1, Level var2) {
      super(var1, var2);
   }

   public static AttributeSupplier.Builder createCaveSpider() {
      return Spider.createAttributes().add(Attributes.MAX_HEALTH, 12.0);
   }

   @Override
   public boolean doHurtTarget(ServerLevel var1, Entity var2) {
      if (super.doHurtTarget(var1, var2)) {
         if (var2 instanceof LivingEntity) {
            byte var3 = 0;
            if (this.level().getDifficulty() == Difficulty.NORMAL) {
               var3 = 7;
            } else if (this.level().getDifficulty() == Difficulty.HARD) {
               var3 = 15;
            }

            if (var3 > 0) {
               ((LivingEntity)var2).addEffect(new MobEffectInstance(MobEffects.POISON, var3 * 20, 0), this);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Nullable
   @Override
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, EntitySpawnReason var3, @Nullable SpawnGroupData var4) {
      return var4;
   }

   @Override
   public Vec3 getVehicleAttachmentPoint(Entity var1) {
      return var1.getBbWidth() <= this.getBbWidth() ? new Vec3(0.0, 0.21875 * (double)this.getScale(), 0.0) : super.getVehicleAttachmentPoint(var1);
   }
}
