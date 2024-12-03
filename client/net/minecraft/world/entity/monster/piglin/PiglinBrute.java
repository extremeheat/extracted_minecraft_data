package net.minecraft.world.entity.monster.piglin;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class PiglinBrute extends AbstractPiglin {
   private static final int MAX_HEALTH = 50;
   private static final float MOVEMENT_SPEED_WHEN_FIGHTING = 0.35F;
   private static final int ATTACK_DAMAGE = 7;
   private static final double TARGETING_RANGE = 12.0;
   protected static final ImmutableList<SensorType<? extends Sensor<? super PiglinBrute>>> SENSOR_TYPES;
   protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES;

   public PiglinBrute(EntityType<? extends PiglinBrute> var1, Level var2) {
      super(var1, var2);
      this.xpReward = 20;
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 50.0).add(Attributes.MOVEMENT_SPEED, 0.3499999940395355).add(Attributes.ATTACK_DAMAGE, 7.0).add(Attributes.FOLLOW_RANGE, 12.0);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, EntitySpawnReason var3, @Nullable SpawnGroupData var4) {
      PiglinBruteAi.initMemories(this);
      this.populateDefaultEquipmentSlots(var1.getRandom(), var2);
      return super.finalizeSpawn(var1, var2, var3, var4);
   }

   protected void populateDefaultEquipmentSlots(RandomSource var1, DifficultyInstance var2) {
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_AXE));
   }

   protected Brain.Provider<PiglinBrute> brainProvider() {
      return Brain.<PiglinBrute>provider(MEMORY_TYPES, SENSOR_TYPES);
   }

   protected Brain<?> makeBrain(Dynamic<?> var1) {
      return PiglinBruteAi.makeBrain(this, this.brainProvider().makeBrain(var1));
   }

   public Brain<PiglinBrute> getBrain() {
      return super.getBrain();
   }

   public boolean canHunt() {
      return false;
   }

   public boolean wantsToPickUp(ServerLevel var1, ItemStack var2) {
      return var2.is(Items.GOLDEN_AXE) ? super.wantsToPickUp(var1, var2) : false;
   }

   protected void customServerAiStep(ServerLevel var1) {
      ProfilerFiller var2 = Profiler.get();
      var2.push("piglinBruteBrain");
      this.getBrain().tick(var1, this);
      var2.pop();
      PiglinBruteAi.updateActivity(this);
      PiglinBruteAi.maybePlayActivitySound(this);
      super.customServerAiStep(var1);
   }

   public PiglinArmPose getArmPose() {
      return this.isAggressive() && this.isHoldingMeleeWeapon() ? PiglinArmPose.ATTACKING_WITH_MELEE_WEAPON : PiglinArmPose.DEFAULT;
   }

   public boolean hurtServer(ServerLevel var1, DamageSource var2, float var3) {
      boolean var4 = super.hurtServer(var1, var2, var3);
      if (var4) {
         Entity var6 = var2.getEntity();
         if (var6 instanceof LivingEntity) {
            LivingEntity var5 = (LivingEntity)var6;
            PiglinBruteAi.wasHurtBy(var1, this, var5);
         }
      }

      return var4;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.PIGLIN_BRUTE_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.PIGLIN_BRUTE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.PIGLIN_BRUTE_DEATH;
   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.PIGLIN_BRUTE_STEP, 0.15F, 1.0F);
   }

   protected void playAngrySound() {
      this.makeSound(SoundEvents.PIGLIN_BRUTE_ANGRY);
   }

   protected void playConvertedSound() {
      this.makeSound(SoundEvents.PIGLIN_BRUTE_CONVERTED_TO_ZOMBIFIED);
   }

   static {
      SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.HURT_BY, SensorType.PIGLIN_BRUTE_SPECIFIC_SENSOR);
      MEMORY_TYPES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.DOORS_TO_CLOSE, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, MemoryModuleType.NEARBY_ADULT_PIGLINS, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, new MemoryModuleType[]{MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.PATH, MemoryModuleType.ANGRY_AT, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.HOME});
   }
}
