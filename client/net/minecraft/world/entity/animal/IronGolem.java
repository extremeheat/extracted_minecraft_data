package net.minecraft.world.entity.animal;

import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.GolemRandomStrollInVillageGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveBackToVillageGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.OfferFlowerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.DefendVillageTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

public class IronGolem extends AbstractGolem implements NeutralMob {
   protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(IronGolem.class, EntityDataSerializers.BYTE);
   private static final int IRON_INGOT_HEAL_AMOUNT = 25;
   private int attackAnimationTick;
   private int offerFlowerTick;
   private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
   private int remainingPersistentAngerTime;
   @Nullable
   private UUID persistentAngerTarget;

   public IronGolem(EntityType<? extends IronGolem> var1, Level var2) {
      super(var1, var2);
      this.setMaxUpStep(1.0F);
   }

   @Override
   protected void registerGoals() {
      this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, true));
      this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9, 32.0F));
      this.goalSelector.addGoal(2, new MoveBackToVillageGoal(this, 0.6, false));
      this.goalSelector.addGoal(4, new GolemRandomStrollInVillageGoal(this, 0.6));
      this.goalSelector.addGoal(5, new OfferFlowerGoal(this));
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new DefendVillageTargetGoal(this));
      this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
      this.targetSelector
         .addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false, var0 -> var0 instanceof Enemy && !(var0 instanceof Creeper)));
      this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal<>(this, false));
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_FLAGS_ID, (byte)0);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes()
         .add(Attributes.MAX_HEALTH, 100.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25)
         .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
         .add(Attributes.ATTACK_DAMAGE, 15.0);
   }

   @Override
   protected int decreaseAirSupply(int var1) {
      return var1;
   }

   @Override
   protected void doPush(Entity var1) {
      if (var1 instanceof Enemy && !(var1 instanceof Creeper) && this.getRandom().nextInt(20) == 0) {
         this.setTarget((LivingEntity)var1);
      }

      super.doPush(var1);
   }

   @Override
   public void aiStep() {
      super.aiStep();
      if (this.attackAnimationTick > 0) {
         --this.attackAnimationTick;
      }

      if (this.offerFlowerTick > 0) {
         --this.offerFlowerTick;
      }

      if (!this.level().isClientSide) {
         this.updatePersistentAnger((ServerLevel)this.level(), true);
      }
   }

   @Override
   public boolean canSpawnSprintParticle() {
      return this.getDeltaMovement().horizontalDistanceSqr() > 2.500000277905201E-7 && this.random.nextInt(5) == 0;
   }

   @Override
   public boolean canAttackType(EntityType<?> var1) {
      if (this.isPlayerCreated() && var1 == EntityType.PLAYER) {
         return false;
      } else {
         return var1 == EntityType.CREEPER ? false : super.canAttackType(var1);
      }
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putBoolean("PlayerCreated", this.isPlayerCreated());
      this.addPersistentAngerSaveData(var1);
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setPlayerCreated(var1.getBoolean("PlayerCreated"));
      this.readPersistentAngerSaveData(this.level(), var1);
   }

   @Override
   public void startPersistentAngerTimer() {
      this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
   }

   @Override
   public void setRemainingPersistentAngerTime(int var1) {
      this.remainingPersistentAngerTime = var1;
   }

   @Override
   public int getRemainingPersistentAngerTime() {
      return this.remainingPersistentAngerTime;
   }

   @Override
   public void setPersistentAngerTarget(@Nullable UUID var1) {
      this.persistentAngerTarget = var1;
   }

   @Nullable
   @Override
   public UUID getPersistentAngerTarget() {
      return this.persistentAngerTarget;
   }

   private float getAttackDamage() {
      return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
   }

   @Override
   public boolean doHurtTarget(Entity var1) {
      this.attackAnimationTick = 10;
      this.level().broadcastEntityEvent(this, (byte)4);
      float var2 = this.getAttackDamage();
      float var3 = (int)var2 > 0 ? var2 / 2.0F + (float)this.random.nextInt((int)var2) : var2;
      boolean var4 = var1.hurt(this.damageSources().mobAttack(this), var3);
      if (var4) {
         double var5 = var1 instanceof LivingEntity var7 ? var7.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE) : 0.0;
         double var9 = Math.max(0.0, 1.0 - var5);
         var1.setDeltaMovement(var1.getDeltaMovement().add(0.0, 0.4000000059604645 * var9, 0.0));
         this.doEnchantDamageEffects(this, var1);
      }

      this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
      return var4;
   }

   @Override
   public boolean hurt(DamageSource var1, float var2) {
      IronGolem.Crackiness var3 = this.getCrackiness();
      boolean var4 = super.hurt(var1, var2);
      if (var4 && this.getCrackiness() != var3) {
         this.playSound(SoundEvents.IRON_GOLEM_DAMAGE, 1.0F, 1.0F);
      }

      return var4;
   }

   public IronGolem.Crackiness getCrackiness() {
      return IronGolem.Crackiness.byFraction(this.getHealth() / this.getMaxHealth());
   }

   @Override
   public void handleEntityEvent(byte var1) {
      if (var1 == 4) {
         this.attackAnimationTick = 10;
         this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
      } else if (var1 == 11) {
         this.offerFlowerTick = 400;
      } else if (var1 == 34) {
         this.offerFlowerTick = 0;
      } else {
         super.handleEntityEvent(var1);
      }
   }

   public int getAttackAnimationTick() {
      return this.attackAnimationTick;
   }

   public void offerFlower(boolean var1) {
      if (var1) {
         this.offerFlowerTick = 400;
         this.level().broadcastEntityEvent(this, (byte)11);
      } else {
         this.offerFlowerTick = 0;
         this.level().broadcastEntityEvent(this, (byte)34);
      }
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.IRON_GOLEM_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.IRON_GOLEM_DEATH;
   }

   @Override
   protected InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (!var3.is(Items.IRON_INGOT)) {
         return InteractionResult.PASS;
      } else {
         float var4 = this.getHealth();
         this.heal(25.0F);
         if (this.getHealth() == var4) {
            return InteractionResult.PASS;
         } else {
            float var5 = 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;
            this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0F, var5);
            if (!var1.getAbilities().instabuild) {
               var3.shrink(1);
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
         }
      }
   }

   @Override
   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.IRON_GOLEM_STEP, 1.0F, 1.0F);
   }

   public int getOfferFlowerTick() {
      return this.offerFlowerTick;
   }

   public boolean isPlayerCreated() {
      return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
   }

   public void setPlayerCreated(boolean var1) {
      byte var2 = this.entityData.get(DATA_FLAGS_ID);
      if (var1) {
         this.entityData.set(DATA_FLAGS_ID, (byte)(var2 | 1));
      } else {
         this.entityData.set(DATA_FLAGS_ID, (byte)(var2 & -2));
      }
   }

   @Override
   public void die(DamageSource var1) {
      super.die(var1);
   }

   @Override
   public boolean checkSpawnObstruction(LevelReader var1) {
      BlockPos var2 = this.blockPosition();
      BlockPos var3 = var2.below();
      BlockState var4 = var1.getBlockState(var3);
      if (!var4.entityCanStandOn(var1, var3, this)) {
         return false;
      } else {
         for(int var5 = 1; var5 < 3; ++var5) {
            BlockPos var6 = var2.above(var5);
            BlockState var7 = var1.getBlockState(var6);
            if (!NaturalSpawner.isValidEmptySpawnBlock(var1, var6, var7, var7.getFluidState(), EntityType.IRON_GOLEM)) {
               return false;
            }
         }

         return NaturalSpawner.isValidEmptySpawnBlock(var1, var2, var1.getBlockState(var2), Fluids.EMPTY.defaultFluidState(), EntityType.IRON_GOLEM)
            && var1.isUnobstructed(this);
      }
   }

   @Override
   public Vec3 getLeashOffset() {
      return new Vec3(0.0, (double)(0.875F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
   }

   public static enum Crackiness {
      NONE(1.0F),
      LOW(0.75F),
      MEDIUM(0.5F),
      HIGH(0.25F);

      private static final List<IronGolem.Crackiness> BY_DAMAGE = Stream.of(values())
         .sorted(Comparator.comparingDouble(var0 -> (double)var0.fraction))
         .collect(ImmutableList.toImmutableList());
      private final float fraction;

      private Crackiness(float var3) {
         this.fraction = var3;
      }

      public static IronGolem.Crackiness byFraction(float var0) {
         for(IronGolem.Crackiness var2 : BY_DAMAGE) {
            if (var0 < var2.fraction) {
               return var2;
            }
         }

         return NONE;
      }
   }
}
