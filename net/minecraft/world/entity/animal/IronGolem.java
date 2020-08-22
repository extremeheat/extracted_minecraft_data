package net.minecraft.world.entity.animal;

import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveBackToVillage;
import net.minecraft.world.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.OfferFlowerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.DefendVillageTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public class IronGolem extends AbstractGolem {
   protected static final EntityDataAccessor DATA_FLAGS_ID;
   private int attackAnimationTick;
   private int offerFlowerTick;

   public IronGolem(EntityType var1, Level var2) {
      super(var1, var2);
      this.maxUpStep = 1.0F;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
      this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9D, 32.0F));
      this.goalSelector.addGoal(2, new MoveBackToVillage(this, 0.6D));
      this.goalSelector.addGoal(3, new MoveThroughVillageGoal(this, 0.6D, false, 4, () -> {
         return false;
      }));
      this.goalSelector.addGoal(5, new OfferFlowerGoal(this));
      this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.6D));
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new DefendVillageTargetGoal(this));
      this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Mob.class, 5, false, false, (var0) -> {
         return var0 instanceof Enemy && !(var0 instanceof Creeper);
      }));
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_FLAGS_ID, (byte)0);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
      this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
      this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(15.0D);
   }

   protected int decreaseAirSupply(int var1) {
      return var1;
   }

   protected void doPush(Entity var1) {
      if (var1 instanceof Enemy && !(var1 instanceof Creeper) && this.getRandom().nextInt(20) == 0) {
         this.setTarget((LivingEntity)var1);
      }

      super.doPush(var1);
   }

   public void aiStep() {
      super.aiStep();
      if (this.attackAnimationTick > 0) {
         --this.attackAnimationTick;
      }

      if (this.offerFlowerTick > 0) {
         --this.offerFlowerTick;
      }

      if (getHorizontalDistanceSqr(this.getDeltaMovement()) > 2.500000277905201E-7D && this.random.nextInt(5) == 0) {
         int var1 = Mth.floor(this.getX());
         int var2 = Mth.floor(this.getY() - 0.20000000298023224D);
         int var3 = Mth.floor(this.getZ());
         BlockState var4 = this.level.getBlockState(new BlockPos(var1, var2, var3));
         if (!var4.isAir()) {
            this.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, var4), this.getX() + ((double)this.random.nextFloat() - 0.5D) * (double)this.getBbWidth(), this.getY() + 0.1D, this.getZ() + ((double)this.random.nextFloat() - 0.5D) * (double)this.getBbWidth(), 4.0D * ((double)this.random.nextFloat() - 0.5D), 0.5D, ((double)this.random.nextFloat() - 0.5D) * 4.0D);
         }
      }

   }

   public boolean canAttackType(EntityType var1) {
      if (this.isPlayerCreated() && var1 == EntityType.PLAYER) {
         return false;
      } else {
         return var1 == EntityType.CREEPER ? false : super.canAttackType(var1);
      }
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putBoolean("PlayerCreated", this.isPlayerCreated());
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setPlayerCreated(var1.getBoolean("PlayerCreated"));
   }

   private float getAttackDamage() {
      return (float)this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue();
   }

   public boolean doHurtTarget(Entity var1) {
      this.attackAnimationTick = 10;
      this.level.broadcastEntityEvent(this, (byte)4);
      float var2 = this.getAttackDamage();
      float var3 = var2 > 0.0F ? var2 / 2.0F + (float)this.random.nextInt((int)var2) : 0.0F;
      boolean var4 = var1.hurt(DamageSource.mobAttack(this), var3);
      if (var4) {
         var1.setDeltaMovement(var1.getDeltaMovement().add(0.0D, 0.4000000059604645D, 0.0D));
         this.doEnchantDamageEffects(this, var1);
      }

      this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
      return var4;
   }

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
         this.level.broadcastEntityEvent(this, (byte)11);
      } else {
         this.offerFlowerTick = 0;
         this.level.broadcastEntityEvent(this, (byte)34);
      }

   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.IRON_GOLEM_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.IRON_GOLEM_DEATH;
   }

   protected boolean mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      Item var4 = var3.getItem();
      if (var4 != Items.IRON_INGOT) {
         return false;
      } else {
         float var5 = this.getHealth();
         this.heal(25.0F);
         if (this.getHealth() == var5) {
            return false;
         } else {
            float var6 = 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;
            this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0F, var6);
            if (!var1.abilities.instabuild) {
               var3.shrink(1);
            }

            return true;
         }
      }
   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.IRON_GOLEM_STEP, 1.0F, 1.0F);
   }

   public int getOfferFlowerTick() {
      return this.offerFlowerTick;
   }

   public boolean isPlayerCreated() {
      return ((Byte)this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
   }

   public void setPlayerCreated(boolean var1) {
      byte var2 = (Byte)this.entityData.get(DATA_FLAGS_ID);
      if (var1) {
         this.entityData.set(DATA_FLAGS_ID, (byte)(var2 | 1));
      } else {
         this.entityData.set(DATA_FLAGS_ID, (byte)(var2 & -2));
      }

   }

   public void die(DamageSource var1) {
      super.die(var1);
   }

   public boolean checkSpawnObstruction(LevelReader var1) {
      BlockPos var2 = new BlockPos(this);
      BlockPos var3 = var2.below();
      BlockState var4 = var1.getBlockState(var3);
      if (!var4.entityCanStandOn(var1, var3, this)) {
         return false;
      } else {
         for(int var5 = 1; var5 < 3; ++var5) {
            BlockPos var6 = var2.above(var5);
            BlockState var7 = var1.getBlockState(var6);
            if (!NaturalSpawner.isValidEmptySpawnBlock(var1, var6, var7, var7.getFluidState())) {
               return false;
            }
         }

         return NaturalSpawner.isValidEmptySpawnBlock(var1, var2, var1.getBlockState(var2), Fluids.EMPTY.defaultFluidState()) && var1.isUnobstructed(this);
      }
   }

   static {
      DATA_FLAGS_ID = SynchedEntityData.defineId(IronGolem.class, EntityDataSerializers.BYTE);
   }

   public static enum Crackiness {
      NONE(1.0F),
      LOW(0.75F),
      MEDIUM(0.5F),
      HIGH(0.25F);

      private static final List BY_DAMAGE = (List)Stream.of(values()).sorted(Comparator.comparingDouble((var0) -> {
         return (double)var0.fraction;
      })).collect(ImmutableList.toImmutableList());
      private final float fraction;

      private Crackiness(float var3) {
         this.fraction = var3;
      }

      public static IronGolem.Crackiness byFraction(float var0) {
         Iterator var1 = BY_DAMAGE.iterator();

         IronGolem.Crackiness var2;
         do {
            if (!var1.hasNext()) {
               return NONE;
            }

            var2 = (IronGolem.Crackiness)var1.next();
         } while(var0 >= var2.fraction);

         return var2;
      }
   }
}
