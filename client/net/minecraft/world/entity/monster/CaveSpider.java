package net.minecraft.world.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
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

   @Override
   public boolean hasPotatoVariant() {
      return false;
   }

   public static AttributeSupplier.Builder createCaveSpider() {
      return Spider.createAttributes().add(Attributes.MAX_HEALTH, 12.0);
   }

   @Override
   public boolean doHurtTarget(Entity var1) {
      if (super.doHurtTarget(var1)) {
         poisonMethodThatSpidersUse(var1, this);
         return true;
      } else {
         return false;
      }
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static void poisonMethodThatSpidersUse(Entity var0, @Nullable Entity var1) {
      if (var0 instanceof LivingEntity var2) {
         byte var3 = 0;
         if (var0.level().getDifficulty() == Difficulty.NORMAL) {
            var3 = 7;
         } else if (var0.level().getDifficulty() == Difficulty.HARD) {
            var3 = 15;
         }

         if (var3 > 0) {
            var2.addEffect(new MobEffectInstance(MobEffects.POISON, var3 * 20, 0), var1);
         }
      }
   }

   @Nullable
   @Override
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4) {
      return var4;
   }

   @Override
   public Vec3 getVehicleAttachmentPoint(Entity var1) {
      return var1.getBbWidth() <= this.getBbWidth() ? new Vec3(0.0, 0.21875 * (double)this.getScale(), 0.0) : super.getVehicleAttachmentPoint(var1);
   }
}
