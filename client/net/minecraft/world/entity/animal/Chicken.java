package net.minecraft.world.entity.animal;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

public class Chicken extends Animal {
   private static final Ingredient FOOD_ITEMS;
   public float flap;
   public float flapSpeed;
   public float oFlapSpeed;
   public float oFlap;
   public float flapping = 1.0F;
   private float nextFlap = 1.0F;
   public int eggTime;
   public boolean isChickenJockey;

   public Chicken(EntityType<? extends Chicken> var1, Level var2) {
      super(var1, var2);
      this.eggTime = this.random.nextInt(6000) + 6000;
      this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new PanicGoal(this, 1.4));
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.0, FOOD_ITEMS, false));
      this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
   }

   protected float getStandingEyeHeight(Pose var1, EntityDimensions var2) {
      return this.isBaby() ? var2.height * 0.85F : var2.height * 0.92F;
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 4.0).add(Attributes.MOVEMENT_SPEED, 0.25);
   }

   public void aiStep() {
      super.aiStep();
      this.oFlap = this.flap;
      this.oFlapSpeed = this.flapSpeed;
      this.flapSpeed += (this.onGround ? -1.0F : 4.0F) * 0.3F;
      this.flapSpeed = Mth.clamp(this.flapSpeed, 0.0F, 1.0F);
      if (!this.onGround && this.flapping < 1.0F) {
         this.flapping = 1.0F;
      }

      this.flapping *= 0.9F;
      Vec3 var1 = this.getDeltaMovement();
      if (!this.onGround && var1.y < 0.0) {
         this.setDeltaMovement(var1.multiply(1.0, 0.6, 1.0));
      }

      this.flap += this.flapping * 2.0F;
      if (!this.level.isClientSide && this.isAlive() && !this.isBaby() && !this.isChickenJockey() && --this.eggTime <= 0) {
         this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
         this.spawnAtLocation(Items.EGG);
         this.gameEvent(GameEvent.ENTITY_PLACE);
         this.eggTime = this.random.nextInt(6000) + 6000;
      }

   }

   protected boolean isFlapping() {
      return this.flyDist > this.nextFlap;
   }

   protected void onFlap() {
      this.nextFlap = this.flyDist + this.flapSpeed / 2.0F;
   }

   public boolean causeFallDamage(float var1, float var2, DamageSource var3) {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.CHICKEN_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.CHICKEN_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.CHICKEN_DEATH;
   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.CHICKEN_STEP, 0.15F, 1.0F);
   }

   public Chicken getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return (Chicken)EntityType.CHICKEN.create(var1);
   }

   public boolean isFood(ItemStack var1) {
      return FOOD_ITEMS.test(var1);
   }

   public int getExperienceReward() {
      return this.isChickenJockey() ? 10 : super.getExperienceReward();
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.isChickenJockey = var1.getBoolean("IsChickenJockey");
      if (var1.contains("EggLayTime")) {
         this.eggTime = var1.getInt("EggLayTime");
      }

   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putBoolean("IsChickenJockey", this.isChickenJockey);
      var1.putInt("EggLayTime", this.eggTime);
   }

   public boolean removeWhenFarAway(double var1) {
      return this.isChickenJockey();
   }

   public void positionRider(Entity var1) {
      super.positionRider(var1);
      float var2 = Mth.sin(this.yBodyRot * 0.017453292F);
      float var3 = Mth.cos(this.yBodyRot * 0.017453292F);
      float var4 = 0.1F;
      float var5 = 0.0F;
      var1.setPos(this.getX() + (double)(0.1F * var2), this.getY(0.5) + var1.getMyRidingOffset() + 0.0, this.getZ() - (double)(0.1F * var3));
      if (var1 instanceof LivingEntity) {
         ((LivingEntity)var1).yBodyRot = this.yBodyRot;
      }

   }

   public boolean isChickenJockey() {
      return this.isChickenJockey;
   }

   public void setChickenJockey(boolean var1) {
      this.isChickenJockey = var1;
   }

   // $FF: synthetic method
   public AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return this.getBreedOffspring(var1, var2);
   }

   static {
      FOOD_ITEMS = Ingredient.of(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS);
   }
}
