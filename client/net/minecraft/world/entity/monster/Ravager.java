package net.minecraft.world.entity.monster;

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
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Ravager extends Raider {
   private static final Predicate<Entity> NO_RAVAGER_AND_ALIVE = var0 -> var0.isAlive() && !(var0 instanceof Ravager);
   private static final double BASE_MOVEMENT_SPEED = 0.3;
   private static final double ATTACK_MOVEMENT_SPEED = 0.35;
   private static final int STUNNED_COLOR = 8356754;
   private static final double STUNNED_COLOR_BLUE = 0.5725490196078431;
   private static final double STUNNED_COLOR_GREEN = 0.5137254901960784;
   private static final double STUNNED_COLOR_RED = 0.4980392156862745;
   private static final int ATTACK_DURATION = 10;
   public static final int STUN_DURATION = 40;
   private int attackTick;
   private int stunnedTick;
   private int roarTick;

   public Ravager(EntityType<? extends Ravager> var1, Level var2) {
      super(var1, var2);
      this.maxUpStep = 1.0F;
      this.xpReward = 20;
   }

   @Override
   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(4, new Ravager.RavagerMeleeAttackGoal());
      this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.4));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
      this.targetSelector.addGoal(2, new HurtByTargetGoal(this, Raider.class).setAlertOthers());
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
      this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true, var0 -> !var0.isBaby()));
      this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
   }

   @Override
   protected void updateControlFlags() {
      boolean var1 = !(this.getControllingPassenger() instanceof Mob) || this.getControllingPassenger().getType().is(EntityTypeTags.RAIDERS);
      boolean var2 = !(this.getVehicle() instanceof Boat);
      this.goalSelector.setControlFlag(Goal.Flag.MOVE, var1);
      this.goalSelector.setControlFlag(Goal.Flag.JUMP, var1 && var2);
      this.goalSelector.setControlFlag(Goal.Flag.LOOK, var1);
      this.goalSelector.setControlFlag(Goal.Flag.TARGET, var1);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 100.0)
         .add(Attributes.MOVEMENT_SPEED, 0.3)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.75)
         .add(Attributes.ATTACK_DAMAGE, 12.0)
         .add(Attributes.ATTACK_KNOCKBACK, 1.5)
         .add(Attributes.FOLLOW_RANGE, 32.0);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("AttackTick", this.attackTick);
      var1.putInt("StunTick", this.stunnedTick);
      var1.putInt("RoarTick", this.roarTick);
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.attackTick = var1.getInt("AttackTick");
      this.stunnedTick = var1.getInt("StunTick");
      this.roarTick = var1.getInt("RoarTick");
   }

   @Override
   public SoundEvent getCelebrateSound() {
      return SoundEvents.RAVAGER_CELEBRATE;
   }

   @Override
   protected PathNavigation createNavigation(Level var1) {
      return new Ravager.RavagerNavigation(this, var1);
   }

   @Override
   public int getMaxHeadYRot() {
      return 45;
   }

   @Override
   public double getPassengersRidingOffset() {
      return 2.1;
   }

   @Nullable
   @Override
   public Entity getControllingPassenger() {
      Entity var1 = this.getFirstPassenger();
      return var1 != null && this.canBeControlledBy(var1) ? var1 : null;
   }

   private boolean canBeControlledBy(Entity var1) {
      return !this.isNoAi() && var1 instanceof LivingEntity;
   }

   @Override
   public void aiStep() {
      super.aiStep();
      if (this.isAlive()) {
         if (this.isImmobile()) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
         } else {
            double var1 = this.getTarget() != null ? 0.35 : 0.3;
            double var3 = this.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue();
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(Mth.lerp(0.1, var3, var1));
         }

         if (this.horizontalCollision && this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            boolean var7 = false;
            AABB var2 = this.getBoundingBox().inflate(0.2);

            for(BlockPos var4 : BlockPos.betweenClosed(
               Mth.floor(var2.minX), Mth.floor(var2.minY), Mth.floor(var2.minZ), Mth.floor(var2.maxX), Mth.floor(var2.maxY), Mth.floor(var2.maxZ)
            )) {
               BlockState var5 = this.level.getBlockState(var4);
               Block var6 = var5.getBlock();
               if (var6 instanceof LeavesBlock) {
                  var7 = this.level.destroyBlock(var4, true, this) || var7;
               }
            }

            if (!var7 && this.onGround) {
               this.jumpFromGround();
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
         double var1 = this.getX() - (double)this.getBbWidth() * Math.sin((double)(this.yBodyRot * 0.017453292F)) + (this.random.nextDouble() * 0.6 - 0.3);
         double var3 = this.getY() + (double)this.getBbHeight() - 0.3;
         double var5 = this.getZ() + (double)this.getBbWidth() * Math.cos((double)(this.yBodyRot * 0.017453292F)) + (this.random.nextDouble() * 0.6 - 0.3);
         this.level.addParticle(ParticleTypes.ENTITY_EFFECT, var1, var3, var5, 0.4980392156862745, 0.5137254901960784, 0.5725490196078431);
      }
   }

   @Override
   protected boolean isImmobile() {
      return super.isImmobile() || this.attackTick > 0 || this.stunnedTick > 0 || this.roarTick > 0;
   }

   @Override
   public boolean hasLineOfSight(Entity var1) {
      return this.stunnedTick <= 0 && this.roarTick <= 0 ? super.hasLineOfSight(var1) : false;
   }

   @Override
   protected void blockedByShield(LivingEntity var1) {
      if (this.roarTick == 0) {
         if (this.random.nextDouble() < 0.5) {
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
         for(LivingEntity var3 : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0), NO_RAVAGER_AND_ALIVE)) {
            if (!(var3 instanceof AbstractIllager)) {
               var3.hurt(DamageSource.mobAttack(this), 6.0F);
            }

            this.strongKnockback(var3);
         }

         Vec3 var10 = this.getBoundingBox().getCenter();

         for(int var11 = 0; var11 < 40; ++var11) {
            double var4 = this.random.nextGaussian() * 0.2;
            double var6 = this.random.nextGaussian() * 0.2;
            double var8 = this.random.nextGaussian() * 0.2;
            this.level.addParticle(ParticleTypes.POOF, var10.x, var10.y, var10.z, var4, var6, var8);
         }

         this.gameEvent(GameEvent.ENTITY_ROAR);
      }
   }

   private void strongKnockback(Entity var1) {
      double var2 = var1.getX() - this.getX();
      double var4 = var1.getZ() - this.getZ();
      double var6 = Math.max(var2 * var2 + var4 * var4, 0.001);
      var1.push(var2 / var6 * 4.0, 0.2, var4 / var6 * 4.0);
   }

   @Override
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

   @Override
   public boolean doHurtTarget(Entity var1) {
      this.attackTick = 10;
      this.level.broadcastEntityEvent(this, (byte)4);
      this.playSound(SoundEvents.RAVAGER_ATTACK, 1.0F, 1.0F);
      return super.doHurtTarget(var1);
   }

   @Nullable
   @Override
   protected SoundEvent getAmbientSound() {
      return SoundEvents.RAVAGER_AMBIENT;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.RAVAGER_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.RAVAGER_DEATH;
   }

   @Override
   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.RAVAGER_STEP, 0.15F, 1.0F);
   }

   @Override
   public boolean checkSpawnObstruction(LevelReader var1) {
      return !var1.containsAnyLiquid(this.getBoundingBox());
   }

   @Override
   public void applyRaidBuffs(int var1, boolean var2) {
   }

   @Override
   public boolean canBeLeader() {
      return false;
   }

   class RavagerMeleeAttackGoal extends MeleeAttackGoal {
      public RavagerMeleeAttackGoal() {
         super(Ravager.this, 1.0, true);
      }

      @Override
      protected double getAttackReachSqr(LivingEntity var1) {
         float var2 = Ravager.this.getBbWidth() - 0.1F;
         return (double)(var2 * 2.0F * var2 * 2.0F + var1.getBbWidth());
      }
   }

   static class RavagerNavigation extends GroundPathNavigation {
      public RavagerNavigation(Mob var1, Level var2) {
         super(var1, var2);
      }

      @Override
      protected PathFinder createPathFinder(int var1) {
         this.nodeEvaluator = new Ravager.RavagerNodeEvaluator();
         return new PathFinder(this.nodeEvaluator, var1);
      }
   }

   static class RavagerNodeEvaluator extends WalkNodeEvaluator {
      RavagerNodeEvaluator() {
         super();
      }

      @Override
      protected BlockPathTypes evaluateBlockPathType(BlockGetter var1, boolean var2, boolean var3, BlockPos var4, BlockPathTypes var5) {
         return var5 == BlockPathTypes.LEAVES ? BlockPathTypes.OPEN : super.evaluateBlockPathType(var1, var2, var3, var4, var5);
      }
   }
}