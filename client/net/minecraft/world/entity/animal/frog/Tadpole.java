package net.minecraft.world.entity.animal.frog;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import javax.annotation.Nullable;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

public class Tadpole extends AbstractFish {
   @VisibleForTesting
   public static int ticksToBeFrog = Math.abs(-24000);
   public static final float HITBOX_WIDTH = 0.4F;
   public static final float HITBOX_HEIGHT = 0.3F;
   private int age;
   protected static final ImmutableList<SensorType<? extends Sensor<? super Tadpole>>> SENSOR_TYPES;
   protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES;

   public Tadpole(EntityType<? extends AbstractFish> var1, Level var2) {
      super(var1, var2);
      this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, true);
      this.lookControl = new SmoothSwimmingLookControl(this, 10);
   }

   protected PathNavigation createNavigation(Level var1) {
      return new WaterBoundPathNavigation(this, var1);
   }

   protected Brain.Provider<Tadpole> brainProvider() {
      return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
   }

   protected Brain<?> makeBrain(Dynamic<?> var1) {
      return TadpoleAi.makeBrain(this.brainProvider().makeBrain(var1));
   }

   public Brain<Tadpole> getBrain() {
      return super.getBrain();
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.TADPOLE_FLOP;
   }

   protected void customServerAiStep(ServerLevel var1) {
      ProfilerFiller var2 = Profiler.get();
      var2.push("tadpoleBrain");
      this.getBrain().tick(var1, this);
      var2.pop();
      var2.push("tadpoleActivityUpdate");
      TadpoleAi.updateActivity(this);
      var2.pop();
      super.customServerAiStep(var1);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Animal.createAnimalAttributes().add(Attributes.MOVEMENT_SPEED, 1.0).add(Attributes.MAX_HEALTH, 6.0);
   }

   public void aiStep() {
      super.aiStep();
      if (!this.level().isClientSide) {
         this.setAge(this.age + 1);
      }

   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("Age", this.age);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setAge(var1.getInt("Age"));
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return null;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.TADPOLE_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return SoundEvents.TADPOLE_DEATH;
   }

   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (this.isFood(var3)) {
         this.feed(var1, var3);
         return InteractionResult.SUCCESS;
      } else {
         return (InteractionResult)Bucketable.bucketMobPickup(var1, var2, this).orElse(super.mobInteract(var1, var2));
      }
   }

   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendEntityBrain(this);
   }

   public boolean fromBucket() {
      return true;
   }

   public void setFromBucket(boolean var1) {
   }

   public void saveToBucketTag(ItemStack var1) {
      Bucketable.saveDefaultDataToBucketTag(this, var1);
      CustomData.update(DataComponents.BUCKET_ENTITY_DATA, var1, (var1x) -> {
         var1x.putInt("Age", this.getAge());
      });
   }

   public void loadFromBucketTag(CompoundTag var1) {
      Bucketable.loadDefaultDataFromBucketTag(this, var1);
      if (var1.contains("Age")) {
         this.setAge(var1.getInt("Age"));
      }

   }

   public ItemStack getBucketItemStack() {
      return new ItemStack(Items.TADPOLE_BUCKET);
   }

   public SoundEvent getPickupSound() {
      return SoundEvents.BUCKET_FILL_TADPOLE;
   }

   private boolean isFood(ItemStack var1) {
      return var1.is(ItemTags.FROG_FOOD);
   }

   private void feed(Player var1, ItemStack var2) {
      this.usePlayerItem(var1, var2);
      this.ageUp(AgeableMob.getSpeedUpSecondsWhenFeeding(this.getTicksLeftUntilAdult()));
      this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), 0.0, 0.0, 0.0);
   }

   private void usePlayerItem(Player var1, ItemStack var2) {
      var2.consume(1, var1);
   }

   private int getAge() {
      return this.age;
   }

   private void ageUp(int var1) {
      this.setAge(this.age + var1 * 20);
   }

   private void setAge(int var1) {
      this.age = var1;
      if (this.age >= ticksToBeFrog) {
         this.ageUp();
      }

   }

   private void ageUp() {
      Level var2 = this.level();
      if (var2 instanceof ServerLevel var1) {
         this.convertTo(EntityType.FROG, ConversionParams.single(this, false, false), (var2x) -> {
            var2x.finalizeSpawn(var1, this.level().getCurrentDifficultyAt(var2x.blockPosition()), EntitySpawnReason.CONVERSION, (SpawnGroupData)null);
            var2x.setPersistenceRequired();
            var2x.fudgePositionAfterSizeChange(this.getDimensions(this.getPose()));
            this.playSound(SoundEvents.TADPOLE_GROW_UP, 0.15F, 1.0F);
         });
      }

   }

   private int getTicksLeftUntilAdult() {
      return Math.max(0, ticksToBeFrog - this.age);
   }

   public boolean shouldDropExperience() {
      return false;
   }

   static {
      SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY, SensorType.FROG_TEMPTATIONS);
      MEMORY_TYPES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.IS_TEMPTED, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.BREED_TARGET, MemoryModuleType.IS_PANICKING);
   }
}
