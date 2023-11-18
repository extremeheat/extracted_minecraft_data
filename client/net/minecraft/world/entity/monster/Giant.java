package net.minecraft.world.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

public class Giant extends Monster {
   public Giant(EntityType<? extends Giant> var1, Level var2) {
      super(var1, var2);
   }

   @Override
   protected float getStandingEyeHeight(Pose var1, EntityDimensions var2) {
      return 10.440001F;
   }

   @Override
   protected float ridingOffset(Entity var1) {
      return -3.75F;
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 100.0).add(Attributes.MOVEMENT_SPEED, 0.5).add(Attributes.ATTACK_DAMAGE, 50.0);
   }

   @Override
   public float getWalkTargetValue(BlockPos var1, LevelReader var2) {
      return var2.getPathfindingCostFromLightLevels(var1);
   }
}
