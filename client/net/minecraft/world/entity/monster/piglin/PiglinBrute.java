package net.minecraft.world.entity.monster.piglin;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
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
   protected static final ImmutableList<SensorType<? extends Sensor<? super PiglinBrute>>> SENSOR_TYPES;
   protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES;

   public PiglinBrute(EntityType<? extends PiglinBrute> var1, Level var2) {
      super(var1, var2);
      this.xpReward = 20;
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 50.0D).add(Attributes.MOVEMENT_SPEED, 0.3499999940395355D).add(Attributes.ATTACK_DAMAGE, 7.0D);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5) {
      PiglinBruteAi.initMemories(this);
      this.populateDefaultEquipmentSlots(var2);
      return super.finalizeSpawn(var1, var2, var3, var4, var5);
   }

   protected void populateDefaultEquipmentSlots(DifficultyInstance var1) {
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_AXE));
   }

   protected Brain.Provider<PiglinBrute> brainProvider() {
      return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
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

   public boolean wantsToPickUp(ItemStack var1) {
      return var1.is(Items.GOLDEN_AXE) ? super.wantsToPickUp(var1) : false;
   }

   protected void customServerAiStep() {
      this.level.getProfiler().push("piglinBruteBrain");
      this.getBrain().tick((ServerLevel)this.level, this);
      this.level.getProfiler().pop();
      PiglinBruteAi.updateActivity(this);
      PiglinBruteAi.maybePlayActivitySound(this);
      super.customServerAiStep();
   }

   public PiglinArmPose getArmPose() {
      return this.isAggressive() && this.isHoldingMeleeWeapon() ? PiglinArmPose.ATTACKING_WITH_MELEE_WEAPON : PiglinArmPose.DEFAULT;
   }

   public boolean hurt(DamageSource var1, float var2) {
      boolean var3 = super.hurt(var1, var2);
      if (this.level.isClientSide) {
         return false;
      } else {
         if (var3 && var1.getEntity() instanceof LivingEntity) {
            PiglinBruteAi.wasHurtBy(this, (LivingEntity)var1.getEntity());
         }

         return var3;
      }
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
      this.playSound(SoundEvents.PIGLIN_BRUTE_ANGRY, 1.0F, this.getVoicePitch());
   }

   protected void playConvertedSound() {
      this.playSound(SoundEvents.PIGLIN_BRUTE_CONVERTED_TO_ZOMBIFIED, 1.0F, this.getVoicePitch());
   }

   static {
      SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.HURT_BY, SensorType.PIGLIN_BRUTE_SPECIFIC_SENSOR);
      MEMORY_TYPES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.DOORS_TO_CLOSE, MemoryModuleType.LIVING_ENTITIES, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, MemoryModuleType.NEARBY_ADULT_PIGLINS, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, new MemoryModuleType[]{MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.PATH, MemoryModuleType.ANGRY_AT, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.HOME});
   }
}
