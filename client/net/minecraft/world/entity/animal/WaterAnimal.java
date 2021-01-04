package net.minecraft.world.entity.animal;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

public abstract class WaterAnimal extends PathfinderMob {
   protected WaterAnimal(EntityType<? extends WaterAnimal> var1, Level var2) {
      super(var1, var2);
   }

   public boolean canBreatheUnderwater() {
      return true;
   }

   public MobType getMobType() {
      return MobType.WATER;
   }

   public boolean checkSpawnObstruction(LevelReader var1) {
      return var1.isUnobstructed(this);
   }

   public int getAmbientSoundInterval() {
      return 120;
   }

   protected int getExperienceReward(Player var1) {
      return 1 + this.level.random.nextInt(3);
   }

   protected void handleAirSupply(int var1) {
      if (this.isAlive() && !this.isInWaterOrBubble()) {
         this.setAirSupply(var1 - 1);
         if (this.getAirSupply() == -20) {
            this.setAirSupply(0);
            this.hurt(DamageSource.DROWN, 2.0F);
         }
      } else {
         this.setAirSupply(300);
      }

   }

   public void baseTick() {
      int var1 = this.getAirSupply();
      super.baseTick();
      this.handleAirSupply(var1);
   }

   public boolean isPushedByWater() {
      return false;
   }

   public boolean canBeLeashed(Player var1) {
      return false;
   }
}
