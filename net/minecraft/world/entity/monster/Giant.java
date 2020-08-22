package net.minecraft.world.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

public class Giant extends Monster {
   public Giant(EntityType var1, Level var2) {
      super(var1, var2);
   }

   protected float getStandingEyeHeight(Pose var1, EntityDimensions var2) {
      return 10.440001F;
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(50.0D);
   }

   public float getWalkTargetValue(BlockPos var1, LevelReader var2) {
      return var2.getBrightness(var1) - 0.5F;
   }
}
