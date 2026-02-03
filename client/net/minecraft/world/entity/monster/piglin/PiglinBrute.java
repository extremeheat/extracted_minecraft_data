package net.minecraft.world.entity.monster.piglin;

import java.util.List;
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
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class PiglinBrute extends AbstractPiglin {
   private static final int MAX_HEALTH = 50;
   private static final float MOVEMENT_SPEED_WHEN_FIGHTING = 0.35F;
   private static final int ATTACK_DAMAGE = 7;
   private static final double TARGETING_RANGE = 12.0;
   public static final Brain.Provider<PiglinBrute> BRAIN_PROVIDER;

   public PiglinBrute(final EntityType<? extends PiglinBrute> type, final Level level) {
      super(type, level);
      this.xpReward = 20;
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 50.0).add(Attributes.MOVEMENT_SPEED, 0.3499999940395355).add(Attributes.ATTACK_DAMAGE, 7.0).add(Attributes.FOLLOW_RANGE, 12.0);
   }

   public @Nullable SpawnGroupData finalizeSpawn(final ServerLevelAccessor level, final DifficultyInstance difficulty, final EntitySpawnReason spawnReason, final @Nullable SpawnGroupData groupData) {
      PiglinBruteAi.initMemories(this);
      this.populateDefaultEquipmentSlots(level.getRandom(), difficulty);
      return super.finalizeSpawn(level, difficulty, spawnReason, groupData);
   }

   protected void populateDefaultEquipmentSlots(final RandomSource random, final DifficultyInstance difficulty) {
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_AXE));
   }

   protected Brain<PiglinBrute> makeBrain(final Brain.Packed packedBrain) {
      return BRAIN_PROVIDER.makeBrain(this, packedBrain);
   }

   public Brain<PiglinBrute> getBrain() {
      return super.getBrain();
   }

   public boolean canHunt() {
      return false;
   }

   public boolean wantsToPickUp(final ServerLevel level, final ItemStack itemStack) {
      return itemStack.is(Items.GOLDEN_AXE) ? super.wantsToPickUp(level, itemStack) : false;
   }

   protected void customServerAiStep(final ServerLevel level) {
      ProfilerFiller profiler = Profiler.get();
      profiler.push("piglinBruteBrain");
      this.getBrain().tick(level, this);
      profiler.pop();
      PiglinBruteAi.updateActivity(this);
      PiglinBruteAi.maybePlayActivitySound(this);
      super.customServerAiStep(level);
   }

   public PiglinArmPose getArmPose() {
      return this.isAggressive() && this.isHoldingMeleeWeapon() ? PiglinArmPose.ATTACKING_WITH_MELEE_WEAPON : PiglinArmPose.DEFAULT;
   }

   public boolean hurtServer(final ServerLevel level, final DamageSource source, final float damage) {
      boolean wasHurt = super.hurtServer(level, source, damage);
      if (wasHurt) {
         Entity var6 = source.getEntity();
         if (var6 instanceof LivingEntity) {
            LivingEntity sourceEntity = (LivingEntity)var6;
            PiglinBruteAi.wasHurtBy(level, this, sourceEntity);
         }
      }

      return wasHurt;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.PIGLIN_BRUTE_AMBIENT;
   }

   protected SoundEvent getHurtSound(final DamageSource source) {
      return SoundEvents.PIGLIN_BRUTE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.PIGLIN_BRUTE_DEATH;
   }

   protected void playStepSound(final BlockPos pos, final BlockState blockState) {
      this.playSound(SoundEvents.PIGLIN_BRUTE_STEP, 0.15F, 1.0F);
   }

   protected void playAngrySound() {
      this.makeSound(SoundEvents.PIGLIN_BRUTE_ANGRY);
   }

   protected void playConvertedSound() {
      this.makeSound(SoundEvents.PIGLIN_BRUTE_CONVERTED_TO_ZOMBIFIED);
   }

   static {
      BRAIN_PROVIDER = Brain.<PiglinBrute>provider(List.of(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS), List.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.HURT_BY, SensorType.PIGLIN_BRUTE_SPECIFIC_SENSOR), PiglinBruteAi::getActivities);
   }
}
