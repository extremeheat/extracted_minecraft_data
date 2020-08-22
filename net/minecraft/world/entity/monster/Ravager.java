package net.minecraft.world.entity.monster;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Ravager extends Raider {
   private static final Predicate NO_RAVAGER_AND_ALIVE = (var0) -> {
      return var0.isAlive() && !(var0 instanceof Ravager);
   };
   private int attackTick;
   private int stunnedTick;
   private int roarTick;

   public Ravager(EntityType var1, Level var2) {
      super(var1, var2);
      this.maxUpStep = 1.0F;
      this.xpReward = 20;
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(4, new Ravager.RavagerMeleeAttackGoal());
      this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.4D));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
      this.targetSelector.addGoal(2, (new HurtByTargetGoal(this, new Class[]{Raider.class})).setAlertOthers());
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Player.class, true));
      this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, AbstractVillager.class, true));
      this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, IronGolem.class, true));
   }

   protected void updateControlFlags() {
      boolean var1 = !(this.getControllingPassenger() instanceof Mob) || this.getControllingPassenger().getType().is(EntityTypeTags.RAIDERS);
      boolean var2 = !(this.getVehicle() instanceof Boat);
      this.goalSelector.setControlFlag(Goal.Flag.MOVE, var1);
      this.goalSelector.setControlFlag(Goal.Flag.JUMP, var1 && var2);
      this.goalSelector.setControlFlag(Goal.Flag.LOOK, var1);
      this.goalSelector.setControlFlag(Goal.Flag.TARGET, var1);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
      this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.5D);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(12.0D);
      this.getAttribute(SharedMonsterAttributes.ATTACK_KNOCKBACK).setBaseValue(1.5D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("AttackTick", this.attackTick);
      var1.putInt("StunTick", this.stunnedTick);
      var1.putInt("RoarTick", this.roarTick);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.attackTick = var1.getInt("AttackTick");
      this.stunnedTick = var1.getInt("StunTick");
      this.roarTick = var1.getInt("RoarTick");
   }

   public SoundEvent getCelebrateSound() {
      return SoundEvents.RAVAGER_CELEBRATE;
   }

   protected PathNavigation createNavigation(Level var1) {
      return new Ravager.RavagerNavigation(this, var1);
   }

   public int getMaxHeadYRot() {
      return 45;
   }

   public double getRideHeight() {
      return 2.1D;
   }

   public boolean canBeControlledByRider() {
      return !this.isNoAi() && this.getControllingPassenger() instanceof LivingEntity;
   }

   @Nullable
   public Entity getControllingPassenger() {
      return this.getPassengers().isEmpty() ? null : (Entity)this.getPassengers().get(0);
   }

   public void aiStep() {
      super.aiStep();
      if (this.isAlive()) {
         if (this.isImmobile()) {
            this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
         } else {
            double var1 = this.getTarget() != null ? 0.35D : 0.3D;
            double var3 = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue();
            this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(Mth.lerp(0.1D, var3, var1));
         }

         if (this.horizontalCollision && this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            boolean var7 = false;
            AABB var2 = this.getBoundingBox().inflate(0.2D);
            Iterator var8 = BlockPos.betweenClosed(Mth.floor(var2.minX), Mth.floor(var2.minY), Mth.floor(var2.minZ), Mth.floor(var2.maxX), Mth.floor(var2.maxY), Mth.floor(var2.maxZ)).iterator();

            label60:
            while(true) {
               BlockPos var4;
               Block var6;
               do {
                  if (!var8.hasNext()) {
                     if (!var7 && this.onGround) {
                        this.jumpFromGround();
                     }
                     break label60;
                  }

                  var4 = (BlockPos)var8.next();
                  BlockState var5 = this.level.getBlockState(var4);
                  var6 = var5.getBlock();
               } while(!(var6 instanceof LeavesBlock));

               var7 = this.level.destroyBlock(var4, true, this) || var7;
            }
         }

         if (this.roarTick > 0) {
            --this.roarTick;
            if (this.roarTick == 10) {
               this.roar();
            }
         }

         if (this.attackTick > 0) {
            --this.attackTick;
         }

         if (this.stunnedTick > 0) {
            --this.stunnedTick;
            this.stunEffect();
            if (this.stunnedTick == 0) {
               this.playSound(SoundEvents.RAVAGER_ROAR, 1.0F, 1.0F);
               this.roarTick = 20;
            }
         }

      }
   }

   private void stunEffect() {
      if (this.random.nextInt(6) == 0) {
         double var1 = this.getX() - (double)this.getBbWidth() * Math.sin((double)(this.yBodyRot * 0.017453292F)) + (this.random.nextDouble() * 0.6D - 0.3D);
         double var3 = this.getY() + (double)this.getBbHeight() - 0.3D;
         double var5 = this.getZ() + (double)this.getBbWidth() * Math.cos((double)(this.yBodyRot * 0.017453292F)) + (this.random.nextDouble() * 0.6D - 0.3D);
         this.level.addParticle(ParticleTypes.ENTITY_EFFECT, var1, var3, var5, 0.4980392156862745D, 0.5137254901960784D, 0.5725490196078431D);
      }

   }

   protected boolean isImmobile() {
      return super.isImmobile() || this.attackTick > 0 || this.stunnedTick > 0 || this.roarTick > 0;
   }

   public boolean canSee(Entity var1) {
      return this.stunnedTick <= 0 && this.roarTick <= 0 ? super.canSee(var1) : false;
   }

   protected void blockedByShield(LivingEntity var1) {
      if (this.roarTick == 0) {
         if (this.random.nextDouble() < 0.5D) {
            this.stunnedTick = 40;
            this.playSound(SoundEvents.RAVAGER_STUNNED, 1.0F, 1.0F);
            this.level.broadcastEntityEvent(this, (byte)39);
            var1.push(this);
         } else {
            this.strongKnockback(var1);
         }

         var1.hurtMarked = true;
      }

   }

   private void roar() {
      if (this.isAlive()) {
         List var1 = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0D), NO_RAVAGER_AND_ALIVE);

         Entity var3;
         for(Iterator var2 = var1.iterator(); var2.hasNext(); this.strongKnockback(var3)) {
            var3 = (Entity)var2.next();
            if (!(var3 instanceof AbstractIllager)) {
               var3.hurt(DamageSource.mobAttack(this), 6.0F);
            }
         }

         Vec3 var10 = this.getBoundingBox().getCenter();

         for(int var11 = 0; var11 < 40; ++var11) {
            double var4 = this.random.nextGaussian() * 0.2D;
            double var6 = this.random.nextGaussian() * 0.2D;
            double var8 = this.random.nextGaussian() * 0.2D;
            this.level.addParticle(ParticleTypes.POOF, var10.x, var10.y, var10.z, var4, var6, var8);
         }
      }

   }

   private void strongKnockback(Entity var1) {
      double var2 = var1.getX() - this.getX();
      double var4 = var1.getZ() - this.getZ();
      double var6 = Math.max(var2 * var2 + var4 * var4, 0.001D);
      var1.push(var2 / var6 * 4.0D, 0.2D, var4 / var6 * 4.0D);
   }

   public void handleEntityEvent(byte var1) {
      if (var1 == 4) {
         this.attackTick = 10;
         this.playSound(SoundEvents.RAVAGER_ATTACK, 1.0F, 1.0F);
      } else if (var1 == 39) {
         this.stunnedTick = 40;
      }

      super.handleEntityEvent(var1);
   }

   public int getAttackTick() {
      return this.attackTick;
   }

   public int getStunnedTick() {
      return this.stunnedTick;
   }

   public int getRoarTick() {
      return this.roarTick;
   }

   public boolean doHurtTarget(Entity var1) {
      this.attackTick = 10;
      this.level.broadcastEntityEvent(this, (byte)4);
      this.playSound(SoundEvents.RAVAGER_ATTACK, 1.0F, 1.0F);
      return super.doHurtTarget(var1);
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return SoundEvents.RAVAGER_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.RAVAGER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.RAVAGER_DEATH;
   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.RAVAGER_STEP, 0.15F, 1.0F);
   }

   public boolean checkSpawnObstruction(LevelReader var1) {
      return !var1.containsAnyLiquid(this.getBoundingBox());
   }

   public void applyRaidBuffs(int var1, boolean var2) {
   }

   public boolean canBeLeader() {
      return false;
   }

   static class RavagerNodeEvaluator extends WalkNodeEvaluator {
      private RavagerNodeEvaluator() {
      }

      protected BlockPathTypes evaluateBlockPathType(BlockGetter var1, boolean var2, boolean var3, BlockPos var4, BlockPathTypes var5) {
         return var5 == BlockPathTypes.LEAVES ? BlockPathTypes.OPEN : super.evaluateBlockPathType(var1, var2, var3, var4, var5);
      }

      // $FF: synthetic method
      RavagerNodeEvaluator(Object var1) {
         this();
      }
   }

   static class RavagerNavigation extends GroundPathNavigation {
      public RavagerNavigation(Mob var1, Level var2) {
         super(var1, var2);
      }

      protected PathFinder createPathFinder(int var1) {
         this.nodeEvaluator = new Ravager.RavagerNodeEvaluator();
         return new PathFinder(this.nodeEvaluator, var1);
      }
   }

   class RavagerMeleeAttackGoal extends MeleeAttackGoal {
      public RavagerMeleeAttackGoal() {
         super(Ravager.this, 1.0D, true);
      }

      protected double getAttackReachSqr(LivingEntity var1) {
         float var2 = Ravager.this.getBbWidth() - 0.1F;
         return (double)(var2 * 2.0F * var2 * 2.0F + var1.getBbWidth());
      }
   }
}
