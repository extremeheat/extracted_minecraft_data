package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraft.world.level.block.state.BlockState;

public class Silverfish extends Monster {
   private Silverfish.SilverfishWakeUpFriendsGoal friendsGoal;

   public Silverfish(EntityType<? extends Silverfish> var1, Level var2) {
      super(var1, var2);
   }

   protected void registerGoals() {
      this.friendsGoal = new Silverfish.SilverfishWakeUpFriendsGoal(this);
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(3, this.friendsGoal);
      this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, false));
      this.goalSelector.addGoal(5, new Silverfish.SilverfishMergeWithStoneGoal(this));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[0])).setAlertOthers());
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
   }

   public double getMyRidingOffset() {
      return 0.1D;
   }

   protected float getStandingEyeHeight(Pose var1, EntityDimensions var2) {
      return 0.13F;
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 8.0D).add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.ATTACK_DAMAGE, 1.0D);
   }

   protected boolean isMovementNoisy() {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.SILVERFISH_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.SILVERFISH_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SILVERFISH_DEATH;
   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.SILVERFISH_STEP, 0.15F, 1.0F);
   }

   public boolean hurt(DamageSource var1, float var2) {
      if (this.isInvulnerableTo(var1)) {
         return false;
      } else {
         if ((var1 instanceof EntityDamageSource || var1 == DamageSource.MAGIC) && this.friendsGoal != null) {
            this.friendsGoal.notifyHurt();
         }

         return super.hurt(var1, var2);
      }
   }

   public void tick() {
      this.yBodyRot = this.yRot;
      super.tick();
   }

   public void setYBodyRot(float var1) {
      this.yRot = var1;
      super.setYBodyRot(var1);
   }

   public float getWalkTargetValue(BlockPos var1, LevelReader var2) {
      return InfestedBlock.isCompatibleHostBlock(var2.getBlockState(var1.below())) ? 10.0F : super.getWalkTargetValue(var1, var2);
   }

   public static boolean checkSliverfishSpawnRules(EntityType<Silverfish> var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, Random var4) {
      if (checkAnyLightMonsterSpawnRules(var0, var1, var2, var3, var4)) {
         Player var5 = var1.getNearestPlayer((double)var3.getX() + 0.5D, (double)var3.getY() + 0.5D, (double)var3.getZ() + 0.5D, 5.0D, true);
         return var5 == null;
      } else {
         return false;
      }
   }

   public MobType getMobType() {
      return MobType.ARTHROPOD;
   }

   static class SilverfishMergeWithStoneGoal extends RandomStrollGoal {
      private Direction selectedDirection;
      private boolean doMerge;

      public SilverfishMergeWithStoneGoal(Silverfish var1) {
         super(var1, 1.0D, 10);
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canUse() {
         if (this.mob.getTarget() != null) {
            return false;
         } else if (!this.mob.getNavigation().isDone()) {
            return false;
         } else {
            Random var1 = this.mob.getRandom();
            if (this.mob.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && var1.nextInt(10) == 0) {
               this.selectedDirection = Direction.getRandom(var1);
               BlockPos var2 = (new BlockPos(this.mob.getX(), this.mob.getY() + 0.5D, this.mob.getZ())).relative(this.selectedDirection);
               BlockState var3 = this.mob.level.getBlockState(var2);
               if (InfestedBlock.isCompatibleHostBlock(var3)) {
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
            Level var1 = this.mob.level;
            BlockPos var2 = (new BlockPos(this.mob.getX(), this.mob.getY() + 0.5D, this.mob.getZ())).relative(this.selectedDirection);
            BlockState var3 = var1.getBlockState(var2);
            if (InfestedBlock.isCompatibleHostBlock(var3)) {
               var1.setBlock(var2, InfestedBlock.stateByHostBlock(var3.getBlock()), 3);
               this.mob.spawnAnim();
               this.mob.remove();
            }

         }
      }
   }

   static class SilverfishWakeUpFriendsGoal extends Goal {
      private final Silverfish silverfish;
      private int lookForFriends;

      public SilverfishWakeUpFriendsGoal(Silverfish var1) {
         super();
         this.silverfish = var1;
      }

      public void notifyHurt() {
         if (this.lookForFriends == 0) {
            this.lookForFriends = 20;
         }

      }

      public boolean canUse() {
         return this.lookForFriends > 0;
      }

      public void tick() {
         --this.lookForFriends;
         if (this.lookForFriends <= 0) {
            Level var1 = this.silverfish.level;
            Random var2 = this.silverfish.getRandom();
            BlockPos var3 = this.silverfish.blockPosition();

            for(int var4 = 0; var4 <= 5 && var4 >= -5; var4 = (var4 <= 0 ? 1 : 0) - var4) {
               for(int var5 = 0; var5 <= 10 && var5 >= -10; var5 = (var5 <= 0 ? 1 : 0) - var5) {
                  for(int var6 = 0; var6 <= 10 && var6 >= -10; var6 = (var6 <= 0 ? 1 : 0) - var6) {
                     BlockPos var7 = var3.offset(var5, var4, var6);
                     BlockState var8 = var1.getBlockState(var7);
                     Block var9 = var8.getBlock();
                     if (var9 instanceof InfestedBlock) {
                        if (var1.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                           var1.destroyBlock(var7, true, this.silverfish);
                        } else {
                           var1.setBlock(var7, ((InfestedBlock)var9).getHostBlock().defaultBlockState(), 3);
                        }

                        if (var2.nextBoolean()) {
                           return;
                        }
                     }
                  }
               }
            }
         }

      }
   }
}
