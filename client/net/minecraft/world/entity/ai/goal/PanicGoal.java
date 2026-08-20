package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class PanicGoal extends Goal {
   public static final int WATER_CHECK_DISTANCE_VERTICAL = 1;
   protected final PathfinderMob mob;
   protected final double speedModifier;
   protected double posX;
   protected double posY;
   protected double posZ;
   protected boolean isRunning;
   private final Function<PathfinderMob, TagKey<DamageType>> panicCausingDamageTypes;

   public PanicGoal(final PathfinderMob mob, final double speedModifier) {
      this(mob, speedModifier, DamageTypeTags.PANIC_CAUSES);
   }

   public PanicGoal(final PathfinderMob mob, final double speedModifier, final TagKey<DamageType> panicCausingDamageTypes) {
      this(mob, speedModifier, (Function)((entity) -> panicCausingDamageTypes));
   }

   public PanicGoal(final PathfinderMob mob, final double speedModifier, final Function<PathfinderMob, TagKey<DamageType>> panicCausingDamageTypes) {
      super();
      this.mob = mob;
      this.speedModifier = speedModifier;
      this.panicCausingDamageTypes = panicCausingDamageTypes;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   public boolean canUse() {
      if (!this.shouldPanic()) {
         return false;
      } else {
         if (this.mob.isOnFire()) {
            BlockPos blockPos = this.lookForWater(this.mob.level(), this.mob, 5);
            if (blockPos != null) {
               this.posX = (double)blockPos.getX();
               this.posY = (double)blockPos.getY();
               this.posZ = (double)blockPos.getZ();
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
      Vec3 pos = DefaultRandomPos.getPos(this.mob, 5, 4);
      if (pos == null) {
         return false;
      } else {
         this.posX = pos.x;
         this.posY = pos.y;
         this.posZ = pos.z;
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

   protected @Nullable BlockPos lookForWater(final LevelReader level, final Entity mob, final int xzDist) {
      BlockPos mobPosition = mob.blockPosition();
      return !level.getBlockState(mobPosition).getCollisionShape(level, mobPosition).isEmpty() ? null : (BlockPos)level.findBlocksInBoxByManhattanDistance(mobPosition, xzDist, 1).filterState((state) -> state.getFluidState().is(FluidTags.WATER)).findFirst().orElse((Object)null);
   }
}
