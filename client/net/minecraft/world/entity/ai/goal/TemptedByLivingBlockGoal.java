package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class TemptedByLivingBlockGoal extends Goal {
   private static final TargetingConditions TEMPT_TARGETING = TargetingConditions.forNonCombat().ignoreLineOfSight();
   private static final double DEFAULT_STOP_DISTANCE = 2.5;
   private final TargetingConditions targetingConditions;
   protected final Mob mob;
   protected final double speedModifier;
   protected @Nullable LivingBlock blockToFollow;
   protected @Nullable Player player;
   private int calmDown;
   private int looseInterestTimer;
   private boolean isRunning;
   final TagKey<Block> blockTag;
   private final boolean canScare;
   private final double stopDistance;

   public TemptedByLivingBlockGoal(final PathfinderMob mob, final double speedModifier, final TagKey<Block> blockTag, final boolean canScare) {
      this((Mob)mob, speedModifier, blockTag, canScare, 2.5);
   }

   public TemptedByLivingBlockGoal(final PathfinderMob mob, final double speedModifier, final TagKey<Block> blockTag, final boolean canScare, final double stopDistance) {
      this((Mob)mob, speedModifier, blockTag, canScare, stopDistance);
   }

   private TemptedByLivingBlockGoal(final Mob mob, final double speedModifier, final TagKey<Block> blockTag, final boolean canScare, final double stopDistance) {
      super();
      this.mob = mob;
      this.speedModifier = speedModifier;
      this.blockTag = blockTag;
      this.canScare = canScare;
      this.stopDistance = stopDistance;
      this.looseInterestTimer = 0;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      this.targetingConditions = TEMPT_TARGETING.copy().selector((target, level) -> {
         boolean var10000;
         if (target instanceof LivingEntity livingEntity) {
            if (this.shouldFollow(livingEntity)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      });
   }

   public boolean canUse() {
      if (this.calmDown > 0) {
         --this.calmDown;
         return false;
      } else {
         return this.shouldFollow(this.mob);
      }
   }

   private boolean shouldFollow(final LivingEntity player) {
      ServerLevel level = getServerLevel(player);
      List<LivingBlock> entities = level.getEntities(EntityType.LIVING_BLOCK, this.mob.getBoundingBox().inflate(5.0), (target) -> target.is(EntityType.LIVING_BLOCK));

      for(int i = 0; i < entities.size(); ++i) {
         if (((LivingBlock)entities.get(i)).getBlockState().is(this.blockTag) || ((LivingBlock)entities.get(i)).entityTags().contains(this.blockTag.location().getPath())) {
            this.blockToFollow = (LivingBlock)entities.get(i);
            return true;
         }
      }

      return false;
   }

   public boolean canContinueToUse() {
      return this.blockToFollow != null && this.blockToFollow.isAlive() ? this.canUse() : false;
   }

   public void start() {
      this.isRunning = true;
      this.looseInterestTimer = 0;
   }

   public void stop() {
      this.blockToFollow = null;
      this.stopNavigation();
      this.looseInterestTimer = 0;
      this.calmDown = reducedTickDelay(200);
      this.isRunning = false;
   }

   public void tick() {
      if (this.blockToFollow != null && this.blockToFollow.isAlive()) {
         if (this.looseInterestTimer > 0) {
            --this.looseInterestTimer;
            if (this.looseInterestTimer <= 0) {
               this.stop();
               return;
            }
         }

         this.mob.getLookControl().setLookAt(this.blockToFollow, (float)(this.mob.getMaxHeadYRot() + 20), (float)this.mob.getMaxHeadXRot());
         if (this.mob.distanceToSqr(this.blockToFollow) < this.stopDistance * this.stopDistance) {
            this.stopNavigation();
            if (this.looseInterestTimer <= 0) {
               this.looseInterestTimer = 100;
            }
         } else {
            this.navigateTowards(this.blockToFollow);
         }

      } else {
         this.stopNavigation();
      }
   }

   protected void stopNavigation() {
      this.mob.getNavigation().stop();
   }

   protected void navigateTowards(final LivingBlock blockToFollow) {
      this.mob.getNavigation().moveTo((Entity)blockToFollow, this.speedModifier);
   }

   public boolean isRunning() {
      return this.isRunning;
   }

   public static class ForNonPathfinders extends TemptedByLivingBlockGoal {
      public ForNonPathfinders(final Mob mob, final double speedModifier, final TagKey<Block> blockTag, final boolean canScare, final double stopDistance) {
         super(mob, speedModifier, blockTag, canScare, stopDistance);
      }

      protected void stopNavigation() {
         this.mob.getMoveControl().setWait();
      }

      protected void navigateTowards(final LivingBlock livingBlock) {
         Vec3 target = livingBlock.getEyePosition().subtract(this.mob.position()).scale(this.mob.getRandom().nextDouble()).add(this.mob.position());
         this.mob.getMoveControl().setWantedPosition(target.x, target.y, target.z, this.speedModifier);
      }
   }
}
