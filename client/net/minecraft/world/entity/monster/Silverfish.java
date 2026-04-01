package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.ClimbOnTopOfPowderSnowGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import org.jspecify.annotations.Nullable;

public class Silverfish extends Monster {
   private @Nullable SilverfishWakeUpFriendsGoal friendsGoal;

   public Silverfish(final EntityType<? extends Silverfish> type, final Level level) {
      super(type, level);
   }

   protected void registerGoals() {
      this.friendsGoal = new SilverfishWakeUpFriendsGoal(this);
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(1, new ClimbOnTopOfPowderSnowGoal(this, this.level()));
      this.goalSelector.addGoal(3, this.friendsGoal);
      this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, false));
      this.goalSelector.addGoal(5, new SilverfishMergeWithStoneGoal(this));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[0])).setAlertOthers());
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, LivingBlock.class, true));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 8.0).add(Attributes.MOVEMENT_SPEED, 0.25).add(Attributes.ATTACK_DAMAGE, 1.0);
   }

   protected Entity.MovementEmission getMovementEmission() {
      return Entity.MovementEmission.EVENTS;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.SILVERFISH_AMBIENT;
   }

   protected SoundEvent getHurtSound(final DamageSource source) {
      return SoundEvents.SILVERFISH_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SILVERFISH_DEATH;
   }

   protected void playStepSound(final BlockPos pos, final BlockState blockState) {
      this.playSound(SoundEvents.SILVERFISH_STEP, 0.15F, 1.0F);
   }

   public boolean hurtServer(final ServerLevel level, final DamageSource source, final float damage) {
      if (this.isInvulnerableTo(level, source)) {
         return false;
      } else {
         if ((source.getEntity() != null || source.is(DamageTypeTags.ALWAYS_TRIGGERS_SILVERFISH)) && this.friendsGoal != null) {
            this.friendsGoal.notifyHurt();
         }

         return super.hurtServer(level, source, damage);
      }
   }

   public void tick() {
      this.yBodyRot = this.getYRot();
      super.tick();
   }

   public void setYBodyRot(final float yBodyRot) {
      this.setYRot(yBodyRot);
      super.setYBodyRot(yBodyRot);
   }

   public float getWalkTargetValue(final BlockPos pos, final LevelReader level) {
      return InfestedBlock.isCompatibleHostBlock(level.getBlockState(pos.below())) ? 10.0F : super.getWalkTargetValue(pos, level);
   }

   public static boolean checkSilverfishSpawnRules(final EntityType<Silverfish> type, final LevelAccessor level, final EntitySpawnReason spawnReason, final BlockPos pos, final RandomSource random) {
      if (!checkAnyLightMonsterSpawnRules(type, level, spawnReason, pos, random)) {
         return false;
      } else if (EntitySpawnReason.isSpawner(spawnReason)) {
         return true;
      } else {
         Player nearestPlayer = level.getNearestPlayer((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 5.0, true);
         return nearestPlayer == null;
      }
   }

   private static class SilverfishWakeUpFriendsGoal extends Goal {
      private final Silverfish silverfish;
      private int lookForFriends;

      public SilverfishWakeUpFriendsGoal(final Silverfish silverfish) {
         super();
         this.silverfish = silverfish;
      }

      public void notifyHurt() {
         if (this.lookForFriends == 0) {
            this.lookForFriends = this.adjustedTickDelay(20);
         }

      }

      public boolean canUse() {
         return this.lookForFriends > 0;
      }

      public void tick() {
         --this.lookForFriends;
         if (this.lookForFriends <= 0) {
            Level level = this.silverfish.level();
            RandomSource random = this.silverfish.getRandom();
            BlockPos basePos = this.silverfish.blockPosition();

            for(int yOff = 0; yOff <= 5 && yOff >= -5; yOff = (yOff <= 0 ? 1 : 0) - yOff) {
               for(int xOff = 0; xOff <= 10 && xOff >= -10; xOff = (xOff <= 0 ? 1 : 0) - xOff) {
                  for(int zOff = 0; zOff <= 10 && zOff >= -10; zOff = (zOff <= 0 ? 1 : 0) - zOff) {
                     BlockPos testPos = basePos.offset(xOff, yOff, zOff);
                     BlockState blockState = level.getBlockState(testPos);
                     Block block = blockState.getBlock();
                     if (block instanceof InfestedBlock) {
                        if ((Boolean)getServerLevel(level).getGameRules().get(GameRules.MOB_GRIEFING)) {
                           level.destroyBlock(testPos, true, this.silverfish);
                        } else {
                           level.setBlock(testPos, ((InfestedBlock)block).hostStateByInfested(level.getBlockState(testPos)), 3);
                        }

                        if (random.nextBoolean()) {
                           return;
                        }
                     }
                  }
               }
            }
         }

      }
   }

   private static class SilverfishMergeWithStoneGoal extends RandomStrollGoal {
      private @Nullable Direction selectedDirection;
      private boolean doMerge;

      public SilverfishMergeWithStoneGoal(final Silverfish silverfish) {
         super(silverfish, 1.0, 10);
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canUse() {
         if (this.mob.getTarget() != null) {
            return false;
         } else if (!this.mob.getNavigation().isDone()) {
            return false;
         } else {
            RandomSource random = this.mob.getRandom();
            if ((Boolean)getServerLevel(this.mob).getGameRules().get(GameRules.MOB_GRIEFING) && random.nextInt(reducedTickDelay(10)) == 0) {
               this.selectedDirection = Direction.getRandom(random);
               BlockPos pos = BlockPos.containing(this.mob.getX(), this.mob.getY() + 0.5, this.mob.getZ()).relative(this.selectedDirection);
               BlockState blockState = this.mob.level().getBlockState(pos);
               if (InfestedBlock.isCompatibleHostBlock(blockState)) {
                  this.doMerge = true;
                  return true;
               }
            }

            this.doMerge = false;
            return super.canUse();
         }
      }

      public boolean canContinueToUse() {
         return this.doMerge ? false : super.canContinueToUse();
      }

      public void start() {
         if (!this.doMerge) {
            super.start();
         } else {
            LevelAccessor level = this.mob.level();
            BlockPos pos = BlockPos.containing(this.mob.getX(), this.mob.getY() + 0.5, this.mob.getZ()).relative(this.selectedDirection);
            BlockState blockState = level.getBlockState(pos);
            if (InfestedBlock.isCompatibleHostBlock(blockState)) {
               level.setBlock(pos, InfestedBlock.infestedStateByHost(blockState), 3);
               this.mob.spawnAnim();
               this.mob.discard();
            }

         }
      }
   }
}
