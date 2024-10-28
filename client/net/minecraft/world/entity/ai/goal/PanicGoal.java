package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;

public class PanicGoal extends Goal {
   public static final int WATER_CHECK_DISTANCE_VERTICAL = 1;
   protected final PathfinderMob mob;
   protected final double speedModifier;
   protected double posX;
   protected double posY;
   protected double posZ;
   protected boolean isRunning;
   private final Function<PathfinderMob, TagKey<DamageType>> panicCausingDamageTypes;

   public PanicGoal(PathfinderMob var1, double var2) {
      this(var1, var2, DamageTypeTags.PANIC_CAUSES);
   }

   public PanicGoal(PathfinderMob var1, double var2, TagKey<DamageType> var4) {
      this(var1, var2, (var1x) -> {
         return var4;
      });
   }

   public PanicGoal(PathfinderMob var1, double var2, Function<PathfinderMob, TagKey<DamageType>> var4) {
      super();
      this.mob = var1;
      this.speedModifier = var2;
      this.panicCausingDamageTypes = var4;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   public boolean canUse() {
      if (!this.shouldPanic()) {
         return false;
      } else {
         if (this.mob.isOnFire()) {
            BlockPos var1 = this.lookForWater(this.mob.level(), this.mob, 5);
            if (var1 != null) {
               this.posX = (double)var1.getX();
               this.posY = (double)var1.getY();
               this.posZ = (double)var1.getZ();
               return true;
            }
         }

         return this.findRandomPosition();
      }
   }

   protected boolean shouldPanic() {
      return this.mob.getLastDamageSource() != null && this.mob.getLastDamageSource().is((TagKey)this.panicCausingDamageTypes.apply(this.mob));
   }

   protected boolean findRandomPosition() {
      Vec3 var1 = DefaultRandomPos.getPos(this.mob, 5, 4);
      if (var1 == null) {
         return false;
      } else {
         this.posX = var1.x;
         this.posY = var1.y;
         this.posZ = var1.z;
         return true;
      }
   }

   public boolean isRunning() {
      return this.isRunning;
   }

   public void start() {
      this.mob.getNavigation().moveTo(this.posX, this.posY, this.posZ, this.speedModifier);
      this.isRunning = true;
   }

   public void stop() {
      this.isRunning = false;
   }

   public boolean canContinueToUse() {
      return !this.mob.getNavigation().isDone();
   }

   @Nullable
   protected BlockPos lookForWater(BlockGetter var1, Entity var2, int var3) {
      BlockPos var4 = var2.blockPosition();
      return !var1.getBlockState(var4).getCollisionShape(var1, var4).isEmpty() ? null : (BlockPos)BlockPos.findClosestMatch(var2.blockPosition(), var3, 1, (var1x) -> {
         return var1.getFluidState(var1x).is(FluidTags.WATER);
      }).orElse((Object)null);
   }
}
