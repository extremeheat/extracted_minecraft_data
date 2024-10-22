package net.minecraft.world.entity.monster.creaking;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CreakingHeartBlock;
import net.minecraft.world.level.block.entity.CreakingHeartBlockEntity;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class CreakingTransient extends Creaking {
   public static final int INVULNERABILITY_ANIMATION_DURATION = 8;
   private int invulnerabilityAnimationRemainingTicks;
   @Nullable
   BlockPos homePos;

   public CreakingTransient(EntityType<? extends Creaking> var1, Level var2) {
      super(var1, var2);
   }

   public void bindToCreakingHeart(BlockPos var1) {
      this.homePos = var1;
   }

   @Override
   public boolean hurtServer(ServerLevel var1, DamageSource var2, float var3) {
      if (this.level().isClientSide) {
         return super.hurtServer(var1, var2, var3);
      } else if (var2.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         return super.hurtServer(var1, var2, var3);
      } else if (!this.isInvulnerableTo(var1, var2) && this.invulnerabilityAnimationRemainingTicks <= 0) {
         this.invulnerabilityAnimationRemainingTicks = 8;
         this.level().broadcastEntityEvent(this, (byte)66);
         if (this.level().getBlockEntity(this.homePos) instanceof CreakingHeartBlockEntity var4 && var4.isProtector(this)) {
            if (var2.getEntity() instanceof Player) {
               var4.creakingHurt();
            }

            this.playHurtSound(var2);
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void aiStep() {
      if (this.invulnerabilityAnimationRemainingTicks > 0) {
         this.invulnerabilityAnimationRemainingTicks--;
      }

      super.aiStep();
   }

   @Override
   public void tick() {
      if (this.level().isClientSide
         || this.homePos != null && this.level().getBlockEntity(this.homePos) instanceof CreakingHeartBlockEntity var1 && var1.isProtector(this)) {
         super.tick();
         if (this.level().isClientSide) {
            this.setupAnimationStates();
         }
      } else {
         this.setRemoved(Entity.RemovalReason.DISCARDED);
      }
   }

   @Override
   public void handleEntityEvent(byte var1) {
      if (var1 == 66) {
         this.invulnerabilityAnimationRemainingTicks = 8;
         this.playHurtSound(this.damageSources().generic());
      } else {
         super.handleEntityEvent(var1);
      }
   }

   private void setupAnimationStates() {
      this.invulnerabilityAnimationState.animateWhen(this.invulnerabilityAnimationRemainingTicks > 0, this.tickCount);
   }

   public void tearDown(@Nullable DamageSource var1) {
      if (this.level() instanceof ServerLevel var2) {
         AABB var12 = this.getBoundingBox();
         Vec3 var4 = var12.getCenter();
         double var5 = var12.getXsize() * 0.3;
         double var7 = var12.getYsize() * 0.3;
         double var9 = var12.getZsize() * 0.3;
         var2.sendParticles(
            new BlockParticleOption(ParticleTypes.BLOCK_CRUMBLE, Blocks.PALE_OAK_WOOD.defaultBlockState()), var4.x, var4.y, var4.z, 100, var5, var7, var9, 0.0
         );
         var2.sendParticles(
            new BlockParticleOption(
               ParticleTypes.BLOCK_CRUMBLE,
               Blocks.CREAKING_HEART.defaultBlockState().setValue(CreakingHeartBlock.CREAKING, CreakingHeartBlock.CreakingHeartState.ACTIVE)
            ),
            var4.x,
            var4.y,
            var4.z,
            10,
            var5,
            var7,
            var9,
            0.0
         );
      }

      this.makeSound(this.getDeathSound());
      if (this.deathScore >= 0 && var1 != null && var1.getEntity() instanceof LivingEntity var11) {
         var11.awardKillScore(this, this.deathScore, var1);
      }

      this.remove(Entity.RemovalReason.DISCARDED);
   }

   @Override
   protected boolean canAddPassenger(Entity var1) {
      return false;
   }

   @Override
   protected boolean couldAcceptPassenger() {
      return false;
   }

   @Override
   protected void addPassenger(Entity var1) {
      throw new IllegalStateException("Should never addPassenger without checking couldAcceptPassenger()");
   }

   @Override
   public boolean canUsePortal(boolean var1) {
      return false;
   }

   @Override
   protected PathNavigation createNavigation(Level var1) {
      return new CreakingTransient.CreakingPathNavigation(this, var1);
   }

   class CreakingPathNavigation extends GroundPathNavigation {
      CreakingPathNavigation(final Creaking nullx, final Level nullxx) {
         super(nullx, nullxx);
      }

      @Override
      public void tick() {
         if (CreakingTransient.this.canMove()) {
            super.tick();
         }
      }

      @Override
      protected PathFinder createPathFinder(int var1) {
         this.nodeEvaluator = CreakingTransient.this.new HomeNodeEvaluator();
         return new PathFinder(this.nodeEvaluator, var1);
      }
   }

   class HomeNodeEvaluator extends WalkNodeEvaluator {
      private static final int MAX_DISTANCE_TO_HOME_SQ = 1024;

      HomeNodeEvaluator() {
         super();
      }

      @Override
      public PathType getPathType(PathfindingContext var1, int var2, int var3, int var4) {
         BlockPos var5 = CreakingTransient.this.homePos;
         if (var5 == null) {
            return super.getPathType(var1, var2, var3, var4);
         } else {
            double var6 = var5.distSqr(new Vec3i(var2, var3, var4));
            return var6 > 1024.0 && var6 >= var5.distSqr(var1.mobPosition()) ? PathType.BLOCKED : super.getPathType(var1, var2, var3, var4);
         }
      }
   }
}
