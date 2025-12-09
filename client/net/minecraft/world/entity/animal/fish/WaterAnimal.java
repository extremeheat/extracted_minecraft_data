package net.minecraft.world.entity.animal.fish;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.PathType;

public abstract class WaterAnimal extends PathfinderMob {
   public static final int AMBIENT_SOUND_INTERVAL = 120;

   protected WaterAnimal(EntityType<? extends WaterAnimal> var1, Level var2) {
      super(var1, var2);
      this.setPathfindingMalus(PathType.WATER, 0.0F);
   }

   public boolean checkSpawnObstruction(LevelReader var1) {
      return var1.isUnobstructed(this);
   }

   public int getAmbientSoundInterval() {
      return 120;
   }

   protected int getBaseExperienceReward(ServerLevel var1) {
      return 1 + this.random.nextInt(3);
   }

   protected void handleAirSupply(ServerLevel var1, int var2) {
      if (this.isAlive() && !this.isInWater()) {
         this.setAirSupply(var2 - 1);
         if (this.shouldTakeDrowningDamage()) {
            this.setAirSupply(0);
            this.hurtServer(var1, this.damageSources().drown(), 2.0F);
         }
      } else {
         this.setAirSupply(300);
      }

   }

   public void baseTick() {
      int var1 = this.getAirSupply();
      super.baseTick();
      Level var3 = this.level();
      if (var3 instanceof ServerLevel var2) {
         this.handleAirSupply(var2, var1);
      }

   }

   public boolean isPushedByFluid() {
      return false;
   }

   public boolean canBeLeashed() {
      return false;
   }

   public static boolean checkSurfaceWaterAnimalSpawnRules(EntityType<? extends WaterAnimal> var0, LevelAccessor var1, EntitySpawnReason var2, BlockPos var3, RandomSource var4) {
      int var5 = var1.getSeaLevel();
      int var6 = var5 - 13;
      return var3.getY() >= var6 && var3.getY() <= var5 && var1.getFluidState(var3.below()).is(FluidTags.WATER) && var1.getBlockState(var3.above()).is(Blocks.WATER);
   }
}
