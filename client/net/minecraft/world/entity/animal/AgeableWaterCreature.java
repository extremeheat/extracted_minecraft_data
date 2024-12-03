package net.minecraft.world.entity.animal;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.PathType;

public abstract class AgeableWaterCreature extends AgeableMob {
   protected AgeableWaterCreature(EntityType<? extends AgeableWaterCreature> var1, Level var2) {
      super(var1, var2);
      this.setPathfindingMalus(PathType.WATER, 0.0F);
   }

   public boolean checkSpawnObstruction(LevelReader var1) {
      return var1.isUnobstructed(this);
   }

   public int getAmbientSoundInterval() {
      return 120;
   }

   public int getBaseExperienceReward(ServerLevel var1) {
      return 1 + this.random.nextInt(3);
   }

   protected void handleAirSupply(int var1) {
      if (this.isAlive() && !this.isInWaterOrBubble()) {
         this.setAirSupply(var1 - 1);
         if (this.getAirSupply() == -20) {
            this.setAirSupply(0);
            this.hurt(this.damageSources().drown(), 2.0F);
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

   public boolean isPushedByFluid() {
      return false;
   }

   public boolean canBeLeashed() {
      return false;
   }

   public static boolean checkSurfaceAgeableWaterCreatureSpawnRules(EntityType<? extends AgeableWaterCreature> var0, LevelAccessor var1, EntitySpawnReason var2, BlockPos var3, RandomSource var4) {
      int var5 = var1.getSeaLevel();
      int var6 = var5 - 13;
      return var3.getY() >= var6 && var3.getY() <= var5 && var1.getFluidState(var3.below()).is(FluidTags.WATER) && var1.getBlockState(var3.above()).is(Blocks.WATER);
   }
}